package kz.diplomka.startupmatch.ui.investor_role;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.ClipDrawable;
import android.net.Uri;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.databinding.ItemInvestorProjectSuggestionBinding;
import kz.diplomka.startupmatch.ui.investor_role.model.InvestorProjectSuggestionUi;

public final class InvestorProjectSuggestionsAdapter
        extends RecyclerView.Adapter<InvestorProjectSuggestionsAdapter.Holder> {

    private static final int[] ACCENT_COLORS = new int[]{
            R.color.kvadrat_blue,
            R.color.investor_badge_experienced_bg,
            R.color.challenge_decision_green,
            R.color.challenge_prize_text,
            R.color.challenge_decision_red
    };

    private final List<InvestorProjectSuggestionUi> items = new ArrayList<>();
    private final LayoutInflater inflater;

    public InvestorProjectSuggestionsAdapter(@NonNull Context context) {
        this.inflater = LayoutInflater.from(context);
    }
    
    public void submit(@NonNull List<InvestorProjectSuggestionUi> list) {
        items.clear();
        items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(ItemInvestorProjectSuggestionBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        int accentRes = ACCENT_COLORS[position % ACCENT_COLORS.length];
        holder.bind(items.get(position), accentRes);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static final class Holder extends RecyclerView.ViewHolder {
        private final ItemInvestorProjectSuggestionBinding binding;

        Holder(@NonNull ItemInvestorProjectSuggestionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(@NonNull InvestorProjectSuggestionUi item, int accentColorRes) {
            Context c = binding.getRoot().getContext();
            int accentColor = ContextCompat.getColor(c, accentColorRes);
            binding.textProjectTitle.setText(item.title);
            binding.textIndustry.setText(item.industry);
            binding.textScorePercent.setText(item.scorePercent + "%");
            binding.progressScore.setProgress(item.scorePercent);
            binding.textTraction.setText(item.tractionLine);
            binding.textProduct.setText(item.productLine);
            binding.textScorePercent.setTextColor(accentColor);
            binding.buttonPitch.setBackgroundTintList(ColorStateList.valueOf(accentColor));

            Drawable progressDrawable = binding.progressScore.getProgressDrawable();
            if (progressDrawable instanceof LayerDrawable) {
                LayerDrawable layer = (LayerDrawable) progressDrawable.mutate();
                Drawable progressPart = layer.findDrawableByLayerId(android.R.id.progress);
                if (progressPart instanceof ClipDrawable) {
                    Drawable clipInner = ((ClipDrawable) progressPart).getDrawable();
                    if (clipInner != null) {
                        DrawableCompat.setTint(DrawableCompat.wrap(clipInner.mutate()), accentColor);
                    }
                } else if (progressPart != null) {
                    DrawableCompat.setTint(DrawableCompat.wrap(progressPart.mutate()), accentColor);
                }
            }

            binding.buttonPitch.setOnClickListener(v -> {
                if (item.pitchUrl == null || item.pitchUrl.trim().isEmpty()) {
                    Toast.makeText(c, R.string.investor_role_no_pitch, Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    c.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(item.pitchUrl)));
                } catch (Exception e) {
                    Toast.makeText(c, R.string.investor_role_open_pitch, Toast.LENGTH_SHORT).show();
                }
            });
            binding.buttonProfile.setOnClickListener(v -> c.startActivity(
                    ProjectDetailActivity.newIntent(
                            c,
                            item.projectId,
                            item.title,
                            item.industry,
                            item.scorePercent,
                            item.tractionLine,
                            item.productLine,
                            item.pitchUrl
                    )));
        }
    }
}
