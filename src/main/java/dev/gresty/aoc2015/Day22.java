package dev.gresty.aoc2015;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static dev.gresty.aoc2015.Day22.Result.*;
import static dev.gresty.aoc2015.Utils.msg;
import static dev.gresty.aoc2015.Utils.test;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Math.*;

public class Day22 {

    enum Result { PLAYER_WON, BOSS_WON, NOONE_WON, INVALID_SEQ}

    static Spell[] spells = new Spell[5];
    static {
        spells[0] = new Spell("Magic Missile", 53, (p, b) -> b.receiveAttack(4));
        spells[1] = new Spell("Drain", 73, (p, b) -> {
            p.heal(2);
            return b.receiveAttack(2);
        });
        spells[2] = new Spell("Shield", 113, (p, b) -> p.startEffect(
                new Effect("Shield", 6, (p1, b1) -> p1.boostArmor(7), (p2, b2) -> p2.boostArmor(0))));
        spells[3] = new Spell("Poison", 173, (p, b) -> p.startEffect(
                new Effect("Poison", 6, (p1, b1) -> b1.receiveAttack(3), null)));
        spells[4] = new Spell("Recharge", 229, (p, b) -> p.startEffect(
                new Effect("Recharge", 5, (p1, b1) -> p1.addMana(101), null)));
    }

    public static void main(String[] args) {
        test(1, true, Day22::test1);
        test(2, true, Day22::test2);
        test(3, true, Day22::test3);
        test(4, true, Day22::test4);
        execute(false);
        execute(true);
    }

    static boolean test1(Integer ignore) {
        Player p = new Player(10, 250);
        Boss b = new Boss(13, 8);
        int[] seq = new int[] {3, 0};
        return play(seq, p, b, false) >= 0;
    }

    static boolean test2(Integer ignore) {
        Player p = new Player(10, 250);
        Boss b = new Boss(14, 8);
        int[] seq = new int[] {4, 2, 1, 3, 0};
        return play(seq, p, b, false) >= 0;
    }

    static boolean test3(Integer len) {
        next(null, new int[len], 0, 5, (s, i) -> {msg(Arrays.toString(i)); return 2;});
        return true;
    }

    static boolean test4(Integer ignore) {
        Player p = new Player(10, 250);
        Boss b = new Boss(14, 8);
        State state = new State(p, b, false);
        next(state, new int[5], 0, 4, Day22::testSequence);
        msg("Winning sequence is " + Arrays.toString(state.cheapestSequence) + " with cost " + state.cheapestManna);
        return true;
    }

    static void execute(boolean hard) {
        Player p = new Player(50, 500);
        Boss b = new Boss(51, 9);
        State state = new State(p, b, hard);
        next(state, new int[12], 0, 4, Day22::testSequence);
        msg("Winning sequence is " + Arrays.toString(state.cheapestSequence) + " with cost " + state.cheapestManna);

        p.reset();
        b.reset();
        msg("" + play(state.cheapestSequence, p, b, hard));
    }

    // 0 = Success
    // x = Sequence failed/succeeded at step x
    static int next(State state, int[] sequence, int step, int max, BiFunction<State, int[], Integer> function) {
        if (step >= sequence.length) {
            return function.apply(state, sequence);
        }
        for (int i = 0; i <= max; i++) {
            sequence[step] = i;
            int result = next(state, sequence, step + 1, max, function);
            if (result > 0 && result < step) return result;
        }
        return 0;
    }

    static int testSequence(State state, int[] sequence) {
        try {
            int result = play(sequence, state.player, state.boss, state.hard);
            int manna = manna(sequence, result + 1);
            if (result >= 0 && manna < state.cheapestManna) {
                state.cheapestSequence = Arrays.copyOf(sequence, result + 1);
                state.cheapestManna = manna;
                msg(Arrays.toString(state.cheapestSequence) + " with cost " + state.cheapestManna);
            }
            return abs(result);
        } catch (InvalidSequenceException e) {
            return e.step;
        } catch (NoOneWonException e) {
            return 0;
        }
    }

    static int manna(int[] sequence, int length) {
        int manna = 0;
        for (int i = 0; i < length; i++) {
            manna += spells[sequence[i]].cost;
        }
        return manna;
    }

    @Data
    static class State {
        final Player player;
        final Boss boss;
        final boolean hard;
        int[] cheapestSequence;
        int cheapestManna = MAX_VALUE;
    }

