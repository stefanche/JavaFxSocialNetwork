package com.example.demo.services;

import com.example.demo.Repository.database.FriendshipDbRepo;
import com.example.demo.Repository.database.UserDbRepo;
import com.example.demo.Views.ThreadedUpdater;
import com.example.demo.Views.utils.FriendshipsView;
import com.example.demo.domain.Friendship;
import com.example.demo.domain.Tuple;
import com.example.demo.domain.User;
import com.example.demo.utils.events.ChangeEventType;
import com.example.demo.utils.events.UserEntityChangeEvent;
import com.example.demo.utils.observer.Observable;
import com.example.demo.utils.observer.Observer;

import java.util.*;

public class UserService implements Observable<UserEntityChangeEvent> {
    private UserDbRepo repo;
    private List<Observer<UserEntityChangeEvent>> observers=new ArrayList<>();
    private Optional<User> loggedIn;
    private FriendshipDbRepo friendshipRepo;

    public UserService(UserDbRepo repo, FriendshipDbRepo friendshipRepo) {
        this.repo = repo;
        this.friendshipRepo = friendshipRepo;
    }
    public Optional<User> getLoggedIn() {
        return loggedIn;
    }
    public User addUser(User user) {
        if(repo.save(user).isEmpty()){
            UserEntityChangeEvent event = new UserEntityChangeEvent(ChangeEventType.ADD, user);
            notifyObservers(event);
            return null;
        }
        return user;
    }
    public String checkFriendship (Integer id) {
        if (loggedIn.isPresent()) {
            Tuple<Integer> tuple = new Tuple<Integer>(loggedIn.get().getID(), id);
            Optional<Friendship> friendship = friendshipRepo.findOne(tuple);
            if (friendship.isPresent()) {
                if (friendship.get().isAccepted())
                    return "Accepted";
                else return "Pending";
            } else if (id == loggedIn.get().getID()){
                return "Logged in user";
            } else {
                return "Add friend";
            }
        }
        return null;
    }

    public User deleteUtilizator(Integer id){
        Optional<User> user=repo.delete(id);
        if (user.isPresent()) {
            notifyObservers(new UserEntityChangeEvent(ChangeEventType.DELETE, user.get()));
            return user.get();
        }
        return null;
    }

    public Iterable<User> getAll(){
        return repo.findAll();
    }

    public Optional<User> login(String username, String password){
        loggedIn = repo.checkCredentials(username, password);
        loggedIn.ifPresent(ThreadedUpdater::setLoggedInUser);
        return loggedIn;
    }

    @Override
    public void addObserver(Observer<UserEntityChangeEvent> e) {
        observers.add(e);

    }

    @Override
    public void removeObserver(Observer<UserEntityChangeEvent> e) {
        //observers.remove(e);
    }

    @Override
    public void notifyObservers(UserEntityChangeEvent t) {

        observers.stream().forEach(x->x.update(t));
    }

    public User updateUtilizator(User u) {
        Optional<User> oldUser=repo.findOne(u.getId());
        if(oldUser.isPresent()) {
            Optional<User> newUser=repo.update(u);
            if (newUser.isEmpty())
                notifyObservers(new UserEntityChangeEvent(ChangeEventType.UPDATE, u, oldUser.get()));
            return newUser.orElse(null);
        }
        return oldUser.orElse(null);
    }
}
