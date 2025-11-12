/*
 * Block20 Gym Management System
 * Renewals Controller - Pending Renewals & Process Renewal Flow
 */
package com.block20.controllers.renewals;

import javafx.animation.PauseTransition;
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
import java.util.Random;
import java.util.function.Consumer;

/**
 * Controller for comprehensive renewals management:
 * 1. Pending Renewals List (with filters for All/Expiring Soon/Overdue)
 * 2. Renewal Processing Flow (3-step wizard: Review → Payment → Confirmation)
 */
public class RenewalsController {
    
    private StackPane mainContainer;
    private VBox pendingRenewalsView;
    private StackPane renewalProcessView;
    private Consumer<String> navigationHandler;
    
    // Current renewal in process
    private RenewalData currentRenewal;
    
    // Filter state
    private String currentFilter = "All"; // All, Expiring Soon, Overdue
    
    /**
     * Constructor
     */
    public RenewalsController(Consumer<String> navigationHandler) {
        this.navigationHandler = navigationHandler;
        initializeView();
    }
    
    /**
     * Initialize the main view
     */
    private void initializeView() {
        mainContainer = new StackPane();
        mainContainer.getStyleClass().add("renewals-container");
        
        // Create pending renewals list view
        pendingRenewalsView = createPendingRenewalsView();
        
        // Show pending renewals by default
        mainContainer.getChildren().add(pendingRenewalsView);
    }
    
    /**
     * Get the main view
     */
    public StackPane getView() {
        return mainContainer;
    }
    
    // ==================== PENDING RENEWALS VIEW ====================
    
