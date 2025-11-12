package com.block20.controllers.enrollment;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

/**
 * Complete Enrollment Wizard Controller
 * Implements 5-step enrollment flow: Member Info → Plan Selection → Review → Payment → Confirmation
 */
public class EnrollmentController {
    private VBox mainContainer;
    private StackPane contentArea;
    private HBox navigationButtons;
    private HBox progressIndicator;
    
    private int currentStep = 1;
    private final int totalSteps = 5;
    
    // Form data storage
    private EnrollmentData enrollmentData;
    private Consumer<String> navigationHandler;
    
    public EnrollmentController(Consumer<String> navigationHandler) {
        this.navigationHandler = navigationHandler;
        this.enrollmentData = new EnrollmentData();
        initialize();
    }

    private void initialize() {
        mainContainer = new VBox(0);
        mainContainer.getStyleClass().add("enrollment-wizard");
        
        mainContainer.getChildren().addAll(
            createHeader(),
            createProgressIndicator(),
            createContentArea(),
            createNavigationButtons()
        );
        
        showStep(1);
    }

    private VBox createHeader() {
        VBox header = new VBox(8);
        header.getStyleClass().add("wizard-header");
        header.setPadding(new Insets(32, 32, 24, 32));
        header.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-width: 0 0 1 0;");
        
        Text title = new Text("New Member Enrollment");
        title.getStyleClass().add("text-h2");
        
        Text subtitle = new Text("Complete the enrollment process to activate new membership");
        subtitle.getStyleClass().add("text-muted");
        
        header.getChildren().addAll(title, subtitle);
        return header;
    }

    private HBox createProgressIndicator() {
        progressIndicator = new HBox(0);
        progressIndicator.setAlignment(Pos.CENTER);
        progressIndicator.setPadding(new Insets(24, 32, 24, 32));
        progressIndicator.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-width: 0 0 1 0;");
        
        String[] stepLabels = {
            "Member Information",
            "Plan Selection",
            "Review & Pricing",
            "Payment Processing",
            "Confirmation"
        };
        
        for (int i = 0; i < totalSteps; i++) {
            final int stepNum = i + 1;
            HBox step = createProgressStep(stepNum, stepLabels[i]);
            progressIndicator.getChildren().add(step);
            
            if (i < totalSteps - 1) {
                Region connector = new Region();
                connector.setPrefWidth(60);
                connector.setPrefHeight(2);
                connector.setStyle("-fx-background-color: #E2E8F0;");
                connector.getStyleClass().add("progress-connector");
                progressIndicator.getChildren().add(connector);
            }
        }
        
        return progressIndicator;
    }

    private HBox createProgressStep(int stepNumber, String label) {
        HBox step = new HBox(12);
        step.setAlignment(Pos.CENTER);
        step.getStyleClass().add("progress-step");
        
        // Circle with number
        StackPane circle = new StackPane();
        circle.setPrefSize(40, 40);
        circle.setStyle("-fx-background-color: #E2E8F0; -fx-background-radius: 20;");
        circle.getStyleClass().add("progress-circle");
        
        Text number = new Text(String.valueOf(stepNumber));
        number.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-fill: #64748B;");
        circle.getChildren().add(number);
        
        // Label
        VBox labelBox = new VBox(4);
        Text stepLabel = new Text("Step " + stepNumber);
        stepLabel.getStyleClass().add("text-caption");
        stepLabel.setStyle("-fx-fill: #94A3B8;");
        
        Text stepName = new Text(label);
        stepName.getStyleClass().add("text-body");
        stepName.setStyle("-fx-font-weight: 600; -fx-fill: #64748B;");
        
        labelBox.getChildren().addAll(stepLabel, stepName);
        
        step.getChildren().addAll(circle, labelBox);
        return step;
    }

    private StackPane createContentArea() {
        contentArea = new StackPane();
        contentArea.setPadding(new Insets(32));
        contentArea.setStyle("-fx-background-color: #F8FAFC;");
        VBox.setVgrow(contentArea, Priority.ALWAYS);
        return contentArea;
    }

    private HBox createNavigationButtons() {
        navigationButtons = new HBox(12);
        navigationButtons.setAlignment(Pos.CENTER_RIGHT);
        navigationButtons.setPadding(new Insets(20, 32, 32, 32));
        navigationButtons.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-width: 1 0 0 0;");
        
        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().addAll("btn", "btn-ghost");
        cancelButton.setOnAction(e -> handleCancel());
        
        Button backButton = new Button("← Back");
        backButton.getStyleClass().addAll("btn", "btn-secondary");
        backButton.setOnAction(e -> previousStep());
        
        Button nextButton = new Button("Continue →");
        nextButton.getStyleClass().addAll("btn", "btn-primary");
        nextButton.setOnAction(e -> nextStep());
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        navigationButtons.getChildren().addAll(cancelButton, spacer, backButton, nextButton);
        return navigationButtons;
    }

