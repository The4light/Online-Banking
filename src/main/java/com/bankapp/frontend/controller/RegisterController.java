package com.bankapp.frontend.controller;

import com.bankapp.frontend.utils.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
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
        System.out.println("Button clicked! Attempting to register: " + emailField.getText());

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

        // We use ONLY the 'send' method now (Synchronous)
        // This waits for the response before moving to the next line of code
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("--- Backend Response ---");
            System.out.println("Status Code: " + response.statusCode());
            System.out.println("Body: " + response.body());

            if (response.statusCode() == 200) {
                if (response.statusCode() == 200) {
                    System.out.println("Success! User is in the database.");

                    // ADD THIS: Jump to login automatically
                    javafx.application.Platform.runLater(() -> {
                        // We use 'null' for the event since we don't have one in a background thread,
                        // but it's better to pass the actual button or a reference.
                        // For simplicity, let's just trigger our existing switcher.
                        SceneManager.switchScene(emailField, "login.fxml");
                    });
                }
            } else {
                System.out.println("Registration failed. Check if email already exists.");
            }

        } catch (Exception e) {
            System.err.println("CONNECTION ERROR: Is the Backend running?");
            e.printStackTrace();
        }
    }
    @FXML
    public void switchToLogin(ActionEvent event) {
        // Cast the source of the click to a Node
        SceneManager.switchScene((Node) event.getSource(), "login.fxml");
    }
}