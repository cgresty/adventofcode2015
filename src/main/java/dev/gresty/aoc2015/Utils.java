package dev.gresty.aoc2015;

import java.io.*;
import java.net.URL;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Utils {

    public static InputStream streamFile(String filename) throws IOException {
        return resource(filename).openStream();
    }

    public static BufferedReader readFile(String filename) throws IOException {
        return new BufferedReader(new InputStreamReader(resource(filename).openStream()));
    }

    public static URL resource(String filename) throws FileNotFoundException {
        URL url = Utils.class.getClassLoader().getResource("dev/gresty/aoc2015/" + filename);
        if (url == null) throw new FileNotFoundException("dev/gresty/aoc2015/" + filename);
        return url;
    }

    public static void msg(String msg) {
        System.out.println(msg);
    }

    public static <I> boolean testBool(I input, boolean output, Predicate<I> predicate) {
        Function<I, Boolean> f = i -> predicate.test(i);
        return test(input, output, f);
    }

    public static <I, O> boolean test(I input, O output, Function<I, O> function) {
        msg("Testing input " + input);
        try {
            O result = function.apply(input);
            if (result.equals(output)) {
                msg("Result = " + result + " - SUCCESS!");
                return true;
            }
            else {
                msg("Result = " + result + " - FAILED!");
                return false;
            }
        } catch (Exception e) {
            msg("Exception thrown: FAILED");
            e.printStackTrace();
            return false;
        }
    }


    static <T> void heapsAlgorithm(T[] a, Consumer<T[]> f) {
        generate(a.length, a, f);
    }

    static <T> void generate(int k, T[] a, Consumer<T[]> f) {
        if (k == 1) {
            f.accept(a);
        } else {
            generate(k - 1, a, f);

            for (int i = 0; i < k - 1; i++) {
                if (k % 2 == 0) {
                    swap(a, i, k - 1);
                } else {
                    swap(a, 0, k -1);
                }
                generate(k - 1, a, f);
            }
        }
    }

    static void swap(Object[] a, int i1, int i2) {
        Object x = a[i1];
        a[i1] = a[i2];
        a[i2] = x;
    }
}
