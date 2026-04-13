package com.example.geektrust.services;

import com.example.geektrust.enums.ProMembershipFee;
import com.example.geektrust.enums.Programme;
import com.example.geektrust.repositories.PurchaseRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PurchaseService {

    private final PurchaseRepository repository = new PurchaseRepository();

    // ---------------- COMMAND METHODS ---------------- //

    public void addProgramme(String category, int quantity) {
        // Just update state; defer calculations to print() to ensure correct order
        repository.addProgramme(category, quantity);
    }

    public void applyCoupon(String coupon) {
        repository.addCoupon(coupon);
    }

    public void addProMembership() {
        repository.addProMembership();
    }

    public void print() {
        calculateAndPrintBill();
    }

    // ---------------- CALCULATION LOGIC ---------------- //

    private void calculateAndPrintBill() {
        List<Programme> programmes = repository.getProgrammeList();
        boolean isProMember = repository.isMember();
        List<String> appliedCoupons = repository.getCouponList();

        double totalProDiscount = 0.0;
        double totalProgrammeCostAfterPro = 0.0;
        List<Double> itemPricesAfterPro = new ArrayList<>();

        // 1. Calculate Pro Discounts and Establish Base Adjusted Costs
        for (Programme p : programmes) {
            double basePrice = p.cost;
            double proDiscount = 0.0;

            if (isProMember) {
                // Fetch discount % from ProMembershipFee enum matching the Programme name
                double discountPercent = ProMembershipFee.valueOf(p.name()).discount;
                proDiscount = basePrice * discountPercent;
            }

            totalProDiscount += proDiscount;
            double finalItemPrice = basePrice - proDiscount;

            itemPricesAfterPro.add(finalItemPrice);
            totalProgrammeCostAfterPro += finalItemPrice;
        }

        // 2. Determine Coupon Discount
        String finalCoupon = "NONE";
        double couponDiscount = 0.0;
        int programCount = programmes.size();

        if (programCount >= 4) {
            // B4G1 Rule: Auto-applied on 4+ items.
            // Value is the price of the lowest item (after Pro discount).
            finalCoupon = "B4G1";
            if (!itemPricesAfterPro.isEmpty()) {
                couponDiscount = Collections.min(itemPricesAfterPro);
            }
        } else {
            // Manual Coupon Rules
            double maxDiscount = 0.0;
            String bestCoupon = "NONE";

            for (String coupon : appliedCoupons) {
                double currentDiscount = 0.0;

                if (coupon.equals("DEAL_G20")) {
                    // Rule: Apply if value >= 10,000 (Based on Pro-Adjusted Cost)
                    if (totalProgrammeCostAfterPro >= 10000) {
                        currentDiscount = totalProgrammeCostAfterPro * 0.20;
                    }
                } else if (coupon.equals("DEAL_G5")) {
                    // Rule: Apply if quantity >= 2
                    if (programCount >= 2) {
                        currentDiscount = totalProgrammeCostAfterPro * 0.05;
                    }
                }

                // Rule: If multiple valid, take the higher value
                if (currentDiscount > maxDiscount) {
                    maxDiscount = currentDiscount;
                    bestCoupon = coupon;
                }
            }

            if (maxDiscount > 0) {
                couponDiscount = maxDiscount;
                finalCoupon = bestCoupon;
            }
        }

        // 3. Calculate Fees
        double proMemFee = isProMember ? 200.0 : 0.0;
        double enrollmentFee = 0.0;

        // Enrollment Fee Rule: If total programme cost (after discount) < 6666, add 500
        double costAfterCoupon = totalProgrammeCostAfterPro - couponDiscount;
        if (costAfterCoupon < 6666) {
            enrollmentFee = 500.0;
        }

        // 4. Calculate Final Totals
        // Subtotal = Cost of Programmes (Pro Adjusted) + Pro Membership Fee
        double subTotal = totalProgrammeCostAfterPro;

        // Total = Subtotal - Coupon + Enrollment
        double totalToPay = subTotal - couponDiscount + enrollmentFee + proMemFee;

        // 5. Print Output
        System.out.println("SUB_TOTAL " + format(subTotal));
        System.out.println("COUPON_DISCOUNT " + finalCoupon + " " + format(couponDiscount));
        System.out.println("TOTAL_PRO_DISCOUNT " + format(totalProDiscount));
        System.out.println("PRO_MEMBERSHIP_FEE " + format(proMemFee));
        System.out.println("ENROLLMENT_FEE " + format(enrollmentFee));
        System.out.println("TOTAL " + format(totalToPay));
    }

    private String format(double val) {
        return String.format("%.2f", val);
    }
}