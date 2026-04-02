package kz.diplomka.startupmatch.ui.home.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.data.local.AppDatabase;
import kz.diplomka.startupmatch.data.local.entity.ProjectEntity;
import kz.diplomka.startupmatch.data.local.entity.TeamMemberEntity;
import kz.diplomka.startupmatch.ui.home.navigation.ProjectFlowExtras;

public class CommandListActivity extends AppCompatActivity {

    private long projectId = -1L;

    private LinearLayout layoutMembers;
    private TextView textEmpty;
    private TextView textTeamName;
    private TextView textTeamMeta;
    private CircleImageView imageTeamAvatar;
    private TextView chipPitch;
    private ImageView buttonAddMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command_list);

        projectId = getIntent().getLongExtra(ProjectFlowExtras.EXTRA_PROJECT_ID, -1L);
        if (projectId <= 0) {
            ProjectEntity latest = AppDatabase.get(this).projectDao().getLatest();
            if (latest != null) {
                projectId = latest.getId();
            }
        }

        bindViews();
        setupHeader();
        render();
    }

    private void bindViews() {
        layoutMembers = findViewById(R.id.layoutMembers);
        textEmpty = findViewById(R.id.textEmpty);
        textTeamName = findViewById(R.id.textTeamName);
        textTeamMeta = findViewById(R.id.textTeamMeta);
        imageTeamAvatar = findViewById(R.id.imageTeamAvatar);
        chipPitch = findViewById(R.id.chipPitch);
        buttonAddMember = findViewById(R.id.buttonAddMember);

        FrameLayout buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> finish());
        buttonAddMember.setOnClickListener(v -> addCommandMember());
    }

    private void addCommandMember() {
        Intent intent = new Intent(this, AddCommandActivity.class);
        intent.putExtra(ProjectFlowExtras.EXTRA_PROJECT_ID, projectId);
        startActivity(intent);
    }

    private void setupHeader() {
        if (projectId <= 0) {
            textTeamName.setText(getString(R.string.home_team));
            textTeamMeta.setText(getString(R.string.command_list_team_meta_placeholder));
            chipPitch.setVisibility(View.GONE);
            return;
        }

        ProjectEntity project = AppDatabase.get(this).projectDao().getById(projectId);
        if (project == null) {
            Toast.makeText(this, getString(R.string.error_invalid_project), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        textTeamName.setText(project.getName());
        boolean hasPitch = !TextUtils.isEmpty(project.getPitchDriveLink());
        chipPitch.setVisibility(hasPitch ? View.VISIBLE : View.GONE);
    }

    private void render() {
        layoutMembers.removeAllViews();

        if (projectId <= 0) {
            textEmpty.setVisibility(View.VISIBLE);
            return;
        }

        List<TeamMemberEntity> members = AppDatabase.get(this).teamMemberDao().listForProject(projectId);
        if (members == null || members.isEmpty()) {
            textEmpty.setVisibility(View.VISIBLE);
            textTeamMeta.setText(getString(R.string.command_list_team_meta_placeholder));
            return;
        }

        textEmpty.setVisibility(View.GONE);
        textTeamMeta.setText(buildMeta(members.size()));

        LayoutInflater inflater = LayoutInflater.from(this);
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

    private void showDeleteMemberDialog(@NonNull TeamMemberEntity member) {
        String name = member.getFullName() != null ? member.getFullName().trim() : "";
        new AlertDialog.Builder(this)
                .setTitle(R.string.command_list_delete_member_title)
                .setMessage(getString(R.string.command_list_delete_member_message, name))
                .setNegativeButton(R.string.dialog_cancel, null)
                .setPositiveButton(R.string.dialog_yes, (dialog, which) -> deleteMember(member))
                .show();
    }

    private void deleteMember(@NonNull TeamMemberEntity member) {
        long id = member.getId();
        maybeDeleteAvatarFile(member.getAvatarUri());
        AppDatabase.get(this).teamMemberDao().deleteById(id);
        Toast.makeText(this, getString(R.string.command_list_member_deleted), Toast.LENGTH_SHORT).show();
        render();
        setupHeader();
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

    private String buildMeta(int membersCount) {
        return membersCount + " мүше · Early-stage startup · Investor-ready профиль";
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}

