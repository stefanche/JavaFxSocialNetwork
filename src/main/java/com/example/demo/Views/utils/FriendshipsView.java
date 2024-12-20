package com.example.demo.Views.utils;

import com.example.demo.domain.Entity;

import javax.swing.text.View;
import java.sql.Timestamp;
import java.util.Objects;

public class FriendshipsView extends Entity<Integer> {
    public String from_name;
    public Timestamp recieved;
    public Integer id;
    public Integer friendId;

    public FriendshipsView(String from_name, Timestamp recieved, int id) {
        this.from_name = from_name;
        this.recieved = recieved;
        this.id = id;
    }
    public FriendshipsView(String from_name, Timestamp recieved, int id, Integer friendId) {
        this.friendId= friendId;
        this.from_name = from_name;
        this.recieved = recieved;
        this.id = id;
    }
    public Integer getFriendId() {
        return friendId;
    }
    public String getFrom_name() {
        return from_name;
    }
    public Timestamp getRecieved() {
        return recieved;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public Integer getID() {
        return 0;
    }

    @Override
    public String toString() {
        return "RecievedFriendshipsView{" +
                "from_name='" + from_name + '\'' +
                ", recieved=" + recieved + " id= " +id+
                '}' + "friendId=" + friendId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendshipsView that = (FriendshipsView) o;
        return Objects.equals(from_name, that.from_name) && Objects.equals(recieved, that.recieved);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from_name, recieved);
    }
}
