package dev.gresty.aoc2015;

import lombok.Data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static dev.gresty.aoc2015.Utils.*;

public class Day12 {

    public static void main(String[] args) throws IOException {
        test("[1,2,3]", 6, Day12::sumNumbers);
        test("{\"a\":2,\"b\":4}", 6, Day12::sumNumbers);
        test("[[[3]]]", 3, Day12::sumNumbers);
        test("{\"a\":{\"b\":4},\"c\":-1}", 3, Day12::sumNumbers);
        test("{\"a\":[-1,1]}", 0, Day12::sumNumbers);
        test("[-1,{\"a\":1}]", 0, Day12::sumNumbers);
        test("[]", 0, Day12::sumNumbers);
        test("{}", 0, Day12::sumNumbers);

        msg("Sum of numbers is " + execute());
        msg("Sum of non-red numbers is " + execute2());
    }

    static int execute() throws IOException {
        try (var in = readFile("day12.txt")) {
            return in.lines().map(Day12::sumNumbers).mapToInt(Integer::intValue).sum();
        }
    }

    static int execute2() throws IOException {
        try (var in = readFile("day12.txt")) {
            var input = in.readLine();
            var ignoredRanges = findIgnoredRanges(input);
            var asChars = input.toCharArray();
            for (var ignored : ignoredRanges) {
                for (int i = ignored.from; i < ignored.to; i++) {
                    asChars[i] = ' ';
                }
            }
            return sumNumbers(new String(asChars));
        }

    }

    static final Pattern findNumbers = Pattern.compile("-?\\d+");
    static int sumNumbers(String input) {
        int sum = 0;
        Matcher m = findNumbers.matcher(input);
        while(m.find()) {
            sum += Integer.parseInt(m.group());
        }
        return sum;
    }

    static Set<Range> findIgnoredRanges(String input) {
        var ignored = new HashSet<Range>();
        var reds = new ArrayList<Integer>();
        int i = 0;
        while ((i = input.indexOf(":\"red\"", i + 1)) > -1) {
            reds.add(i);
        }
        var ranges = new Stack<Range>();
        int lastBracket = 0;
        for (i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '{' || input.charAt(i) == '[') {
                if (!ranges.empty()) {
                    var currentRange = ranges.peek();
                    int j = lastBracket;
                    int k = i;
                    if (currentRange.isObject() && reds.stream().anyMatch(x -> x > j && x < k)) {
                        currentRange.ignore(true);
                    }
                }
                ranges.push(new Range(i, input.charAt(i) == '{'));
                lastBracket = i;
            } else if (input.charAt(i) == ']' || input.charAt(i) == '}') {
                var currentRange = ranges.pop().to(i);
                int j = lastBracket;
                int k = i;
                if (currentRange.isObject() && reds.stream().anyMatch(x -> x > j && x < k)) {
                    currentRange.ignore(true);
                }
                if (currentRange.ignore()) {
                    ignored.add(currentRange);
                }
                lastBracket = i;
            }
        }
        return ignored;
    }

    @Data
    static class Range {
        final int from;
        final boolean isObject;
        int to;
        boolean ignore;
    }
}
