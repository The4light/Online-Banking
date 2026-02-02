package com.bankapp.frontend.controller;

import com.bankapp.frontend.dto.AccountDTO;
import com.bankapp.frontend.utils.SceneManager;
import com.fasterxml.jackson.core.type.TypeReference;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LoginController {
    @FXML private javafx.scene.control.Label statusLabel;
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
                        // Parse the name from the response body (it's JSON now)
                        String body = response.body();
                        String firstName = body.substring(body.indexOf("firstName\":\"") + 12, body.indexOf("\",\"email"));

                        Platform.runLater(() -> {
                            fetchUserAccountsAndSwitch(emailField.getText(), firstName, (Node) event.getSource());
                        });
                    }else {
                        Platform.runLater(() -> {
                            statusLabel.setText("Invalid email or password!");
                        });
                    }
                })
                .exceptionally(e -> {
                    Platform.runLater(() -> statusLabel.setText("Server Error: Check connection"));
                    return null;
                });
    }
    // Helper method to fetch data before showing the Dashboard
    // Change the signature to include String firstName
    private void fetchUserAccountsAndSwitch(String email, String firstName, Node sourceNode) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/accounts/user/" + email))
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    List<AccountDTO> accounts = parseAccountsJson(response.body());

                    Platform.runLater(() -> {
                        DashboardController controller = SceneManager.loadDashboard(sourceNode);
                        if (controller != null) {
                            controller.setAccountData(accounts);
                            // Use the firstName we passed in!
                            controller.setWelcomeMessage("Welcome back, " + firstName + "!");
                        }
                    });
                });
    }
    @FXML
    public void switchToRegister(ActionEvent event) {
        SceneManager.switchScene((Node) event.getSource(), "register.fxml");
    }
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());

    private List<AccountDTO> parseAccountsJson(String json) {
        try {
            // If the backend sends "No accounts found" or empty text, return an empty list
            if (json == null || json.isBlank() || !json.trim().startsWith("[")) {
                System.out.println("DEBUG: Received non-JSON response from backend: " + json);
                return java.util.Collections.emptyList();
            }

            return objectMapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<List<AccountDTO>>() {});
        } catch (IOException e) {
            System.err.println("ERROR: Failed to parse accounts JSON: " + e.getMessage());
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }
}