package com.example.geektrust.enums;

public enum ProMembershipFee {
    DIPLOMA(0.01F),
    CERTIFICATION(0.02F),
    DEGREE(0.03F);

    public float discount;

    ProMembershipFee(float discount) {
        this.discount = discount;
    }
}
