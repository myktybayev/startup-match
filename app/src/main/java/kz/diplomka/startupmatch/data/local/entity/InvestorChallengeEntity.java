package kz.diplomka.startupmatch.data.local.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Инвестор жариялаған ашық тапсырма (тапсырмалар тізімі мен деталь үшін).
 */
@Entity(tableName = "investor_challenges")
public class InvestorChallengeEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    @ColumnInfo(name = "title")
    private String title;

    @NonNull
    @ColumnInfo(name = "description")
    private String description;

    @NonNull
    @ColumnInfo(name = "team_fit")
    private String teamFit;

    @ColumnInfo(name = "deadline_ms")
    private long deadlineMs;

    @NonNull
    @ColumnInfo(name = "deadline_label")
    private String deadlineLabel;

    /** Мысалы: fintech,web — сүзгі және көрсету үшін. */
    @NonNull
    @ColumnInfo(name = "tag_keys_csv")
    private String tagKeysCsv;

    /** JSON массив: ["талап 1", ...] */
    @NonNull
    @ColumnInfo(name = "requirements_json")
    private String requirementsJson;

    /** {@link #REWARD_INVESTMENT} немесе {@link #REWARD_PILOT}. */
    @NonNull
    @ColumnInfo(name = "reward_type")
    private String rewardType;

    /** {@link kz.diplomka.startupmatch.ui.сhallenges.model.ChallengeType#name()} */
    @NonNull
    @ColumnInfo(name = "filter_type")
    private String filterType;

    @NonNull
    @ColumnInfo(name = "investor_name")
    private String investorName;

    @NonNull
    @ColumnInfo(name = "investor_role")
    private String investorRole;

    @Nullable
    @ColumnInfo(name = "investor_photo_uri")
    private String investorPhotoUri;

    @NonNull
    @ColumnInfo(name = "stage_badge")
    private String stageBadge;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    public static final String REWARD_INVESTMENT = "INVESTMENT";
    public static final String REWARD_PILOT = "PILOT";

    public InvestorChallengeEntity(
            @NonNull String title,
            @NonNull String description,
            @NonNull String teamFit,
            long deadlineMs,
            @NonNull String deadlineLabel,
            @NonNull String tagKeysCsv,
            @NonNull String requirementsJson,
            @NonNull String rewardType,
            @NonNull String filterType,
            @NonNull String investorName,
            @NonNull String investorRole,
            @Nullable String investorPhotoUri,
            @NonNull String stageBadge,
            long createdAt) {
        this.title = title;
        this.description = description;
        this.teamFit = teamFit;
        this.deadlineMs = deadlineMs;
        this.deadlineLabel = deadlineLabel;
        this.tagKeysCsv = tagKeysCsv;
        this.requirementsJson = requirementsJson;
        this.rewardType = rewardType;
        this.filterType = filterType;
        this.investorName = investorName;
        this.investorRole = investorRole;
        this.investorPhotoUri = investorPhotoUri;
        this.stageBadge = stageBadge;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    public void setDescription(@NonNull String description) {
        this.description = description;
    }

    @NonNull
    public String getTeamFit() {
        return teamFit;
    }

    public void setTeamFit(@NonNull String teamFit) {
        this.teamFit = teamFit;
    }

    public long getDeadlineMs() {
        return deadlineMs;
    }

    public void setDeadlineMs(long deadlineMs) {
        this.deadlineMs = deadlineMs;
    }

    @NonNull
    public String getDeadlineLabel() {
        return deadlineLabel;
    }

    public void setDeadlineLabel(@NonNull String deadlineLabel) {
        this.deadlineLabel = deadlineLabel;
    }

    @NonNull
    public String getTagKeysCsv() {
        return tagKeysCsv;
    }

    public void setTagKeysCsv(@NonNull String tagKeysCsv) {
        this.tagKeysCsv = tagKeysCsv;
    }

    @NonNull
    public String getRequirementsJson() {
        return requirementsJson;
    }

    public void setRequirementsJson(@NonNull String requirementsJson) {
        this.requirementsJson = requirementsJson;
    }

    @NonNull
    public String getRewardType() {
        return rewardType;
    }

    public void setRewardType(@NonNull String rewardType) {
        this.rewardType = rewardType;
    }

    @NonNull
    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(@NonNull String filterType) {
        this.filterType = filterType;
    }

    @NonNull
    public String getInvestorName() {
        return investorName;
    }

    public void setInvestorName(@NonNull String investorName) {
        this.investorName = investorName;
    }

    @NonNull
    public String getInvestorRole() {
        return investorRole;
    }

    public void setInvestorRole(@NonNull String investorRole) {
        this.investorRole = investorRole;
    }

    @Nullable
    public String getInvestorPhotoUri() {
        return investorPhotoUri;
    }

    public void setInvestorPhotoUri(@Nullable String investorPhotoUri) {
        this.investorPhotoUri = investorPhotoUri;
    }

    @NonNull
    public String getStageBadge() {
        return stageBadge;
    }

    public void setStageBadge(@NonNull String stageBadge) {
        this.stageBadge = stageBadge;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
