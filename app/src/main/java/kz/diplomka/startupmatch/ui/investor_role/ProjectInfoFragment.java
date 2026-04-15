package kz.diplomka.startupmatch.ui.investor_role;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.data.local.AppDatabase;
import kz.diplomka.startupmatch.data.local.entity.ProjectEntity;
import kz.diplomka.startupmatch.databinding.FragmentProjectInfoBinding;

public class ProjectInfoFragment extends Fragment {

    /** AddTractionActivity сақтайтын JSON кілттері — traction_link бағанасында. */
    private static final String J_USERS = "users";
    private static final String J_MRR = "mrr";
    private static final String J_GROWTH = "growth";
    private static final String J_KPI = "kpi";

    @NonNull
    public static ProjectInfoFragment newInstance(@NonNull Bundle args) {
        ProjectInfoFragment fragment = new ProjectInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private FragmentProjectInfoBinding binding;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentProjectInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args == null) {
            return;
        }
        int score = clampScore(args.getInt(ProjectDetailActivity.EXTRA_SCORE, 80));
        String industry = getValue(args.getString(ProjectDetailActivity.EXTRA_PROJECT_INDUSTRY),
                getString(R.string.project_detail_default_industry));
        String product = getValue(args.getString(ProjectDetailActivity.EXTRA_PRODUCT),
                getString(R.string.project_detail_default_product));
        String pitchUrl = args.getString(ProjectDetailActivity.EXTRA_PITCH_URL);
        long projectId = args.getLong(ProjectDetailActivity.EXTRA_PROJECT_ID, -1L);

