package com.example.demo.controller;

import com.example.demo.Views.ThreadedUpdater;
import com.example.demo.Views.ViewFactory;
import com.example.demo.services.MenuService;
import com.example.demo.utils.events.MenuChangeEvent;
import com.example.demo.utils.observer.Observer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {
    @FXML
    public Button home_btn;
    @FXML
    public Button friends_btn;
    @FXML
    public Button messages_btn;
    @FXML
    public Button logoutButton;
    @FXML
    public Label friendshipsCount;
    @FXML
    public Label messagesCount;

    public MenuService menuService;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addListeners();
        initModel();
        System.out.println("MenuController initialized");
        messagesCount.setVisible(false);
        friendshipsCount.setVisible(false);
        ThreadedUpdater.setMenuController(this);
    }
    public void setService(MenuService service) {
        this.menuService = service;

    }
    private void addListeners(){
        home_btn.setOnAction(event -> ViewFactory.getInstance().getClientMenuSelector().set("home"));
        friends_btn.setOnAction(event -> {
            ViewFactory.getInstance().getClientMenuSelector().set("friends");
            friendshipsCount.setVisible(false);
            friendshipsCount.setText("0");
            ThreadedUpdater.updateLastFriendship();
        });
        messages_btn.setOnAction(event -> {
            ViewFactory.getInstance().getClientMenuSelector().set("messages");
        });
        logoutButton.setOnAction(event -> {
            try {
                ViewFactory.getInstance().showLoginWindow();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }
    private void initModel(){
        friendshipsCount.setText("5");
    }
    public void updateFriendships(int nrOfFriends) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                friendshipsCount.setText(String.valueOf(nrOfFriends));
                friendshipsCount.setVisible(true);
            }
        });
    }
    public void updateMessages(int nrOfMessages) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                messagesCount.setText(String.valueOf(nrOfMessages));
                messagesCount.setVisible(true);
            }
        });
    }
}
