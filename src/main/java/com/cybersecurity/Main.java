package com.cybersecurity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Main {

    public static void main(String[] args) {

        String trainingFile =
                "data/UNSW_NB15_training-set.csv";

        String testingFile =
        "data/UNSW_NB15_testing-set.csv";        

        try {

            // ==========================================
            // 1. LOAD DATA
            // ==========================================

            List<TrafficRecord> allRecords =
                    DataLoader.load(trainingFile);

            List<TrafficRecord> testingRecords =
        DataLoader.load(testingFile);

System.out.println(
        "Testing records: "
        + testingRecords.size()
);        

            // ==========================================
// 1. CREATE BALANCED TRAINING/VALIDATION SET
// ==========================================

List<TrafficRecord> normalRecords = new ArrayList<>();
List<TrafficRecord> maliciousRecords = new ArrayList<>();

for (TrafficRecord record : allRecords) {

    if (record.getLabel() == 0) {
        normalRecords.add(record);
    } else {
        maliciousRecords.add(record);
    }
}

// Shuffle both classes so that we do not depend
// on the original ordering of the dataset.
Collections.shuffle(normalRecords, new Random(42));
Collections.shuffle(maliciousRecords, new Random(42));

// 4000 training records:
// 2000 Normal + 2000 Malicious
int trainingSize = 4000;

// 1000 validation records:
// 500 Normal + 500 Malicious
int validationSize = 1000;

List<TrafficRecord> trainingRecords =
        new ArrayList<>();

List<TrafficRecord> validationRecords =
        new ArrayList<>();

// Training set
trainingRecords.addAll(
        normalRecords.subList(0, 2000)
);

trainingRecords.addAll(
        maliciousRecords.subList(0, 2000)
);

// Validation set
validationRecords.addAll(
        normalRecords.subList(2000, 2500)
);

validationRecords.addAll(
        maliciousRecords.subList(2000, 2500)
);

// Shuffle final datasets
Collections.shuffle(trainingRecords, new Random(42));
Collections.shuffle(validationRecords, new Random(42));

System.out.println(
        "Training records: " + trainingRecords.size()
);

System.out.println(
        "Validation records: " + validationRecords.size()
);

            int trainNormal = 0;
            int trainMalicious = 0;

            for (TrafficRecord record : trainingRecords) {
                if (record.getLabel() == 0) {
                    trainNormal++;
                } else {
                    trainMalicious++;
                }
}

            int validationNormal = 0;
            int validationMalicious = 0;

            for (TrafficRecord record : validationRecords) {
                if (record.getLabel() == 0) {
                    validationNormal++;
                } else {
                    validationMalicious++;
                }
}

            System.out.println("\n========== LABEL DISTRIBUTION ==========");
            System.out.println("Training Normal     : " + trainNormal);
            System.out.println("Training Malicious  : " + trainMalicious);
            System.out.println("Validation Normal   : " + validationNormal);
            System.out.println("Validation Malicious: " + validationMalicious);
            System.out.println("=========================================");

            // ==========================================
            // 2. PREPROCESS
            // ==========================================

            Preprocessor preprocessor =
                    new Preprocessor();

            preprocessor.fit(trainingRecords);

            double[][] trainInputs =
                    new double[trainingSize][];

            int[] trainLabels =
                    new int[trainingSize];

            for (int i = 0; i < trainingSize; i++) {

                trainInputs[i] =
                        preprocessor.transform(
                                trainingRecords
                                        .get(i)
                                        .getFeatures()
                        );

                trainLabels[i] =
                        trainingRecords
                                .get(i)
                                .getLabel();
            }

            double[][] validationInputs =
                    new double[validationSize][];

            int[] validationLabels =
                    new int[validationSize];

            for (int i = 0; i < validationSize; i++) {

                validationInputs[i] =
                        preprocessor.transform(
                                validationRecords
                                        .get(i)
                                        .getFeatures()
                        );

                validationLabels[i] =
                        validationRecords
                                .get(i)
                                .getLabel();
            }

// ==========================================
// PREPROCESS OFFICIAL TESTING DATA
// ==========================================

double[][] testInputs =
        new double[testingRecords.size()][];

int[] testLabels =
        new int[testingRecords.size()];

for (int i = 0; i < testingRecords.size(); i++) {

    testInputs[i] =
            preprocessor.transform(
                    testingRecords
                            .get(i)
                            .getFeatures()
            );

    testLabels[i] =
            testingRecords
                    .get(i)
                    .getLabel();
}


            // ==========================================
            // 3. DNN
            // ==========================================

            DNN dnn =
                    new DNN(
                            28,
                            16,
                            8
                    );

            System.out.println(
                    "\n========== DNN =========="
            );

            dnn.train(
                    trainInputs,
                    trainLabels,
                    10,
                    0.01
            );

            // ==========================================
            // 4. EXTRACT DEEP FEATURES
            // ==========================================

            double[][] trainDeepFeatures =
                    new double[trainingSize][8];

            double[][] validationDeepFeatures =
                    new double[validationSize][8];

            double[][] testDeepFeatures =
                    new double[testingRecords.size()][8];        

            for (int i = 0; i < trainingSize; i++) {

                trainDeepFeatures[i] =
                        dnn.extractDeepFeatures(
                                trainInputs[i]
                        );
            }
 
            for (int i = 0; i < testingRecords.size(); i++) {

    testDeepFeatures[i] =
            dnn.extractDeepFeatures(
                    testInputs[i]
            );
}

            for (int i = 0; i < validationSize; i++) {

                validationDeepFeatures[i] =
                        dnn.extractDeepFeatures(
                                validationInputs[i]
                        );
            }

            System.out.println(
                    "\nDNN deep features extracted."
            );

            System.out.println(
                    "Deep feature size: "
                    + trainDeepFeatures[0].length
            );

            // ==========================================
            // 5. XGBOOST
            // ==========================================

            XGBoostClassifier xgboost =
                    new XGBoostClassifier();

            xgboost.train(
                    trainDeepFeatures,
                    trainLabels
            );

            // ==========================================
            // 6. XGBOOST VALIDATION
            // ==========================================

            double[] probabilities =
                    xgboost.predict(
                            validationDeepFeatures
                    );

            double[] testProbabilities =
                        xgboost.predict(
                                  testDeepFeatures
                );

            int correct = 0;

            int truePositive = 0;
            int trueNegative = 0;
            int falsePositive = 0;
            int falseNegative = 0;

            for (int i = 0;
                 i < validationSize;
                 i++) {

                int prediction =
                        probabilities[i] >= 0.5
                                ? 1
                                : 0;

                int actual =
                        validationLabels[i];

                if (prediction == actual) {
                    correct++;
                }

                if (prediction == 1 && actual == 1) {
                    truePositive++;
                }

                else if (prediction == 0 && actual == 0) {
                    trueNegative++;
                }

                else if (prediction == 1 && actual == 0) {
                    falsePositive++;
                }

                else if (prediction == 0 && actual == 1) {
                    falseNegative++;
                }
            }

            double accuracy =
                    (double) correct
                    / validationSize;

            double precision =
                    truePositive + falsePositive == 0
                            ? 0
                            : (double) truePositive
                            / (truePositive + falsePositive);

            double recall =
                    truePositive + falseNegative == 0
                            ? 0
                            : (double) truePositive
                            / (truePositive + falseNegative);

            double f1 =
                    precision + recall == 0
                            ? 0
                            : 2 * precision * recall
                            / (precision + recall);

            // ==========================================
            // 7. RESULTS
            // ==========================================

            System.out.println(
                    "\n========== HYBRID DNN + XGBOOST =========="
            );

            System.out.printf(
                    "Accuracy : %.2f%%%n",
                    accuracy * 100
            );

            System.out.printf(
                    "Precision: %.4f%n",
                    precision
            );

            System.out.printf(
                    "Recall   : %.4f%n",
                    recall
            );

            System.out.printf(
                    "F1 Score : %.4f%n",
                    f1
            );

            System.out.println(
                    "\nConfusion Matrix:"
            );

            System.out.println(
                    "True Positive : " + truePositive
            );

            System.out.println(
                    "True Negative : " + trueNegative
            );

            System.out.println(
                    "False Positive: " + falsePositive
            );

            System.out.println(
                    "False Negative: " + falseNegative
            );

            System.out.println(
                    "=========================================="
            );

// ==========================================
// FINAL OFFICIAL TEST RESULTS
// ==========================================

int testCorrect = 0;

int testTP = 0;
int testTN = 0;
int testFP = 0;
int testFN = 0;

for (int i = 0; i < testingRecords.size(); i++) {

    int prediction =
            testProbabilities[i] >= 0.5
                    ? 1
                    : 0;

    int actual =
            testLabels[i];

    if (prediction == actual) {
        testCorrect++;
    }

    if (prediction == 1 && actual == 1) {
        testTP++;
    }

    else if (prediction == 0 && actual == 0) {
        testTN++;
    }

    else if (prediction == 1 && actual == 0) {
        testFP++;
    }

    else if (prediction == 0 && actual == 1) {
        testFN++;
    }
}

double testAccuracy =
        (double) testCorrect
        / testingRecords.size();

double testPrecision =
        testTP + testFP == 0
                ? 0
                : (double) testTP
                / (testTP + testFP);

double testRecall =
        testTP + testFN == 0
                ? 0
                : (double) testTP
                / (testTP + testFN);

double testF1 =
        testPrecision + testRecall == 0
                ? 0
                : 2 * testPrecision * testRecall
                / (testPrecision + testRecall);

System.out.println(
        "\n========== OFFICIAL TEST RESULTS =========="
);

System.out.println(
        "Testing Records: "
        + testingRecords.size()
);

System.out.printf(
        "Accuracy : %.2f%%%n",
        testAccuracy * 100
);

System.out.printf(
        "Precision: %.4f%n",
        testPrecision
);

System.out.printf(
        "Recall   : %.4f%n",
        testRecall
);

System.out.printf(
        "F1 Score : %.4f%n",
        testF1
);

System.out.println(
        "\nConfusion Matrix:"
);

System.out.println(
        "True Positive : " + testTP
);

System.out.println(
        "True Negative : " + testTN
);

System.out.println(
        "False Positive: " + testFP
);

System.out.println(
        "False Negative: " + testFN
);

System.out.println(
        "============================================"
);

// ==========================================
// 8. FINAL END-TO-END DEMONSTRATION
// ==========================================

System.out.println(
        "\n\n================================================"
);

System.out.println(
        "       END-TO-END CYBERSECURITY DEMO"
);

System.out.println(
        "================================================"
);

// Find three useful demonstration cases
int normalCase = -1;
int maliciousCase = -1;
int errorCase = -1;

for (int i = 0; i < testingRecords.size(); i++) {

    int actual = testLabels[i];

    int prediction =
            testProbabilities[i] >= 0.5
                    ? 1
                    : 0;

    // Correctly classified normal
    if (actual == 0
            && prediction == 0
            && normalCase == -1) {

        normalCase = i;
    }

    // Correctly classified malicious
    if (actual == 1
            && prediction == 1
            && maliciousCase == -1) {

        maliciousCase = i;
    }

    // Find a misclassified case
    if (actual != prediction
            && errorCase == -1) {

        errorCase = i;
    }

    if (normalCase != -1
            && maliciousCase != -1
            && errorCase != -1) {

        break;
    }
}


// ------------------------------------------
// DEMO CASE 1: NORMAL TRAFFIC
// ------------------------------------------

if (normalCase != -1) {

    runSecurityDemo(
            testingRecords.get(normalCase),
            testInputs[normalCase],
            testProbabilities[normalCase],
            testLabels[normalCase],
            "CASE 1 - NORMAL TRAFFIC"
    );
}


// ------------------------------------------
// DEMO CASE 2: MALICIOUS TRAFFIC
// ------------------------------------------

if (maliciousCase != -1) {

    runSecurityDemo(
            testingRecords.get(maliciousCase),
            testInputs[maliciousCase],
            testProbabilities[maliciousCase],
            testLabels[maliciousCase],
            "CASE 2 - MALICIOUS TRAFFIC"
    );
}


// ------------------------------------------
// DEMO CASE 3: MODEL ERROR
// ------------------------------------------

if (errorCase != -1) {

    runSecurityDemo(
            testingRecords.get(errorCase),
            testInputs[errorCase],
            testProbabilities[errorCase],
            testLabels[errorCase],
            "CASE 3 - MISCLASSIFIED TRAFFIC"
    );
}

          } catch (Exception e) {

        System.out.println(
                "\nERROR: " + e.getMessage()
        );

        e.printStackTrace();
    }
}


// ==========================================
// END-TO-END SECURITY DEMO METHOD
// ==========================================

private static void runSecurityDemo(
        TrafficRecord record,
        double[] processedFeatures,
        double xgboostProbability,
        int actualLabel,
        String caseName) {

    System.out.println(
            "\n\n=============================================="
    );

    System.out.println(
            caseName
    );

    System.out.println(
            "=============================================="
    );

    // ------------------------------------------
    // 1. ML CLASSIFICATION
    // ------------------------------------------

    int prediction =
            xgboostProbability >= 0.5
                    ? 1
                    : 0;

    String classification =
            prediction == 1
                    ? "MALICIOUS"
                    : "NORMAL";

    String actual =
            actualLabel == 1
                    ? "MALICIOUS"
                    : "NORMAL";

    System.out.println(
            "\n---------- ML CLASSIFICATION ----------"
    );

    System.out.println(
            "Actual Traffic     : " + actual
    );

    System.out.println(
            "Predicted Traffic  : " + classification
    );

    System.out.printf(
            "XGBoost Confidence : %.4f%n",
            xgboostProbability
    );


    // ------------------------------------------
    // 2. KNOWLEDGE BASE
    // ------------------------------------------

    KnowledgeBase knowledgeBase =
            new KnowledgeBase();

    double rate = record.getFeatures()[5];

double sourceJitter =
        record.getFeatures()[14];

double destinationJitter =
        record.getFeatures()[15];

double serverCount =
        record.getFeatures()[27];


    System.out.println(
            "\n---------- KNOWLEDGE BASE ----------"
    );

    System.out.printf(
            "Rate           : %.4f%n",
            rate
    );

    System.out.printf(
            "Source Jitter  : %.4f%n",
            sourceJitter
    );

    System.out.printf(
            "Destination Jitter : %.4f%n",
            destinationJitter
    );

    System.out.printf(
            "Server Count   : %.4f%n",
            serverCount
    );


    // ------------------------------------------
    // DERIVE FACTS
    // ------------------------------------------

    if (processedFeatures[5] >= 0.75) {
    knowledgeBase.addFact(
            "HighPacketRate"
    );
}

if (processedFeatures[27] >= 0.75) {
    knowledgeBase.addFact(
            "HighFlowRate"
    );
}

if (processedFeatures[14] >= 0.75
        || processedFeatures[15] >= 0.75) {

    knowledgeBase.addFact(
            "HighJitter"
    );
}
    if (xgboostProbability >= 0.5) {

        knowledgeBase.addFact(
                "XGBoostMalicious"
        );
    }

    knowledgeBase.displayFacts();


    // ------------------------------------------
    // 3. FORWARD CHAINING
    // ------------------------------------------

    System.out.println(
            "\n---------- FORWARD CHAINING ----------"
    );

    InferenceEngine inferenceEngine =
            new InferenceEngine();

    inferenceEngine.infer(
            knowledgeBase
    );

    for (String fact :
            knowledgeBase.getFacts()) {

        System.out.println(
                fact + " : TRUE"
        );
    }


    // ------------------------------------------
    // 4. FUZZY RISK
    // ------------------------------------------

    System.out.println(
            "\n---------- FUZZY RISK ----------"
    );

    double attackConfidence =
            xgboostProbability;

    int evidenceCount = 0;

    if (knowledgeBase.hasFact(
            "HighPacketRate")) {

        evidenceCount++;
    }

    if (knowledgeBase.hasFact(
            "HighFlowRate")) {

        evidenceCount++;
    }

    if (knowledgeBase.hasFact(
            "HighJitter")) {

        evidenceCount++;
    }

    if (knowledgeBase.hasFact(
            "Anomalous")) {

        evidenceCount++;
    }

    double anomalySeverity =
        evidenceCount / 4.0;

// XGBoost malicious prediction contributes
// directly to the anomaly severity.
if (knowledgeBase.hasFact("XGBoostMalicious")) {

    anomalySeverity =
            Math.max(
                    anomalySeverity,
                    attackConfidence
            );
}

    FuzzyRiskEngine fuzzyRiskEngine =
            new FuzzyRiskEngine();

    String riskLevel =
            fuzzyRiskEngine.assessRisk(
                    attackConfidence,
                    anomalySeverity
            );

    System.out.printf(
            "Attack Confidence : %.4f%n",
            attackConfidence
    );

    System.out.printf(
            "Anomaly Severity  : %.4f%n",
            anomalySeverity
    );

    System.out.println(
            "Final Risk Level  : " + riskLevel
    );


    // ------------------------------------------
    // 5. CONDITIONAL PLANNER
    // ------------------------------------------

    ResponsePlanner responsePlanner =
            new ResponsePlanner();

    responsePlanner.executePlan(
            riskLevel
    );
}
}