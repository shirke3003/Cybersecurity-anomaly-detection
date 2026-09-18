package com.cybersecurity;

import ml.dmlc.xgboost4j.java.Booster;
import ml.dmlc.xgboost4j.java.DMatrix;
import ml.dmlc.xgboost4j.java.XGBoost;

import java.util.HashMap;
import java.util.Map;

public class XGBoostClassifier {

    private Booster booster;

    public void train(
            double[][] features,
            int[] labels) throws Exception {

        int rows = features.length;
        int cols = features[0].length;

        // XGBoost4J expects float values
        float[] data = new float[rows * cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                data[i * cols + j] =
                        (float) features[i][j];
            }
        }

        float[] target = new float[labels.length];

        for (int i = 0; i < labels.length; i++) {
            target[i] = labels[i];
        }

        DMatrix trainMatrix =
                new DMatrix(
                        data,
                        rows,
                        cols,
                        Float.NaN
                );

        trainMatrix.setLabel(target);

        Map<String, Object> parameters =
                new HashMap<>();

        parameters.put(
                "objective",
                "binary:logistic"
        );

        parameters.put(
                "eval_metric",
                "logloss"
        );

        parameters.put(
                "max_depth",
                3
        );

        parameters.put(
                "eta",
                0.1
        );

        parameters.put(
                "subsample",
                0.8
        );

        parameters.put(
                "colsample_bytree",
                0.8
        );

        parameters.put(
                "seed",
                42
        );

        System.out.println(
                "\nTraining XGBoost..."
        );

        booster =
                XGBoost.train(
                        trainMatrix,
                        parameters,
                        50,
                        new HashMap<>(),
                        null,
                        null
                );

        System.out.println(
                "XGBoost training complete."
        );
    }

    public double[] predict(
            double[][] features) throws Exception {

        int rows = features.length;
        int cols = features[0].length;

        float[] data =
                new float[rows * cols];

        for (int i = 0; i < rows; i++) {

            for (int j = 0; j < cols; j++) {

                data[i * cols + j] =
                        (float) features[i][j];
            }
        }

        DMatrix testMatrix =
                new DMatrix(
                        data,
                        rows,
                        cols,
                        Float.NaN
                );

        float[][] predictions =
                booster.predict(testMatrix);

        double[] probabilities =
                new double[rows];

        for (int i = 0; i < rows; i++) {
            probabilities[i] =
                    predictions[i][0];
        }

        return probabilities;
    }
}