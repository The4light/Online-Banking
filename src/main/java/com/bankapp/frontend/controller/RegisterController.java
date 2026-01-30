package com.bankapp.frontend.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

public class RegisterController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private TextField phoneField;

    @FXML
    public void handleRegister() {
        // This is where we call the backend API
        String json = String.format(
                "{\"firstName\":\"%s\", \"lastName\":\"%s\", \"email\":\"%s\", \"password\":\"%s\", \"phoneNumber\":\"%s\"}",
                firstNameField.getText(), lastNameField.getText(), emailField.getText(), passwordField.getText(), phoneField.getText()
        );

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/auth/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        System.out.println("User Registered!");
                    } else {
                        System.out.println("Error: " + response.body());
                    }
                });
    }
}