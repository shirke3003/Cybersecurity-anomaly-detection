package com.cybersecurity;

import java.util.HashSet;
import java.util.Set;

public class KnowledgeBase {

    // Facts currently known about a traffic record
    private final Set<String> facts;

    public KnowledgeBase() {
        facts = new HashSet<>();
    }

    // Add a new fact
    public void addFact(String fact) {
        facts.add(fact);
    }

    // Check whether a fact exists
    public boolean hasFact(String fact) {
        return facts.contains(fact);
    }

    // Get all known facts
    public Set<String> getFacts() {
        return new HashSet<>(facts);
    }

    // Display current facts
    public void displayFacts() {

        System.out.println("\n========== KNOWLEDGE BASE ==========");

        if (facts.isEmpty()) {
            System.out.println("No facts available.");
        } else {
            for (String fact : facts) {
                System.out.println(fact + " : TRUE");
            }
        }

        System.out.println("====================================");
    }
}
