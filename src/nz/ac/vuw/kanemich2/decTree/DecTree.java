package nz.ac.vuw.kanemich2.decTree;


import nz.ac.vuw.kanemich2.knn.KNN;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class DecTree {
    private record DataSet(int numCategories, int numAttributes, Map<String, Integer> categoryNames,
                          List<String> attNames, List<Instance> allInstances){}
    private DataSet trainingSet;
    private DataSet testSet;
    private Node decisionTree;

    public DecTree(String[] args) {
        if (args.length == 0 || args.length == 1) {
            System.out.println("USAGE ass1-decTree.jar <training-filename> <test-filename>");
        } else {
            System.out.println("Loading training data...\n");
            trainingSet = readDataFile(args[0]);
            System.out.println("\nBuilding Tree...\n");
            decisionTree = buildTree(trainingSet.allInstances, trainingSet.attNames);
            decisionTree.report("\t");

            System.out.println("\nLoading test data...\n");
            testSet = readDataFile(args[1]);
        }
    }

    /**
     * Helper method copied over from the provided helper-code.java file  for
     * the assignment.
     * Format of names file:
     *  - names of attributes (the first one should be the class/category)
     *  - category followed by true's and false's for each instance
     *
     * @param fileName The name of the file to be read
     */
    private DataSet readDataFile(String fileName) {
        DataSet fileData = null;
        try {
            Scanner din = new Scanner(new File(fileName));
            List<String> attNames = new ArrayList<>();
            Scanner s = new Scanner(din.nextLine());

            s.next();       // Skip the "Class" attribute.
            while (s.hasNext()) {attNames.add(s.next());}

            int numAttributes = attNames.size();
            System.out.println("Number of attributes: " + numAttributes);

            List<Instance> allInstances = readInstances(din);
            System.out.println("Number of instances: " + allInstances.size());
            din.close();

            Map<String, Integer> categoryNames = tally(allInstances);
//            for (Instance i : allInstances) {
//                if (categoryNames.containsKey(i.getCategory())) {
//                    categoryNames.put(i.getCategory(), categoryNames.get(i.getCategory()) + 1);
//                } else {
//                    categoryNames.put(i.getCategory(), 1);
//                }
//
//                //categoryNames.add(i.getCategory());
//            }
            int numCategories = categoryNames.size();
            System.out.println("Number of categories: " + numCategories);
            for (Map.Entry<String, Integer> cat : categoryNames.entrySet()) {
                System.out.println(cat.getKey() + ": " + cat.getValue());
            }

            /* Print out table */
            System.out.print("\nAttributes: ");
            for (String name : attNames) {System.out.print(name + " ");}
            System.out.println();

            for (int i = 0; i < numAttributes+2; i++) {
                System.out.print("--------");
            }
            System.out.println();
            for (Instance i : allInstances) {
                System.out.println(i);
            }
            /* Store data into a reusable class */
            fileData =  new DataSet(numCategories, numAttributes, categoryNames, attNames, allInstances);
        } catch (FileNotFoundException e) {
            System.err.println("File \"" + fileName + "\" not found.");
            System.err.println("Make sure file is in same directory as the jar file. " +
                    "Otherwise you need to include the entire filepath too.");
            System.exit(0);
        }
        return fileData;
    }
    private List<Instance> readInstances(Scanner din) {
        /* instance = classname and space separated attribute values */
        List<Instance> instances = new ArrayList<>();
        while (din.hasNext()) {
            Scanner line = new Scanner(din.nextLine());
            instances.add(new Instance(line.next(), line));
        }
        return instances;
    }
    private Node buildTree(List<Instance> instances, List<String> attributes) {
        if (instances.isEmpty()) {
            // TODO: replace this with buildLeaf method
            String bestCat = "";
            int maxFreq = 0;
            for (Map.Entry<String, Integer> cat : trainingSet.categoryNames.entrySet()) {
                if (cat.getValue() > maxFreq) {
                    bestCat = cat.getKey();
                    maxFreq = cat.getValue();
                }
            }
            float prob = (float)maxFreq / trainingSet.allInstances.size();
            return new Node(bestCat, prob); // Leaf node
        } else if (impurity(instances) == 0) {
            return new Node(instances.get(0).getCategory(), 1f); // Leaf Node
        } else if (attributes.isEmpty()) {
            return buildLeaf(instances);
        } else {
            // Looking for the attribute with the best weighted impurity
            float bestImpurity = Float.MAX_VALUE;
            String bestAtt = "";
            List<Instance> bestTrueInsts = new ArrayList<>();
            List<Instance> bestFalseInsts = new ArrayList<>();
            for (String attribute : attributes) {
                // Split into two sets of instances
                List<Instance> trueInstances = new ArrayList<>();
                List<Instance> falseInstances = new ArrayList<>();
                int attIndex = trainingSet.attNames.indexOf(attribute);   // Index of the attribute in the training set
                for (Instance instance : instances) {
                    if(instance.getAtt(attIndex)) {     // Checking if the attribute is true in this instance
                        trueInstances.add(instance);
                    } else {
                        falseInstances.add(instance);
                    }
                }
                // make weights
                float trueWeight = (float) trueInstances.size() / instances.size();
                float falseWeight = (float) falseInstances.size() / instances.size();
                float averageWeightedImpurity = trueWeight*impurity(trueInstances) +
                        falseWeight*impurity(falseInstances);
                // check if best (im)purity
                if (averageWeightedImpurity < bestImpurity) {
                    bestImpurity = averageWeightedImpurity;
                    bestAtt = attribute;
                    bestTrueInsts = trueInstances;
                    bestFalseInsts = falseInstances;
                }
            }
            List<String> newAtt = new ArrayList<>(attributes);
            newAtt.remove(bestAtt);
            Node ifTrueNode = buildTree(bestTrueInsts, newAtt);
            Node ifFalseNode = buildTree(bestFalseInsts, newAtt);
            return  new Node(bestAtt, ifTrueNode, ifFalseNode);
        }
    }
    private Node buildLeaf(List<Instance> instances) {
        Map<String, Integer> tally = tally(instances);
        String bestCat = "";
        int maxFreq = 0;
        for (Map.Entry<String, Integer> cat : tally.entrySet()) {
            if (cat.getValue() > maxFreq) {
                bestCat = cat.getKey();
                maxFreq = cat.getValue();
            }
        }
        float probability = (float)maxFreq / trainingSet.allInstances.size();
        return new Node(bestCat, probability);
    }
    /**
     * Convienience method for counting the number of each categoies for each method
     * @param instances
     * @return
     */
    private Map<String, Integer> tally(List<Instance> instances) {
        HashMap<String, Integer> tally = new HashMap<>();
        for (Instance i : instances) {
            if (tally.containsKey(i.getCategory())) {
                tally.put(i.getCategory(), tally.get(i.getCategory()) +1);
            } else {
                tally.put(i.getCategory(), 1);
            }
        }
        return tally;
    }
    private float impurity(List<Instance> instances) {
        if (instances.isEmpty()) return 0; // <- shouldn't happen
        Map<String, Integer> tally = tally(instances);

        if (tally.size() ==1) {   // assuming there's only 2 categories his assignment
            return 0;
        }
        float impurity = 1;
        for (int catCount : tally.values()) {
            impurity = impurity * ((float) catCount / instances.size());
        }
        return impurity;
    }
    private String classify(Instance testInstance, Node decider) {
        if (decider.isLeaf()) {
            return decider.getCategory();
        } else {
            int attIndex = testSet.attNames.indexOf(decider.getAttribute());
            if (testInstance.getAtt(attIndex)) {    // If given attribute of this instance is true
                return classify(testInstance, decider.getIfTrueNode());
            } else {
                return classify(testInstance, decider.getIfFalseNode());
            }
        }
    }
    private void makePredictions() {
        System.out.println(
                "\nMaking predictions of each test instance using a decision tree:");
        int instanceNumber = 1;
        int successCount = 0;
        for (Instance test : testSet.allInstances) {
            System.out.print("Instance " + instanceNumber + ": ");

            System.out.print("Prediction = ");
            String classPrediction = classify(test, decisionTree);
            System.out.print(classPrediction + ", ");

            System.out.print("Actual = " + test.getCategory() + "\t");
            if (classPrediction.equals(test.getCategory())) {
                System.out.println("Success!");
                successCount++;
            } else {
                System.out.println("Fail...");
            }
            instanceNumber++;
        }
        System.out.print("\nAccuracy: ");
        float accuracy = (float) successCount/testSet.allInstances.size();
        System.out.println(accuracy*100f + "%");
    }

    public static void main(String[] args) {
        DecTree dt = new DecTree(args);
        dt.makePredictions();
    }
}
 class Instance {

    private final String category;
    private final List<Boolean> values;

    public Instance(String cat, Scanner s) {
        category = cat;
        values = new ArrayList<>();
        while (s.hasNextBoolean()) {
            values.add(s.nextBoolean());
        }
    }

    public boolean getAtt(int index) {
        return values.get(index);
    }

    public String getCategory() {
        return category;
    }

    public String toString() {
        StringBuilder ans = new StringBuilder(category);
        ans.append("\t");
        for (Boolean val : values) {
            ans.append(val ? "true\t" : "false\t");
        }
        return ans.toString();
    }

}

