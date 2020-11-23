package dev.gresty.aoc2015;

import java.io.IOException;

import static dev.gresty.aoc2015.Utils.*;

public class Day18 {

    static final char ON = '#';
    static final char OFF = '.';

    public static void main(String[] args) throws IOException {
        test(4, 4, Day18::test1);
        test(5, 17, Day18::test2);
        msg("Number of lights on after 100 turns: " + execute1());
        msg("Number of lights on after 100 turns (with stuck lights): " + execute2());


    }

    static int test1(int turns) {
        return execute(testLights(), turns, false);
    }

    static int test2(int turns) {
        return execute(testLights(), turns, true);
    }

    static char[][] testLights() {
        char[][] lights = new char[6][];
        lights[0] = ".#.#.#".toCharArray();
        lights[1] = "...##.".toCharArray();
        lights[2] = "#....#".toCharArray();
        lights[3] = "..#...".toCharArray();
        lights[4] = "#.#..#".toCharArray();
        lights[5] = "####..".toCharArray();
        return lights;
    }

    static int execute1() throws IOException {
        return execute(lights(), 100, false);
    }

    static int execute2() throws IOException {
        return execute(lights(), 100, true);
    }

    static char[][] lights() throws IOException {
        try (var in = readFile("day18.txt")) {
            return in.lines().map(String::toCharArray).toArray(char[][]::new);
        }
    }

    static int execute(char[][] lights, int turns, boolean stuckLights) {
        char[][][] buffer = new char[2][][];
        buffer[0] = lights;
        buffer[1] = new char[lights.length][lights[0].length];
        int numLightsOn = 0;

        for (int y = 0; y < lights.length; y++) {
            for (int x = 0; x < lights[y].length; x++) {
                if (stuck(stuckLights, lights, y, x)) {
                    lights[y][x] = ON;
                }
            }
        }

        for (int i = 0; i < turns; i++) {
            char[][] from = buffer[i % 2];
            char[][] to = buffer[1 - (i % 2)];
            numLightsOn = 0;
            for (int y = 0; y < from.length; y++) {
                for (int x = 0; x < from[y].length; x++) {
                    if (stuck(stuckLights, from, y, x)) {
                        to[y][x] = ON;
                    } else {
                        char current = from[y][x];
                        int n = neighboursOn(from, y, x);
                        if (current == OFF) {
                            to[y][x] = (n == 3) ? ON : OFF;
                        } else {
                            to[y][x] = (n == 2 || n == 3) ? ON : OFF;
                        }
                    }
                    if (to[y][x] == ON) numLightsOn++;
                }
            }
        }
        return numLightsOn;
    }

    static int neighboursOn(char[][] array, int y, int x) {
        int count = 0;
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                if (dy == 0 && dx == 0) continue;
                int yy = y + dy;
                int xx = x + dx;
                if (yy>= 0 && yy < array.length
                        && xx >= 0 && xx < array[yy].length
                        && array[yy][xx] == '#') {
                    count++;
                }
            }
        }
        return count;
    }

    static boolean stuck(boolean stuckLights, char[][] lights, int y, int x) {
        return (stuckLights && (
                (y == 0 && x == 0) ||
                        (y == 0 && x == lights[0].length - 1) ||
                        (y == lights.length - 1 && x == 0) ||
                        (y == lights.length - 1 && x == lights[y].length - 1)));

    }
}
