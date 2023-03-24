To run ass1-knn.jar just have the jar file in the same directory as the
wine training and test files. Then under the directory type the command line:

    java -jar ass1-knn.jar <training file name> <test file name>

For example,

    java -jar ass1-knn.jar wine-training wine-test

The program assumes the first line has the feature labels, and the remaining lines
are space separated values where the last value on each line is an integer
representing the wine's class. If a file doesn't meet these assumptions, th program
may not function as intended or throw and error.

The program will perform k-Nearest Neighbour with the training and test files and
report the results, including its accuracy. It will perform with k=1 as default.