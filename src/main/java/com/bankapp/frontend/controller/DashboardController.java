package com.bankapp.frontend.controller;

import com.bankapp.frontend.dto.AccountDTO;
import com.bankapp.frontend.dto.TransactionDTO;
import com.bankapp.frontend.utils.SceneManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

public class DashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label checkingBalanceLabel;
    @FXML private Label savingsBalanceLabel;
    @FXML private Label investmentBalanceLabel;
    @FXML private Label totalBalanceLabel;
    @FXML private Label checkingAccNumLabel;
    @FXML private Label savingsAccNumLabel;
    @FXML private Label investmentAccNumLabel;

    // Fixed: Changed TableView<?> to TableView<TransactionDTO>
    @FXML private TableView<TransactionDTO> transactionTable;
    @FXML private TableColumn<TransactionDTO, String> colDescription;
    @FXML private TableColumn<TransactionDTO, BigDecimal> colAmount;
    @FXML private TableColumn<TransactionDTO, LocalDateTime> colDate;
    @FXML private Label topAccountLabel;

    private List<AccountDTO> currentAccounts;
    private String currentUserEmail;

    // ADDED: The ObjectMapper needs to be initialized to handle dates correctly
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public void setWelcomeMessage(String message) {
        welcomeLabel.setText(message);
    }

    public void setAccountData(List<AccountDTO> accounts) {
        this.currentAccounts = accounts;
        BigDecimal total = BigDecimal.ZERO;

        if (accounts != null && !accounts.isEmpty()) {
            // 1. Get the shared number from the first account
            String sharedNumber = accounts.get(0).getAccountNumber();

            // 2. Set the top label (Make sure this @FXML Label exists in your file!)
            topAccountLabel.setText("Account Number: " + sharedNumber);

            // 3. Load history ONCE for the shared number
            loadTransactionHistory(sharedNumber);

            // 4. Update the individual balance cards
            for (AccountDTO acc : accounts) {
                BigDecimal balance = acc.getBalance() != null ? acc.getBalance() : BigDecimal.ZERO;
                total = total.add(balance);

                String balanceStr = String.format("$%,.2f", balance);
                String accNumStr = "Acc: " + acc.getAccountNumber();

                switch (acc.getAccountType().toUpperCase()) {
                    case "CHECKING" -> {
                        checkingBalanceLabel.setText(balanceStr);
                        checkingAccNumLabel.setText(accNumStr);
                    }
                    case "SAVINGS" -> {
                        savingsBalanceLabel.setText(balanceStr);
                        savingsAccNumLabel.setText(accNumStr);
                    }
                    case "INVESTMENT" -> {
                        investmentBalanceLabel.setText(balanceStr);
                        investmentAccNumLabel.setText(accNumStr);
                    }
                }
            }
            totalBalanceLabel.setText(String.format("$%,.2f", total));
        }
    }
    private void loadTransactionHistory(String accountNumber) {
        System.out.println("DEBUG: Requesting transactions for: " + accountNumber); // Check console

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/accounts/transactions/" + accountNumber))
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    System.out.println("DEBUG: Response Code: " + response.statusCode());
                    System.out.println("DEBUG: Raw JSON: " + response.body()); // <--- CRUCIAL: See the raw data

                    if (response.statusCode() == 200) {
                        try {
                            List<TransactionDTO> transactions = objectMapper.readValue(
                                    response.body(),
                                    new TypeReference<List<TransactionDTO>>(){}
                            );

                            Platform.runLater(() -> {
                                System.out.println("DEBUG: Transactions parsed: " + transactions.size());
                                ObservableList<TransactionDTO> data = FXCollections.observableArrayList(transactions);
                                transactionTable.setItems(data);
                                transactionTable.refresh();
                            });
                        } catch (IOException e) {
                            System.err.println("DEBUG: JSON Parsing Failed!");
                            e.printStackTrace();
                        }
                    }
                }).exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }

    @FXML
    public void handleTransferNavigation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/transfer.fxml"));
            Parent root = loader.load();
            TransferController controller = loader.getController();
            controller.setAccounts(currentAccounts);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        SceneManager.switchScene((Node) event.getSource(), "login.fxml");
    }
    @FXML
    public void initialize() {
        // Link columns to DTO fields.
        // Note: "formattedDate" matches the getFormattedDate() method you just added!
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("formattedDate"));

        transactionTable.setPlaceholder(new Label("Waiting for data..."));
    }
}