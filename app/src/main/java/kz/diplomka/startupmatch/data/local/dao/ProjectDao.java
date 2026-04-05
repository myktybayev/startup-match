package kz.diplomka.startupmatch.data.local.dao;

import androidx.annotation.Nullable;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import kz.diplomka.startupmatch.data.local.entity.ProjectEntity;

@Dao
public interface ProjectDao {

    @Insert
    long insert(ProjectEntity project);

    @Update
    void update(ProjectEntity project);

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    @Nullable
    ProjectEntity getById(long id);

    @Query("SELECT * FROM projects ORDER BY updated_at DESC LIMIT 1")
    @Nullable
    ProjectEntity getLatest();

    @Query("UPDATE projects SET pitch_drive_link = :link, updated_at = :updatedAt, pitch_saved_at = :pitchSavedAt WHERE id = :id")
    void updatePitchDriveLink(long id, @Nullable String link, long updatedAt, long pitchSavedAt);

    @Query("UPDATE projects SET github_link = :link, updated_at = :updatedAt WHERE id = :id")
    void updateGithubLink(long id, @Nullable String link, long updatedAt);

    @Query("UPDATE projects SET mvp_link = :link, updated_at = :updatedAt, mvp_saved_at = :mvpSavedAt WHERE id = :id")
    void updateMvpLink(long id, @Nullable String link, long updatedAt, long mvpSavedAt);

    @Query("UPDATE projects SET traction_link = :link, updated_at = :updatedAt WHERE id = :id")
    void updateTractionLink(long id, @Nullable String link, long updatedAt);
}