    private void showStep(int step) {
        currentStep = step;
        contentArea.getChildren().clear();
        updateProgressIndicator();
        updateNavigationButtons();
        
        VBox stepContent = switch (step) {
            case 1 -> createStep1_MemberInfo();
            case 2 -> createStep2_PlanSelection();
            case 3 -> createStep3_ReviewPricing();
            case 4 -> createStep4_Payment();
            case 5 -> createStep5_Confirmation();
            default -> new VBox();
        };
        
        ScrollPane scrollPane = new ScrollPane(stepContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        
        contentArea.getChildren().add(scrollPane);
    }

    private void updateProgressIndicator() {
        int childIndex = 0;
        for (int i = 0; i < totalSteps; i++) {
            HBox step = (HBox) progressIndicator.getChildren().get(childIndex);
            StackPane circle = (StackPane) step.getChildren().get(0);
            VBox labelBox = (VBox) step.getChildren().get(1);
            Text stepName = (Text) labelBox.getChildren().get(1);
            
            if (i + 1 < currentStep) {
                // Completed
                circle.setStyle("-fx-background-color: #10B981; -fx-background-radius: 20;");
                ((Text) circle.getChildren().get(0)).setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-fill: white;");
                ((Text) circle.getChildren().get(0)).setText("✓");
                stepName.setStyle("-fx-font-weight: 600; -fx-fill: #10B981;");
            } else if (i + 1 == currentStep) {
                // Current
                circle.setStyle("-fx-background-color: #2563EB; -fx-background-radius: 20;");
                ((Text) circle.getChildren().get(0)).setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-fill: white;");
                ((Text) circle.getChildren().get(0)).setText(String.valueOf(i + 1));
                stepName.setStyle("-fx-font-weight: 600; -fx-fill: #2563EB;");
            } else {
                // Upcoming
                circle.setStyle("-fx-background-color: #E2E8F0; -fx-background-radius: 20;");
                ((Text) circle.getChildren().get(0)).setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-fill: #64748B;");
                ((Text) circle.getChildren().get(0)).setText(String.valueOf(i + 1));
                stepName.setStyle("-fx-font-weight: 600; -fx-fill: #64748B;");
            }
            
            childIndex++;
            if (i < totalSteps - 1) {
                Region connector = (Region) progressIndicator.getChildren().get(childIndex);
                if (i + 1 < currentStep) {
                    connector.setStyle("-fx-background-color: #10B981;");
                } else {
                    connector.setStyle("-fx-background-color: #E2E8F0;");
                }
                childIndex++;
            }
        }
    }

    private void updateNavigationButtons() {
        Button backButton = (Button) navigationButtons.getChildren().get(2);
        Button nextButton = (Button) navigationButtons.getChildren().get(3);
        
        backButton.setDisable(currentStep == 1);
        
        if (currentStep == totalSteps) {
            nextButton.setText("Complete Enrollment");
            nextButton.getStyleClass().clear();
            nextButton.getStyleClass().addAll("btn", "btn-success");
        } else if (currentStep == 4) {
            nextButton.setText("Process Payment →");
        } else {
            nextButton.setText("Continue →");
            nextButton.getStyleClass().clear();
            nextButton.getStyleClass().addAll("btn", "btn-primary");
        }
    }

    // STEP 1: Member Information
    private VBox createStep1_MemberInfo() {
        VBox container = new VBox(24);
        container.setMaxWidth(800);
        container.setAlignment(Pos.TOP_CENTER);
        
        VBox card = new VBox(24);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(32));
        
        Text cardTitle = new Text("Member Information");
        cardTitle.getStyleClass().add("text-h3");
        
        Text cardSubtitle = new Text("Provide personal details for the new member");
        cardSubtitle.getStyleClass().add("text-muted");
        
        Separator separator = new Separator();
        
        // Personal Information Section
        Text personalTitle = new Text("Personal Details");
        personalTitle.getStyleClass().add("text-h5");
        
        GridPane personalGrid = new GridPane();
        personalGrid.setHgap(16);
        personalGrid.setVgap(16);
        
        // Full Name
        Label nameLabel = new Label("Full Name *");
        nameLabel.getStyleClass().add("form-label");
        TextField nameField = new TextField();
        nameField.setPromptText("Enter full name");
        nameField.getStyleClass().add("form-input");
        nameField.setText(enrollmentData.fullName);
        nameField.textProperty().addListener((obs, old, val) -> enrollmentData.fullName = val);
        
        // Date of Birth
        Label dobLabel = new Label("Date of Birth *");
        dobLabel.getStyleClass().add("form-label");
        DatePicker dobPicker = new DatePicker();
        dobPicker.setPromptText("Select date");
        dobPicker.getStyleClass().add("form-input");
        if (enrollmentData.dateOfBirth != null) {
            dobPicker.setValue(enrollmentData.dateOfBirth);
        }
        dobPicker.valueProperty().addListener((obs, old, val) -> enrollmentData.dateOfBirth = val);
        
        // Email
        Label emailLabel = new Label("Email Address *");
        emailLabel.getStyleClass().add("form-label");
        TextField emailField = new TextField();
        emailField.setPromptText("member@example.com");
        emailField.getStyleClass().add("form-input");
        emailField.setText(enrollmentData.email);
        emailField.textProperty().addListener((obs, old, val) -> enrollmentData.email = val);
        
        // Phone
        Label phoneLabel = new Label("Phone Number *");
        phoneLabel.getStyleClass().add("form-label");
        TextField phoneField = new TextField();
        phoneField.setPromptText("555-0100");
        phoneField.getStyleClass().add("form-input");
        phoneField.setText(enrollmentData.phone);
        phoneField.textProperty().addListener((obs, old, val) -> enrollmentData.phone = val);
        
        // Address
        Label addressLabel = new Label("Address");
        addressLabel.getStyleClass().add("form-label");
        TextArea addressArea = new TextArea();
        addressArea.setPromptText("Street address, city, state, zip");
        addressArea.setPrefRowCount(2);
        addressArea.getStyleClass().add("form-input");
        addressArea.setText(enrollmentData.address);
        addressArea.textProperty().addListener((obs, old, val) -> enrollmentData.address = val);
        
        personalGrid.add(nameLabel, 0, 0);
        personalGrid.add(nameField, 0, 1);
        personalGrid.add(dobLabel, 1, 0);
        personalGrid.add(dobPicker, 1, 1);
        personalGrid.add(emailLabel, 0, 2);
        personalGrid.add(emailField, 0, 3);
        personalGrid.add(phoneLabel, 1, 2);
        personalGrid.add(phoneField, 1, 3);
        personalGrid.add(addressLabel, 0, 4, 2, 1);
        personalGrid.add(addressArea, 0, 5, 2, 1);
        
        // Emergency Contact Section
        Text emergencyTitle = new Text("Emergency Contact");
        emergencyTitle.getStyleClass().add("text-h5");
        
        GridPane emergencyGrid = new GridPane();
        emergencyGrid.setHgap(16);
        emergencyGrid.setVgap(16);
        
        Label emergencyNameLabel = new Label("Contact Name *");
        emergencyNameLabel.getStyleClass().add("form-label");
        TextField emergencyNameField = new TextField();
        emergencyNameField.setPromptText("Full name");
        emergencyNameField.getStyleClass().add("form-input");
        emergencyNameField.setText(enrollmentData.emergencyContactName);
        emergencyNameField.textProperty().addListener((obs, old, val) -> enrollmentData.emergencyContactName = val);
        
        Label emergencyPhoneLabel = new Label("Contact Phone *");
        emergencyPhoneLabel.getStyleClass().add("form-label");
        TextField emergencyPhoneField = new TextField();
        emergencyPhoneField.setPromptText("555-0100");
        emergencyPhoneField.getStyleClass().add("form-input");
        emergencyPhoneField.setText(enrollmentData.emergencyContactPhone);
        emergencyPhoneField.textProperty().addListener((obs, old, val) -> enrollmentData.emergencyContactPhone = val);
        
        Label relationshipLabel = new Label("Relationship *");
        relationshipLabel.getStyleClass().add("form-label");
        ComboBox<String> relationshipCombo = new ComboBox<>();
        relationshipCombo.getItems().addAll("Spouse", "Parent", "Sibling", "Child", "Friend", "Other");
        relationshipCombo.setPromptText("Select relationship");
        relationshipCombo.getStyleClass().add("form-input");
        if (enrollmentData.emergencyRelationship != null) {
            relationshipCombo.setValue(enrollmentData.emergencyRelationship);
        }
        relationshipCombo.valueProperty().addListener((obs, old, val) -> enrollmentData.emergencyRelationship = val);
        
        emergencyGrid.add(emergencyNameLabel, 0, 0);
        emergencyGrid.add(emergencyNameField, 0, 1);
        emergencyGrid.add(emergencyPhoneLabel, 1, 0);
        emergencyGrid.add(emergencyPhoneField, 1, 1);
        emergencyGrid.add(relationshipLabel, 0, 2);
        emergencyGrid.add(relationshipCombo, 0, 3);
        
        card.getChildren().addAll(
            cardTitle,
            cardSubtitle,
            separator,
            personalTitle,
            personalGrid,
            new Separator(),
            emergencyTitle,
            emergencyGrid
        );
        
        container.getChildren().add(card);
        return container;
    }

