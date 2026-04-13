package com.example.geektrust;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                return;
            }
            run(args[0]);
        } catch (Exception e) {
            // Quietly exit or handle appropriately
        }
    }

    private static void run(String filePath) throws IOException {
        CommandExecutor executor = new CommandExecutor();
        try (FileInputStream fis = new FileInputStream(filePath);
             Scanner sc = new Scanner(fis)) {
            while (sc.hasNextLine()) {
                executor.execute(sc.nextLine());
            }
        }
    }
}