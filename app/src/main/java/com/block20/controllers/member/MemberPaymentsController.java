/*
 * Block20 Gym Management System  
 * Member Payments Controller
 */
package com.block20.controllers.member;

import com.block20.models.PaymentPlan;
import com.block20.models.PaymentReceipt;
import com.block20.services.PaymentService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MemberPaymentsController extends ScrollPane {
    private static final String STAT_VALUE_STYLE = "-fx-font-size: 28px; -fx-font-weight: 700;";
    private final PaymentService paymentService;
    private final String memberId;
    private VBox contentContainer;
    private Label balanceValue;
    private Label nextDueValue;
    private Label overdueValue;
    private VBox historyList;
    private VBox plansList;
    private List<PaymentReceipt> paymentHistory = List.of();
    private List<PaymentPlan> activePlans = List.of();
    
    public MemberPaymentsController(String memberId, PaymentService paymentService) {
        this.memberId = memberId;
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
        Text subtitle = new Text("Track dues, payment history, and plans in one place");
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
        nextDueValue = createStatBlock(stats, "Next Installment Due", "—");
        overdueValue = createStatBlock(stats, "Overdue Installments", "0 • $0.00");
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
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);
        Text heading = new Text("Payment Plans");
        heading.getStyleClass().add("text-h3");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button addPlanButton = new Button("+ Payment Plan");
        addPlanButton.getStyleClass().addAll("btn", "btn-primary");
        addPlanButton.setDisable(paymentService == null);
        addPlanButton.setOnAction(e -> showCreatePlanDialog());
        header.getChildren().addAll(heading, spacer, addPlanButton);
        plansList = new VBox(12);
        card.getChildren().addAll(header, plansList);
        return card;
    }
    
    private void refreshData() {
        if (paymentService == null) {
            showAlert("Payments service unavailable", "This demo build cannot load billing data. Try again later.");
            return;
        }
        this.paymentHistory = paymentService.getPaymentsForMember(memberId);
        this.activePlans = paymentService.getActivePlans(memberId);
        updateSummary();
        populateHistory();
        populatePlans();
    }
    
    private void updateSummary() {
        double outstanding = paymentService.getOutstandingBalance(memberId);
        balanceValue.setText(String.format("$%.2f", outstanding));
        nextDueValue.setText(getNextInstallmentText());
        OverdueSummary overdue = calculateOverdueSummary();
        overdueValue.setText(String.format("%d • $%.2f", overdue.count, overdue.amount));
        overdueValue.setStyle(overdue.count > 0
            ? STAT_VALUE_STYLE + " -fx-text-fill: #B91C1C;"
            : STAT_VALUE_STYLE);
    }
    
    private String getNextInstallmentText() {
        LocalDate nextDue = null;
        double nextAmount = 0.0;
        for (PaymentPlan plan : activePlans) {
            for (PaymentPlan.Installment installment : plan.getInstallments()) {
                if (!installment.isPaid() && (nextDue == null || installment.getDueDate().isBefore(nextDue))) {
                    nextDue = installment.getDueDate();
                    nextAmount = installment.getAmount();
                }
            }
        }
        if (nextDue == null) {
            return "All clear";
        }
        return String.format("%s ($%.2f)", nextDue.format(DateTimeFormatter.ofPattern("MMM dd")), nextAmount);
    }

    private OverdueSummary calculateOverdueSummary() {
        LocalDate today = LocalDate.now();
        int count = 0;
        double amount = 0.0;
        for (PaymentPlan plan : activePlans) {
            for (PaymentPlan.Installment installment : plan.getInstallments()) {
                if (!installment.isPaid() && installment.getDueDate().isBefore(today)) {
                    count++;
                    amount += installment.getAmount();
                }
            }
        }
        return new OverdueSummary(count, amount);
    }
    
    private void populateHistory() {
        historyList.getChildren().clear();
        if (paymentHistory.isEmpty()) {
            Label empty = new Label("No payments recorded yet.");
            empty.getStyleClass().add("text-muted");
            historyList.getChildren().add(empty);
            return;
        }
        for (PaymentReceipt receipt : paymentHistory) {
            historyList.getChildren().add(createHistoryRow(receipt));
        }
    }
    
    private HBox createHistoryRow(PaymentReceipt receipt) {
        HBox row = new HBox(16);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 12; -fx-background-color: -fx-surface; -fx-background-radius: 8;");
        VBox left = new VBox(4);
        String timestamp = receipt.getProcessedAt() != null
            ? receipt.getProcessedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
            : "Pending";
        Text dateText = new Text(timestamp);
        dateText.getStyleClass().add("text-caption");
        Text description = new Text(receipt.getDescription());
        description.getStyleClass().add("text-body");
        left.getChildren().addAll(description, dateText);
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Text amount = new Text(String.format("$%.2f", receipt.getTotal()));
        amount.setStyle("-fx-font-weight: 700;");
        Label badge = new Label(receipt.getMethod() + " • " + receipt.getStatus());
        badge.getStyleClass().add("badge");
        row.getChildren().addAll(left, spacer, amount, badge);
        return row;
    }
    
    private void populatePlans() {
        plansList.getChildren().clear();
        if (activePlans.isEmpty()) {
            Label empty = new Label("No active payment plans. Use the button above to create one.");
            empty.getStyleClass().add("text-muted");
            plansList.getChildren().add(empty);
            return;
        }
        for (PaymentPlan plan : activePlans) {
            plansList.getChildren().add(createPlanCard(plan));
        }
    }
    
    private VBox createPlanCard(PaymentPlan plan) {
        LocalDate today = LocalDate.now();
        VBox card = new VBox(12);
        card.setStyle("-fx-border-color: #E2E8F0; -fx-border-radius: 8; -fx-padding: 16; -fx-background-radius: 8; -fx-background-color: white;");
        Text planTitle = new Text(String.format("Plan %s", plan.getPlanId()));
        planTitle.getStyleClass().add("text-h5");
        boolean hasOverdue = plan.getOverdueInstallments(today).size() > 0;
        Label status = new Label(hasOverdue ? "Overdue" : plan.getStatus());
        status.getStyleClass().add("badge");
        if ("COMPLETED".equalsIgnoreCase(plan.getStatus())) {
            status.getStyleClass().add("badge-success");
        } else if (hasOverdue) {
            status.getStyleClass().add("badge-error");
        } else {
            status.getStyleClass().add("badge-info");
        }
        HBox header = new HBox(12, planTitle, status);
        header.setAlignment(Pos.CENTER_LEFT);
        Label created = new Label("Created " + plan.getCreatedOn().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        created.getStyleClass().add("text-muted");
        Label remaining = new Label(String.format("Outstanding: $%.2f", plan.getOutstandingAmount()));
        remaining.setStyle("-fx-font-weight: 600;");
        VBox installmentList = new VBox(6);
        for (PaymentPlan.Installment installment : plan.getInstallments()) {
            boolean overdue = !installment.isPaid() && installment.getDueDate().isBefore(today);
            HBox line = new HBox(8);
            Text due = new Text(installment.getDueDate().format(DateTimeFormatter.ofPattern("MMM dd")));
            due.getStyleClass().add("text-body");
            if (overdue) {
                due.setStyle("-fx-font-weight: 600; -fx-text-fill: #B91C1C;");
            }
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            Text amount = new Text(String.format("$%.2f", installment.getAmount()));
            Label badge = new Label(installment.isPaid() ? "Paid" : overdue ? "Overdue" : "Scheduled");
            badge.getStyleClass().add("badge");
            if (installment.isPaid()) {
                badge.getStyleClass().add("badge-success");
            } else if (overdue) {
                badge.getStyleClass().add("badge-error");
            } else {
                badge.getStyleClass().add("badge-warning");
            }
            line.getChildren().addAll(due, spacer, amount, badge);
            installmentList.getChildren().add(line);
        }
        Button recordPaymentBtn = new Button("Mark Next Installment Paid");
        recordPaymentBtn.getStyleClass().add("btn-secondary");
        recordPaymentBtn.setDisable(plan.getOutstandingAmount() == 0);
        recordPaymentBtn.setOnAction(e -> recordNextInstallment(plan));
        card.getChildren().addAll(header, created, remaining, installmentList, recordPaymentBtn);
        return card;
    }
    
    private void recordNextInstallment(PaymentPlan plan) {
        PaymentPlan.Installment next = plan.getInstallments().stream()
            .filter(inst -> !inst.isPaid())
            .findFirst()
            .orElse(null);
        if (next == null) {
            showAlert("Nothing to apply", "All installments on this plan are marked paid.");
            return;
        }
        try {
            paymentService.recordInstallmentPayment(plan.getPlanId(), next.getInstallmentId(), null);
            refreshData();
        } catch (Exception ex) {
            showAlert("Unable to record payment", ex.getMessage());
        }
    }
    
    private void showCreatePlanDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Create Payment Plan");
        dialog.setHeaderText("Split a balance into installments");
        ButtonType createBtn = new ButtonType("Create Plan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createBtn, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(10));
        TextField amountField = new TextField();
        amountField.setPromptText("Total amount");
        Spinner<Integer> installmentsField = new Spinner<>(2, 12, 3);
        DatePicker firstDuePicker = new DatePicker(LocalDate.now().plusWeeks(1));
        grid.addRow(0, new Label("Total Amount"), amountField);
        grid.addRow(1, new Label("Installments"), installmentsField);
        grid.addRow(2, new Label("First Due Date"), firstDuePicker);
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(btn -> btn);
        dialog.showAndWait().ifPresent(result -> {
            if (result == createBtn) {
                try {
                    double amount = Double.parseDouble(amountField.getText());
                    if (amount <= 0) {
                        throw new IllegalArgumentException("Amount must be positive");
                    }
                    paymentService.createPaymentPlan(
                        memberId,
                        amount,
                        installmentsField.getValue(),
                        firstDuePicker.getValue()
                    );
                    refreshData();
                } catch (Exception ex) {
                    showAlert("Unable to create plan", ex.getMessage());
                }
            }
        });
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static class OverdueSummary {
        private final int count;
        private final double amount;

        private OverdueSummary(int count, double amount) {
            this.count = count;
            this.amount = amount;
        }
    }
}
