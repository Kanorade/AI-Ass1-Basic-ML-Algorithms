package nz.ac.vuw.kanemich2.perception;

public class Perception {
    public static void main(String[] args) {
        System.out.print("Hello, ");
        if (args.length == 0) {
            System.out.println("World!");
        } else {
            for (int i = 0; i < args.length; i++) {
                System.out.print(args[i] + " ");
            }
        }
        System.out.println();
    }
}
