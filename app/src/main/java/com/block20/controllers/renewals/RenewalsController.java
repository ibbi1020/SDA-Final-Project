/*
 * Block20 Gym Management System
 * Renewals Controller - Pending Renewals & Process Renewal Flow
 */
package com.block20.controllers.renewals;

import com.block20.models.Member;
import com.block20.services.MemberService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class RenewalsController {
    
    private ScrollPane mainContainer;
    private StackPane viewContainer; 
    private VBox pendingRenewalsView;
    private StackPane renewalProcessView;
    private Consumer<String> navigationHandler;
    
    // NEW: Service Dependency
    private MemberService memberService;

    // Current renewal in process
    private RenewalData currentRenewal;
    
    // Filter state
    private String currentFilter = "All"; 

    // UPDATED: Constructor accepts MemberService
    public RenewalsController(Consumer<String> navigationHandler, MemberService memberService) {
        this.navigationHandler = navigationHandler;
        this.memberService = memberService;
        initializeView();
    }
    
    private void initializeView() {
        mainContainer = new ScrollPane();
        mainContainer.setFitToWidth(true);
        mainContainer.setFitToHeight(false);
        mainContainer.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        mainContainer.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mainContainer.getStyleClass().add("content-scroll-pane");
        
        viewContainer = new StackPane();
        viewContainer.getStyleClass().add("renewals-container");
        
        pendingRenewalsView = createPendingRenewalsView();
        
        viewContainer.getChildren().add(pendingRenewalsView);
        mainContainer.setContent(viewContainer);
    }
    
    public ScrollPane getView() {
        return mainContainer;
    }
    
    // ==================== PENDING RENEWALS VIEW ====================
    
    private VBox createPendingRenewalsView() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(30));
        container.getStyleClass().add("content-container");
        
        container.getChildren().addAll(
            createHeader(),
            createFiltersSection(),
            createStatsBar(),
            createRenewalsTable()
        );
        
        return container;
    }
    
    private HBox createHeader() {
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("Renewals");
        titleLabel.getStyleClass().addAll("page-title");
        
        Label subtitleLabel = new Label("Manage pending renewals and process membership extensions");
        subtitleLabel.getStyleClass().add("page-subtitle");
        
        VBox titleBox = new VBox(5, titleLabel, subtitleLabel);
        header.getChildren().add(titleBox);
        return header;
    }
    
    private HBox createFiltersSection() {
        HBox filtersBox = new HBox(15);
        filtersBox.setAlignment(Pos.CENTER_LEFT);
        filtersBox.setPadding(new Insets(10));
        filtersBox.getStyleClass().add("filters-section");
        
        Label filtersLabel = new Label("FILTERS:");
        filtersLabel.getStyleClass().add("form-label");
        
        ToggleGroup filterGroup = new ToggleGroup();
        
        RadioButton allFilter = new RadioButton("All");
        allFilter.setToggleGroup(filterGroup);
        allFilter.setSelected(true);
        allFilter.getStyleClass().add("filter-radio");
        allFilter.setOnAction(e -> applyFilter("All"));
        
        RadioButton expiringSoonFilter = new RadioButton("Expiring Soon");
        expiringSoonFilter.setToggleGroup(filterGroup);
        expiringSoonFilter.getStyleClass().add("filter-radio");
        expiringSoonFilter.setOnAction(e -> applyFilter("Expiring Soon"));
        
        RadioButton overdueFilter = new RadioButton("Overdue");
        overdueFilter.setToggleGroup(filterGroup);
        overdueFilter.getStyleClass().add("filter-radio");
        overdueFilter.setOnAction(e -> applyFilter("Overdue"));
        
        filtersBox.getChildren().addAll(filtersLabel, allFilter, expiringSoonFilter, overdueFilter);
        return filtersBox;
    }
    
    private HBox createStatsBar() {
        // In a real app, these counts would be dynamic based on the list below
        // For now, we can leave them static or update them after loading data
        HBox statsBar = new HBox(20);
        statsBar.setAlignment(Pos.CENTER_LEFT);
        statsBar.setPadding(new Insets(15));
        statsBar.getStyleClass().add("stats-bar");
        
        statsBar.getChildren().addAll(
            createStatItem("Total Pending", "-", "#3B82F6"),
            createStatItem("Expiring Soon", "-", "#F59E0B"),
            createStatItem("Overdue", "-", "#EF4444")
        );
        
        return statsBar;
    }
    
    private VBox createStatItem(String label, String value, String color) {
        VBox statBox = new VBox(5);
        statBox.setAlignment(Pos.CENTER);
        statBox.setPadding(new Insets(10));
        statBox.getStyleClass().add("stat-item");
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        Label labelText = new Label(label);
        labelText.getStyleClass().add("stat-label");
        
        statBox.getChildren().addAll(valueLabel, labelText);
        return statBox;
    }
    
    private VBox createRenewalsTable() {
        VBox tableContainer = new VBox(10);
        tableContainer.getStyleClass().add("table-container");
        
        // Table header
        HBox tableHeader = new HBox();
        tableHeader.getStyleClass().add("table-header");
        tableHeader.setPadding(new Insets(10));
        
        Label statusCol = new Label(""); statusCol.setPrefWidth(50);
        Label idCol = new Label("ID"); idCol.setPrefWidth(100);
        Label nameCol = new Label("Name"); nameCol.setPrefWidth(200);
        Label planCol = new Label("Plan"); planCol.setPrefWidth(150);
        Label expiryCol = new Label("Expiry Date"); expiryCol.setPrefWidth(150);
        Label daysCol = new Label("Days Until"); daysCol.setPrefWidth(120);
        Label amountCol = new Label("Amount"); amountCol.setPrefWidth(120);
        Label actionCol = new Label("Action"); actionCol.setPrefWidth(150);
        
        tableHeader.getChildren().addAll(statusCol, idCol, nameCol, planCol, expiryCol, daysCol, amountCol, actionCol);
        
        // Table rows
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        
        VBox tableRows = new VBox(5);
        tableRows.setPadding(new Insets(10));
        
        // NEW: Load REAL data instead of mock data
        List<MemberRenewalData> renewalsList = loadRealRenewals();
        
        if (renewalsList.isEmpty()) {
            Label emptyLabel = new Label("No memberships need renewal at this time.");
            emptyLabel.setPadding(new Insets(20));
            tableRows.getChildren().add(emptyLabel);
        } else {
            for (MemberRenewalData member : renewalsList) {
                tableRows.getChildren().add(createTableRow(member));
            }
        }
        
        scrollPane.setContent(tableRows);
        tableContainer.getChildren().addAll(tableHeader, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        return tableContainer;
    }
    
    private HBox createTableRow(MemberRenewalData member) {
        HBox row = new HBox();
        row.getStyleClass().add("table-row");
        row.setPadding(new Insets(10));
        row.setAlignment(Pos.CENTER_LEFT);
        
        Label statusIndicator = new Label("●");
        statusIndicator.setPrefWidth(50);
        statusIndicator.setAlignment(Pos.CENTER);
        statusIndicator.setStyle("-fx-font-size: 20px; -fx-text-fill: " + member.getStatusColor() + ";");
        
        Label idLabel = new Label(member.memberId); idLabel.setPrefWidth(100);
        Label nameLabel = new Label(member.name); nameLabel.setPrefWidth(200);
        Label planLabel = new Label(member.plan); planLabel.setPrefWidth(150);
        Label expiryLabel = new Label(member.expiryDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))); expiryLabel.setPrefWidth(150);
        
        Label daysLabel = new Label(member.getStatusText());
        daysLabel.setPrefWidth(120);
        daysLabel.setStyle("-fx-text-fill: " + member.getStatusColor() + ";");
        
        Label amountLabel = new Label(String.format("$%.2f", member.renewalAmount));
        amountLabel.setPrefWidth(120);
        
        Button renewButton = new Button("Renew");
        renewButton.getStyleClass().add("btn-primary-small");
        renewButton.setPrefWidth(130);
        renewButton.setOnAction(e -> startRenewalProcess(member));
        
        HBox actionBox = new HBox(renewButton);
        actionBox.setPrefWidth(150);
        actionBox.setAlignment(Pos.CENTER_LEFT);
        
        row.getChildren().addAll(statusIndicator, idLabel, nameLabel, planLabel, expiryLabel, daysLabel, amountLabel, actionBox);
        return row;
    }
    
    private void applyFilter(String filter) {
        currentFilter = filter;
        // In a real implementation, we would reload the table here
        viewContainer.getChildren().clear();
        viewContainer.getChildren().add(createPendingRenewalsView());
    }
    
    // ==================== RENEWAL PROCESS LOGIC ====================
    
    private void startRenewalProcess(MemberRenewalData member) {
        currentRenewal = new RenewalData();
        currentRenewal.memberId = member.memberId;
        currentRenewal.memberName = member.name;
        currentRenewal.memberEmail = member.email;
        currentRenewal.currentPlan = member.plan;
        currentRenewal.currentExpiry = member.expiryDate;
        currentRenewal.renewalAmount = member.renewalAmount;
        currentRenewal.discount = 0.0; 
        currentRenewal.selectedPlan = member.plan; 
        
        renewalProcessView = createRenewalWizard();
        viewContainer.getChildren().clear();
        viewContainer.getChildren().add(renewalProcessView);
    }
    
    private StackPane createRenewalWizard() {
        StackPane wizardContainer = new StackPane();
        wizardContainer.getStyleClass().add("renewal-wizard");
        wizardContainer.getChildren().add(createStep1_ReviewConfirm());
        return wizardContainer;
    }
    
    // STEP 1: Review
    private VBox createStep1_ReviewConfirm() {
        VBox container = new VBox(30);
        container.setPadding(new Insets(30));
        container.setAlignment(Pos.TOP_CENTER);
        
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setMaxWidth(800);
        
        content.getChildren().addAll(
            createMemberInfoCard(),
            createCurrentMembershipCard(),
            createPlanSelectionCard(),
            createPricingSummaryCard()
        );
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        HBox navButtons = createNavigationButtons("Cancel", "Continue to Payment", e -> cancelRenewal(), e -> showStep2_Payment());
        
        container.getChildren().addAll(createWizardHeader("Renewal Review", "Step 1 of 3"), scrollPane, navButtons);
        return container;
    }
    
    // STEP 2: Payment
    private VBox createStep2_Payment() {
        VBox container = new VBox(30);
        container.setPadding(new Insets(30));
        container.setAlignment(Pos.TOP_CENTER);
        
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setMaxWidth(800);
        
        content.getChildren().addAll(createInvoiceCard(), createPaymentMethodCard());
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        HBox navButtons = createNavigationButtons("Back", "Process Payment", e -> showStep1_ReviewConfirm(), e -> processRenewalPayment());
        
        container.getChildren().addAll(createWizardHeader("Payment", "Step 2 of 3"), scrollPane, navButtons);
        return container;
    }
    
