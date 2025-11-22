package com.block20.services.impl;

import com.block20.controllers.FinancialReportsController.RevenueBreakdown;
import com.block20.services.ExportService;
import com.block20.services.export.CsvExportStrategy;
import com.block20.services.export.ExportStrategy;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExportServiceImpl implements ExportService {
    
    private ExportStrategy strategy;

    public ExportServiceImpl() {
        // Default to CSV for now
        this.strategy = new CsvExportStrategy();
    }

    @Override
    public void exportRevenueReport(List<RevenueBreakdown> data) {
        if (data == null || data.isEmpty()) {
            System.err.println("EXPORT ERROR: No data to export!");
            return;
        }

        // 1. Get path to Desktop
        String userHome = System.getProperty("user.home");
        String desktopPath = userHome + "/Desktop/"; 
        
        // 2. Create Filename with Date
        String filename = desktopPath + "Revenue_Report_" + LocalDate.now().toString();

        String[] headers = {"Category", "Amount", "Count", "Average"};
        
        List<String[]> rows = new ArrayList<>();
        for (RevenueBreakdown item : data) {
            rows.add(new String[] {
                item.categoryProperty().get(),
                item.amountProperty().get(),
                item.countProperty().get(),
                item.avgProperty().get()
            });
        }
        
        System.out.println("DEBUG: Attempting to save to: " + filename + ".csv");
        System.out.println("DEBUG: Rows to write: " + rows.size());
        
        strategy.export(filename, headers, rows);
    }
}