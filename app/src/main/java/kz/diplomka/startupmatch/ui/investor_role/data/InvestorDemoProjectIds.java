package kz.diplomka.startupmatch.ui.investor_role.data;

/**
 * Инвестор экранындағы демо карточкалардың id-лары (Room-да жоба ретінде жоқ, тек UI).
 * Әр id үшін бөлек «команда» демосы көрсетіледі.
 */
public final class InvestorDemoProjectIds {

    public static final long SMARTPAY_KZ = -101L;
    public static final long EDU_AI = -102L;
    public static final long MEDLINK_AI = -103L;
    public static final long NOMAD_LEDGER = -104L;

    private InvestorDemoProjectIds() {
    }

    public static boolean isKnownDemo(long projectId) {
        return projectId == SMARTPAY_KZ
                || projectId == EDU_AI
                || projectId == MEDLINK_AI
                || projectId == NOMAD_LEDGER;
    }
}
