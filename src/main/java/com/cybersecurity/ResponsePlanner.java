package com.cybersecurity;

public class ResponsePlanner {

    public void executePlan(String riskLevel) {

        System.out.println("\n========== CONDITIONAL PLANNER ==========");

        switch (riskLevel) {

            case "LOW":
                System.out.println("Risk Level : LOW");
                System.out.println("Action     : LOG INCIDENT");
                System.out.println("Action     : CONTINUE MONITORING");
                break;

            case "MEDIUM":
                System.out.println("Risk Level : MEDIUM");
                System.out.println("Action     : PERFORM ADDITIONAL ANALYSIS");
                System.out.println("Action     : CONTINUE MONITORING");
                System.out.println("Action     : LOG INCIDENT");
                break;

            case "HIGH":
                System.out.println("Risk Level : HIGH");
                System.out.println("Action     : GENERATE SECURITY ALERT");
                System.out.println("Action     : ISOLATE / BLOCK TRAFFIC");
                System.out.println("Action     : LOG INCIDENT");
                break;

            default:
                System.out.println("Unknown risk level.");
                System.out.println("Action     : CONTINUE MONITORING");
                break;
        }

        System.out.println("=========================================");
    }
}