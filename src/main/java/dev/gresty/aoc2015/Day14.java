package dev.gresty.aoc2015;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static dev.gresty.aoc2015.Utils.msg;
import static dev.gresty.aoc2015.Utils.readFile;
import static java.lang.Integer.*;

public class Day14 {

    static final int RACE_LENGTH = 2503;

    public static void main(String[] args) throws IOException {

        try (var in = readFile("day14.txt")) {
            var reindeer = in.lines().map(Reindeer::decode)
                    .collect(Collectors.toList());
            race1(reindeer);
            reindeer.forEach(Reindeer::reset);
            race2(reindeer);
        }
    }

    static void race1(List<Reindeer> reindeer) {
        int furthest = reindeer.stream().mapToInt(r -> r.flyFor(RACE_LENGTH).distanceFlown)
                .max().orElse(0);
        msg("The fastest reindeer flew " + furthest + "km");
    }

    static void race2(List<Reindeer> reindeer) {
        var points = new HashMap<Reindeer, Integer>(reindeer.size());
        for (int i = 0; i < RACE_LENGTH; i++) {
            int max = reindeer.stream().map(Reindeer::flyForOneSecond)
                    .mapToInt(Reindeer::distanceFlown)
                    .max().orElse(0);
            reindeer.stream().filter(r -> r.distanceFlown == max)
                    .forEach(r -> points.compute(r, (k, p) -> p == null ? 1 : (p + 1)));
        }
        int maxScore = points.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        msg("The highest scoring reindeer scored " + maxScore);
    }

    @Data
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    static class Reindeer {
        @EqualsAndHashCode.Include final String name;
        final int speed;  // km/sec
        final int maxFlyingTime; // sec
        final int requiredRestingTime; // sec
        int distanceFlown;
        int hopTime;
        int restingTime;
        boolean needsRest;

        static Reindeer decode(String input) {
            var tokens = input.split(" ");
            return new Reindeer(tokens[0], parseInt(tokens[3]), parseInt(tokens[6]), parseInt(tokens[13]));
        }

        void reset() {
            distanceFlown = 0;
            hopTime = 0;
            restingTime = 0;
            needsRest = false;
        }

        Reindeer flyForOneSecond() {
            if (!needsRest) {
                hopTime++;
                distanceFlown += speed;
                if (hopTime == maxFlyingTime) {
                    needsRest = true;
                    restingTime = 0;
                }
            } else {
                restingTime++;
                if (restingTime == requiredRestingTime) {
                    needsRest = false;
                    hopTime = 0;
                }
            }
            return this;
        }

        Reindeer flyFor(int time) {
            for (int i = 0; i < time; i++) {
                flyForOneSecond();
            }
            return this;
        }
    }
}
