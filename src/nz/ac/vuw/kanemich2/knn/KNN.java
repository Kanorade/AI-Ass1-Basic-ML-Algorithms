package nz.ac.vuw.kanemich2.knn;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KNN {
    private record Wine(ArrayList<Float> values, int classifier){}
    private List<Wine> trainingWines;
    private List<Wine> testWines;
    private int kValue = 1;

    /**
     * Constructor takes the data from the training and test files specified in the arguments and
     * organises them into two lists
     * @param args Arguments given when the app was executed.
     */
    public KNN(String[] args) {
        if (args.length == 0 || args.length == 1) {
            System.out.println("USAGE ass1-knn.jar <training-filename> <test-filename> <optional k-value>");
        } else {
            /* Reading Training File */
            try {
                trainingWines = readFile(args[0]);
            } catch (FileNotFoundException e) {
                System.err.println("File \"" + args[0] + "\" not found.");
                System.err.println("Make sure file is in same directory as the jar file. " +
                        "Otherwise you need to include the entire filepath too.");
                System.exit(0);
            } catch (IOException e) {
                System.out.println("Something went wrong reading the training file.");
                throw new RuntimeException(e);
            }

            /* Reading Test File (just wanted separate meaningful error messages)*/
            try {
                testWines = readFile(args[1]);
            } catch (FileNotFoundException e) {
                System.err.println("File \"" + args[1] + "\" not found.");
                System.err.println("Make sure file is in same directory as the jar file. " +
                        "Otherwise you need to include the entire filepath too.");
                System.exit(0);
            } catch (IOException e) {
                System.out.println("Something went wrong reading the test file.");
                throw new RuntimeException(e);
            }

            System.out.println("Number of wine instances:\n" +
                    "Training wines: " + trainingWines.size() + "\n" +
                    "Test wines: " + testWines.size() + "\n");

        }
    }

    /**
     * Read the text file and organises it into a usable list.
     * @param fileName The name of the data file to be read.
     * @return List<WineData> Usable list of wine data with the classifications included.
     * @throws IOException Thrown if the file is missing or there's an error reading the file.
     */
    private List<Wine> readFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        List<Wine> fileData = new ArrayList<>();
        String line;
        boolean isFirstLine = true;
        while ((line = br.readLine()) != null) {
            if (isFirstLine) {
                isFirstLine = false;       // Ignore the first line as it's all the labels
            } else {
                String[] tokens = line.split(" ");
                ArrayList<Float> wineValues = new ArrayList<>();
                for (int i = 0; i < tokens.length-1; i++) {
                    wineValues.add(Float.parseFloat(tokens[i]));
                }
                int wineClass = Integer.parseInt(tokens[tokens.length-1]);
                fileData.add(new Wine(wineValues, wineClass));
            }
        }
        // close resources
        //br.close();
        return fileData;
    }

    private int classify(Wine testWine) {
        return 0;
    }

    /**
     * Make predictions on classifying the test wine and reporting on it.
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
            int classPrediction = classify(testWine);
            System.out.print(classPrediction + ", ");

            System.out.print("Actual = " + testWine.classifier + "\t");
            if (classPrediction == testWine.classifier) {
                System.out.println("Success!");
                successCount++;
            } else {
                System.out.println("Fail...");
            }
            wineNumber++;
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
