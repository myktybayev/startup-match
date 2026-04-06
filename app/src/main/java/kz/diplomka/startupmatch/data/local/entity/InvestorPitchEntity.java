package kz.diplomka.startupmatch.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Инвесторға жіберілген pitch (traction + мәтіндер) — ағымдағы жоба id-імен байланысты.
 */
@Entity(
        tableName = "investor_pitches",
        indices = {@Index(value = {"project_id"})}
)
public class InvestorPitchEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "project_id")
    private long projectId;

    @NonNull
    @ColumnInfo(name = "investor_name")
    private String investorName;

    @NonNull
    @ColumnInfo(name = "investor_role")
    private String investorRole;

    /** Users (тек сандар / мәтін) */
    @NonNull
    @ColumnInfo(name = "traction_users")
    private String tractionUsers;

    @NonNull
    @ColumnInfo(name = "traction_mrr")
    private String tractionMrr;

    @NonNull
    @ColumnInfo(name = "traction_growth")
    private String tractionGrowth;

    @NonNull
    @ColumnInfo(name = "team_why_us")
    private String teamWhyUs;

    @NonNull
    @ColumnInfo(name = "validation_market")
    private String validationMarket;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    public InvestorPitchEntity(
            long projectId,
            @NonNull String investorName,
            @NonNull String investorRole,
            @NonNull String tractionUsers,
            @NonNull String tractionMrr,
            @NonNull String tractionGrowth,
            @NonNull String teamWhyUs,
            @NonNull String validationMarket,
            long createdAt) {
        this.projectId = projectId;
        this.investorName = investorName;
        this.investorRole = investorRole;
        this.tractionUsers = tractionUsers;
        this.tractionMrr = tractionMrr;
        this.tractionGrowth = tractionGrowth;
        this.teamWhyUs = teamWhyUs;
        this.validationMarket = validationMarket;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
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

    @NonNull
    public String getTractionUsers() {
        return tractionUsers;
    }

    public void setTractionUsers(@NonNull String tractionUsers) {
        this.tractionUsers = tractionUsers;
    }

    @NonNull
    public String getTractionMrr() {
        return tractionMrr;
    }

    public void setTractionMrr(@NonNull String tractionMrr) {
        this.tractionMrr = tractionMrr;
    }

    @NonNull
    public String getTractionGrowth() {
        return tractionGrowth;
    }

    public void setTractionGrowth(@NonNull String tractionGrowth) {
        this.tractionGrowth = tractionGrowth;
    }

    @NonNull
    public String getTeamWhyUs() {
        return teamWhyUs;
    }

    public void setTeamWhyUs(@NonNull String teamWhyUs) {
        this.teamWhyUs = teamWhyUs;
    }

    @NonNull
    public String getValidationMarket() {
        return validationMarket;
    }

    public void setValidationMarket(@NonNull String validationMarket) {
        this.validationMarket = validationMarket;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
