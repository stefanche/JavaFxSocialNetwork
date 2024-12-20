package com.example.demo.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User extends Entity<Integer> {
    private int ID;
    private final String name;
    private final List<User> friends = new ArrayList<User>();
    private String password;
    public User(int id, String name, String password) {
        this.ID = id;
        this.name = name;
    }
    public User(int id, String name) {
        this(id, name, "0000");
    }
    public User(String name, String password) {
        this(0, name, password);
    }
    public User(String name) {
        this(0, name, "0000");
    }
    @Override
    public Integer getID() {
        return ID;
    }
    public void setID(int ID) { this.ID = ID; }
    public String getName() { return name; }
    public String getPassword() { return password; }
    public void addFriend(User user) {
        friends.add(user);
    }
    public List<User> getFriends() {
        return friends;
    }
    @Override
    public String toString() {
        return "User "+ name+ " id= " + this.ID;
    }
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User that = (User) o;
        return getName().equals(that.getName()) &&
                getFriends().equals(that.getFriends());
    }
    @Override
    public int hashCode() {
        return Objects.hash(getName(), getFriends(), getID());
    }
}
