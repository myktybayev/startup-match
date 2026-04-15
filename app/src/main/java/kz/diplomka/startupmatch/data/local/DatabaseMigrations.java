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

    public static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS investor_pitches ("
                            + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                            + "project_id INTEGER NOT NULL, "
                            + "investor_name TEXT NOT NULL, "
                            + "investor_role TEXT NOT NULL, "
                            + "traction_users TEXT NOT NULL, "
                            + "traction_mrr TEXT NOT NULL, "
                            + "traction_growth TEXT NOT NULL, "
                            + "team_why_us TEXT NOT NULL, "
                            + "validation_market TEXT NOT NULL, "
                            + "created_at INTEGER NOT NULL)"
            );
            db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_investor_pitches_project_id "
                            + "ON investor_pitches(project_id)"
            );
        }
    };

    public static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE projects ADD COLUMN contact_phone TEXT");
        }
    };

    public static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS challenge_submissions ("
                            + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                            + "challenge_id INTEGER NOT NULL, "
                            + "project_id INTEGER NOT NULL, "
                            + "challenge_title TEXT NOT NULL, "
                            + "project_name TEXT NOT NULL, "
                            + "investor_name TEXT NOT NULL, "
                            + "investor_role TEXT NOT NULL, "
                            + "investor_avatar_res_id INTEGER NOT NULL, "
                            + "motivation TEXT NOT NULL, "
                            + "pitch_link TEXT, "
                            + "mvp_link TEXT, "
                            + "status TEXT NOT NULL, "
                            + "submitted_at INTEGER NOT NULL)"
            );
            db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS "
                            + "index_challenge_submissions_challenge_id_project_id "
                            + "ON challenge_submissions(challenge_id, project_id)"
            );
        }
    };

    public static final Migration[] ALL =
            new Migration[]{
                    MIGRATION_1_2,
                    MIGRATION_2_3,
                    MIGRATION_3_4,
                    MIGRATION_4_5,
                    MIGRATION_5_6,
                    MIGRATION_6_7
            };
}
