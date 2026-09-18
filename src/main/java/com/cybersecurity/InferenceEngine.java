package com.cybersecurity;

import java.util.Set;

public class InferenceEngine {

    public void infer(KnowledgeBase kb) {

        boolean newFactAdded = true;

        while (newFactAdded) {

            newFactAdded = false;

            Set<String> facts = kb.getFacts();

            // Rule 1:
            // High packet rate + high flow rate
            // -> Suspicious
            if (facts.contains("HighPacketRate")
                    && facts.contains("HighFlowRate")
                    && !facts.contains("Suspicious")) {

                kb.addFact("Suspicious");
                newFactAdded = true;

                System.out.println(
                        "Rule 1 fired: HighPacketRate + HighFlowRate -> Suspicious"
                );
            }

            // Rule 2:
            // Suspicious + high jitter
            // -> Anomalous
            if (facts.contains("Suspicious")
                    && facts.contains("HighJitter")
                    && !facts.contains("Anomalous")) {

                kb.addFact("Anomalous");
                newFactAdded = true;

                System.out.println(
                        "Rule 2 fired: Suspicious + HighJitter -> Anomalous"
                );
            }

            // Rule 3:
            // Anomalous + XGBoost says malicious
            // -> High Risk
            if (facts.contains("Anomalous")
                    && facts.contains("XGBoostMalicious")
                    && !facts.contains("HighRisk")) {

                kb.addFact("HighRisk");
                newFactAdded = true;

                System.out.println(
                        "Rule 3 fired: Anomalous + XGBoostMalicious -> HighRisk"
                );
            }
        }
    }
}