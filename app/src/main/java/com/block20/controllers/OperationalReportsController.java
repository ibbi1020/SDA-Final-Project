package com.block20.controllers;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.*;
import javafx.beans.property.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

/**
 * Controller for Operational Reports functionality
 * Implements OM7.2 user story
 * Handles Daily, Attendance, and Session reports
 */
public class OperationalReportsController extends ScrollPane {
    
    // Data structures
    private String currentReportType = "Daily Report";
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
    
    public OperationalReportsController(Consumer<String> navigationHandler) {
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
        
        Label title = new Label("Operational Reports");
        title.getStyleClass().add("page-title");
        
        Label subtitle = new Label("Monitor daily operations, attendance trends, and training session analytics");
        subtitle.getStyleClass().add("page-subtitle");
        
        header.getChildren().addAll(title, subtitle);
        return header;
    }
    
    private VBox createReportTypeSection() {
        VBox section = new VBox(10);
        section.getStyleClass().add("report-type-section");
        section.setPadding(new Insets(20));
        
        Label sectionTitle = new Label("📋 OPERATIONAL REPORTS");
        sectionTitle.getStyleClass().add("section-title");
        
        HBox reportButtons = new HBox(15);
        reportButtons.setAlignment(Pos.CENTER_LEFT);
        
        Button dailyBtn = new Button("Daily Report");
        dailyBtn.getStyleClass().addAll("report-type-button", "selected");
        dailyBtn.setOnAction(e -> selectReportType("Daily Report", dailyBtn, reportButtons));
        
        Button attendanceBtn = new Button("Attendance Report");
        attendanceBtn.getStyleClass().add("report-type-button");
        attendanceBtn.setOnAction(e -> selectReportType("Attendance Report", attendanceBtn, reportButtons));
        
        Button sessionBtn = new Button("Session Report");
        sessionBtn.getStyleClass().add("report-type-button");
        sessionBtn.setOnAction(e -> selectReportType("Session Report", sessionBtn, reportButtons));
        
        reportButtons.getChildren().addAll(dailyBtn, attendanceBtn, sessionBtn);
        
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
        groupByCombo.getItems().addAll("Daily", "Weekly", "Monthly");
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
        
        if (currentReportType.equals("Daily Report")) {
            reportContent.getChildren().add(createDailyReport());
        } else if (currentReportType.equals("Attendance Report")) {
            reportContent.getChildren().add(createAttendanceReport());
        } else if (currentReportType.equals("Session Report")) {
            reportContent.getChildren().add(createSessionReport());
        }
        
        reportDisplayArea.getChildren().addAll(reportHeader, reportContent);
    }
    
