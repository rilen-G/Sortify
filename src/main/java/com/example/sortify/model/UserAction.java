package com.example.sortify.model;

public interface UserAction {
    void doIt();
    void undo();
    String label();
}

