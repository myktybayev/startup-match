package kz.diplomka.startupmatch.ui.home.command;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.ui.investors.InvestorListItem;

public class LinkedInvestorsAdapter extends RecyclerView.Adapter<LinkedInvestorsAdapter.Vh> {

    private final List<LinkedInvestorUiModel> items = new ArrayList<>();

    public void submit(@NonNull List<LinkedInvestorUiModel> list) {
        items.clear();
        items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_linked_investor_card, parent, false);
        return new Vh(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Vh holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static final class Vh extends RecyclerView.ViewHolder {

        private final CircleImageView imageAvatar;
        private final TextView textName;
        private final TextView textBadge;
        private final TextView textSubtitle;
        private final TextView textMetricUsers;
        private final TextView textMetricMrr;
        private final TextView textMetricGrowth;
        private final TextView textTeamWhy;
        private final TextView textValidation;
        private final TextView textFooterTime;

        Vh(@NonNull View itemView) {
            super(itemView);
            imageAvatar = itemView.findViewById(R.id.imageAvatar);
            textName = itemView.findViewById(R.id.textName);
            textBadge = itemView.findViewById(R.id.textBadge);
            textSubtitle = itemView.findViewById(R.id.textSubtitle);
            textMetricUsers = itemView.findViewById(R.id.textMetricUsers);
            textMetricMrr = itemView.findViewById(R.id.textMetricMrr);
            textMetricGrowth = itemView.findViewById(R.id.textMetricGrowth);
            textTeamWhy = itemView.findViewById(R.id.textTeamWhy);
            textValidation = itemView.findViewById(R.id.textValidation);
            textFooterTime = itemView.findViewById(R.id.textFooterTime);
        }

        void bind(@NonNull LinkedInvestorUiModel m) {
            imageAvatar.setImageResource(m.avatarResId);
            textName.setText(m.name);
            bindBadge(m.badgeKind);
            textSubtitle.setText(m.role);
            textMetricUsers.setText(
                    itemView.getContext().getString(R.string.linked_investor_metric_users, m.tractionUsers));
            textMetricMrr.setText(
                    itemView.getContext().getString(R.string.linked_investor_metric_mrr, m.tractionMrr));
            textMetricGrowth.setText(
                    itemView.getContext().getString(R.string.linked_investor_metric_growth, m.tractionGrowth));
            textTeamWhy.setText(m.teamWhyUs);
            textValidation.setText(m.validationMarket);
            textFooterTime.setText(m.footerTimeLabel);
        }

        private void bindBadge(@NonNull InvestorListItem.BadgeKind kind) {
            switch (kind) {
                case VERIFIED:
                    textBadge.setBackgroundResource(R.drawable.bg_investor_badge_verified);
                    textBadge.setText(R.string.investors_badge_verified);
                    textBadge.setTextColor(
                            ContextCompat.getColor(itemView.getContext(), R.color.investor_badge_verified_text));
                    break;
                case EXPERIENCED:
                    textBadge.setBackgroundResource(R.drawable.bg_linked_badge_experienced);
                    textBadge.setText(R.string.investors_badge_experienced);
                    textBadge.setTextColor(
                            ContextCompat.getColor(itemView.getContext(), R.color.linked_badge_experienced_text));
                    break;
                case GUEST:
                default:
                    textBadge.setBackgroundResource(R.drawable.bg_linked_badge_guest);
                    textBadge.setText(R.string.investors_badge_guest);
                    textBadge.setTextColor(
                            ContextCompat.getColor(itemView.getContext(), R.color.investor_text_secondary));
                    break;
            }
        }
    }
}
