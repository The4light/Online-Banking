package com.bankapp.frontend.controller;

import com.bankapp.frontend.dto.AccountDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class TransferController {
    @FXML private ComboBox<String> fromAccountSelector;
    @FXML private TextField targetAccountField;
    @FXML private TextField amountField;
    @FXML private TextArea descriptionField;
    @FXML private Label statusLabel;

    private List<AccountDTO> userAccounts;
    private final ObjectMapper objectMapper = new ObjectMapper(); // Added for JSON parsing

    public void setAccounts(List<AccountDTO> accounts) {
        this.userAccounts = accounts;
        if (accounts != null && !accounts.isEmpty()) {
            List<String> options = accounts.stream()
                    .map(acc -> acc.getAccountType() + " (" + acc.getAccountNumber() + ")")
                    .toList();
            fromAccountSelector.setItems(FXCollections.observableArrayList(options));
        }
    }

    @FXML
    private void handleTransfer() {
        // 1. Disable button to prevent double-spending/spamming
        // (Add an fx:id="transferBtn" to your FXML button to use this)

        int selectedIndex = fromAccountSelector.getSelectionModel().getSelectedIndex();
        if (selectedIndex == -1 || targetAccountField.getText().isEmpty() || amountField.getText().isEmpty()) {
            statusLabel.setText("Please fill in all fields");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        String sourceAcc = userAccounts.get(selectedIndex).getAccountNumber();
        String json = String.format(
                "{\"sourceAccountNumber\":\"%s\", \"destinationAccountNumber\":\"%s\", \"amount\":%s, \"description\":\"%s\"}",
                sourceAcc, targetAccountField.getText(), amountField.getText(), descriptionField.getText()
        );

        statusLabel.setText("Processing...");
        statusLabel.setStyle("-fx-text-fill: blue;");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/transactions/transfer"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    Platform.runLater(() -> {
                        if (response.statusCode() == 200) {
                            System.out.println("Backend says transfer worked! Updating local state...");
                            statusLabel.setText("Transfer Successful!");
                            statusLabel.setStyle("-fx-text-fill: green;");

                            // CLEAR THE FIELDS
                            targetAccountField.clear();
                            amountField.clear();
                            descriptionField.clear();

                            // Refresh data in background
                            refreshAccountData(userAccounts.get(0).getUserEmail());
                        } else {
                            statusLabel.setText("Failed: " + response.body());
                            statusLabel.setStyle("-fx-text-fill: red;");
                            System.err.println("Backend Error: " + response.body());
                        }
                    });
                });
    }


    private void refreshAccountData(String email) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/accounts/user/" + email))
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            // 3. UPDATE: Overwrite the old list with new balances
                            this.userAccounts = objectMapper.readValue(response.body(), new TypeReference<List<AccountDTO>>(){});
                            System.out.println("Balances refreshed for: " + email);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @FXML
    public void goBack(ActionEvent event) {
        // 1. Get the email (we need this for the API call)
        if (userAccounts == null || userAccounts.isEmpty()) return;
        String email = userAccounts.get(0).getUserEmail();

        // 2. Fetch LATEST data from Backend
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/accounts/user/" + email))
                .GET()
                .build();

        // 3. Wait for the response, then switch
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            List<AccountDTO> freshData = objectMapper.readValue(
                                    response.body(),
                                    new TypeReference<List<AccountDTO>>(){}
                            );

                            // 4. Switch scenes on the UI Thread with NEW data
                            Platform.runLater(() -> {
                                try {
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
                                    Parent root = loader.load();

                                    DashboardController dsController = loader.getController();
                                    dsController.setAccountData(freshData); // PASSING FRESH DATA

                                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                                    stage.getScene().setRoot(root);
                                } catch (IOException e) { e.printStackTrace(); }
                            });
                        } catch (Exception e) { e.printStackTrace(); }
                    }
                });
    }


    private void switchToDashboard(ActionEvent event, List<AccountDTO> data) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Parent root = loader.load();
            DashboardController controller = loader.getController();
            controller.setAccountData(data); // Send the FRESH data

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}