package kz.diplomka.startupmatch.data.local.session;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Жаңа тапсырма черновигі: таңдалған тэгтер.
 * <p>
 * Тапсырма немесе жоба сақталғанда осы жиынды оқыңыз: {@link #getSelectedTagKeys(Context)}.
 * </p>
 */
public final class AddChallengeDraftPrefs {

    private static final String PREF_NAME = "add_challenge_draft";
    private static final String KEY_SELECTED_TAG_KEYS = "selected_tag_keys";

    private AddChallengeDraftPrefs() {
    }

    @NonNull
    private static SharedPreferences prefs(@NonNull Context context) {
        return context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Таңдалған тэг кілттері (мысалы fintech, web).
     */
    @NonNull
    public static Set<String> getSelectedTagKeys(@NonNull Context context) {
        Set<String> raw = prefs(context).getStringSet(KEY_SELECTED_TAG_KEYS, null);
        if (raw == null || raw.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(raw);
    }

    public static void setSelectedTagKeys(@NonNull Context context, @NonNull Set<String> keys) {
        prefs(context).edit().putStringSet(KEY_SELECTED_TAG_KEYS, new HashSet<>(keys)).apply();
    }

    public static void clearSelectedTagKeys(@NonNull Context context) {
        prefs(context).edit().remove(KEY_SELECTED_TAG_KEYS).apply();
    }
}