        if (projectId > 0L) {
            ProjectEntity project = AppDatabase.get(requireContext()).projectDao().getById(projectId);
            if (project != null) {
                bindFromProjectEntity(project, score);
                return;
            }
        }
        bindFromIntentFallback(score, industry, product, pitchUrl);
    }

    private void bindFromProjectEntity(@NonNull ProjectEntity p, int scoreFallback) {
        binding.textIndustryPill.setText(getValue(p.getIndustry(), getString(R.string.project_detail_default_industry)));

        String pitchUrl = p.getPitchDriveLink();
        binding.textPitchSubtitle.setText(
                TextUtils.isEmpty(trimToNull(pitchUrl))
                        ? getString(R.string.project_detail_pitch_subtitle_default)
                        : getString(R.string.project_detail_pitch_subtitle_link));

        binding.textGithubSubtitle.setText(githubSubtitleFor(p));
        binding.textMvpSubtitle.setText(mvpSubtitleFor(p));

        TractionFields traction = parseTractionJson(p.getTractionLink());
        if (traction != null && traction.hasAnyFilled()) {
            binding.textMetricUsersValue.setText(metricCellText(traction.users, MetricKind.USERS));
            binding.textMetricMrrValue.setText(metricCellText(traction.mrr, MetricKind.MRR));
            binding.textMetricMomValue.setText(metricCellText(traction.growth, MetricKind.PLAIN));
            binding.textMetricRetentionValue.setText(metricCellText(traction.kpi, MetricKind.PLAIN));
        } else {
            applyScoreDerivedMetrics(scoreFallback);
        }
    }

    private void bindFromIntentFallback(int score, String industry, String productLine, String pitchUrl) {
        binding.textIndustryPill.setText(getValue(industry, getString(R.string.project_detail_default_industry)));
        binding.textPitchSubtitle.setText(
                (pitchUrl == null || pitchUrl.trim().isEmpty())
                        ? getString(R.string.project_detail_pitch_subtitle_default)
                        : getString(R.string.project_detail_pitch_subtitle_link));
        binding.textGithubSubtitle.setText(getString(R.string.home_step_github_pending_a11y));
        binding.textMvpSubtitle.setText(getValue(productLine, getString(R.string.project_detail_default_product)));
        applyScoreDerivedMetrics(score);
    }

    @NonNull
    private String githubSubtitleFor(@NonNull ProjectEntity p) {
        if (TextUtils.isEmpty(trimToNull(p.getGithubLink()))) {
            return getString(R.string.home_step_github_pending_a11y);
        }
        long t = p.getUpdatedAt() > 0L ? p.getUpdatedAt() : p.getCreatedAt();
        String when = formatResourceTime(t);
        if (TextUtils.isEmpty(when)) {
            return getString(R.string.home_step_github_done_a11y);
        }
        return getString(R.string.incoming_pitch_row_last_update, when);
    }

    @NonNull
    private String mvpSubtitleFor(@NonNull ProjectEntity p) {
        if (TextUtils.isEmpty(trimToNull(p.getMvpLink()))) {
            return getString(R.string.home_step_mvp_pending_a11y);
        }
        Long savedAt = p.getMvpSavedAt();
        long t = (savedAt != null && savedAt > 0L) ? savedAt : (p.getUpdatedAt() > 0L ? p.getUpdatedAt() : p.getCreatedAt());
        String when = formatResourceTime(t);
        if (TextUtils.isEmpty(when)) {
            return getString(R.string.home_step_mvp_done_a11y);
        }
        return getString(R.string.add_mvp_ver_last_change_format, when);
    }

    @NonNull
    private String formatResourceTime(long ms) {
        if (ms <= 0L) {
            return "";
        }
        DateFormat fb = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.getDefault());
        return fb.format(new Date(ms));
    }

    @Nullable
    private static TractionFields parseTractionJson(@Nullable String raw) {
        if (TextUtils.isEmpty(raw)) {
            return null;
        }
        String trimmed = raw.trim();
        if (!trimmed.startsWith("{")) {
            return null;
        }
        try {
            JSONObject o = new JSONObject(trimmed);
            return new TractionFields(
                    o.optString(J_USERS, ""),
                    o.optString(J_MRR, ""),
                    o.optString(J_GROWTH, ""),
                    o.optString(J_KPI, "")
            );
        } catch (JSONException e) {
            return null;
        }
    }

    private enum MetricKind {
        USERS,
        MRR,
        PLAIN
    }

    @NonNull
    private String metricCellText(@NonNull String raw, @NonNull MetricKind kind) {
        String s = raw.trim();
        if (s.isEmpty()) {
            return getString(R.string.add_mvp_ver_date_placeholder);
        }
        Long amount = parseLongLenient(s);
        if (kind == MetricKind.MRR && amount != null) {
            long v = amount;
            if (v >= Integer.MIN_VALUE && v <= Integer.MAX_VALUE) {
                return getString(R.string.project_detail_metric_currency, (int) v);
            }
        }
        if (kind == MetricKind.USERS && amount != null) {
            return String.format(Locale.getDefault(), "%,d", amount);
        }
        return s;
    }

    @Nullable
    private static Long parseLongLenient(@NonNull String s) {
        String digits = s.replace(" ", "").replace("\u00a0", "").replace(",", "").replace("$", "");
        if (digits.isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(digits);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void applyScoreDerivedMetrics(int score) {
        int users = 1000 + (score * 125);
        int mrr = 2000 + (score * 100);
        int mom = Math.max(5, score / 6);
        int retention = Math.min(99, score + 2);
        binding.textMetricUsersValue.setText(String.format(Locale.getDefault(), "%,d", users));
        binding.textMetricMrrValue.setText(getString(R.string.project_detail_metric_currency, mrr));
        binding.textMetricMomValue.setText(getString(R.string.project_detail_metric_percent, mom));
        binding.textMetricRetentionValue.setText(getString(R.string.project_detail_metric_percent, retention));
    }

    private static int clampScore(int score) {
        return Math.max(0, Math.min(100, score));
    }

    @Nullable
    private static String trimToNull(@Nullable String value) {
        if (value == null) {
            return null;
        }
        String t = value.trim();
        return t.isEmpty() ? null : t;
    }

    @NonNull
    private static String getValue(@Nullable String value, @NonNull String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        return value.trim();
    }

    private static final class TractionFields {
        @NonNull final String users;
        @NonNull final String mrr;
        @NonNull final String growth;
        @NonNull final String kpi;

        TractionFields(@NonNull String users, @NonNull String mrr, @NonNull String growth, @NonNull String kpi) {
            this.users = users;
            this.mrr = mrr;
            this.growth = growth;
            this.kpi = kpi;
        }

        boolean hasAnyFilled() {
            return !users.trim().isEmpty()
                    || !mrr.trim().isEmpty()
                    || !growth.trim().isEmpty()
                    || !kpi.trim().isEmpty();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

