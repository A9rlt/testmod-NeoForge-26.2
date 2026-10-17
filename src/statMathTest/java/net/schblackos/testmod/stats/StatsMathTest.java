package net.schblackos.testmod.stats;

/** Runs in its own source set without a game instance or third-party test dependencies. */
public final class StatsMathTest {
    private static int assertions;

    private StatsMathTest() {
    }

    public static void main(String[] args) {
        chanceBoundaries();
        defenceBoundaries();
        resistanceBoundaries();
        damageOrder();
        damageNumberStyle();
        System.out.println("StatMathTest: " + assertions + " assertions passed.");
    }

    private static void chanceBoundaries() {
        check(!StatsMath.succeeds(0.0D, 0.0D), "Zero chance must never succeed");
        check(!StatsMath.succeeds(-5.0D, 0.0D), "Negative chance must behave as zero");
        check(StatsMath.succeeds(100.0D, Math.nextDown(1.0D)), "100% must always succeed");
        check(StatsMath.succeeds(150.0D, Math.nextDown(1.0D)), "Chance is capped at 100%");
        check(StatsMath.succeeds(1.0D, 0.0D), "1% includes a zero roll");
        check(StatsMath.succeeds(1.0D, Math.nextDown(0.01D)), "1% includes rolls below 0.01");
        check(!StatsMath.succeeds(1.0D, 0.01D), "The 1% threshold itself must fail");
        check(!StatsMath.succeeds(1.0D, 0.5D), "1 means 1%, not a guaranteed success");

        double combinedChance = 1.0D + 2.0D + 4.0D;
        check(StatsMath.succeeds(combinedChance, Math.nextDown(0.07D)), "Gear chances add together");
        check(!StatsMath.succeeds(combinedChance, 0.07D), "Combined chance uses the same strict boundary");
    }

    private static void defenceBoundaries() {
        equal(5.0F, StatsMath.afterDefence(5.0F, 0.0D), "Zero defence");
        equal(3.0F, StatsMath.afterDefence(5.0F, 2.0D), "Defence subtracts flat damage");
        equal(0.0F, StatsMath.afterDefence(5.0F, 5.0D), "Defence equals damage");
        equal(0.0F, StatsMath.afterDefence(5.0F, 20.0D), "Defence exceeding damage cannot heal");
        equal(5.0F, StatsMath.afterDefence(5.0F, -2.0D), "Negative defence cannot increase damage");
        equal(0.0F, StatsMath.afterDefence(0.0F, 2.0D), "Zero incoming damage with defence");
        equal(0.0F, StatsMath.afterDefence(-5.0F, 0.0D), "Negative incoming damage is clamped");
        equal(4.5F, StatsMath.afterDefence(5.0F, 0.5D), "Fractional defence");
    }

    private static void resistanceBoundaries() {
        equal(10.0F, StatsMath.afterResistance(10.0F, 0.0D), "Zero resistance");
        equal(9.0F, StatsMath.afterResistance(10.0F, 10.0D), "Resistance uses percentage units");
        equal(5.0F, StatsMath.afterResistance(10.0F, 50.0D), "50% resistance halves damage");
        equal(0.0F, StatsMath.afterResistance(10.0F, 100.0D), "100% resistance blocks all damage");
        equal(0.0F, StatsMath.afterResistance(10.0F, 150.0D), "Resistance caps at 100%");
        equal(10.0F, StatsMath.afterResistance(10.0F, -10.0D), "Negative resistance is ignored");
        equal(0.0F, StatsMath.afterResistance(0.0F, 50.0D), "Zero incoming damage with resistance");
        equal(0.0F, StatsMath.afterResistance(-5.0F, 50.0D), "Resistance cannot produce healing");
        equal(6.0F, StatsMath.afterResistance(10.0F, 10.0D + 15.0D + 15.0D), "Gear resistance adds together");
    }

    private static void damageOrder() {
        equal(7.5F, StatsMath.criticalDamage(5.0F), "A 5 damage critical hit deals 7.5");
        equal(0.0F, StatsMath.criticalDamage(0.0F), "Zero damage remains zero when critical");
        float critical = StatsMath.criticalDamage(10.0F);
        float defended = StatsMath.afterDefence(critical, 3.0D);
        equal(6.0F, StatsMath.afterResistance(defended, 50.0D), "Critical, flat defence, then resistance");
        equal(0.0F, StatsMath.afterResistance(StatsMath.afterDefence(5.0F, 6.0D), 25.0D),
                "Fully defended damage stays zero after resistance");
    }

    private static void damageNumberStyle() {
        check(DamageNumberStyle.color(false) == 0xFFFFFFFF, "Normal damage numbers must be white");
        check(DamageNumberStyle.color(true) == 0xFFFFFF00, "Critical damage numbers must be yellow");
        check("5".equals(DamageNumberStyle.format(5.0F)), "Whole damage does not need a decimal");
        check("7.5".equals(DamageNumberStyle.format(7.5F)), "Critical damage retains its fraction");
        check("<0.1".equals(DamageNumberStyle.format(0.01F)),
                "A small positive hit must not display as zero damage");
        check("0.1".equals(DamageNumberStyle.format(0.1F)), "The smallest exact displayed tenth stays numeric");
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
