package kz.diplomka.startupmatch.ui.investor_role.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Инвестор «Келген ұсыныстар» карточкасы (Figma 150:373).
 */
public final class IncomingPitchCardUi {

    /** Теріс болса демо карточка, базадан жойылмайды. */
    public final long pitchId;
    public final long projectId;
    @NonNull
    public final String startupName;
    @NonNull
    public final String timeLabel;
    @NonNull
    public final String teamWhyUs;
    @NonNull
    public final String validationMarket;
    public final boolean amberLogo;
    @NonNull
    public final String pitchSubtitle;
    @NonNull
    public final String githubSubtitle;
    @NonNull
    public final String mvpSubtitle;
    @NonNull
    public final String tractionSubtitle;
    @Nullable
    public final String pitchUrl;
    @Nullable
    public final String githubUrl;
    @Nullable
    public final String mvpUrl;
    @Nullable
    public final String tractionUrl;
    /** Жобаның WhatsApp нөмірі (сыйкестендіру үшін {@link kz.diplomka.startupmatch.util.WhatsAppUtils}). */
    @Nullable
    public final String contactPhone;

    public IncomingPitchCardUi(
            long pitchId,
            long projectId,
            @NonNull String startupName,
            @NonNull String timeLabel,
            @NonNull String teamWhyUs,
            @NonNull String validationMarket,
            boolean amberLogo,
            @NonNull String pitchSubtitle,
            @NonNull String githubSubtitle,
            @NonNull String mvpSubtitle,
            @NonNull String tractionSubtitle,
            @Nullable String pitchUrl,
            @Nullable String githubUrl,
            @Nullable String mvpUrl,
            @Nullable String tractionUrl,
            @Nullable String contactPhone
    ) {
        this.pitchId = pitchId;
        this.projectId = projectId;
        this.startupName = startupName;
        this.timeLabel = timeLabel;
        this.teamWhyUs = teamWhyUs;
        this.validationMarket = validationMarket;
        this.amberLogo = amberLogo;
        this.pitchSubtitle = pitchSubtitle;
        this.githubSubtitle = githubSubtitle;
        this.mvpSubtitle = mvpSubtitle;
        this.tractionSubtitle = tractionSubtitle;
        this.pitchUrl = pitchUrl;
        this.githubUrl = githubUrl;
        this.mvpUrl = mvpUrl;
        this.tractionUrl = tractionUrl;
        this.contactPhone = contactPhone;
    }
}
