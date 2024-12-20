package com.example.demo.services;

import com.example.demo.Repository.database.FriendshipDbRepo;
import com.example.demo.Repository.database.FriendshipsViewRepo;
import com.example.demo.Repository.database.UserDbRepo;
import com.example.demo.Views.utils.FriendshipsView;
import com.example.demo.controller.FriendsController;
import com.example.demo.domain.Friendship;
import com.example.demo.domain.Tuple;
import com.example.demo.domain.User;
import com.example.demo.utils.Page;
import com.example.demo.utils.Pageable;
import com.example.demo.utils.events.FriendshipEntityChangeEvent;
import com.example.demo.utils.events.UserEntityChangeEvent;
import com.example.demo.utils.observer.Observable;
import com.example.demo.utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FriendshipService implements Observable<FriendshipEntityChangeEvent> {
    private UserDbRepo repo;
    private List<Observer<FriendshipEntityChangeEvent>> observers=new ArrayList<>();
    private Optional<User> loggedIn;
    private FriendshipDbRepo friendshipRepo;
    private FriendshipsViewRepo friendshipsViewRepo;

    public FriendshipService(UserDbRepo repo, FriendshipDbRepo friendshipRepo, FriendshipsViewRepo friendshipsViewRepo) {
        this.repo = repo;
        this.friendshipRepo = friendshipRepo;
        this.friendshipsViewRepo = friendshipsViewRepo;
    }
    public void acceptFriendship (Integer id) {
        if (loggedIn.isPresent() && loggedIn.get().getID() != id && id!=0) {
            Friendship friendship = new Friendship(id, loggedIn.get().getID(), true);
            friendshipRepo.update(friendship);
            notifyObservers(new FriendshipEntityChangeEvent());
        } else {
            System.out.println("id is 0");
        }
    }
    public Iterable<FriendshipsView> recievedFriendsRequests(){
        if (loggedIn.isPresent()) {
            return friendshipRepo.userRecievedRequests(loggedIn.get());
        } else {
            return new ArrayList<>();
        }
    }
    public Iterable<FriendshipsView> sentFriendRequests(){
        if (loggedIn.isPresent()) {
            return friendshipRepo.userSentRequests(loggedIn.get());
        } else {
            return new ArrayList<>();
        }
    }
    public void addFriend (Integer id) {
        if (loggedIn.isPresent() && loggedIn.get().getID() != id) {
            Friendship friendship = new Friendship(loggedIn.get().getID(), id);
            friendshipRepo.save(friendship);
            notifyObservers(new FriendshipEntityChangeEvent());
        }
    }
    public void deleteFriend (Integer id) {
        if (loggedIn.isPresent()) {
            Friendship friendship = new Friendship(loggedIn.get().getID(), id);
            friendshipRepo.delete(new Tuple<Integer>(loggedIn.get().getID(), id));
        }
        notifyObservers(new FriendshipEntityChangeEvent());
    }
    public Page<FriendshipsView> findAllFriensOnPage(Pageable pageable) {
        return loggedIn.map(user -> friendshipsViewRepo.findAllOnPage(pageable, user)).orElse(null);
    }
    public void setLoggedIn(Optional<User> loggedIn) {
        this.loggedIn = loggedIn;
    }

    @Override
    public void addObserver(Observer<FriendshipEntityChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<FriendshipEntityChangeEvent> e) {

    }

    @Override
    public void notifyObservers(FriendshipEntityChangeEvent t) {
        observers.forEach(observer->observer.update(t));
    }

}
