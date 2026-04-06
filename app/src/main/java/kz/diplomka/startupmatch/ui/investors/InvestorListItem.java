package kz.diplomka.startupmatch.ui.investors;

import androidx.annotation.NonNull;

import java.io.Serializable;

public final class InvestorListItem implements Serializable {

    private static final long serialVersionUID = 1L;

    public final int avatarResId;
    @NonNull
    public final String name;
    @NonNull
    public final BadgeKind badge;
    @NonNull
    public final String role;
    @NonNull
    public final String[] industries;
    /**
     * Chip сүзгісі үшін тегтер: Idea, MVP, Growth, TicketSize т.с.с.
     * Сала тегтері (AI, FinTech) негізінен {@link #industries} арқылы сәйкес келеді.
     */
    @NonNull
    public final String[] filterTags;
    /** Инвестиция көлемі ($k), bottom sheet сүзгісі үшін. */
    public final int ticketMinK;
    public final int ticketMaxK;
    /**
     * География кілттері: Kazakhstan, Central Asia, Global, UAE, CEE т.с.с.
     */
    @NonNull
    public final String[] geoTags;
    @NonNull
    public final String ticketAndGeo;
    @NonNull
    public final String quote;
    public final int views;
    public final int challenges;

    public enum BadgeKind {
        VERIFIED,
        GUEST,
        EXPERIENCED
    }

    public InvestorListItem(
            int avatarResId,
            @NonNull String name,
            @NonNull BadgeKind badge,
            @NonNull String role,
            @NonNull String[] industries,
            @NonNull String[] filterTags,
            int ticketMinK,
            int ticketMaxK,
            @NonNull String[] geoTags,
            @NonNull String ticketAndGeo,
            @NonNull String quote,
            int views,
            int challenges) {
        this.avatarResId = avatarResId;
        this.name = name;
        this.badge = badge;
        this.role = role;
        this.industries = industries;
        this.filterTags = filterTags;
        this.ticketMinK = ticketMinK;
        this.ticketMaxK = ticketMaxK;
        this.geoTags = geoTags;
        this.ticketAndGeo = ticketAndGeo;
        this.quote = quote;
        this.views = views;
        this.challenges = challenges;
    }
}
