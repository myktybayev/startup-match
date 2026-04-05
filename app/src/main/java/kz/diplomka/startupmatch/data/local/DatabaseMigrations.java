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

    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE projects ADD COLUMN pitch_saved_at INTEGER");
            db.execSQL(
                    "UPDATE projects SET pitch_saved_at = updated_at "
                            + "WHERE pitch_drive_link IS NOT NULL AND TRIM(pitch_drive_link) != ''"
            );
        }
    };

    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE projects ADD COLUMN mvp_saved_at INTEGER");
            db.execSQL(
                    "UPDATE projects SET mvp_saved_at = updated_at "
                            + "WHERE mvp_link IS NOT NULL AND TRIM(mvp_link) != ''"
            );
        }
    };

    public static final Migration[] ALL = new Migration[]{MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4};
}
