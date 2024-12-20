package com.example.demo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {
    @FXML
    private Label welcomeText1;
    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText1.setText("Welcome to JavaFX Application!");
    }

    @FXML
    protected void switchScene1(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("scene1.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();
        welcomeText1.setText("This is scene 1");
    }

    @FXML
    public void switchScene2(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("scene2.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();

    }


}