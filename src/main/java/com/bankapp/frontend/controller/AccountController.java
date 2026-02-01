package com.bankapp.frontend.controller;

import com.bankapp.frontend.dto.AccountDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.math.BigDecimal;
import java.util.List;

public class AccountController {
    @FXML private Label checkingBalanceLabel;
    @FXML private Label savingsBalanceLabel;
    @FXML private Label investmentBalanceLabel;
    @FXML private Label totalBalanceLabel;

    public void updateDashboard(List<AccountDTO> accounts) {
        BigDecimal total = BigDecimal.ZERO;

        for (AccountDTO acc : accounts) {
            String balanceStr = "$" + acc.getBalance().toString();
            total = total.add(acc.getBalance());

            if (acc.getAccountType().equals("CHECKING")) checkingBalanceLabel.setText(balanceStr);
            if (acc.getAccountType().equals("SAVINGS")) savingsBalanceLabel.setText(balanceStr);
            if (acc.getAccountType().equals("INVESTMENT")) investmentBalanceLabel.setText(balanceStr);
        }

        totalBalanceLabel.setText("$" + total.toString());
    }
}