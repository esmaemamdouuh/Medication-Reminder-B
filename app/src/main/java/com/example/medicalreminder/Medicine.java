package com.example.medicalreminder;

public class Medicine {
    private long id;
    private String name;
    private String time;

    public Medicine(long id, String name, String time) {
        this.id = id;
        this.name = name;
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }
}