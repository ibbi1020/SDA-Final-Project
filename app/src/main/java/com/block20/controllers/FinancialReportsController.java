package com.block20.controllers;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.*;
import javafx.beans.property.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;

/**
 * Controller for Financial Reports functionality
 * Implements FR5.1, FR5.2, FR5.3 user stories
 * Handles Revenue, Collections, and Member Growth reports
 */
public class FinancialReportsController extends ScrollPane {
    
    // Data structures
    private String currentReportType = "Revenue Report";
    private LocalDate fromDate = LocalDate.now().minusMonths(1);
    private LocalDate toDate = LocalDate.now();
    private String groupBy = "Daily";
    
    // UI Components
    private VBox reportDisplayArea;
    private VBox contentContainer;
    private DatePicker fromDatePicker;
    private DatePicker toDatePicker;
    private ComboBox<String> groupByCombo;
    
    // Navigation
    private Consumer<String> navigationHandler;
    
    public FinancialReportsController(Consumer<String> navigationHandler) {
        this.navigationHandler = navigationHandler;
        
        initializeUI();
        generateMockData();
        updateReportDisplay();
    }
    
    private void initializeUI() {
        // Configure ScrollPane
        this.setFitToWidth(true);
        this.setFitToHeight(false);
        this.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.getStyleClass().add("content-scroll-pane");
        
        // Create main content container
        contentContainer = new VBox(20);
        contentContainer.getStyleClass().add("reports-container");
        contentContainer.setPadding(new Insets(30));
        
        // Header
        VBox header = createHeader();
        
        // Report Type Selection
        VBox reportTypeSection = createReportTypeSection();
        
        // Parameters Section
        VBox parametersSection = createParametersSection();
        
        // Report Display Area
        reportDisplayArea = createReportDisplayArea();
        
        contentContainer.getChildren().addAll(header, reportTypeSection, parametersSection, reportDisplayArea);
        
        // Set content to ScrollPane
        this.setContent(contentContainer);
    }
    
    private VBox createHeader() {
        VBox header = new VBox(10);
        header.getStyleClass().add("section-header");
        
        Label title = new Label("Financial Reports");
        title.getStyleClass().add("page-title");
        
        Label subtitle = new Label("Generate and export comprehensive financial reports for analysis");
        subtitle.getStyleClass().add("page-subtitle");
        
        header.getChildren().addAll(title, subtitle);
        return header;
    }
    
    private VBox createReportTypeSection() {
        VBox section = new VBox(10);
        section.getStyleClass().add("report-type-section");
        section.setPadding(new Insets(20));
        
        Label sectionTitle = new Label("📊 FINANCIAL REPORTS");
        sectionTitle.getStyleClass().add("section-title");
        
        HBox reportButtons = new HBox(15);
        reportButtons.setAlignment(Pos.CENTER_LEFT);
        
        Button revenueBtn = new Button("Revenue Report");
        revenueBtn.getStyleClass().addAll("report-type-button", "selected");
        revenueBtn.setOnAction(e -> selectReportType("Revenue Report", revenueBtn, reportButtons));
        
        Button collectionsBtn = new Button("Collections Report");
        collectionsBtn.getStyleClass().add("report-type-button");
        collectionsBtn.setOnAction(e -> selectReportType("Collections Report", collectionsBtn, reportButtons));
        
        Button memberGrowthBtn = new Button("Member Growth");
        memberGrowthBtn.getStyleClass().add("report-type-button");
        memberGrowthBtn.setOnAction(e -> selectReportType("Member Growth", memberGrowthBtn, reportButtons));
        
        reportButtons.getChildren().addAll(revenueBtn, collectionsBtn, memberGrowthBtn);
        
        section.getChildren().addAll(sectionTitle, reportButtons);
        return section;
    }
    
