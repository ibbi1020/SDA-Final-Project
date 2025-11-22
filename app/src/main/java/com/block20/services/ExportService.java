package com.block20.services;

import com.block20.controllers.FinancialReportsController.RevenueBreakdown;
import java.util.List;

public interface ExportService {
    void exportRevenueReport(List<RevenueBreakdown> data);
}