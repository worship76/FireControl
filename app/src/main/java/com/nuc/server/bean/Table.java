package com.nuc.server.bean;

public class Table {
    private String title;
    private String context;

    public Table(){
        this.context = "";
        this.title = "";
    }

    public Table(String title, String context) {
        this.title = title;
        this.context = context;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
