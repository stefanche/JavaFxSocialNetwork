package com.example.demo.domain;

import java.sql.Timestamp;
import java.util.Objects;
import com.example.demo.domain.Tuple;

public class Friendship extends Entity<Tuple<Integer>> {
    private final Tuple<Integer> ID;
    private boolean accepted;
    private final Timestamp timestamp;
    private int from;
    public Friendship( int from_user, int to_user, boolean accepted, Timestamp timestamp ) {
        this.ID = new Tuple<Integer>(from_user, to_user);
        this.from = from_user;
        this.accepted = accepted;
        this.timestamp = timestamp;
    }
    public Friendship( int from_user, int to_user ) {
        this(from_user, to_user, false, new Timestamp(System.currentTimeMillis()) );
    }
    public Friendship( int from_user, int to_user, Boolean accepted) {
        this(from_user, to_user, accepted, new Timestamp(System.currentTimeMillis()) );
    }
    public Tuple<Integer> getID() {
        return ID;
    }
    public Timestamp getTimestamp() {
        return timestamp;
    }
    public boolean isAccepted() {
        return accepted;
    }
    public void setFrom ( int from_user ) {
        this.from = from_user;
    }
    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friendship that = (Friendship) o;
        return timestamp.equals(that.timestamp) && accepted == that.accepted && ID.equals(that.ID) && Objects.equals(timestamp, that.timestamp);
    }
    public int getFrom () {
        return from;
    }
    @Override
    public int hashCode() {
        return Objects.hash(ID, accepted, timestamp);
    }
    @Override
    public String toString() {
        return "User id"+ ID.getFrom() + " is friend with user id" + ID.getTo();
    }
}

