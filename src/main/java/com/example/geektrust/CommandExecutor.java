package com.example.geektrust;

import com.example.geektrust.services.PurchaseService;

import java.util.Arrays;

public class CommandExecutor {
    private final PurchaseService purchaseService = new PurchaseService();

    public void execute(String commandLine) {
        if (commandLine == null || commandLine.trim().isEmpty()) return;

        String[] tokens = commandLine.trim().split("\\s+");
        String command = tokens[0];
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

        try {
            processCommand(command, params);
        } catch (Exception e) {
            // Handle or ignore malformed command lines
        }
    }

    private void processCommand(String command, String[] params) {
        switch (command) {
            case "ADD_PROGRAMME":
                purchaseService.addProgramme(params[0], Integer.parseInt(params[1]));
                break;
            case "APPLY_COUPON":
                purchaseService.applyCoupon(params[0]);
                break;
            case "ADD_PRO_MEMBERSHIP":
                purchaseService.addProMembership();
                break;
            case "PRINT_BILL":
                purchaseService.print();
                break;
            default:
                break;
        }

    }
}
