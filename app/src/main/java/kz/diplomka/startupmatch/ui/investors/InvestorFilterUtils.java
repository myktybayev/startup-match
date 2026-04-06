package kz.diplomka.startupmatch.ui.investors;

import androidx.annotation.NonNull;

/**
 * Горизонталь чиптер мен сала токендері үшін ортақ сүзгі утилиталары.
 */
public final class InvestorFilterUtils {

    private InvestorFilterUtils() {
    }

    public static boolean containsTag(@NonNull String[] tags, @NonNull String key) {
        for (String t : tags) {
            if (t != null && t.equalsIgnoreCase(key)) {
                return true;
            }
        }
        return false;
    }

    public static boolean industryContains(@NonNull InvestorListItem item, @NonNull String needle) {
        if (containsTag(item.filterTags, needle)) {
            return true;
        }
        for (String ind : item.industries) {
            if (ind == null) {
                continue;
            }
            if (ind.equalsIgnoreCase(needle)) {
                return true;
            }
            for (String token : ind.split("[\\s,]+")) {
                if (token.equalsIgnoreCase(needle)) {
                    return true;
                }
            }
        }
        return false;
    }

    /** chipIndex 0 = «Барлығы». */
    public static boolean matchesChipIndex(
            @NonNull InvestorListItem item,
            int chipIndex,
            @NonNull String[] chipLabels) {
        if (chipIndex == 0) {
            return true;
        }
        if (chipIndex < 0 || chipIndex >= chipLabels.length) {
            return true;
        }
        return industryContains(item, chipLabels[chipIndex]);
    }
}
