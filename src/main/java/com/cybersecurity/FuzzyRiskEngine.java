package com.cybersecurity;

public class FuzzyRiskEngine {

    public String assessRisk(
            double attackConfidence,
            double anomalySeverity) {

        // Fuzzy membership values for attack confidence
        double confidenceLow =
                lowMembership(attackConfidence);

        double confidenceMedium =
                mediumMembership(attackConfidence);

        double confidenceHigh =
                highMembership(attackConfidence);

        // Fuzzy membership values for anomaly severity
        double severityLow =
                lowMembership(anomalySeverity);

        double severityMedium =
                mediumMembership(anomalySeverity);

        double severityHigh =
                highMembership(anomalySeverity);

        // ------------------------------------------
        // FUZZY RULES
        // ------------------------------------------

        // Low confidence + Low severity -> Low risk
        double rule1 =
                Math.min(confidenceLow, severityLow);

        // Low confidence + Medium severity -> Medium risk
        double rule2 =
                Math.min(confidenceLow, severityMedium);

        // Medium confidence + Medium severity -> Medium risk
        double rule3 =
                Math.min(confidenceMedium, severityMedium);

        // Medium confidence + High severity -> High risk
        double rule4 =
                Math.min(confidenceMedium, severityHigh);

        // High confidence + Medium severity -> High risk
        double rule5 =
                Math.min(confidenceHigh, severityMedium);

        // High confidence + High severity -> High risk
        double rule6 =
                Math.min(confidenceHigh, severityHigh);

        // ------------------------------------------
        // AGGREGATE RISK LEVELS
        // ------------------------------------------

        double lowRisk =
                rule1;

        double mediumRisk =
                Math.max(rule2, rule3);

        double highRisk =
                Math.max(
                        rule4,
                        Math.max(rule5, rule6)
                );

        // ------------------------------------------
        // DEFUZZIFICATION
        // ------------------------------------------

        double weightedRisk =
                (lowRisk * 0.25
                        + mediumRisk * 0.55
                        + highRisk * 0.90);

        double totalMembership =
                lowRisk
                        + mediumRisk
                        + highRisk;

        if (totalMembership == 0) {
            return "LOW";
        }

        double finalRisk =
                weightedRisk / totalMembership;

        if (finalRisk < 0.40) {
            return "LOW";
        } else if (finalRisk < 0.70) {
            return "MEDIUM";
        } else {
            return "HIGH";
        }
    }

    // ------------------------------------------
    // FUZZY MEMBERSHIP FUNCTIONS
    // ------------------------------------------

    private double lowMembership(double value) {

        if (value <= 0.30) {
            return 1.0;
        }

        if (value >= 0.50) {
            return 0.0;
        }

        return (0.50 - value) / 0.20;
    }

    private double mediumMembership(double value) {

        if (value <= 0.30 || value >= 0.80) {
            return 0.0;
        }

        if (value <= 0.55) {
            return (value - 0.30) / 0.25;
        }

        return (0.80 - value) / 0.25;
    }

    private double highMembership(double value) {

        if (value <= 0.60) {
            return 0.0;
        }

        if (value >= 0.80) {
            return 1.0;
        }

        return (value - 0.60) / 0.20;
    }
}