    private VBox createDailyReport() {
        VBox content = new VBox(15);
        
        // Summary Cards
        HBox summaryCards = new HBox(15);
        summaryCards.setAlignment(Pos.CENTER_LEFT);
        
        VBox totalVisitsCard = createMetricCard("Total Visits", "1,847", "positive-card");
        VBox avgSessionDurationCard = createMetricCard("Avg Session Duration", "67 min", "revenue-card");
        VBox peakHourCard = createMetricCard("Peak Hour", "6-7 PM", "warning-card");
        VBox staffCoverageCard = createMetricCard("Staff Coverage", "94%", "positive-card");
        
        summaryCards.getChildren().addAll(totalVisitsCard, avgSessionDurationCard, peakHourCard, staffCoverageCard);
        
        // Daily Operations Table
        TableView<DailyOperations> table = new TableView<>();
        table.setPrefHeight(300);
        
        TableColumn<DailyOperations, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(data -> data.getValue().dateProperty());
        dateCol.setPrefWidth(120);
        
        TableColumn<DailyOperations, String> visitsCol = new TableColumn<>("Visits");
        visitsCol.setCellValueFactory(data -> data.getValue().visitsProperty());
        visitsCol.setPrefWidth(100);
        
        TableColumn<DailyOperations, String> peakHourCol = new TableColumn<>("Peak Hour");
        peakHourCol.setCellValueFactory(data -> data.getValue().peakHourProperty());
        peakHourCol.setPrefWidth(120);
        
        TableColumn<DailyOperations, String> avgDurationCol = new TableColumn<>("Avg Duration");
        avgDurationCol.setCellValueFactory(data -> data.getValue().avgDurationProperty());
        avgDurationCol.setPrefWidth(130);
        
        TableColumn<DailyOperations, String> equipmentUtilCol = new TableColumn<>("Equipment Util");
        equipmentUtilCol.setCellValueFactory(data -> data.getValue().equipmentUtilProperty());
        equipmentUtilCol.setPrefWidth(140);
        
        TableColumn<DailyOperations, String> staffCoverageCol = new TableColumn<>("Staff Coverage");
        staffCoverageCol.setCellValueFactory(data -> data.getValue().staffCoverageProperty());
        staffCoverageCol.setPrefWidth(130);
        
        table.getColumns().addAll(dateCol, visitsCol, peakHourCol, avgDurationCol, equipmentUtilCol, staffCoverageCol);
        
        ObservableList<DailyOperations> dailyData = FXCollections.observableArrayList(
            new DailyOperations("Nov 28, 2024", "287", "6-7 PM", "71 min", "82%", "95%"),
            new DailyOperations("Nov 27, 2024", "251", "7-8 PM", "68 min", "78%", "92%"),
            new DailyOperations("Nov 26, 2024", "268", "6-7 PM", "65 min", "80%", "94%"),
            new DailyOperations("Nov 25, 2024", "293", "5-6 PM", "69 min", "85%", "96%"),
            new DailyOperations("Nov 24, 2024", "178", "10-11 AM", "58 min", "65%", "88%"),
            new DailyOperations("Nov 23, 2024", "195", "9-10 AM", "62 min", "68%", "90%"),
            new DailyOperations("Nov 22, 2024", "275", "6-7 PM", "70 min", "81%", "95%")
        );
        
        table.setItems(dailyData);
        
        Label tableTitle = new Label("Daily Operations Summary");
        tableTitle.getStyleClass().add("section-subtitle");
        
        // Equipment Utilization by Zone
        Label zoneTitle = new Label("Equipment Utilization by Zone");
        zoneTitle.getStyleClass().add("section-subtitle");
        zoneTitle.setPadding(new Insets(20, 0, 10, 0));
        
        HBox zoneCards = new HBox(15);
        zoneCards.setAlignment(Pos.CENTER_LEFT);
        
        VBox cardioCard = createMetricCard("Cardio Zone", "88%", "positive-card");
        VBox strengthCard = createMetricCard("Strength Zone", "76%", "revenue-card");
        VBox freeWeightsCard = createMetricCard("Free Weights", "82%", "positive-card");
        VBox functionalCard = createMetricCard("Functional Training", "71%", "revenue-card");
        
        zoneCards.getChildren().addAll(cardioCard, strengthCard, freeWeightsCard, functionalCard);
        
        content.getChildren().addAll(summaryCards, new Separator(), tableTitle, table, zoneTitle, zoneCards);
        return content;
    }
    
    private VBox createAttendanceReport() {
        VBox content = new VBox(15);
        
        // Summary Cards
        HBox summaryCards = new HBox(15);
        summaryCards.setAlignment(Pos.CENTER_LEFT);
        
        VBox totalAttendanceCard = createMetricCard("Total Attendance", "1,847", "positive-card");
        VBox avgDailyCard = createMetricCard("Avg Daily", "264", "revenue-card");
        VBox peakDayCard = createMetricCard("Peak Day", "Monday", "warning-card");
        VBox utilizationCard = createMetricCard("Capacity Utilization", "74%", "positive-card");
        
        summaryCards.getChildren().addAll(totalAttendanceCard, avgDailyCard, peakDayCard, utilizationCard);
        
        // Attendance by Time Slot
        TableView<AttendanceByTime> table = new TableView<>();
        table.setPrefHeight(300);
        
        TableColumn<AttendanceByTime, String> timeSlotCol = new TableColumn<>("Time Slot");
        timeSlotCol.setCellValueFactory(data -> data.getValue().timeSlotProperty());
        timeSlotCol.setPrefWidth(150);
        
        TableColumn<AttendanceByTime, String> mondayCol = new TableColumn<>("Mon");
        mondayCol.setCellValueFactory(data -> data.getValue().mondayProperty());
        mondayCol.setPrefWidth(80);
        
        TableColumn<AttendanceByTime, String> tuesdayCol = new TableColumn<>("Tue");
        tuesdayCol.setCellValueFactory(data -> data.getValue().tuesdayProperty());
        tuesdayCol.setPrefWidth(80);
        
        TableColumn<AttendanceByTime, String> wednesdayCol = new TableColumn<>("Wed");
        wednesdayCol.setCellValueFactory(data -> data.getValue().wednesdayProperty());
        wednesdayCol.setPrefWidth(80);
        
        TableColumn<AttendanceByTime, String> thursdayCol = new TableColumn<>("Thu");
        thursdayCol.setCellValueFactory(data -> data.getValue().thursdayProperty());
        thursdayCol.setPrefWidth(80);
        
        TableColumn<AttendanceByTime, String> fridayCol = new TableColumn<>("Fri");
        fridayCol.setCellValueFactory(data -> data.getValue().fridayProperty());
        fridayCol.setPrefWidth(80);
        
        TableColumn<AttendanceByTime, String> saturdayCol = new TableColumn<>("Sat");
        saturdayCol.setCellValueFactory(data -> data.getValue().saturdayProperty());
        saturdayCol.setPrefWidth(80);
        
        TableColumn<AttendanceByTime, String> sundayCol = new TableColumn<>("Sun");
        sundayCol.setCellValueFactory(data -> data.getValue().sundayProperty());
        sundayCol.setPrefWidth(80);
        
        table.getColumns().addAll(timeSlotCol, mondayCol, tuesdayCol, wednesdayCol, thursdayCol, 
                                  fridayCol, saturdayCol, sundayCol);
        
        ObservableList<AttendanceByTime> attendanceData = FXCollections.observableArrayList(
            new AttendanceByTime("6-8 AM", "45", "42", "48", "43", "47", "38", "32"),
            new AttendanceByTime("8-10 AM", "52", "48", "51", "49", "53", "58", "62"),
            new AttendanceByTime("10-12 PM", "38", "35", "37", "36", "39", "51", "48"),
            new AttendanceByTime("12-2 PM", "42", "40", "43", "41", "44", "45", "42"),
            new AttendanceByTime("2-4 PM", "35", "33", "36", "34", "37", "48", "52"),
            new AttendanceByTime("4-6 PM", "58", "55", "59", "57", "61", "54", "47"),
            new AttendanceByTime("6-8 PM", "72", "68", "71", "69", "73", "62", "55"),
            new AttendanceByTime("8-10 PM", "48", "45", "47", "46", "49", "38", "32")
        );
        
        table.setItems(attendanceData);
        
        Label tableTitle = new Label("Attendance Heatmap by Time Slot");
        tableTitle.getStyleClass().add("section-subtitle");
        
        content.getChildren().addAll(summaryCards, new Separator(), tableTitle, table);
        return content;
    }
    
