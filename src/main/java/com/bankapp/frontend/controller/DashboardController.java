package com.bankapp.frontend.controller;

import com.bankapp.frontend.utils.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableView;

public class DashboardController {

    // This MUST match the fx:id in your FXML
    @FXML
    private TableView<?> transactionTable;

    @FXML
    public void handleLogout(ActionEvent event) {
        // Switch back to login
        SceneManager.switchScene((Node) event.getSource(), "login.fxml");
    }

    @FXML
    public void initialize() {
        // This runs automatically when the dashboard loads
        System.out.println("Dashboard Initialized!");
    }
}