    // STEP 2: Plan Selection
    private VBox createStep2_PlanSelection() {
        VBox container = new VBox(24);
        container.setMaxWidth(1000);
        container.setAlignment(Pos.TOP_CENTER);
        
        Text title = new Text("Select Membership Plan");
        title.getStyleClass().add("text-h3");
        
        Text subtitle = new Text("Choose the plan that best fits your needs");
        subtitle.getStyleClass().add("text-muted");
        
        // Plan cards in grid
        GridPane plansGrid = new GridPane();
        plansGrid.setHgap(20);
        plansGrid.setVgap(20);
        
        plansGrid.add(createPlanCard("Basic", "29.99", "Off-peak access", 
            new String[]{"Access during off-peak hours", "Standard equipment", "Locker room access", "Mobile app"}), 0, 0);
        plansGrid.add(createPlanCard("Premium", "49.99", "Full access", 
            new String[]{"24/7 facility access", "All equipment", "Group classes", "Guest passes (2/month)", "Mobile app"}), 1, 0);
        plansGrid.add(createPlanCard("Elite", "79.99", "Premium + Training", 
            new String[]{"Everything in Premium", "Personal training (4 sessions/month)", "Nutrition consultation", "Priority booking", "Spa access"}), 0, 1);
        plansGrid.add(createPlanCard("Student", "24.99", "Special discount", 
            new String[]{"Valid student ID required", "Off-peak access", "Standard equipment", "Study lounge access"}), 1, 1);
        
        container.getChildren().addAll(title, subtitle, plansGrid);
        return container;
    }

