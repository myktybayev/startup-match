package kz.diplomka.startupmatch.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import kz.diplomka.startupmatch.data.local.entity.ChallengeSubmissionEntity;

@Dao
public interface ChallengeSubmissionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(ChallengeSubmissionEntity entity);

    @Query("SELECT * FROM challenge_submissions ORDER BY submitted_at DESC")
    List<ChallengeSubmissionEntity> listAllOrdered();

    @Query("SELECT COUNT(*) FROM challenge_submissions WHERE project_id = :projectId")
    int countForProject(long projectId);
}
