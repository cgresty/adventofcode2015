package dev.gresty.aoc2015;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static dev.gresty.aoc2015.Utils.*;
import static java.lang.Integer.parseInt;

public class Day15 {

    static final int CAPACITY = 0;
    static final int DURABILITY = 1;
    static final int FLAVOR = 2;
    static final int TEXTURE = 3;

    static Ingredient[] ingredients;

    public static void main(String[] args) throws IOException {

        ingredients = testIngredients();
        test(new int[] {44, 56}, 62842880, Day15::score);

        try (var in = readFile("day15.txt")) {
            ingredients = in.lines().map(Ingredient::decode)
                    .collect(Collectors.toList()).toArray(new Ingredient[0]);
            enumerate(4, 100, Day15::evaluate);
            msg("The highest cookie score is " + highestScore);
            highestScore = 0;
            enumerate(4, 100, Day15::evaluate500);
            msg("The highest cookie score (at 500 calories) is " + highestScore);
        }
    }

    static int highestScore = 0;

    static void evaluate(int[] recipe) {
        int score = score(recipe);
        if (score > highestScore) {
            highestScore = score;
        }
    }

    static void evaluate500(int[] recipe) {
        int score = score(recipe);
        if (score > highestScore && calories(recipe) == 500) {
            highestScore = score;
        }
    }

    static void enumerate(int numElements, int total, Consumer<int[]> function) {
        int[] value = new int[numElements];
        next(value, 0, total, function);
    }

    static void next(int[] value, int index, int total, Consumer<int[]> function) {
        if (index == value.length - 1) {
            value[index] = total - sum(value, index);
            if (value[index] >= 0) {
                function.accept(value);
            }
        } else {
            int max = total - sum(value, index);
            for (int v = 0; v <= max; v++) {
                value[index] = v;
                next(value, index + 1, total, function);
            }
        }
    }

    static int sum(int[] value, int toIndex) {
        int sum = 0;
        for (int i = 0; i < toIndex; i++) {
            sum += value[i];
        }
        return sum;
    }

    static int score(int[] recipe) {
        int[] property = new int[ingredients[0].properties.length];
        for (int p = 0; p < property.length; p++) {
            for (int i = 0; i < ingredients.length; i++) {
                property[p] += ingredients[i].properties[p] * recipe[i];
            }
            if (property[p] <= 0) property[p] = 0;
        }

        return Arrays.stream(property).reduce(1, (i, j) -> i * j);
    }

    static int calories(int[] recipe) {
        int calories = 0;
        for (int i = 0; i < ingredients.length; i++) {
            calories += ingredients[i].calories * recipe[i];
        }
        return calories;
    }

    static Ingredient[] testIngredients() {
        Ingredient[] i = new Ingredient[2];
        i[0] = new Ingredient("Butterscotch", new int[]{-1, -2, 6, 3}, 8);
        i[1] = new Ingredient("Cinnamon", new int[]{2, 3, -2, -1}, 3);
        return i;
    }

    @Data
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    static class Ingredient {
        @EqualsAndHashCode.Include final String name;
        final int[] properties;
        final int calories;

        static Ingredient decode(String input) {
            String[] tokens = input.split("[ :,]");

            return new Ingredient(tokens[0], new int[] {
                    parseInt(tokens[3]),
                    parseInt(tokens[6]),
                    parseInt(tokens[9]),
                    parseInt(tokens[12])},
                    parseInt(tokens[15]));
        }
    }
}
