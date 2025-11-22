package com.block20.controllers;

import com.block20.models.Member;
import com.block20.models.Transaction;
import com.block20.services.MemberService;

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

/**
 * Controller for Financial Reports functionality
 * Now connected to Real Backend Data via MemberService
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
    
    // Dependencies
    private Consumer<String> navigationHandler;
    private MemberService memberService; // <--- NEW: Backend Connection
    
    // UPDATED: Constructor accepts MemberService
    public FinancialReportsController(Consumer<String> navigationHandler, MemberService memberService) {
        this.navigationHandler = navigationHandler;
        this.memberService = memberService;
        
        initializeUI();
        // generateMockData(); // Removed: We use real data now
        updateReportDisplay();
    }
    
    private void initializeUI() {
        this.setFitToWidth(true);
        this.setFitToHeight(false);
        this.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
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
        
        HBox dateRangeBox = new HBox(15);
        dateRangeBox.setAlignment(Pos.CENTER_LEFT);
        
        VBox fromBox = new VBox(5);
        fromDatePicker = new DatePicker(fromDate);
        fromBox.getChildren().addAll(new Label("From Date:"), fromDatePicker);
        
        VBox toBox = new VBox(5);
        toDatePicker = new DatePicker(toDate);
        toBox.getChildren().addAll(new Label("To Date:"), toDatePicker);
        
        dateRangeBox.getChildren().addAll(fromBox, toBox);
        
        HBox groupByBox = new HBox(15);
        VBox groupByVBox = new VBox(5);
        groupByCombo = new ComboBox<>();
        groupByCombo.getItems().addAll("Daily", "Weekly", "Monthly", "Yearly");
        groupByCombo.setValue(groupBy);
        groupByCombo.setPrefWidth(200);
        groupByVBox.getChildren().addAll(new Label("Group By:"), groupByCombo);
        groupByBox.getChildren().add(groupByVBox);
        
        HBox actionButtons = new HBox(10);
        Button generateBtn = new Button("Generate Report");
        generateBtn.getStyleClass().add("primary-button");
        generateBtn.setOnAction(e -> generateReport());
        
        actionButtons.getChildren().add(generateBtn);
        
        section.getChildren().addAll(sectionTitle, dateRangeBox, groupByBox, actionButtons);
        return section;
    }
    
    private VBox createReportDisplayArea() {
        VBox displayArea = new VBox(20);
        displayArea.getStyleClass().add("report-display-area");
        displayArea.setPadding(new Insets(20));
        displayArea.setMinHeight(400);
        displayArea.getChildren().add(new Label("Select parameters and click Generate to view results."));
        return displayArea;
    }
    
    private void selectReportType(String reportType, Button selectedBtn, HBox buttonContainer) {
        currentReportType = reportType;
        buttonContainer.getChildren().forEach(node -> {
            if (node instanceof Button) node.getStyleClass().remove("selected");
        });
        selectedBtn.getStyleClass().add("selected");
    }
    
    private void generateReport() {
        fromDate = fromDatePicker.getValue();
        toDate = toDatePicker.getValue();
        groupBy = groupByCombo.getValue();
        
        if (fromDate.isAfter(toDate)) {
            showAlert("Invalid Date Range", "From date must be before To date");
            return;
        }
        updateReportDisplay();
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
    
    // --- REAL DATA LOGIC STARTS HERE ---

    private VBox createRevenueReport() {
        VBox content = new VBox(15);
        
        // 1. Fetch Real Transactions
        List<Transaction> allTxns = memberService.getAllTransactions();
        
        // 2. Filter by Date
        List<Transaction> filtered = allTxns.stream()
            .filter(t -> !t.getDate().isBefore(fromDate) && !t.getDate().isAfter(toDate))
            .collect(Collectors.toList());

        // 3. Calculate Totals
        double totalRevenue = filtered.stream().mapToDouble(Transaction::getAmount).sum();
        
        double enrollmentRevenue = filtered.stream()
            .filter(t -> t.getType().equals("Enrollment"))
            .mapToDouble(Transaction::getAmount).sum();
            
        double renewalRevenue = filtered.stream()
            .filter(t -> t.getType().equals("Renewal"))
            .mapToDouble(Transaction::getAmount).sum();

        int enrollCount = (int) filtered.stream().filter(t -> t.getType().equals("Enrollment")).count();
        int renewalCount = (int) filtered.stream().filter(t -> t.getType().equals("Renewal")).count();

        // 4. Display Cards
        HBox summaryCards = new HBox(15);
        summaryCards.getChildren().addAll(
            createMetricCard("Total Revenue", String.format("$%.2f", totalRevenue), "revenue-card"),
            createMetricCard("Enrollments", String.format("$%.2f", enrollmentRevenue), "positive-card"),
            createMetricCard("Renewals", String.format("$%.2f", renewalRevenue), "positive-card")
        );
        
        // 5. Create Table Data
        ObservableList<RevenueBreakdown> revenueData = FXCollections.observableArrayList();
        
        if (enrollCount > 0) {
            revenueData.add(new RevenueBreakdown("Enrollments", 
                String.format("$%.2f", enrollmentRevenue), 
                String.valueOf(enrollCount), 
                String.format("$%.2f", enrollmentRevenue/enrollCount)));
        }
        if (renewalCount > 0) {
            revenueData.add(new RevenueBreakdown("Renewals", 
                String.format("$%.2f", renewalRevenue), 
                String.valueOf(renewalCount), 
                String.format("$%.2f", renewalRevenue/renewalCount)));
        }
        
        // Table Setup
        TableView<RevenueBreakdown> table = new TableView<>();
        table.setPrefHeight(250);
        
        TableColumn<RevenueBreakdown, String> catCol = new TableColumn<>("Category"); catCol.setCellValueFactory(d -> d.getValue().categoryProperty()); catCol.setPrefWidth(200);
        TableColumn<RevenueBreakdown, String> amtCol = new TableColumn<>("Amount"); amtCol.setCellValueFactory(d -> d.getValue().amountProperty()); amtCol.setPrefWidth(150);
        TableColumn<RevenueBreakdown, String> cntCol = new TableColumn<>("Count"); cntCol.setCellValueFactory(d -> d.getValue().countProperty()); cntCol.setPrefWidth(100);
        TableColumn<RevenueBreakdown, String> avgCol = new TableColumn<>("Avg"); avgCol.setCellValueFactory(d -> d.getValue().avgProperty()); avgCol.setPrefWidth(150);
        
        table.getColumns().addAll(catCol, amtCol, cntCol, avgCol);
        table.setItems(revenueData);
        
        if (revenueData.isEmpty()) {
            table.setPlaceholder(new Label("No revenue data found for this period"));
        }
        
        content.getChildren().addAll(summaryCards, new Separator(), table);
        return content;
    }
    
    private VBox createMemberGrowthReport() {
        VBox content = new VBox(15);
        
        // 1. Fetch Real Members
        List<Member> allMembers = memberService.getAllMembers();
        
        // 2. Filter by Join Date
        long newMembers = allMembers.stream()
            .filter(m -> !m.getJoinDate().isBefore(fromDate) && !m.getJoinDate().isAfter(toDate))
            .count();
            
        long totalActive = allMembers.stream()
            .filter(m -> "Active".equalsIgnoreCase(m.getStatus()))
            .count();

        // 3. Display Cards
        HBox summaryCards = new HBox(15);
        summaryCards.getChildren().addAll(
            createMetricCard("New Members", String.valueOf(newMembers), "positive-card"),
            createMetricCard("Total Active", String.valueOf(totalActive), "revenue-card")
        );
        
        // 4. Simple Table showing new members
        TableView<Member> table = new TableView<>();
        table.setPrefHeight(250);
        
        TableColumn<Member, String> idCol = new TableColumn<>("ID"); idCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getMemberId()));
        TableColumn<Member, String> nameCol = new TableColumn<>("Name"); nameCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getFullName()));
        TableColumn<Member, String> dateCol = new TableColumn<>("Joined"); dateCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getJoinDate().toString()));
        
        table.getColumns().addAll(idCol, nameCol, dateCol);
        
        List<Member> filteredMembers = allMembers.stream()
            .filter(m -> !m.getJoinDate().isBefore(fromDate) && !m.getJoinDate().isAfter(toDate))
            .collect(Collectors.toList());
            
        table.setItems(FXCollections.observableArrayList(filteredMembers));
        
        content.getChildren().addAll(summaryCards, new Separator(), new Label("New Members Joined:"), table);
        return content;
    }
    
    // Mock Collections Report (Since we don't have a 'Debt' model yet)
    private VBox createCollectionsReport() {
        VBox content = new VBox(15);
        content.getChildren().add(new Label("Collections data requires 'Invoicing' feature (Coming Soon)"));
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
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Data model for Revenue Table
    public static class RevenueBreakdown {
        private final StringProperty category, amount, count, avg;
        public RevenueBreakdown(String c, String a, String ct, String av) {
            this.category = new SimpleStringProperty(c);
            this.amount = new SimpleStringProperty(a);
            this.count = new SimpleStringProperty(ct);
            this.avg = new SimpleStringProperty(av);
        }
        public StringProperty categoryProperty() { return category; }
        public StringProperty amountProperty() { return amount; }
        public StringProperty countProperty() { return count; }
        public StringProperty avgProperty() { return avg; }
    }
}