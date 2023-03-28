To run ass1-decTree.jar just have the jar file in the same directory as the
training and test files. Then under the directory type the command line:

    java -jar ass1-decTree.jar <training file name> <test file name>

For example,

    java -jar ass1-knn.jar hepatitis-training hepatitis-test

The program assumes the first line has the attribute labels. The remaining lines
are space separated values. Where the first value on each line is a String
representing that instance's result and the remaining values are either "true" or
"false" for the given attributes for the first line. If a file doesn't meet these
assumptions, the program may not function as intended or throw and error.

The program tend to output quite a lot of information. At the time it reads a file, it
displays the data to confirm it has read it properly. After reading the training file,
it will build and display the decision tree. After reading the test file, it will test
the decision tree and report the results.