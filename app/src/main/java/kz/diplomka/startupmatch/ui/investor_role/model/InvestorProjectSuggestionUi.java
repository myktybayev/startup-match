package kz.diplomka.startupmatch.ui.investor_role.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class InvestorProjectSuggestionUi {

    public final long projectId;
    @NonNull
    public final String title;
    @NonNull
    public final String industry;
    public final int scorePercent;
    @NonNull
    public final String tractionLine;
    @NonNull
    public final String productLine;
    @Nullable
    public final String pitchUrl;

    public InvestorProjectSuggestionUi(
            long projectId,
            @NonNull String title,
            @NonNull String industry,
            int scorePercent,
            @NonNull String tractionLine,
            @NonNull String productLine,
            @Nullable String pitchUrl
    ) {
        this.projectId = projectId;
        this.title = title;
        this.industry = industry;
        this.scorePercent = scorePercent;
        this.tractionLine = tractionLine;
        this.productLine = productLine;
        this.pitchUrl = pitchUrl;
    }
}
