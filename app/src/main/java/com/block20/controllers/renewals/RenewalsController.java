/*
 * Block20 Gym Management System
 * Renewals Controller - Fixed Inheritance Structure
 */
package com.block20.controllers.renewals;

import com.block20.models.Member;
import com.block20.services.MemberService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.scene.text.Text;


public class RenewalsController extends ScrollPane { // <--- The Controller IS the View

    private StackPane viewContainer;
    private VBox pendingRenewalsView;
    private StackPane renewalProcessView;
    private Consumer<String> navigationHandler;
    private MemberService memberService;

    // Filter state
    private String currentFilter = "All";
    
    // UI References
    private VBox tableRowsContainer;
    private Label statsTotalLabel;
    private Label statsExpiringLabel;
    private Label statsOverdueLabel;
    
    // Renewal Process State
    private RenewalData currentRenewal;

    public RenewalsController(Consumer<String> navigationHandler, MemberService memberService) {
        this.navigationHandler = navigationHandler;
        this.memberService = memberService;
        initializeView();
        refreshData(); 
    }

    private void initializeView() {
        // Configure THIS ScrollPane (The class itself)
        this.setFitToWidth(true);
        this.setFitToHeight(false);
        this.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        this.getStyleClass().add("content-scroll-pane");

        viewContainer = new StackPane();
        viewContainer.getStyleClass().add("renewals-container");

        pendingRenewalsView = createPendingRenewalsView();

        viewContainer.getChildren().add(pendingRenewalsView);
        
        // Set the content of THIS ScrollPane
        this.setContent(viewContainer);
    }

    // ==================== PENDING RENEWALS VIEW ====================

    private VBox createPendingRenewalsView() {
        VBox container = new VBox(20);
        container.setPadding(new Insets(30));
        container.getStyleClass().add("content-container");

        container.getChildren().addAll(
                createHeader(),
                createStatsBar(),
                createFiltersSection(),
                createRenewalsTable()); 

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

    private HBox createStatsBar() {
        HBox statsBar = new HBox(24);
        statsBar.setPadding(new Insets(10, 0, 10, 0));
        
        statsTotalLabel = new Label("-");
        statsExpiringLabel = new Label("-");
        statsOverdueLabel = new Label("-");
        
        statsBar.getChildren().addAll(
            createStatItem("Total Pending", statsTotalLabel, "#2563EB"),
            createStatItem("Expiring Soon", statsExpiringLabel, "#F59E0B"),
            createStatItem("Overdue", statsOverdueLabel, "#EF4444")
        );
        return statsBar;
    }

    private VBox createStatItem(String title, Label valueLabel, String color) {
        VBox box = new VBox(4);
        valueLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        Text lbl = new Text(title); lbl.getStyleClass().add("text-caption");
        box.getChildren().addAll(valueLabel, lbl);
        return box;
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

    private VBox createRenewalsTable() {
        VBox tableContainer = new VBox(10);
        tableContainer.getStyleClass().add("table-container");

        HBox tableHeader = new HBox();
        tableHeader.getStyleClass().add("table-header");
        tableHeader.setPadding(new Insets(10));

        Label[] cols = {
            new Label(""), new Label("ID"), new Label("Name"), new Label("Plan"), 
            new Label("Expiry"), new Label("Status"), new Label("Action")
        };
        int[] widths = {40, 80, 200, 120, 120, 150, 150};
        for(int i=0; i<cols.length; i++) { cols[i].setPrefWidth(widths[i]); tableHeader.getChildren().add(cols[i]); }

        // We don't create a new ScrollPane here anymore, we just create the row container
        // because 'RenewalsController' IS the scroll pane.
        tableRowsContainer = new VBox(5);
        tableRowsContainer.setPadding(new Insets(10));

        tableContainer.getChildren().addAll(tableHeader, tableRowsContainer);
        return tableContainer;
    }

    private void applyFilter(String filter) {
        this.currentFilter = filter;
        refreshTableRows();
    }

    private void refreshData() {
        // 1. Calculate Stats using ALL members
        List<Member> allMembers = memberService.getAllMembers();
        LocalDate today = LocalDate.now();
        LocalDate monthLater = today.plusDays(31); // 31 days covers the full month inclusive

        long overdueCount = allMembers.stream()
            .filter(m -> m.getExpiryDate().isBefore(today))
            .count();
            
        long expiringCount = allMembers.stream()
            .filter(m -> !m.getExpiryDate().isBefore(today) && !m.getExpiryDate().isAfter(monthLater)) // Inclusive check
            .count();
            
        long totalPending = overdueCount + expiringCount;

        // 2. Update UI Labels
        statsTotalLabel.setText(String.valueOf(totalPending));
        statsExpiringLabel.setText(String.valueOf(expiringCount));
        statsOverdueLabel.setText(String.valueOf(overdueCount));

        // 3. Populate Table
        refreshTableRows();
    }
    private void refreshTableRows() {
        tableRowsContainer.getChildren().clear();
        List<MemberRenewalData> renewalsList = loadRealRenewals();

        if (renewalsList.isEmpty()) {
            Label emptyLabel = new Label("No memberships found for this filter.");
            emptyLabel.setPadding(new Insets(20));
            tableRowsContainer.getChildren().add(emptyLabel);
        } else {
            for (MemberRenewalData member : renewalsList) {
                tableRowsContainer.getChildren().add(createTableRow(member));
            }
        }
    }

    private HBox createTableRow(MemberRenewalData member) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 10, 12, 10));
        row.setStyle("-fx-border-color: transparent transparent #E2E8F0 transparent;");

