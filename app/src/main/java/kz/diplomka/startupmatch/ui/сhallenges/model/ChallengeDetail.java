package kz.diplomka.startupmatch.ui.сhallenges.model;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.List;

import kz.diplomka.startupmatch.ui.investors.InvestorListItem;

/**
 * Тапсырма толық беті (деталь экран). Кейін DB entity-ден толтырылады.
 */
public final class ChallengeDetail {

    private final long id;
    @NonNull
    private final String stageBadge;
    @NonNull
    private final String statusBadge;
    @NonNull
    private final String title;
    @NonNull
    private final String deadlineLine;
    @NonNull
    private final String categoriesLine;
    /**
     * Инвестор кабинетіне ({@code InvestorsCabinetActivity}) өту үшін толық профиль.
     */
    @NonNull
    private final InvestorListItem investorProfile;
    @NonNull
    private final String description;
    @NonNull
    private final List<String> requirements;
    @NonNull
    private final String outcomeTitle;
    @NonNull
    private final String outcomeDescription;

    public ChallengeDetail(
            long id,
            @NonNull String stageBadge,
            @NonNull String statusBadge,
            @NonNull String title,
            @NonNull String deadlineLine,
            @NonNull String categoriesLine,
            @NonNull InvestorListItem investorProfile,
            @NonNull String description,
            @NonNull List<String> requirements,
            @NonNull String outcomeTitle,
            @NonNull String outcomeDescription) {
        this.id = id;
        this.stageBadge = stageBadge;
        this.statusBadge = statusBadge;
        this.title = title;
        this.deadlineLine = deadlineLine;
        this.categoriesLine = categoriesLine;
        this.investorProfile = investorProfile;
        this.description = description;
        this.requirements = Collections.unmodifiableList(requirements);
        this.outcomeTitle = outcomeTitle;
        this.outcomeDescription = outcomeDescription;
    }

    public long getId() {
        return id;
    }

    @NonNull
    public String getStageBadge() {
        return stageBadge;
    }

    @NonNull
    public String getStatusBadge() {
        return statusBadge;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    @NonNull
    public String getDeadlineLine() {
        return deadlineLine;
    }

    @NonNull
    public String getCategoriesLine() {
        return categoriesLine;
    }

    @NonNull
    public InvestorListItem getInvestorProfile() {
        return investorProfile;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    @NonNull
    public List<String> getRequirements() {
        return requirements;
    }

    @NonNull
    public String getOutcomeTitle() {
        return outcomeTitle;
    }

    @NonNull
    public String getOutcomeDescription() {
        return outcomeDescription;
    }
}
