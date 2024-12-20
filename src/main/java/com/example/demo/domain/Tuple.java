package com.example.demo.domain;

import java.util.Objects;

public class Tuple<Integer>{
    private int e1;
    private int e2;
    public Tuple(int e1, int e2) {
        if (e1<e2) {
            this.e1 = e1;
            this.e2 = e2;
        } else {
            this.e1 = e2;
            this.e2 = e1;
        }
    }

    public int getFrom() {
        return e1;
    }

    public void setFrom(int e1) {
        this.e1 = e1;
    }

    public int getTo() {
        return e2;
    }

    public void setTo(int e2) {
        this.e2 = e2;
    }

    @Override
    public String toString() {
        return " " + e1 + "," + e2;

    }

    @Override
    public int hashCode() {
        return Objects.hash(e1, e2);
    }
}
