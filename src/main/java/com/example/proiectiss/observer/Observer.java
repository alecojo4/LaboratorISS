package com.example.proiectiss.observer;

public interface Observer<E extends Event> {
    void update(E e);
}
