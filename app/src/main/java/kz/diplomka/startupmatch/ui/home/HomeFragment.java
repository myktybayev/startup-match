package kz.diplomka.startupmatch.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.data.local.AppDatabase;
import kz.diplomka.startupmatch.data.local.entity.ProjectEntity;
import kz.diplomka.startupmatch.databinding.FragmentHomeBinding;
import kz.diplomka.startupmatch.ui.home.activity.AddPitchDeckActivity;
import kz.diplomka.startupmatch.ui.home.activity.AddProjectActivity;
import kz.diplomka.startupmatch.ui.home.activity.CommandListActivity;
import kz.diplomka.startupmatch.ui.home.navigation.ProjectFlowExtras;

public class HomeFragment extends Fragment {

    /** Профиль дайындығы: 6 тең қадам (100% / 6). */
    private static final int PROFILE_TOTAL_STEPS = 6;

    private FragmentHomeBinding binding;
    private long latestProjectId = -1L;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        binding.buttonAddProject.setOnClickListener(v -> startActivity(new Intent(requireContext(), AddProjectActivity.class)));
        binding.commandLayout.setOnClickListener(v -> startActivity(new Intent(requireContext(), CommandListActivity.class)));
        binding.pitchBtn.getRoot().setOnClickListener(
                v -> openPitchScreen()
        );
        loadLatestProject();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding != null) {
            loadLatestProject();
        }
    }

    private void loadLatestProject() {
        ProjectEntity latest = AppDatabase.get(requireContext()).projectDao().getLatest();
        if (latest == null) {
            latestProjectId = -1L;
            binding.textTeamName.setText(getString(R.string.home_team));
            binding.textProjectName.setText(getString(R.string.home_ai_score));
            binding.textProgressPercent.setText(getString(R.string.home_percent_zero));
            binding.progressProfile.setProgress(0);
            binding.buttonAddProject.setText(getString(R.string.home_add_project));
            setAllStepsIncomplete();
            return;
        }

        latestProjectId = latest.getId();
        binding.textTeamName.setText(latest.getName());
        binding.textProjectName.setText(latest.getName());

        int progressPercent = calculateProfilePercent(latestProjectId, latest);
        binding.textProgressPercent.setText(getString(R.string.home_percent_format, progressPercent));
        binding.progressProfile.setProgress(progressPercent);
        binding.buttonAddProject.setText(getString(R.string.home_add_project_another));
        updateStepStates(latest);
    }

    /**
     * 1) Жоба қосу 2) Команда мүшелері 3) Pitch 4) GitHub 5) MVP 6) Traction — әрқайсысы ~100/6%.
     */
    private int calculateProfilePercent(long projectId, @NonNull ProjectEntity project) {
        int done = 0;
        done++; // жоба жолы бар
        if (AppDatabase.get(requireContext()).teamMemberDao().countForProject(projectId) > 0) {
            done++;
        }
        if (!TextUtils.isEmpty(project.getPitchDriveLink())) {
            done++;
        }
        if (!TextUtils.isEmpty(project.getGithubLink())) {
            done++;
        }
        if (!TextUtils.isEmpty(project.getMvpLink())) {
            done++;
        }
        if (!TextUtils.isEmpty(project.getTractionLink())) {
            done++;
        }
        return (int) Math.round(done * 100.0 / PROFILE_TOTAL_STEPS);
    }

    private void setAllStepsIncomplete() {
        float dim = 0.7f;
        binding.pitchBtn.getRoot().setAlpha(dim);
        binding.githubBtn.getRoot().setAlpha(dim);
        binding.mvpBtn.getRoot().setAlpha(dim);
        binding.tractionBtn.getRoot().setAlpha(dim);
    }

    private void updateStepStates(@NonNull ProjectEntity project) {
        binding.pitchBtn.getRoot().setAlpha(!TextUtils.isEmpty(project.getPitchDriveLink()) ? 1f : 0.7f);
        binding.githubBtn.getRoot().setAlpha(!TextUtils.isEmpty(project.getGithubLink()) ? 1f : 0.7f);
        binding.mvpBtn.getRoot().setAlpha(!TextUtils.isEmpty(project.getMvpLink()) ? 1f : 0.7f);
        binding.tractionBtn.getRoot().setAlpha(!TextUtils.isEmpty(project.getTractionLink()) ? 1f : 0.7f);
    }

    private void openPitchScreen() {
        Intent intent = new Intent(requireContext(), AddPitchDeckActivity.class);
        if (latestProjectId > 0) {
            intent.putExtra(ProjectFlowExtras.EXTRA_PROJECT_ID, latestProjectId);
        }
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
