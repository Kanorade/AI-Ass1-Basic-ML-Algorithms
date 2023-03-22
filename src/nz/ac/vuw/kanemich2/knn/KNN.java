package nz.ac.vuw.kanemich2.knn;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KNN {
    private record WineData (ArrayList<Float> values, int classifier){}
    private List<WineData> trainingData;
    private List<WineData> testData;

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
                trainingData = readFile(args[0]);
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
                testData = readFile(args[1]);
            } catch (FileNotFoundException e) {
                System.err.println("File \"" + args[1] + "\" not found.");
                System.err.println("Make sure file is in same directory as the jar file. " +
                        "Otherwise you need to include the entire filepath too.");
                System.exit(0);
            } catch (IOException e) {
                System.out.println("Something went wrong reading the test file.");
                throw new RuntimeException(e);
            }

            System.out.println("Number of instances:\n" +
                    "Training: " + trainingData.size() + "\n" +
                    "Test: " + testData.size());

        }
    }

    /**
     * Read the text file and organises it into a usable list.
     * @param fileName The name of the data file to be read.
     * @return List<WineData> Usable list of wine data with the classifications included.
     * @throws IOException Thrown if the file is missing or there's an error reading the file.
     */
    private List<WineData> readFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        List<WineData> fileData = new ArrayList<>();
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
                fileData.add(new WineData(wineValues, wineClass));
            }
        }
        return fileData;
    }
    public static void main(String[] args) {
        KNN knn = new KNN(args);
        //knn.makePredictions();
    }
}
