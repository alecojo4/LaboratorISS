package com.example.proiectiss.observer;

import com.example.proiectiss.domain.User;

public class UserLogoutEvent implements Event{
    private User user;

    public UserLogoutEvent(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public User getData() {
        return this.user;
    }
}
