package dev.gresty.aoc2015;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Predicate;

import static dev.gresty.aoc2015.Utils.*;

public class Day05 {

    public static void main(String[] args) throws IOException {

        test("ugknbfddgicrmopn", true, Day05::isNice1);
        test("aaa", true, Day05::isNice1);
        test("jchzalrnumimnmhp", false, Day05::isNice1);
        test("haegwjzuvuyypxyu", false, Day05::isNice1);
        test("dvszwmarrgswjxmb", false, Day05::isNice1);

        execute(Day05::isNice1);

        test("qjhvhtzxzqqjkmpb", true, Day05::isNice2);
        test("xxyxx", true, Day05::isNice2);
        test("uurcxstgmygtbstg", false, Day05::isNice2);
        test("ieodomkazucvgmuy", false, Day05::isNice2);

        execute(Day05::isNice2);

    }

    static void execute(Predicate<String> isNice) throws IOException {
        try (var in = streamFile("day05.txt")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            var niceCount = reader.lines()
                    .filter(isNice)
                    .count();
            msg("There are " + niceCount + " nice strings");
        }
    }

    static boolean isNice1(String maybe) {
        var checker = new CompositeChecker()
                .add(new ThreeVowelChecker())
                .add(new DoubleChecker())
                .add(new BadPairChecker());

        maybe.chars().forEach(checker::nextChar);
        return checker.isNice();
    }

    static boolean isNice2(String maybe) {
        var checker = new CompositeChecker()
                .add(new DoublePairChecker())
                .add(new GappedRepeatChecker());
        maybe.chars().forEach(checker::nextChar);
        return checker.isNice();
    }

    interface NiceChecker {
        void nextChar(int x);
        boolean isNice();
    }

    static class ThreeVowelChecker implements NiceChecker {

        int vowels;

        @Override
        public void nextChar(int x) {
            if (vowels < 3 && "aeiou".indexOf(x) > -1) vowels++;
        }

        @Override
        public boolean isNice() {
            return vowels >= 3;
        }
    }

    static class DoubleChecker implements NiceChecker {

        int lastChar;
        boolean nice;

        @Override
        public void nextChar(int x) {
            if (!nice && lastChar == x) nice = true;
            lastChar = x;
        }

        @Override
        public boolean isNice() {
            return nice;
        }
    }

    static class DoublePairChecker implements NiceChecker {

        Map<String, Integer> pairLocations = new HashMap<>();
        int lastChar = -1;
        int pos = 0;
        boolean nice;

        @Override
        public void nextChar(int x) {
            if (nice) return;
            if (lastChar != -1) {
                String pair = "" + (char) lastChar + (char) x;
                if (pairLocations.containsKey(pair)) {
                    if (pos - pairLocations.get(pair) >= 2) {
                        nice = true;
                    }
                } else {
                    pairLocations.put(pair, pos);
                }
            }
            lastChar = x;
            pos++;
        }

        @Override
        public boolean isNice() {
            return nice;
        }
    }

    static class GappedRepeatChecker implements NiceChecker {

        int charMinus1;
        int charMinus2;
        boolean nice;

        @Override
        public void nextChar(int x) {
            if (nice) return;
            if (x == charMinus2) {
                nice = true;
            }
            charMinus2 = charMinus1;
            charMinus1 = x;
        }

        @Override
        public boolean isNice() {
            return nice;
        }
    }



    static class BadPairChecker implements NiceChecker {

        int lastChar;
        boolean nice = true;
        Set<String> badPairs = Set.of("ab", "cd", "pq", "xy");

        @Override
        public void nextChar(int x) {
            if (nice && badPairs.contains("" + (char) lastChar + (char) x)) {
                nice = false;
            }
            lastChar = x;
        }

        @Override
        public boolean isNice() {
            return nice;
        }
    }

    static class CompositeChecker implements NiceChecker {

        List<NiceChecker> checkers = new ArrayList<>();

        CompositeChecker add(NiceChecker checker) {
            checkers.add(checker);
            return this;
        }

        @Override
        public void nextChar(int x) {
            checkers.forEach(checker -> checker.nextChar(x));
        }

        @Override
        public boolean isNice() {
            boolean nice = true;
            for (var checker : checkers) {
                nice = nice && checker.isNice();
            }
            return nice;
        }
    }
}
