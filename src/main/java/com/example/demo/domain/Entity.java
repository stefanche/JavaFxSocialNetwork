package com.example.demo.domain;

public abstract class Entity<ID> {
    private ID id;

    public ID getId() {
        return id;
    }

    public ID getID(){return id;}
}