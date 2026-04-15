package kz.diplomka.startupmatch.ui.investor_role;

import androidx.annotation.NonNull;

import java.util.Set;

import kz.diplomka.startupmatch.ui.сhallenges.model.ChallengeType;

/**
 * Тэгтерден тапсырма сүзгісі үшін {@link ChallengeType} анықтайды.
 */
public final class InvestorChallengeTypeMapper {

    private InvestorChallengeTypeMapper() {
    }

    @NonNull
    public static ChallengeType fromTagKeys(@NonNull Set<String> keys) {
        if (keys.contains(ChallengeTagKeys.FINTECH) || keys.contains(ChallengeTagKeys.PAYMENTS)) {
            return ChallengeType.FINTECH;
        }
        if (keys.contains(ChallengeTagKeys.WEB)) {
            return ChallengeType.WEB;
        }
        return ChallengeType.SAAS;
    }
}
