package com.bankapp.frontend.controller;

import com.bankapp.frontend.utils.SceneManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML
    public void handleLogin(ActionEvent event) {
        String json = String.format("{\"email\":\"%s\", \"password\":\"%s\"}",
                emailField.getText(), passwordField.getText());

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        Platform.runLater(() -> {
                            // Use emailField as the anchor to find the window
                            System.out.println("Login Success! Switching to Dashboard...");
                            SceneManager.switchScene(emailField, "dashboard.fxml");
                        });
                    } else {
                        System.out.println("Login Failed: " + response.statusCode() + " - " + response.body());
                    }
                }).exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }
    @FXML
    public void switchToRegister(ActionEvent event) {
        SceneManager.switchScene((Node) event.getSource(), "register.fxml");
    }
}