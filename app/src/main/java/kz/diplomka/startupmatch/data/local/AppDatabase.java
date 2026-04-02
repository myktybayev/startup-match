package kz.diplomka.startupmatch.data.local;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import kz.diplomka.startupmatch.data.local.dao.ProjectDao;
import kz.diplomka.startupmatch.data.local.dao.TeamMemberDao;
import kz.diplomka.startupmatch.data.local.entity.ProjectEntity;
import kz.diplomka.startupmatch.data.local.entity.TeamMemberEntity;

@Database(
        entities = {ProjectEntity.class, TeamMemberEntity.class},
        version = AppDatabase.VERSION,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public static final int VERSION = 2;
    private static final String DB_NAME = "startupmatch.db";

    private static volatile AppDatabase instance;

    public abstract ProjectDao projectDao();

    public abstract TeamMemberDao teamMemberDao();

    public static void init(@NonNull Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    DB_NAME
                            )
                            .addMigrations(DatabaseMigrations.ALL)
                            .fallbackToDestructiveMigrationOnDowngrade()
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
    }

    @NonNull
    public static AppDatabase get(@NonNull Context context) {
        if (instance == null) {
            init(context);
        }
        return instance;
    }
}
