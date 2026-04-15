package kz.diplomka.startupmatch.data.local.session;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Тіркелу/сессия үшін рөл: таңдалған рөлді жергілікті сақтау ({@link #ROLE_FOUNDER}, {@link #ROLE_INVESTOR}).
 */
public final class AuthRolePrefs {

    private static final String PREF_NAME = "auth_role";
    private static final String KEY_SELECTED_ROLE = "selected_role";

    /** Стартап / founder жолы. */
    public static final String ROLE_FOUNDER = "founder";

    /** Инвестор жолы. */
    public static final String ROLE_INVESTOR = "investor";

    private AuthRolePrefs() {
    }

    @NonNull
    private static SharedPreferences prefs(@NonNull Context context) {
        return context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Рөлді сақтау (мысалы рөл бетінен тіркелуге өткенде).
     */
    public static void setSelectedRole(@NonNull Context context, @Nullable String role) {
        SharedPreferences.Editor e = prefs(context).edit();
        if (role == null || role.trim().isEmpty()) {
            e.remove(KEY_SELECTED_ROLE);
        } else {
            e.putString(KEY_SELECTED_ROLE, role.trim());
        }
        e.apply();
    }

    /**
     * Соңғы сақталған рөл немесе null.
     */
    @Nullable
    public static String getSelectedRole(@NonNull Context context) {
        String v = prefs(context).getString(KEY_SELECTED_ROLE, null);
        if (v == null) {
            return null;
        }
        v = v.trim();
        return v.isEmpty() ? null : v;
    }

    public static void clear(@NonNull Context context) {
        prefs(context).edit().remove(KEY_SELECTED_ROLE).apply();
    }
}
