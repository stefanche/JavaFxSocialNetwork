package com.example.demo.controller;

import com.example.demo.Views.ViewFactory;
import com.example.demo.services.UserService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class LoginController implements Initializable {
    UserService service;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        passwordField.setOnKeyPressed(event -> {
            if( event.getCode() == KeyCode.ENTER ) {
                try {
                    loginButtonOnAction();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    public void loginButtonOnAction() throws IOException {
        System.out.println(usernameField.getText());
        System.out.println(passwordField.getText());
        if (usernameField.getText().isBlank() || passwordField.getText().isBlank()) {
            errorLabel.setText("Please enter credentials");
        } else if (service.login(usernameField.getText(), passwordField.getText()).isPresent()) {
            Stage stage = (Stage) errorLabel.getScene().getWindow();
            ViewFactory.getInstance().showMainWindow();
        } else {
            errorLabel.setText("Login Failed");
        }
    }
    public void setService(UserService service) {
        this.service = service;
    }
}
