package dev.gresty.aoc2015;

import static dev.gresty.aoc2015.Utils.msg;

public class Day20 {
    public static void main(String[] args) {
        int h = 1;
        while (presents1(h) < 36000000) h++;
        msg("First house to get that many presents is " + h);
        h = 1;
        while (presents2(h) < 36000000) h++;
        msg("After renegotiation, first house to get that many presents is " + h);
    }

    static int presents1(int house) {
        int sum = 0;
        int sqrt = (int) Math.sqrt(house);
        for (int i = 1; i <= sqrt; i++) {
            int j = house / i;
            if (i * j == house) {
                sum += i + j;
            }
        }
        return sum * 10;
    }

    static int presents2(int house) {
        int sum = 0;
        int sqrt = (int) Math.sqrt(house);
        for (int i = 1; i <= sqrt; i++) {
            int j = house / i;
            if (i * j == house) {
                if (j <= 50) sum += i;
                if (i <= 50) sum += j;
            }
        }
        return sum * 11;
    }
}
