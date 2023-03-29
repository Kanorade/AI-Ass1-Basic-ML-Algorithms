To run ass1-perceptron.jar just have the jar file in the same directory as the
training and test files. Then under the directory type the command line:

    java -jar ass1-perceptron.jar <training file name>

For example,

    java -jar ass1-perceptron.jar ionosphere.data

The program assumes the first line has the feature labels to be ignored,
and the remaining lines are space separated values where the last value
is either 'g', or 'b'.

The perceptron will stop after 100 iterations, the learning rate is set to 0.1,
and lastly, the initial weights are set 2 zero. If you want to change these
numbers, you'll need to adjust them in the source code and recompile.