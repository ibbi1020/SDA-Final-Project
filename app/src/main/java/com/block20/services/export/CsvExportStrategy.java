package com.block20.services.export;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CsvExportStrategy implements ExportStrategy {

    @Override
    public void export(String filename, String[] headers, List<String[]> data) {
        // Ensure filename ends with .csv
        if (!filename.endsWith(".csv")) {
            filename += ".csv";
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            // 1. Write Headers
            pw.println(convertToCSV(headers));

            // 2. Write Data Rows
            for (String[] row : data) {
                pw.println(convertToCSV(row));
            }

            System.out.println("Export Success: File saved to " + filename);

        } catch (IOException e) {
            System.err.println("Export Failed: " + e.getMessage());
            throw new RuntimeException("Could not save file: " + e.getMessage());
        }
    }

    // Helper to escape commas and quotes
    private String convertToCSV(String[] data) {
        return Stream.of(data)
            .map(this::escapeSpecialCharacters)
            .collect(Collectors.joining(","));
    }

    private String escapeSpecialCharacters(String data) {
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
}