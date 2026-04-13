package com.example.geektrust;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MainTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() {
        // Redirect System.out to capture the output for verification
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void tearDown() {
        // Restore original System.out
        System.setOut(originalOut);
    }

    @Test
    public void testMain_Scenario1_StandardPurchaseWithG20() throws IOException {
        // Create a temporary input file
        File inputFile = File.createTempFile("input_scenario1", ".txt");
        inputFile.deleteOnExit();

        // Write commands to the file
        try (FileWriter writer = new FileWriter(inputFile)) {
            writer.write("ADD_PROGRAMME CERTIFICATION 1\n");
            writer.write("ADD_PROGRAMME DEGREE 1\n");
            writer.write("ADD_PROGRAMME DIPLOMA 1\n");
            writer.write("APPLY_COUPON DEAL_G20\n");
            writer.write("PRINT_BILL\n");
        }

        // Execute Main with the file path
        Main.main(new String[]{inputFile.getAbsolutePath()});

        String output = outContent.toString();

        /* Expected Calculation:
         * Items: Cert(3000) + Degree(5000) + Diploma(2500) = 10500
         * Pro Membership: None (Fee 0)
         * Coupon: DEAL_G20 (Total >= 10000). Discount = 20% of 10500 = 2100.
         * Subtotal: 10500.00
         * Enrollment Fee: (10500 - 2100) = 8400. Since 8400 > 6666, Fee is 0.
         * Total: 8400.00
         */

        assertTrue(output.contains("SUB_TOTAL 10500.00"), "Subtotal should be 10500.00");
        assertTrue(output.contains("COUPON_DISCOUNT DEAL_G20 2100.00"), "Coupon should be G20 with 2100.00 discount");
        assertTrue(output.contains("TOTAL_PRO_DISCOUNT 0.00"), "No pro discount expected");
        assertTrue(output.contains("PRO_MEMBERSHIP_FEE 0.00"), "No pro fee expected");
        assertTrue(output.contains("ENROLLMENT_FEE 0.00"), "Enrollment fee waived");
        assertTrue(output.contains("TOTAL 8400.00"), "Total should be 8400.00");
    }

    @Test
    public void testMain_Scenario2_ProMembershipWithB4G1() throws IOException {
        File inputFile = File.createTempFile("input_scenario2", ".txt");
        inputFile.deleteOnExit();

        try (FileWriter writer = new FileWriter(inputFile)) {
            writer.write("ADD_PROGRAMME DIPLOMA 2\n");
            writer.write("ADD_PROGRAMME CERTIFICATION 2\n");
            writer.write("ADD_PRO_MEMBERSHIP\n");
            writer.write("PRINT_BILL\n");
        }

        Main.main(new String[]{inputFile.getAbsolutePath()});

        String output = outContent.toString();

        /* Expected Calculation:
         * Pro Member: Yes (Fee 200)
         * Diploma (2500) - 1% Pro Discount = 2475. Qty 2 = 4950.
         * Certification (3000) - 2% Pro Discount = 2940. Qty 2 = 5880.
         * Total Pro Discount = (25*2) + (60*2) = 170.
         * Programme Cost (After Pro): 10830.
         * * Subtotal: 10830 + 200 (Fee) = 11030.00
         * * Coupon: 4 items -> B4G1 Auto Applied.
         * Free Item: Lowest Cost Item (After Pro Discount) -> Diploma (2475).
         * Coupon Discount: 2475.00.
         * * Enrollment Fee Check: (10830 - 2475) = 8355. > 6666, so Fee is 0.
         * Total: 11030 - 2475 + 0 = 8555.00
         */

        assertTrue(output.contains("SUB_TOTAL 11030.00"), "Subtotal should include Pro Fee and Discounted Item Prices");
        assertTrue(output.contains("COUPON_DISCOUNT B4G1 2475.00"), "B4G1 should deduct the PRO-DISCOUNTED price of the lowest item");
        assertTrue(output.contains("TOTAL_PRO_DISCOUNT 170.00"), "Pro discount sum incorrect");
        assertTrue(output.contains("PRO_MEMBERSHIP_FEE 200.00"), "Pro membership fee missing");
        assertTrue(output.contains("ENROLLMENT_FEE 0.00"), "Enrollment fee incorrect");
        assertTrue(output.contains("TOTAL 8555.00"), "Final total incorrect");
    }

    @Test
    public void testMain_Scenario3_EnrollmentFeeApplied() throws IOException {
        File inputFile = File.createTempFile("input_scenario3", ".txt");
        inputFile.deleteOnExit();

        try (FileWriter writer = new FileWriter(inputFile)) {
            writer.write("ADD_PROGRAMME DIPLOMA 2\n");
            writer.write("APPLY_COUPON DEAL_G5\n"); // 2 items, G5 applies
            writer.write("PRINT_BILL\n");
        }

        Main.main(new String[]{inputFile.getAbsolutePath()});

        String output = outContent.toString();

        /* Expected Calculation:
         * Diploma (2500) * 2 = 5000.
         * Coupon G5 (Qty>=2): 5% of 5000 = 250.
         * Cost After Coupon: 5000 - 250 = 4750.
         * Enrollment Check: 4750 < 6666 -> ADD 500 Fee.
         * * Subtotal: 5000.00
         * Total: 5000 - 250 + 500 = 5250.00
         */

        assertTrue(output.contains("SUB_TOTAL 5000.00"));
        assertTrue(output.contains("COUPON_DISCOUNT DEAL_G5 250.00"));
        assertTrue(output.contains("ENROLLMENT_FEE 500.00"));
        assertTrue(output.contains("TOTAL 5250.00"));
    }

    @Test
    public void testMain_EmptyArgs() {
        // Should run without error and print nothing
        Main.main(new String[]{});
        assertTrue(outContent.toString().isEmpty());
    }
}