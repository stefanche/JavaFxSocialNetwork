package com.example.demo.Views;

import com.example.demo.HelloApplication;
import com.example.demo.controller.*;
import com.example.demo.domain.User;
import com.example.demo.services.FriendshipService;
import com.example.demo.services.MenuService;
import com.example.demo.services.MessageService;
import com.example.demo.services.UserService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.swing.text.View;
import java.io.IOException;
import java.util.Optional;

//view factory singleton
// this
public class ViewFactory {
    private static final ViewFactory view = new ViewFactory();
    private UserService service;
    private FriendshipService friendshipService;
    private MenuService menuService;
    private MessageService messageService;
    private Stage stage;
    private AnchorPane friends;
    private AnchorPane home;
    private AnchorPane messages;
    private final StringProperty clientMenuSelector;
    private ViewFactory() {
        this.clientMenuSelector = new SimpleStringProperty("home");
    }
    public StringProperty getClientMenuSelector() {
        return clientMenuSelector;
    }
    public static ViewFactory getInstance() {
        return view;
    }
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }
    public void setService(UserService service) {
        this.service = service;
    }
    public void setFriendshipService(FriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }
    public void setMenuService(MenuService menuService) {
        this.menuService = menuService;
    }
    public void setServiceToMenu(MenuController menuController) {
        menuController.setService(menuService);
    }
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    public void showLoginWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("views/login.fxml"));
        Scene scene = new Scene(loader.load());
        LoginController loginController = loader.getController();
        loginController.setService(service);
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }
    public void showMainWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("views/client.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle("Dashboard");
        stage.show();
        Optional<User> user = service.getLoggedIn();
        if (user.isPresent()) {
            friendshipService.setLoggedIn(user);
            messageService.setLoggedIn(user);
        }
        if (friends == null && messages == null) {
            this.friends = showFriendsWindow();
            this.messages = showMessagesWindow();
        }
    }
    public AnchorPane showFriendsWindow() {
        if (friends == null) {
            try {
                FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("views/friends.fxml"));
                friends = loader.load();
                FriendsController friendsController = loader.getController();
                friendsController.setService(service, friendshipService);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return friends;
    }
    public AnchorPane showMessagesWindow() {
        if (messages == null) {
            try {
                FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("views/messages.fxml"));
                messages = loader.load();
                MessageController messagesController = loader.getController();
                messagesController.setMessageService(messageService);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return messages;
    }
    public AnchorPane getHomeWindow() {
        if (home == null) {
            try {
                FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("views/home.fxml"));
                home = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return home;
    }
    public void closeStage(Stage stage) {
        stage.close();
    }
}