    private VBox createSessionReport() {
        VBox content = new VBox(15);
        
        // Summary Cards
        HBox summaryCards = new HBox(15);
        summaryCards.setAlignment(Pos.CENTER_LEFT);
        
        VBox totalSessionsCard = createMetricCard("Total Sessions", "142", "positive-card");
        VBox completionRateCard = createMetricCard("Completion Rate", "94%", "positive-card");
        VBox avgDurationCard = createMetricCard("Avg Duration", "52 min", "revenue-card");
        VBox revenueCard = createMetricCard("Revenue", "$7,100", "revenue-card");
        
        summaryCards.getChildren().addAll(totalSessionsCard, completionRateCard, avgDurationCard, revenueCard);
        
        // Sessions by Trainer
        TableView<SessionByTrainer> table = new TableView<>();
        table.setPrefHeight(250);
        
        TableColumn<SessionByTrainer, String> trainerCol = new TableColumn<>("Trainer");
        trainerCol.setCellValueFactory(data -> data.getValue().trainerProperty());
        trainerCol.setPrefWidth(180);
        
        TableColumn<SessionByTrainer, String> specializationCol = new TableColumn<>("Specialization");
        specializationCol.setCellValueFactory(data -> data.getValue().specializationProperty());
        specializationCol.setPrefWidth(150);
        
        TableColumn<SessionByTrainer, String> sessionsCol = new TableColumn<>("Sessions");
        sessionsCol.setCellValueFactory(data -> data.getValue().sessionsProperty());
        sessionsCol.setPrefWidth(100);
        
        TableColumn<SessionByTrainer, String> completedCol = new TableColumn<>("Completed");
        completedCol.setCellValueFactory(data -> data.getValue().completedProperty());
        completedCol.setPrefWidth(110);
        
        TableColumn<SessionByTrainer, String> cancelledCol = new TableColumn<>("Cancelled");
        cancelledCol.setCellValueFactory(data -> data.getValue().cancelledProperty());
        cancelledCol.setPrefWidth(110);
        
        TableColumn<SessionByTrainer, String> revenueColTrainer = new TableColumn<>("Revenue");
        revenueColTrainer.setCellValueFactory(data -> data.getValue().revenueProperty());
        revenueColTrainer.setPrefWidth(100);
        
        table.getColumns().addAll(trainerCol, specializationCol, sessionsCol, completedCol, 
                                  cancelledCol, revenueColTrainer);
        
        ObservableList<SessionByTrainer> sessionData = FXCollections.observableArrayList(
            new SessionByTrainer("Mike Johnson", "Personal Training", "28", "27", "1", "$1,400"),
            new SessionByTrainer("Sarah Williams", "Yoga", "24", "23", "1", "$1,200"),
            new SessionByTrainer("David Chen", "CrossFit", "22", "21", "1", "$1,100"),
            new SessionByTrainer("Emily Rodriguez", "Pilates", "20", "19", "1", "$1,000"),
            new SessionByTrainer("James Anderson", "Strength", "18", "17", "1", "$900"),
            new SessionByTrainer("Robert Taylor", "Boxing", "16", "15", "1", "$800"),
            new SessionByTrainer("Jessica Lee", "Swimming", "14", "12", "2", "$700")
        );
        
        table.setItems(sessionData);
        
        Label tableTitle = new Label("Sessions by Trainer");
        tableTitle.getStyleClass().add("section-subtitle");
        
        // Session Types Breakdown
        Label typesTitle = new Label("Popular Session Types");
        typesTitle.getStyleClass().add("section-subtitle");
        typesTitle.setPadding(new Insets(20, 0, 10, 0));
        
        HBox typeCards = new HBox(15);
        typeCards.setAlignment(Pos.CENTER_LEFT);
        
        VBox personalCard = createMetricCard("Personal Training", "28 sessions", "positive-card");
        VBox yogaCard = createMetricCard("Yoga", "24 sessions", "positive-card");
        VBox crossfitCard = createMetricCard("CrossFit", "22 sessions", "revenue-card");
        VBox pilatesCard = createMetricCard("Pilates", "20 sessions", "revenue-card");
        
        typeCards.getChildren().addAll(personalCard, yogaCard, crossfitCard, pilatesCard);
        
        content.getChildren().addAll(summaryCards, new Separator(), tableTitle, table, typesTitle, typeCards);
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
    public static class DailyOperations {
        private final StringProperty date;
        private final StringProperty visits;
        private final StringProperty peakHour;
        private final StringProperty avgDuration;
        private final StringProperty equipmentUtil;
        private final StringProperty staffCoverage;
        
        public DailyOperations(String date, String visits, String peakHour, 
                             String avgDuration, String equipmentUtil, String staffCoverage) {
            this.date = new SimpleStringProperty(date);
            this.visits = new SimpleStringProperty(visits);
            this.peakHour = new SimpleStringProperty(peakHour);
            this.avgDuration = new SimpleStringProperty(avgDuration);
            this.equipmentUtil = new SimpleStringProperty(equipmentUtil);
            this.staffCoverage = new SimpleStringProperty(staffCoverage);
        }
        
        public StringProperty dateProperty() { return date; }
        public StringProperty visitsProperty() { return visits; }
        public StringProperty peakHourProperty() { return peakHour; }
        public StringProperty avgDurationProperty() { return avgDuration; }
        public StringProperty equipmentUtilProperty() { return equipmentUtil; }
        public StringProperty staffCoverageProperty() { return staffCoverage; }
    }
    
    public static class AttendanceByTime {
        private final StringProperty timeSlot;
        private final StringProperty monday;
        private final StringProperty tuesday;
        private final StringProperty wednesday;
        private final StringProperty thursday;
        private final StringProperty friday;
        private final StringProperty saturday;
        private final StringProperty sunday;
        
        public AttendanceByTime(String timeSlot, String monday, String tuesday, String wednesday,
                               String thursday, String friday, String saturday, String sunday) {
            this.timeSlot = new SimpleStringProperty(timeSlot);
            this.monday = new SimpleStringProperty(monday);
            this.tuesday = new SimpleStringProperty(tuesday);
            this.wednesday = new SimpleStringProperty(wednesday);
            this.thursday = new SimpleStringProperty(thursday);
            this.friday = new SimpleStringProperty(friday);
            this.saturday = new SimpleStringProperty(saturday);
            this.sunday = new SimpleStringProperty(sunday);
        }
        
        public StringProperty timeSlotProperty() { return timeSlot; }
        public StringProperty mondayProperty() { return monday; }
        public StringProperty tuesdayProperty() { return tuesday; }
        public StringProperty wednesdayProperty() { return wednesday; }
        public StringProperty thursdayProperty() { return thursday; }
        public StringProperty fridayProperty() { return friday; }
        public StringProperty saturdayProperty() { return saturday; }
        public StringProperty sundayProperty() { return sunday; }
    }
    
    public static class SessionByTrainer {
        private final StringProperty trainer;
        private final StringProperty specialization;
        private final StringProperty sessions;
        private final StringProperty completed;
        private final StringProperty cancelled;
        private final StringProperty revenue;
        
        public SessionByTrainer(String trainer, String specialization, String sessions,
                               String completed, String cancelled, String revenue) {
            this.trainer = new SimpleStringProperty(trainer);
            this.specialization = new SimpleStringProperty(specialization);
            this.sessions = new SimpleStringProperty(sessions);
            this.completed = new SimpleStringProperty(completed);
            this.cancelled = new SimpleStringProperty(cancelled);
            this.revenue = new SimpleStringProperty(revenue);
        }
        
        public StringProperty trainerProperty() { return trainer; }
        public StringProperty specializationProperty() { return specialization; }
        public StringProperty sessionsProperty() { return sessions; }
        public StringProperty completedProperty() { return completed; }
        public StringProperty cancelledProperty() { return cancelled; }
        public StringProperty revenueProperty() { return revenue; }
    }
}
