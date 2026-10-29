package net.schblackos.testmod.stats;

import net.schblackos.testmod.network.style.DamageNumberStyle;

/** Runs in its own source set without a game instance or third-party test dependencies. */
public final class StatsMathTest {
    private static int assertions;

    private StatsMathTest() {
    }

    public static void main(String[] args) {
        chanceBoundaries();
        defenceBoundaries();
        resistanceBoundaries();
        independentDamageChannels();
        damageOrder();
        damageNumberStyle();
        System.out.println("StatMathTest: " + assertions + " assertions passed.");
    }

    private static void chanceBoundaries() {
        check(!StatsMath.succeeds(0.0D, 0.0D),
                "Zero chance must never succeed");

        check(!StatsMath.succeeds(-5.0D, 0.0D),
                "Negative chance must behave as zero");

        check(StatsMath.succeeds(100.0D, Math.nextDown(1.0D)),
                "100% must always succeed");

        check(StatsMath.succeeds(150.0D, Math.nextDown(1.0D)),
                "Chance is capped at 100% (WILL CHANGE TO OVERCRIT)");

        check(StatsMath.succeeds(1.0D, 0.0D),
                "1% includes a zero roll");

        check(StatsMath.succeeds(1.0D, Math.nextDown(0.01D)),
                "1% includes rolls below 0.01");

        check(!StatsMath.succeeds(1.0D, 0.01D),
                "The 1% threshold itself must fail");

        check(!StatsMath.succeeds(1.0D, 0.5D),
                "1 means 1%, not a guaranteed success");


        double combinedChance = 1.0D + 2.0D + 4.0D;
        check(StatsMath.succeeds(combinedChance, Math.nextDown(0.07D)),
                "Gear chances add together");

        check(!StatsMath.succeeds(combinedChance, 0.07D),
                "Combined chance uses the same strict boundary");
    }

    private static void defenceBoundaries() {
        equal(5.0F, StatsMath.afterDefence(5.0F, 0.0D),
                "Zero defence");

        equal(3.0F, StatsMath.afterDefence(5.0F, 2.0D),
                "Defence subtracts flat damage");

        equal(0.0F, StatsMath.afterDefence(5.0F, 5.0D),
                "Defence equals damage");

        equal(0.0F, StatsMath.afterDefence(5.0F, 20.0D),
                "Defence exceeding damage cannot heal");

        equal(5.0F, StatsMath.afterDefence(5.0F, -2.0D),
                "Negative defence cannot increase damage");

        equal(0.0F, StatsMath.afterDefence(0.0F, 2.0D),
                "Zero incoming damage with defence");

        equal(0.0F, StatsMath.afterDefence(-5.0F, 0.0D),
                "Negative incoming damage is clamped");

        equal(4.5F, StatsMath.afterDefence(5.0F, 0.5D),
                "Fractional defence");
    }


    private static void resistanceBoundaries() {
        equal(10.0F, StatsMath.afterResistance(10.0F, 0.0D),
                "Zero resistance");

        equal(7.0F, StatsMath.afterResistance(10.0F, 3.0D),
                "Resistance subtracts flat magic damage");

        equal(0.0F, StatsMath.afterResistance(10.0F, 10.0D),
                "Resistance equals magic damage");

        equal(0.0F, StatsMath.afterResistance(10.0F, 50.0D),
                "Resistance exceeding magic damage cannot heal");

        equal(50.0F, StatsMath.afterResistance(200.0F, 150.0D),
                "Flat resistance can exceed 100");

        equal(10.0F, StatsMath.afterResistance(10.0F, -10.0D),
                "Negative resistance is ignored");

        equal(0.0F, StatsMath.afterResistance(0.0F, 50.0D),
                "Zero incoming magic damage with resistance");

        equal(0.0F, StatsMath.afterResistance(-5.0F, 50.0D),
                "Negative incoming magic damage is clamped");

        equal(4.0F, StatsMath.afterResistance(10.0F, 1.0D + 2.0D + 3.0D),
                "Gear resistance adds together");

        equal(9.5F, StatsMath.afterResistance(10.0F, 0.5D),
                "Fractional resistance");
    }

