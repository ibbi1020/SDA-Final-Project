/*
 * Block20 Gym Management System  
 * Member Payments Controller - Real Data Integration
 */
package com.block20.controllers.member;

import com.block20.models.Transaction;
import com.block20.services.MemberService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MemberPaymentsController extends ScrollPane {
    
    private static final String STAT_VALUE_STYLE = "-fx-font-size: 28px; -fx-font-weight: 700;";
    
    // Dependencies
    private final String memberId;
    private final MemberService memberService;
    
    // UI Components
    private VBox contentContainer;
    private Label balanceValue;
    private Label nextDueValue;
    private VBox historyList;
    
    public MemberPaymentsController(String memberId, MemberService memberService) {
        this.memberId = memberId;
        this.memberService = memberService;
        initializeView();
        refreshData();
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
        
        contentContainer.getChildren().addAll(
            createHeader(),
            createSummaryCard(),
            createHistoryCard()
            // Removed "Plans Card" temporarily as PaymentPlans are handled by Partner B's logic
        );
        setContent(contentContainer);
    }
    
    private HBox createHeader() {
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);
        VBox titles = new VBox(6);
        Text title = new Text("Payments & Billing");
        title.getStyleClass().add("text-h2");
        Text subtitle = new Text("Track dues and payment history");
        subtitle.getStyleClass().add("text-muted");
        titles.getChildren().addAll(title, subtitle);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button refreshButton = new Button("↻ Refresh");
        refreshButton.getStyleClass().addAll("btn", "btn-secondary");
        refreshButton.setOnAction(e -> refreshData());
        
        header.getChildren().addAll(titles, spacer, refreshButton);
        return header;
    }
    
    private VBox createSummaryCard() {
        VBox card = new VBox(16);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(24));
        
        Text heading = new Text("Account Snapshot");
        heading.getStyleClass().add("text-h3");
        
        HBox stats = new HBox(32);
        stats.setAlignment(Pos.CENTER_LEFT);
        
        // For now, balance is 0.00 as we aren't tracking Debt yet
        balanceValue = createStatBlock(stats, "Outstanding Balance", "$0.00");
        nextDueValue = createStatBlock(stats, "Total Paid (Lifetime)", "$0.00");
        
        card.getChildren().addAll(heading, stats);
        return card;
    }
    
    private Label createStatBlock(HBox parent, String label, String defaultValue) {
        VBox block = new VBox(6);
        Label value = new Label(defaultValue);
        value.getStyleClass().add("stat-value");
        value.setStyle(STAT_VALUE_STYLE);
        Label lbl = new Label(label);
        lbl.getStyleClass().add("text-muted");
        block.getChildren().addAll(value, lbl);
        parent.getChildren().add(block);
        return value;
    }
    
    private VBox createHistoryCard() {
        VBox card = new VBox(16);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(24));
        
        Text heading = new Text("Recent Payments");
        heading.getStyleClass().add("text-h3");
        
        historyList = new VBox(8);
        card.getChildren().addAll(heading, historyList);
        return card;
    }
    
    private void refreshData() {
        // 1. Fetch Real Transactions from SQLite
        List<Transaction> transactions = memberService.getTransactionsForMember(memberId);
        
        // 2. Update History List
        historyList.getChildren().clear();
        
        if (transactions.isEmpty()) {
            Label empty = new Label("No payments recorded yet.");
            empty.getStyleClass().add("text-muted");
            historyList.getChildren().add(empty);
        } else {
            // Sort by date if needed, or assume DB order
            for (Transaction txn : transactions) {
                historyList.getChildren().add(createHistoryRow(txn));
            }
        }
        
        // 3. Update Totals
        double totalPaid = transactions.stream().mapToDouble(Transaction::getAmount).sum();
        nextDueValue.setText(String.format("$%.2f", totalPaid));
        balanceValue.setText("$0.00"); // Placeholder until Invoicing module
    }
    
    private HBox createHistoryRow(Transaction txn) {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 12; -fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #E2E8F0;");
        
        VBox left = new VBox(4);
        Text description = new Text(txn.getType()); // "Enrollment", "Renewal"
        description.setStyle("-fx-font-weight: 600; -fx-font-size: 16px;");
        
        Text dateText = new Text(txn.getDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        dateText.getStyleClass().add("text-caption");
        
        left.getChildren().addAll(description, dateText);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Text amount = new Text(String.format("$%.2f", txn.getAmount()));
        amount.setStyle("-fx-font-weight: 700; -fx-fill: #2563EB; -fx-font-size: 16px;");
        
        Label badge = new Label("Paid");
        badge.getStyleClass().add("badge");
        badge.setStyle("-fx-background-color: #D1FAE5; -fx-text-fill: #065F46;");
        
        row.getChildren().addAll(left, spacer, amount, badge);
        return row;
    }
}