package kz.diplomka.startupmatch.ui.home.command;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import kz.diplomka.startupmatch.ui.investors.InvestorListItem;

/**
 * Байланысқан инвестор карточкасының UI моделі (Figma LinkedInvestors).
 */
public final class LinkedInvestorUiModel {

    @DrawableRes
    public final int avatarResId;
    @NonNull
    public final String name;
    @NonNull
    public final String role;
    @NonNull
    public final InvestorListItem.BadgeKind badgeKind;
    @NonNull
    public final String tractionUsers;
    @NonNull
    public final String tractionMrr;
    @NonNull
    public final String tractionGrowth;
    @NonNull
    public final String teamWhyUs;
    @NonNull
    public final String validationMarket;
    @NonNull
    public final String footerTimeLabel;

    public LinkedInvestorUiModel(
            @DrawableRes int avatarResId,
            @NonNull String name,
            @NonNull String role,
            @NonNull InvestorListItem.BadgeKind badgeKind,
            @NonNull String tractionUsers,
            @NonNull String tractionMrr,
            @NonNull String tractionGrowth,
            @NonNull String teamWhyUs,
            @NonNull String validationMarket,
            @NonNull String footerTimeLabel) {
        this.avatarResId = avatarResId;
        this.name = name;
        this.role = role;
        this.badgeKind = badgeKind;
        this.tractionUsers = tractionUsers;
        this.tractionMrr = tractionMrr;
        this.tractionGrowth = tractionGrowth;
        this.teamWhyUs = teamWhyUs;
        this.validationMarket = validationMarket;
        this.footerTimeLabel = footerTimeLabel;
    }
}
