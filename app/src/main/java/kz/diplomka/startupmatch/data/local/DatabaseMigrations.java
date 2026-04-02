package kz.diplomka.startupmatch.data.local;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * Room migrations: bump {@link AppDatabase#VERSION} and add a {@link Migration} here,
 * then register it in {@link AppDatabase}.
 */
public final class DatabaseMigrations {

    private DatabaseMigrations() {
    }

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE projects ADD COLUMN github_link TEXT");
            db.execSQL("ALTER TABLE projects ADD COLUMN mvp_link TEXT");
            db.execSQL("ALTER TABLE projects ADD COLUMN traction_link TEXT");
        }
    };

    public static final Migration[] ALL = new Migration[]{MIGRATION_1_2};
}
