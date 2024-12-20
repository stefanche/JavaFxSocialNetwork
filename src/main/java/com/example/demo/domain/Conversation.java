package com.example.demo.domain;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Conversation {
    public String name;
    public ArrayList<Integer> participants;
    public List<User> users;
    public Conversation(List<User> users) {
        this.users = users;
        participants = new ArrayList<>();
        if (users.stream().count()>2) {
            this.name = "Group: ";
            for (User user : users) {
                this.name = this.name + " " + user.getName();
                participants.add(user.getID());
            }
        } else {
            this.name = "";
            for (User user : users) {
                this.name = this.name + user.getName() + " " ;
                participants.add(user.getID());
            }
        }
    }

    public ArrayList<Integer> getParticipants() {
        return participants;
    }

    @Override
    public String toString() {
        return name;
    }
}
