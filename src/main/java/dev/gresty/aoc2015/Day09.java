package dev.gresty.aoc2015;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static dev.gresty.aoc2015.Utils.*;

public class Day09 {

    static int[][] distances = new int[8][8]; // yeah, assuming 8 destinations...
    static List<String> destinations = new ArrayList<>();
    static String[] idealRoute;
    static int idealDistance = Integer.MAX_VALUE;

    public static void main(String[] args) throws IOException {
        readTable();
        heapsAlgorithm(destinations.toArray(new String[0]), Day09::shortestRoute);
        msg("Shortest route (" + idealDistance + "): " + Arrays.toString(idealRoute));
        heapsAlgorithm(destinations.toArray(new String[0]), Day09::longestRoute);
        msg("Longest route (" + idealDistance + "): " + Arrays.toString(idealRoute));
    }

    static void readTable() throws IOException {
        try (var in = readFile("day09.txt")) {
            in.lines().forEach(l -> {
                String[] el = l.split(" ");
                int from = indexOf(el[0]);
                int to = indexOf(el[2]);
                distances[from][to] = Integer.parseInt(el[4]);
                distances[to][from] = distances[from][to];
            });
        }
    }

    static void shortestRoute(String[] route) {
        findRoute(route, d -> d < idealDistance);
    }

    static void longestRoute(String[] route) {
        findRoute(route, d -> d > idealDistance);
    }

    static void findRoute(String[] route, Predicate<Integer> condition) {
        int distance = 0;
        for (int i = 1; i < route.length; i++) {
            distance += distances
                    [indexOf(route[i - 1])]
                    [indexOf(route[i])];
        }
        if (condition.test(distance)) {
            idealDistance = distance;
            idealRoute = Arrays.copyOf(route, route.length);
        }
    }

    static int indexOf(String destination) {
        int index = destinations.indexOf(destination);
        if (index == -1) {
            destinations.add(destination);
            index = destinations.size() - 1;
        }
        return index;
    }


}