    private static void independentDamageChannels() {
        equal(10.0F, StatsMath.combinedDamage(10.0F, 0.0F, 0.0D, 100.0D),
                "Resistance does not reduce physical damage");

        equal(6.0F, StatsMath.combinedDamage(0.0F, 6.0F, 100.0D, 0.0D),
                "Defence does not reduce magic damage");

        equal(11.0F, StatsMath.combinedDamage(10.0F, 6.0F, 3.0D, 2.0D),
                "Mixed hits reduce each damage type independently");

        equal(4.0F, StatsMath.combinedDamage(10.0F, 6.0F, 20.0D, 2.0D),
                "Excess defence cannot reduce remaining magic damage");

        equal(7.0F, StatsMath.combinedDamage(10.0F, 6.0F, 3.0D, 20.0D),
                "Excess resistance cannot reduce remaining physical damage");

        equal(0.0F, StatsMath.combinedDamage(10.0F, 6.0F, 10.0D, 6.0D),
                "Fully reduced damage cannot heal");

        equal(0.0F, StatsMath.combinedDamage(-10.0F, -6.0F, 0.0D, 0.0D),
                "Negative damage channels cannot heal");

        equal(16.0F, StatsMath.combinedDamage(10.0F, 6.0F, -3.0D, -2.0D),
                "Negative defences do not amplify either damage type");
    }

    private static void damageOrder() {
        equal(7.5F, StatsMath.criticalDamage(5.0F),
                "A 5 damage critical hit deals 7.5");

        equal(0.0F, StatsMath.criticalDamage(0.0F),
                "Zero damage remains zero when critical");

        equal(12.0F, StatsMath.combinedDamage(StatsMath.criticalDamage(10.0F), 0.0F, 3.0D, 50.0D),
                "Physical critical damage is calculated before defence");

        equal(7.0F, StatsMath.combinedDamage(0.0F, StatsMath.criticalDamage(6.0F), 50.0D, 2.0D),
                "Magic critical damage is calculated before resistance");
    }

    private static void damageNumberStyle() {
        check(DamageNumberStyle.color(false, false) == 0xFFFFFF00,
                "Physical damage numbers must be yellow");

        check(DamageNumberStyle.color(false, true) == 0xFFFFFF00,
                "Physical critical damage numbers retain yellow");

        check(DamageNumberStyle.color(true, false) == 0xFF0000FF,
                "Magic damage numbers must be opaque blue");

        check(DamageNumberStyle.color(true, true) == 0xFFAA00FF,
                "Magic critical damage numbers must be opaque purple");

        check("5".equals(DamageNumberStyle.format(5.0F)),
                "Whole damage does not need a decimal");

        check("7.5".equals(DamageNumberStyle.format(7.5F)),
                "Critical damage retains its fraction");

        check("<0.1".equals(DamageNumberStyle.format(0.01F)),
                "A small positive hit must not display as zero damage");

        check("0.1".equals(DamageNumberStyle.format(0.1F)),
                "The smallest exact displayed tenth stays numeric");

        check("5.6".equals(DamageNumberStyle.format(5.6000004F)),
                "Damage numbers hide floating point calculation noise");


        java.util.Locale original = java.util.Locale.getDefault();
        try {
            java.util.Locale.setDefault(java.util.Locale.GERMANY);
            check("7.5".equals(DamageNumberStyle.format(7.5F)),
                    "Damage formatting remains consistent with a decimal-comma system locale");
        } finally {
            java.util.Locale.setDefault(original);
        }
    }

    private static void check(boolean condition, String message) {
        assertions++;
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void equal(float expected, float actual, String message) {
        check(Math.abs(expected - actual) < 0.00001F,
                message + ": expected " + expected + ", got " + actual);
    }
}
