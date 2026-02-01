package com.bankapp.frontend.utils;

import com.bankapp.frontend.controller.DashboardController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class SceneManager {
    // We use Node because every UI element (Button, Label, TextField) is a Node
    public static void switchScene(Node node, String fxmlFile) {
        try {
            System.out.println("Attempting to switch to: " + fxmlFile);
            URL fxmlLocation = SceneManager.class.getResource("/fxml/" + fxmlFile);

            if (fxmlLocation == null) {
                throw new IOException("FXML file not found at: /fxml/" + fxmlFile);
            }

            Parent root = FXMLLoader.load(fxmlLocation);
            Stage stage = (Stage) node.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("CRITICAL: Could not load " + fxmlFile);
            e.printStackTrace();
        }
    }
    public static DashboardController loadDashboard(Node node) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/fxml/dashboard.fxml"));
            Parent root = loader.load();

            // Use the existing stage more reliably
            Stage stage = (Stage) node.getScene().getWindow();
            stage.getScene().setRoot(root);

            return loader.getController();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}