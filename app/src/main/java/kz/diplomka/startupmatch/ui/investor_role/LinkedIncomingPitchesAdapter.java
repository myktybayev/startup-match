package kz.diplomka.startupmatch.ui.investor_role;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.databinding.ItemIncomingPitchCardBinding;
import kz.diplomka.startupmatch.ui.investor_role.model.IncomingPitchCardUi;

public final class LinkedIncomingPitchesAdapter extends RecyclerView.Adapter<LinkedIncomingPitchesAdapter.Holder> {

    private final List<IncomingPitchCardUi> items = new ArrayList<>();
    private final LayoutInflater inflater;
    private final PitchActions pitchActions;

    public interface PitchActions {
        void deletePitch(long pitchId);

        void openProjectChat(long projectId, @NonNull String startupName, @Nullable String fallbackPhone);
    }

    public LinkedIncomingPitchesAdapter(
            @NonNull Context context,
            @NonNull PitchActions pitchActions
    ) {
        this.inflater = LayoutInflater.from(context);
        this.pitchActions = pitchActions;
    }

    public void submit(@NonNull List<IncomingPitchCardUi> list) {
        items.clear();
        items.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(ItemIncomingPitchCardBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    final class Holder extends RecyclerView.ViewHolder {
        private final ItemIncomingPitchCardBinding binding;

        Holder(@NonNull ItemIncomingPitchCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(@NonNull IncomingPitchCardUi item) {
            Context c = binding.getRoot().getContext();
            binding.textStartupName.setText(item.startupName);
            binding.textReceivedTime.setText(item.timeLabel);
            binding.textTeamWhy.setText(item.teamWhyUs);
            binding.textValidation.setText(item.validationMarket);
            binding.textRowPitchSub.setText(item.pitchSubtitle);
            binding.textRowGithubSub.setText(item.githubSubtitle);
            binding.textRowMvpSub.setText(item.mvpSubtitle);
            binding.textRowTractionSub.setText(item.tractionSubtitle);

            if (item.amberLogo) {
                binding.frameLogo.setBackgroundResource(R.drawable.bg_incoming_logo_amber);
                binding.imageLogoSymbol.setImageResource(R.drawable.ic_incoming_book);
            } else {
                binding.frameLogo.setBackgroundResource(R.drawable.bg_incoming_logo_blue);
                binding.imageLogoSymbol.setImageResource(R.drawable.ic_incoming_lightning);
            }

            bindRowClick(binding.rowPitch, c, item.pitchUrl);
            bindRowClick(binding.rowGithub, c, item.githubUrl);
            bindRowClick(binding.rowMvp, c, item.mvpUrl);
            bindRowClick(binding.rowTraction, c, item.tractionUrl);

            binding.buttonReject.setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos == RecyclerView.NO_POSITION || pos < 0 || pos >= items.size()) {
                    return;
                }
                if (item.pitchId > 0L) {
                    pitchActions.deletePitch(item.pitchId);
                    Toast.makeText(c, R.string.incoming_pitch_rejected, Toast.LENGTH_SHORT).show();
                }
                items.remove(pos);
                notifyItemRemoved(pos);
            });
            binding.buttonStartChat.setOnClickListener(v -> {
                pitchActions.openProjectChat(item.projectId, item.startupName, item.contactPhone);
            });
        }

        private void bindRowClick(@NonNull View row, @NonNull Context c, @Nullable String url) {
            if (TextUtils.isEmpty(url)) {
                row.setOnClickListener(v ->
                        Toast.makeText(c, R.string.incoming_pitch_row_no_update, Toast.LENGTH_SHORT).show());
            } else {
                final String u = url.trim();
                row.setOnClickListener(v -> {
                    try {
                        c.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(u)));
                    } catch (Exception e) {
                        Toast.makeText(c, R.string.incoming_pitch_open_link_fail, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
