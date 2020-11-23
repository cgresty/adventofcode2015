package dev.gresty.aoc2015;

import lombok.Data;

import java.io.IOException;
import java.util.HashSet;

import static dev.gresty.aoc2015.Utils.streamFile;

public class Day03 {

    public static void main(String[] args) throws IOException {
        year1();
        year2();
    }

    private static void year1()  throws IOException {
        var visited = new HashSet<>();
        var location = new Location2(0, 0);
        try (var in = streamFile("day03.txt")) {
            visited.add(location);
            int direction;
            while ((direction = in.read()) >= 0) {
                location = location.move((char) direction);
                visited.add(location);
            }
        }
        System.out.println("Year 1: Santa has visited " + visited.size() + " houses");
    }

    private static void year2()  throws IOException {
        final int SANTA = 0;
        final int ROBO = 1;

        var visited = new HashSet<Location2>();

        var location = new Location2[2];
        location[SANTA] = new Location2(0, 0);
        location[ROBO] = new Location2(0, 0);
        var toMove = SANTA;

        try (var in = streamFile("day03.txt")) {
            visited.add(location[SANTA]);
            int direction;
            while ((direction = in.read()) >= 0) {
                location[toMove] = location[toMove].move((char) direction);
                visited.add(location[toMove]);
                toMove = 1 - toMove;
            }
        }
        System.out.println("Year 2: Santa and Robo hav visited " + visited.size() + " houses");
    }

    @Data
    static class Location2 {
        private static final Location2 UP = new Location2(0, 1);
        private static final Location2 DOWN = new Location2(0, -1);
        private static final Location2 LEFT = new Location2(1, 0);
        private static final Location2 RIGHT = new Location2(-1, 0);

        private final int x;
        private final int y;

        public Location2 move(Location2 by) {
            return new Location2(x + by.x, y + by.y);
        }

        public Location2 move(char dir) {
            return switch (dir) {
                case '^' -> move(UP);
                case 'v' -> move(DOWN);
                case '<' -> move(LEFT);
                case '>' -> move(RIGHT);
                default -> throw new IllegalArgumentException("Unexpected direction indicator " + dir);
            };
        }
    }
}
