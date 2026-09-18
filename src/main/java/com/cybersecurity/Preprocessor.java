package com.cybersecurity;

import java.util.ArrayList;
import java.util.List;

public class Preprocessor {

    private double[] minValues;
    private double[] maxValues;

    public void fit(List<TrafficRecord> records) {

        int featureCount = records.get(0).getFeatures().length;

        minValues = new double[featureCount];
        maxValues = new double[featureCount];

        for (int i = 0; i < featureCount; i++) {
            minValues[i] = Double.POSITIVE_INFINITY;
            maxValues[i] = Double.NEGATIVE_INFINITY;
        }

        for (TrafficRecord record : records) {

            double[] features = record.getFeatures();

            for (int i = 0; i < featureCount; i++) {

                minValues[i] =
                        Math.min(minValues[i], features[i]);

                maxValues[i] =
                        Math.max(maxValues[i], features[i]);
            }
        }
    }

    public double[] transform(double[] features) {

        if (minValues == null) {
            throw new IllegalStateException(
                    "Preprocessor must be fitted before transformation."
            );
        }

        double[] normalized =
                new double[features.length];

        for (int i = 0; i < features.length; i++) {

            double range =
                    maxValues[i] - minValues[i];

            if (range == 0) {
                normalized[i] = 0.0;
            } else {
                normalized[i] =
                        (features[i] - minValues[i]) / range;
            }
        }

        return normalized;
    }

    public List<TrafficRecord> transformRecords(
            List<TrafficRecord> records) {

        List<TrafficRecord> result =
                new ArrayList<>();

        for (TrafficRecord record : records) {

            result.add(
                    new TrafficRecord(
                            transform(record.getFeatures()),
                            record.getLabel()
                    )
            );
        }

        return result;
    }
}