    static int play(int[] sequence, Player player, Boss boss, boolean hard) {
        player.reset();
        boss.reset();

        for (int i = 0; i < sequence.length; i++) {
            int s = sequence[i];
            //msg("-- Player turn " + i + "--");
            //msg(player.report());
            //msg(boss.report());

            if (hard) player.hp--;
            if (player.isDead()) {
                //msg("PLAYER DIES at step " + i);
                //msg("");
                return -i;
            }
            player.applyEffects(boss);
            if (boss.isDead()) {
                //msg("BOSS DIES at step " + i);
                //msg("");
                return i;
            }
            switch (player.cast(spells[s], boss)) {
                case PLAYER_WON: return i;
                case BOSS_WON: return -i;
                case INVALID_SEQ: throw new InvalidSequenceException(i);
                case NOONE_WON:
            }
            if (boss.isDead()) {
                //msg("BOSS DIES at step " + i);
                //msg("");
                return i;
            }
            //msg("");

            //msg("-- Boss turn " + i + "--");
            //msg(player.report());
            //msg(boss.report());

            player.applyEffects(boss);
            if (boss.isDead()) {
                //msg("BOSS DIES at step " + i);
                //msg("");
                return i;
            }
            boss.attack(player);
            if (player.isDead()) {
                //msg("PLAYER DIES at step " + i);
                //msg("");
                return -i;
            }
            //msg("");
        }
        //msg("NO-ONE WON by step " + sequence.length);
        //msg("");

        throw new NoOneWonException(); // No-one died, need longer sequence, maybe.
    }

    @Data
    static class Player {
        final int maxhp;
        final int startmana;
        int hp;
        int mana;
        int armor;
        final Set<Effect> activeEffects = new HashSet<>();

        void reset() {
            hp = maxhp;
            mana = startmana;
            armor = 0;
            activeEffects.clear();
        }

        Result cast(Spell spell, Boss boss) {
            if (mana < spell.cost) return BOSS_WON;
            //msg("Player casts " + spell.name);
            mana -= spell.cost;
            if (!spell.apply(this, boss)) return INVALID_SEQ; // Abandon this sequence of events
            return NOONE_WON;
        }

        void boostArmor(int boost) {
            armor = boost;
        }

        void addMana(int extra) {
            mana += extra;
        }

        boolean startEffect(Effect effect) {
            if (activeEffects.contains(effect)) return false; // Abandon this sequence of events
            effect.reset();
            activeEffects.add(effect);
            return true;
        }

        void applyEffects(Boss b) {
            armor = 0;
            var effects = new HashSet<>(activeEffects);
            for (var e : effects) {
                if (e.apply(this, b)) {
                    activeEffects.remove(e);
                }
            }
        }

        void receiveAttack(int damage) {
            int actualDamage = max(1, damage - armor);
            hp -= actualDamage;
            //msg("Boss attacks for " + actualDamage + " damage");
        }

        void heal(int healing) {
            hp += healing;
            hp = min(hp, maxhp);
        }

        boolean isDead() {
            return hp <= 0;
        }

        String report() {
            return "- Player has " + hp + " hit points, " + armor + " armor, " + mana + " mana";
        }
    }

    @Data
    static class Boss {
        final int maxhp;
        final int damage;
        int hp;

        void reset() {
            hp = maxhp;
        }

        void attack(Player player) {
            player.receiveAttack(damage);
        }

        boolean receiveAttack(int damage) {
            hp -= damage;
            return true;
        }

        boolean isDead() {
            return hp <= 0;
        }

        String report() {
            return "- Boss has " + hp + " hit points";
        }

    }

    @Data
    static class Spell {
        final String name;
        final int cost;
        final BiFunction<Player, Boss, Boolean> action;

        boolean apply(Player p, Boss b) {
            return action.apply(p, b);
        }
    }

    @Data
    static class Effect {
        final String name;
        final int duration;
        final BiConsumer<Player, Boss> action;
        final BiConsumer<Player, Boss> endAction;

        @EqualsAndHashCode.Exclude
        int timer;

        void reset() {
            timer = duration;
        }

        boolean apply(Player p, Boss b) {
            timer--;
            //msg("Applied effect " + name + " - timer is now " + timer);
            action.accept(p, b);
            if (timer == 0 && endAction != null) {
                endAction.accept(p, b);
            }
            return timer == 0;
        }
    }

    static class NoOneWonException extends RuntimeException {}

    @Data
    static class InvalidSequenceException extends RuntimeException {
        final int step;
    }
}
