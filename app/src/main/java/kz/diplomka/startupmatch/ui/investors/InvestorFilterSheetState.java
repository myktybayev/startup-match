package kz.diplomka.startupmatch.ui.investors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Инвесторлар bottom sheet сүзгісінің күйі (Figma FilterBottomSheet).
 */
public final class InvestorFilterSheetState implements Serializable {

    private static final long serialVersionUID = 1L;

    @Nullable
    public final String stage;
    @NonNull
    public final ArrayList<String> industries;
    @Nullable
    public final String ticketTier;
    @Nullable
    public final String geo;
    @Nullable
    public final String status;
    public final boolean activeInvestorsOnly;
    public final boolean challengesOnly;

    public InvestorFilterSheetState(
            @Nullable String stage,
            @NonNull ArrayList<String> industries,
            @Nullable String ticketTier,
            @Nullable String geo,
            @Nullable String status,
            boolean activeInvestorsOnly,
            boolean challengesOnly) {
        this.stage = stage;
        this.industries = industries;
        this.ticketTier = ticketTier;
        this.geo = geo;
        this.status = status;
        this.activeInvestorsOnly = activeInvestorsOnly;
        this.challengesOnly = challengesOnly;
    }

    @NonNull
    public static InvestorFilterSheetState empty() {
        return new InvestorFilterSheetState(
                null,
                new ArrayList<>(),
                null,
                null,
                null,
                false,
                false);
    }

    public boolean isEmpty() {
        return stage == null
                && industries.isEmpty()
                && ticketTier == null
                && geo == null
                && status == null
                && !activeInvestorsOnly
                && !challengesOnly;
    }

    public static boolean matches(@NonNull InvestorListItem item, @NonNull InvestorFilterSheetState s) {
        if (s.isEmpty()) {
            return true;
        }
        if (s.stage != null && !InvestorFilterUtils.containsTag(item.filterTags, s.stage)) {
            return false;
        }
        if (!s.industries.isEmpty()) {
            boolean any = false;
            for (String ind : s.industries) {
                if (InvestorFilterUtils.industryContains(item, ind)) {
                    any = true;
                    break;
                }
            }
            if (!any) {
                return false;
            }
        }
        if (s.ticketTier != null && !tierMatches(item.ticketMinK, item.ticketMaxK, s.ticketTier)) {
            return false;
        }
        if (s.geo != null && !geoMatches(item, s.geo)) {
            return false;
        }
        if (s.status != null && !statusMatches(item, s.status)) {
            return false;
        }
        if (s.activeInvestorsOnly && item.views < 50) {
            return false;
        }
        if (s.challengesOnly && item.challenges <= 0) {
            return false;
        }
        return true;
    }

    private static boolean tierMatches(int minK, int maxK, @NonNull String tier) {
        switch (tier) {
            case "1_10":
                return minK >= 1 && maxK <= 10;
            case "10_50":
                return rangesOverlap(minK, maxK, 10, 50);
            case "50_plus":
                return maxK >= 50;
            default:
                return true;
        }
    }

    private static boolean rangesOverlap(int aMin, int aMax, int bMin, int bMax) {
        return aMin <= bMax && aMax >= bMin;
    }

    private static boolean geoMatches(@NonNull InvestorListItem item, @NonNull String geo) {
        for (String g : item.geoTags) {
            if (g != null && g.equalsIgnoreCase(geo)) {
                return true;
            }
        }
        return false;
    }

    private static boolean statusMatches(@NonNull InvestorListItem item, @NonNull String status) {
        switch (status) {
            case "Guest":
                return item.badge == InvestorListItem.BadgeKind.GUEST;
            case "Verified":
                return item.badge == InvestorListItem.BadgeKind.VERIFIED;
            case "Experienced":
                return item.badge == InvestorListItem.BadgeKind.EXPERIENCED;
            default:
                return true;
        }
    }

    /** Қолданылған сүзгі + горизонталь чип индексі бойынша сәйкес инвесторлар санын есептеу. */
    public static int countMatching(
            @NonNull List<InvestorListItem> all,
            int chipIndex,
            @NonNull String[] chipLabels,
            @NonNull InvestorFilterSheetState sheet) {
        int n = 0;
        for (InvestorListItem item : all) {
            if (InvestorFilterUtils.matchesChipIndex(item, chipIndex, chipLabels)
                    && matches(item, sheet)) {
                n++;
            }
        }
        return n;
    }
}