    private VBox createPlanCard(String planName, String price, String description, String[] features) {
        VBox card = new VBox(16);
        card.getStyleClass().add("plan-card");
        card.setPadding(new Insets(24));
        card.setPrefWidth(300);
        card.setStyle("-fx-background-color: white; -fx-border-color: #E2E8F0; -fx-border-width: 2; -fx-border-radius: 12; -fx-background-radius: 12;");
        
        boolean isSelected = planName.equals(enrollmentData.selectedPlan);
        if (isSelected) {
            card.setStyle("-fx-background-color: #EFF6FF; -fx-border-color: #2563EB; -fx-border-width: 2; -fx-border-radius: 12; -fx-background-radius: 12;");
        }
        
        // Header
        Text name = new Text(planName);
        name.getStyleClass().add("text-h4");
        
        Text desc = new Text(description);
        desc.getStyleClass().add("text-caption");
        desc.setStyle("-fx-fill: #64748B;");
        
        // Price
        HBox priceBox = new HBox(4);
        priceBox.setAlignment(Pos.BASELINE_LEFT);
        Text dollar = new Text("$");
        dollar.setStyle("-fx-font-size: 24px; -fx-font-weight: 700; -fx-fill: #2563EB;");
        Text amount = new Text(price);
        amount.setStyle("-fx-font-size: 36px; -fx-font-weight: 700; -fx-fill: #2563EB;");
        Text period = new Text("/month");
        period.getStyleClass().add("text-body");
        period.setStyle("-fx-fill: #64748B;");
        priceBox.getChildren().addAll(dollar, amount, period);
        
        Separator separator = new Separator();
        
        // Features
        VBox featuresBox = new VBox(12);
        for (String feature : features) {
            HBox featureRow = new HBox(8);
            featureRow.setAlignment(Pos.CENTER_LEFT);
            Text checkmark = new Text("✓");
            checkmark.setStyle("-fx-font-size: 16px; -fx-fill: #10B981;");
            Text featureText = new Text(feature);
            featureText.getStyleClass().add("text-body");
            featureRow.getChildren().addAll(checkmark, featureText);
            featuresBox.getChildren().add(featureRow);
        }
        
        // Select button
        Button selectButton = new Button(isSelected ? "✓ Selected" : "Select Plan");
        selectButton.getStyleClass().addAll("btn", isSelected ? "btn-success" : "btn-primary");
        selectButton.setPrefWidth(Double.MAX_VALUE);
        selectButton.setOnAction(e -> {
            enrollmentData.selectedPlan = planName;
            enrollmentData.planPrice = Double.parseDouble(price);
            showStep(2); // Refresh to show selection
        });
        
        card.getChildren().addAll(name, desc, priceBox, separator, featuresBox, selectButton);
        card.setOnMouseClicked(e -> selectButton.fire());
        card.setStyle(card.getStyle() + " -fx-cursor: hand;");
        
        return card;
    }

