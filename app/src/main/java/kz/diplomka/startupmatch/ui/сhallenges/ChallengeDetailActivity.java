package kz.diplomka.startupmatch.ui.сhallenges;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.databinding.ActivityChallengeDetailBinding;
import kz.diplomka.startupmatch.databinding.ItemChallengeRequirementBinding;
import kz.diplomka.startupmatch.ui.investors.InvestorsCabinetActivity;
import kz.diplomka.startupmatch.ui.сhallenges.data.ChallengesRepository;
import kz.diplomka.startupmatch.ui.сhallenges.model.ChallengeDetail;

public class ChallengeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_CHALLENGE_ID = "kz.diplomka.startupmatch.extra.CHALLENGE_ID";
    String act = "";
    @NonNull
    public static Intent newIntent(@NonNull Context context, long challengeId) {
        Intent intent = new Intent(context, ChallengeDetailActivity.class);
        intent.putExtra("act", "startup");
        intent.putExtra(EXTRA_CHALLENGE_ID, challengeId);

        return intent;
    }

    private ActivityChallengeDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChallengeDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        long id = getIntent().getLongExtra(EXTRA_CHALLENGE_ID, -1L);
        act = getIntent().getStringExtra("act");

        if(act.equals("investor")){
            binding.answerButton.setVisibility(View.GONE);
        }

        ChallengesRepository repository = new ChallengesRepository(this);
        ChallengeDetail detail = repository.getChallengeDetail(id);

        binding.buttonBack.setOnClickListener(v -> finish());
        binding.buttonBookmark.setOnClickListener(v ->
                Toast.makeText(this, R.string.challenge_detail_bookmark_added_toast, Toast.LENGTH_SHORT).show());

        bindDetail(detail);
    }

    private void bindDetail(@NonNull ChallengeDetail detail) {
        binding.textStageBadge.setText(detail.getStageBadge());
        binding.textStatusBadge.setText(detail.getStatusBadge());
        if (getString(R.string.challenges_badge_submitted).equals(detail.getStatusBadge())) {
            binding.textStatusBadge.setBackgroundResource(R.drawable.bg_challenge_badge_submitted);
            binding.textStatusBadge.setTextColor(
                    ContextCompat.getColor(this, R.color.challenge_badge_submitted_text));
        } else {
            binding.textStatusBadge.setBackgroundResource(R.drawable.bg_challenge_badge_active);
            binding.textStatusBadge.setTextColor(
                    ContextCompat.getColor(this, R.color.challenge_badge_active_text));
        }

        binding.textChallengeTitle.setText(detail.getTitle());
        binding.textDeadlineLine.setText(detail.getDeadlineLine());
        binding.textCategoriesLine.setText(detail.getCategoriesLine());
        binding.textDescription.setText(detail.getDescription());
        binding.textInvestorName.setText(detail.getInvestorProfile().name);
        binding.textInvestorRole.setText(detail.getInvestorProfile().role);
        if (!TextUtils.isEmpty(detail.getInvestorPhotoUri())) {
            try {
                binding.imageInvestor.setImageURI(Uri.parse(detail.getInvestorPhotoUri()));
            } catch (Exception e) {
                binding.imageInvestor.setImageResource(detail.getInvestorProfile().avatarResId);
            }
        } else {
            binding.imageInvestor.setImageResource(detail.getInvestorProfile().avatarResId);
        }
        binding.textOutcomeTitle.setText(detail.getOutcomeTitle());
        binding.textOutcomeDescription.setText(detail.getOutcomeDescription());

        binding.containerRequirements.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (String line : detail.getRequirements()) {
            ItemChallengeRequirementBinding row = ItemChallengeRequirementBinding.inflate(
                    inflater, binding.containerRequirements, false);
            row.textRequirement.setText(line);
            binding.containerRequirements.addView(row.getRoot());
        }

        binding.cardInvestor.setOnClickListener(v ->
                startActivity(InvestorsCabinetActivity.newIntent(
                        ChallengeDetailActivity.this,
                        detail.getInvestorProfile(),
                        null,
                        -1L)));

        binding.buttonAnswer.setOnClickListener(v ->
                startActivity(ChallengeAnswerActivity.newIntent(ChallengeDetailActivity.this, detail.getId())));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
