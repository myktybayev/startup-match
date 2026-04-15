package kz.diplomka.startupmatch.ui.home.command;

import android.content.Context;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.data.local.entity.InvestorPitchEntity;
import kz.diplomka.startupmatch.ui.investors.InvestorListItem;

/**
 * {@link InvestorPitchEntity} → карточка UI; инвестор аты бойынша аватар мен badge табу.
 */
public final class LinkedInvestorDisplayMapper {

    private LinkedInvestorDisplayMapper() {
    }

    @NonNull
    public static List<LinkedInvestorUiModel> fromEntities(
            @NonNull Context context,
            @NonNull List<InvestorPitchEntity> entities) {
        List<LinkedInvestorUiModel> out = new ArrayList<>();
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        for (InvestorPitchEntity e : entities) {
            Lookup l = lookup(e.getInvestorName());
            String footer =
                    context.getString(R.string.linked_investor_last_sent, df.format(new Date(e.getCreatedAt())));
            out.add(
                    new LinkedInvestorUiModel(
                            l.avatarResId,
                            e.getInvestorName(),
                            e.getInvestorRole(),
                            l.badgeKind,
                            e.getTractionUsers(),
                            e.getTractionMrr(),
                            e.getTractionGrowth(),
                            e.getTeamWhyUs(),
                            e.getValidationMarket(),
                            footer));
        }
        return out;
    }

    private static final class Lookup {
        final int avatarResId;
        final InvestorListItem.BadgeKind badgeKind;

        Lookup(int avatarResId, InvestorListItem.BadgeKind badgeKind) {
            this.avatarResId = avatarResId;
            this.badgeKind = badgeKind;
        }
    }

    @NonNull
    private static Lookup lookup(@NonNull String name) {
        switch (name) {
            case "Асқар Есен":
                return new Lookup(R.drawable.figma_investor_avatar_1, InvestorListItem.BadgeKind.VERIFIED);
            case "Меруерт Садық":
                return new Lookup(R.drawable.figma_investor_avatar_2, InvestorListItem.BadgeKind.GUEST);
            case "Dana Ventures":
                return new Lookup(R.drawable.figma_investor_avatar_3, InvestorListItem.BadgeKind.VERIFIED);
            case "Nurai Capital":
                return new Lookup(R.drawable.figma_investor_avatar_4, InvestorListItem.BadgeKind.EXPERIENCED);
            case "Timur Qaz Angels":
                return new Lookup(R.drawable.figma_investor_avatar_5, InvestorListItem.BadgeKind.GUEST);
            case "Айдана Серік":
                return new Lookup(R.drawable.figma_investors_page2_1, InvestorListItem.BadgeKind.VERIFIED);
            case "Bek Horizon":
                return new Lookup(R.drawable.figma_investors_page2_2, InvestorListItem.BadgeKind.GUEST);
            case "Aruna Capital":
                return new Lookup(R.drawable.figma_investors_page2_3, InvestorListItem.BadgeKind.EXPERIENCED);
            case "Самат Алиев":
                return new Lookup(R.drawable.figma_investors_page2_4, InvestorListItem.BadgeKind.VERIFIED);
            case "Luna Ventures":
                return new Lookup(R.drawable.figma_investors_page2_5, InvestorListItem.BadgeKind.EXPERIENCED);
            default:
                return new Lookup(R.drawable.startup_default, InvestorListItem.BadgeKind.GUEST);
        }
    }
}
