package com.example.demo.services;

import com.example.demo.utils.events.MenuChangeEvent;
import com.example.demo.utils.events.UserEntityChangeEvent;
import com.example.demo.utils.observer.Observable;
import com.example.demo.utils.observer.Observer;

import java.util.ArrayList;
import java.util.List;

public class MenuService implements Observable<MenuChangeEvent> {
    private List<Observer<MenuChangeEvent>> observers=new ArrayList<>();

    @Override
    public void addObserver(Observer<MenuChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<MenuChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(MenuChangeEvent t) {
        observers.forEach(x->x.update(t));
    }
}
