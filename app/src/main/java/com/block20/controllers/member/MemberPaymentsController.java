/*
 * Block20 Gym Management System  
 * Member Payments Controller
 */
package com.block20.controllers.member;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MemberPaymentsController extends ScrollPane {
    
    private VBox contentContainer;
    
    public MemberPaymentsController(String memberId) {
        initializeView();
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
        
        Text title = new Text("Payments & Billing");
        title.getStyleClass().add("text-h2");
        
        Text subtitle = new Text("View payment history and upcoming dues");
        subtitle.getStyleClass().add("text-muted");
        
        VBox card = new VBox(16);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(24));
        
        Text cardTitle = new Text("Payment History");
        cardTitle.getStyleClass().add("text-h3");
        
        VBox paymentsList = new VBox(12);
        paymentsList.getChildren().addAll(
            createPaymentRow(LocalDate.now().minusMonths(1), "Membership Fee", 49.99, "Paid"),
            createPaymentRow(LocalDate.now().minusMonths(2), "Membership Fee", 49.99, "Paid"),
            createPaymentRow(LocalDate.now().minusMonths(3), "Membership Fee", 49.99, "Paid")
        );
        
        card.getChildren().addAll(cardTitle, paymentsList);
        contentContainer.getChildren().addAll(title, subtitle, card);
        setContent(contentContainer);
    }
    
    private HBox createPaymentRow(LocalDate date, String description, double amount, String status) {
        HBox row = new HBox(16);
        row.setStyle("-fx-padding: 12; -fx-background-color: -fx-gray-50; -fx-background-radius: 8;");
        
        VBox dateBox = new VBox();
        dateBox.setPrefWidth(120);
        Text dateText = new Text(date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        dateText.getStyleClass().add("text-body-sm");
        dateBox.getChildren().add(dateText);
        
        Text desc = new Text(description);
        desc.getStyleClass().add("text-body");
        HBox.setHgrow(desc, Priority.ALWAYS);
        
        Text amountText = new Text("$" + String.format("%.2f", amount));
        amountText.getStyleClass().add("text-body");
        amountText.setStyle("-fx-font-weight: 600;");
        
        Label statusLabel = new Label(status);
        statusLabel.getStyleClass().addAll("badge", "badge-success");
        
        row.getChildren().addAll(dateBox, desc, amountText, statusLabel);
        return row;
    }
}
