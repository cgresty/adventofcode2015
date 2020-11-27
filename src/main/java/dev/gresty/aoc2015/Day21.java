package dev.gresty.aoc2015;

import lombok.Data;

import java.util.*;

import static dev.gresty.aoc2015.Utils.msg;
import static java.lang.Math.max;

public class Day21 {

    static final int PLAYER = 0;
    static final int BOSS = 1;

    static final int WEAPON = 0;
    static final int ARMOR = 1;
    static final int RING = 2;

    static final Item NOTHING = new Item("Nothing", 0, 0, 0);

    static final Item[][] items = new Item[3][];
    static {
        items[WEAPON] = new Item[5];
        items[ARMOR] = new Item[5];
        items[RING] = new Item[6];

        items[WEAPON][0] = new Item("Dagger", 8, 4, 0);
        items[WEAPON][1] = new Item("Shortsword", 10, 5, 0);
        items[WEAPON][2] = new Item("Warhammer", 25, 6, 0);
        items[WEAPON][3] = new Item("Longsword", 40, 7, 0);
        items[WEAPON][4] = new Item("Greataxe", 74, 8, 0);

        items[ARMOR][0] = new Item("Leather", 13, 0, 1);
        items[ARMOR][1] = new Item("Chainmail", 31, 0, 2);
        items[ARMOR][2] = new Item("Splintmail", 53, 0, 3);
        items[ARMOR][3] = new Item("Bandedmail", 75, 0, 4);
        items[ARMOR][4] = new Item("Platemail", 102, 0, 5);

        items[RING][0] = new Item("Defence +1", 20, 0, 1);
        items[RING][1] = new Item("Damage +1", 25, 1, 0);
        items[RING][2] = new Item("Defence +2", 40, 0, 2);
        items[RING][3] = new Item("Damage +2", 50, 2, 0);
        items[RING][4] = new Item("Defence +3", 80, 0, 3);
        items[RING][5] = new Item("Damage +3", 100, 3, 0);
    }

    static final TreeSet<Outfit> outfits = calculateOutfits();

    public static void main(String[] args) {
        Combatant boss = new Combatant(100, 8, 2);
        Outfit cheapestWinningOutfit = outfits.stream()
                .filter(o -> fight(new Combatant(100, o.damage, o.armor), boss))
                .findFirst().orElse(null);
        msg("Cheapest winning outfit cost " + cheapestWinningOutfit.cost + " - " + cheapestWinningOutfit);

        NavigableSet<Outfit> outfitsDesc = outfits.descendingSet();
        Outfit mostExpensiveLosingOutfit = outfitsDesc.stream()
                .filter(o -> !fight(new Combatant(100, o.damage, o.armor), boss))
                .findFirst().orElse(null);

        msg("Most expensive losing outfit cost " + mostExpensiveLosingOutfit.cost + " - " + mostExpensiveLosingOutfit);

    }

    static TreeSet<Outfit> calculateOutfits() {
        var outfits = new TreeSet<Outfit>((a, b) -> {
            int diff = a.cost - b.cost;
            return (diff == 0 ? a.hashCode() - b.hashCode() : diff); // cos comparator == 0 => equals. Duh.
        });

        var components = new Item[4];
        for (int w = 0; w < 5; w++) {
            components[0] = items[WEAPON][w];
            for (int a = -1; a < 5; a++) {
                components[1] = a == -1 ? NOTHING : items[ARMOR][a];
                for (int r1 = -1; r1 < 6; r1++) {
                    components[2] = r1 == -1 ? NOTHING : items[RING][r1];
                    for (int r2 = r1 + 1; r2 < 7; r2++) {
                        components[3] = r2 == 6 ? NOTHING : items[RING][r2];
                        outfits.add(new Outfit(components));
                    }
                }
            }
        }
        return outfits;
    }

    static boolean fight(Combatant player, Combatant boss) {
        var hp = new int[2];
        hp[PLAYER] = player.maxhp;
        hp[BOSS] = boss.maxhp;
        var damage = new int[2];
        damage[PLAYER] = max(player.damage - boss.armor, 1);
        damage[BOSS] = max(boss.damage - player.armor, 1);
        int attacker = PLAYER;
        int defender = BOSS;
        int winner = -1;
        do {
            hp[defender] -= damage[attacker];
            if (hp[defender] <= 0) {
                winner = attacker;
            } else {
                attacker = 1 - attacker;
                defender = 1 - defender;
            }
        } while (winner == -1);
        return winner == PLAYER;
    }

    @Data
    static class Combatant {
        final int maxhp;
        final int damage;
        final int armor;
    }

    @Data
    static class Outfit {
        final List<Item> items;
        final int cost;
        final int damage;
        final int armor;

        public Outfit(Item[] items) {
            this.items = Arrays.asList(items.clone());
            cost = this.items.stream().mapToInt(Item::cost).sum();
            damage = this.items.stream().mapToInt(Item::damage).sum();
            armor = this.items.stream().mapToInt(Item::armor).sum();
        }

        public String toString() {
            StringBuilder s = new StringBuilder();
            items.stream().filter(item -> item != NOTHING).forEach(item -> s.append(item.name + " "));
            s.append("damage=" + damage + " ");
            s.append("armor=" + armor + " ");
            s.append("cost=" + cost + " ");
            return s.toString();
        }
    }

    @Data
    static class Item {
        final String name;
        final int cost;
        final int damage;
        final int armor;
    }
}


