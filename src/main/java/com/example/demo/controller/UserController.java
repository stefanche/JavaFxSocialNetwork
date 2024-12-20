package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.services.UserService;
import com.example.demo.utils.events.UserEntityChangeEvent;
import com.example.demo.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class UserController implements Observer<UserEntityChangeEvent> {
    UserService service;
    ObservableList<User> model = FXCollections.observableArrayList();

    @FXML
    TableView<User> tableView;
    @FXML
    TableColumn<User, Integer> tableColumnId;
    @FXML
    TableColumn<User,String> tableColumnName;

    public void setUtilizatorService(UserService service) {
        this.service = service;
        initModel();
    }

    @FXML
    public void initialize() {
        //TODO
    }

    private void initModel() {
        // TODO
    }

    @Override
    public void update(UserEntityChangeEvent userEntityChangeEvent) {

    }

    public void handleDeleteUser(ActionEvent actionEvent) {
        User user=(User) tableView.getSelectionModel().getSelectedItem();
        if (user!=null) {
            User deleted= service.deleteUtilizator(user.getId());
        }
    }
}
