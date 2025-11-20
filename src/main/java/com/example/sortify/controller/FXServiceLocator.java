package com.example.sortify.controller;

public class FXServiceLocator {

    private static MainController main;

    public static void setMainController(MainController m) {
        main = m;
    }

    public static MainController getMain() {
        return main;
    }
}

