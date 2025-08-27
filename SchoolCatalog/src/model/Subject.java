package model;

import java.util.ArrayList;

public class Subject {
    private int id;
    private String name;


    public Subject(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return name;
    }




}
