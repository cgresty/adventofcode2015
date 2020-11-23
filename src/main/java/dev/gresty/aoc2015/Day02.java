package dev.gresty.aoc2015;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import static dev.gresty.aoc2015.Utils.streamFile;
import static java.lang.Math.*;

public class Day02 {
    public static void main(String[] args) throws IOException {
        try (var in = streamFile("day02.txt")) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            WrappingAndRibbon total = reader.lines().map(Day02::dimensions)
                    .map(dim -> new WrappingAndRibbon(area(dim), length(dim)))
                    .collect(WrappingAndRibbon::new,
                            (a, n) -> {
                                a.wrapping(a.wrapping() + n.wrapping());
                                a.ribbon(a.ribbon() + n.ribbon());
                            },
                            (a, n) -> {
                                a.wrapping(a.wrapping() + n.wrapping());
                                a.ribbon(a.ribbon() + n.ribbon());
                            });
            System.out.println("Wrapping area required = " + total.wrapping());
            System.out.println("Ribbon length required = " + total.ribbon());

        }
    }


    static int[] dimensions(String asString) {
        return Arrays.stream(asString.split("x"))
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    static int area(int[] d) {
        int a1 = d[0] * d[1];
        int a2 = d[1] * d[2];
        int a3 = d[2] * d[0];
        int min = min(min(a1, a2), a3);
        return 2 * (a1 + a2 + a3) + min;
    }

    static int length(int[] d) {
        int longest = 0;
        if (d[1] > d[longest]) longest = 1;
        if (d[2] > d[longest]) longest = 2;
        int length = 0;
        if (longest != 0) length += d[0];
        if (longest != 1) length += d[1];
        if (longest != 2) length += d[2];
        length *= 2;
        length += d[0] * d[1] * d[2];
        return length;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class WrappingAndRibbon {
        private int wrapping;
        private int ribbon;
    }
}
