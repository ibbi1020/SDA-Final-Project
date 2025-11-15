/*
 * Block20 Gym Management System
 * Member Membership Controller - Renewal and Plan Management
 */
package com.block20.controllers.member;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;

/**
 * Member Membership Controller - Self-service renewal and plan information
 */
public class MemberMembershipController extends ScrollPane {
    
    private VBox contentContainer;
    private String memberId;
    private Consumer<String> navigationHandler;
    
    // Renewal wizard state
    private String selectedPlan = null;
    private String paymentMethod = null;
    
    // Mock data
    private MembershipData currentMembership;
    
    public MemberMembershipController(String memberId, Consumer<String> navigationHandler) {
        this.memberId = memberId;
        this.navigationHandler = navigationHandler;
        this.currentMembership = generateMockMembershipData();
        initializeView();
    }
    
    private void initializeView() {
        // Configure ScrollPane
        setFitToWidth(true);
        setFitToHeight(false);
        setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        getStyleClass().add("content-scroll-pane");
        
        contentContainer = new VBox(24);
        contentContainer.setPadding(new Insets(32));
        contentContainer.getStyleClass().add("main-content");
        
        contentContainer.getChildren().addAll(
            createHeader(),
            createCurrentMembershipSection(),
            createRenewalSection(),
            createAvailablePlansSection()
        );
        
        setContent(contentContainer);
    }
    
    /**
     * Create header
     */
    private VBox createHeader() {
        VBox header = new VBox(8);
        
        Text title = new Text("My Membership");
        title.getStyleClass().add("text-h2");
        
        Text subtitle = new Text("Manage your membership and renew your plan");
        subtitle.getStyleClass().add("text-muted");
        
        header.getChildren().addAll(title, subtitle);
        return header;
    }
    
    /**
     * Create current membership section
     */
    private VBox createCurrentMembershipSection() {
        VBox section = new VBox(16);
        
        Text sectionTitle = new Text("Current Membership");
        sectionTitle.getStyleClass().add("text-h3");
        
        VBox card = new VBox(20);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(24));
        
        GridPane grid = new GridPane();
        grid.setHgap(40);
        grid.setVgap(16);
        
