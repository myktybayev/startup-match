package kz.diplomka.startupmatch.ui.сhallenges.model;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.List;

/**
 * Ашық тапсырма карточкасы (RecyclerView элементі).
 */
public final class Challenge {

    private final long id;
    @NonNull
    private final ChallengeType type;
    @NonNull
    private final String title;
    @NonNull
    private final String prizeLabel;
    @NonNull
    private final String deadline;
    @NonNull
    private final List<String> tags;

    public Challenge(long id,
                     @NonNull ChallengeType type,
                     @NonNull String title,
                     @NonNull String prizeLabel,
                     @NonNull String deadline,
                     @NonNull List<String> tags) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.prizeLabel = prizeLabel;
        this.deadline = deadline;
        this.tags = Collections.unmodifiableList(tags);
    }

    public long getId() {
        return id;
    }

    @NonNull
    public ChallengeType getType() {
        return type;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    @NonNull
    public String getPrizeLabel() {
        return prizeLabel;
    }

    @NonNull
    public String getDeadline() {
        return deadline;
    }

    @NonNull
    public List<String> getTags() {
        return tags;
    }
}
