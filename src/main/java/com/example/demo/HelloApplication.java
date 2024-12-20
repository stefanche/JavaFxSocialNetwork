package com.example.demo;

import com.example.demo.Repository.database.FriendshipDbRepo;
import com.example.demo.Repository.database.FriendshipsViewRepo;
import com.example.demo.Repository.database.MessageDbRepo;
import com.example.demo.Repository.database.UserDbRepo;
import com.example.demo.Views.ThreadedUpdater;
import com.example.demo.Views.ViewFactory;
import com.example.demo.domain.Friendship;
import com.example.demo.domain.validators.FriendshipValidator;
import com.example.demo.domain.validators.MessageValidator;
import com.example.demo.domain.validators.UserValidator;
import com.example.demo.services.FriendshipService;
import com.example.demo.services.MenuService;
import com.example.demo.services.MessageService;
import com.example.demo.services.UserService;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Timestamp;

public class HelloApplication extends Application {
    UserService service;
    ViewFactory viewFactory = ViewFactory.getInstance();
    FriendshipService friendshipService;
    MenuService menuService;
    MessageService messageService;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        String username="postgres";
        String pasword="postgres";
        String url="jdbc:postgresql://localhost:5432/postgres";
        UserDbRepo utilizatorRepository = new UserDbRepo(url,username, pasword,  new UserValidator());
        FriendshipDbRepo friendRepo = new FriendshipDbRepo(url, username, pasword, new FriendshipValidator());
        MessageDbRepo messageRepo = new MessageDbRepo(url, username, pasword, new MessageValidator(), utilizatorRepository);
        FriendshipsViewRepo friendshipsViewRepo= new FriendshipsViewRepo(url, username, pasword, utilizatorRepository);
        service = new UserService(utilizatorRepository, friendRepo);
        friendshipService = new FriendshipService(utilizatorRepository, friendRepo, friendshipsViewRepo);
        menuService = new MenuService();
        messageService = new MessageService(messageRepo, friendRepo);
        viewFactory.setMessageService(messageService);
        viewFactory.setMenuService(menuService);
        viewFactory.setService(service);
        viewFactory.setFriendshipService(friendshipService);
        viewFactory.setStage(primaryStage);
        viewFactory.showLoginWindow();
        ThreadedUpdater.setFriendshipRepo(friendRepo);
        ThreadedUpdater.setUserRepo(utilizatorRepository);
        ThreadedUpdater.setMessageService(messageService);
        ThreadedUpdater.setMessageRepo(messageRepo);
        ThreadedUpdater.setFriendshipService(friendshipService);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    ThreadedUpdater.init();
                }
            }
        });
        thread.start();
    }
}