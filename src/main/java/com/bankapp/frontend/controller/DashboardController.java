package com.bankapp.frontend.controller;

import com.bankapp.frontend.dto.AccountDTO;
import com.bankapp.frontend.utils.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import java.math.BigDecimal;
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
    @FXML private TableView<?> transactionTable;

    public void setWelcomeMessage(String message) {
        welcomeLabel.setText(message);
    }

    public void setAccountData(List<AccountDTO> accounts) {
        BigDecimal total = BigDecimal.ZERO;

        for (AccountDTO acc : accounts) {
            BigDecimal balance = acc.getBalance() != null ? acc.getBalance() : BigDecimal.ZERO;
            total = total.add(balance);

            String balanceStr = String.format("$%,.2f", balance);
            String accNumStr = "Acc: " + acc.getAccountNumber();

            switch (acc.getAccountType()) {
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

    @FXML
    public void handleTransferNavigation(ActionEvent event) {
        SceneManager.switchScene((Node) event.getSource(), "transfer.fxml");
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        SceneManager.switchScene((Node) event.getSource(), "login.fxml");
    }
}