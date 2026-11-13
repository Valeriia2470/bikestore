package com.bikestore;

import com.bikestore.controller.InventoryController;
import com.bikestore.controller.OrderController;
import com.bikestore.view.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.nio.file.Files;
import java.nio.file.Path;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        InventoryController inventory = new InventoryController();
        OrderController orders = new OrderController(inventory);
        MainView view = new MainView(inventory, orders, stage);

        Path defaultFile = Path.of("data/bikes.csv");
        if (Files.exists(defaultFile)) view.loadFile(defaultFile);

        stage.setScene(new Scene(view.getRoot(), 960, 560));
        stage.setTitle("Bike Store");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}