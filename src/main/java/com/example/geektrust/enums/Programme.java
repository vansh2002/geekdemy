package com.example.geektrust.enums;

public enum Programme {
    CERTIFICATION(3000),
    DEGREE(5000),
    DIPLOMA(2500);

    public final int cost;


    Programme(int cost) {
        this.cost = cost;
    }
}
