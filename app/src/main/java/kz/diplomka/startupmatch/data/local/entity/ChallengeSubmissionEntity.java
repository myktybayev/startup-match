package kz.diplomka.startupmatch.data.local.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Команда (жоба) инвестор тапсырмасына жіберген жауап — тізім және болашақта инвестор статусы.
 */
@Entity(
        tableName = "challenge_submissions",
        indices = {@Index(value = {"challenge_id", "project_id"}, unique = true)}
)
public class ChallengeSubmissionEntity {

    public static final String STATUS_SUBMITTED = "SUBMITTED";

    /** Инвестор әлі шешім шығарған жоқ (күту). Жаңа жауап осы күйде сақталады. */
    public static final String STATUS_PENDING_INVESTOR = "PENDING_INVESTOR";

    public static final String STATUS_ACCEPTED = "ACCEPTED";

    public static final String STATUS_DECLINED = "DECLINED";

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "challenge_id")
    private long challengeId;

    @ColumnInfo(name = "project_id")
    private long projectId;

    @NonNull
    @ColumnInfo(name = "challenge_title")
    private String challengeTitle;

    @NonNull
    @ColumnInfo(name = "project_name")
    private String projectName;

    @NonNull
    @ColumnInfo(name = "investor_name")
    private String investorName;

    @NonNull
    @ColumnInfo(name = "investor_role")
    private String investorRole;

    @ColumnInfo(name = "investor_avatar_res_id")
    private int investorAvatarResId;

    @NonNull
    @ColumnInfo(name = "motivation")
    private String motivation;

    @Nullable
    @ColumnInfo(name = "pitch_link")
    private String pitchLink;

    @Nullable
    @ColumnInfo(name = "mvp_link")
    private String mvpLink;

    /**
     * SUBMITTED — жіберілді; кейін инвестор жақтан өзгертуге болады (REVIEWED, ACCEPTED, …).
     */
    @NonNull
    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo(name = "submitted_at")
    private long submittedAt;

    public ChallengeSubmissionEntity(
            long challengeId,
            long projectId,
            @NonNull String challengeTitle,
            @NonNull String projectName,
            @NonNull String investorName,
            @NonNull String investorRole,
            int investorAvatarResId,
            @NonNull String motivation,
            @Nullable String pitchLink,
            @Nullable String mvpLink,
            @NonNull String status,
            long submittedAt) {
        this.challengeId = challengeId;
        this.projectId = projectId;
        this.challengeTitle = challengeTitle;
        this.projectName = projectName;
        this.investorName = investorName;
        this.investorRole = investorRole;
        this.investorAvatarResId = investorAvatarResId;
        this.motivation = motivation;
        this.pitchLink = pitchLink;
        this.mvpLink = mvpLink;
        this.status = status;
        this.submittedAt = submittedAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(long challengeId) {
        this.challengeId = challengeId;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    @NonNull
    public String getChallengeTitle() {
        return challengeTitle;
    }

    public void setChallengeTitle(@NonNull String challengeTitle) {
        this.challengeTitle = challengeTitle;
    }

    @NonNull
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(@NonNull String projectName) {
        this.projectName = projectName;
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

    public int getInvestorAvatarResId() {
        return investorAvatarResId;
    }

    public void setInvestorAvatarResId(int investorAvatarResId) {
        this.investorAvatarResId = investorAvatarResId;
    }

    @NonNull
    public String getMotivation() {
        return motivation;
    }

    public void setMotivation(@NonNull String motivation) {
        this.motivation = motivation;
    }

    @Nullable
    public String getPitchLink() {
        return pitchLink;
    }

    public void setPitchLink(@Nullable String pitchLink) {
        this.pitchLink = pitchLink;
    }

    @Nullable
    public String getMvpLink() {
        return mvpLink;
    }

    public void setMvpLink(@Nullable String mvpLink) {
        this.mvpLink = mvpLink;
    }

    @NonNull
    public String getStatus() {
        return status;
    }

    public void setStatus(@NonNull String status) {
        this.status = status;
    }

    public long getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(long submittedAt) {
        this.submittedAt = submittedAt;
    }
}
