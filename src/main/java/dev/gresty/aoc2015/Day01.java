package dev.gresty.aoc2015;

import java.io.IOException;
import java.io.InputStream;

import static dev.gresty.aoc2015.Utils.streamFile;

public class Day01 {
    public static void main(String[] args) throws IOException {
        try (InputStream in = streamFile("day01a.txt")) {
            int floor = 0;
            int next;
            int pos = 0;
            int firstBasement = 0;
            while ((next = in.read()) >= 0) {
                pos++;
                floor = floor + (next == '(' ? 1 : -1);
                if (firstBasement == 0 && floor == -1) {
                    firstBasement = pos;
                }
            }
            System.out.println("We finish in floor " + floor);
            System.out.println("We first hit the basement with step " + firstBasement);
        }
    }
}
