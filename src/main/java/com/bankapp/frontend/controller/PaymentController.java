package com.bankapp.frontend.controller;

import com.bankapp.frontend.dto.AccountDTO;
import com.bankapp.frontend.utils.SceneManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

public class PaymentController {
    @FXML private ComboBox<String> billerSelector;
    @FXML private ComboBox<String> paymentAccountSelector;
    @FXML private TextField amountField;
    @FXML private Label statusLabel;

    @FXML private TableView<PaymentRow> paymentTable;
    @FXML private TableColumn<PaymentRow, String> colDate;
    @FXML private TableColumn<PaymentRow, String> colBiller;
    @FXML private TableColumn<PaymentRow, String> colStatus;
    @FXML private TableColumn<PaymentRow, String> colAmount;

    private List<AccountDTO> userAccounts;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @FXML
    public void initialize() {
        // Table Mapping
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colBiller.setCellValueFactory(new PropertyValueFactory<>("biller"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));

        billerSelector.setItems(FXCollections.observableArrayList(
                "Electric Company", "Water Department", "Internet Provider", "Mobile Phone", "Cable TV"
        ));
    }

    public void setAccounts(List<AccountDTO> accounts) {
        this.userAccounts = accounts;
        if (accounts != null && !accounts.isEmpty()) {
            String accNum = accounts.get(0).getAccountNumber();
            String display = "Checking Account (****" + accNum.substring(accNum.length() - 4) + ")";
            paymentAccountSelector.setItems(FXCollections.observableArrayList(display));
            paymentAccountSelector.getSelectionModel().selectFirst();
            loadRecentPayments(accNum);
        }
    }

    /**
     * Handlers for the "Pay Now" Cards
     */
    @FXML private void handleElectricPay() { processPayment("Electric Company", "89.99"); }
    @FXML private void handleWaterPay() { processPayment("Water Department", "45.50"); }
    @FXML private void handleInternetPay() { processPayment("Internet Provider", "79.99"); }
    @FXML private void handleMobilePay() { processPayment("Mobile Phone", "65.00"); }

    @FXML
    private void handlePayment() {
        processPayment(billerSelector.getValue(), amountField.getText());
    }

    private void processPayment(String biller, String amount) {
        if (biller == null || amount == null || amount.isEmpty()) {
            statusLabel.setText("Select a biller and amount");
            statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            return;
        }

        String sourceAcc = userAccounts.get(0).getAccountNumber();

        // Transaction JSON
        String json = String.format(
                "{\"sourceAccountNumber\":\"%s\", \"destinationAccountNumber\":\"BILLER-OFFICE\", \"amount\":%s, \"description\":\"Bill Payment: %s\"}",
                sourceAcc, amount, biller
        );

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
                            statusLabel.setText("Payment Successful to " + biller + "!");
                            statusLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;");
                            amountField.clear();
                            refreshData(sourceAcc);
                        } else {
                            statusLabel.setText("Payment Failed: " + response.body());
                            statusLabel.setStyle("-fx-text-fill: #e74c3c;");
                        }
                    });
                });
    }

    private void refreshData(String accNum) {
        loadRecentPayments(accNum);
        String email = userAccounts.get(0).getUserEmail();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/accounts/user/" + email))
                .GET().build();
        HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(res -> {
                    try {
                        this.userAccounts = objectMapper.readValue(res.body(), new TypeReference<List<AccountDTO>>(){});
                    } catch (Exception e) { e.printStackTrace(); }
                });
    }

    private void loadRecentPayments(String accountNumber) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/transactions/account/" + accountNumber))
                .GET().build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            List<com.bankapp.backend.model.Transaction> list = objectMapper.readValue(
                                    response.body(), new TypeReference<List<com.bankapp.backend.model.Transaction>>(){}
                            );

                            List<PaymentRow> rows = list.stream()
                                    .filter(t -> t.getDescription().contains("Bill Payment"))
                                    .map(t -> new PaymentRow(
                                            t.getTimestamp().toString().substring(0, 10),
                                            t.getDescription().replace("Bill Payment: ", ""),
                                            "Completed",
                                            "$" + t.getAmount()
                                    )).collect(Collectors.toList());

                            Platform.runLater(() -> paymentTable.setItems(FXCollections.observableArrayList(rows)));
                        } catch (Exception e) { e.printStackTrace(); }
                    }
                });
    }

    // --- NAVIGATION HANDLERS ---

    @FXML
    public void handleDashboardNavigation(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
        Parent root = loader.load();
        DashboardController controller = loader.getController();
        controller.setAccountData(userAccounts);
        switchScene(event, root);
    }

    @FXML
    public void handleCardsNavigation(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cards.fxml"));
        Parent root = loader.load();
        CardController controller = loader.getController();
        controller.setAccountData(userAccounts);
        switchScene(event, root);
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        SceneManager.switchScene((Node) event.getSource(), "login.fxml");
    }

    private void switchScene(ActionEvent event, Parent root) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }

    // Row Data Model
    public static class PaymentRow {
        private final String date, biller, status, amount;
        public PaymentRow(String d, String b, String s, String a) {
            this.date = d; this.biller = b; this.status = s; this.amount = a;
        }
        public String getDate() { return date; }
        public String getBiller() { return biller; }
        public String getStatus() { return status; }
        public String getAmount() { return amount; }
    }
}