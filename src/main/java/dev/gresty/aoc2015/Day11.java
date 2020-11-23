package dev.gresty.aoc2015;

import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dev.gresty.aoc2015.Utils.*;

public class Day11 {

    static Predicate<char[]> isValid =
            ((Predicate<char[]>) Day11::hasIncreasingStraightOf3)
            .and(Day11::hasNoForbiddenLetters)
            .and(Day11::hasTwoPairs);

    public static void main(String[] args) {
        testBool("hijklmmn".toCharArray(), false, isValid);
        testBool("abbceffg".toCharArray(), false, isValid);
        testBool("abbcegjk".toCharArray(), false, isValid);
        testBool("abcdffaa".toCharArray(), true, isValid);
        testBool("ghjaabcc".toCharArray(), true, isValid);

        test("abcdefgh", "abcdffaa", Day11::nextPassword);
        test("ghijklmn", "ghjaabcc", Day11::nextPassword);

        msg("Next valid password is " + nextPassword("hepxcrrq"));
        msg("And the next is " + nextPassword(nextPassword("hepxcrrq")));
    }

    static String nextPassword(String password) {
        char[] current = password.toCharArray();
        do {
            increment(current);
        } while (!isValid.test(current));
        return new String(current);
    }

    static void increment(char[] input) {
        increment(input, input.length - 1);
    }

    static void increment(char[] input, int i) {
        if (i < 0) return;
        char newchar = (char) (input[i] + 1);
        if (newchar > 'z') {
            newchar = 'a';
            increment(input, i - 1);
        }
        input[i] = newchar;
    }

    static boolean hasIncreasingStraightOf3(char[] input) {
        boolean found = false;
        char last = 0;
        int runlength = 0;
        for (char c : input) {
            if (c - last == 1) runlength++;
            else runlength = 0;
            last = c;
            if (runlength == 2) found = true;
        }
        return found;
    }

    static boolean hasNoForbiddenLetters(char[] input) {
        for (char c : input) {
            if (c == 'i' || c == 'o' || c == 'l') return false;
        }
        return true;
    }

    static final Pattern twoPairs = Pattern.compile("(.)\\1.*(.)\\2");

    static boolean hasTwoPairs(char[] input) {
        Matcher m = twoPairs.matcher(new String(input));
        return m.find();
    }
}


