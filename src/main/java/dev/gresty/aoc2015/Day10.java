package dev.gresty.aoc2015;

import static dev.gresty.aoc2015.Utils.msg;
import static dev.gresty.aoc2015.Utils.test;

public class Day10 {

    public static void main(String[] args) {
        test("1", "11", Day10::lookAndSay);
        test("11", "21", Day10::lookAndSay);
        test("21", "1211", Day10::lookAndSay);
        test("1211", "111221", Day10::lookAndSay);
        test("111221", "312211", Day10::lookAndSay);

        String value = "3113322113";
        for (int i = 0; i < 40; i++)
            value = lookAndSay(value);
        msg("Output length after 40 is " + value.length());
        for (int i = 40; i < 50; i++)
            value = lookAndSay(value);
        msg("Output length after 50 is " + value.length());

    }

    static String lookAndSay(String input) {
        StringBuilder output = new StringBuilder();

        int i = 0;
        while (i < input.length()) {
            char digit = input.charAt(i);
            int j = i;
            while (j < input.length() && input.charAt(j) == digit) j++;
            output.append(j - i);
            output.append(digit);
            i = j;
        }
        return output.toString();
    }
}
