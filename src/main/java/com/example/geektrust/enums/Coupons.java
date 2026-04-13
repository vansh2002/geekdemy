package com.example.geektrust.enums;

public enum Coupons {
    NONE(0.0),
    DEAL_G20(0.2),
    DEAL_G5(0.05);

    public double percent;

    Coupons(double percent) {
        this.percent = percent;
    }
}
