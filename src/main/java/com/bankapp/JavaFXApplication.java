package com.bankapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavaFXApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // This looks into your resources/fxml folder
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Online Banking - Registration");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}