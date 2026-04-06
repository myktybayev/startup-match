package kz.diplomka.startupmatch.ui.home.command;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.data.local.AppDatabase;
import kz.diplomka.startupmatch.data.local.entity.TeamMemberEntity;
import kz.diplomka.startupmatch.ui.home.activity.CommandListActivity;
import kz.diplomka.startupmatch.ui.home.navigation.ProjectFlowExtras;

public class TeamMembersFragment extends Fragment {

    private static final String ARG_PROJECT_ID = ProjectFlowExtras.EXTRA_PROJECT_ID;

    private long projectId = -1L;
    private LinearLayout layoutMembers;
    private TextView textEmpty;

    public static TeamMembersFragment newInstance(long projectId) {
        TeamMembersFragment f = new TeamMembersFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PROJECT_ID, projectId);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            projectId = args.getLong(ARG_PROJECT_ID, -1L);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_team_members, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        layoutMembers = view.findViewById(R.id.layoutMembers);
        textEmpty = view.findViewById(R.id.textEmpty);
    }

    @Override
    public void onResume() {
        super.onResume();
        render();
    }

    private void render() {
        if (layoutMembers == null || textEmpty == null) {
            return;
        }
        layoutMembers.removeAllViews();

        if (projectId <= 0) {
            textEmpty.setVisibility(View.VISIBLE);
            notifyActivityHeaderMeta();
            return;
        }

        List<TeamMemberEntity> members = AppDatabase.get(requireContext()).teamMemberDao().listForProject(projectId);
        if (members == null || members.isEmpty()) {
            textEmpty.setVisibility(View.VISIBLE);
            notifyActivityHeaderMeta();
            return;
        }

        textEmpty.setVisibility(View.GONE);
        notifyActivityHeaderMeta();

        LayoutInflater inflater = LayoutInflater.from(requireContext());
        for (int i = 0; i < members.size(); i++) {
            TeamMemberEntity m = members.get(i);
            View card = inflater.inflate(R.layout.item_command_list_member, layoutMembers, false);
            bindMemberCard(card, m);
            card.setClickable(true);
            card.setFocusable(true);
            card.setOnClickListener(v -> showDeleteMemberDialog(m));

            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) card.getLayoutParams();
            if (lp != null && i > 0) {
                lp.topMargin = dpToPx(12);
                card.setLayoutParams(lp);
            }
            layoutMembers.addView(card);
        }
    }

    private void notifyActivityHeaderMeta() {
        if (getActivity() instanceof CommandListActivity) {
            ((CommandListActivity) getActivity()).refreshTeamHeaderMeta();
        }
    }

    private void showDeleteMemberDialog(@NonNull TeamMemberEntity member) {
        String name = member.getFullName() != null ? member.getFullName().trim() : "";
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.command_list_delete_member_title)
                .setMessage(getString(R.string.command_list_delete_member_message, name))
                .setNegativeButton(R.string.dialog_cancel, null)
                .setPositiveButton(R.string.dialog_yes, (dialog, which) -> deleteMember(member))
                .show();
    }

    private void deleteMember(@NonNull TeamMemberEntity member) {
        long id = member.getId();
        maybeDeleteAvatarFile(member.getAvatarUri());
        AppDatabase.get(requireContext()).teamMemberDao().deleteById(id);
        Toast.makeText(requireContext(), getString(R.string.command_list_member_deleted), Toast.LENGTH_SHORT).show();
        render();
        if (getActivity() instanceof CommandListActivity) {
            ((CommandListActivity) getActivity()).setupHeader();
        }
    }

    private void maybeDeleteAvatarFile(@Nullable String avatarUri) {
        if (TextUtils.isEmpty(avatarUri)) {
            return;
        }
        try {
            Uri uri = Uri.parse(avatarUri);
            if (!"file".equalsIgnoreCase(uri.getScheme())) {
                return;
            }
            File f = new File(uri.getPath());
            if (f.exists() && f.getAbsolutePath().contains("member_avatars")) {
                //noinspection ResultOfMethodCallIgnored
                f.delete();
            }
        } catch (Exception ignored) {
        }
    }

    private void bindMemberCard(@NonNull View card, @NonNull TeamMemberEntity member) {
        CircleImageView avatar = card.findViewById(R.id.imageAvatar);
        TextView name = card.findViewById(R.id.textName);
        TextView role = card.findViewById(R.id.chipRole);
        TextView exp = card.findViewById(R.id.textExperience);
        TextView portfolio = card.findViewById(R.id.textPortfolio);

        name.setText(member.getFullName());
        role.setText(member.getRole());
        exp.setText(member.getExperience());

        String p = member.getPortfolio();
        boolean hasPortfolio = !TextUtils.isEmpty(p);
        portfolio.setVisibility(hasPortfolio ? View.VISIBLE : View.GONE);
        if (hasPortfolio) {
            portfolio.setText("LinkedIn: " + p);
        }

        String avatarUri = member.getAvatarUri();
        if (!TextUtils.isEmpty(avatarUri)) {
            try {
                avatar.setImageURI(Uri.parse(avatarUri));
            } catch (Exception ignored) {
                avatar.setImageResource(R.drawable.home_user_avatar);
            }
        } else {
            avatar.setImageResource(R.drawable.home_user_avatar);
        }
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
