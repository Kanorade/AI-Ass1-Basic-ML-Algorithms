package nz.ac.vuw.kanemich2.knn;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class KNN {
    private record Wine(ArrayList<Float> values, int classifier){}
    private final List<Wine> trainingWines;
    private final List<Wine> testWines;
    private int kValue = 1;
    private final List<Float> trainingRanges;

    /**
     * Constructor takes the data from the training and test files specified in the arguments and
     * organises them into two lists
     * @param args Arguments given when the app was executed.
     */
    public KNN(String[] args) {
        if (args.length == 0 || args.length == 1 || args.length > 3) {
            System.out.println("USAGE ass1-knn.jar <training-filename> <test-filename> <optional k-value>");
            System.exit(0);
        } else if (args.length == 3) { // checking if the optional k value is the third argument
            int k;
            try {
                k = Integer.parseInt(args[2]);
                if (k <= 0) {
                    System.err.println("The k value needs to be a positive non-zero integer.");
                    System.exit(0);
                } else {
                    kValue = k;
                }
            } catch (NumberFormatException e) {
                System.err.println("The argument for the k value is invalid.");
                System.err.println("Please ensure k value is a positive integer, ideally an odd number.");
                System.exit(0);
            }
        }
        trainingWines = readFile(args[0]);
        testWines = readFile(args[1]);

        System.out.println("Number of wine instances:\n" +
                "Training wines: " + trainingWines.size() + "\n" +
                "Test wines: " + testWines.size() + "\n");

        System.out.println("Finding value ranges for training wine:");
        trainingRanges = setUpRanges(trainingWines);
        System.out.println(trainingRanges + "\n");
    }

    /**
     * Method to read the text file and organises it into a usable list.
     * Typically, being used in the constructor.
     * @param fileName The name of the data file to be read.
     * @return List<WineData> Usable list of wine data with the classifications included.
     */
    private List<Wine> readFile(String fileName) {
        List<Wine> fileData = new ArrayList<>();
        try {
            FileReader fr = new FileReader(fileName);
            BufferedReader br = new BufferedReader(fr);

            String line;
            boolean isFirstLine = true;
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;       // Ignore the first line as it's all the labels
                } else {
                    String[] tokens = line.split(" ");
                    ArrayList<Float> wineValues = new ArrayList<>();
                    for (int i = 0; i < tokens.length - 1; i++) {
                        wineValues.add(Float.parseFloat(tokens[i]));
                    }
                    int wineClass = Integer.parseInt(tokens[tokens.length - 1]);
                    fileData.add(new Wine(wineValues, wineClass));
                }
            }
            // close resources
            fr.close();
            br.close();

        } catch (FileNotFoundException e) {
            System.err.println("File \"" + fileName + "\" not found.");
            System.err.println("Make sure file is in same directory as the jar file. " +
                    "Otherwise you need to include the entire filepath too.");
            System.exit(0);
        } catch (IOException e) {
            System.out.println("Something went wrong reading the file: " + fileName);
            throw new RuntimeException(e);
        }

        return fileData;
    }

    /**
     * Convenience method to find the range of values for each wine feature
     * for the provided list of Wines.
     * Typically, being used in the constructor.
     * @param wineList The training set used for this algorithm.
     * @return The list of ranges for each of the wine features.
     */
    private ArrayList<Float> setUpRanges(List<Wine> wineList) {
        assert (!wineList.isEmpty());
        // get ranges for each wine feature in the training set
        ArrayList<Float> ranges = new ArrayList<>();
        int wineValueSize = wineList.get(0).values.size();
        for (int i = 0; i < wineValueSize; i++) {
            float min = Float.MAX_VALUE;
            float max = Float.MIN_VALUE;
            for(Wine wine : trainingWines) {
                if (wine.values.get(i) < min)
                    min = wine.values.get(i);
                if (wine.values.get(i) > max)
                    max = wine.values.get(i);
            }
            ranges.add(max-min);
        }
        return ranges;
    }

    /**
     * The method that does all the math. I tried to use variable names that make sense in context, but I apologise
     * if it's still confusing. Essentially it's calculating the "distance" (d), or similarity, between two wines
     * and it's values using the Euclidean distance formula: d^2 = Sum[(a-b)^2/R^2]
     * <br>
     * The expression in the square brackets is applied to each wine feature before adding them together.
     * a and b represent the value of a specific feature for each wine, and R is the range of that feature provided
     * on the training sets. The range is needed to normalise all the wine features and to ensure all the values are
     * relative to each other.
     * @param firstWine The first wine to find the distance from
     * @param secondWine The second wine to find the distance to
     * @return the distance between 2 wines using a normalized metric.
     */
    private double findNormalisedEuclideanDistance(Wine firstWine, Wine secondWine) {
        double distanceSquared = 0;
        for (int i = 0; i < firstWine.values.size(); i++) {
            double difference = firstWine.values.get(i) - secondWine.values.get(i);
            double calculation = (difference * difference) /
                    (trainingRanges.get(i) * trainingRanges.get(i));
            distanceSquared += calculation;
        }
        return Math.sqrt(distanceSquared);
    }
    public static <T> T mostCommon(List<T> list) {
        Map<T, Integer> map = new HashMap<>();

        for (T t : list) {
            Integer val = map.get(t);
            map.put(t, val == null ? 1 : val + 1);
        }

        Map.Entry<T, Integer> max = null;

        for (Map.Entry<T, Integer> e : map.entrySet()) {
            if (max == null || e.getValue() > max.getValue())
                max = e;
        }

        return max.getKey();
    }
    /**
     * Method to try and classify the test wine using the k-Nearest Neighbour.
     * It's not very efficient as it goes through all the training wines every time to find the closest wine(s).
     * The number of the closest wines is determined by the k value (eg. 1, 3, 5, etc...), and these wines are
     * used to determine the classification for the provided wine.
     * @param testWine the provided wine to classify
     * @param k the k value that effects the performance for the k-Nearest Neighbour algorithm
     * @return the classification of the provided wine as determined by the training set.
     */
    private int classify(Wine testWine, int k) {
        record Neighbour(int wineClass, double distance) implements Comparable<Neighbour> {
            @Override
            public int compareTo(Neighbour o) {
                double result = distance - o.distance;
                if (result < 0) {
                    return -1;
                } else if (result == 0) {
                    return 0;
                } else {
                    return 1;
                }
            }
        }
        PriorityQueue<Neighbour> neighbours = new PriorityQueue<>();
        double minDistance = Float.MAX_VALUE;
        int closestWineClass = 0;
        for (Wine wine : trainingWines) {
            double distance = findNormalisedEuclideanDistance(testWine, wine);
            neighbours.add(new Neighbour(wine.classifier, distance));
//            if (minDistance > distance) {
//                minDistance = distance;
//                closestWineClass = wine.classifier;
//            }
        }
        List<Integer> nearestNeighbours = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            Neighbour n = neighbours.poll();
            nearestNeighbours.add(n.wineClass);
            //System.out.println(n.distance);
            //System.out.println(minDistance);
        }
       // System.out.println(nearestNeighbours);
        int res = mostCommon(nearestNeighbours);
        //System.out.println(res);
        return res;
    }

    /**
     * Make predictions on classifying the test wines using k-Nearest Neighbour and reporting the results.
     */
    private void makePredictions() {
        //ArrayList<Integer> predictions;
        System.out.println(
                "Making predictions of test wines using k-Nearest Neighbour where k = " + kValue + ":");
        int wineNumber = 1;
        int successCount = 0;
        for (Wine testWine : testWines) {
            System.out.print("Wine " + wineNumber + ": ");

            System.out.print("Class Prediction = ");
            int classPrediction = classify(testWine, kValue);
            System.out.print(classPrediction + ", ");

            System.out.print("Actual = " + testWine.classifier + "\t");
            if (classPrediction == testWine.classifier) {
                System.out.println("Success!");
                successCount++;
            } else {
                System.out.println("Fail...");
            }
            wineNumber++;
            //break;
        }
        System.out.print("\nAccuracy: ");
        float accuracy = (float) successCount/testWines.size();
        System.out.println(accuracy*100f + "%");
    }
    public static void main(String[] args) {
        KNN knn = new KNN(args);
        knn.makePredictions();
    }
}

