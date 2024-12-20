package com.example.demo.controller;

import com.example.demo.Views.ThreadedUpdater;
import com.example.demo.Views.utils.FriendshipsView;
import com.example.demo.domain.Conversation;
import com.example.demo.domain.Friendship;
import com.example.demo.domain.Message;
import com.example.demo.services.MessageService;
import com.sun.jdi.IntegerValue;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static javafx.geometry.Pos.BASELINE_CENTER;

public class MessageController implements Initializable {
    @FXML
    public Label convNameLabel;
    @FXML
    public TextField messageBoxTextField;
    @FXML
    public Button sendButton;
    @FXML
    public VBox vbox_messages;
    @FXML
    public ScrollPane sp_main;
    @FXML
    public ListView conversationsListView;
    @FXML
    public Button cancelButton;
    @FXML
    public Label newConvoLabel;
    @FXML
    public Button newConvoButton;
    @FXML
    public TableView friendsTable;
    @FXML
    public TableColumn friendsNameColumn;
    @FXML
    public Button sendFirstButton;
    @FXML
    public TextField firstMessageTextField;
    @FXML
    public Label writeFirstLabel;
    @FXML
    public AnchorPane BigAnchorPane;
    @FXML
    public Label errorLabel;
    public ContextMenu contextMenu = new ContextMenu();
    public MenuItem replyItem = new MenuItem("Reply");
    @FXML
    public Label replyText;
    @FXML
    public Button cancelReply;
    private Integer replyId = 0;
    private MessageService messageService;
    private ObservableList<Conversation> conversationList;
    private ObservableList<FriendshipsView> friendshipList = FXCollections.observableArrayList();
    private Integer loggedIn;
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
        this.messageService.setMessageController(this);
    }
    public void setLoggedIn (Integer loggedIn) {
        this.loggedIn = loggedIn;
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ThreadedUpdater.setMessageController(this);
        firstMessageTextField.setVisible(false);
        writeFirstLabel.setVisible(false);
        sp_main.setVisible(false);
        sendFirstButton.setVisible(false);
        errorLabel.setVisible(false);
        BigAnchorPane.setVisible(false);
        convNameLabel.setVisible(false);
        vbox_messages.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                sp_main.setVvalue((double) t1);
            }
        });
        messageBoxTextField.setOnKeyPressed(event -> {
            if( event.getCode() == KeyCode.ENTER ) {
                    sendMessage();
            }
        });
        firstMessageTextField.setOnKeyPressed(event -> {
            if( event.getCode() == KeyCode.ENTER ) {
                sendMessage();
            }
        });
        cancelButton.setVisible(false);
        friendsTable.setVisible(false);
        replyText.setVisible(false);
        cancelReply.setVisible(false);
        contextMenu.getItems().add(replyItem);
    }
    public void init(){
        renderConversations();
    }
    public void sendMessage() {
        String messageToSend = messageBoxTextField.getText();
        if(messageToSend.equals("")) {
            messageBoxTextField.requestFocus();
        } else {
            Message sent;
            sent = messageService.sendMessageFromConvo(messageToSend, conversationsListView.getSelectionModel().getSelectedItems(), replyId);
            appendSentMessage(sent);
            messageBoxTextField.clear();
            cancelReply();
        }
    }
    public void handleReplyAction() {
        messageBoxTextField.requestFocus();
        String replyTextStr = messageService.getText(replyId);
        String reply = replyTextStr.length() > 7 ? replyTextStr.substring(0, 7) +"..." : replyTextStr + "...";
        replyText.setText(reply);
        replyText.setVisible(true);
        cancelReply.setVisible(true);
    }
    public void receiveMessage(String receivedMessage, String repliedMessage) {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setPadding(new Insets(5, 5, 5, 10));
        Text text = new Text(receivedMessage);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: rgb(233, 233, 235);" +
                "-fx-background-radius: 20px");
        textFlow.setPadding(new Insets(5, 10, 5, 10));
        hbox.getChildren().add(textFlow);
    }
    private void appendRecievedMessage (Message message) {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setPadding(new Insets(5, 5, 5, 10));
        Text text = new Text(message.getMessage());
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: rgb(233, 233, 235);" +
                "-fx-background-radius: 20px");
        textFlow.setPadding(new Insets(5, 10, 5, 10));
        hbox.getChildren().add(textFlow);
        hbox.setId(message.getID().toString());
        hbox.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                replyId =  Integer.parseInt(hbox.getId());
                replyItem.setOnAction(replyEvent -> {
                    handleReplyAction();
                });
                contextMenu.show(hbox, event.getScreenX(), event.getScreenY());
            }
        });
        if (message.getReply() > 0) {
            VBox vbox = new VBox();
            vbox.setAlignment(Pos.CENTER_LEFT);
            String replyMessage = messageService.getText(message.getReply());
            String reply = replyMessage.length() > 7 ? replyMessage.substring(0, 7) +"..." : replyMessage + "...";
            Text replyText = new Text(reply);
            TextFlow replyTextFlow = new TextFlow(replyText);
            replyTextFlow.setPadding(new Insets(5, 10, 5, 10));
            replyText.setFill(Color.color(0.52,0.52,0.53));
            HBox replyHbox = new HBox();
            replyHbox.setAlignment(Pos.CENTER_LEFT);
            replyHbox.setPadding(new Insets(5, 10, 5, 10));
            replyHbox.getChildren().add(replyText);
            vbox.getChildren().add(replyHbox);
            vbox.getChildren().add(hbox);
            vbox_messages.getChildren().add(vbox);
        } else {
            vbox_messages.getChildren().add(hbox);
        }
    }
    private void appendSentMessage (Message message) {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_RIGHT);
        hbox.setPadding(new Insets(5, 15, 5, 10));
        Text text = new Text(message.getMessage());
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-color: rgb(239, 242, 255);" +
                "-fx-background-color: rgb(14, 125, 242);" +
                "-fx-background-radius: 20px");
        textFlow.setPadding(new Insets(5, 10, 5, 10));
        text.setFill(Color.color(0.934,0.94,0.96));
        hbox.getChildren().add(textFlow);
        hbox.setId(message.getID().toString());
        hbox.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                replyId =  Integer.parseInt(hbox.getId());
                replyItem.setOnAction(replyEvent -> {
                    handleReplyAction();
                });
                contextMenu.show(hbox, event.getScreenX(), event.getScreenY());
            }
        });
        if (message.getReply() > 0) {
            VBox vbox = new VBox();
            vbox.setAlignment(Pos.CENTER_RIGHT);
            String replyMessage = messageService.getText(message.getReply());
            String reply = replyMessage.length() > 7 ? replyMessage.substring(0, 7) +"..." : replyMessage + "...";
            Text replyText = new Text(reply);
            TextFlow replyTextFlow = new TextFlow(replyText);
            replyTextFlow.setPadding(new Insets(5, 10, 5, 10));
            replyText.setFill(Color.color(0.52,0.52,0.53));
            HBox replyHbox = new HBox();
            replyHbox.setAlignment(Pos.CENTER_RIGHT);
            replyHbox.setPadding(new Insets(5, 10, 5, 10));
            replyHbox.getChildren().add(replyText);
            vbox.getChildren().add(replyHbox);
            vbox.getChildren().add(hbox);
            vbox_messages.getChildren().add(vbox);
        } else {
            vbox_messages.getChildren().add(hbox);
        }
    }
    public void renderMessageView(Conversation conversation) {
        sp_main.setVisible(true);
        BigAnchorPane.setVisible(true);
        convNameLabel.setText(conversation.name);
        convNameLabel.setText(conversation.name);
        convNameLabel.setVisible(true);
        vbox_messages.getChildren().clear();
        Iterable< Message> messages =  messageService.getAllMessagesFromConvo(conversation);
        for(Message message : messages) {
            if (message.getFrom() == loggedIn) {
                appendSentMessage(message);
            } else {
                appendRecievedMessage(message);
            }
        }
    }
    public void renderConversations(){
        conversationList = FXCollections.observableArrayList();
        Iterable<Conversation> convoList = messageService.getConvos();
        convoList.forEach(conversationList::add);
        conversationsListView.setItems(conversationList);
        conversationsListView.getSelectionModel().selectedItemProperty().addListener((observableValue, o, t1) -> {
            if(t1 != null) {
                renderMessageView((Conversation) t1);
            }
        });
    }
    public void updateConvo() {
        Platform.runLater(()->{
            Conversation convo = (Conversation) conversationsListView.getSelectionModel().getSelectedItem();
            if(convo != null) {
                renderMessageView(convo);
            }
        });
    }
    public void renderFriends() {
        Iterable<FriendshipsView> friendshipsViews = messageService.getAllFriends();
        friendshipsViews.forEach(friendshipList::add);
        friendsTable.setItems(friendshipList);
        friendsNameColumn.setCellValueFactory(new PropertyValueFactory<>("from_name"));
        friendsTable.setVisible(true);
        friendsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
    public void newConvoButtonPressed(ActionEvent actionEvent) {
        cancelButton.setVisible(true);
        sendFirstButton.setVisible(true);
        firstMessageTextField.setVisible(true);
        writeFirstLabel.setVisible(true);
        renderFriends();
    }

    public void sendFirstMessage(ActionEvent actionEvent) {
        if (firstMessageTextField.getText().equals("")) {
            firstMessageTextField.requestFocus();
            errorLabel.setText("Please write a message");
            errorLabel.setVisible(true);
            return;
        }
        if (friendsTable.getSelectionModel().getSelectedItems().isEmpty()) {
            friendsTable.requestFocus();
            errorLabel.setText("No friends selected");
            errorLabel.setVisible(true);
            return;
        }
        messageService.sendMessage(firstMessageTextField.getText(),(List)friendsTable.getSelectionModel().getSelectedItems(), null);
        errorLabel.setVisible(false);
        renderConversations();
    }

    public void closeButtonAction() {
        cancelButton.setVisible(false);
        friendsTable.setVisible(false);
        sendFirstButton.setVisible(false);
        firstMessageTextField.setVisible(false);
        writeFirstLabel.setVisible(false);
        errorLabel.setVisible(false);
    }

    public void cancelReply() {
        replyText.setVisible(false);
        cancelReply.setVisible(false);
        replyId = 0;
    }
}
