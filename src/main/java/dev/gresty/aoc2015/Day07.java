package dev.gresty.aoc2015;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static dev.gresty.aoc2015.Day07.Operator.*;
import static dev.gresty.aoc2015.Utils.msg;
import static dev.gresty.aoc2015.Utils.readFile;

public class Day07 {

    static Map<String, Operation> circuit = new HashMap<>();

    public static void main(String[] args) throws IOException {

        try (var in = readFile("day07.txt")) {
            in.lines().map(Operation::new)
                    .forEach(o -> circuit.put(o.output(), o));
        }

        int a1 = circuit.get("a").evaluate();
        msg("a = " + a1);

        circuit.values().forEach(Operation::reset);
        circuit.put("b", new Operation("" + a1 + " -> b"));
        int a2 = circuit.get("a").evaluate();
        msg("a = " + a2);

    }

    static class Operation {

        final Operator operator;
        final Parameter[] parameters;
        final String output;

        Integer result;

        public Operation (String cmd) {
            String[] tokens = cmd.split(" ");
            output = tokens[tokens.length - 1];
            if (tokens[0].equals("NOT")) {
                operator = NOT;
                parameters = new Parameter[1];
                parameters[0] = Parameter.create(tokens[1]);
            } else if (tokens.length == 3) {
                operator = IDENTITY;
                parameters = new Parameter[1];
                parameters[0] = Parameter.create(tokens[0]);
            } else {
                operator = Operator.valueOf(tokens[1]);
                parameters = new Parameter[2];
                parameters[0] = Parameter.create(tokens[0]);
                parameters[1] = Parameter.create(tokens[2]);
            }
        }

        public String output() {
            return output;
        }

        public void reset() {
            result = null;
        }

        int evaluate() {
            if (result == null) {
                result = operator.apply(parameters);
            }
            return result;
        }
    }

    enum Operator {
        IDENTITY(p -> p[0].get()),
        AND(p -> (p[0].get() & p[1].get()) & 0xffff),
        OR(p -> (p[0].get() | p[1].get()) & 0xffff),
        NOT(p -> ~(p[0].get()) & 0xffff),
        LSHIFT(p -> (p[0].get() << p[1].get()) & 0xffff),
        RSHIFT(p -> (p[0].get() >>> p[1].get()) & 0xffff);

        Function<Parameter[], Integer> function;

        Operator(Function<Parameter[], Integer> function) {
            this.function = function;
        }

        public int apply(Parameter[] parameters) {
            return function.apply(parameters);
        }

    }

    interface Parameter {
        int get();

        static Parameter create(String asString) {
            try {
                return new ConstantParameter(Integer.parseInt(asString));
            } catch (NumberFormatException e) {
                return new ReferenceParameter(asString);
            }
        }
    }

    @RequiredArgsConstructor
    static class ReferenceParameter implements Parameter {
        final String name;

        public int get() {
            return circuit.get(name).evaluate();
        }
    }

    @RequiredArgsConstructor
    static class ConstantParameter implements Parameter {
        final int value;

        public int get() {
            return value;
        }
    }
}
