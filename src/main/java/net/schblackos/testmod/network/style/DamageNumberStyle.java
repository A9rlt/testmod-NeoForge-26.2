package net.schblackos.testmod.network.style;

import java.util.Locale;

/** Damage number appearance, also usable by the standalone calculation checks. */
public final class DamageNumberStyle {
    public static final int PHYSICAL_COLOR = 0xFFFFFFFF;
    public static final int MAGICAL_COLOR = 0x0000FF;
    public static final int CRITICAL_COLOR = 0xFFFFFF00;

    private DamageNumberStyle() {
    }

    public static int color(boolean critical) {
        return critical ? CRITICAL_COLOR : PHYSICAL_COLOR;
    }

    public static String format(float damage) {
        if (damage > 0.0F && damage < 0.1F) {
            return "<0.1";
        }
        String formatted = String.format(Locale.ROOT, "%.1f", damage);
        return formatted.endsWith(".0") ? formatted.substring(0, formatted.length() - 2) : formatted;
    }
}