class Node {
    private final boolean isLeaf;

    private String attribute;
    private final Node ifTrueNode;
    private final Node ifFalseNode;

    /* Leaf node Attributes, is null otherwise */
    private String category;
    private float probability;

    /**
     * Constructor for a non-leaf node
     * @param attribute
     * @param ifTrueNode
     * @param ifFalseNode
     */
    public Node(String attribute, Node ifTrueNode, Node ifFalseNode) {
        this.attribute = attribute;
        this.ifTrueNode = ifTrueNode;
        this.ifFalseNode = ifFalseNode;

        isLeaf = false;
    }

    /**
     * Constructor for leaf node
     * @param category
     * @param probability
     */
    public Node(String category, float probability) {
        this.category = category;
        this.probability = probability;
        isLeaf = true;

        ifTrueNode = null;
        ifFalseNode = null;
    }

    public String getAttribute() {
        return attribute;
    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public Node getIfTrueNode() {
        return ifTrueNode;
    }

    public Node getIfFalseNode() {
        return ifFalseNode;
    }
    public String getCategory() {
        return category;
    }
    public void report(String indent) {
        if (isLeaf) {
            if (probability==0){ //Error-checking
                System.out.printf("%sUnknown%n", indent);
            }else{
                System.out.printf("%sClass %s, prob=%.2f%n", indent, category, probability);
            }
        } else {
            System.out.printf("%s%s = True:%n", indent, attribute);
            assert ifTrueNode != null;
            ifTrueNode.report(indent+"\t");
            System.out.printf("%s%s = False:%n", indent, attribute);
            assert ifFalseNode != null;
            ifFalseNode.report(indent+"\t");
        }
    }
}
