package com.cybersecurity;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataLoader {

    // Numerical features selected from UNSW-NB15
    private static final int[] FEATURE_COLUMNS = {
        1, 5, 6, 7, 8, 9, 10, 11,
        12, 13, 14, 15, 16, 17, 18, 19,
        20, 21, 22, 23, 24, 25, 26, 27,
        28, 31, 32, 33
    };

    // "label" is column 44 (0-based)
    private static final int LABEL_COLUMN = 44;

    public static List<TrafficRecord> load(String filePath) throws IOException {

        List<TrafficRecord> records = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            // Skip header
            String line = reader.readLine();

            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] values = line.split(",", -1);

                double[] features = new double[FEATURE_COLUMNS.length];

                for (int i = 0; i < FEATURE_COLUMNS.length; i++) {
                    features[i] = Double.parseDouble(
                        values[FEATURE_COLUMNS[i]].trim()
                    );
                }

                int label = Integer.parseInt(
                    values[LABEL_COLUMN].trim()
                );

                records.add(new TrafficRecord(features, label));
            }
        }

        return records;
    }
}