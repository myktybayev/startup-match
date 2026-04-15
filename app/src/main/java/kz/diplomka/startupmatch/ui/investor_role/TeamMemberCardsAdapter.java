package kz.diplomka.startupmatch.ui.investor_role;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.databinding.ItemTeamMemberCardBinding;
import kz.diplomka.startupmatch.ui.investor_role.model.TeamMemberRowUi;

public final class TeamMemberCardsAdapter extends RecyclerView.Adapter<TeamMemberCardsAdapter.Holder> {

    private static final String[] ASSET_AVATARS = new String[]{
            "figma_command_list/avatar_arman.png",
            "figma_command_list/avatar_aigerim.png",
            "figma_command_list/avatar_nursultan.png",
            "figma_command_list/avatar_dana.png"
    };

    private final List<TeamMemberRowUi> rows = new ArrayList<>();
    private final LayoutInflater inflater;
    /** Әр жобада бірдей ретте аватар тұрмауы үшін asset индексіне қосылады. */
    private int avatarStartOffset;

    public TeamMemberCardsAdapter(@NonNull Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    public void submit(@NonNull List<TeamMemberRowUi> list, long projectId) {
        avatarStartOffset = computeAvatarStartOffset(projectId);
        rows.clear();
        rows.addAll(list);
        notifyDataSetChanged();
    }

    private static int computeAvatarStartOffset(long projectId) {
        int n = ASSET_AVATARS.length;
        if (n <= 1) {
            return 0;
        }
        int h = Long.hashCode(projectId);
        return Math.floorMod(h, n);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(ItemTeamMemberCardBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.bind(rows.get(position), position);
    }

    @Override
    public int getItemCount() {
        return rows.size();
    }

    final class Holder extends RecyclerView.ViewHolder {
        private final ItemTeamMemberCardBinding binding;

        Holder(@NonNull ItemTeamMemberCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(@NonNull TeamMemberRowUi item, int position) {
            Context c = binding.getRoot().getContext();
            binding.textName.setText(item.name);
            binding.textRoleBadge.setText(item.role);
            binding.textBio.setText(item.bio);

            if (TextUtils.isEmpty(item.chipOne)) {
                binding.textChipOne.setVisibility(View.GONE);
            } else {
                binding.textChipOne.setVisibility(View.VISIBLE);
                binding.textChipOne.setText(item.chipOne);
            }
            if (TextUtils.isEmpty(item.chipTwo)) {
                binding.textChipTwo.setVisibility(View.GONE);
            } else {
                binding.textChipTwo.setVisibility(View.VISIBLE);
                binding.textChipTwo.setText(item.chipTwo);
            }
            binding.rowChips.setVisibility(
                    binding.textChipOne.getVisibility() == View.VISIBLE
                            || binding.textChipTwo.getVisibility() == View.VISIBLE
                            ? View.VISIBLE
                            : View.GONE
            );

            if (TextUtils.isEmpty(item.linkLine)) {
                binding.textLink.setVisibility(View.GONE);
                binding.textLink.setOnClickListener(null);
            } else {
                binding.textLink.setVisibility(View.VISIBLE);
                binding.textLink.setText(item.linkLine);
                if (item.hasClickableLink()) {
                    Uri uri = item.linkUri();
                    binding.textLink.setOnClickListener(v -> {
                        if (uri != null) {
                            try {
                                c.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                            } catch (Exception ignored) {
                            }
                        }
                    });
                } else {
                    binding.textLink.setOnClickListener(null);
                }
            }

            bindAvatar(c, item.avatarUri, position);
        }

        private void bindAvatar(@NonNull Context c, @Nullable String avatarUri, int position) {
            if (!TextUtils.isEmpty(avatarUri)) {
                try {
                    binding.imageAvatar.setImageURI(Uri.parse(avatarUri.trim()));
                    return;
                } catch (Exception ignored) {
                }
            }
            int n = ASSET_AVATARS.length;
            int idx = Math.floorMod(avatarStartOffset + position, n);
            try (InputStream is = c.getAssets().open(ASSET_AVATARS[idx])) {
                binding.imageAvatar.setImageBitmap(BitmapFactory.decodeStream(is));
            } catch (Exception e) {
                binding.imageAvatar.setImageResource(R.drawable.bg_investors_icon_circle);
            }
        }
    }
}
