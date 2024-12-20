package com.example.demo.domain;

import java.util.ArrayList;

public class Network {
    private int length;
    private ArrayList<Integer> users = new ArrayList<Integer>();

    public Network() {
        this.length = 0;
    }
    public Iterable<Integer> getUsers() {
        return users;
    }
    public void addUser(Integer user) {
        users.add(user);
        length++;
    }
    public int getLength() {
        return length;
    }
    public void swallowNetwork(Network network) {
        network.getUsers().forEach(this::addUser);
    }
    public boolean hasUser(Integer user) {
       return users.contains(user);
    }
    @Override
    public String toString() {
        return "Network{" + "length=" + length + ", users=" + users + '}';
    }
}
