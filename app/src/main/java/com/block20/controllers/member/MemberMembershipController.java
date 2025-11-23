/*
 * Block20 Gym Management System
 * Member Membership Controller - Real Backend Integration
 */
package com.block20.controllers.member;

import com.block20.models.Member;
import com.block20.services.MemberService;

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
    private MemberService memberService;
    
    // Renewal wizard state
    private String selectedPlan = null;
    
    // Data storage (View Model)
    private MembershipData currentMembership;
    
    public MemberMembershipController(String memberId, Consumer<String> navigationHandler, MemberService memberService) {
        this.memberId = memberId;
        this.navigationHandler = navigationHandler;
        this.memberService = memberService;
        
        loadRealData(); // Fetch from DB
        initializeView();
    }
    
    private void loadRealData() {
        Member member = memberService.getMemberById(memberId);
        if (member == null) {
            System.err.println("Error: Member not found for ID " + memberId);
            return;
        }

        this.currentMembership = new MembershipData();
        this.currentMembership.plan = member.getPlanType();
        this.currentMembership.status = member.getStatus();
        this.currentMembership.startDate = member.getJoinDate();
        this.currentMembership.expiryDate = member.getExpiryDate();
        
        // Calculate derived fields
        this.currentMembership.monthlyFee = getPlanPrice(member.getPlanType());
        this.currentMembership.memberYears = (int) ChronoUnit.YEARS.between(member.getJoinDate(), LocalDate.now());
    }

    private void initializeView() {
        setFitToWidth(true);
        setFitToHeight(false);
        setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        getStyleClass().add("content-scroll-pane");
        
        contentContainer = new VBox(24);
        contentContainer.setPadding(new Insets(32));
        contentContainer.getStyleClass().add("main-content");
        
        if (currentMembership != null) {
            contentContainer.getChildren().addAll(
                createHeader(),
                createCurrentMembershipSection(),
                createRenewalSection(),
                createAvailablePlansSection()
            );
        } else {
            contentContainer.getChildren().add(new Label("Membership data unavailable."));
        }
        
        setContent(contentContainer);
    }
    
    private VBox createHeader() {
        VBox header = new VBox(8);
        Text title = new Text("My Membership");
        title.getStyleClass().add("text-h2");
        Text subtitle = new Text("Manage your membership and renew your plan");
        subtitle.getStyleClass().add("text-muted");
        header.getChildren().addAll(title, subtitle);
        return header;
    }
    
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
        String daysText = daysRemaining < 0 ? "Expired (" + Math.abs(daysRemaining) + " days ago)" : daysRemaining + " days";
        grid.add(createInfoItem("Time Remaining", daysText), 0, 2);
        grid.add(createInfoItem("Monthly Fee", "$" + String.format("%.2f", currentMembership.monthlyFee)), 1, 2);
        
        card.getChildren().add(grid);
        section.getChildren().addAll(sectionTitle, card);
        return section;
    }
    
    private VBox createRenewalSection() {
        VBox section = new VBox(16);
        Text sectionTitle = new Text("Renew Membership");
        sectionTitle.getStyleClass().add("text-h3");
        
        VBox card = new VBox(20);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(24));
        
        VBox benefits = new VBox(12);
        Text benefitsTitle = new Text("Renewal Benefits");
        benefitsTitle.getStyleClass().add("text-body");
        benefitsTitle.setStyle("-fx-font-weight: 600;");
        
        VBox benefitsList = new VBox(8);
        benefitsList.getChildren().addAll(
            createBenefitItem("✓ No interruption to your gym access"),
            createBenefitItem("✓ Keep your current rate lock"),
            createBenefitItem("✓ Instant activation")
        );
        benefits.getChildren().addAll(benefitsTitle, benefitsList);
        
        VBox summary = new VBox(12);
        HBox priceRow = new HBox(12);
        priceRow.setAlignment(Pos.CENTER_LEFT);
        Text priceLabel = new Text("Renewal Price:");
        priceLabel.getStyleClass().add("text-body");
        Text priceText = new Text("$" + String.format("%.2f", currentMembership.monthlyFee));
        priceText.getStyleClass().add("text-h3");
        priceRow.getChildren().addAll(priceLabel, priceText);
        
        Button renewButton = new Button("Renew Now");
        renewButton.getStyleClass().addAll("primary-button", "button-large");
        renewButton.setPrefWidth(200);
        renewButton.setOnAction(e -> showRenewalWizard());
        
        summary.getChildren().addAll(priceRow, renewButton);
        card.getChildren().addAll(benefits, new Separator(), summary);
        section.getChildren().addAll(sectionTitle, card);
        return section;
    }
    
    private VBox createAvailablePlansSection() {
        VBox section = new VBox(16);
        Text sectionTitle = new Text("Available Plans");
        sectionTitle.getStyleClass().add("text-h3");
        
        FlowPane plansGrid = new FlowPane(16, 16);
        plansGrid.getChildren().addAll(
            createPlanCard("Basic", 29.99, "Off-peak hours access"),
            createPlanCard("Premium", 49.99, "24/7 access, Classes"),
            createPlanCard("Student", 24.99, "Valid ID required"),
            createPlanCard("Senior", 19.99, "Age 60+ required")
        );
        
        section.getChildren().addAll(sectionTitle, plansGrid);
        return section;
    }
    
    private VBox createPlanCard(String planName, double price, String feature) {
        VBox card = new VBox(16);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(24));
        card.setMaxWidth(280);
        card.setMinWidth(220);
        
        VBox header = new VBox(8);
        header.setAlignment(Pos.CENTER);
        Text name = new Text(planName); name.getStyleClass().add("text-h3");
        Text priceVal = new Text("$" + String.format("%.0f", price)); priceVal.getStyleClass().add("text-h2");
        priceVal.setStyle("-fx-fill: -fx-primary-500;");
        header.getChildren().addAll(name, priceVal);
        
        Text featText = new Text("• " + feature);
        featText.getStyleClass().add("text-body-sm");
        
        Button selectButton = new Button("Select Plan");
        selectButton.getStyleClass().addAll("secondary-button");
        selectButton.setPrefWidth(Double.MAX_VALUE);
        
        if (planName.equals(currentMembership.plan)) {
            card.setStyle("-fx-border-color: -fx-primary-500; -fx-border-width: 2;");
            selectButton.setText("Current Plan");
            selectButton.setDisable(true);
        }
        
        card.getChildren().addAll(header, new Separator(), featText, selectButton);
        return card;
    }
    
    private void showRenewalWizard() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Renew Membership");
        dialog.setHeaderText("Complete your membership renewal");
        
        VBox content = new VBox(20);
        content.setPadding(new Insets(20));
        content.setPrefWidth(500);
        
        VBox planSection = new VBox(8);
        Text planLabel = new Text("Renewing Plan:"); planLabel.setStyle("-fx-font-weight: 600;");
        Text planValue = new Text(currentMembership.plan); planValue.getStyleClass().add("text-h4");
        planSection.getChildren().addAll(planLabel, planValue);
        
        VBox paymentSection = new VBox(12);
        Text payLabel = new Text("Payment Method:"); payLabel.setStyle("-fx-font-weight: 600;");
        ToggleGroup grp = new ToggleGroup();
        RadioButton card = new RadioButton("Credit/Debit Card"); card.setToggleGroup(grp); card.setSelected(true);
        RadioButton cash = new RadioButton("Pay at Front Desk"); cash.setToggleGroup(grp);
        paymentSection.getChildren().addAll(payLabel, card, cash);
        
        VBox summary = new VBox(8);
        summary.setStyle("-fx-background-color: -fx-gray-50; -fx-padding: 16; -fx-background-radius: 8;");
        Text sumTitle = new Text("Payment Summary"); sumTitle.setStyle("-fx-font-weight: 600;");
        Text amount = new Text("Amount Due: $" + String.format("%.2f", currentMembership.monthlyFee));
        amount.getStyleClass().add("text-h3");
        
        // Calculate new date logic
        LocalDate newDate = currentMembership.expiryDate.isBefore(LocalDate.now()) 
            ? LocalDate.now().plusMonths(1) 
            : currentMembership.expiryDate.plusMonths(1);
            
        Text next = new Text("New Expiry: " + newDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        summary.getChildren().addAll(sumTitle, amount, next);
        
        content.getChildren().addAll(planSection, new Separator(), paymentSection, summary);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                processRenewal();
            }
        });
    }
    
    private void processRenewal() {
        try {
            // 1. Call Backend
            // Note: memberService.renewMembership extends by 1 month automatically
            // It also records the Transaction in SQLite
            memberService.renewMembership(memberId, currentMembership.plan);
            
            // 2. Refresh Data
            loadRealData();
            
            // 3. Show Success
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Renewal Complete");
            alert.setContentText("Your membership has been extended until " + 
                currentMembership.expiryDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
            alert.showAndWait();
            
            // 4. Refresh View
            contentContainer.getChildren().clear();
            initializeView();
            
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Renewal Failed: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    // Helper Methods
    private HBox createBenefitItem(String text) {
        HBox item = new HBox(8); item.setAlignment(Pos.CENTER_LEFT);
        Text t = new Text(text); t.getStyleClass().add("text-body-sm");
        item.getChildren().add(t); return item;
    }
    
    private VBox createInfoItem(String label, String value) {
        VBox item = new VBox(4);
        Text l = new Text(label); l.getStyleClass().add("text-caption"); l.setStyle("-fx-fill: -fx-gray-600;");
        Text v = new Text(value); v.getStyleClass().add("text-body"); v.setStyle("-fx-font-weight: 600;");
        item.getChildren().addAll(l, v); return item;
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
    
    // View Model
    private static class MembershipData {
        String plan;
        String status;
        LocalDate startDate;
        LocalDate expiryDate;
        double monthlyFee;
        int memberYears;
    }
}