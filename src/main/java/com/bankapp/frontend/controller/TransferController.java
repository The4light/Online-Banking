package com.bankapp.frontend.controller;

import com.bankapp.frontend.dto.AccountDTO;
import com.bankapp.frontend.utils.SceneManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
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

    public void setAccounts(List<AccountDTO> accounts) {
        this.userAccounts = accounts;
        List<String> options = accounts.stream()
                .map(acc -> acc.getAccountType() + " - " + acc.getAccountNumber() + " ($" + acc.getBalance() + ")")
                .toList();
        fromAccountSelector.setItems(FXCollections.observableArrayList(options));
    }

    @FXML
    private void handleTransfer() {
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
                            statusLabel.setText("Transfer Successful!");
                            statusLabel.setStyle("-fx-text-fill: green;");
                        } else {
                            statusLabel.setText("Failed: " + response.body());
                            statusLabel.setStyle("-fx-text-fill: red;");
                        }
                    });
                });
    }

    @FXML
    public void goBack(ActionEvent event) {
        SceneManager.switchScene((Node) event.getSource(), "dashboard.fxml");
    }
}