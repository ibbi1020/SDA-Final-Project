package com.block20.controllers.equipment;

import com.block20.models.Equipment;
import com.block20.services.EquipmentService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MaintenanceScheduleController extends ScrollPane {
    
    private VBox contentContainer;
    private VBox maintenanceList;
    private Consumer<String> navigationHandler;
    private EquipmentService equipmentService;

    public MaintenanceScheduleController(Consumer<String> navigationHandler, EquipmentService equipmentService) {
        this.navigationHandler = navigationHandler;
        this.equipmentService = equipmentService;
        initialize();
        refreshData();
    }

    private void initialize() {
        setFitToWidth(true);
        setFitToHeight(false);
        setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        getStyleClass().add("content-scroll-pane");
        
        contentContainer = new VBox(24);
        contentContainer.getStyleClass().add("main-content");
        contentContainer.setPadding(new Insets(32));

        contentContainer.getChildren().addAll(
            createHeader(),
            createListSection()
        );
        
        setContent(contentContainer);
    }

    private VBox createHeader() {
        VBox header = new VBox(8);
        Text title = new Text("Maintenance Schedule");
        title.getStyleClass().add("text-h2");
        Text subtitle = new Text("Manage broken equipment and schedule repairs");
        subtitle.getStyleClass().add("text-muted");
        header.getChildren().addAll(title, subtitle);
        return header;
    }

    private VBox createListSection() {
        VBox section = new VBox(16);
        section.getStyleClass().add("card");
        section.setPadding(new Insets(24));
        
        maintenanceList = new VBox(12);
        section.getChildren().addAll(new Label("Pending Repairs"), new Separator(), maintenanceList);
        return section;
    }

    private void refreshData() {
        maintenanceList.getChildren().clear();
        
        // 1. Get Real Inventory
        List<Equipment> allEquipment = equipmentService.getInventory();
        
        // 2. Filter for Broken Items
        List<Equipment> brokenItems = allEquipment.stream()
            .filter(e -> !e.getStatus().equalsIgnoreCase("Functional"))
            .collect(Collectors.toList());

        if (brokenItems.isEmpty()) {
            VBox emptyBox = new VBox(10);
            emptyBox.setAlignment(Pos.CENTER);
            Label icon = new Label("✅"); icon.setStyle("-fx-font-size: 40px;");
            Label msg = new Label("All systems operational. No maintenance required.");
            msg.getStyleClass().add("text-muted");
            emptyBox.getChildren().addAll(icon, msg);
            maintenanceList.getChildren().add(emptyBox);
        } else {
            for (Equipment item : brokenItems) {
                maintenanceList.getChildren().add(createMaintenanceCard(item));
            }
        }
    }

    private HBox createMaintenanceCard(Equipment item) {
        HBox card = new HBox(16);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(16));
        card.setStyle("-fx-background-color: #FEF2F2; -fx-border-color: #FECACA; -fx-border-radius: 8; -fx-background-radius: 8;");

        VBox details = new VBox(4);
        HBox.setHgrow(details, Priority.ALWAYS);
        
        Text name = new Text(item.getName());
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        
        Text id = new Text("ID: " + item.getEquipmentId() + " • Category: " + item.getCategory());
        id.getStyleClass().add("text-caption");
        
        Label statusBadge = new Label(item.getStatus());
        statusBadge.getStyleClass().add("badge");
        statusBadge.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white;");
        
        details.getChildren().addAll(name, id, statusBadge);

        // Action Button
        Button fixBtn = new Button("Mark Repaired");
        fixBtn.getStyleClass().addAll("btn", "btn-success");
        fixBtn.setOnAction(e -> markAsFixed(item));

        card.getChildren().addAll(details, fixBtn);
        return card;
    }

    private void markAsFixed(Equipment item) {
        try {
            // 1. Call Backend to update status
            equipmentService.updateStatus(item.getEquipmentId(), "Functional");
            
            // 2. Show Success
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText(item.getName() + " is now marked as Functional.");
            alert.showAndWait();
            
            // 3. Refresh List (Item should disappear)
            refreshData();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}