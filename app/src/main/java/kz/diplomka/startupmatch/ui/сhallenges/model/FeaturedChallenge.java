package kz.diplomka.startupmatch.ui.сhallenges.model;

import androidx.annotation.NonNull;

/**
 * Үстіңгі featured тапсырмалар (инвестордың ерекше challenge-дері).
 */
public final class FeaturedChallenge {

    private final long id;
    @NonNull
    private final FeaturedBadge badge;
    @NonNull
    private final String title;
    @NonNull
    private final String description;
    @NonNull
    private final String deadline;

    public FeaturedChallenge(long id,
                             @NonNull FeaturedBadge badge,
                             @NonNull String title,
                             @NonNull String description,
                             @NonNull String deadline) {
        this.id = id;
        this.badge = badge;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
    }

    public long getId() {
        return id;
    }

    @NonNull
    public FeaturedBadge getBadge() {
        return badge;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    @NonNull
    public String getDeadline() {
        return deadline;
    }
}
