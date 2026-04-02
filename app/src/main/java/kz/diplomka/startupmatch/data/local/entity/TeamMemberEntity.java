package kz.diplomka.startupmatch.data.local.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "team_members",
        foreignKeys = @ForeignKey(
                entity = ProjectEntity.class,
                parentColumns = "id",
                childColumns = "project_id",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = "project_id")}
)
public class TeamMemberEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "project_id")
    private long projectId;

    @NonNull
    @ColumnInfo(name = "full_name")
    private String fullName;

    @NonNull
    @ColumnInfo(name = "role")
    private String role;

    @NonNull
    @ColumnInfo(name = "experience")
    private String experience;

    @NonNull
    @ColumnInfo(name = "portfolio")
    private String portfolio;

    @Nullable
    @ColumnInfo(name = "avatar_uri")
    private String avatarUri;

    @ColumnInfo(name = "sort_order")
    private int sortOrder;

    public TeamMemberEntity(
            long projectId,
            @NonNull String fullName,
            @NonNull String role,
            @NonNull String experience,
            @NonNull String portfolio,
            @Nullable String avatarUri,
            int sortOrder
    ) {
        this.projectId = projectId;
        this.fullName = fullName;
        this.role = role;
        this.experience = experience;
        this.portfolio = portfolio;
        this.avatarUri = avatarUri;
        this.sortOrder = sortOrder;
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
    public String getFullName() {
        return fullName;
    }

    public void setFullName(@NonNull String fullName) {
        this.fullName = fullName;
    }

    @NonNull
    public String getRole() {
        return role;
    }

    public void setRole(@NonNull String role) {
        this.role = role;
    }

    @NonNull
    public String getExperience() {
        return experience;
    }

    public void setExperience(@NonNull String experience) {
        this.experience = experience;
    }

    @NonNull
    public String getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(@NonNull String portfolio) {
        this.portfolio = portfolio;
    }

    @Nullable
    public String getAvatarUri() {
        return avatarUri;
    }

    public void setAvatarUri(@Nullable String avatarUri) {
        this.avatarUri = avatarUri;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }
}
