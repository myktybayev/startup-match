package kz.diplomka.startupmatch.data.local.session;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Инвестор профилінің көрінетін аты мен аватар URI (жергілікті сақтау).
 * Кіру/тіркелу соңынан толтырылады; UI экрандары синхрон оқи алады.
 */
public final class InvestorSessionPrefs {

    private static final String PREF_NAME = "investor_session";
    private static final String KEY_DISPLAY_NAME = "display_name";
    private static final String KEY_AVATAR_URI = "avatar_uri";

    private InvestorSessionPrefs() {
    }

    @NonNull
    private static SharedPreferences prefs(@NonNull Context context) {
        return context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * @return тазаланған аты немесе null (әлі орнатылмаған)
     */
    @Nullable
    public static String getDisplayName(@NonNull Context context) {
        String v = prefs(context).getString(KEY_DISPLAY_NAME, null);
        if (v == null) {
            return null;
        }
        v = v.trim();
        return v.isEmpty() ? null : v;
    }

    /**
     * @return content:// немесе file:// URI жолы, немесе null
     */
    @Nullable
    public static String getAvatarUriString(@NonNull Context context) {
        String v = prefs(context).getString(KEY_AVATAR_URI, null);
        if (v == null) {
            return null;
        }
        v = v.trim();
        return v.isEmpty() ? null : v;
    }

    public static void setDisplayName(@NonNull Context context, @Nullable String displayName) {
        SharedPreferences.Editor e = prefs(context).edit();
        if (displayName == null || displayName.trim().isEmpty()) {
            e.remove(KEY_DISPLAY_NAME);
        } else {
            e.putString(KEY_DISPLAY_NAME, displayName.trim());
        }
        e.apply();
    }

    public static void setAvatarUriString(@NonNull Context context, @Nullable String uriString) {
        SharedPreferences.Editor e = prefs(context).edit();
        if (uriString == null || uriString.trim().isEmpty()) {
            e.remove(KEY_AVATAR_URI);
        } else {
            e.putString(KEY_AVATAR_URI, uriString.trim());
        }
        e.apply();
    }

    public static void setProfile(
            @NonNull Context context,
            @Nullable String displayName,
            @Nullable String avatarUriString
    ) {
        setDisplayName(context, displayName);
        setAvatarUriString(context, avatarUriString);
    }
}
