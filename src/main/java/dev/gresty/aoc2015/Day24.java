package dev.gresty.aoc2015;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static dev.gresty.aoc2015.Utils.msg;
import static dev.gresty.aoc2015.Utils.readFile;
import static java.lang.Integer.MAX_VALUE;

public class Day24 {

    public static void main(String[] args) throws IOException {
        test();
        execute();
    }

    static void test() {
        var giftsInGroup = new ArrayList<Integer>();
        var availableGifts = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        generateCombinations(giftsInGroup, availableGifts, 0,
                l -> l.size() <= 3,
                l -> l.size() == 3,
                l-> msg(l.toString()));
    }

    static void execute() throws IOException {
        try (var in = readFile("day24.txt")) {
            int[] giftWeights = in.lines().mapToInt(Integer::parseInt).toArray();
            Problem problem = new Problem(giftWeights, 3);
            msg("Smallest group QE is " + problem.solve());
            Problem problem2 = new Problem(giftWeights, 4);
            msg("Smallest group QE is " + problem2.solve());
        }
    }

    static void generateCombinations(List<Integer> combo, List<Integer> available, int index,
                                     Predicate<List<Integer>> isValid,
                                     Predicate<List<Integer>> isComplete,
                                     Consumer<List<Integer>> onComplete) {
        if (isComplete.test(combo)) {
            onComplete.accept(combo);
        } else if (index < available.size() && isValid.test(combo)) {
            for (int i = index; i < available.size(); i++) {
                combo.add(available.get(i));
                generateCombinations(combo, available, i + 1, isValid, isComplete, onComplete);
                combo.remove(combo.size() - 1);
            }
        }
    }

    static class Problem {
        final int[] giftWeights;
        final int groupWeight;

        int smallestGroupSize = MAX_VALUE;
        long smallestGroupQE = MAX_VALUE;

        Problem(int[] giftWeights, int groups) {
            this.giftWeights = giftWeights;
            groupWeight = Arrays.stream(giftWeights).sum() / groups;
        }

        long solve() {
            var available = IntStream.range(0, giftWeights.length)
                    .boxed()
                    .collect(Collectors.toList());

            generateCombinations(new ArrayList<>(), available, 0,
                    g -> weight(g) <= groupWeight,
                    g -> weight(g) == groupWeight,
                    this::checkForSmallest);

            return smallestGroupQE;
        }

        int weight(List<Integer> group) {
            return group.stream()
                    .map(i -> giftWeights[i])
                    .mapToInt(Integer::intValue)
                    .sum();
        }

        void checkForSmallest(List<Integer> group) {
            if (group.size() < smallestGroupSize ||
                    (group.size() == smallestGroupSize && qe(group) < smallestGroupQE)) {
                smallestGroupSize = group.size();
                smallestGroupQE = qe(group);
            }
        }

        long qe(List<Integer> group) {
            return group.stream()
                    .map(i -> giftWeights[i])
                    .mapToLong(Integer::longValue)
                    .reduce(1, (a, b) -> a * b);
        }
    }
}