    /**
     * Create the pending renewals list view
     */
    private VBox createPendingRenewalsView() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(30));
        container.getStyleClass().add("content-container");
        
        // Header
        HBox header = createHeader();
        
        // Filters section
        HBox filtersSection = createFiltersSection();
        
        // Stats bar
        HBox statsBar = createStatsBar();
        
        // Renewals table
        VBox tableSection = createRenewalsTable();
        
        container.getChildren().addAll(header, filtersSection, statsBar, tableSection);
        
        return container;
    }
    
    /**
     * Create header
     */
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
    
    /**
     * Create filters section
     */
    private HBox createFiltersSection() {
        HBox filtersBox = new HBox(15);
        filtersBox.setAlignment(Pos.CENTER_LEFT);
        filtersBox.setPadding(new Insets(10));
        filtersBox.getStyleClass().add("filters-section");
        
        Label filtersLabel = new Label("FILTERS:");
        filtersLabel.getStyleClass().add("form-label");
        
        // Filter buttons
        ToggleGroup filterGroup = new ToggleGroup();
        
        RadioButton allFilter = new RadioButton("All (47)");
        allFilter.setToggleGroup(filterGroup);
        allFilter.setSelected(true);
        allFilter.getStyleClass().add("filter-radio");
        allFilter.setOnAction(e -> applyFilter("All"));
        
        RadioButton expiringSoonFilter = new RadioButton("Expiring Soon (35)");
        expiringSoonFilter.setToggleGroup(filterGroup);
        expiringSoonFilter.getStyleClass().add("filter-radio");
        expiringSoonFilter.setOnAction(e -> applyFilter("Expiring Soon"));
        
        RadioButton overdueFilter = new RadioButton("Overdue (12)");
        overdueFilter.setToggleGroup(filterGroup);
        overdueFilter.getStyleClass().add("filter-radio");
        overdueFilter.setOnAction(e -> applyFilter("Overdue"));
        
        filtersBox.getChildren().addAll(filtersLabel, allFilter, expiringSoonFilter, overdueFilter);
        
        return filtersBox;
    }
    
    /**
     * Create stats bar
     */
    private HBox createStatsBar() {
        HBox statsBar = new HBox(20);
        statsBar.setAlignment(Pos.CENTER_LEFT);
        statsBar.setPadding(new Insets(15));
        statsBar.getStyleClass().add("stats-bar");
        
        statsBar.getChildren().addAll(
            createStatItem("Total Pending", "47", "#3B82F6"),
            createStatItem("Expiring Soon", "35", "#F59E0B"),
            createStatItem("Overdue", "12", "#EF4444")
        );
        
        return statsBar;
    }
    
    /**
     * Create stat item
     */
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
    
    /**
     * Create renewals table
     */
    private VBox createRenewalsTable() {
        VBox tableContainer = new VBox(10);
        tableContainer.getStyleClass().add("table-container");
        
        // Table header
        HBox tableHeader = new HBox();
        tableHeader.getStyleClass().add("table-header");
        tableHeader.setPadding(new Insets(10));
        
        Label statusCol = new Label("");
        statusCol.setPrefWidth(50);
        statusCol.getStyleClass().add("table-header-cell");
        
        Label idCol = new Label("ID");
        idCol.setPrefWidth(100);
        idCol.getStyleClass().add("table-header-cell");
        
        Label nameCol = new Label("Name");
        nameCol.setPrefWidth(200);
        nameCol.getStyleClass().add("table-header-cell");
        
        Label planCol = new Label("Plan");
        planCol.setPrefWidth(150);
        planCol.getStyleClass().add("table-header-cell");
        
        Label expiryCol = new Label("Expiry Date");
        expiryCol.setPrefWidth(150);
        expiryCol.getStyleClass().add("table-header-cell");
        
        Label daysCol = new Label("Days Until");
        daysCol.setPrefWidth(120);
        daysCol.getStyleClass().add("table-header-cell");
        
        Label amountCol = new Label("Amount");
        amountCol.setPrefWidth(120);
        amountCol.getStyleClass().add("table-header-cell");
        
        Label actionCol = new Label("Action");
        actionCol.setPrefWidth(150);
        actionCol.getStyleClass().add("table-header-cell");
        
        tableHeader.getChildren().addAll(statusCol, idCol, nameCol, planCol, expiryCol, daysCol, amountCol, actionCol);
        
        // Table rows
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("table-scroll");
        
        VBox tableRows = new VBox(5);
        tableRows.setPadding(new Insets(10));
        
        // Generate mock data
        List<MemberRenewalData> renewalsList = generateMockRenewals();
        
        for (MemberRenewalData member : renewalsList) {
            tableRows.getChildren().add(createTableRow(member));
        }
        
        scrollPane.setContent(tableRows);
        
        tableContainer.getChildren().addAll(tableHeader, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        return tableContainer;
    }
    
    /**
     * Create table row
     */
    private HBox createTableRow(MemberRenewalData member) {
        HBox row = new HBox();
        row.getStyleClass().add("table-row");
        row.setPadding(new Insets(10));
        row.setAlignment(Pos.CENTER_LEFT);
        
        // Status indicator
        Label statusIndicator = new Label("●");
        statusIndicator.setPrefWidth(50);
        statusIndicator.setAlignment(Pos.CENTER);
        statusIndicator.setStyle("-fx-font-size: 20px; -fx-text-fill: " + member.getStatusColor() + ";");
        
        // Member ID
        Label idLabel = new Label(member.memberId);
        idLabel.setPrefWidth(100);
        
        // Name
        Label nameLabel = new Label(member.name);
        nameLabel.setPrefWidth(200);
        nameLabel.setStyle("-fx-font-weight: 500;");
        
        // Plan
        Label planLabel = new Label(member.plan);
        planLabel.setPrefWidth(150);
        
        // Expiry date
        Label expiryLabel = new Label(member.expiryDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        expiryLabel.setPrefWidth(150);
        
        // Days until expiry
        long daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), member.expiryDate);
        Label daysLabel = new Label(member.getStatusText());
        daysLabel.setPrefWidth(120);
        daysLabel.setStyle("-fx-text-fill: " + member.getStatusColor() + "; -fx-font-weight: 500;");
        
        // Amount
        Label amountLabel = new Label(String.format("$%.2f", member.renewalAmount));
        amountLabel.setPrefWidth(120);
        amountLabel.setStyle("-fx-font-weight: 500;");
        
        // Action button
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
    
    /**
     * Apply filter
     */
    private void applyFilter(String filter) {
        currentFilter = filter;
        System.out.println("Filter applied: " + filter);
        // In real implementation, this would refresh the table with filtered data
    }
    
    // ==================== RENEWAL PROCESS WIZARD ====================
    
    /**
     * Start renewal process for a member
     */
    private void startRenewalProcess(MemberRenewalData member) {
        System.out.println("Starting renewal process for: " + member.name);
        
        // Initialize renewal data
        currentRenewal = new RenewalData();
        currentRenewal.memberId = member.memberId;
        currentRenewal.memberName = member.name;
        currentRenewal.memberEmail = member.email;
        currentRenewal.currentPlan = member.plan;
        currentRenewal.currentExpiry = member.expiryDate;
        currentRenewal.renewalAmount = member.renewalAmount;
        currentRenewal.discount = calculateDiscount(member);
        currentRenewal.selectedPlan = member.plan; // Default to current plan
        
        // Create and show renewal wizard
        renewalProcessView = createRenewalWizard();
        mainContainer.getChildren().clear();
        mainContainer.getChildren().add(renewalProcessView);
    }
    
    /**
     * Calculate discount based on renewal timing and loyalty
     */
    private double calculateDiscount(MemberRenewalData member) {
        double discount = 0.0;
        
        // Early renewal discount (30+ days before expiry) - 10%
        long daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), member.expiryDate);
        if (daysUntil >= 30) {
            discount += 0.10;
        }
        
        // Loyalty bonus (3+ years) - 5%
        if (member.memberYears >= 3) {
            discount += 0.05;
        }
        
        return discount;
    }
    
    /**
     * Create renewal wizard
     */
    private StackPane createRenewalWizard() {
        StackPane wizardContainer = new StackPane();
        wizardContainer.getStyleClass().add("renewal-wizard");
        
        // Show Step 1: Review & Confirm
        VBox step1 = createStep1_ReviewConfirm();
        wizardContainer.getChildren().add(step1);
        
        return wizardContainer;
    }
    
    // ==================== RENEWAL WIZARD STEPS ====================
    
    /**
     * Step 1: Review & Confirm Renewal
     */
    private VBox createStep1_ReviewConfirm() {
        VBox container = new VBox(30);
        container.setPadding(new Insets(30));
        container.setAlignment(Pos.TOP_CENTER);
        container.getStyleClass().add("wizard-step");
        
        // Wizard header
        VBox header = createWizardHeader("Renewal Review", "Step 1 of 3");
        
        // Progress indicator
        HBox progressBar = createProgressIndicator(1);
        
        // Content
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.getStyleClass().add("wizard-scroll");
        
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setMaxWidth(800);
        content.setAlignment(Pos.TOP_CENTER);
        
        // Member information card
        VBox memberCard = createMemberInfoCard();
        
        // Current membership card
        VBox currentMembershipCard = createCurrentMembershipCard();
        
        // Plan selection card
        VBox planSelectionCard = createPlanSelectionCard();
        
        // Pricing summary card
        VBox pricingSummaryCard = createPricingSummaryCard();
        
        content.getChildren().addAll(memberCard, currentMembershipCard, planSelectionCard, pricingSummaryCard);
        scrollPane.setContent(content);
        
        // Navigation buttons
        HBox navButtons = createNavigationButtons(
            "Cancel",
            "Continue to Payment",
            e -> cancelRenewal(),
            e -> showStep2_Payment()
        );
        
        container.getChildren().addAll(header, progressBar, scrollPane, navButtons);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        return container;
    }
    
    /**
     * Step 2: Payment Processing
     */
    private VBox createStep2_Payment() {
        VBox container = new VBox(30);
        container.setPadding(new Insets(30));
        container.setAlignment(Pos.TOP_CENTER);
        container.getStyleClass().add("wizard-step");
        
        // Wizard header
        VBox header = createWizardHeader("Payment", "Step 2 of 3");
        
        // Progress indicator
        HBox progressBar = createProgressIndicator(2);
        
        // Content
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.getStyleClass().add("wizard-scroll");
        
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setMaxWidth(800);
        content.setAlignment(Pos.TOP_CENTER);
        
        // Invoice summary
        VBox invoiceCard = createInvoiceCard();
        
        // Payment method selection
        VBox paymentCard = createPaymentMethodCard();
        
        content.getChildren().addAll(invoiceCard, paymentCard);
        scrollPane.setContent(content);
        
        // Navigation buttons
        HBox navButtons = createNavigationButtons(
            "Back",
            "Process Payment",
            e -> showStep1_ReviewConfirm(),
            e -> processRenewalPayment()
        );
        
        container.getChildren().addAll(header, progressBar, scrollPane, navButtons);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        return container;
    }
    
    /**
     * Step 3: Confirmation
     */
    private VBox createStep3_Confirmation() {
        VBox container = new VBox(30);
        container.setPadding(new Insets(30));
        container.setAlignment(Pos.CENTER);
        container.getStyleClass().add("wizard-step");
        
        // Wizard header
        VBox header = createWizardHeader("Renewal Confirmed", "Step 3 of 3");
        
        // Progress indicator
        HBox progressBar = createProgressIndicator(3);
        
        // Success content
        VBox successContent = new VBox(30);
        successContent.setAlignment(Pos.CENTER);
        successContent.setMaxWidth(600);
        
        // Success icon and message
        Label successIcon = new Label("✓");
        successIcon.getStyleClass().add("success-icon");
        successIcon.setStyle("-fx-font-size: 80px; -fx-text-fill: #10B981;");
        
        Label successTitle = new Label("Renewal Successful!");
        successTitle.getStyleClass().add("success-title");
        successTitle.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #10B981;");
        
        Label successMessage = new Label("Membership has been extended successfully");
        successMessage.getStyleClass().add("success-message");
        successMessage.setStyle("-fx-font-size: 16px; -fx-text-fill: #6B7280;");
        
        // Renewal details card
        VBox detailsCard = createRenewalDetailsCard();
        
        // Action buttons
        HBox actionButtons = new HBox(15);
        actionButtons.setAlignment(Pos.CENTER);
        
        Button printButton = new Button("Print Receipt");
        printButton.getStyleClass().add("btn-secondary");
        printButton.setPrefWidth(150);
        printButton.setOnAction(e -> printReceipt());
        
        Button emailButton = new Button("Email Receipt");
        emailButton.getStyleClass().add("btn-secondary");
        emailButton.setPrefWidth(150);
        emailButton.setOnAction(e -> emailReceipt());
        
        Button doneButton = new Button("Done");
        doneButton.getStyleClass().add("btn-primary");
        doneButton.setPrefWidth(150);
        doneButton.setOnAction(e -> returnToPendingRenewals());
        
        actionButtons.getChildren().addAll(printButton, emailButton, doneButton);
        
        successContent.getChildren().addAll(successIcon, successTitle, successMessage, detailsCard, actionButtons);
        
        container.getChildren().addAll(header, progressBar, successContent);
        
        return container;
    }
    
    // ==================== WIZARD COMPONENTS ====================
    
    /**
     * Create wizard header
     */
    private VBox createWizardHeader(String title, String subtitle) {
        VBox header = new VBox(5);
        header.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("wizard-title");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.getStyleClass().add("wizard-subtitle");
        subtitleLabel.setStyle("-fx-text-fill: #6B7280;");
        
        header.getChildren().addAll(titleLabel, subtitleLabel);
        
        return header;
    }
    
    /**
     * Create progress indicator
     */
    private HBox createProgressIndicator(int currentStep) {
        HBox progressBox = new HBox(10);
        progressBox.setAlignment(Pos.CENTER);
        progressBox.setPadding(new Insets(10));
        
        for (int i = 1; i <= 3; i++) {
            // Step circle
            Label stepCircle = new Label(String.valueOf(i));
            stepCircle.getStyleClass().add("progress-circle");
            
            if (i < currentStep) {
                stepCircle.getStyleClass().add("completed");
                stepCircle.setText("✓");
            } else if (i == currentStep) {
                stepCircle.getStyleClass().add("current");
            } else {
                stepCircle.getStyleClass().add("upcoming");
            }
            
            progressBox.getChildren().add(stepCircle);
            
            // Connector line (except after last step)
            if (i < 3) {
                Label connector = new Label("───");
                connector.getStyleClass().add("progress-connector");
                if (i < currentStep) {
                    connector.getStyleClass().add("completed");
                }
                progressBox.getChildren().add(connector);
            }
        }
        
        return progressBox;
    }
    
    /**
     * Create member info card
     */
    private VBox createMemberInfoCard() {
        VBox card = new VBox(15);
        card.getStyleClass().add("info-card");
        card.setPadding(new Insets(20));
        
        Label cardTitle = new Label("Member Information");
        cardTitle.getStyleClass().add("card-title");
        cardTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(10);
        
        addInfoRow(grid, 0, "Member ID:", currentRenewal.memberId);
        addInfoRow(grid, 1, "Name:", currentRenewal.memberName);
        addInfoRow(grid, 2, "Email:", currentRenewal.memberEmail);
        
        card.getChildren().addAll(cardTitle, grid);
        
        return card;
    }
    
    /**
     * Create current membership card
     */
    private VBox createCurrentMembershipCard() {
        VBox card = new VBox(15);
        card.getStyleClass().add("info-card");
        card.setPadding(new Insets(20));
        
        Label cardTitle = new Label("Current Membership");
        cardTitle.getStyleClass().add("card-title");
        cardTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(10);
        
        addInfoRow(grid, 0, "Plan:", currentRenewal.currentPlan);
        addInfoRow(grid, 1, "Expires:", currentRenewal.currentExpiry.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        
        long daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), currentRenewal.currentExpiry);
        String status = daysUntil < 0 ? "Expired " + Math.abs(daysUntil) + " days ago" : 
                        daysUntil < 7 ? "Expires in " + daysUntil + " days" : "Active";
        addInfoRow(grid, 2, "Status:", status);
        
        card.getChildren().addAll(cardTitle, grid);
        
        return card;
    }
    
    /**
     * Create plan selection card
     */
    private VBox createPlanSelectionCard() {
        VBox card = new VBox(15);
        card.getStyleClass().add("info-card");
        card.setPadding(new Insets(20));
        
        Label cardTitle = new Label("Select Renewal Plan");
        cardTitle.getStyleClass().add("card-title");
        cardTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        ToggleGroup planGroup = new ToggleGroup();
        VBox planOptions = new VBox(10);
        
        // Plan options
        RadioButton keepPlan = new RadioButton("Keep Current Plan (" + currentRenewal.currentPlan + ") - $" + String.format("%.2f", currentRenewal.renewalAmount) + "/month");
        keepPlan.setToggleGroup(planGroup);
        keepPlan.setSelected(true);
        keepPlan.getStyleClass().add("plan-radio");
        keepPlan.setOnAction(e -> {
            currentRenewal.selectedPlan = currentRenewal.currentPlan;
            currentRenewal.renewalAmount = getPlanPrice(currentRenewal.currentPlan);
        });
        
        RadioButton basicPlan = new RadioButton("Downgrade to Basic - $29.99/month");
        basicPlan.setToggleGroup(planGroup);
        basicPlan.getStyleClass().add("plan-radio");
        basicPlan.setOnAction(e -> {
            currentRenewal.selectedPlan = "Basic";
            currentRenewal.renewalAmount = 29.99;
        });
        
        RadioButton premiumPlan = new RadioButton("Upgrade to Premium - $49.99/month");
        premiumPlan.setToggleGroup(planGroup);
        premiumPlan.getStyleClass().add("plan-radio");
        premiumPlan.setOnAction(e -> {
            currentRenewal.selectedPlan = "Premium";
            currentRenewal.renewalAmount = 49.99;
        });
        
        RadioButton elitePlan = new RadioButton("Upgrade to Elite - $79.99/month");
        elitePlan.setToggleGroup(planGroup);
        elitePlan.getStyleClass().add("plan-radio");
        elitePlan.setOnAction(e -> {
            currentRenewal.selectedPlan = "Elite";
            currentRenewal.renewalAmount = 79.99;
        });
        
        planOptions.getChildren().addAll(keepPlan, basicPlan, premiumPlan, elitePlan);
        
        card.getChildren().addAll(cardTitle, planOptions);
        
        return card;
    }
    
    /**
     * Create pricing summary card
     */
    private VBox createPricingSummaryCard() {
        VBox card = new VBox(15);
        card.getStyleClass().add("info-card");
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: #F3F4F6;");
        
        Label cardTitle = new Label("Pricing Summary");
        cardTitle.getStyleClass().add("card-title");
        cardTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        VBox pricingItems = new VBox(8);
        
        // Base price
        HBox baseRow = createPricingRow("Base Price:", String.format("$%.2f", currentRenewal.renewalAmount));
        
        // Discount
        double discountAmount = currentRenewal.renewalAmount * currentRenewal.discount;
        HBox discountRow = createPricingRow("Discount (" + String.format("%.0f", currentRenewal.discount * 100) + "%):", 
                                           String.format("-$%.2f", discountAmount));
        discountRow.setStyle("-fx-text-fill: #10B981;");
        
        // Subtotal
        double subtotal = currentRenewal.renewalAmount - discountAmount;
        HBox subtotalRow = createPricingRow("Subtotal:", String.format("$%.2f", subtotal));
        
        // Tax
        double tax = subtotal * 0.08;
        HBox taxRow = createPricingRow("Tax (8%):", String.format("$%.2f", tax));
        
        // Separator
        Separator separator = new Separator();
        separator.setStyle("-fx-padding: 5 0;");
        
        // Total
        double total = subtotal + tax;
        currentRenewal.totalAmount = total;
        HBox totalRow = createPricingRow("Total:", String.format("$%.2f", total));
        totalRow.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // New expiry date
        LocalDate newExpiry = currentRenewal.currentExpiry.isAfter(LocalDate.now()) ? 
                              currentRenewal.currentExpiry.plusMonths(1) : 
                              LocalDate.now().plusMonths(1);
        currentRenewal.newExpiry = newExpiry;
        HBox expiryRow = createPricingRow("New Expiry:", newExpiry.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        
        pricingItems.getChildren().addAll(baseRow, discountRow, subtotalRow, taxRow, separator, totalRow, expiryRow);
        
        card.getChildren().addAll(cardTitle, pricingItems);
        
        return card;
    }
    
    /**
     * Create invoice card
     */
    private VBox createInvoiceCard() {
        VBox card = new VBox(15);
        card.getStyleClass().add("info-card");
        card.setPadding(new Insets(20));
        
        Label cardTitle = new Label("Renewal Invoice");
        cardTitle.getStyleClass().add("card-title");
        cardTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(10);
        
        addInfoRow(grid, 0, "Member:", currentRenewal.memberName + " (" + currentRenewal.memberId + ")");
        addInfoRow(grid, 1, "Plan:", currentRenewal.selectedPlan);
        addInfoRow(grid, 2, "Current Expiry:", currentRenewal.currentExpiry.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        addInfoRow(grid, 3, "New Expiry:", currentRenewal.newExpiry.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        
        Separator separator = new Separator();
        GridPane.setColumnSpan(separator, 2);
        grid.add(separator, 0, 4);
        
        addInfoRow(grid, 5, "Amount Due:", String.format("$%.2f", currentRenewal.totalAmount));
        
        card.getChildren().addAll(cardTitle, grid);
        
        return card;
    }
    
    /**
     * Create payment method card
     */
    private VBox createPaymentMethodCard() {
        VBox card = new VBox(15);
        card.getStyleClass().add("info-card");
        card.setPadding(new Insets(20));
        
        Label cardTitle = new Label("Payment Method");
        cardTitle.getStyleClass().add("card-title");
        cardTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Payment method toggle
        HBox methodToggle = new HBox(15);
        methodToggle.setAlignment(Pos.CENTER_LEFT);
        
        ToggleGroup methodGroup = new ToggleGroup();
        
        RadioButton cardMethod = new RadioButton("Credit/Debit Card");
        cardMethod.setToggleGroup(methodGroup);
        cardMethod.setSelected(true);
        cardMethod.getStyleClass().add("radio-button");
        
        RadioButton cashMethod = new RadioButton("Cash");
        cashMethod.setToggleGroup(methodGroup);
        cashMethod.getStyleClass().add("radio-button");
        
        methodToggle.getChildren().addAll(cardMethod, cashMethod);
        
        // Payment form
        GridPane paymentForm = new GridPane();
        paymentForm.setHgap(15);
        paymentForm.setVgap(15);
        paymentForm.setPadding(new Insets(15, 0, 0, 0));
        
        // Card holder name
        Label nameLabel = new Label("Card Holder Name:");
        nameLabel.getStyleClass().add("form-label");
        TextField nameField = new TextField();
        nameField.setPromptText("John Smith");
        nameField.getStyleClass().add("form-input");
        paymentForm.add(nameLabel, 0, 0);
        paymentForm.add(nameField, 1, 0);
        
        // Card number
        Label cardLabel = new Label("Card Number:");
        cardLabel.getStyleClass().add("form-label");
        TextField cardField = new TextField();
        cardField.setPromptText("1234 5678 9012 3456");
        cardField.getStyleClass().add("form-input");
        paymentForm.add(cardLabel, 0, 1);
        paymentForm.add(cardField, 1, 1);
        
        // Expiry and CVV
        Label expiryLabel = new Label("Expiry:");
        expiryLabel.getStyleClass().add("form-label");
        TextField expiryField = new TextField();
        expiryField.setPromptText("MM/YY");
        expiryField.getStyleClass().add("form-input");
        expiryField.setPrefWidth(100);
        
        Label cvvLabel = new Label("CVV:");
        cvvLabel.getStyleClass().add("form-label");
        TextField cvvField = new TextField();
        cvvField.setPromptText("123");
        cvvField.getStyleClass().add("form-input");
        cvvField.setPrefWidth(80);
        
        HBox expiryBox = new HBox(15, expiryField, cvvLabel, cvvField);
        expiryBox.setAlignment(Pos.CENTER_LEFT);
        
        paymentForm.add(expiryLabel, 0, 2);
        paymentForm.add(expiryBox, 1, 2);
        
        // Security notice
        Label securityNotice = new Label("🔒 Your payment information is secure and encrypted");
        securityNotice.setStyle("-fx-text-fill: #6B7280; -fx-font-size: 12px;");
        
        // Toggle payment form visibility
        cashMethod.setOnAction(e -> paymentForm.setVisible(false));
        cardMethod.setOnAction(e -> paymentForm.setVisible(true));
        
        card.getChildren().addAll(cardTitle, methodToggle, paymentForm, securityNotice);
        
        return card;
    }
    
    /**
     * Create renewal details card
     */
    private VBox createRenewalDetailsCard() {
        VBox card = new VBox(15);
        card.getStyleClass().add("info-card");
        card.setPadding(new Insets(30));
        card.setStyle("-fx-background-color: #F9FAFB; -fx-border-color: #10B981; -fx-border-width: 2;");
        
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(12);
        grid.setAlignment(Pos.CENTER);
        
        addInfoRow(grid, 0, "Member ID:", currentRenewal.memberId);
        addInfoRow(grid, 1, "Name:", currentRenewal.memberName);
        addInfoRow(grid, 2, "Plan:", currentRenewal.selectedPlan);
        addInfoRow(grid, 3, "Amount Paid:", String.format("$%.2f", currentRenewal.totalAmount));
        addInfoRow(grid, 4, "Payment Method:", currentRenewal.paymentMethod);
        addInfoRow(grid, 5, "Transaction ID:", currentRenewal.transactionId);
        addInfoRow(grid, 6, "Previous Expiry:", currentRenewal.currentExpiry.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        addInfoRow(grid, 7, "New Expiry:", currentRenewal.newExpiry.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        
        card.getChildren().add(grid);
        
        return card;
    }
    
    /**
     * Create pricing row
     */
    private HBox createPricingRow(String label, String value) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        
        Label labelText = new Label(label);
        labelText.getStyleClass().add("pricing-label");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label valueText = new Label(value);
        valueText.getStyleClass().add("pricing-value");
        valueText.setStyle("-fx-font-weight: 500;");
        
        row.getChildren().addAll(labelText, spacer, valueText);
        
        return row;
    }
    
    /**
     * Add info row to grid
     */
    private void addInfoRow(GridPane grid, int row, String label, String value) {
        Label labelText = new Label(label);
        labelText.getStyleClass().add("form-label");
        labelText.setStyle("-fx-font-weight: 500; -fx-min-width: 150px;");
        
        Label valueText = new Label(value);
        valueText.setStyle("-fx-font-size: 14px;");
        
        grid.add(labelText, 0, row);
        grid.add(valueText, 1, row);
    }
    
    /**
     * Create navigation buttons
     */
    private HBox createNavigationButtons(String backText, String nextText, 
                                        javafx.event.EventHandler<javafx.event.ActionEvent> backAction,
                                        javafx.event.EventHandler<javafx.event.ActionEvent> nextAction) {
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));
        
        Button backButton = new Button(backText);
        backButton.getStyleClass().add("btn-secondary");
        backButton.setPrefWidth(150);
        backButton.setOnAction(backAction);
        
        Button nextButton = new Button(nextText);
        nextButton.getStyleClass().add("btn-primary");
        nextButton.setPrefWidth(200);
        nextButton.setOnAction(nextAction);
        
        buttonBox.getChildren().addAll(backButton, nextButton);
        
        return buttonBox;
    }
    
    // ==================== WIZARD NAVIGATION ====================
    
    /**
     * Show Step 1 (from Step 2 back)
     */
    private void showStep1_ReviewConfirm() {
        VBox step1 = createStep1_ReviewConfirm();
        renewalProcessView.getChildren().clear();
        renewalProcessView.getChildren().add(step1);
    }
    
    /**
     * Show Step 2: Payment
     */
    private void showStep2_Payment() {
        VBox step2 = createStep2_Payment();
        renewalProcessView.getChildren().clear();
        renewalProcessView.getChildren().add(step2);
    }
    
    /**
     * Process renewal payment
     */
    private void processRenewalPayment() {
        System.out.println("\n=== Processing Renewal Payment ===");
        System.out.println("Member: " + currentRenewal.memberName);
        System.out.println("Amount: $" + String.format("%.2f", currentRenewal.totalAmount));
        
        // Simulate payment processing
        currentRenewal.paymentMethod = "Card";
        currentRenewal.transactionId = "TXN" + System.currentTimeMillis();
        
        // Log complete renewal
        logRenewalCompletion();
        
        // Show confirmation
        VBox step3 = createStep3_Confirmation();
        renewalProcessView.getChildren().clear();
        renewalProcessView.getChildren().add(step3);
    }
    
    /**
     * Log renewal completion
     */
    private void logRenewalCompletion() {
        System.out.println("\n=== Renewal Completed ===");
        System.out.println("Member ID: " + currentRenewal.memberId);
        System.out.println("Name: " + currentRenewal.memberName);
        System.out.println("Email: " + currentRenewal.memberEmail);
        System.out.println("Plan: " + currentRenewal.selectedPlan);
        System.out.println("Amount Paid: $" + String.format("%.2f", currentRenewal.totalAmount));
        System.out.println("Payment Method: " + currentRenewal.paymentMethod);
        System.out.println("Transaction ID: " + currentRenewal.transactionId);
        System.out.println("Previous Expiry: " + currentRenewal.currentExpiry);
        System.out.println("New Expiry: " + currentRenewal.newExpiry);
        System.out.println("Discount Applied: " + String.format("%.0f", currentRenewal.discount * 100) + "%");
        System.out.println("========================\n");
    }
    
    /**
     * Cancel renewal process
     */
    private void cancelRenewal() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Renewal");
        alert.setHeaderText("Are you sure you want to cancel this renewal?");
        alert.setContentText("All entered information will be lost.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                returnToPendingRenewals();
            }
        });
    }
    
    /**
     * Return to pending renewals list
     */
    private void returnToPendingRenewals() {
        currentRenewal = null;
        mainContainer.getChildren().clear();
        mainContainer.getChildren().add(pendingRenewalsView);
    }
    
    /**
     * Print receipt
     */
    private void printReceipt() {
        System.out.println("Printing receipt...");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Print Receipt");
        alert.setHeaderText("Receipt Sent to Printer");
        alert.setContentText("The renewal receipt has been sent to the default printer.");
        alert.showAndWait();
    }
    
    /**
     * Email receipt
     */
    private void emailReceipt() {
        System.out.println("Emailing receipt to: " + currentRenewal.memberEmail);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Email Receipt");
        alert.setHeaderText("Receipt Sent");
        alert.setContentText("The renewal receipt has been emailed to " + currentRenewal.memberEmail);
        alert.showAndWait();
    }
    
    // ==================== UTILITY METHODS ====================
    
    /**
     * Get plan price
     */
    private double getPlanPrice(String plan) {
        return switch (plan) {
            case "Basic" -> 29.99;
            case "Premium" -> 49.99;
            case "Elite" -> 79.99;
            case "Student" -> 24.99;
            case "Senior" -> 19.99;
            default -> 29.99;
        };
    }
    
    /**
     * Generate mock renewal data
     */
    private List<MemberRenewalData> generateMockRenewals() {
        List<MemberRenewalData> renewals = new ArrayList<>();
        Random random = new Random();
        
        String[] names = {"Alice Brown", "Bob Wilson", "Carol Davis", "David Lee", "Emma Thompson",
                         "Frank Miller", "Grace Chen", "Henry Taylor", "Iris Johnson", "Jack Martin",
                         "Kate Anderson", "Leo Garcia"};
        String[] plans = {"Basic", "Premium", "Elite", "Student"};
        
        for (int i = 0; i < 12; i++) {
            MemberRenewalData member = new MemberRenewalData();
            member.memberId = "M" + (1050 + i);
            member.name = names[i];
            member.email = names[i].toLowerCase().replace(" ", ".") + "@email.com";
            member.plan = plans[random.nextInt(plans.length)];
            member.renewalAmount = getPlanPrice(member.plan);
            member.memberYears = 1 + random.nextInt(5);
            
            // Create varied expiry dates
            if (i < 3) {
                // Overdue (negative days)
                member.expiryDate = LocalDate.now().minusDays(3 + random.nextInt(5));
            } else if (i < 8) {
                // Expiring soon (1-7 days)
                member.expiryDate = LocalDate.now().plusDays(1 + random.nextInt(7));
            } else {
                // Future (8-30 days)
                member.expiryDate = LocalDate.now().plusDays(8 + random.nextInt(23));
            }
            
            renewals.add(member);
        }
        
        return renewals;
    }
    
    // ==================== DATA CLASSES ====================
    
    /**
     * Member renewal data
     */
    private static class MemberRenewalData {
        String memberId;
        String name;
        String email;
        String plan;
        LocalDate expiryDate;
        double renewalAmount;
        int memberYears;
        
        String getStatusText() {
            long daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
            if (daysUntil < 0) {
                return "Expired " + Math.abs(daysUntil) + "d ago";
            } else if (daysUntil == 0) {
                return "Expires today";
            } else if (daysUntil == 1) {
                return "Expires tomorrow";
            } else {
                return daysUntil + " days";
            }
        }
        
        String getStatusColor() {
            long daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
            if (daysUntil < 0) {
                return "#EF4444"; // Red - overdue
            } else if (daysUntil <= 7) {
                return "#F59E0B"; // Yellow - expiring soon
            } else {
                return "#10B981"; // Green - active
            }
        }
    }
    
    /**
     * Renewal data for current renewal process
     */
    private static class RenewalData {
        String memberId;
        String memberName;
        String memberEmail;
        String currentPlan;
        LocalDate currentExpiry;
        String selectedPlan;
        double renewalAmount;
        double discount;
        double totalAmount;
        LocalDate newExpiry;
        String paymentMethod;
        String transactionId;
    }
}
