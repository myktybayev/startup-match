package kz.diplomka.startupmatch.data.local.dao;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import kz.diplomka.startupmatch.data.local.entity.InvestorChallengeEntity;

@Dao
public interface InvestorChallengeDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(@NonNull InvestorChallengeEntity entity);

    @Query("SELECT * FROM investor_challenges ORDER BY created_at DESC")
    @NonNull
    List<InvestorChallengeEntity> listAllOrdered();

    @Query("SELECT * FROM investor_challenges WHERE id = :id LIMIT 1")
    @Nullable
    InvestorChallengeEntity getById(long id);
}
