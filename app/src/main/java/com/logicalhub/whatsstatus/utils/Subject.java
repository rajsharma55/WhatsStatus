package com.logicalhub.whatsstatus.utils;


public interface Subject {
    void register(Observer observer);

    void unregister(Observer observer);

    void notifyObservers();
}