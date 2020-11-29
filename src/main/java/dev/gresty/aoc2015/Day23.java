package dev.gresty.aoc2015;

import lombok.ToString;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;

import static dev.gresty.aoc2015.Utils.msg;
import static dev.gresty.aoc2015.Utils.readFile;
import static java.lang.Integer.parseInt;

public class Day23 {
    public static void main(String[] args) throws IOException {
        test();
        execute();
    }

    static void test() throws IOException {
        String source = """
                        inc a
                        jio a, +2
                        tpl a
                        inc a""";
        Computer c = new Computer();
        try (var in = new BufferedReader(new StringReader(source))) {
            c.load(in);
            c.run();
            msg(c.register("a") == 2 ? "TEST OK" : "TEST FAILED");
        }
    }

    static void execute() throws IOException {
        try (var in = readFile("day23.txt")) {
            Computer c = new Computer();
            c.load(in);
            c.run();
            msg("Register b ends up with " + c.register("b"));
            c.reset();
            c.register("a", 1);
            c.run();
            msg("If register a starts as 1, then register b ends up with " + c.register("b"));
        }
    }

    static class Computer {
        final int[] registers = new int[2];
        Instruction[] program;
        int pc = 0;

        void load(BufferedReader in) {
            program = in.lines().map(Instruction::new).toArray(Instruction[]::new);
        }

        void run() {
            pc = 0;
            while (pc < program.length) {
                pc += execute(program[pc]);
            }
        }

        private int execute(Instruction instr) {
            String[] p = instr.parameters;
            int jmp = 1;
            switch (instr.command) {
                case "hlf":
                    register(p[0], register(p[0]) / 2);
                    break;
                case "tpl":
                    register(p[0], register(p[0]) * 3);
                    break;
                case "inc":
                    register(p[0], register(p[0]) + 1);
                    break;
                case "jmp":
                    jmp = parseInt(p[0]);
                    break;
                case "jie":
                    if (register(p[0]) % 2 == 0)
                        jmp = parseInt(p[1]);
                    break;
                case "jio":
                    if (register(p[0]) == 1)
                        jmp = parseInt(p[1]);
                    break;
            }
            return jmp;
        }

        void register(String r, int value) {
            registers[r.charAt(0) - 'a'] = value;
        }

        int register(String r) {
            return registers[r.charAt(0) - 'a'];
        }

        void reset() {
            pc = 0;
            registers[0] = 0;
            registers[1] = 0;
        }
    }

    @ToString
    static class Instruction {
        final String command;
        final String[] parameters;

        Instruction(String line) {
            command = line.substring(0, line.indexOf(' '));
            parameters = Arrays.stream(line.substring(command.length()).split(","))
                    .map(String::trim)
                    .toArray(String[]::new);
        }
    }

}
