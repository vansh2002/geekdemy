package com.example.geektrust.repositories;

import com.example.geektrust.enums.Coupons;
import com.example.geektrust.enums.Programme;
import com.example.geektrust.helpers.PairStringDouble;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PurchaseRepository {

    private double totalCost;
    private boolean isMember;
    private PairStringDouble couponDiscount;
    private double proDiscount;
    private double enrollmentFee;
    private double amountToPay;

    private final List<Programme> programmeList;
    private final List<String> couponList;

    public PurchaseRepository() {
        this.programmeList = new ArrayList<>();
        this.couponList = new ArrayList<>();
        this.couponDiscount =
                new PairStringDouble(Coupons.NONE.name(), 0.0);
        this.proDiscount = 0.0;
        this.enrollmentFee = 0.0;
        this.amountToPay = 0.0;
        this.totalCost = 0.0;
        this.isMember = false;
    }

    public void addCoupon(String coupon) {
        this.couponList.add(coupon);
    }

    public void addProMembership() {
        this.isMember = true;
    }

    public void addProgramme(String category, int quantity) {
        for (int i = 0; i < quantity; i++) {
            programmeList.add(Programme.valueOf(category));
        }
    }

}