// --- MISSING NAVIGATION METHODS ---

    private void showStep1_ReviewConfirm() {
        VBox step1 = createStep1_ReviewConfirm();
        renewalProcessView.getChildren().clear();
        renewalProcessView.getChildren().add(step1);
    }

    private void showStep2_Payment() {
        VBox step2 = createStep2_Payment();
        renewalProcessView.getChildren().clear();
        renewalProcessView.getChildren().add(step2);
    }

    // NEW: Real Payment Processing
    private void processRenewalPayment() {
        try {
            System.out.println("Backend: Processing Renewal for " + currentRenewal.memberId);
            
            // 1. CALL THE SERVICE
            memberService.renewMembership(currentRenewal.memberId, currentRenewal.selectedPlan);
            
            // 2. Update local data for display
            currentRenewal.transactionId = "TXN" + System.currentTimeMillis();
            // Expiry is updated in backend, but we calculate here for display
            currentRenewal.newExpiry = currentRenewal.currentExpiry.isBefore(LocalDate.now()) 
                ? LocalDate.now().plusMonths(1) 
                : currentRenewal.currentExpiry.plusMonths(1);

            // 3. Show Success
            VBox step3 = createStep3_Confirmation();
            renewalProcessView.getChildren().clear();
            renewalProcessView.getChildren().add(step3);
            
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Renewal failed: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    // STEP 3: Confirmation
    private VBox createStep3_Confirmation() {
        VBox container = new VBox(30);
        container.setPadding(new Insets(30));
        container.setAlignment(Pos.CENTER);
        
        Label successIcon = new Label("✓");
        successIcon.setStyle("-fx-font-size: 80px; -fx-text-fill: #10B981;");
        Label successTitle = new Label("Renewal Successful!");
        successTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #10B981;");
        
        Button doneButton = new Button("Done");
        doneButton.getStyleClass().add("btn-primary");
        doneButton.setPrefWidth(150);
        doneButton.setOnAction(e -> returnToPendingRenewals());
        
        container.getChildren().addAll(successIcon, successTitle, createRenewalDetailsCard(), doneButton);
        return container;
    }
    
    // ==================== HELPER COMPONENTS ====================
    
    private VBox createWizardHeader(String title, String subtitle) {
        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER);
        Label t = new Label(title); t.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        Label s = new Label(subtitle); s.setStyle("-fx-text-fill: #6B7280;");
        header.getChildren().addAll(t, s);
        return header;
    }
    
    private VBox createMemberInfoCard() {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        GridPane grid = new GridPane(); grid.setHgap(20); grid.setVgap(10);
        addInfoRow(grid, 0, "Member ID:", currentRenewal.memberId);
        addInfoRow(grid, 1, "Name:", currentRenewal.memberName);
        addInfoRow(grid, 2, "Email:", currentRenewal.memberEmail);
        card.getChildren().addAll(new Label("Member Info"), grid);
        return card;
    }
    
    private VBox createCurrentMembershipCard() {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        GridPane grid = new GridPane(); grid.setHgap(20); grid.setVgap(10);
        addInfoRow(grid, 0, "Plan:", currentRenewal.currentPlan);
        addInfoRow(grid, 1, "Expires:", currentRenewal.currentExpiry.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        card.getChildren().addAll(new Label("Current Membership"), grid);
        return card;
    }
    
    private VBox createPlanSelectionCard() {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        ToggleGroup grp = new ToggleGroup();
        RadioButton keep = new RadioButton("Keep " + currentRenewal.currentPlan); keep.setToggleGroup(grp); keep.setSelected(true);
        keep.setOnAction(e -> { currentRenewal.selectedPlan = currentRenewal.currentPlan; currentRenewal.renewalAmount = getPlanPrice(currentRenewal.currentPlan); });
        
        RadioButton upgrade = new RadioButton("Upgrade to Premium ($49.99)"); upgrade.setToggleGroup(grp);
        upgrade.setOnAction(e -> { currentRenewal.selectedPlan = "Premium"; currentRenewal.renewalAmount = 49.99; });
        
        card.getChildren().addAll(new Label("Select Plan"), keep, upgrade);
        return card;
    }
    
    private VBox createPricingSummaryCard() {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: #F3F4F6; -fx-padding: 20; -fx-background-radius: 8;");
        currentRenewal.totalAmount = currentRenewal.renewalAmount * 1.08;
        card.getChildren().addAll(new Label("Total Due: $" + String.format("%.2f", currentRenewal.totalAmount)));
        return card;
    }
    
    private VBox createInvoiceCard() {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 8;");
        card.getChildren().add(new Label("Invoice Amount: $" + String.format("%.2f", currentRenewal.totalAmount)));
        return card;
    }
    
    private VBox createPaymentMethodCard() {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 8;");
        card.getChildren().addAll(new Label("Payment Method"), new RadioButton("Credit Card"), new TextField("Card Number..."));
        return card;
    }
    
    private VBox createRenewalDetailsCard() {
        VBox card = new VBox(15);
        card.setStyle("-fx-background-color: #F0FDF4; -fx-padding: 20;");
        card.getChildren().add(new Label("New Expiry: " + currentRenewal.newExpiry));
        return card;
    }
    
    private void addInfoRow(GridPane grid, int row, String label, String value) {
        grid.add(new Label(label), 0, row);
        grid.add(new Label(value), 1, row);
    }
    
    private HBox createNavigationButtons(String backText, String nextText, javafx.event.EventHandler<javafx.event.ActionEvent> back, javafx.event.EventHandler<javafx.event.ActionEvent> next) {
        HBox box = new HBox(15); box.setAlignment(Pos.CENTER);
        Button b1 = new Button(backText); b1.getStyleClass().add("btn-secondary"); b1.setOnAction(back);
        Button b2 = new Button(nextText); b2.getStyleClass().add("btn-primary"); b2.setOnAction(next);
        box.getChildren().addAll(b1, b2);
        return box;
    }
    
    private void cancelRenewal() {
        returnToPendingRenewals();
    }
    
    private void returnToPendingRenewals() {
        currentRenewal = null;
        viewContainer.getChildren().clear();
        // Re-create to refresh data
        viewContainer.getChildren().add(createPendingRenewalsView());
    }
    
    private double getPlanPrice(String plan) {
        return switch (plan) {
            case "Basic" -> 29.99;
            case "Premium" -> 49.99;
            case "Elite" -> 79.99;
            case "Student" -> 24.99;
            default -> 29.99;
        };
    }
    
    // NEW: Load REAL data from backend
    private List<MemberRenewalData> loadRealRenewals() {
        List<Member> allMembers = memberService.getAllMembers();
        List<MemberRenewalData> renewalList = new ArrayList<>();
        
        for (Member m : allMembers) {
            // In a real app, we would filter here (e.g. only those expiring in < 30 days)
            // For this demo, we show ALL members so you can test the button easily
            
            MemberRenewalData data = new MemberRenewalData();
            data.memberId = m.getMemberId();
            data.name = m.getFullName();
            data.email = m.getEmail();
            data.plan = m.getPlanType();
            data.expiryDate = m.getExpiryDate();
            data.renewalAmount = getPlanPrice(m.getPlanType());
            
            // Apply Filter Logic
            long daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), m.getExpiryDate());
            boolean matchesFilter = false;
            
            if (currentFilter.equals("All")) matchesFilter = true;
            else if (currentFilter.equals("Overdue") && daysUntil < 0) matchesFilter = true;
            else if (currentFilter.equals("Expiring Soon") && daysUntil >= 0 && daysUntil <= 30) matchesFilter = true;
            
            if (matchesFilter) {
                renewalList.add(data);
            }
        }
        return renewalList;
    }
    
    // Data Classes
    private static class MemberRenewalData {
        String memberId, name, email, plan;
        LocalDate expiryDate;
        double renewalAmount;
        
        String getStatusText() {
            long d = ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
            return d < 0 ? "Expired " + Math.abs(d) + "d ago" : d + " days";
        }
        String getStatusColor() {
            long d = ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
            return d < 0 ? "#EF4444" : (d <= 7 ? "#F59E0B" : "#10B981");
        }
    }
    
    private static class RenewalData {
        String memberId, memberName, memberEmail, currentPlan, selectedPlan, paymentMethod, transactionId;
        LocalDate currentExpiry, newExpiry;
        double renewalAmount, discount, totalAmount;
    }
}