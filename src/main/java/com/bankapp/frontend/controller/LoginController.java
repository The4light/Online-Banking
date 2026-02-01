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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
                        // ONLY fetch accounts if login was successful
                        fetchUserAccountsAndSwitch(emailField.getText(), (Node) event.getSource());
                    } else {
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
    private void fetchUserAccountsAndSwitch(String email, Node sourceNode) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/accounts/user/" + email))
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    // Convert JSON response to List<AccountDTO>
                    List<AccountDTO> accounts = parseAccountsJson(response.body());

                    Platform.runLater(() -> {
                        // Switch scene and pass data to the new controller
                        DashboardController controller = SceneManager.loadDashboard(sourceNode);
                        controller.setAccountData(accounts);
                        controller.setWelcomeMessage("Welcome back, " + email + "!");
                    });
                });
    }
    @FXML
    public void switchToRegister(ActionEvent event) {
        SceneManager.switchScene((Node) event.getSource(), "register.fxml");
    }
    private List<AccountDTO> parseAccountsJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            // This tells Jackson: "Take this JSON string and turn it into a List of AccountDTOs"
            return mapper.readValue(json, new TypeReference<List<AccountDTO>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }
}