    // STEP 3: Review & Pricing
    private VBox createStep3_ReviewPricing() {
        VBox container = new VBox(24);
        container.setMaxWidth(800);
        container.setAlignment(Pos.TOP_CENTER);
        
        Text title = new Text("Review & Confirm Details");
        title.getStyleClass().add("text-h3");
        
        // Member Info Card
        VBox memberCard = new VBox(16);
        memberCard.getStyleClass().add("card");
        memberCard.setPadding(new Insets(24));
        
        Text memberTitle = new Text("Member Information");
        memberTitle.getStyleClass().add("text-h5");
        
        GridPane memberGrid = new GridPane();
        memberGrid.setHgap(16);
        memberGrid.setVgap(12);
        
        addReviewRow(memberGrid, 0, "Full Name:", enrollmentData.fullName);
        addReviewRow(memberGrid, 1, "Date of Birth:", enrollmentData.dateOfBirth != null ? enrollmentData.dateOfBirth.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) : "");
        addReviewRow(memberGrid, 2, "Email:", enrollmentData.email);
        addReviewRow(memberGrid, 3, "Phone:", enrollmentData.phone);
        addReviewRow(memberGrid, 4, "Emergency Contact:", enrollmentData.emergencyContactName + " (" + enrollmentData.emergencyRelationship + ") - " + enrollmentData.emergencyContactPhone);
        
        memberCard.getChildren().addAll(memberTitle, new Separator(), memberGrid);
        
        // Plan Info Card
        VBox planCard = new VBox(16);
        planCard.getStyleClass().add("card");
        planCard.setPadding(new Insets(24));
        
        Text planTitle = new Text("Membership Plan");
        planTitle.getStyleClass().add("text-h5");
        
        HBox planInfoBox = new HBox(16);
        planInfoBox.setAlignment(Pos.CENTER_LEFT);
        
        VBox planDetails = new VBox(4);
        Text planName = new Text(enrollmentData.selectedPlan + " Plan");
        planName.getStyleClass().add("text-h4");
        Text planDesc = new Text("Monthly membership");
        planDesc.getStyleClass().add("text-muted");
        planDetails.getChildren().addAll(planName, planDesc);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Text planPrice = new Text("$" + String.format("%.2f", enrollmentData.planPrice) + "/month");
        planPrice.setStyle("-fx-font-size: 24px; -fx-font-weight: 700; -fx-fill: #2563EB;");
        
        planInfoBox.getChildren().addAll(planDetails, spacer, planPrice);
        
        planCard.getChildren().addAll(planTitle, new Separator(), planInfoBox);
        
        // Pricing Breakdown Card
        VBox pricingCard = new VBox(16);
        pricingCard.getStyleClass().add("card");
        pricingCard.setPadding(new Insets(24));
        pricingCard.setStyle("-fx-background-color: #F8FAFC;");
        
        Text pricingTitle = new Text("Pricing Breakdown");
        pricingTitle.getStyleClass().add("text-h5");
        
        VBox pricingItems = new VBox(12);
        
        // Calculate dates and pricing
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(1);
        enrollmentData.startDate = startDate;
        enrollmentData.expiryDate = endDate;
        
        double subtotal = enrollmentData.planPrice;
        double tax = subtotal * 0.08; // 8% tax
        double total = subtotal + tax;
        
        enrollmentData.totalAmount = total;
        
        addPricingRow(pricingItems, "Membership Fee", "$" + String.format("%.2f", subtotal));
        addPricingRow(pricingItems, "Start Date", startDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        addPricingRow(pricingItems, "First Renewal", endDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        pricingItems.getChildren().add(new Separator());
        addPricingRow(pricingItems, "Subtotal", "$" + String.format("%.2f", subtotal));
        addPricingRow(pricingItems, "Tax (8%)", "$" + String.format("%.2f", tax));
        pricingItems.getChildren().add(new Separator());
        
        HBox totalRow = new HBox();
        totalRow.setAlignment(Pos.CENTER_LEFT);
        Text totalLabel = new Text("Total Due Today");
        totalLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: 700;");
        Region totalSpacer = new Region();
        HBox.setHgrow(totalSpacer, Priority.ALWAYS);
        Text totalAmount = new Text("$" + String.format("%.2f", total));
        totalAmount.setStyle("-fx-font-size: 24px; -fx-font-weight: 700; -fx-fill: #2563EB;");
        totalRow.getChildren().addAll(totalLabel, totalSpacer, totalAmount);
        pricingItems.getChildren().add(totalRow);
        
        pricingCard.getChildren().addAll(pricingTitle, new Separator(), pricingItems);
        
        container.getChildren().addAll(title, memberCard, planCard, pricingCard);
        return container;
    }

    private void addReviewRow(GridPane grid, int row, String label, String value) {
        Text labelText = new Text(label);
        labelText.getStyleClass().add("text-body");
        labelText.setStyle("-fx-fill: #64748B; -fx-font-weight: 600;");
        
        Text valueText = new Text(value != null ? value : "");
        valueText.getStyleClass().add("text-body");
        
        grid.add(labelText, 0, row);
        grid.add(valueText, 1, row);
    }

    private void addPricingRow(VBox container, String label, String value) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        
        Text labelText = new Text(label);
        labelText.getStyleClass().add("text-body");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Text valueText = new Text(value);
        valueText.getStyleClass().add("text-body");
        valueText.setStyle("-fx-font-weight: 600;");
        
        row.getChildren().addAll(labelText, spacer, valueText);
        container.getChildren().add(row);
    }

