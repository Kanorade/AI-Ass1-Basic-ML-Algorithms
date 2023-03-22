package nz.ac.vuw.kanemich2.knn;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KNN {
    private record WineData (ArrayList<Float> values, int classifier){}
    public KNN(String[] args) {
        if (args.length == 0) {
            System.out.println("USAGE ass1-knn.jar <training-filename> <test-filename> <optional k-value>");
        } else {
            List<WineData> trainingData = null;
            try {
                trainingData = readFile(args[0]);
            } catch (FileNotFoundException e) {
                System.err.println("Couldn't find training file. Make sure file is in same directory as the jar file. " +
                        "Otherwise you need to include the entire filepath too.");
                System.err.println("File \"" + args[0] + "\" not found.");
                System.exit(0);
            } catch (IOException e) {
                System.out.println("Something went wrong reading the file.");
                throw new RuntimeException(e);
            }

            for (WineData data : trainingData) {
                System.out.println(data.toString());
            }

        }
    }

    private List<WineData> readFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        List<WineData> fileData = new ArrayList<>();
        String line;
        boolean isFirstLine = true;
        while ((line = br.readLine()) != null) {
            if (isFirstLine) {
                isFirstLine =  false;       // Ignore the first line as it's all the labels
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
    }
}
