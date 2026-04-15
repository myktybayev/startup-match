package kz.diplomka.startupmatch.ui.investor_role;

import androidx.annotation.NonNull;

/**
 * Жаңа тапсырма формасындағы тэг идентификаторлары (сақтау / API үшін тұрақты).
 */
public final class ChallengeTagKeys {

    public static final String FINTECH = "fintech";
    public static final String WEB = "web";
    public static final String API = "api";
    public static final String B2B = "b2b";
    public static final String PAYMENTS = "payments";

    private ChallengeTagKeys() {
    }

    @NonNull
    public static String[] allKeys() {
        return new String[]{FINTECH, WEB, API, B2B, PAYMENTS};
    }
}
