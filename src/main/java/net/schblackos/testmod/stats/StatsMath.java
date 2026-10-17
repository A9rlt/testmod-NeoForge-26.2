package net.schblackos.testmod.stats;

/** Combat calculations shared by the stat events. Chance values use 1 = 1%. */
public final class StatsMath {
    private StatsMath() {
    }

    /** Tests a random sample in [0, 1) against a chance expressed as a percentage. */
    public static boolean succeeds(double chancePercent, double randomValue) {
        return randomValue < clampPercent(chancePercent) / 100.0D;
    }

    public static float afterDefence(float damage, double defence) {
        return (float) Math.max(0.0D, damage - Math.max(0.0D, defence));
    }

    public static float afterResistance(float damage, double resistancePercent) {
        return (float) (Math.max(0.0F, damage) * (1.0D - clampPercent(resistancePercent) / 100.0D));
    }

    public static float criticalDamage(float damage) {
        return damage * 1.5F;
    }

    private static double clampPercent(double value) {
        return Math.max(0.0D, Math.min(100.0D, value));
    }
}
