package com.bankapp.frontend.controller;

import com.bankapp.frontend.dto.AccountDTO;
import com.bankapp.frontend.utils.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class CardController {

    @FXML private Label cardNumberLabel;
    @FXML private Label cardHolderLabel;

    private List<AccountDTO> userAccounts;
    private boolean isCardNumberVisible = false;
    private String maskedNumber = "**** **** **** 1234";
    private String fullNumber = "4532 8812 9901 4521"; // Dummy formatted version of real acc

    /**
     * Receives data from Dashboard/Sidebar
     */
    public void setAccountData(List<AccountDTO> accounts) {
        this.userAccounts = accounts;
        if (accounts != null && !accounts.isEmpty()) {
            AccountDTO mainAccount = accounts.get(0);
            String rawAcc = mainAccount.getAccountNumber(); // This is 9021610593

            if (rawAcc != null && rawAcc.length() >= 4) {
                String lastFour = rawAcc.substring(rawAcc.length() - 4);

                // 1. Create a Masked Version (Standard for security)
                // Format: 4532 •••• •••• [Last 4]
                this.maskedNumber = "4532 •••• •••• " + lastFour;

                // 2. Create the "Real" Full Number for the Toggle
                // We'll keep the 4532 (Visa/Mastercard prefix) but use your real middle digits
                // Format: 4532 [First 4 of yours] [Last 4 of yours]
                String middle = (rawAcc.length() >= 8) ? rawAcc.substring(0, 4) : "0000";
                this.fullNumber = "4532 " + middle + " 1059 " + lastFour;
            }

            // Update the UI
            javafx.application.Platform.runLater(() -> {
                cardNumberLabel.setText(maskedNumber);
                if (cardHolderLabel != null) {
                    // Let's make it look even better by trying to get the user name
                    cardHolderLabel.setText("VALUED CUSTOMER");
                }
            });
        }
    }

    @FXML
    private void handleToggleCardVisibility() {
        isCardNumberVisible = !isCardNumberVisible;
        cardNumberLabel.setText(isCardNumberVisible ? fullNumber : maskedNumber);
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
    public void handlePaymentsNavigation(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/payments.fxml"));
        Parent root = loader.load();
        PaymentController controller = loader.getController();
        controller.setAccounts(userAccounts);
        switchScene(event, root);
    }

    @FXML
    public void handleTransferNavigation(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/transfer.fxml"));
        Parent root = loader.load();
        TransferController controller = loader.getController();
        controller.setAccounts(userAccounts);
        switchScene(event, root);
    }

    @FXML
    public void handleBack(ActionEvent event) throws IOException {
        handleDashboardNavigation(event);
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