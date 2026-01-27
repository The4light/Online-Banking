package com.bankapp.frontend.controller;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        // TEMP: frontend-only validation
        if (email.isEmpty() || password.isEmpty()) {
            System.out.println("Fields cannot be empty");
            return;
        }

        System.out.println("Login clicked");
        // Backend call comes later
    }

    @FXML
    private void goToRegister() {
        System.out.println("Go to register screen");
    }
}
