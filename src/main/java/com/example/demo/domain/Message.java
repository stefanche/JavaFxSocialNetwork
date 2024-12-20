package com.example.demo.domain;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Message extends Entity<Integer>{
    private String message; // the body of the message
    private Integer from; // user that sends the message
    private List<Integer> to;
    private Integer reply; //null if the message is not a reply
    private LocalDateTime time;
    private Integer messageID;

    public Message(String message, Integer from, List<Integer> to, Integer reply) {
        this.message = message;
        this.from = from;
        this.to = to;
        this.reply = reply;
        this.time = LocalDateTime.now();
    }
    public Message(String message, Integer from, List<Integer> to, Integer reply, LocalDateTime time) {
        this.message = message;
        this.from = from;
        this.to = to;
        this.reply = reply;
        this.time = time;
    }
    public String getMessage() {
        return message;
    }

    public Integer getFrom() {
        return from;
    }
    public void setTime(LocalDateTime time) {
        this.time = time;
    }
    public List<Integer> getTo() {
        return to;
    }
    public Timestamp getTime() {
        return Timestamp.valueOf(time);
    }
    public void setID(Integer messageID) {
        this.messageID = messageID;
    }

    @Override
    public Integer getID() {
        return messageID;
    }

    public Integer getReply() {
        return reply;
    }

    @Override
    public String toString() {
        return "Message{" +
                "message='" + message + '\'' +
                ", from=" + from +
                ", to=" + to +
                ", reply=" + reply +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message1 = (Message) o;
        return Objects.equals(message, message1.message) && Objects.equals(from, message1.from) && Objects.equals(to, message1.to) && Objects.equals(messageID, message1.messageID) && Objects.equals(reply, message1.reply);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message, from, to, messageID, reply);
    }
}
