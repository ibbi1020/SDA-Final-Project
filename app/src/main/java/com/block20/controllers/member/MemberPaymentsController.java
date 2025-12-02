/*
 * Block20 Gym Management System  
 * Member Payments Controller - Real Data Integration
 */
package com.block20.controllers.member;

import com.block20.models.PaymentPlan;
import com.block20.models.Transaction;
import com.block20.services.MemberService;
import com.block20.services.PaymentService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Comparator;

public class MemberPaymentsController extends ScrollPane {
    
    private static final String STAT_VALUE_STYLE = "-fx-font-size: 28px; -fx-font-weight: 700;";
    
    // Dependencies
    private final String memberId;
    private final MemberService memberService;
    private final PaymentService paymentService;
    
    // UI Components
    private VBox contentContainer;
    private Label balanceValue;
    private Label totalPaidValue;
    private VBox historyList;
    private VBox planList;
    
    public MemberPaymentsController(String memberId,
                                    MemberService memberService,
                                    PaymentService paymentService) {
        this.memberId = memberId;
        this.memberService = memberService;
        this.paymentService = paymentService;
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
            createHistoryCard(),
            createPlansCard()
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
        
        balanceValue = createStatBlock(stats, "Outstanding Balance", "$0.00");
        totalPaidValue = createStatBlock(stats, "Total Paid (Lifetime)", "$0.00");
        
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

    private VBox createPlansCard() {
        VBox card = new VBox(16);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(24));

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Text heading = new Text("Payment Plans");
        heading.getStyleClass().add("text-h3");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label hint = new Label("Installments auto-sync with the billing desk");
        hint.getStyleClass().add("text-muted");
        header.getChildren().addAll(heading, spacer, hint);

        planList = new VBox(12);
        planList.getChildren().add(new Label("Loading plans..."));

        card.getChildren().addAll(header, planList);
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
        totalPaidValue.setText(String.format("$%.2f", totalPaid));

        double outstanding = paymentService != null
            ? paymentService.getOutstandingBalance(memberId)
            : 0.0;
        balanceValue.setText(String.format("$%.2f", outstanding));

        List<PaymentPlan> plans = paymentService != null
            ? paymentService.getActivePlans(memberId)
            : List.of();
        updatePlanList(plans);
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

    private void updatePlanList(List<PaymentPlan> plans) {
        planList.getChildren().clear();
        if (plans.isEmpty()) {
            Label empty = new Label("No payment plans are active. All dues are up to date.");
            empty.getStyleClass().add("text-muted");
            planList.getChildren().add(empty);
            return;
        }
        for (PaymentPlan plan : plans) {
            planList.getChildren().add(createPlanCard(plan));
        }
    }

    private VBox createPlanCard(PaymentPlan plan) {
        VBox card = new VBox(10);
        card.getStyleClass().add("plan-card");
        card.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: #E2E8F0; -fx-border-radius: 8; -fx-padding: 16;");

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        Text title = new Text("Plan " + plan.getPlanId());
        title.setStyle("-fx-font-weight: 600;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label status = new Label(plan.getStatus());
        status.getStyleClass().add("badge");
        header.getChildren().addAll(title, spacer, status);

        double outstanding = plan.getOutstandingAmount();
        long totalInstallments = plan.getInstallments().size();
        long paidInstallments = plan.getInstallments().stream().filter(PaymentPlan.Installment::isPaid).count();
        PaymentPlan.Installment nextDue = plan.getInstallments().stream()
                .filter(i -> !i.isPaid())
                .min(Comparator.comparing(PaymentPlan.Installment::getDueDate))
                .orElse(null);

        Label amountLabel = new Label(String.format("Outstanding: $%.2f", outstanding));
        amountLabel.setStyle("-fx-font-weight: 600; -fx-font-size: 14px;");

        String nextDueText = nextDue != null
                ? String.format("Next Due: %s ($%.2f)",
                    nextDue.getDueDate().format(DateTimeFormatter.ofPattern("MMM dd")),
                    nextDue.getAmount())
                : "Plan Paid In Full";
        Label nextDueLabel = new Label(nextDueText);
        nextDueLabel.getStyleClass().add("text-muted");

        Label progress = new Label(
                String.format("Installments: %d / %d paid", paidInstallments, totalInstallments));
        progress.getStyleClass().add("text-muted");

        card.getChildren().addAll(header, amountLabel, nextDueLabel, progress);
        return card;
    }
}