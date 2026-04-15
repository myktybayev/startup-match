package kz.diplomka.startupmatch.ui.сhallenges;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.data.local.AppDatabase;
import kz.diplomka.startupmatch.data.local.entity.ChallengeSubmissionEntity;
import kz.diplomka.startupmatch.data.local.entity.ProjectEntity;
import kz.diplomka.startupmatch.databinding.ActivityChallengeAnswerBinding;
import kz.diplomka.startupmatch.ui.сhallenges.data.ChallengesRepository;
import kz.diplomka.startupmatch.ui.сhallenges.model.ChallengeDetail;

public class ChallengeAnswerActivity extends AppCompatActivity {

    public static final String EXTRA_CHALLENGE_ID = "kz.diplomka.startupmatch.extra.CHALLENGE_ID";

    @NonNull
    public static Intent newIntent(@NonNull Context context, long challengeId) {
        Intent intent = new Intent(context, ChallengeAnswerActivity.class);
        intent.putExtra(EXTRA_CHALLENGE_ID, challengeId);
        return intent;
    }

    private ActivityChallengeAnswerBinding binding;
    private final List<ProjectEntity> projects = new ArrayList<>();
    private long challengeId = -1L;
    private ChallengeDetail challengeDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChallengeAnswerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        challengeId = getIntent().getLongExtra(EXTRA_CHALLENGE_ID, -1L);
        challengeDetail = new ChallengesRepository(this).getChallengeDetail(challengeId);

        bindSummary(challengeDetail);
        binding.buttonBack.setOnClickListener(v -> finish());
        loadProjects();
        binding.buttonSubmit.setOnClickListener(v -> trySubmit());
    }

    private void bindSummary(@NonNull ChallengeDetail detail) {
        binding.textSummaryTitle.setText(detail.getTitle());
        binding.textSummaryDeadline.setText(shortDeadlineLabel(detail.getDeadlineLine()));
        binding.textSummaryCategories.setText(detail.getCategoriesLine());
        binding.textSummaryInvestorName.setText(detail.getInvestorProfile().name);
        if (!TextUtils.isEmpty(detail.getInvestorPhotoUri())) {
            try {
                binding.imageSummaryInvestor.setImageURI(Uri.parse(detail.getInvestorPhotoUri()));
            } catch (Exception e) {
                binding.imageSummaryInvestor.setImageResource(detail.getInvestorProfile().avatarResId);
            }
        } else {
            binding.imageSummaryInvestor.setImageResource(detail.getInvestorProfile().avatarResId);
        }
    }

    /**
     * Карточкада тек күн/мерзім көрінісі (Figma): «Deadline:» префиксін алып тастаймыз.
     */
    @NonNull
    private static String shortDeadlineLabel(@NonNull String deadlineLine) {
        int colon = deadlineLine.indexOf(':');
        if (colon >= 0 && colon + 1 < deadlineLine.length()) {
            return deadlineLine.substring(colon + 1).trim();
        }
        return deadlineLine;
    }

    private void loadProjects() {
        projects.clear();
        projects.addAll(AppDatabase.get(this).projectDao().getAll());
        if (projects.isEmpty()) {
            binding.textNoProjects.setVisibility(View.VISIBLE);
            binding.spinnerProject.setVisibility(View.GONE);
            binding.buttonSubmit.setEnabled(false);
            return;
        }
        binding.textNoProjects.setVisibility(View.GONE);
        binding.spinnerProject.setVisibility(View.VISIBLE);
        binding.buttonSubmit.setEnabled(true);

        ProjectSpinnerAdapter adapter = new ProjectSpinnerAdapter(this, projects);
        binding.spinnerProject.setAdapter(adapter);
        binding.spinnerProject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < 0 || position >= projects.size()) {
                    return;
                }
                prefillFromProject(projects.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        binding.spinnerProject.setSelection(0);
    }

    private void prefillFromProject(@NonNull ProjectEntity p) {
        String pitch = p.getPitchDriveLink();
        String mvp = p.getMvpLink();
        binding.editPitchLink.setText(pitch != null ? pitch : "");
        binding.editMvpLink.setText(mvp != null ? mvp : "");
    }

    private void trySubmit() {
        if (projects.isEmpty()) {
            Toast.makeText(this, R.string.challenge_answer_error_no_projects, Toast.LENGTH_SHORT).show();
            return;
        }
        String motivation = binding.editMotivation.getText() != null
                ? binding.editMotivation.getText().toString().trim()
                : "";
        if (motivation.isEmpty()) {
            Toast.makeText(this, R.string.challenge_answer_error_motivation, Toast.LENGTH_SHORT).show();
            return;
        }
        String pitch = binding.editPitchLink.getText() != null
                ? binding.editPitchLink.getText().toString().trim()
                : "";
        if (!pitch.isEmpty()) {
            String lower = pitch.toLowerCase(Locale.ROOT);
            if (!lower.contains("drive.google.com")) {
                Toast.makeText(this, R.string.challenge_answer_error_pitch_drive, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        int pos = binding.spinnerProject.getSelectedItemPosition();
        if (pos < 0 || pos >= projects.size()) {
            Toast.makeText(this, R.string.challenge_answer_error_no_projects, Toast.LENGTH_SHORT).show();
            return;
        }
        ProjectEntity project = projects.get(pos);
        String mvp = binding.editMvpLink.getText() != null
                ? binding.editMvpLink.getText().toString().trim()
                : "";
        ChallengeSubmissionEntity row = new ChallengeSubmissionEntity(
                challengeId,
                project.getId(),
                challengeDetail.getTitle(),
                project.getName(),
                challengeDetail.getInvestorProfile().name,
                challengeDetail.getInvestorProfile().role,
                challengeDetail.getInvestorProfile().avatarResId,
                motivation,
                pitch.isEmpty() ? null : pitch,
                mvp.isEmpty() ? null : mvp,
                ChallengeSubmissionEntity.STATUS_PENDING_INVESTOR,
                System.currentTimeMillis());
        AppDatabase.get(this).challengeSubmissionDao().insert(row);
        Toast.makeText(this, R.string.challenge_answer_success, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
