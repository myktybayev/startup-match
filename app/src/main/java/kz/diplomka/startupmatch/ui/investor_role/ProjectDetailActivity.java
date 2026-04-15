package kz.diplomka.startupmatch.ui.investor_role;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.databinding.ActivityProjectDetailBinding;

public class ProjectDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PROJECT_ID = "extra.project.id";
    public static final String EXTRA_PROJECT_TITLE = "extra.project.title";
    public static final String EXTRA_PROJECT_INDUSTRY = "extra.project.industry";
    public static final String EXTRA_SCORE = "extra.project.score";
    public static final String EXTRA_TRACTION = "extra.project.traction";
    public static final String EXTRA_PRODUCT = "extra.project.product";
    public static final String EXTRA_PITCH_URL = "extra.project.pitch_url";
    private static final String STATE_SELECTED_TAB = "state.selected.tab";
    private static final int TAB_INFO = 0;
    private static final int TAB_TEAM = 1;

    @NonNull
    public static Intent newIntent(
            @NonNull Context context,
            long projectId,
            @NonNull String title,
            @NonNull String industry,
            int scorePercent,
            @Nullable String tractionLine,
            @Nullable String productLine,
            @Nullable String pitchUrl
    ) {
        Intent i = new Intent(context, ProjectDetailActivity.class);
        i.putExtra(EXTRA_PROJECT_ID, projectId);
        i.putExtra(EXTRA_PROJECT_TITLE, title);
        i.putExtra(EXTRA_PROJECT_INDUSTRY, industry);
        i.putExtra(EXTRA_SCORE, scorePercent);
        i.putExtra(EXTRA_TRACTION, tractionLine);
        i.putExtra(EXTRA_PRODUCT, productLine);
        i.putExtra(EXTRA_PITCH_URL, pitchUrl);
        return i;
    }

    private ActivityProjectDetailBinding binding;
    @NonNull
    private Bundle projectArgs = new Bundle();
    private int selectedTab = TAB_INFO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProjectDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String title = valueOrDefault(
                getIntent().getStringExtra(EXTRA_PROJECT_TITLE),
                getString(R.string.project_detail_default_title)
        );
        String industry = valueOrDefault(
                getIntent().getStringExtra(EXTRA_PROJECT_INDUSTRY),
                getString(R.string.project_detail_default_industry)
        );
        int score = clampScore(getIntent().getIntExtra(EXTRA_SCORE, 80));
        String traction = valueOrDefault(
                getIntent().getStringExtra(EXTRA_TRACTION),
                getString(R.string.project_detail_default_traction)
        );
        String product = valueOrDefault(
                getIntent().getStringExtra(EXTRA_PRODUCT),
                getString(R.string.project_detail_default_product)
        );
        String pitchUrl = getIntent().getStringExtra(EXTRA_PITCH_URL);
        long projectId = getIntent().getLongExtra(EXTRA_PROJECT_ID, -1L);

        binding.buttonBack.setOnClickListener(v -> finish());
        bindHero(title, score);

        projectArgs = buildProjectArgs(projectId, title, industry, score, traction, product, pitchUrl);
        selectedTab = savedInstanceState != null
                ? savedInstanceState.getInt(STATE_SELECTED_TAB, TAB_INFO)
                : TAB_INFO;

        binding.tabInfo.setOnClickListener(v -> selectTab(TAB_INFO));
        binding.tabTeam.setOnClickListener(v -> selectTab(TAB_TEAM));
        selectTab(selectedTab);
    }

    private void bindHero(@NonNull String title, int score) {
        binding.textProjectTitle.setText(title);
        binding.textProjectStatus.setText(getString(R.string.project_detail_status_active));
        binding.textAiScore.setScorePercent(score);
        binding.textAiFactorF.setText(getString(R.string.project_detail_factor_f));
        binding.textAiFactorP.setText(getString(R.string.project_detail_factor_p));
        binding.textAiFactorT.setText(getString(R.string.project_detail_factor_t));
        binding.textAiFactorM.setText(getString(R.string.project_detail_factor_m));
    }

    private void selectTab(int tab) {
        selectedTab = tab;
        updateTabsUi();
        Fragment fragment = (tab == TAB_INFO)
                ? ProjectInfoFragment.newInstance(projectArgs)
                : ProjectCommanFragment.newInstance(projectArgs);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.project_detail_content_container, fragment)
                .commit();
    }

    private void updateTabsUi() {
        boolean infoSelected = selectedTab == TAB_INFO;
        setTabState(binding.tabInfo, infoSelected);
        setTabState(binding.tabTeam, !infoSelected);
    }

    private void setTabState(@NonNull android.widget.TextView tab, boolean selected) {
        if (selected) {
            tab.setBackgroundResource(R.drawable.bg_challenge_filter_selected);
            tab.setTextColor(ContextCompat.getColor(this, R.color.white));
        } else {
            tab.setBackgroundResource(R.drawable.bg_challenge_filter_unselected);
            tab.setTextColor(ContextCompat.getColor(this, R.color.investor_title));
        }
    }

    @NonNull
    private static Bundle buildProjectArgs(
            long projectId,
            @NonNull String title,
            @NonNull String industry,
            int score,
            @NonNull String traction,
            @NonNull String product,
            @Nullable String pitchUrl
    ) {
        Bundle args = new Bundle();
        args.putLong(EXTRA_PROJECT_ID, projectId);
        args.putString(EXTRA_PROJECT_TITLE, title);
        args.putString(EXTRA_PROJECT_INDUSTRY, industry);
        args.putInt(EXTRA_SCORE, score);
        args.putString(EXTRA_TRACTION, traction);
        args.putString(EXTRA_PRODUCT, product);
        args.putString(EXTRA_PITCH_URL, pitchUrl);
        return args;
    }

    @NonNull
    private static String valueOrDefault(@Nullable String value, @NonNull String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        return value.trim();
    }

    private static int clampScore(int score) {
        return Math.max(0, Math.min(100, score));
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_TAB, selectedTab);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}

