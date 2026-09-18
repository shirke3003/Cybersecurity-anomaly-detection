package com.cybersecurity;

public class TrafficRecord {

    private final double[] features;
    private final int label;

    public TrafficRecord(double[] features, int label) {
        this.features = features;
        this.label = label;
    }

    public double[] getFeatures() {
        return features;
    }

    public int getLabel() {
        return label;
    }
}