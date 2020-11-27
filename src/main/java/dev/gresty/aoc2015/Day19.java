package dev.gresty.aoc2015;

import lombok.Data;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static dev.gresty.aoc2015.Utils.*;

public class Day19 {
    public static void main(String[] args) throws IOException {
        test("HOH", 4, Day19::dotest);
        test("HOHOHO", 7, Day19::dotest);
        execute();
        test("HOH", 3, Day19::doTest2);
        test("HOHOHO", 6, Day19::doTest2);
        execute2();
    }

    static int dotest(String molecule) {
        Input i = new Input(new HashMap<>(), molecule);
        i.replacements.put("H", List.of("HO", "OH"));
        i.replacements.put("O", List.of("HH"));
        return countAllReplacements(i);
    }

    static int doTest2(String molecule) {
        Input i = new Input(new HashMap<>(), molecule);
        i.replacements.put("e", List.of("H", "O"));
        i.replacements.put("H", List.of("HO", "OH"));
        i.replacements.put("O", List.of("HH"));
        return fabricate(i.replacements, i.molecule);
    }

    static void execute() throws IOException {
        msg("Number of generated molecules after one replacement: " + countAllReplacements(loadFromFile()));
    }

    static void execute2() throws IOException {
        var i = loadFromFile();
        // See https://www.reddit.com/r/adventofcode/comments/3xflz8/day_19_solutions/cy4etju/
        var elements = toElements(i.molecule);
        msg("Target molecule fabricated in " +
                (elements.size()
                        - 2 * count("Rn", elements)
                        - 2 * count("Y", elements)
                        - 1));
    }

    static long count(String element, List<String> elements) {
        return elements.stream().filter(e -> e.equals(element)).count();
    }

    static List<String> toElements(String molecule) {
        var elements = new ArrayList<String>();
        while(molecule.length() > 0) {
            String e = firstElement(molecule);
            elements.add(e);
            molecule = molecule.substring(e.length());
        }
        return elements;
    }
    static String firstElement(String molecule) {
        if (molecule.length() > 1 && molecule.charAt(1) >= 'a' && molecule.charAt(1) <= 'z') {
            return molecule.substring(0, 2);
        } else {
            return molecule.substring(0, 1);
        }
    }

    static Input loadFromFile() throws IOException {
        try (var in = readFile("day19.txt")) {
            var r = new HashMap<String, List<String>>();
            String l;
            while((l = in.readLine()).length() > 0) {
                var tok = l.split(" ");
                var k = tok[0];
                var v = tok[2];
                var vlist = r.computeIfAbsent(k, x -> new ArrayList<String>());
                vlist.add(v);
            }
            var m = in.readLine();
            return new Input(r, m);
        }
    }

    static int fabricate(Map<String, List<String>> replacements, String target) {
        var winner = new AtomicReference<Task>();
        var latch = new CountDownLatch(1);
        var exe = Executors.newSingleThreadExecutor();

        exe.submit(new Task(new HashSet<>(), winner, exe, replacements, latch, target, 0, "e"));

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        exe.shutdownNow();

        msg("Target molecule fabricated in " + winner.get().steps);
        return winner.get().steps;
    }

    static void next() {

    }

    @Data
    static class Task implements Runnable {
        final Set<String> foundSoFar;
        final AtomicReference<Task> winner;
        final ExecutorService exe;
        final Map<String, List<String>> replacements;
        final CountDownLatch latch;
        final String target;
        final int steps;
        final String molecule;

        @Override
        public void run()  {
            if (molecule.equals(target)) {
                winner.set(this);
                latch.countDown();
                return;
            }
            if (molecule.length() > target.length()) {
                return;
            }
            generateAllReplacements(new Input(replacements, molecule)).stream()
                    .filter(m -> !foundSoFar.contains(m))
                    .forEach(m -> {
                        foundSoFar.add(m);
                        exe.submit(new Task(foundSoFar, winner, exe, replacements, latch, target, steps + 1, m));
                    });
        }
    }

    static int countAllReplacements(Input input) {
        return generateAllReplacements(input).size();
    }

    static Set<String> generateAllReplacements(Input input) {
        var m = input.molecule;
        var mlen = m.length();
        var r = input.replacements;
        var generated = new HashSet<String>();
        for (int i = 0; i < mlen; i++) {
            int ii = i;
            r.forEach((k, l) -> {
                var klen = k.length();
                if (klen <= mlen - ii && m.substring(ii, ii + klen).equals(k)) {
                    l.forEach(v -> {
                        String mnew = input.molecule.substring(0, ii) + v;
                        if (ii + klen < input.molecule.length()) {
                            mnew += m.substring(ii + klen);
                        }
                        generated.add(mnew);
                    });
                }
            });
        }
        return generated;
    }

    @Data
    static class Input {
        final Map<String, List<String>> replacements;
        final String molecule;
    }
}
