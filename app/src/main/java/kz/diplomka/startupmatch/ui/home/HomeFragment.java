package kz.diplomka.startupmatch.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;

import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.data.local.AppDatabase;
import kz.diplomka.startupmatch.data.local.entity.ProjectEntity;
import kz.diplomka.startupmatch.databinding.FragmentHomeBinding;
import kz.diplomka.startupmatch.ui.home.activity.AddMvpActivity;
import kz.diplomka.startupmatch.ui.home.activity.AddPitchDeckActivity;
import kz.diplomka.startupmatch.ui.home.activity.AddTractionActivity;
import kz.diplomka.startupmatch.ui.home.activity.AddProjectGithubActivity;
import kz.diplomka.startupmatch.ui.home.activity.AddProjectActivity;
import kz.diplomka.startupmatch.ui.home.activity.CommandListActivity;
import kz.diplomka.startupmatch.ui.home.navigation.ProjectFlowExtras;

public class HomeFragment extends Fragment {

    /** Профиль дайындығы: 6 тең қадам (100% / 6). */
    private static final int PROFILE_TOTAL_STEPS = 6;

    private FragmentHomeBinding binding;
    private long latestProjectId = -1L;

    /** Pitch, GitHub, MVP, Traction экрандарынан оралғанда профиль % жаңарту. */
    private ActivityResultLauncher<Intent> projectFlowLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        projectFlowLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (binding != null) {
                        loadLatestProject();
                    }
                }
        );
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        binding.buttonAddProject.setOnClickListener(v -> startActivity(new Intent(requireContext(), AddProjectActivity.class)));
        binding.commandLayout.setOnClickListener(v -> startActivity(new Intent(requireContext(), CommandListActivity.class)));
        binding.pitchBtn.getRoot().setOnClickListener(v -> openPitchScreen());
        binding.githubBtn.getRoot().setOnClickListener(v -> openGithubScreen());
        binding.mvpBtn.getRoot().setOnClickListener(v -> openMvpScreen());
        binding.tractionBtn.getRoot().setOnClickListener(v -> openTractionScreen());
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
        applyPitchStepUi(false);
        applyGithubStepUi(false);
        applyMvpStepUi(false);
        applyTractionStepUi(false);
    }

    private void updateStepStates(@NonNull ProjectEntity project) {
        applyPitchStepUi(!TextUtils.isEmpty(project.getPitchDriveLink()));
        applyGithubStepUi(!TextUtils.isEmpty(project.getGithubLink()));
        applyMvpStepUi(!TextUtils.isEmpty(project.getMvpLink()));
        applyTractionStepUi(!TextUtils.isEmpty(project.getTractionLink()));
    }

    /**
     * Pitch Deck: толтырылғанда — қою тақырып, көк иконка фоны, жасыл галочка (дизайн бойынша).
     */
    private void applyPitchStepUi(boolean completed) {
        MaterialCardView card = (MaterialCardView) binding.pitchBtn.getRoot();
        float density = getResources().getDisplayMetrics().density;
        card.setAlpha(completed ? 1f : 0.7f);
        card.setCardElevation(completed ? 2f * density : 1f * density);

        binding.pitchBtn.framePitchIcon.setBackgroundResource(
                completed ? R.drawable.bg_home_step_icon_done : R.drawable.bg_home_step_icon);
        binding.pitchBtn.imagePitchLogo.setImageResource(
                completed ? R.drawable.home_icon_pitch_active : R.drawable.home_icon_pitch);
        binding.pitchBtn.textPitchTitle.setTextColor(
                ContextCompat.getColor(requireContext(),
                        completed ? R.color.home_step_title_done : R.color.home_step_title_pending));
        binding.pitchBtn.textPitchTitle.setTypeface(
                ResourcesCompat.getFont(requireContext(),
                        completed ? R.font.inter_semibold : R.font.inter_medium));

        binding.pitchBtn.imagePitchStatus.setImageResource(
                completed ? R.drawable.home_icon_step_done : R.drawable.home_icon_circle_dashed);
        binding.pitchBtn.imagePitchStatus.setContentDescription(
                getString(completed ? R.string.home_step_pitch_done_a11y : R.string.home_step_pitch_pending_a11y));
    }

    /**
     * GitHub: сілтеме сақталғанда — қою тақырып, көгілдір иконка фоны (#EAF2FF), жасыл галочка.
     */
    private void applyGithubStepUi(boolean completed) {
        MaterialCardView card = (MaterialCardView) binding.githubBtn.getRoot();
        float density = getResources().getDisplayMetrics().density;
        card.setAlpha(completed ? 1f : 0.7f);
        card.setCardElevation(completed ? 2f * density : 1f * density);

        binding.githubBtn.frameGithubIcon.setBackgroundResource(
                completed ? R.drawable.bg_home_step_icon_done : R.drawable.bg_home_step_icon);
        binding.githubBtn.imageGithubLogo.setImageResource(
                completed ? R.drawable.home_icon_github_active : R.drawable.home_icon_github);
        binding.githubBtn.textGithubTitle.setTextColor(
                ContextCompat.getColor(requireContext(),
                        completed ? R.color.home_step_title_done : R.color.home_step_title_pending));
        binding.githubBtn.textGithubTitle.setTypeface(
                ResourcesCompat.getFont(requireContext(),
                        completed ? R.font.inter_semibold : R.font.inter_medium));

        binding.githubBtn.imageGithubStatus.setImageResource(
                completed ? R.drawable.home_icon_step_done : R.drawable.home_icon_circle_dashed);
        binding.githubBtn.imageGithubStatus.setContentDescription(
                getString(completed ? R.string.home_step_github_done_a11y : R.string.home_step_github_pending_a11y));
    }

    /**
     * MVP: сілтеме сақталғанда — қою тақырып, көгілдір иконка фоны, жасыл галочка.
     */
    private void applyMvpStepUi(boolean completed) {
        MaterialCardView card = (MaterialCardView) binding.mvpBtn.getRoot();
        float density = getResources().getDisplayMetrics().density;
        card.setAlpha(completed ? 1f : 0.7f);
        card.setCardElevation(completed ? 2f * density : 1f * density);

        binding.mvpBtn.frameMvpIcon.setBackgroundResource(
                completed ? R.drawable.bg_home_step_icon_done : R.drawable.bg_home_step_icon);
        binding.mvpBtn.imageMvpPhone.setImageResource(
                completed ? R.drawable.home_icon_phone_active : R.drawable.home_icon_phone);
        binding.mvpBtn.textMvpTitle.setTextColor(
                ContextCompat.getColor(requireContext(),
                        completed ? R.color.home_step_title_done : R.color.home_step_title_pending));
        binding.mvpBtn.textMvpTitle.setTypeface(
                ResourcesCompat.getFont(requireContext(),
                        completed ? R.font.inter_semibold : R.font.inter_medium));

        binding.mvpBtn.imageMvpStatus.setImageResource(
                completed ? R.drawable.home_icon_step_done : R.drawable.home_icon_circle_dashed);
        binding.mvpBtn.imageMvpStatus.setContentDescription(
                getString(completed ? R.string.home_step_mvp_done_a11y : R.string.home_step_mvp_pending_a11y));
    }

    /**
     * Traction: көрсеткіштер сақталғанда — қою тақырып, көгілдір фон, көк график иконкасы, жасыл галочка.
     */
    private void applyTractionStepUi(boolean completed) {
        MaterialCardView card = (MaterialCardView) binding.tractionBtn.getRoot();
        float density = getResources().getDisplayMetrics().density;
        card.setAlpha(completed ? 1f : 0.7f);
        card.setCardElevation(completed ? 2f * density : 1f * density);

        binding.tractionBtn.frameTractionIcon.setBackgroundResource(
                completed ? R.drawable.bg_home_step_icon_done : R.drawable.bg_home_step_icon);
        binding.tractionBtn.imageTractionChart.setImageResource(
                completed ? R.drawable.home_icon_chart_active : R.drawable.home_icon_chart);
        binding.tractionBtn.textTractionTitle.setTextColor(
                ContextCompat.getColor(requireContext(),
                        completed ? R.color.home_step_title_done : R.color.home_step_title_pending));
        binding.tractionBtn.textTractionTitle.setTypeface(
                ResourcesCompat.getFont(requireContext(),
                        completed ? R.font.inter_semibold : R.font.inter_medium));

        binding.tractionBtn.imageTractionStatus.setImageResource(
                completed ? R.drawable.home_icon_step_done : R.drawable.home_icon_circle_dashed);
        binding.tractionBtn.imageTractionStatus.setContentDescription(
                getString(completed ? R.string.home_step_traction_done_a11y : R.string.home_step_traction_pending_a11y));
    }

    /**
     * Ағымдағы жоба id-сін DB-дан алып береді; сақтаудан кейін {@link #projectFlowLauncher} арқылы прогресс жаңарады.
     */
    private void openPitchScreen() {
        ProjectEntity current = AppDatabase.get(requireContext()).projectDao().getLatest();
        if (current == null) {
            Toast.makeText(requireContext(), getString(R.string.error_no_project_for_pitch), Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(requireContext(), AddPitchDeckActivity.class);
        intent.putExtra(ProjectFlowExtras.EXTRA_PROJECT_ID, current.getId());
        projectFlowLauncher.launch(intent);
    }

    private void openGithubScreen() {
        ProjectEntity current = AppDatabase.get(requireContext()).projectDao().getLatest();
        if (current == null) {
            Toast.makeText(requireContext(), getString(R.string.error_no_project_for_github), Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(requireContext(), AddProjectGithubActivity.class);
        intent.putExtra(ProjectFlowExtras.EXTRA_PROJECT_ID, current.getId());
        projectFlowLauncher.launch(intent);
    }

    private void openMvpScreen() {
        ProjectEntity current = AppDatabase.get(requireContext()).projectDao().getLatest();
        if (current == null) {
            Toast.makeText(requireContext(), getString(R.string.error_no_project_for_mvp), Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(requireContext(), AddMvpActivity.class);
        intent.putExtra(ProjectFlowExtras.EXTRA_PROJECT_ID, current.getId());
        projectFlowLauncher.launch(intent);
    }

    private void openTractionScreen() {
        ProjectEntity current = AppDatabase.get(requireContext()).projectDao().getLatest();
        if (current == null) {
            Toast.makeText(requireContext(), getString(R.string.error_no_project_for_traction), Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(requireContext(), AddTractionActivity.class);
        intent.putExtra(ProjectFlowExtras.EXTRA_PROJECT_ID, current.getId());
        projectFlowLauncher.launch(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
