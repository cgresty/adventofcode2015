package dev.gresty.aoc2015;

import lombok.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static dev.gresty.aoc2015.Utils.*;

public class Day13 {

    public static void main(String[] args) throws IOException {
        execute();
    }

    static Map<Neighbour, Integer> neighbours = new HashMap<>();

    static void execute() throws IOException {
        var people = new HashSet<String>();


        try (var in = readFile("day13.txt")) {
            in.lines().map(Day13::decode).forEach(n -> {
                neighbours.compute(n, (n1, i) -> n.happiness() + (i == null ? 0 : i));
                people.add(n.a);
                people.add(n.b);
            });

            heapsAlgorithm(people.toArray(new String[0]), Day13::happiness);
            msg("Optimal happiness is " + optimalHappiness + " - " + Arrays.toString(optimalPeople));
            people.add("Me");
            optimalHappiness = 0;
            heapsAlgorithm(people.toArray(new String[0]), Day13::happiness);
            msg("Optimal happiness (me included) is " + optimalHappiness + " - " + Arrays.toString(optimalPeople));


        }
    }

    static Neighbour decode(String line) {
        String[] tokens = line.split(" ");
        String a = tokens[0];
        String b = tokens[10];
        b = b.substring(0, b.length() - 1);
        int happiness = Integer.parseInt(tokens[3]) * (tokens[2].equals("gain") ? 1 : -1);
        return new Neighbour(a, b, happiness);
    }

    static String[] optimalPeople;
    static int optimalHappiness;

    static void happiness(String[] people) {
        var happiness = 0;
        for(int i = 0; i < people.length; i++) {
            String a = (i == 0 ? people[people.length - 1] : people[i - 1]);
            String b = people[i];
            var n = new Neighbour(a, b, 0);
            var h = neighbours.get(n);
            happiness += ( h == null ? 0 : h);
        }

        if (happiness > optimalHappiness) {
            optimalHappiness = happiness;
            optimalPeople = Arrays.copyOf(people, people.length);
        }
    }

    @Data
    static class Neighbour {
        final String a;
        final String b;
        final int happiness;

        public boolean equals(Object other ) {
            if (other instanceof Neighbour == false) return false;
            var otherN = (Neighbour) other;
            return ((otherN.a.equals(a) && otherN.b.equals(b)) ||
                    (otherN.a.equals(b) && otherN.b.equals(a)));
        }

        public int hashCode() {
            return a.hashCode() + b.hashCode();
        }
    }
}
