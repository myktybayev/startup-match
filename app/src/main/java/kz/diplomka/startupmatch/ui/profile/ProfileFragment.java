package kz.diplomka.startupmatch.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.data.local.AppDatabase;
import kz.diplomka.startupmatch.data.local.entity.AuthUserEntity;
import kz.diplomka.startupmatch.data.local.entity.ProjectEntity;
import kz.diplomka.startupmatch.data.local.entity.TeamMemberEntity;
import kz.diplomka.startupmatch.data.local.session.AuthRolePrefs;
import kz.diplomka.startupmatch.data.local.session.SessionLocalStore;
import kz.diplomka.startupmatch.databinding.FragmentProfileBinding;
import kz.diplomka.startupmatch.ui.authentication.RolePageActivity;
import kz.diplomka.startupmatch.ui.home.activity.CommandListActivity;
import kz.diplomka.startupmatch.ui.home.navigation.ProjectFlowExtras;
import kz.diplomka.startupmatch.util.WhatsAppUtils;

/**
 * Профиль экраны (Figma 99:2163). Команда/жоба деректері — {@link AppDatabase}.
 */
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private final CompositeDisposable disposables = new CompositeDisposable();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonProfileSettings.setOnClickListener(v -> showAccountCredentialsDialog());
        binding.profileSetting.setOnClickListener(v -> showAccountCredentialsDialog());
        binding.rowTeamAbout.setOnClickListener(v -> openCommandList());
        binding.rowLinkedInvestors.setOnClickListener(v -> openCommandList());
        binding.rowSecurity.setOnClickListener(v ->
                Toast.makeText(requireContext(), R.string.profile_toast_soon, Toast.LENGTH_SHORT).show());
        binding.rowHelp.setOnClickListener(v ->
                WhatsAppUtils.openChatOrToast(
                        requireContext(),
                        "77023310762",
                        getString(R.string.profile_help_whatsapp_message)));
        binding.rowLogout.setOnClickListener(v -> logout());

    }

    @Override
    public void onResume() {
        super.onResume();
        bindFromDatabase();
    }

    private void bindFromDatabase() {
        if (binding == null) {
            return;
        }
        AuthUserEntity authUser = resolveCurrentAuthUser();
        if (authUser != null && !TextUtils.isEmpty(authUser.getPhone())) {
            binding.textProfilePhone.setText(authUser.getPhone());
        } else {
            binding.textProfilePhone.setText(R.string.profile_phone_not_set);
        }

        AppDatabase db = AppDatabase.get(requireContext());
        ProjectEntity project = db.projectDao().getLatest();
        if (project == null) {
            binding.textProfileTeamName.setText(R.string.profile_no_project_hint);
            binding.textStatTeamMembers.setText("0");
            binding.textStatActiveProjects.setText("0");
            binding.textProfileInvestorsSubtitle.setText(
                    getString(R.string.profile_investors_subtitle_format, 0, 0));
            return;
        }
        binding.textProfileTeamName.setText(project.getName());
        int members = db.teamMemberDao().countForProject(project.getId());
        binding.textStatTeamMembers.setText(String.valueOf(members));
        List<ProjectEntity> all = db.projectDao().getAll();
        binding.textStatActiveProjects.setText(String.valueOf(all.size()));

        List<TeamMemberEntity> memList = db.teamMemberDao().listForProject(project.getId());
        binding.textProfileTeamAboutSubtitle.setText(buildRolesLine(memList));

        int pitches = db.investorPitchDao().countForProject(project.getId());
        int challenges = db.challengeSubmissionDao().countForProject(project.getId());
        binding.textProfileInvestorsSubtitle.setText(
                getString(R.string.profile_investors_subtitle_format, pitches, challenges));
        String stage = project.getMvpLink() != null && !project.getMvpLink().trim().isEmpty()
                ? "MVP"
                : "Idea";

    }

    private void openCommandList() {
        Intent i = new Intent(requireContext(), CommandListActivity.class);
        ProjectEntity p = AppDatabase.get(requireContext()).projectDao().getLatest();
        if (p != null) {
            i.putExtra(ProjectFlowExtras.EXTRA_PROJECT_ID, p.getId());
        }
        startActivity(i);
    }

    private void showAccountCredentialsDialog() {
        if (binding == null) {
            return;
        }
        AuthUserEntity user = resolveCurrentAuthUser();
        if (user == null) {
            Toast.makeText(requireContext(), R.string.profile_credentials_not_found, Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_profile_account_credentials, null, false);
        TextView textEmail = dialogView.findViewById(R.id.text_dialog_email);
        TextView textPassword = dialogView.findViewById(R.id.text_dialog_password);
        textEmail.setText(user.getEmail());
        textPassword.setText(user.getPassword());

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();
        dialogView.findViewById(R.id.button_dialog_close).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @Nullable
    private AuthUserEntity resolveCurrentAuthUser() {
        AppDatabase db = AppDatabase.get(requireContext());
        String role = AuthRolePrefs.getSelectedRole(requireContext());
        if (!TextUtils.isEmpty(role)) {
            AuthUserEntity byRole = db.authUserDao().getLatestByRole(role);
            if (byRole != null) {
                return byRole;
            }
        }
        return db.authUserDao().getLatest();
    }

    private void logout() {
        disposables.add(
                SessionLocalStore.setLoggedIn(requireContext(), false)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> Toast.makeText(
                                                requireContext(),
                                                R.string.profile_toast_logout,
                                                Toast.LENGTH_SHORT)
                                        .show(),
                                Throwable::printStackTrace));

        startActivity(new Intent(getActivity(), RolePageActivity.class));
    }

    @NonNull
    private static String teamEmailFromProject(@NonNull String projectName) {
        String slug = projectName.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "");
        if (slug.isEmpty()) {
            slug = "team";
        }
        return "team@" + slug + ".local";
    }

    @NonNull
    private String buildRolesLine(@NonNull List<TeamMemberEntity> members) {
        if (members.isEmpty()) {
            return getString(R.string.profile_team_roles_placeholder);
        }
        LinkedHashSet<String> roles = new LinkedHashSet<>();
        for (TeamMemberEntity m : members) {
            roles.add(m.getRole());
        }
        List<String> list = new ArrayList<>(roles);
        String joined = TextUtils.join(", ", list);
        if (joined.length() > 120) {
            return joined.substring(0, 117) + "…";
        }
        return joined;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposables.clear();
        binding = null;
    }
}
