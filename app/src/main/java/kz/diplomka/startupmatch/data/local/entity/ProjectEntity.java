package kz.diplomka.startupmatch.data.local.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "projects")
public class ProjectEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    @NonNull
    @ColumnInfo(name = "industry")
    private String industry;

    @NonNull
    @ColumnInfo(name = "target_audience")
    private String targetAudience;

    @NonNull
    @ColumnInfo(name = "market")
    private String market;

    @NonNull
    @ColumnInfo(name = "short_description")
    private String shortDescription;

    @NonNull
    @ColumnInfo(name = "full_description")
    private String fullDescription;

    @Nullable
    @ColumnInfo(name = "pitch_drive_link")
    private String pitchDriveLink;

    @Nullable
    @ColumnInfo(name = "github_link")
    private String githubLink;

    @Nullable
    @ColumnInfo(name = "mvp_link")
    private String mvpLink;

    @Nullable
    @ColumnInfo(name = "traction_link")
    private String tractionLink;

    /** Стартап WhatsApp / байланыс нөмірі (тек цифрлар немесе +7… форматы). */
    @Nullable
    @ColumnInfo(name = "contact_phone")
    private String contactPhone;

    /** Pitch сілтемесі соңғы сақталған уақыт (epoch millis). */
    @Nullable
    @ColumnInfo(name = "pitch_saved_at")
    private Long pitchSavedAt;

    /** MVP сілтемесі соңғы сақталған уақыт (epoch millis). «Қазіргі нұсқа» күніне тіркеледі. */
    @Nullable
    @ColumnInfo(name = "mvp_saved_at")
    private Long mvpSavedAt;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    @ColumnInfo(name = "updated_at")
    private long updatedAt;

    public ProjectEntity(
            @NonNull String name,
            @NonNull String industry,
            @NonNull String targetAudience,
            @NonNull String market,
            @NonNull String shortDescription,
            @NonNull String fullDescription,
            @Nullable String pitchDriveLink,
            @Nullable String githubLink,
            @Nullable String mvpLink,
            @Nullable String tractionLink,
            @Nullable String contactPhone,
            @Nullable Long pitchSavedAt,
            @Nullable Long mvpSavedAt,
            long createdAt,
            long updatedAt
    ) {
        this.name = name;
        this.industry = industry;
        this.targetAudience = targetAudience;
        this.market = market;
        this.shortDescription = shortDescription;
        this.fullDescription = fullDescription;
        this.pitchDriveLink = pitchDriveLink;
        this.githubLink = githubLink;
        this.mvpLink = mvpLink;
        this.tractionLink = tractionLink;
        this.contactPhone = contactPhone;
        this.pitchSavedAt = pitchSavedAt;
        this.mvpSavedAt = mvpSavedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getIndustry() {
        return industry;
    }

    public void setIndustry(@NonNull String industry) {
        this.industry = industry;
    }

    @NonNull
    public String getTargetAudience() {
        return targetAudience;
    }

    public void setTargetAudience(@NonNull String targetAudience) {
        this.targetAudience = targetAudience;
    }

    @NonNull
    public String getMarket() {
        return market;
    }

    public void setMarket(@NonNull String market) {
        this.market = market;
    }

    @NonNull
    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(@NonNull String shortDescription) {
        this.shortDescription = shortDescription;
    }

    @NonNull
    public String getFullDescription() {
        return fullDescription;
    }

    public void setFullDescription(@NonNull String fullDescription) {
        this.fullDescription = fullDescription;
    }

    @Nullable
    public String getPitchDriveLink() {
        return pitchDriveLink;
    }

    public void setPitchDriveLink(@Nullable String pitchDriveLink) {
        this.pitchDriveLink = pitchDriveLink;
    }

    @Nullable
    public String getGithubLink() {
        return githubLink;
    }

    public void setGithubLink(@Nullable String githubLink) {
        this.githubLink = githubLink;
    }

    @Nullable
    public String getMvpLink() {
        return mvpLink;
    }

    public void setMvpLink(@Nullable String mvpLink) {
        this.mvpLink = mvpLink;
    }

    @Nullable
    public String getTractionLink() {
        return tractionLink;
    }

    public void setTractionLink(@Nullable String tractionLink) {
        this.tractionLink = tractionLink;
    }

    @Nullable
    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(@Nullable String contactPhone) {
        this.contactPhone = contactPhone;
    }

    @Nullable
    public Long getPitchSavedAt() {
        return pitchSavedAt;
    }

    public void setPitchSavedAt(@Nullable Long pitchSavedAt) {
        this.pitchSavedAt = pitchSavedAt;
    }

    @Nullable
    public Long getMvpSavedAt() {
        return mvpSavedAt;
    }

    public void setMvpSavedAt(@Nullable Long mvpSavedAt) {
        this.mvpSavedAt = mvpSavedAt;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
