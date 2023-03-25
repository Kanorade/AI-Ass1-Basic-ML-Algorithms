package nz.ac.vuw.kanemich2.decTree;


import java.io.File;
import java.io.IOException;
import java.util.*;

public class DecTree {
    int numCategories;
    int numAttributes;
    Set<String> categoryNames;
    List<String> attNames;
    List<Instance> allInstances;
    public DecTree(String[] args) {

    }

    /**
     * Helper method copied over from the provided helper-code.java file  for
     * the assignment.
     * @param fileName
     */
    private void readDataFile(String fileName) {
        /* format of names file:
         * names of attributes (the first one should be the class/category)
         * category followed by true's and false's for each instance
         */
        System.out.println("Reading data from file " + fileName);
        try {
            Scanner din = new Scanner(new File(fileName));

            attNames = new ArrayList<>();
            Scanner s = new Scanner(din.nextLine());
            // Skip the "Class" attribute.
            s.next();
            while (s.hasNext()) {
                attNames.add(s.next());
            }
            numAttributes = attNames.size();
            System.out.println(numAttributes + " attributes");

            allInstances = readInstances(din);
            din.close();

            categoryNames = new HashSet<>();
            for (Instance i : allInstances) {
                categoryNames.add(i.category);
            }
            numCategories = categoryNames.size();
            System.out.println(numCategories + " categories");

            for (Instance i : allInstances) {
                System.out.println(i);
            }
        } catch (IOException e) {
            throw new RuntimeException("Data File caused IO exception");
        }
    }
    private List<Instance> readInstances(Scanner din) {
        /* instance = classname and space separated attribute values */
        List<Instance> instances = new ArrayList<>();
        while (din.hasNext()) {
            Scanner line = new Scanner(din.nextLine());
            instances.add(new Instance(line.next(), line));
        }
        System.out.println("Read " + instances.size() + " instances");
        return instances;
    }
    private static class Instance {

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
            ans.append(" ");
            for (Boolean val : values) {
                ans.append(val ? "true " : "false ");
            }
            return ans.toString();
        }

    }
    public static void main(String[] args) {
        DecTree dt = new DecTree(args);
    }
}
