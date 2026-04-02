package kz.diplomka.startupmatch.ui.home.activity;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.ui.home.module.CommandMemberData;

/**
 * Binds team member cards for horizontal row (see {@code activity_add_command.xml}).
 */
public final class CommandMembersAdapter {

    private CommandMembersAdapter() {
    }

    public static void bindMemberCard(View row, CommandMemberData member) {
        if (member == null) {
            return;
        }

        ImageView imageMemberAvatar = row.findViewById(R.id.imageMemberAvatar);
        TextView textMemberName = row.findViewById(R.id.textMemberName);
        TextView textMemberRole = row.findViewById(R.id.textMemberRole);
        TextView textMemberExperience = row.findViewById(R.id.textMemberExperience);

        LinearLayout layoutTagChips = row.findViewById(R.id.layoutTagChips);
        TextView textTag1 = row.findViewById(R.id.textTag1);

        LinearLayout layoutLinkedin = row.findViewById(R.id.layoutLinkedin);
        TextView textMemberLinkedinLabel = row.findViewById(R.id.textMemberLinkedinLabel);
        TextView textMemberLinkedinValue = row.findViewById(R.id.textMemberLinkedinValue);

        String fullName = member.getFullName() == null ? "" : member.getFullName().trim();
        String role = member.getRole() == null ? "" : member.getRole().trim();
        String experience = member.getExperience() == null ? "" : member.getExperience().trim();
        String portfolio = member.getPortfolio() == null ? "" : member.getPortfolio().trim();

        textMemberName.setText(fullName);
        textMemberRole.setText(role);
        textMemberExperience.setText(experience);

        String avatarUri = member.getAvatarUri();
        if (avatarUri != null && !avatarUri.trim().isEmpty()) {
            try {
                imageMemberAvatar.setImageURI(Uri.parse(avatarUri));
            } catch (Exception ignored) {
                imageMemberAvatar.setImageResource(R.drawable.ic_dashboard_black_24dp);
            }
        } else {
            imageMemberAvatar.setImageResource(R.drawable.ic_dashboard_black_24dp);
        }

        List<String> chips = extractChipsFromExperience(role);
        if (chips.isEmpty()) {
            layoutTagChips.setVisibility(View.GONE);
        } else {
            layoutTagChips.setVisibility(View.VISIBLE);
            textTag1.setText(chips.get(0));
        }

        if (portfolio.isEmpty()) {
            layoutLinkedin.setVisibility(View.GONE);
        } else {
            layoutLinkedin.setVisibility(View.VISIBLE);
            textMemberLinkedinLabel.setText("LinkedIn:");
            textMemberLinkedinValue.setText(portfolio);
        }
    }

    private static List<String> extractChipsFromExperience(String experience) {
        List<String> result = new ArrayList<>();
        if (experience == null || experience.trim().isEmpty()) {
            return result;
        }

        String[] parts = experience.split("[\\n\\r,\\.؛();:]+");
        for (String part : parts) {
            String candidate = part == null ? "" : part.trim();
            if (candidate.isEmpty()) continue;
            if (candidate.length() > 26) candidate = candidate.substring(0, 26).trim();
            if (candidate.length() < 2) continue;
            if (!result.contains(candidate)) {
                result.add(candidate);
            }
            if (result.size() >= 2) break;
        }
        return result;
    }
}
