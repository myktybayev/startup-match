package kz.diplomka.startupmatch.ui.сhallenges;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.data.local.entity.ChallengeSubmissionEntity;
import kz.diplomka.startupmatch.databinding.ItemChallengeMySubmissionBinding;

public class MyChallengeSubmissionsAdapter extends RecyclerView.Adapter<MyChallengeSubmissionsAdapter.Holder> {

    private final LayoutInflater inflater;
    private final List<ChallengeSubmissionEntity> items = new ArrayList<>();

    public MyChallengeSubmissionsAdapter(@NonNull Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    public void submit(@NonNull List<ChallengeSubmissionEntity> submissions) {
        items.clear();
        items.addAll(submissions);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(ItemChallengeMySubmissionBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private enum Decision {
        PENDING,
        ACCEPTED,
        DECLINED
    }

    @NonNull
    private static Decision decisionFrom(@NonNull String status) {
        if (ChallengeSubmissionEntity.STATUS_ACCEPTED.equals(status)) {
            return Decision.ACCEPTED;
        }
        if (ChallengeSubmissionEntity.STATUS_DECLINED.equals(status)) {
            return Decision.DECLINED;
        }
        return Decision.PENDING;
    }

    static final class Holder extends RecyclerView.ViewHolder {

        private final ItemChallengeMySubmissionBinding binding;

        Holder(@NonNull ItemChallengeMySubmissionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(@NonNull ChallengeSubmissionEntity e) {
            Context c = binding.getRoot().getContext();
            Decision d = decisionFrom(e.getStatus());

            MaterialCardView card = (MaterialCardView) binding.getRoot();
            int border = ContextCompat.getColor(c, borderColorRes(d));
            card.setStrokeColor(border);

            binding.textProjectTitle.setText(e.getProjectName());
            binding.textMotivation.setText(e.getMotivation());

            bindDecisionBadge(c, d);
            bindFooter(c, d);

            String pitch = e.getPitchLink();
            if (pitch == null || pitch.trim().isEmpty()) {
                binding.rowPitch.setVisibility(View.GONE);
            } else {
                binding.rowPitch.setVisibility(View.VISIBLE);
                binding.textPitchLine.setText(
                        c.getString(R.string.challenge_submission_pitch_line, shortenUrl(pitch.trim())));
                binding.rowPitch.setOnClickListener(v -> openUrl(c, pitch.trim()));
            }

            String mvp = e.getMvpLink();
            if (mvp == null || mvp.trim().isEmpty()) {
                binding.rowMvp.setVisibility(View.GONE);
            } else {
                binding.rowMvp.setVisibility(View.VISIBLE);
                binding.textMvpLine.setText(
                        c.getString(R.string.challenge_submission_mvp_line, shortenUrl(mvp.trim())));
                binding.rowMvp.setOnClickListener(v -> openUrl(c, mvp.trim()));
            }
        }

        private static int borderColorRes(@NonNull Decision d) {
            switch (d) {
                case ACCEPTED:
                    return R.color.challenge_answer_card_border_blue;
                case DECLINED:
                    return R.color.challenge_decision_red;
                case PENDING:
                default:
                    return R.color.challenge_decision_orange;
            }
        }

        private void bindDecisionBadge(@NonNull Context c, @NonNull Decision d) {
            switch (d) {
                case ACCEPTED:
                    binding.textDecisionBadge.setBackgroundResource(R.drawable.bg_challenge_decision_green);
                    binding.textDecisionBadge.setText(R.string.challenge_decision_accepted);
                    binding.textDecisionBadge.setTextColor(ContextCompat.getColor(c, R.color.white));
                    break;
                case DECLINED:
                    binding.textDecisionBadge.setBackgroundResource(R.drawable.bg_challenge_decision_red);
                    binding.textDecisionBadge.setText(R.string.challenge_decision_declined);
                    binding.textDecisionBadge.setTextColor(ContextCompat.getColor(c, R.color.white));
                    break;
                case PENDING:
                default:
                    binding.textDecisionBadge.setBackgroundResource(R.drawable.bg_challenge_decision_orange);
                    binding.textDecisionBadge.setText(R.string.challenge_decision_pending);
                    binding.textDecisionBadge.setTextColor(
                            ContextCompat.getColor(c, R.color.challenge_decision_orange_text));
                    break;
            }
        }

        private void bindFooter(@NonNull Context c, @NonNull Decision d) {
            binding.imageFooterIcon.clearColorFilter();
            switch (d) {
                case ACCEPTED:
                    binding.imageFooterIcon.setImageResource(R.drawable.ic_investors_cabinet_message_outline);
                    binding.textFooter.setText(R.string.challenge_footer_accepted);
                    break;
                case DECLINED:
                    binding.imageFooterIcon.setImageResource(R.drawable.ic_sheet_close);
                    binding.imageFooterIcon.setColorFilter(
                            ContextCompat.getColor(c, R.color.challenge_decision_red));
                    binding.textFooter.setText(R.string.challenge_footer_declined);
                    break;
                case PENDING:
                default:
                    binding.imageFooterIcon.setImageResource(R.drawable.ic_investors_cabinet_clock);
                    binding.textFooter.setText(R.string.challenge_footer_pending);
                    break;
            }
        }

        @NonNull
        private static String shortenUrl(@NonNull String url) {
            if (url.length() <= 32) {
                return url;
            }
            return url.substring(0, 28) + "…";
        }

        private static void openUrl(@NonNull Context c, @NonNull String url) {
            try {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                c.startActivity(intent);
            } catch (Exception ignored) {
            }
        }
    }
}
