package kz.diplomka.startupmatch.ui.investors;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.databinding.ItemInvestorCardBinding;

public class InvestorsAdapter extends RecyclerView.Adapter<InvestorsAdapter.Vh> {

    public interface OnInvestorClickListener {
        void onInvestorClick(@NonNull InvestorListItem item);
    }

    private final List<InvestorListItem> items = new ArrayList<>();
    @Nullable
    private OnInvestorClickListener onInvestorClickListener;

    public void setOnInvestorClickListener(@Nullable OnInvestorClickListener listener) {
        this.onInvestorClickListener = listener;
    }

    public void submit(@NonNull List<InvestorListItem> list) {
        items.clear();
        items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemInvestorCardBinding binding = ItemInvestorCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new Vh(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull Vh holder, int position) {
        holder.bind(items.get(position), onInvestorClickListener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static final class Vh extends RecyclerView.ViewHolder {

        private final ItemInvestorCardBinding binding;

        Vh(ItemInvestorCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(InvestorListItem item, @Nullable OnInvestorClickListener listener) {
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onInvestorClick(item);
                }
            });
            binding.imageAvatar.setImageResource(item.avatarResId);
            binding.textName.setText(item.name);
            binding.textRole.setText(item.role);
            binding.textTicketGeo.setText(item.ticketAndGeo);
            binding.textQuote.setText(item.quote);

            binding.textViews.setText(
                    binding.getRoot().getContext().getString(R.string.investors_stat_views_format, item.views));
            binding.textChallenges.setText(
                    binding.getRoot().getContext().getString(R.string.investors_stat_challenges_format,
                            item.challenges));

            binding.badgeVerified.setVisibility(View.GONE);
            binding.badgeGuest.setVisibility(View.GONE);
            binding.badgeExperienced.setVisibility(View.GONE);
            switch (item.badge) {
                case VERIFIED:
                    binding.badgeVerified.setVisibility(View.VISIBLE);
                    break;
                case GUEST:
                    binding.badgeGuest.setVisibility(View.VISIBLE);
                    break;
                case EXPERIENCED:
                    binding.badgeExperienced.setVisibility(View.VISIBLE);
                    break;
            }

            bindIndustries(item.industries);
        }

        private void bindIndustries(@NonNull String[] industries) {
            LinearLayout row = binding.layoutIndustries;
            row.removeAllViews();
            if (industries.length == 0) {
                return;
            }
            android.content.Context ctx = row.getContext();
            int pxH = (int) (8 * ctx.getResources().getDisplayMetrics().density + 0.5f);
            int pxV = (int) (3 * ctx.getResources().getDisplayMetrics().density + 0.5f);
            int marginEnd = (int) (6 * ctx.getResources().getDisplayMetrics().density + 0.5f);
            for (String tag : industries) {
                TextView tv = new TextView(ctx);
                tv.setText(tag);
                tv.setBackgroundResource(R.drawable.bg_investor_industry_tag);
                tv.setPadding(pxH, pxV, pxH, pxV);
                tv.setTextColor(ContextCompat.getColor(ctx, R.color.investor_chip_text));
                tv.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 11);
                tv.setTypeface(ResourcesCompat.getFont(ctx, R.font.inter_medium));
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMarginEnd(marginEnd);
                tv.setLayoutParams(lp);
                row.addView(tv);
            }
        }
    }
}