        Label dot = new Label("●"); dot.setPrefWidth(40); dot.setStyle("-fx-text-fill: " + member.getStatusColor() + "; -fx-font-size: 18px;");
        Label id = new Label(member.memberId); id.setPrefWidth(80);
        Label name = new Label(member.name); name.setPrefWidth(200); name.setStyle("-fx-font-weight: bold;");
        Label plan = new Label(member.plan); plan.setPrefWidth(120);
        Label date = new Label(member.expiryDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))); date.setPrefWidth(120);
        Label status = new Label(member.getStatusText()); status.setPrefWidth(150); status.setStyle("-fx-text-fill: " + member.getStatusColor() + ";");

        Button renewBtn = new Button("Renew");
        renewBtn.getStyleClass().add("btn-sm");
        renewBtn.getStyleClass().add("btn-primary");
        renewBtn.setOnAction(e -> showRenewalDialog(member));

        HBox actions = new HBox(renewBtn); actions.setPrefWidth(150);
        row.getChildren().addAll(dot, id, name, plan, date, status, actions);
        return row;
    }

    // ==================== POPUP DIALOG (UX FIX) ====================

    private void showRenewalDialog(MemberRenewalData m) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Process Renewal");
        dialog.setHeaderText("Renew membership for " + m.name);

        GridPane grid = new GridPane();
        grid.setHgap(20); grid.setVgap(15); grid.setPadding(new Insets(20));

        Label planLbl = new Label("Current Plan:");
        ComboBox<String> planCombo = new ComboBox<>();
        planCombo.getItems().addAll("Basic", "Premium", "Elite", "Student");
        planCombo.setValue(m.plan); 

        Label priceLbl = new Label("Price:");
        Label priceVal = new Label("$" + String.format("%.2f", m.renewalAmount)); 
        
        planCombo.valueProperty().addListener((obs, old, newVal) -> {
            priceVal.setText("$" + String.format("%.2f", getPlanPrice(newVal)));
        });

        Label dateLbl = new Label("New Expiry:");
        LocalDate newDate = m.expiryDate.isBefore(LocalDate.now()) ? LocalDate.now().plusMonths(1) : m.expiryDate.plusMonths(1);
        Label dateVal = new Label(newDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));

        grid.add(planLbl, 0, 0); grid.add(planCombo, 1, 0);
        grid.add(priceLbl, 0, 1); grid.add(priceVal, 1, 1);
        grid.add(dateLbl, 0, 2); grid.add(dateVal, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                memberService.renewMembership(m.memberId, planCombo.getValue());
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle("Success"); a.setHeaderText("Membership Renewed");
                a.showAndWait();
                refreshData(); // Refresh logic
            } catch (Exception e) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText(e.getMessage());
                a.showAndWait();
            }
        }
    }

    // ==================== HELPERS ====================

    private double getPlanPrice(String plan) {
        return switch (plan) {
            case "Basic" -> 29.99; case "Premium" -> 49.99; case "Elite" -> 79.99; case "Student" -> 24.99; default -> 29.99;
        };
    }

    private List<MemberRenewalData> loadRealRenewals() {
        List<Member> allMembers = memberService.getAllMembers();
        List<MemberRenewalData> renewalList = new ArrayList<>();

        for (Member m : allMembers) {
            MemberRenewalData data = new MemberRenewalData();
            data.memberId = m.getMemberId();
            data.name = m.getFullName();
            data.email = m.getEmail();
            data.plan = m.getPlanType();
            data.expiryDate = m.getExpiryDate();
            data.renewalAmount = getPlanPrice(m.getPlanType());

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

    // Simple DTO for the table row
    private static class MemberRenewalData {
        String memberId, name, email, plan;
        LocalDate expiryDate;
        double renewalAmount;

        String getStatusText() {
            long d = ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
            return d < 0 ? "Expired" : d + " days";
        }
        String getStatusColor() {
            long d = ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
            return d < 0 ? "#EF4444" : (d <= 7 ? "#F59E0B" : "#10B981");
        }
    }
    
    // Deprecated Internal Class needed only if legacy code refrences it
    private static class RenewalData {
        String memberId, memberName, memberEmail, currentPlan, selectedPlan, paymentMethod, transactionId;
        LocalDate currentExpiry, newExpiry;
        double renewalAmount, discount, totalAmount;
    }
}