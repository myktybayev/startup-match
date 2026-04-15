package kz.diplomka.startupmatch.ui.сhallenges;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
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
import kz.diplomka.startupmatch.databinding.ItemChallengeOpenBinding;
import kz.diplomka.startupmatch.ui.сhallenges.model.Challenge;

public class OpenChallengesAdapter extends RecyclerView.Adapter<OpenChallengesAdapter.ChallengeViewHolder> {

    public interface Listener {
        void onChallengeClick(@NonNull Challenge challenge);
    }

    private final LayoutInflater inflater;
    private final List<Challenge> items = new ArrayList<>();
    @Nullable
    private Listener listener;

    public OpenChallengesAdapter(@NonNull Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    public void setListener(@Nullable Listener listener) {
        this.listener = listener;
    }

    public void submit(@NonNull List<Challenge> challenges) {
        items.clear();
        items.addAll(challenges);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChallengeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChallengeOpenBinding binding = ItemChallengeOpenBinding.inflate(inflater, parent, false);
        return new ChallengeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengeViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    final class ChallengeViewHolder extends RecyclerView.ViewHolder {

        private final ItemChallengeOpenBinding binding;

        ChallengeViewHolder(@NonNull ItemChallengeOpenBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(@NonNull Challenge challenge) {
            binding.textTitle.setText(challenge.getTitle());
            binding.textPrize.setText(challenge.getPrizeLabel());
            binding.textDeadline.setText(challenge.getDeadline());
            bindTags(binding.containerTags, challenge.getTags());
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onChallengeClick(challenge);
                }
            });
        }

        private void bindTags(@NonNull LinearLayout container, @NonNull List<String> tags) {
            container.removeAllViews();
            Context context = container.getContext();
            Resources res = context.getResources();
            float density = res.getDisplayMetrics().density;
            int marginStart = (int) (8 * density + 0.5f);
            int padH = (int) (8 * density + 0.5f);
            int padV = (int) (4 * density + 0.5f);
            for (int i = 0; i < tags.size(); i++) {
                TextView tv = new TextView(context);
                tv.setText(tags.get(i));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                tv.setTextColor(ContextCompat.getColor(context, R.color.investor_chip_text));
                tv.setBackgroundResource(R.drawable.bg_investor_industry_tag);
                tv.setPadding(padH, padV, padH, padV);
                Typeface tf = ResourcesCompat.getFont(context, R.font.inter_medium);
                if (tf != null) {
                    tv.setTypeface(tf);
                }
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                if (i > 0) {
                    lp.setMarginStart(marginStart);
                }
                container.addView(tv, lp);
            }
        }
    }
}