    // STEP 4: Payment Processing
    private VBox createStep4_Payment() {
        VBox container = new VBox(24);
        container.setMaxWidth(600);
        container.setAlignment(Pos.TOP_CENTER);
        
        Text title = new Text("Payment Information");
        title.getStyleClass().add("text-h3");
        
        Text subtitle = new Text("Amount due: $" + String.format("%.2f", enrollmentData.totalAmount));
        subtitle.setStyle("-fx-font-size: 24px; -fx-font-weight: 700; -fx-fill: #2563EB;");
        
        VBox card = new VBox(24);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(32));
        
        // Payment method selection
        Text methodTitle = new Text("Payment Method");
        methodTitle.getStyleClass().add("text-h5");
        
        ToggleGroup paymentMethodGroup = new ToggleGroup();
        
        HBox methodBox = new HBox(16);
        
        RadioButton cardRadio = new RadioButton("Credit/Debit Card");
        cardRadio.setToggleGroup(paymentMethodGroup);
        cardRadio.setSelected(true);
        cardRadio.getStyleClass().add("radio-button");
        
        RadioButton cashRadio = new RadioButton("Cash");
        cashRadio.setToggleGroup(paymentMethodGroup);
        cashRadio.getStyleClass().add("radio-button");
        
        methodBox.getChildren().addAll(cardRadio, cashRadio);
        
        // Card payment form
        VBox cardForm = new VBox(16);
        
        Label cardNumberLabel = new Label("Card Number *");
        cardNumberLabel.getStyleClass().add("form-label");
        TextField cardNumberField = new TextField();
        cardNumberField.setPromptText("1234 5678 9012 3456");
        cardNumberField.getStyleClass().add("form-input");
        cardNumberField.textProperty().addListener((obs, old, val) -> enrollmentData.cardNumber = val);
        
        Label cardNameLabel = new Label("Cardholder Name *");
        cardNameLabel.getStyleClass().add("form-label");
        TextField cardNameField = new TextField();
        cardNameField.setPromptText("Name on card");
        cardNameField.getStyleClass().add("form-input");
        cardNameField.textProperty().addListener((obs, old, val) -> enrollmentData.cardholderName = val);
        
        GridPane cardDetailsGrid = new GridPane();
        cardDetailsGrid.setHgap(16);
        cardDetailsGrid.setVgap(16);
        
        Label expiryLabel = new Label("Expiry Date *");
        expiryLabel.getStyleClass().add("form-label");
        TextField expiryField = new TextField();
        expiryField.setPromptText("MM/YY");
        expiryField.getStyleClass().add("form-input");
        expiryField.textProperty().addListener((obs, old, val) -> enrollmentData.cardExpiry = val);
        
        Label cvvLabel = new Label("CVV *");
        cvvLabel.getStyleClass().add("form-label");
        TextField cvvField = new TextField();
        cvvField.setPromptText("123");
        cvvField.getStyleClass().add("form-input");
        cvvField.textProperty().addListener((obs, old, val) -> enrollmentData.cardCVV = val);
        
        cardDetailsGrid.add(expiryLabel, 0, 0);
        cardDetailsGrid.add(expiryField, 0, 1);
        cardDetailsGrid.add(cvvLabel, 1, 0);
        cardDetailsGrid.add(cvvField, 1, 1);
        
        cardForm.getChildren().addAll(
            cardNumberLabel, cardNumberField,
            cardNameLabel, cardNameField,
            cardDetailsGrid
        );
        
        // Cash payment note
        VBox cashNote = new VBox(12);
        cashNote.setVisible(false);
        cashNote.setStyle("-fx-background-color: #FEF3C7; -fx-padding: 16; -fx-background-radius: 8;");
        Text cashNoteIcon = new Text("ℹ️ Cash Payment");
        cashNoteIcon.setStyle("-fx-font-weight: 600;");
        Text cashNoteText = new Text("Receipt will be generated upon payment confirmation.");
        cashNote.getChildren().addAll(cashNoteIcon, cashNoteText);
        
        // Toggle between card and cash
        paymentMethodGroup.selectedToggleProperty().addListener((obs, old, newVal) -> {
            if (newVal == cardRadio) {
                cardForm.setVisible(true);
                cardForm.setManaged(true);
                cashNote.setVisible(false);
                cashNote.setManaged(false);
                enrollmentData.paymentMethod = "Card";
            } else {
                cardForm.setVisible(false);
                cardForm.setManaged(false);
                cashNote.setVisible(true);
                cashNote.setManaged(true);
                enrollmentData.paymentMethod = "Cash";
            }
        });
        
        // Security notice
        HBox securityBox = new HBox(8);
        securityBox.setStyle("-fx-background-color: #F0FDF4; -fx-padding: 12; -fx-background-radius: 6;");
        Text lockIcon = new Text("🔒");
        Text securityText = new Text("Your payment information is encrypted and secure");
        securityText.getStyleClass().add("text-caption");
        securityText.setStyle("-fx-fill: #059669;");
        securityBox.getChildren().addAll(lockIcon, securityText);
        
