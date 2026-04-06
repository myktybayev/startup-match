package kz.diplomka.startupmatch.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import kz.diplomka.startupmatch.data.local.entity.InvestorPitchEntity;

@Dao
public interface InvestorPitchDao {

    @Insert
    long insert(InvestorPitchEntity entity);

    @Query("SELECT * FROM investor_pitches WHERE project_id = :projectId ORDER BY created_at DESC")
    List<InvestorPitchEntity> listForProject(long projectId);
}
