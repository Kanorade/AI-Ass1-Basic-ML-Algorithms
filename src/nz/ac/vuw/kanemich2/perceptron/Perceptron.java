package nz.ac.vuw.kanemich2.perceptron;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Perceptron {
    private record Instance(ArrayList<Double> inputs, String classifier, int d){}
    private List<Instance> instances;
    private final static int MAX_ITERATIONS = 100;
    private Map<String, Integer> classifiers;
    private final static double LEARNING_RATE = 0.1;


    public Perceptron(String[] args) {
        if (args.length != 1) {
            System.out.println("USAGE ass1-perceptron.jar <data-filename>");
            System.exit(0);
        } else {
            // Hard coding the classes 'g' and 'b' into 1 and 0.
            // This will probably not work for more general files.
            // Should be okay for the scope of the assignment.
            classifiers = new HashMap<>();
            classifiers.put("g", 1);
            classifiers.put("b", 0);
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
                    ArrayList<Double> inputs = new ArrayList<>();
                    for (int i = 0; i < tokens.length - 1; i++) {
                        inputs.add(Double.parseDouble(tokens[i]));
                    }
                    String instanceClass = tokens[tokens.length - 1];
                    fileData.add(new Instance(inputs, instanceClass, classifiers.get(instanceClass)));
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

    private void buildPerceptron() {
        // Initialise weights
        List<Double> weights = new ArrayList<>();
        for (int i = 0; i < instances.get(0).inputs.size() + 1; i++) {  //one extra weight for the bias
            weights.add(0.0);
        }

        for (int z = 0; z < MAX_ITERATIONS; z++) {
            int successCount = 0;
            for (Instance inst: instances) {
                int wi = 0;  // Setting weight index
                double sum = weights.get(wi);    // Starting sum with weight w0

                wi++;    // i = 1, referring to weight w1
                for (double input : inst.inputs) {
                    sum += weights.get(wi)*input;
                    wi++;
                }
                int y;              // Predicted value
                int d = inst.d;     // Expected value
                // Should probably give more meaningful variable names,
                // but it was easier to visualise the math this way

                if(sum > 0) y = 1;
                else y = 0;

                if (y == d) {
                    successCount++;    // No learning taking place
                } else {
                    // adjust weights
                    // resetting weight index
                    wi = 0;

                    double adjust = LEARNING_RATE * (d - y);
                    double replace = weights.get(wi) + adjust;    // because x0 = 1
                    weights.set(wi,replace);
                    wi++;
                    for (double input : inst.inputs) {
                        replace = weights.get(wi) + (adjust*input);
                        weights.set(wi, replace);
                        wi++;
                    }
                }
            }
            System.out.println("\nIteration: " + (z+1));
            System.out.println("Success rate: " + successCount + " out of " + instances.size() + " instances");
            System.out.println("Accuracy: " + (double)successCount/instances.size()*100 + "%");
            if (successCount == instances.size()) {
                break;
            }

        }
        System.out.println("\nFinal weights:\n" + weights);
    }
    public static void main(String[] args) {
        Perceptron p = new Perceptron(args);
        p.buildPerceptron();

    }
}
