package com.example.weibo_huangqiushi.ui.home;

public class EventMessage {
    public Boolean flag;
    public String message;
    public int id;

    public EventMessage(Boolean flag, int id) {
        this.flag = flag;
        this.id = id;
    }

    public EventMessage(String message, int id) {
        this.message=message;
        this.id = id;
    }
}
