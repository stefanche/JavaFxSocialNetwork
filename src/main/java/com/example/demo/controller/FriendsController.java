package com.example.demo.controller;

import com.example.demo.Views.utils.FriendshipsView;
import com.example.demo.domain.Friendship;
import com.example.demo.domain.User;
import com.example.demo.services.FriendshipService;
import com.example.demo.services.UserService;
import com.example.demo.utils.Page;
import com.example.demo.utils.Pageable;
import com.example.demo.utils.events.FriendshipEntityChangeEvent;
import com.example.demo.utils.observer.Observer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendsController implements Observer<FriendshipEntityChangeEvent> {
    @FXML
    public TableColumn friendsNameColumn;
    @FXML
    public TableView friendsTable;
    @FXML
    public TableColumn friendsDateColumn;
    @FXML
    public TableView searchTable;
    @FXML
    public TableColumn searchNameColumn;
    @FXML
    public TableColumn searchStatusColumn;
    @FXML
    public TextField searchPeople;
    @FXML
    public TextField searchFriends;
    @FXML
    public TableColumn recievedFrom;
    @FXML
    public TableColumn recievedDate;
    @FXML
    public TableView recievedFriendRequestsTable;
    @FXML
    public TableColumn acceptColumn;
    @FXML
    public TableColumn toColumn;
    @FXML
    public TableColumn dateSentColumn;
    @FXML
    public TableView pendingRequests;
    @FXML
    private Button buttonNext;
    @FXML
    private Button buttonPrevious;
    @FXML
    private Label labelPage;

    private int pageSize = 5;
    private int currentPage = 0;
    private int totalNumberOfElements = 0;

    private UserService service;
    private FriendshipService friendshipService;
    ObservableList<User> userObservableList = FXCollections.observableArrayList();
    ObservableList<FriendshipsView> recievedFriendReq = FXCollections.observableArrayList();
    ObservableList<FriendshipsView> friendshipsSent = FXCollections.observableArrayList();
    ObservableList<FriendshipsView> friends = FXCollections.observableArrayList();

    public void setService(UserService service, FriendshipService friendshipService) {
        this.service = service;
        this.friendshipService = friendshipService;
        friendshipService.addObserver(this);
        initModel();
        friendsList();
    }


    public void initModel(){
        Iterable<User> userIterable = service.getAll();
        userIterable.forEach(userObservableList::add);
        Iterable<FriendshipsView> recieved = friendshipService.recievedFriendsRequests();
        recieved.forEach(recievedFriendReq::add);
        Iterable<FriendshipsView> sent = friendshipService.sentFriendRequests();
        sent.forEach(friendshipsSent::add);

        searchNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("name"));
        searchStatusColumn.setCellValueFactory(new PropertyValueFactory<User, Integer>("ID"));
        searchStatusColumn.setCellFactory(column -> new TableCell<User, Integer>() {
            private final Button addFriendButton = new Button("Add Friend");

            @Override
            protected void updateItem(Integer ID, boolean empty) {
                super.updateItem(ID, empty);

                if (empty || ID == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    String status = service.checkFriendship(user.getID());
                    switch (status) {
                        case "Accepted":{
                            setText("Friend");
                            setGraphic(null);
                            break;
                        }
                        case "Pending": {
                            setText("Pending");
                            setGraphic(null);
                            break;
                        }
                        case "Logged in user":{
                            setText("Yourself");
                            setGraphic(null);
                            break;
                        }
                        case "Add friend":{
                            setText(null);
                            setGraphic(addFriendButton);
                            break;
                        }
                        default: {
                            setText("Unknown");
                            setGraphic(null);
                            break;
                        }
                    }
                    addFriendButton.setOnAction(event -> {
                        friendshipService.addFriend(user.getID()); // Update status via service
                        getTableView().refresh();     // Refresh the table
                    });
                }
            }
        });
        searchTable.setItems(userObservableList);
        FilteredList<User> filteredList = new FilteredList<>(userObservableList, b->true);
        searchPeople.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(user -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String keyword = newValue.toLowerCase();
                if (user.getName().toLowerCase().indexOf(keyword) != -1) {
                    return true;
                } else return false;
            });
        });
        SortedList<User> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(searchTable.comparatorProperty());
        searchTable.setItems(sortedList);

        recievedFrom.setCellValueFactory(new PropertyValueFactory<FriendshipsView, String>("from_name"));
        recievedDate.setCellValueFactory(new PropertyValueFactory<FriendshipsView, Timestamp>("recieved"));
        acceptColumn.setCellValueFactory(new PropertyValueFactory<FriendshipsView, Integer>("id"));
        acceptColumn.setCellFactory(column -> new TableCell<FriendshipsView, Integer>() {
            private final Button acceptFriendButton = new Button("Add");

            @Override
            protected void updateItem(Integer id, boolean empty) {
                super.updateItem(id, empty);

                if (empty || id == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(null);
                    setGraphic(acceptFriendButton);
                    FriendshipsView user = getTableView().getItems().get(getIndex());

                    acceptFriendButton.setOnAction(event -> {
                        friendshipService.acceptFriendship(user.getId()); // Update status via service
                        getTableView().refresh();     // Refresh the table
                    });
                }
            }
        });
        recievedFriendRequestsTable.setItems(recievedFriendReq);

        toColumn.setCellValueFactory(new PropertyValueFactory<FriendshipsView, String>("from_name"));
        dateSentColumn.setCellValueFactory(new PropertyValueFactory<FriendshipsView, Timestamp>("recieved"));
        pendingRequests.setItems(friendshipsSent);

        friendsNameColumn.setCellValueFactory(new PropertyValueFactory<FriendshipsView, String>("from_name"));
        friendsDateColumn.setCellValueFactory(new PropertyValueFactory<FriendshipsView, Timestamp>("recieved"));
        friendsTable.setItems(friends);
    }
    public void friendsList () {
        Page<FriendshipsView> page = friendshipService.findAllFriensOnPage(new Pageable(currentPage, pageSize));
        int maxPage = (int) Math.ceil((double) page.getTotalNumberOfElements() / pageSize) - 1;
        if (maxPage == -1) {
            maxPage = 0;
        }
        if (currentPage > maxPage) {
            currentPage = maxPage;
            page = friendshipService.findAllFriensOnPage(new Pageable(currentPage, pageSize));
        }
        totalNumberOfElements = page.getTotalNumberOfElements();
        buttonPrevious.setDisable(currentPage == 0);
        buttonNext.setDisable((currentPage + 1) * pageSize >= totalNumberOfElements);
        List<FriendshipsView> friendshipsViews = StreamSupport.stream(page.getElementsOnPage().spliterator(), false)
                .collect(Collectors.toList());
        friends.setAll(friendshipsViews);
        labelPage.setText("Page " + (currentPage + 1) + " of " + (maxPage + 1));
    }

    @Override
    public void update(FriendshipEntityChangeEvent event) {
        Platform.runLater(()->{
            userObservableList = FXCollections.observableArrayList();
            recievedFriendReq = FXCollections.observableArrayList();
            friendshipsSent = FXCollections.observableArrayList();
            friends = FXCollections.observableArrayList();
            initModel();
            friendsList();
        });
    }
    public void onNextPage(ActionEvent actionEvent) {
        currentPage ++;
        friendsList();
    }
    public void onPreviousPage(ActionEvent actionEvent) {
        currentPage --;
        friendsList();
    }
    public void onDelete(ActionEvent actionEvent) {
        FriendshipsView friend = (FriendshipsView) friendsTable.getSelectionModel().getSelectedItem();
        if (friend != null) {
            friendshipService.deleteFriend(friend.getFriendId());
        } else {
            MessageAlert.showErrorMessage(null, "Select a friend first");
        }
    }
}