        grid.add(createInfoItem("Plan", currentMembership.plan), 0, 0);
        grid.add(createInfoItem("Status", currentMembership.status), 1, 0);
        grid.add(createInfoItem("Start Date", currentMembership.startDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))), 0, 1);
        grid.add(createInfoItem("Expiry Date", currentMembership.expiryDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))), 1, 1);
        
        long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), currentMembership.expiryDate);
        grid.add(createInfoItem("Days Remaining", daysRemaining + " days"), 0, 2);
        grid.add(createInfoItem("Monthly Fee", "$" + String.format("%.2f", currentMembership.monthlyFee)), 1, 2);
        
        card.getChildren().add(grid);
        section.getChildren().addAll(sectionTitle, card);
        return section;
    }
    
    /**
     * Create renewal section
     */
    private VBox createRenewalSection() {
        VBox section = new VBox(16);
        
        Text sectionTitle = new Text("Renew Membership");
        sectionTitle.getStyleClass().add("text-h3");
        
        VBox card = new VBox(20);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(24));
        
        // Renewal benefits
        VBox benefits = new VBox(12);
        
        Text benefitsTitle = new Text("Renewal Benefits");
        benefitsTitle.getStyleClass().add("text-body");
        benefitsTitle.setStyle("-fx-font-weight: 600;");
        
        VBox benefitsList = new VBox(8);
        benefitsList.getChildren().addAll(
            createBenefitItem("✓ 10% early renewal discount (30+ days before expiry)"),
            createBenefitItem("✓ 5% loyalty discount (3+ consecutive years)"),
            createBenefitItem("✓ No interruption to your gym access"),
            createBenefitItem("✓ Same membership ID and benefits")
        );
        
        benefits.getChildren().addAll(benefitsTitle, benefitsList);
        
        // Calculate discount
        long daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), currentMembership.expiryDate);
        double discount = 0.0;
        if (daysUntil >= 30) {
            discount += 0.10; // Early renewal
        }
        if (currentMembership.memberYears >= 3) {
            discount += 0.05; // Loyalty
        }
        
        // Renewal summary
        VBox summary = new VBox(12);
        HBox priceRow = new HBox(12);
        priceRow.setAlignment(Pos.CENTER_LEFT);
        
        Text priceLabel = new Text("Renewal Price:");
        priceLabel.getStyleClass().add("text-body");
        
        double originalPrice = currentMembership.monthlyFee;
        double discountedPrice = originalPrice * (1 - discount);
        
        if (discount > 0) {
            Text originalPriceText = new Text("$" + String.format("%.2f", originalPrice));
            originalPriceText.getStyleClass().add("text-body");
            originalPriceText.setStyle("-fx-strikethrough: true; -fx-fill: -fx-gray-500;");
            
            Text discountedPriceText = new Text("$" + String.format("%.2f", discountedPrice));
            discountedPriceText.getStyleClass().add("text-h3");
            discountedPriceText.setStyle("-fx-fill: -fx-success-600;");
            
            Label saveLabel = new Label("SAVE " + (int)(discount * 100) + "%");
            saveLabel.getStyleClass().addAll("badge", "badge-success");
            
            priceRow.getChildren().addAll(priceLabel, originalPriceText, discountedPriceText, saveLabel);
        } else {
            Text priceText = new Text("$" + String.format("%.2f", originalPrice));
            priceText.getStyleClass().add("text-h3");
            
            priceRow.getChildren().addAll(priceLabel, priceText);
        }
        
        Button renewButton = new Button("Renew Now");
        renewButton.getStyleClass().addAll("primary-button", "button-large");
        renewButton.setPrefWidth(200);
        renewButton.setOnAction(e -> showRenewalWizard());
        
        summary.getChildren().addAll(priceRow, renewButton);
        
        card.getChildren().addAll(benefits, new Separator(), summary);
        section.getChildren().addAll(sectionTitle, card);
        return section;
    }
    
    /**
     * Create available plans section
     */
    private VBox createAvailablePlansSection() {
        VBox section = new VBox(16);
        
        Text sectionTitle = new Text("Available Plans");
        sectionTitle.getStyleClass().add("text-h3");
        
        FlowPane plansGrid = new FlowPane(16, 16);
        
        plansGrid.getChildren().addAll(
            createPlanCard("Basic", 29.99, "Off-peak hours access", "5 classes/month", ""),
            createPlanCard("Premium", 49.99, "24/7 access", "Unlimited classes", "1 guest pass/month"),
            createPlanCard("Student", 24.99, "All hours access", "10 classes/month", "Must show student ID"),
            createPlanCard("Senior", 19.99, "All hours access", "10 classes/month", "Age 60+ required")
        );
        
        section.getChildren().addAll(sectionTitle, plansGrid);
        return section;
    }
    
    /**
     * Create plan card
     */
    private VBox createPlanCard(String planName, double price, String... features) {
        VBox card = new VBox(16);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(24));
        card.setMaxWidth(280);
        card.setMinWidth(220);
        
        // Header
        VBox header = new VBox(8);
        header.setAlignment(Pos.CENTER);
        
        Text name = new Text(planName);
        name.getStyleClass().add("text-h3");
        
        HBox priceBox = new HBox(4);
        priceBox.setAlignment(Pos.BASELINE_CENTER);
        
        Text priceValue = new Text("$" + String.format("%.0f", price));
        priceValue.getStyleClass().add("text-h2");
        priceValue.setStyle("-fx-fill: -fx-primary-500;");
        
        Text pricePeriod = new Text("/month");
        pricePeriod.getStyleClass().add("text-body-sm");
        pricePeriod.setStyle("-fx-fill: -fx-gray-600;");
        
        priceBox.getChildren().addAll(priceValue, pricePeriod);
        
        header.getChildren().addAll(name, priceBox);
        
        // Features
        VBox featuresList = new VBox(8);
        for (String feature : features) {
            if (!feature.isEmpty()) {
                Text featureText = new Text("• " + feature);
                featureText.getStyleClass().add("text-body-sm");
                featuresList.getChildren().add(featureText);
            }
        }
        
        // Button
        Button selectButton = new Button("Select Plan");
        selectButton.getStyleClass().addAll("secondary-button");
        selectButton.setPrefWidth(Double.MAX_VALUE);
        
        // Highlight current plan
        if (planName.equals(currentMembership.plan.replace(" Monthly", ""))) {
            card.setStyle("-fx-border-color: -fx-primary-500; -fx-border-width: 2;");
            selectButton.setText("Current Plan");
            selectButton.setDisable(true);
        }
        
        card.getChildren().addAll(header, new Separator(), featuresList, selectButton);
        return card;
    }
    
    /**
     * Create benefit item
     */
    private HBox createBenefitItem(String text) {
        HBox item = new HBox(8);
        item.setAlignment(Pos.CENTER_LEFT);
        
        Text benefitText = new Text(text);
        benefitText.getStyleClass().add("text-body-sm");
        benefitText.setStyle("-fx-fill: -fx-gray-700;");
        
        item.getChildren().add(benefitText);
        return item;
    }
    
    /**
     * Create info item
     */
    private VBox createInfoItem(String label, String value) {
        VBox item = new VBox(4);
        
        Text labelText = new Text(label);
        labelText.getStyleClass().add("text-caption");
        labelText.setStyle("-fx-fill: -fx-gray-600;");
        
        Text valueText = new Text(value);
        valueText.getStyleClass().add("text-body");
        valueText.setStyle("-fx-font-weight: 600;");
        
        item.getChildren().addAll(labelText, valueText);
        return item;
    }
    
    /**
     * Show renewal wizard dialog
     */
    private void showRenewalWizard() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Renew Membership");
        dialog.setHeaderText("Complete your membership renewal");
        
        // Create renewal form
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setPrefWidth(500);
        
        // Plan confirmation
        VBox planSection = new VBox(8);
        Text planLabel = new Text("Renewing Plan:");
        planLabel.getStyleClass().add("text-body");
        planLabel.setStyle("-fx-font-weight: 600;");
        
        Text planValue = new Text(currentMembership.plan);
        planValue.getStyleClass().add("text-h4");
        planValue.setStyle("-fx-fill: -fx-primary-500;");
        
        planSection.getChildren().addAll(planLabel, planValue);
        
        // Payment method
        VBox paymentSection = new VBox(12);
        Text paymentLabel = new Text("Payment Method:");
        paymentLabel.getStyleClass().add("text-body");
        paymentLabel.setStyle("-fx-font-weight: 600;");
        
        ToggleGroup paymentGroup = new ToggleGroup();
        
        RadioButton cardPayment = new RadioButton("Credit/Debit Card");
        cardPayment.setToggleGroup(paymentGroup);
        cardPayment.setSelected(true);
        
        RadioButton cashPayment = new RadioButton("Pay at Front Desk");
        cashPayment.setToggleGroup(paymentGroup);
        
        paymentSection.getChildren().addAll(paymentLabel, cardPayment, cashPayment);
        
        // Summary
        VBox summarySection = new VBox(8);
        summarySection.setStyle("-fx-background-color: -fx-gray-50; -fx-padding: 16; -fx-background-radius: 8;");
        
        long daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), currentMembership.expiryDate);
        double discount = 0.0;
        if (daysUntil >= 30) discount += 0.10;
        if (currentMembership.memberYears >= 3) discount += 0.05;
        
        double amount = currentMembership.monthlyFee * (1 - discount);
        
        Text summaryTitle = new Text("Payment Summary");
        summaryTitle.getStyleClass().add("text-body");
        summaryTitle.setStyle("-fx-font-weight: 600;");
        
        Text amountText = new Text("Amount Due: $" + String.format("%.2f", amount));
        amountText.getStyleClass().add("text-h3");
        
        Text newExpiryText = new Text("New Expiry: " + currentMembership.expiryDate.plusMonths(1).format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        newExpiryText.getStyleClass().add("text-body-sm");
        newExpiryText.setStyle("-fx-fill: -fx-gray-600;");
        
        summarySection.getChildren().addAll(summaryTitle, amountText, newExpiryText);
        
        content.getChildren().addAll(planSection, new Separator(), paymentSection, summarySection);
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        Button confirmButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        confirmButton.setText("Confirm Renewal");
        confirmButton.getStyleClass().add("primary-button");
        
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                showRenewalConfirmation();
            }
        });
    }
    
    /**
     * Show renewal confirmation
     */
    private void showRenewalConfirmation() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Renewal Successful");
        alert.setHeaderText("Your membership has been renewed!");
        alert.setContentText("Thank you for renewing your membership. Your new expiry date is " + 
            currentMembership.expiryDate.plusMonths(1).format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")) + 
            ".\n\nA confirmation email has been sent to your registered email address.");
        alert.showAndWait();
        
        // Navigate back to dashboard
        navigationHandler.accept("dashboard");
    }
    
    /**
     * Generate mock membership data
     */
    private MembershipData generateMockMembershipData() {
        MembershipData data = new MembershipData();
        data.plan = "Premium Monthly";
        data.status = "Active";
        data.startDate = LocalDate.now().minusMonths(1);
        data.expiryDate = LocalDate.now().plusDays(15);
        data.monthlyFee = 49.99;
        data.memberYears = 2;
        return data;
    }
    
    /**
     * Membership data class
     */
    private static class MembershipData {
        String plan;
        String status;
        LocalDate startDate;
        LocalDate expiryDate;
        double monthlyFee;
        int memberYears;
    }
}
