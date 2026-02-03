package com.bankapp.frontend.controller;

import com.bankapp.frontend.dto.AccountDTO;
import com.bankapp.frontend.dto.TransactionDTO;
import com.bankapp.frontend.dto.UserDTO;
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
import java.time.format.DateTimeFormatter;
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
    @FXML private Label topAccountLabel;

    @FXML private TableView<TransactionDTO> transactionTable;
    @FXML private TableColumn<TransactionDTO, String> colDescription;
    @FXML private TableColumn<TransactionDTO, BigDecimal> colAmount;
    @FXML private TableColumn<TransactionDTO, LocalDateTime> colDate;

    private List<AccountDTO> currentAccounts;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @FXML
    public void initialize() {
        // Setup Table Columns
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Date formatting
        colDate.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        colDate.setCellFactory(column -> new javafx.scene.control.TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                }
            }
        });

        // Amount formatting with colors
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colAmount.setCellFactory(column -> new javafx.scene.control.TableCell<>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("$%,.2f", item));
                    setStyle(item.compareTo(BigDecimal.ZERO) < 0 ? "-fx-text-fill: #e74c3c;" : "-fx-text-fill: #2ecc71;");
                }
            }
        });
    }

    public void setWelcomeMessage(String message) {
        welcomeLabel.setText(message);
    }

    public void setAccountData(List<AccountDTO> accounts) {
        this.currentAccounts = accounts;

        if (accounts != null && !accounts.isEmpty()) {
            AccountDTO mainAcc = accounts.get(0);
            String accNum = mainAcc.getAccountNumber();
            BigDecimal balance = mainAcc.getBalance() != null ? mainAcc.getBalance() : BigDecimal.ZERO;

            String balanceStr = String.format("$%,.2f", balance);

            // Update main UI display
            topAccountLabel.setText("Account Number: " + accNum);
            totalBalanceLabel.setText(balanceStr);
            checkingBalanceLabel.setText(balanceStr);
            checkingAccNumLabel.setText("Acc: " + accNum);

            // Defaults for empty slots
            savingsBalanceLabel.setText("$0.00");
            savingsAccNumLabel.setText("Inactive");
            investmentBalanceLabel.setText("$0.00");
            investmentAccNumLabel.setText("Inactive");

            loadTransactionHistory(accNum);
        }
    }

    private void loadTransactionHistory(String accountNumber) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/accounts/transactions/" + accountNumber))
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            List<TransactionDTO> transactions = objectMapper.readValue(
                                    response.body(),
                                    new TypeReference<List<TransactionDTO>>(){}
                            );

                            Platform.runLater(() -> {
                                ObservableList<TransactionDTO> data = FXCollections.observableArrayList(transactions);
                                transactionTable.setItems(data);
                                if (transactions.isEmpty()) {
                                    transactionTable.setPlaceholder(new Label("No recent transactions."));
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @FXML
    public void handleDashboardNavigation(ActionEvent event) {
        // Refresh the current view
        setAccountData(currentAccounts);
    }

    @FXML
    public void handleTransferNavigation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/transfer.fxml"));
            Parent root = loader.load();
            TransferController controller = loader.getController();
            controller.setAccounts(currentAccounts);
            switchScene(event, root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handlePaymentsNavigation(ActionEvent event) {
        try {
            // 1. Ensure this path is correct: /fxml/payments.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/payments.fxml"));
            Parent root = loader.load();

            // 2. This works ONLY if payments.fxml has fx:controller="...PaymentController"
            PaymentController controller = loader.getController();
            controller.setAccounts(currentAccounts);

            switchScene(event, root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleCardsNavigation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cards.fxml"));
            Parent root = loader.load();
            CardController controller = loader.getController();
            controller.setAccountData(currentAccounts);
            switchScene(event, root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleProfileNavigation(ActionEvent event) {
        String email = currentAccounts.get(0).getUserEmail();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/auth/user/" + email))
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            UserDTO user = objectMapper.readValue(response.body(), UserDTO.class);
                            Platform.runLater(() -> {
                                try {
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/profile.fxml"));
                                    Parent root = loader.load();
                                    ProfileController controller = loader.getController();
                                    controller.setUserData(currentAccounts, user);
                                    switchScene(event, root);
                                } catch (IOException e) { e.printStackTrace(); }
                            });
                        } catch (IOException e) { e.printStackTrace(); }
                    }
                });
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        SceneManager.switchScene((Node) event.getSource(), "login.fxml");
    }

    private void switchScene(ActionEvent event, Parent root) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.getScene().setRoot(root);
    }
}