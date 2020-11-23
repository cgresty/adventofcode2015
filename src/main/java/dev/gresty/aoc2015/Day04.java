package dev.gresty.aoc2015;

import java.security.MessageDigest;

import static dev.gresty.aoc2015.Utils.msg;
import static dev.gresty.aoc2015.Utils.test;

public class Day04 {

    public static void main(String[] args) {
        test("abcdef", 609043L, Day04::execute5);
        test("pqrstuv", 1048970L, Day04::execute5);
        execute5("ckczppom");
        execute6("ckczppom");
    }

    static long execute5(String input) {
        return execute(5, input);
    }

    static long execute6(String input) {
        return execute(6, input);
    }

    static long execute(int zeroes, String input) {
        msg("Executing for " + input + ", seeking " + zeroes + " zeroes");
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            long testval = 0;
            boolean found;
            byte[] result;

            do {
                testval++;
                result = digest(md, input + testval);
                found = validate(zeroes, result);
            } while (!found);
            msg("Result: " + testval + " with hash " + toHex(result));
            return testval;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static byte[] digest(MessageDigest md, String input) {
        md.update(input.getBytes());
        byte[] digest = md.digest();
        md.reset();
        return digest;
    }

    static String toHex(byte[] asBytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : asBytes) {
            int asInt = b & 0xff;
            builder.append(String.format("%02x", asInt));
        }
        return builder.toString();
    }

    static boolean validate(int zeroes, byte[] digest) {
        boolean valid = digest[0] == 0;
        valid = valid && digest[1] == 0;
        if (zeroes == 5) valid = valid && digest[2] >= 0 && digest[2] < 16;
        if (zeroes == 6) valid = valid && digest[2] == 0;
        return valid;
    }
}
