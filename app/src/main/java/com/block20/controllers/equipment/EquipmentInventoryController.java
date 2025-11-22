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
import java.util.function.Consumer;

public class EquipmentInventoryController extends ScrollPane {
    private VBox contentContainer;
    private TableView<Equipment> equipmentTable;
    private ObservableList<Equipment> inventoryList;
    
    private Consumer<String> navigationHandler;
    private EquipmentService equipmentService;

    public EquipmentInventoryController(Consumer<String> navigationHandler, EquipmentService equipmentService) {
        this.navigationHandler = navigationHandler;
        this.equipmentService = equipmentService;
        this.inventoryList = FXCollections.observableArrayList();
        
        initialize();
        loadData();
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
            createStatsBar(),
            createTableSection()
        );
        
        setContent(contentContainer);
    }

    private void loadData() {
        inventoryList.clear();
        inventoryList.addAll(equipmentService.getInventory());
    }

    private VBox createHeader() {
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(4);
        Text title = new Text("Equipment Inventory");
        title.getStyleClass().add("text-h2");
        Text subtitle = new Text("Track gym assets and maintenance status");
        subtitle.getStyleClass().add("text-muted");
        titleBox.getChildren().addAll(title, subtitle);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addButton = new Button("+ Add Equipment");
        addButton.getStyleClass().addAll("btn", "btn-primary");
        addButton.setOnAction(e -> showAddDialog());

        header.getChildren().addAll(titleBox, spacer, addButton);
        return new VBox(header);
    }

    private HBox createStatsBar() {
        HBox stats = new HBox(24);
        stats.setPadding(new Insets(10, 0, 10, 0));
        
        long total = equipmentService.getInventory().size();
        long broken = equipmentService.getInventory().stream().filter(e -> !e.getStatus().equals("Functional")).count();
        
        stats.getChildren().addAll(
            createStat("Total Assets", String.valueOf(total), "#2563EB"),
            createStat("Maintenance Needed", String.valueOf(broken), broken > 0 ? "#EF4444" : "#10B981")
        );
        return stats;
    }

    private VBox createStat(String label, String value, String color) {
        VBox box = new VBox(4);
        Text val = new Text(value); val.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-fill: " + color + ";");
        Text lbl = new Text(label); lbl.getStyleClass().add("text-caption");
        box.getChildren().addAll(val, lbl);
        return box;
    }

    private VBox createTableSection() {
        VBox container = new VBox(10);
        container.getStyleClass().add("card");
        container.setPadding(new Insets(20));

        equipmentTable = new TableView<>();
        equipmentTable.setItems(inventoryList);
        equipmentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Equipment, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getEquipmentId()));

        TableColumn<Equipment, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getName()));

        TableColumn<Equipment, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getCategory()));

        TableColumn<Equipment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getStatus()));
        statusCol.setCellFactory(col -> new TableCell<Equipment, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(item);
                    badge.getStyleClass().add("badge");
                    if (item.equals("Functional")) badge.setStyle("-fx-background-color: #D1FAE5; -fx-text-fill: #065F46;");
                    else badge.setStyle("-fx-background-color: #FEE2E2; -fx-text-fill: #991B1B;");
                    setGraphic(badge);
                }
            }
        });

        TableColumn<Equipment, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(col -> new TableCell<Equipment, Void>() {
            private final Button btn = new Button("Toggle Status");
            {
                btn.getStyleClass().addAll("btn", "btn-sm", "btn-secondary");
                btn.setOnAction(e -> {
                    Equipment item = getTableView().getItems().get(getIndex());
                    toggleStatus(item);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) setGraphic(null); else setGraphic(btn);
            }
        });

        equipmentTable.getColumns().addAll(idCol, nameCol, catCol, statusCol, actionCol);
        container.getChildren().add(equipmentTable);
        return container;
    }

    private void showAddDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New Equipment");
        dialog.setHeaderText("Enter Equipment Details");

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(); nameField.setPromptText("e.g. Treadmill X1");
        ComboBox<String> catCombo = new ComboBox<>();
        catCombo.getItems().addAll("Cardio", "Strength", "Free Weights", "Accessories");
        catCombo.setValue("Cardio");

        grid.add(new Label("Name:"), 0, 0); grid.add(nameField, 1, 0);
        grid.add(new Label("Category:"), 0, 1); grid.add(catCombo, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(response -> {
            if (response == saveBtn) {
                equipmentService.addEquipment(nameField.getText(), catCombo.getValue(), "Functional");
                loadData(); // Refresh UI
            }
        });
    }

    private void toggleStatus(Equipment item) {
        String newStatus = item.getStatus().equals("Functional") ? "Maintenance" : "Functional";
        equipmentService.updateStatus(item.getEquipmentId(), newStatus);
        loadData(); // Refresh UI
    }
}