    private VBox createParametersSection() {
        VBox section = new VBox(15);
        section.getStyleClass().add("parameters-section");
        section.setPadding(new Insets(20));
        
        Label sectionTitle = new Label("Report Parameters");
        sectionTitle.getStyleClass().add("section-subtitle");
        
        // Date Range
        HBox dateRangeBox = new HBox(15);
        dateRangeBox.setAlignment(Pos.CENTER_LEFT);
        
        VBox fromBox = new VBox(5);
        Label fromLabel = new Label("From Date:");
        fromDatePicker = new DatePicker(fromDate);
        fromDatePicker.setPrefWidth(200);
        fromBox.getChildren().addAll(fromLabel, fromDatePicker);
        
        VBox toBox = new VBox(5);
        Label toLabel = new Label("To Date:");
        toDatePicker = new DatePicker(toDate);
        toDatePicker.setPrefWidth(200);
        toBox.getChildren().addAll(toLabel, toDatePicker);
        
        dateRangeBox.getChildren().addAll(fromBox, toBox);
        
        // Group By
        HBox groupByBox = new HBox(15);
        groupByBox.setAlignment(Pos.CENTER_LEFT);
        
        VBox groupByVBox = new VBox(5);
        Label groupByLabel = new Label("Group By:");
        groupByCombo = new ComboBox<>();
        groupByCombo.getItems().addAll("Daily", "Weekly", "Monthly", "Yearly");
        groupByCombo.setValue(groupBy);
        groupByCombo.setPrefWidth(200);
        groupByVBox.getChildren().addAll(groupByLabel, groupByCombo);
        
        groupByBox.getChildren().add(groupByVBox);
        
        // Action Buttons
        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER_LEFT);
        actionButtons.setPadding(new Insets(10, 0, 0, 0));
        
        Button generateBtn = new Button("Generate Report");
        generateBtn.getStyleClass().add("primary-button");
        generateBtn.setOnAction(e -> generateReport());
        
        Button exportPdfBtn = new Button("Export PDF");
        exportPdfBtn.getStyleClass().add("secondary-button");
        exportPdfBtn.setOnAction(e -> exportReport("PDF"));
        
        Button exportExcelBtn = new Button("Export Excel");
        exportExcelBtn.getStyleClass().add("secondary-button");
        exportExcelBtn.setOnAction(e -> exportReport("Excel"));
        
        Button exportCsvBtn = new Button("Export CSV");
        exportCsvBtn.getStyleClass().add("secondary-button");
        exportCsvBtn.setOnAction(e -> exportReport("CSV"));
        
        actionButtons.getChildren().addAll(generateBtn, exportPdfBtn, exportExcelBtn, exportCsvBtn);
        
