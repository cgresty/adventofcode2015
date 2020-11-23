package dev.gresty.aoc2015;

import lombok.Data;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import static dev.gresty.aoc2015.Utils.readFile;
import static java.lang.Integer.parseInt;
import static java.util.function.Predicate.isEqual;

public class Day16 {

    static Map<String, Predicate<Integer>> matchSue = new HashMap<>();

    public static void main(String[] args) throws IOException {

        matchSue.put("children", isEqual(3));
        matchSue.put("cats", i -> i > 7);
        matchSue.put("samoyeds", isEqual(2));
        matchSue.put("pomeranians", i -> i < 3);
        matchSue.put("akitas", isEqual(0));
        matchSue.put("vizslas", isEqual(0));
        matchSue.put("goldfish", i -> i < 5);
        matchSue.put("trees", i -> i > 3);
        matchSue.put("cars", isEqual(2));
        matchSue.put("perfumes", isEqual(1));

        try (var in = readFile("day16.txt")) {
            in.lines().map(Sue::new)
                    .filter(Day16::filterSue)
                    .map(Sue::name)
                    .forEach(Utils::msg);
        }
    }

    static boolean filterSue(Sue sue) {
        for (String compound : sue.facts.keySet()) {
            if (!matchSue.get(compound).test(sue.facts.get(compound))) {
                return false;
            }
        }
        return true;
    }

    @Data
    static class Sue {
        final String name;
        final Map<String, Integer> facts = new HashMap<>();

        Sue(String input) {
            String[] tokens = Arrays.stream(input.split("[ :,]"))
                    .filter(s -> s.length() > 0)
                    .toArray(String[]::new);
            name = "Sue " + tokens[1];
            for (int i = 2; i < tokens.length; i += 2) {
                facts.put(tokens[i], parseInt(tokens[i + 1]));
            }
        }
    }


}