        card.getChildren().addAll(
            methodTitle,
            methodBox,
            new Separator(),
            cardForm,
            cashNote,
            securityBox
        );
        
        container.getChildren().addAll(title, subtitle, card);
        return container;
    }

    // STEP 5: Confirmation
    private VBox createStep5_Confirmation() {
        VBox container = new VBox(32);
        container.setMaxWidth(700);
        container.setAlignment(Pos.TOP_CENTER);
        
        // Success icon
        StackPane successIcon = new StackPane();
        successIcon.setPrefSize(100, 100);
        successIcon.setStyle("-fx-background-color: #D1FAE5; -fx-background-radius: 50;");
        Text checkmark = new Text("✓");
        checkmark.setStyle("-fx-font-size: 48px; -fx-fill: #10B981;");
        successIcon.getChildren().add(checkmark);
        
        Text title = new Text("Enrollment Complete!");
        title.getStyleClass().add("text-h2");
        title.setStyle("-fx-fill: #10B981;");
        
        Text subtitle = new Text("Welcome to Block20 Gym!");
        subtitle.getStyleClass().add("text-body");
        subtitle.setStyle("-fx-fill: #64748B;");
        
        // Member card
        VBox memberCard = new VBox(20);
        memberCard.getStyleClass().add("card");
        memberCard.setPadding(new Insets(32));
        memberCard.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #667EEA, #764BA2);");
        
        // Generate member ID
        enrollmentData.memberId = "M" + String.format("%04d", (int)(Math.random() * 10000));
        
        Text memberIdLabel = new Text("Member ID");
        memberIdLabel.setStyle("-fx-fill: white; -fx-font-size: 12px;");
        Text memberId = new Text(enrollmentData.memberId);
        memberId.setStyle("-fx-fill: white; -fx-font-size: 32px; -fx-font-weight: 700;");
        
        Text memberName = new Text(enrollmentData.fullName);
        memberName.setStyle("-fx-fill: white; -fx-font-size: 20px; -fx-font-weight: 600;");
        
        HBox planBadge = new HBox(8);
        planBadge.setAlignment(Pos.CENTER_LEFT);
        Text planIcon = new Text("⭐");
        Text planText = new Text(enrollmentData.selectedPlan + " Plan");
        planText.setStyle("-fx-fill: white; -fx-font-size: 14px;");
        planBadge.getChildren().addAll(planIcon, planText);
        
        Text validityText = new Text("Valid until: " + enrollmentData.expiryDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        validityText.setStyle("-fx-fill: rgba(255, 255, 255, 0.8); -fx-font-size: 12px;");
        
        memberCard.getChildren().addAll(memberIdLabel, memberId, memberName, planBadge, validityText);
        
        // Next steps
        VBox nextStepsCard = new VBox(16);
        nextStepsCard.getStyleClass().add("card");
        nextStepsCard.setPadding(new Insets(24));
        
        Text nextStepsTitle = new Text("Next Steps");
        nextStepsTitle.getStyleClass().add("text-h5");
        
        VBox steps = new VBox(12);
        steps.getChildren().addAll(
            createNextStepItem("1", "Credentials have been sent to " + enrollmentData.email),
            createNextStepItem("2", "Download the Block20 mobile app to access your digital membership card"),
            createNextStepItem("3", "Visit the gym to activate your access and get a facility tour")
        );
        
        nextStepsCard.getChildren().addAll(nextStepsTitle, new Separator(), steps);
        
        // Action buttons
        HBox actionButtons = new HBox(12);
        actionButtons.setAlignment(Pos.CENTER);
        
        Button printButton = new Button("🖨 Print Member Card");
        printButton.getStyleClass().addAll("btn", "btn-secondary");
        printButton.setOnAction(e -> System.out.println("Print member card"));
        
        Button emailButton = new Button("📧 Email Receipt");
        emailButton.getStyleClass().addAll("btn", "btn-secondary");
        emailButton.setOnAction(e -> System.out.println("Email receipt to: " + enrollmentData.email));
        
        Button doneButton = new Button("✓ Done");
        doneButton.getStyleClass().addAll("btn", "btn-success");
        doneButton.setOnAction(e -> {
            // Save enrollment data locally
            saveEnrollment();
            // Navigate to member registry
            navigationHandler.accept("members-registry");
        });
        
        actionButtons.getChildren().addAll(printButton, emailButton, doneButton);
        
        container.getChildren().addAll(
            successIcon,
            title,
            subtitle,
            memberCard,
            nextStepsCard,
            actionButtons
        );
        
        return container;
    }

    private HBox createNextStepItem(String number, String text) {
        HBox item = new HBox(12);
        item.setAlignment(Pos.TOP_LEFT);
        
        StackPane numberCircle = new StackPane();
        numberCircle.setPrefSize(28, 28);
        numberCircle.setStyle("-fx-background-color: #EFF6FF; -fx-background-radius: 14;");
        Text numberText = new Text(number);
        numberText.setStyle("-fx-font-size: 14px; -fx-font-weight: 600; -fx-fill: #2563EB;");
        numberCircle.getChildren().add(numberText);
        
        Text stepText = new Text(text);
        stepText.getStyleClass().add("text-body");
        stepText.setWrappingWidth(500);
        
        item.getChildren().addAll(numberCircle, stepText);
        return item;
    }

    private void nextStep() {
        if (validateCurrentStep()) {
            if (currentStep < totalSteps) {
                showStep(currentStep + 1);
            } else {
                // Complete enrollment
                saveEnrollment();
                navigationHandler.accept("members-registry");
            }
        }
    }

    private void previousStep() {
        if (currentStep > 1) {
            showStep(currentStep - 1);
        }
    }

    private boolean validateCurrentStep() {
        switch (currentStep) {
            case 1:
                if (enrollmentData.fullName == null || enrollmentData.fullName.trim().isEmpty()) {
                    showAlert("Please enter member's full name");
                    return false;
                }
                if (enrollmentData.email == null || enrollmentData.email.trim().isEmpty()) {
                    showAlert("Please enter email address");
                    return false;
                }
                if (enrollmentData.phone == null || enrollmentData.phone.trim().isEmpty()) {
                    showAlert("Please enter phone number");
                    return false;
                }
                if (enrollmentData.emergencyContactName == null || enrollmentData.emergencyContactName.trim().isEmpty()) {
                    showAlert("Please enter emergency contact name");
                    return false;
                }
                return true;
                
            case 2:
                if (enrollmentData.selectedPlan == null) {
                    showAlert("Please select a membership plan");
                    return false;
                }
                return true;
                
            case 3:
                return true; // Review step, no validation needed
                
            case 4:
                if ("Card".equals(enrollmentData.paymentMethod)) {
                    if (enrollmentData.cardNumber == null || enrollmentData.cardNumber.trim().isEmpty()) {
                        showAlert("Please enter card number");
                        return false;
                    }
                    if (enrollmentData.cardholderName == null || enrollmentData.cardholderName.trim().isEmpty()) {
                        showAlert("Please enter cardholder name");
                        return false;
                    }
                }
                // Simulate payment processing
                processPayment();
                return true;
                
            default:
                return true;
        }
    }

    private void processPayment() {
        // Simulate payment processing
        System.out.println("Processing payment...");
        System.out.println("Payment Method: " + enrollmentData.paymentMethod);
        System.out.println("Amount: $" + String.format("%.2f", enrollmentData.totalAmount));
        
        // In real implementation, this would call payment gateway
        enrollmentData.paymentStatus = "Completed";
        enrollmentData.transactionId = "TXN" + System.currentTimeMillis();
    }

    private void saveEnrollment() {
        System.out.println("=== Enrollment Completed ===");
        System.out.println("Member ID: " + enrollmentData.memberId);
        System.out.println("Name: " + enrollmentData.fullName);
        System.out.println("Email: " + enrollmentData.email);
        System.out.println("Phone: " + enrollmentData.phone);
        System.out.println("Plan: " + enrollmentData.selectedPlan);
        System.out.println("Amount Paid: $" + String.format("%.2f", enrollmentData.totalAmount));
        System.out.println("Payment Method: " + enrollmentData.paymentMethod);
        System.out.println("Transaction ID: " + enrollmentData.transactionId);
        System.out.println("Start Date: " + enrollmentData.startDate);
        System.out.println("Expiry Date: " + enrollmentData.expiryDate);
        System.out.println("========================");
        
        // TODO: Save to database/file system
    }

    private void handleCancel() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Enrollment");
        alert.setHeaderText("Are you sure you want to cancel?");
        alert.setContentText("All entered information will be lost.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                navigationHandler.accept("dashboard");
            }
        });
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validation Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public VBox getView() {
        return mainContainer;
    }

    // Enrollment data class
    private static class EnrollmentData {
        // Step 1: Member Info
        String fullName;
        LocalDate dateOfBirth;
        String email;
        String phone;
        String address;
        String emergencyContactName;
        String emergencyContactPhone;
        String emergencyRelationship;
        
        // Step 2: Plan Selection
        String selectedPlan;
        double planPrice;
        
        // Step 3: Pricing
        LocalDate startDate;
        LocalDate expiryDate;
        double totalAmount;
        
        // Step 4: Payment
        String paymentMethod = "Card";
        String cardNumber;
        String cardholderName;
        String cardExpiry;
        String cardCVV;
        String paymentStatus;
        String transactionId;
        
        // Step 5: Confirmation
        String memberId;
    }
}
