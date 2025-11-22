package com.block20.services.export;

import java.util.List;

public interface ExportStrategy {
    // Generic method: Takes a filename, headers, and a list of data rows
    void export(String filename, String[] headers, List<String[]> data);
}