        section.getChildren().addAll(sectionTitle, dateRangeBox, groupByBox, actionButtons);
        return section;
    }
    
    private VBox createReportDisplayArea() {
        VBox displayArea = new VBox(20);
        displayArea.getStyleClass().add("report-display-area");
        displayArea.setPadding(new Insets(20));
        displayArea.setMinHeight(400);
        
        // This will be populated when a report is generated
        Label placeholder = new Label("Select report parameters and click 'Generate Report' to view results");
        placeholder.getStyleClass().add("placeholder-text");
        displayArea.getChildren().add(placeholder);
        
        return displayArea;
    }
    
    private void selectReportType(String reportType, Button selectedBtn, HBox buttonContainer) {
        currentReportType = reportType;
        
        // Update button styles
        buttonContainer.getChildren().forEach(node -> {
            if (node instanceof Button) {
                node.getStyleClass().remove("selected");
            }
        });
        selectedBtn.getStyleClass().add("selected");
        
        System.out.println("Selected report type: " + reportType);
    }
    
    private void generateReport() {
        fromDate = fromDatePicker.getValue();
        toDate = toDatePicker.getValue();
        groupBy = groupByCombo.getValue();
        
        if (fromDate.isAfter(toDate)) {
            showAlert("Invalid Date Range", "From date must be before To date");
            return;
        }
        
        System.out.println("Generating " + currentReportType + " from " + fromDate + " to " + toDate + " grouped by " + groupBy);
        
        updateReportDisplay();
    }
    
    private void updateReportDisplay() {
        reportDisplayArea.getChildren().clear();
        
        // Report Header
        HBox reportHeader = new HBox(20);
        reportHeader.setAlignment(Pos.CENTER_LEFT);
        reportHeader.setPadding(new Insets(0, 0, 20, 0));
        
        Label reportTitle = new Label(currentReportType.toUpperCase());
        reportTitle.getStyleClass().add("report-title");
        
        Label reportPeriod = new Label(fromDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) + 
                                      " - " + 
                                      toDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        reportPeriod.getStyleClass().add("report-period");
        
        reportHeader.getChildren().addAll(reportTitle, reportPeriod);
        
        // Report Content
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
        VBox content = new VBox(15);
        
        // Summary Cards
        HBox summaryCards = new HBox(15);
        summaryCards.setAlignment(Pos.CENTER_LEFT);
        
        VBox totalRevenueCard = createMetricCard("Total Revenue", "$28,450", "revenue-card");
        VBox membershipFeesCard = createMetricCard("Membership Fees", "$25,100", "positive-card");
        VBox trainingSessionsCard = createMetricCard("Training Sessions", "$3,350", "positive-card");
        
        summaryCards.getChildren().addAll(totalRevenueCard, membershipFeesCard, trainingSessionsCard);
        
        // Breakdown Table
        TableView<RevenueBreakdown> table = new TableView<>();
        table.setPrefHeight(250);
        
        TableColumn<RevenueBreakdown, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(data -> data.getValue().categoryProperty());
        categoryCol.setPrefWidth(200);
        
        TableColumn<RevenueBreakdown, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(data -> data.getValue().amountProperty());
        amountCol.setPrefWidth(150);
        
        TableColumn<RevenueBreakdown, String> countCol = new TableColumn<>("Count");
        countCol.setCellValueFactory(data -> data.getValue().countProperty());
        countCol.setPrefWidth(100);
        
        TableColumn<RevenueBreakdown, String> avgCol = new TableColumn<>("Avg/Transaction");
        avgCol.setCellValueFactory(data -> data.getValue().avgProperty());
        avgCol.setPrefWidth(150);
        
        table.getColumns().addAll(categoryCol, amountCol, countCol, avgCol);
        
        ObservableList<RevenueBreakdown> revenueData = FXCollections.observableArrayList(
            new RevenueBreakdown("New Memberships", "$8,640", "24", "$360"),
            new RevenueBreakdown("Renewals", "$16,460", "47", "$350"),
            new RevenueBreakdown("Personal Training", "$2,150", "43", "$50"),
            new RevenueBreakdown("Group Classes", "$1,200", "80", "$15"),
            new RevenueBreakdown("Late Fees", "$400", "20", "$20"),
            new RevenueBreakdown("Other", "$150", "8", "$18.75")
        );
        
        table.setItems(revenueData);
        
        content.getChildren().addAll(summaryCards, new Separator(), table);
        return content;
    }
    
    private VBox createCollectionsReport() {
        VBox content = new VBox(15);
        
        // Summary Cards
        HBox summaryCards = new HBox(15);
        summaryCards.setAlignment(Pos.CENTER_LEFT);
        
        VBox totalCollectedCard = createMetricCard("Total Collected", "$26,890", "positive-card");
        VBox outstandingCard = createMetricCard("Outstanding", "$4,120", "warning-card");
        VBox overdueCard = createMetricCard("Overdue", "$1,560", "negative-card");
        VBox collectionRateCard = createMetricCard("Collection Rate", "86.7%", "revenue-card");
        
        summaryCards.getChildren().addAll(totalCollectedCard, outstandingCard, overdueCard, collectionRateCard);
        
        // Outstanding Breakdown
        TableView<CollectionBreakdown> table = new TableView<>();
        table.setPrefHeight(250);
        
        TableColumn<CollectionBreakdown, String> memberCol = new TableColumn<>("Member ID");
        memberCol.setCellValueFactory(data -> data.getValue().memberIdProperty());
        memberCol.setPrefWidth(120);
        
        TableColumn<CollectionBreakdown, String> nameCol = new TableColumn<>("Member Name");
        nameCol.setCellValueFactory(data -> data.getValue().nameProperty());
        nameCol.setPrefWidth(180);
        
        TableColumn<CollectionBreakdown, String> amountCol = new TableColumn<>("Amount Due");
        amountCol.setCellValueFactory(data -> data.getValue().amountDueProperty());
        amountCol.setPrefWidth(120);
        
        TableColumn<CollectionBreakdown, String> dueDateCol = new TableColumn<>("Due Date");
        dueDateCol.setCellValueFactory(data -> data.getValue().dueDateProperty());
        dueDateCol.setPrefWidth(120);
        
        TableColumn<CollectionBreakdown, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> data.getValue().statusProperty());
        statusCol.setPrefWidth(100);
        statusCol.setCellFactory(column -> new TableCell<CollectionBreakdown, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    if (status.equals("Overdue")) {
                        setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #ffc107; -fx-font-weight: bold;");
                    }
                }
            }
        });
        
        table.getColumns().addAll(memberCol, nameCol, amountCol, dueDateCol, statusCol);
        
        ObservableList<CollectionBreakdown> collectionsData = FXCollections.observableArrayList(
            new CollectionBreakdown("M1023", "Robert Brown", "$380", "Nov 25, 2024", "Overdue"),
            new CollectionBreakdown("M1045", "Jennifer Davis", "$360", "Dec 01, 2024", "Overdue"),
            new CollectionBreakdown("M1012", "Michael Wilson", "$340", "Dec 05, 2024", "Due Soon"),
            new CollectionBreakdown("M1067", "Patricia Moore", "$320", "Dec 08, 2024", "Due Soon"),
            new CollectionBreakdown("M1089", "Christopher Taylor", "$300", "Dec 10, 2024", "Due Soon")
        );
        
        table.setItems(collectionsData);
        
        Label tableTitle = new Label("Outstanding Accounts");
        tableTitle.getStyleClass().add("section-subtitle");
        
        content.getChildren().addAll(summaryCards, new Separator(), tableTitle, table);
        return content;
    }
    
    private VBox createMemberGrowthReport() {
        VBox content = new VBox(15);
        
        // Summary Cards
        HBox summaryCards = new HBox(15);
        summaryCards.setAlignment(Pos.CENTER_LEFT);
        
        VBox newMembersCard = createMetricCard("New Members", "24", "positive-card");
        VBox renewalsCard = createMetricCard("Renewals", "47", "positive-card");
        VBox cancellationsCard = createMetricCard("Cancellations", "8", "negative-card");
        VBox netGrowthCard = createMetricCard("Net Growth", "+16", "revenue-card");
        VBox retentionRateCard = createMetricCard("Retention Rate", "85.5%", "positive-card");
        
        summaryCards.getChildren().addAll(newMembersCard, renewalsCard, cancellationsCard, netGrowthCard, retentionRateCard);
        
        // Growth Trend Table
        TableView<GrowthTrend> table = new TableView<>();
        table.setPrefHeight(250);
        
        TableColumn<GrowthTrend, String> periodCol = new TableColumn<>("Period");
        periodCol.setCellValueFactory(data -> data.getValue().periodProperty());
        periodCol.setPrefWidth(150);
        
        TableColumn<GrowthTrend, String> newCol = new TableColumn<>("New Members");
        newCol.setCellValueFactory(data -> data.getValue().newMembersProperty());
        newCol.setPrefWidth(130);
        
        TableColumn<GrowthTrend, String> renewalCol = new TableColumn<>("Renewals");
        renewalCol.setCellValueFactory(data -> data.getValue().renewalsProperty());
        renewalCol.setPrefWidth(120);
        
        TableColumn<GrowthTrend, String> cancelCol = new TableColumn<>("Cancellations");
        cancelCol.setCellValueFactory(data -> data.getValue().cancellationsProperty());
        cancelCol.setPrefWidth(130);
        
        TableColumn<GrowthTrend, String> totalCol = new TableColumn<>("Total Members");
        totalCol.setCellValueFactory(data -> data.getValue().totalMembersProperty());
        totalCol.setPrefWidth(130);
        
        table.getColumns().addAll(periodCol, newCol, renewalCol, cancelCol, totalCol);
        
        ObservableList<GrowthTrend> growthData = FXCollections.observableArrayList(
            new GrowthTrend("Week 1 (Nov 1-7)", "5", "12", "2", "315"),
            new GrowthTrend("Week 2 (Nov 8-14)", "7", "11", "1", "332"),
            new GrowthTrend("Week 3 (Nov 15-21)", "6", "13", "3", "348"),
            new GrowthTrend("Week 4 (Nov 22-28)", "6", "11", "2", "363")
        );
        
        table.setItems(growthData);
        
        Label tableTitle = new Label("Growth Trend (Grouped by " + groupBy + ")");
        tableTitle.getStyleClass().add("section-subtitle");
        
        content.getChildren().addAll(summaryCards, new Separator(), tableTitle, table);
        return content;
    }
    
    private VBox createMetricCard(String title, String value, String styleClass) {
        VBox card = new VBox(5);
        card.getStyleClass().addAll("metric-card", styleClass);
        card.setPadding(new Insets(15));
        card.setPrefWidth(180);
        
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("metric-title");
        
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("metric-value");
        
        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }
    
    private void exportReport(String format) {
        System.out.println("Exporting " + currentReportType + " to " + format + " format");
        System.out.println("Report period: " + fromDate + " to " + toDate);
        System.out.println("Group by: " + groupBy);
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Export Successful");
        alert.setHeaderText(null);
        alert.setContentText("Report exported successfully to " + format + " format.\n" +
                            "File: " + currentReportType.replace(" ", "_") + "_" + 
                            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "." + 
                            format.toLowerCase());
        alert.showAndWait();
    }
    
    private void generateMockData() {
        // Mock data is generated dynamically when reports are created
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Data models for report tables
    public static class ReportData {
        private final StringProperty category;
        private final StringProperty value;
        
        public ReportData(String category, String value) {
            this.category = new SimpleStringProperty(category);
            this.value = new SimpleStringProperty(value);
        }
        
        public StringProperty categoryProperty() { return category; }
        public StringProperty valueProperty() { return value; }
    }
    
    public static class RevenueBreakdown {
        private final StringProperty category;
        private final StringProperty amount;
        private final StringProperty count;
        private final StringProperty avg;
        
        public RevenueBreakdown(String category, String amount, String count, String avg) {
            this.category = new SimpleStringProperty(category);
            this.amount = new SimpleStringProperty(amount);
            this.count = new SimpleStringProperty(count);
            this.avg = new SimpleStringProperty(avg);
        }
        
        public StringProperty categoryProperty() { return category; }
        public StringProperty amountProperty() { return amount; }
        public StringProperty countProperty() { return count; }
        public StringProperty avgProperty() { return avg; }
    }
    
    public static class CollectionBreakdown {
        private final StringProperty memberId;
        private final StringProperty name;
        private final StringProperty amountDue;
        private final StringProperty dueDate;
        private final StringProperty status;
        
        public CollectionBreakdown(String memberId, String name, String amountDue, String dueDate, String status) {
            this.memberId = new SimpleStringProperty(memberId);
            this.name = new SimpleStringProperty(name);
            this.amountDue = new SimpleStringProperty(amountDue);
            this.dueDate = new SimpleStringProperty(dueDate);
            this.status = new SimpleStringProperty(status);
        }
        
        public StringProperty memberIdProperty() { return memberId; }
        public StringProperty nameProperty() { return name; }
        public StringProperty amountDueProperty() { return amountDue; }
        public StringProperty dueDateProperty() { return dueDate; }
        public StringProperty statusProperty() { return status; }
    }
    
    public static class GrowthTrend {
        private final StringProperty period;
        private final StringProperty newMembers;
        private final StringProperty renewals;
        private final StringProperty cancellations;
        private final StringProperty totalMembers;
        
        public GrowthTrend(String period, String newMembers, String renewals, String cancellations, String totalMembers) {
            this.period = new SimpleStringProperty(period);
            this.newMembers = new SimpleStringProperty(newMembers);
            this.renewals = new SimpleStringProperty(renewals);
            this.cancellations = new SimpleStringProperty(cancellations);
            this.totalMembers = new SimpleStringProperty(totalMembers);
        }
        
        public StringProperty periodProperty() { return period; }
        public StringProperty newMembersProperty() { return newMembers; }
        public StringProperty renewalsProperty() { return renewals; }
        public StringProperty cancellationsProperty() { return cancellations; }
        public StringProperty totalMembersProperty() { return totalMembers; }
    }
}
