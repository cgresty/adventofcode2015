package dev.gresty.aoc2015;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static dev.gresty.aoc2015.Utils.*;

public class Day08 {

    public static void main(String[] args) throws IOException {
        test("\"\"", 0, Day08::decodedLength);
        test("\"abc\"", 3, Day08::decodedLength);
        test("\"aaa\\\"aaa\"", 7, Day08::decodedLength);
        test("\"\\x27\"", 1, Day08::decodedLength);

        execute1();

        test("\"\"", 6, Day08::encodedLength);
        test("\"abc\"", 9, Day08::encodedLength);
        test("\"aaa\\\"aaa\"", 16, Day08::encodedLength);
        test("\"\\x27\"", 11, Day08::encodedLength);

        execute2();
    }

    static void execute1() throws IOException {
        try (var in = readFile("day08.txt")) {
            AtomicInteger encodedLength = new AtomicInteger();
            AtomicInteger decodedLength = new AtomicInteger();
            in.lines().forEach(l -> {
                encodedLength.addAndGet(l.length());
                decodedLength.addAndGet(decodedLength(l));
            });

            msg("Decoded length decrease is " + (encodedLength.get() - decodedLength.get()));
        }
    }

    static void execute2() throws IOException {
        try (var in = readFile("day08.txt")) {
            AtomicInteger encodedLength = new AtomicInteger();
            AtomicInteger decodedLength = new AtomicInteger();
            in.lines().forEach(l -> {
                decodedLength.addAndGet(l.length());
                encodedLength.addAndGet(encodedLength(l));
            });

            msg("Encoded length increase is " + (encodedLength.get() - decodedLength.get()));
        }
    }

    static int decodedLength(String literal) {
        return decode(literal).length();
    }

    static int encodedLength(String literal) {
        int length = 2; // assume will surround with ".."
        for (char c : literal.toCharArray()) {
            if (c == '\"') length++;
            if (c == '\\') length++;
            length++;
        }
        return length;
    }

    static String decode(String literal) {
        StringBuilder inmem = new StringBuilder();
        String escape = "";
        for (int i = 1; i < literal.length() - 1; i++) { // assume bounded by ".."
            if (escape.length() != 0) {
                escape = escape + literal.charAt(i);
                if (escape.equals("\\\\")) {
                    inmem.append("\\");
                    escape = "";
                } else if (escape.equals("\\\"")) {
                    inmem.append("\"");
                    escape = "";
                } else if (escape.startsWith("\\x")) {
                    if (escape.length() == 4) {
                        inmem.append((char) Integer.parseInt(escape.substring(2), 16));
                        escape = "";
                    }
                } else if (escape.length() == 2) {
                    throw new IllegalArgumentException("Unexpected escape sequence " + escape + " in string " + literal);
                }
            } else if (literal.charAt(i) == '\\') {
                escape = "\\";
            } else {
                inmem.append(literal.charAt(i));
            }
        }

        return inmem.toString();
    }

}
