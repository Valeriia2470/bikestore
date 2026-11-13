package com.bikestore.view;

import com.bikestore.controller.InventoryController;
import com.bikestore.controller.InventoryController.Filter;
import com.bikestore.controller.OrderController;
import com.bikestore.io.ParseResult;
import com.bikestore.model.Bike;
import com.bikestore.model.Order;
import com.bikestore.service.OrderException;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainView {
    private final InventoryController inventory;
    private final OrderController orders;
    private final Stage stage;

    private final BorderPane root = new BorderPane();
    private final TableView<Bike> table = new TableView<>();
    private final Label status = new Label("Ready");

    // фильтры
    private final TextField categoryField = new TextField();
    private final TextField minPriceField = new TextField();
    private final TextField maxPriceField = new TextField();
    private final CheckBox inStockBox = new CheckBox("In stock only");

    // заказ
    private final Map<Integer, Integer> cart = new LinkedHashMap<>();
    private final ListView<String> cartView = new ListView<>();
    private final Spinner<Integer> qtySpinner = new Spinner<>(1, 100, 1);
    private final TextField customerField = new TextField();

    public MainView(InventoryController inventory, OrderController orders, Stage stage) {
        this.inventory = inventory;
        this.orders = orders;
        this.stage = stage;

        root.setTop(buildTop());
        root.setCenter(buildTable());
        root.setRight(buildOrderPanel());
        root.setBottom(buildStatusBar());
    }

    public Parent getRoot() {
        return root;
    }



    private Parent buildTop() {
        Button loadBtn = new Button("Load…");
        Button saveBtn = new Button("Save…");
        Button deleteBtn = new Button("Delete selected");
        loadBtn.setOnAction(e -> onLoad());
        saveBtn.setOnAction(e -> onSave());
        deleteBtn.setOnAction(e -> onDelete());
        HBox files = new HBox(8, loadBtn, saveBtn, deleteBtn);

        categoryField.setPromptText("Category");
        minPriceField.setPromptText("Min price");
        maxPriceField.setPromptText("Max price");
        minPriceField.setPrefWidth(90);
        maxPriceField.setPrefWidth(90);
        Button applyBtn = new Button("Apply");
        Button resetBtn = new Button("Reset");
        applyBtn.setOnAction(e -> onApplyFilter());
        resetBtn.setOnAction(e -> onResetFilter());
        HBox filters = new HBox(8, new Label("Filter:"), categoryField, minPriceField,
                maxPriceField, inStockBox, applyBtn, resetBtn);

        VBox top = new VBox(8, files, filters);
        top.setPadding(new Insets(10));
        return top;
    }



    private Parent buildTable() {
        TableColumn<Bike, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Bike, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Bike, String> catCol = new TableColumn<>("Category");
        catCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        TableColumn<Bike, Double> priceCol = new TableColumn<>("Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        TableColumn<Bike, Integer> stockCol = new TableColumn<>("Stock");
        stockCol.setCellValueFactory(new PropertyValueFactory<>("stock"));

        table.getColumns().addAll(idCol, nameCol, catCol, priceCol, stockCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setItems(inventory.visibleBikes());   // привязка к ObservableList контроллера
        table.setPlaceholder(new Label("No data. Click Load… to open a CSV or text file."));
        return table;
    }



    private Parent buildOrderPanel() {
        customerField.setPromptText("Customer name");
        Button addBtn = new Button("Add selected to cart");
        Button clearBtn = new Button("Clear cart");
        Button orderBtn = new Button("Place order");
        addBtn.setOnAction(e -> onAddToCart());
        clearBtn.setOnAction(e -> { cart.clear(); refreshCart(); });
        orderBtn.setOnAction(e -> onPlaceOrder());
        cartView.setPrefHeight(200);

        VBox box = new VBox(8,
                new Label("New order"),
                customerField,
                new HBox(8, new Label("Qty:"), qtySpinner),
                addBtn,
                new Label("Cart:"),
                cartView,
                new HBox(8, orderBtn, clearBtn));
        box.setPadding(new Insets(10));
        box.setPrefWidth(260);
        return box;
    }

    private Parent buildStatusBar() {
        HBox bar = new HBox(status);
        bar.setPadding(new Insets(6, 10, 6, 10));
        return bar;
    }



    public void loadFile(Path path) {
        try {
            ParseResult r = inventory.load(path);
            status.setText("Loaded " + r.bikes().size() + " records, "
                    + r.errors().size() + " errors from " + path.getFileName());
            if (r.hasErrors()) showErrors(r.errors());
        } catch (IOException ex) {
            showError("Cannot read file: " + ex.getMessage());
        }
    }

    private void onLoad() {
        FileChooser fc = chooser("Open data file");
        File f = fc.showOpenDialog(stage);
        if (f != null) loadFile(f.toPath());
    }

    private void onSave() {
        FileChooser fc = chooser("Save data file");
        File f = fc.showSaveDialog(stage);
        if (f == null) return;
        try {
            inventory.save(f.toPath());
            status.setText("Saved to " + f.getName());
        } catch (IOException ex) {
            showError("Cannot write file: " + ex.getMessage());
        }
    }

    private void onDelete() {
        Bike selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            status.setText("Select a row first");
            return;
        }
        inventory.remove(selected.getId());
        status.setText("Deleted " + selected.getName());
    }

    private void onApplyFilter() {
        try {
            Double min = parseOptional(minPriceField.getText());
            Double max = parseOptional(maxPriceField.getText());
            String cat = categoryField.getText().isBlank() ? null : categoryField.getText().trim();
            inventory.applyFilter(new Filter(cat, min, max, inStockBox.isSelected()));
            status.setText("Showing " + inventory.visibleBikes().size() + " bikes");
        } catch (NumberFormatException ex) {
            showError("Price must be a number");
        }
    }

    private void onResetFilter() {
        categoryField.clear();
        minPriceField.clear();
        maxPriceField.clear();
        inStockBox.setSelected(false);
        inventory.applyFilter(Filter.NONE);
        status.setText("Filter cleared");
    }

    private void onAddToCart() {
        Bike selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            status.setText("Select a bike in the table first");
            return;
        }
        cart.merge(selected.getId(), qtySpinner.getValue(), Integer::sum);
        refreshCart();
    }

    private void onPlaceOrder() {
        try {
            Order o = orders.placeOrder(customerField.getText(), new LinkedHashMap<>(cart));
            status.setText(String.format("Order #%d placed for %s, total %.2f",
                    o.id(), o.customer(), o.total()));
            cart.clear();
            refreshCart();
        } catch (OrderException ex) {
            showError(ex.getMessage());
        }
    }



    private void refreshCart() {
        cartView.getItems().clear();
        for (Map.Entry<Integer, Integer> e : cart.entrySet()) {
            String name = inventory.service().search().findById(e.getKey())
                    .map(Bike::getName).orElse("#" + e.getKey());
            cartView.getItems().add(name + " × " + e.getValue());
        }
    }

    private static Double parseOptional(String s) {
        return s == null || s.isBlank() ? null : Double.parseDouble(s.trim().replace(',', '.'));
    }

    private FileChooser chooser(String title) {
        FileChooser fc = new FileChooser();
        fc.setTitle(title);
        fc.setInitialDirectory(new File(".").getAbsoluteFile().toPath().resolve("data").toFile().isDirectory()
                ? new File("data").getAbsoluteFile() : null);
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV files", "*.csv"),
                new FileChooser.ExtensionFilter("Text files", "*.txt"));
        return fc;
    }

    private void showError(String message) {
        status.setText(message);
        new Alert(Alert.AlertType.ERROR, message).showAndWait();
    }

    private void showErrors(java.util.List<String> errors) {
        TextArea area = new TextArea(String.join("\n", errors));
        area.setEditable(false);
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText("Some records were skipped");
        alert.getDialogPane().setContent(area);
        alert.showAndWait();
    }
}