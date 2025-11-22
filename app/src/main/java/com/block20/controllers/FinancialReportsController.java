package com.block20.controllers;

import com.block20.models.Member;
import com.block20.models.Transaction;
import com.block20.services.MemberService;
import com.block20.services.ExportService;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.*;
import javafx.beans.property.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class FinancialReportsController extends ScrollPane {
    
    // Data structures
    private String currentReportType = "Revenue Report";
    private LocalDate fromDate = LocalDate.now().minusMonths(1);
    private LocalDate toDate = LocalDate.now();
    private String groupBy = "Daily";
    
    // THIS WAS THE MISSING FIELD causing Export issues
    private ObservableList<RevenueBreakdown> revenueData;

    // UI Components
    private VBox reportDisplayArea;
    private VBox contentContainer;
    private DatePicker fromDatePicker;
    private DatePicker toDatePicker;
    private ComboBox<String> groupByCombo;
    
    // Dependencies
    private Consumer<String> navigationHandler;
    private MemberService memberService; 
    private ExportService exportService;

    public FinancialReportsController(Consumer<String> navigationHandler, MemberService memberService, ExportService exportService) {
        this.navigationHandler = navigationHandler;
        this.memberService = memberService;
        this.exportService = exportService;
        
        initializeUI();
        // Auto-generate on load so screen isn't empty
        generateReport(); 
    }
    
    private void initializeUI() {
        this.setFitToWidth(true);
        this.setFitToHeight(false);
        this.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.getStyleClass().add("content-scroll-pane");
        
        contentContainer = new VBox(20);
        contentContainer.getStyleClass().add("reports-container");
        contentContainer.setPadding(new Insets(30));
        
        contentContainer.getChildren().addAll(
            createHeader(),
            createReportTypeSection(),
            createParametersSection(),
            (reportDisplayArea = createReportDisplayArea())
        );
        
        this.setContent(contentContainer);
    }
    
    private void generateReport() {
        System.out.println("DEBUG: Generate Report Triggered");
        fromDate = fromDatePicker.getValue();
        toDate = toDatePicker.getValue();
        groupBy = groupByCombo.getValue();
        
        if (fromDate.isAfter(toDate)) {
            showAlert("Invalid Date Range", "From date must be before To date");
            return;
        }
        
        try {
            updateReportDisplay();
        } catch (Exception e) {
            System.err.println("CRITICAL ERROR in generateReport:");
            e.printStackTrace();
            showAlert("Error", "Failed to generate report: " + e.getMessage());
        }
    }
    
    private void updateReportDisplay() {
        reportDisplayArea.getChildren().clear();
        
        HBox reportHeader = new HBox(20);
        reportHeader.setAlignment(Pos.CENTER_LEFT);
        reportHeader.setPadding(new Insets(0, 0, 20, 0));
        
        Label reportTitle = new Label(currentReportType.toUpperCase());
        reportTitle.getStyleClass().add("report-title");
        Label reportPeriod = new Label(fromDate.format(DateTimeFormatter.ofPattern("MMM dd")) + " - " + toDate.format(DateTimeFormatter.ofPattern("MMM dd")));
        reportPeriod.getStyleClass().add("report-period");
        
        reportHeader.getChildren().addAll(reportTitle, reportPeriod);
        
        VBox reportContent = new VBox(20);
        
        if (currentReportType.equals("Revenue Report")) {
            reportContent.getChildren().add(createRevenueReport());
        } else if (currentReportType.equals("Collections Report")) {
            reportContent.getChildren().add(createCollectionsReport());
        } else if (currentReportType.equals("Member Growth")) {
            reportContent.getChildren().add(createMemberGrowthReport());
        }
        
        reportDisplayArea.getChildren().addAll(reportHeader, reportContent);
    }
    
    private VBox createRevenueReport() {
        System.out.println("DEBUG: Creating Revenue Report...");
        VBox content = new VBox(15);
        
        if (memberService == null) {
            content.getChildren().add(new Label("Error: Backend Service Not Connected"));
            return content;
        }

        List<Transaction> allTxns = memberService.getAllTransactions();
        System.out.println("DEBUG: Found " + allTxns.size() + " transactions total.");

        List<Transaction> filtered = allTxns.stream()
            .filter(t -> !t.getDate().isBefore(fromDate) && !t.getDate().isAfter(toDate))
            .collect(Collectors.toList());
        
        System.out.println("DEBUG: " + filtered.size() + " transactions in date range.");

        // Calculations
        double totalRevenue = filtered.stream().mapToDouble(Transaction::getAmount).sum();
        double enrollmentRevenue = filtered.stream().filter(t -> "Enrollment".equals(t.getType())).mapToDouble(Transaction::getAmount).sum();
        double renewalRevenue = filtered.stream().filter(t -> "Renewal".equals(t.getType())).mapToDouble(Transaction::getAmount).sum();
        
        long enrollCount = filtered.stream().filter(t -> "Enrollment".equals(t.getType())).count();
        long renewalCount = filtered.stream().filter(t -> "Renewal".equals(t.getType())).count();

        // Cards
        HBox summaryCards = new HBox(15);
        summaryCards.getChildren().addAll(
            createMetricCard("Total Revenue", String.format("$%.2f", totalRevenue), "revenue-card"),
            createMetricCard("Enrollments", String.format("$%.2f", enrollmentRevenue), "positive-card"),
            createMetricCard("Renewals", String.format("$%.2f", renewalRevenue), "positive-card")
        );
        
        // Table Data
        this.revenueData = FXCollections.observableArrayList();
        
        if (enrollCount > 0) {
            revenueData.add(new RevenueBreakdown("Enrollments", 
                String.format("$%.2f", enrollmentRevenue), String.valueOf(enrollCount), String.format("$%.2f", enrollmentRevenue/enrollCount)));
        }
        if (renewalCount > 0) {
            revenueData.add(new RevenueBreakdown("Renewals", 
                String.format("$%.2f", renewalRevenue), String.valueOf(renewalCount), String.format("$%.2f", renewalRevenue/renewalCount)));
        }
        
        // Table View
        TableView<RevenueBreakdown> table = new TableView<>();
        table.setPrefHeight(300); // FIX: Ensure height is visible
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<RevenueBreakdown, String> catCol = new TableColumn<>("Category"); catCol.setCellValueFactory(d -> d.getValue().categoryProperty());
        TableColumn<RevenueBreakdown, String> amtCol = new TableColumn<>("Amount"); amtCol.setCellValueFactory(d -> d.getValue().amountProperty());
        TableColumn<RevenueBreakdown, String> cntCol = new TableColumn<>("Count"); cntCol.setCellValueFactory(d -> d.getValue().countProperty());
        TableColumn<RevenueBreakdown, String> avgCol = new TableColumn<>("Avg"); avgCol.setCellValueFactory(d -> d.getValue().avgProperty());
        
        table.getColumns().addAll(catCol, amtCol, cntCol, avgCol);
        table.setItems(revenueData);
        
        if (revenueData.isEmpty()) {
            table.setPlaceholder(new Label("No transactions found in this period."));
        }
        
        content.getChildren().addAll(summaryCards, new Separator(), table);
        return content;
    }
    
    private void exportReport(String format) {
        System.out.println("DEBUG: Export Button Clicked for " + format);
        
        if (!"CSV".equalsIgnoreCase(format)) {
            showAlert("Not Supported", "Only CSV export is currently implemented.");
            return;
        }
        
        if (exportService == null) {
            showAlert("Error", "Export Service is not connected!");
            return;
        }

        try {
            if (this.revenueData != null && !this.revenueData.isEmpty()) {
                exportService.exportRevenueReport(this.revenueData);
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Export Successful");
                alert.setHeaderText(null);
                alert.setContentText("File saved to your Desktop.");
                alert.showAndWait();
            } else {
                showAlert("Empty Data", "No data to export. Generate a report with data first.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Export Failed", e.getMessage());
        }
    }

    // --- STANDARD COMPONENTS (Header, Parameters, Etc) ---
    
    private VBox createHeader() {
        VBox header = new VBox(10);
        header.getStyleClass().add("section-header");
        header.getChildren().addAll(new Label("Financial Reports"), new Label("Generate and export financial data"));
        return header;
    }
    
    private VBox createReportTypeSection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(20));
        section.getStyleClass().add("report-type-section");
        HBox btns = new HBox(15); btns.setAlignment(Pos.CENTER_LEFT);
        
        Button revBtn = new Button("Revenue Report"); revBtn.getStyleClass().add("selected");
        revBtn.setOnAction(e -> { currentReportType="Revenue Report"; updateReportDisplay(); });
        
        Button colBtn = new Button("Collections Report");
        colBtn.setOnAction(e -> { currentReportType="Collections Report"; updateReportDisplay(); });
        
        Button growBtn = new Button("Member Growth");
        growBtn.setOnAction(e -> { currentReportType="Member Growth"; updateReportDisplay(); });
        
        btns.getChildren().addAll(revBtn, colBtn, growBtn);
        section.getChildren().addAll(new Label("FINANCIAL REPORTS"), btns);
        return section;
    }
    
    private VBox createParametersSection() {
        VBox section = new VBox(15);
        section.setPadding(new Insets(20));
        section.getStyleClass().add("parameters-section");
        
        HBox dates = new HBox(15); dates.setAlignment(Pos.CENTER_LEFT);
        fromDatePicker = new DatePicker(fromDate);
        toDatePicker = new DatePicker(toDate);
        dates.getChildren().addAll(new Label("From:"), fromDatePicker, new Label("To:"), toDatePicker);
        
        HBox group = new HBox(15);
        groupByCombo = new ComboBox<>(); groupByCombo.getItems().addAll("Daily", "Weekly", "Monthly"); groupByCombo.setValue("Daily");
        group.getChildren().addAll(new Label("Group By:"), groupByCombo);
        
        HBox actions = new HBox(10);
        Button genBtn = new Button("Generate Report");
        genBtn.getStyleClass().add("primary-button");
        genBtn.setOnAction(e -> generateReport());
        
        Button expBtn = new Button("Export CSV");
        expBtn.setOnAction(e -> exportReport("CSV"));
        
        actions.getChildren().addAll(genBtn, expBtn);
        section.getChildren().addAll(new Label("Parameters"), dates, group, actions);
        return section;
    }
    
    private VBox createReportDisplayArea() {
        VBox box = new VBox(20);
        box.setPadding(new Insets(20));
        box.setMinHeight(400);
        return box;
    }
    
    private VBox createMemberGrowthReport() { return new VBox(new Label("Member Growth Report")); }
    private VBox createCollectionsReport() { return new VBox(new Label("Collections Report")); }
    
    private VBox createMetricCard(String title, String value, String style) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");
        card.getChildren().addAll(new Label(title), new Label(value));
        return card;
    }
    
    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title); a.setContentText(msg); a.showAndWait();
    }
    
    public static class RevenueBreakdown {
        private final StringProperty category, amount, count, avg;
        public RevenueBreakdown(String c, String a, String co, String av) {
            this.category = new SimpleStringProperty(c); this.amount = new SimpleStringProperty(a);
            this.count = new SimpleStringProperty(co); this.avg = new SimpleStringProperty(av);
        }
        public StringProperty categoryProperty() { return category; }
        public StringProperty amountProperty() { return amount; }
        public StringProperty countProperty() { return count; }
        public StringProperty avgProperty() { return avg; }
    }
}