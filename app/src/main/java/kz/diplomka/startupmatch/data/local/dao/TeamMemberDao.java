package kz.diplomka.startupmatch.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import kz.diplomka.startupmatch.data.local.entity.TeamMemberEntity;

@Dao
public interface TeamMemberDao {

    @Insert
    long insert(TeamMemberEntity member);

    @Query("SELECT * FROM team_members WHERE project_id = :projectId ORDER BY sort_order ASC, id ASC")
    List<TeamMemberEntity> listForProject(long projectId);

    @Query("SELECT COUNT(*) FROM team_members WHERE project_id = :projectId")
    int countForProject(long projectId);

    @Query("DELETE FROM team_members WHERE id = :id")
    void deleteById(long id);
}
