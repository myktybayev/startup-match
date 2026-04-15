package kz.diplomka.startupmatch.ui.сhallenges;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.databinding.ItemChallengeFeaturedBinding;
import kz.diplomka.startupmatch.ui.сhallenges.model.FeaturedBadge;
import kz.diplomka.startupmatch.ui.сhallenges.model.FeaturedChallenge;

public class FeaturedChallengesAdapter extends RecyclerView.Adapter<FeaturedChallengesAdapter.FeaturedViewHolder> {

    public interface Listener {
        void onFeaturedClick(long featuredChallengeId);
    }

    private final LayoutInflater inflater;
    private final List<FeaturedChallenge> items = new ArrayList<>();
    @Nullable
    private Listener listener;

    public FeaturedChallengesAdapter(@NonNull android.content.Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    public void setListener(@Nullable Listener listener) {
        this.listener = listener;
    }

    public void submit(@NonNull List<FeaturedChallenge> featured) {
        items.clear();
        items.addAll(featured);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FeaturedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChallengeFeaturedBinding binding = ItemChallengeFeaturedBinding.inflate(inflater, parent, false);
        return new FeaturedViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FeaturedViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    final class FeaturedViewHolder extends RecyclerView.ViewHolder {

        private final ItemChallengeFeaturedBinding binding;

        FeaturedViewHolder(@NonNull ItemChallengeFeaturedBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(@NonNull FeaturedChallenge item) {
            MaterialCardView card = binding.cardFeaturedRoot;
            android.content.Context context = card.getContext();
            float density = context.getResources().getDisplayMetrics().density;

            if (item.getBadge() == FeaturedBadge.ACTIVE) {
                card.setStrokeWidth((int) (2 * density + 0.5f));
                card.setStrokeColor(ColorStateList.valueOf(
                        ContextCompat.getColor(context, R.color.kvadrat_blue)));
                card.setCardElevation(4f * density);
                binding.textBadge.setBackgroundResource(R.drawable.bg_challenge_badge_active);
                binding.textBadge.setText(R.string.challenges_badge_active);
                binding.textBadge.setTextColor(
                        ContextCompat.getColor(context, R.color.challenge_badge_active_text));
                binding.buttonAction.setText(R.string.challenges_cta_answer);
                binding.buttonAction.setTextColor(
                        ContextCompat.getColor(context, R.color.white));
                binding.buttonAction.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(context, R.color.kvadrat_blue)));
            } else {
                card.setStrokeWidth((int) (1 * density + 0.5f));
                card.setStrokeColor(ColorStateList.valueOf(
                        ContextCompat.getColor(context, R.color.investor_card_stroke)));
                card.setCardElevation(2f * density);
                binding.textBadge.setBackgroundResource(R.drawable.bg_challenge_badge_submitted);
                binding.textBadge.setText(R.string.challenges_badge_submitted);
                binding.textBadge.setTextColor(
                        ContextCompat.getColor(context, R.color.challenge_badge_submitted_text));
                binding.buttonAction.setText(R.string.challenges_cta_view_answer);
                binding.buttonAction.setTextColor(
                        ContextCompat.getColor(context, R.color.kvadrat_blue));
                binding.buttonAction.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(context, R.color.investor_industry_tag_bg)));
            }

            binding.textTitle.setText(item.getTitle());
            binding.textDescription.setText(item.getDescription());
            binding.textDeadline.setText(item.getDeadline());

            MaterialButton btn = binding.buttonAction;
            btn.setStrokeWidth(0);

            View.OnClickListener openDetail = v -> {
                if (listener != null) {
                    listener.onFeaturedClick(item.getId());
                }
            };
            binding.cardFeaturedRoot.setOnClickListener(openDetail);
            binding.buttonAction.setOnClickListener(openDetail);
        }
    }
}
