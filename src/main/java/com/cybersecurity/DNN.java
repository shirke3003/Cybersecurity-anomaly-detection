package com.cybersecurity;

import java.util.Arrays;
import java.util.Random;

public class DNN {

    private final int inputSize;
    private final int hidden1Size;
    private final int hidden2Size;

    // Weights
    private final double[][] weights1;
    private final double[][] weights2;
    private final double[] weights3;

    // Biases
    private final double[] bias1;
    private final double[] bias2;
    private double bias3;

    private final Random random;

    public DNN(int inputSize, int hidden1Size, int hidden2Size) {

        this.inputSize = inputSize;
        this.hidden1Size = hidden1Size;
        this.hidden2Size = hidden2Size;

        random = new Random(42);

        weights1 = new double[inputSize][hidden1Size];
        weights2 = new double[hidden1Size][hidden2Size];
        weights3 = new double[hidden2Size];

        bias1 = new double[hidden1Size];
        bias2 = new double[hidden2Size];

        initializeWeights();
    }

    private void initializeWeights() {

        // Xavier-style initialization
        double limit1 =
                Math.sqrt(6.0 / (inputSize + hidden1Size));

        double limit2 =
                Math.sqrt(6.0 / (hidden1Size + hidden2Size));

        double limit3 =
                Math.sqrt(6.0 / (hidden2Size + 1));

        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < hidden1Size; j++) {
                weights1[i][j] =
                        random.nextDouble() * 2 * limit1 - limit1;
            }
        }

        for (int i = 0; i < hidden1Size; i++) {
            for (int j = 0; j < hidden2Size; j++) {
                weights2[i][j] =
                        random.nextDouble() * 2 * limit2 - limit2;
            }
        }

        for (int i = 0; i < hidden2Size; i++) {
            weights3[i] =
                    random.nextDouble() * 2 * limit3 - limit3;
        }
    }

    // ReLU activation
    private double relu(double x) {
        return Math.max(0, x);
    }

    // Derivative of ReLU
    private double reluDerivative(double x) {
        return x > 0 ? 1.0 : 0.0;
    }

    // Sigmoid activation
    private double sigmoid(double x) {

        if (x < -500) {
            return 0.0;
        }

        if (x > 500) {
            return 1.0;
        }

        return 1.0 / (1.0 + Math.exp(-x));
    }

    /*
     * Forward propagation.
     *
     * Returns:
     * [0] = hidden layer 1
     * [1] = hidden layer 2
     * [2] = output probability
     */
    private ForwardResult forward(double[] input) {

        double[] z1 = new double[hidden1Size];
        double[] a1 = new double[hidden1Size];

        // Input → Hidden Layer 1
        for (int j = 0; j < hidden1Size; j++) {

            double sum = bias1[j];

            for (int i = 0; i < inputSize; i++) {
                sum += input[i] * weights1[i][j];
            }

            z1[j] = sum;
            a1[j] = relu(sum);
        }

        double[] z2 = new double[hidden2Size];
        double[] a2 = new double[hidden2Size];

        // Hidden Layer 1 → Hidden Layer 2
        for (int j = 0; j < hidden2Size; j++) {

            double sum = bias2[j];

            for (int i = 0; i < hidden1Size; i++) {
                sum += a1[i] * weights2[i][j];
            }

            z2[j] = sum;
            a2[j] = relu(sum);
        }

        // Hidden Layer 2 → Output
        double sum = bias3;

        for (int i = 0; i < hidden2Size; i++) {
            sum += a2[i] * weights3[i];
        }

        double output = sigmoid(sum);

        return new ForwardResult(
                z1, a1,
                z2, a2,
                output
        );
    }

    /*
     * Train using backpropagation.
     */
    public void train(
            double[][] inputs,
            int[] labels,
            int epochs,
            double learningRate) {

        for (int epoch = 0; epoch < epochs; epoch++) {

            double totalLoss = 0.0;
            int correct = 0;

            for (int sample = 0; sample < inputs.length; sample++) {

                double[] input = inputs[sample];
                int label = labels[sample];

                // -------------------------
                // Forward propagation
                // -------------------------

                ForwardResult result =
                        forward(input);

                double prediction = result.output;

                // Prevent log(0)
                double safePrediction =
                        Math.max(
                                1e-7,
                                Math.min(1.0 - 1e-7, prediction)
                        );

                // Binary cross entropy
                double loss =
                        -(label * Math.log(safePrediction)
                                + (1 - label)
                                * Math.log(1 - safePrediction));

                totalLoss += loss;

                if ((prediction >= 0.5 ? 1 : 0) == label) {
                    correct++;
                }

                // -------------------------
                // Backpropagation
                // -------------------------

                // Output layer error
                double delta3 =
                        prediction - label;

                // Hidden layer 2 error
                double[] delta2 =
                        new double[hidden2Size];

                for (int j = 0; j < hidden2Size; j++) {

                    delta2[j] =
                            delta3
                            * weights3[j]
                            * reluDerivative(result.z2[j]);
                }

                // Hidden layer 1 error
                double[] delta1 =
                        new double[hidden1Size];

                for (int j = 0; j < hidden1Size; j++) {

                    double error = 0.0;

                    for (int k = 0; k < hidden2Size; k++) {
                        error += delta2[k] * weights2[j][k];
                    }

                    delta1[j] =
                            error
                            * reluDerivative(result.z1[j]);
                }

                // -------------------------
                // Update output weights
                // -------------------------

                for (int j = 0; j < hidden2Size; j++) {

                    weights3[j] -=
                            learningRate
                            * delta3
                            * result.a2[j];
                }

                bias3 -=
                        learningRate * delta3;

                // -------------------------
                // Update hidden layer 2
                // -------------------------

                for (int i = 0; i < hidden1Size; i++) {

                    for (int j = 0; j < hidden2Size; j++) {

                        weights2[i][j] -=
                                learningRate
                                * delta2[j]
                                * result.a1[i];
                    }
                }

                for (int j = 0; j < hidden2Size; j++) {

                    bias2[j] -=
                            learningRate * delta2[j];
                }

                // -------------------------
                // Update hidden layer 1
                // -------------------------

                for (int i = 0; i < inputSize; i++) {

                    for (int j = 0; j < hidden1Size; j++) {

                        weights1[i][j] -=
                                learningRate
                                * delta1[j]
                                * input[i];
                    }
                }

                for (int j = 0; j < hidden1Size; j++) {

                    bias1[j] -=
                            learningRate * delta1[j];
                }
            }

            double averageLoss =
                    totalLoss / inputs.length;

            double accuracy =
                    (double) correct / inputs.length * 100.0;

            System.out.printf(
                    "Epoch %d/%d | Loss: %.4f | Accuracy: %.2f%%%n",
                    epoch + 1,
                    epochs,
                    averageLoss,
                    accuracy
            );
        }
    }

    /*
     * Returns the probability that a traffic record is malicious.
     */
    public double predictProbability(double[] input) {

        return forward(input).output;
    }

    /*
     * Extract the learned representation from
     * the second hidden layer.
     *
     * These are the "deep features" that will
     * later be passed to XGBoost.
     */
    public double[] extractDeepFeatures(double[] input) {

        return forward(input).a2;
    }

    private static class ForwardResult {

        double[] z1;
        double[] a1;

        double[] z2;
        double[] a2;

        double output;

        ForwardResult(
                double[] z1,
                double[] a1,
                double[] z2,
                double[] a2,
                double output) {

            this.z1 = z1;
            this.a1 = a1;
            this.z2 = z2;
            this.a2 = a2;
            this.output = output;
        }
    }
}