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
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void setAccounts(List<AccountDTO> accounts) {
        this.userAccounts = accounts;
        if (accounts != null && !accounts.isEmpty()) {
            // Display only the one available account
            String option = accounts.get(0).getAccountType() + " (" + accounts.get(0).getAccountNumber() + ")";
            fromAccountSelector.setItems(FXCollections.observableArrayList(option));
            fromAccountSelector.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void handleTransfer() {
        if (targetAccountField.getText().isEmpty() || amountField.getText().isEmpty()) {
            statusLabel.setText("Please fill in all fields");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        String sourceAcc = userAccounts.get(0).getAccountNumber();
        String json = String.format(
                "{\"sourceAccountNumber\":\"%s\", \"destinationAccountNumber\":\"%s\", \"amount\":%s, \"description\":\"%s\"}",
                sourceAcc, targetAccountField.getText(), amountField.getText(), descriptionField.getText()
        );

        statusLabel.setText("Processing...");
        statusLabel.setStyle("-fx-text-fill: blue;");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/accounts/transfer"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    Platform.runLater(() -> {
                        if (response.statusCode() == 200) {
                            statusLabel.setText("Transfer Successful!");
                            statusLabel.setStyle("-fx-text-fill: green;");
                            targetAccountField.clear();
                            amountField.clear();
                            descriptionField.clear();
                        } else {
                            statusLabel.setText("Error: " + response.body());
                            statusLabel.setStyle("-fx-text-fill: red;");
                        }
                    });
                });
    }

    @FXML
    public void goBack(ActionEvent event) {
        String email = userAccounts.get(0).getUserEmail();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/accounts/user/" + email))
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            List<AccountDTO> freshData = objectMapper.readValue(
                                    response.body(), new TypeReference<List<AccountDTO>>(){}
                            );
                            Platform.runLater(() -> switchToDashboard(event, freshData));
                        } catch (Exception e) { e.printStackTrace(); }
                    }
                });
    }

    private void switchToDashboard(ActionEvent event, List<AccountDTO> data) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Parent root = loader.load();
            DashboardController controller = loader.getController();
            controller.setAccountData(data);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) { e.printStackTrace(); }
    }
}