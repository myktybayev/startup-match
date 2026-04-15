package kz.diplomka.startupmatch.data.local.dao;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import kz.diplomka.startupmatch.data.local.entity.AuthUserEntity;

@Dao
public interface AuthUserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(@NonNull AuthUserEntity entity);

    @Nullable
    @Query("SELECT * FROM auth_users WHERE email = :email LIMIT 1")
    AuthUserEntity getByEmail(@NonNull String email);

    @Nullable
    @Query("SELECT * FROM auth_users WHERE role = :role ORDER BY created_at DESC LIMIT 1")
    AuthUserEntity getLatestByRole(@NonNull String role);

    @Nullable
    @Query("SELECT * FROM auth_users ORDER BY created_at DESC LIMIT 1")
    AuthUserEntity getLatest();

    @NonNull
    @Query("SELECT * FROM auth_users WHERE role = :role ORDER BY created_at DESC")
    List<AuthUserEntity> listByRole(@NonNull String role);
}
