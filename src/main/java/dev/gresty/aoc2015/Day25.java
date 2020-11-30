package dev.gresty.aoc2015;

import static dev.gresty.aoc2015.Utils.msg;
import static dev.gresty.aoc2015.Utils.test;

public class Day25 {

    static final int FIRST = 20151125;
    static final long MULTIPLIER = 252533L;
    static final int MODULO = 33554393;

    public static void main(String[] args) {
        test(new int[]{3, 4}, 19, Day25::testToIndex);
        test(new int[]{5, 1}, 11, Day25::testToIndex);
        test(new int[]{1, 4}, 10, Day25::testToIndex);
        test(FIRST, 31916031, Day25::nextCode);
        test(28094349, 9380097, Day25::nextCode);

        msg("" + execute(3, 3));
        msg("" + execute(2981, 3075));
    }

    static int testToIndex(int[] i) {
        return toIndex(i[0], i[1]);
    }

    static int execute(int row, int column) {
        var code = FIRST;
        for (int i = 2; i <= toIndex(row, column); i++) {
            code = nextCode(code);
        }
        return code;
    }

    static int nextCode(int previous) {
        return (int) (previous * MULTIPLIER % MODULO);
    }

    static int toIndex(int row, int column) {
        return (row + column - 1) * (row + column - 2) / 2 + column;
    }



    // start: r / 2 * (r - 1) = i - 1
    // r(r - 1) = 2i - 2
    // r^2 - r = 2i - 2
    // r^2 - r - (2i - 2) = 0

    // r = (1 +/- sqrt(1 - 4(2i - 2))) / 2





}
