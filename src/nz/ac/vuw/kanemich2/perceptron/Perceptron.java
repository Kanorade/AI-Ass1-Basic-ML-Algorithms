package nz.ac.vuw.kanemich2.perceptron;

import nz.ac.vuw.kanemich2.knn.KNN;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Perceptron {
    private record Instance(ArrayList<Float> inputs, String classifier){}
    private List<Instance> instances;
    public Perceptron(String[] args) {
        if (args.length != 1) {
            System.out.println("USAGE ass1-perceptron.jar <data-filename>");
        } else {
            instances = readFile(args[0]);

            System.out.println("Number of instances loaded: " + instances.size());
            System.out.println("Number of inputs per instance: " + instances.get(0).inputs.size());
        }
    }

    private List<Instance> readFile(String fileName) {
        List<Instance> fileData = new ArrayList<>();
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
                    ArrayList<Float> inputs = new ArrayList<>();
                    for (int i = 0; i < tokens.length - 1; i++) {
                        inputs.add(Float.parseFloat(tokens[i]));
                    }
                    String instanceClass = tokens[tokens.length - 1];
                    fileData.add(new Instance(inputs, instanceClass));
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

    public static void main(String[] args) {
        Perceptron p = new Perceptron(args);
    }
}
