package dev.gresty.aoc2015;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

import static dev.gresty.aoc2015.Utils.msg;
import static dev.gresty.aoc2015.Utils.readFile;
import static java.lang.Integer.MAX_VALUE;

public class Day17 {

    public static void main(String[] args) throws IOException {
        Utils.test(null, 4, Day17::test);
        msg("Found " + execute() + " combinations");
        msg("Found " + execute2() + " ways to fill the minimum number of containers");
    }

    static int test(Object ignore) {
        var testFoundCount = new AtomicInteger();
        enumerate(new int[] {20, 15, 10, 5, 5}, 25, (i, j) -> testFoundCount.incrementAndGet());
        return testFoundCount.get();
    }

    static int execute() throws IOException {
        try (var in = readFile("day17.txt")) {
            int[] sizes = in.lines().mapToInt(Integer::parseInt)
                    .toArray();
            var foundCount = new AtomicInteger();
            enumerate(sizes, 150, (i, j) -> foundCount.incrementAndGet());
            return foundCount.get();
        }
    }

    static int execute2() throws IOException {
        try (var in = readFile("day17.txt")) {
            int[] sizes = in.lines().mapToInt(Integer::parseInt)
                    .toArray();
            var foundCount = new AtomicInteger();
            var containerCombinations= new HashMap<Integer, Integer>();
            enumerate(sizes, 150, (ignored, selected) -> {
                foundCount.incrementAndGet();
                int numContainers = Arrays.stream(selected).sum();
                containerCombinations.compute(numContainers, (k, v) -> v == null ? 1 : v + 1);
            });
            int minContainers = containerCombinations.keySet().stream().mapToInt(Integer::intValue).min().orElse(0);
            return containerCombinations.get(minContainers);
        }
    }

    static void enumerate(int[] sizes, int target, BiConsumer<int[], int[]> onFound) {
        next(sizes, target, Arrays.stream(sizes).sum(), new int[sizes.length], 0, onFound);
    }

    static void next(int[] sizes, int target, int available, int[] selected, int index, BiConsumer<int[], int[]> onFound) {
        if (available < target) return;
        if (index == selected.length) {
            if (target == 0) onFound.accept(sizes, selected);
        } else {
            for (int state = 0; state < 2; state++) {
                selected[index] = state;
                next(sizes, target - state * sizes[index], available - sizes[index], selected, index + 1, onFound);
            }
        }
    }
}
