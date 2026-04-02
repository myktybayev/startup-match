package kz.diplomka.startupmatch.data.local.session;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Session and lightweight key-value preferences via Preferences DataStore (RxJava3).
 * Use {@link Flowable} for UI observation; avoid blocking reads on the main thread.
 */
public final class SessionLocalStore {

    private static final String DATA_STORE_NAME = "session";

    private static final Preferences.Key<Boolean> KEY_LOGGED_IN = PreferencesKeys.booleanKey("logged_in");
    private static final Preferences.Key<String> KEY_AUTH_TOKEN = PreferencesKeys.stringKey("auth_token");

    private static volatile RxDataStore<Preferences> dataStore;

    private SessionLocalStore() {
    }

    @NonNull
    public static RxDataStore<Preferences> getDataStore(@NonNull Context context) {
        if (dataStore == null) {
            synchronized (SessionLocalStore.class) {
                if (dataStore == null) {
                    dataStore = new RxPreferenceDataStoreBuilder(
                            context.getApplicationContext(),
                            DATA_STORE_NAME
                    ).build();
                }
            }
        }
        return dataStore;
    }

    @NonNull
    public static Flowable<Boolean> observeLoggedIn(@NonNull Context context) {
        return getDataStore(context).data()
                .map(prefs -> Boolean.TRUE.equals(prefs.get(KEY_LOGGED_IN)))
                .distinctUntilChanged();
    }

    @NonNull
    public static Single<Boolean> getLoggedIn(@NonNull Context context) {
        return getDataStore(context).data().firstOrError()
                .map(prefs -> Boolean.TRUE.equals(prefs.get(KEY_LOGGED_IN)))
                .subscribeOn(Schedulers.io());
    }

    @NonNull
    public static Completable setLoggedIn(@NonNull Context context, boolean loggedIn) {
        return update(context, prefs -> {
            MutablePreferences mutable = prefs.toMutablePreferences();
            mutable.set(KEY_LOGGED_IN, loggedIn);
            return mutable;
        });
    }

    @NonNull
    public static Single<String> getAuthToken(@NonNull Context context) {
        return getDataStore(context).data().firstOrError()
                .map(prefs -> {
                    String t = prefs.get(KEY_AUTH_TOKEN);
                    return t != null ? t : "";
                })
                .subscribeOn(Schedulers.io());
    }

    @NonNull
    public static Completable setAuthToken(@NonNull Context context, @Nullable String token) {
        return update(context, prefs -> {
            MutablePreferences mutable = prefs.toMutablePreferences();
            if (token == null || token.isEmpty()) {
                mutable.remove(KEY_AUTH_TOKEN);
            } else {
                mutable.set(KEY_AUTH_TOKEN, token);
            }
            return mutable;
        });
    }

    @NonNull
    private static Completable update(
            @NonNull Context context,
            @NonNull PreferencesTransform transform
    ) {
        return getDataStore(context).updateDataAsync(prefs ->
                Single.just(transform.apply(prefs))
        ).ignoreElement().subscribeOn(Schedulers.io());
    }

    private interface PreferencesTransform {
        @NonNull
        Preferences apply(@NonNull Preferences current);
    }
}
