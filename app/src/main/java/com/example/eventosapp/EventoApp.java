package com.example.eventosapp;

import android.app.Application;

public class EventoApp extends Application {
    public static EventoApp instance;

    public static EventoApp getInstance(){
        if(instance == null){
            synchronized (EventoApp.class){
                if(instance == null){
                    instance = new EventoApp();
                }
            }
        }
        return instance;
    }
}
