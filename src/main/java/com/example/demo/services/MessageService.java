package com.example.demo.services;

import com.example.demo.Repository.database.FriendshipDbRepo;
import com.example.demo.Repository.database.MessageDbRepo;
import com.example.demo.Views.utils.FriendshipsView;
import com.example.demo.controller.MessageController;
import com.example.demo.domain.Conversation;
import com.example.demo.domain.Message;
import com.example.demo.domain.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessageService {
    public Optional<User> loggedIn;
    public MessageDbRepo messageDbRepo;
    public MessageController messageController;
    public FriendshipDbRepo friendshipDbRepo;

    public MessageService(MessageDbRepo messageDbRepo, FriendshipDbRepo friendshipDbRepo) {
        this.messageDbRepo = messageDbRepo;
        this.friendshipDbRepo = friendshipDbRepo;
    }

    public void setMessageController(MessageController messageController) {
        this.messageController = messageController;
        this.messageController.setLoggedIn(loggedIn.get().getID());
        this.messageController.init();
    }

    public void setLoggedIn(Optional<User> loggedIn) {
        this.loggedIn = loggedIn;
    }
    public void sendMessage(String message, List<FriendshipsView> friendships, Integer reply) {
        List<Integer> ids = new ArrayList<>();
        friendships.forEach(f -> ids.add(f.getFriendId()));
        Message newMessage = new Message(message, loggedIn.get().getID(), ids, reply);
        messageDbRepo.save(newMessage);
    }
    public Message sendMessageFromConvo(String message, List<Conversation> conv, Integer reply) {
        List<Integer> ids = new ArrayList<>();
        for (Conversation conversation : conv) {
            for (Integer c : conversation.participants) {
                if (c!=loggedIn.get().getID()) {
                    ids.add(c);
                }
            }
        }
        Message newMessage = new Message(message, loggedIn.get().getID(), ids, reply);
        messageDbRepo.save(newMessage);
        return newMessage;
    }
    public void updateConvo() {
        this.messageController.updateConvo();
    }
    public Iterable<Conversation> getConvos() {
        return messageDbRepo.findAllConversations(loggedIn.get().getID());
    }
    public Iterable<FriendshipsView> getAllFriends() {
        return loggedIn.map(user -> friendshipDbRepo.friendshipsOfUser(user.getID())).orElse(null);
    }
    public Iterable<Message> getAllMessagesFromConvo(Conversation convo) {
        return messageDbRepo.findAllMessagesFromConversation(convo);
    }
    public String getText(Integer id) {
        return messageDbRepo.findText(id);
    }
}
