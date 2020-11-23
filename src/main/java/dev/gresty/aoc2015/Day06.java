package dev.gresty.aoc2015;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.function.Function;

import static dev.gresty.aoc2015.Utils.msg;
import static dev.gresty.aoc2015.Utils.readFile;
import static java.lang.Math.max;

public class Day06 {

    static int[][] lights = new int[1000][1000];

    public static void main(String[] args) throws IOException {
        try (var in = readFile("day06.txt")){
            in.lines().forEach(cmd -> Instruction.decode(cmd).apply(lights));
        }
        int on = 0;
        for (int x = 0; x < 1000; x++) {
            for (int y = 0; y < 1000; y++) {
                on += lights[x][y];
            }
        }
        msg("There are " + on + " lights switched on");
    }

    enum Operation {
        TURN_ON("turn on", x -> x + 1),
        TOGGLE("toggle", x -> x + 2),
        TURN_OFF("turn off", x -> max(0, x - 1));

        String asString;
        Function<Integer, Integer> function;

        Operation(String asString, Function<Integer, Integer> function) {
            this.asString = asString;
            this.function = function;
        }

        static Operation decode(String command) {
            if (command.startsWith(TURN_ON.asString)) return TURN_ON;
            if (command.startsWith(TOGGLE.asString)) return TOGGLE;
            if (command.startsWith(TURN_OFF.asString)) return TURN_OFF;
            throw new IllegalArgumentException("Unrecognized command " + command);
        }

        public int apply(int input) {
            return function.apply(input);
        }
    }

    @RequiredArgsConstructor
    static class Instruction {
        final Operation operation;
        final Location from;
        final Location to;

        static Instruction decode(String command) {
            CommandBuffer cmd = new CommandBuffer(command);
            return new Instruction(cmd.readOperation(),
                    new Location(cmd.readInteger(), cmd.readInteger()),
                    new Location(cmd.readInteger(), cmd.readInteger()));
        }

        void apply(int[][] lights) {
            for (int x = from.x(); x <= to.x(); x++) {
                for (int y = from.y(); y <= to.y(); y++) {
                    lights[x][y] = operation.apply(lights[x][y]);
                }
            }
        }
    }

    @Data
    static class Location {
        private final int x;
        private final int y;
    }

    @RequiredArgsConstructor
    static class CommandBuffer {
        private final String command;
        private int index;

        Operation readOperation() {
            Operation operation = Operation.decode(command);
            index = operation.asString.length() + 1;
            return operation;
        }

        int readInteger() {
            nextInteger();
            int nextNonInt = command.length();
            for (int i = index; i < command.length(); i++) {
                if (command.charAt(i) < '0' || command.charAt(i) > '9') {
                    nextNonInt = i;
                    break;
                }
            }

            int integer = Integer.parseInt(command, index, nextNonInt, 10);
            index = nextNonInt;
            return integer;
        }

        void nextInteger() {
            int nextInt = command.length();
            for (int i = index; i < command.length(); i++) {
                if (command.charAt(i) >= '0' && command.charAt(i) <= '9') {
                    nextInt = i;
                    break;
                }
            }
            index = nextInt;
        }
    }
}
