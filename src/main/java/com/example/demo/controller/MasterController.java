package com.example.demo.controller;

import com.example.demo.Views.ThreadedUpdater;
import com.example.demo.Views.ViewFactory;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MasterController implements Initializable {
    public BorderPane client_parent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        client_parent.setCenter(ViewFactory.getInstance().getHomeWindow());
        FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("/views/menu.fxml"));
        ViewFactory.getInstance().setMenuService(menuLoader.getController());
        ViewFactory.getInstance().getClientMenuSelector().addListener((observableValue, oldVal, newVal) -> {
            switch (newVal) {
                case "friends" -> client_parent.setCenter(ViewFactory.getInstance().showFriendsWindow());
                case "home" ->client_parent.setCenter(ViewFactory.getInstance().getHomeWindow());
                case "messages" -> client_parent.setCenter(ViewFactory.getInstance().showMessagesWindow());
                default -> client_parent.setCenter(ViewFactory.getInstance().getHomeWindow());
            }
        });
    }
}

