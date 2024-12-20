package com.example.demo.Views;

import com.example.demo.Repository.database.FriendshipDbRepo;
import com.example.demo.Repository.database.MessageDbRepo;
import com.example.demo.Repository.database.UserDbRepo;
import com.example.demo.controller.MenuController;
import com.example.demo.controller.MessageController;
import com.example.demo.domain.Friendship;
import com.example.demo.domain.Message;
import com.example.demo.domain.User;
import com.example.demo.services.FriendshipService;
import com.example.demo.services.MessageService;
import com.example.demo.utils.events.FriendshipEntityChangeEvent;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Optional;

public class ThreadedUpdater {
    private static Friendship lastFriendship;
    private static FriendshipDbRepo friendshipRepo;
    private static UserDbRepo userRepo;
    private static MenuController menuController;
    private static FriendshipService friendshipService;
    private static MessageService messageService;
    private static MessageController messageController;
    private static MessageDbRepo messageRepo;
    private static User loggedInUser;
    private static Message lastMessage;

    public ThreadedUpdater() {}

    public static void setFriendshipRepo(FriendshipDbRepo friendshipRepo) {
        ThreadedUpdater.friendshipRepo = friendshipRepo;
    }

    public static void setUserRepo(UserDbRepo userRepo) {
        ThreadedUpdater.userRepo = userRepo;
    }

    public static void setMenuController(MenuController menuController) {
        ThreadedUpdater.menuController = menuController;
    }

    public static void setFriendshipService(FriendshipService friendshipService) {
        ThreadedUpdater.friendshipService = friendshipService;
    }

    public static void setMessageService(MessageService messageService) {
        ThreadedUpdater.messageService = messageService;
    }

    public static void setMessageController(MessageController messageController) {
        ThreadedUpdater.messageController = messageController;
    }

    public static void setMessageRepo(MessageDbRepo messageRepo) {
        ThreadedUpdater.messageRepo = messageRepo;
    }

    public static void setLoggedInUser(User loggedInUser) {
        ThreadedUpdater.loggedInUser = loggedInUser;
        ThreadedUpdater.lastFriendship = new Friendship(100,loggedInUser.getID());
        ThreadedUpdater.lastMessage = new Message("test", loggedInUser.getID(), new ArrayList<>(), 0);
        System.out.println(lastFriendship);
    }
    private static boolean checkVariables() {
        if (loggedInUser == null) {
            System.out.println("You have not logged in");
            return false;
        }
        if (friendshipRepo == null) {
            System.out.println("fr repo");
            return false;
        }
        if (userRepo == null) {
            System.out.println("3");
            return false;
        }
        if (menuController == null) {
            System.out.println("4");
            return false;
        }
        if (messageRepo == null) {
            System.out.println("5");
            return false;
        }
        if (messageService == null) {
            System.out.println("6");
            return false;
        }
        if (friendshipService == null) {
            System.out.println("8");
            return false;
        }
        return true;
    }
    public static void init() {
        if (checkVariables()!= true) {
            return;
        }
        if(lastFriendship != null) {
            Optional<Friendship> optional = friendshipRepo.mostRecentFriendship(loggedInUser);
            if (optional.isPresent()) {
                if (lastFriendship.getTimestamp().before(optional.get().getTimestamp())) {
                    menuController.updateFriendships(friendshipRepo.nrOfNewFriendships(lastFriendship, loggedInUser.getID()));
                    friendshipService.notifyObservers(new FriendshipEntityChangeEvent());
                }
            }
        }
        if (lastMessage != null) {
            Optional<Message> optional = messageRepo.mostRecentMessage(lastMessage);
            if (optional.isPresent()) {
                System.out.println(lastMessage);
                messageService.updateConvo();
                lastMessage = optional.get();
            }
        }
        //check for message controllere
    }
    public static void updateLastFriendship() {
        Optional<Friendship> optional = friendshipRepo.mostRecentFriendship(loggedInUser);
        optional.ifPresent(friendship -> lastFriendship = friendship);
    }
}
