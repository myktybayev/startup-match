package kz.diplomka.startupmatch.ui.investor_role.data;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.data.local.AppDatabase;
import kz.diplomka.startupmatch.data.local.entity.ChallengeSubmissionEntity;
import kz.diplomka.startupmatch.data.local.entity.InvestorPitchEntity;
import kz.diplomka.startupmatch.data.local.entity.ProjectEntity;
import kz.diplomka.startupmatch.ui.investor_role.model.IncomingPitchCardUi;

public final class LinkedProjectsRepository {

    @NonNull
    private final Context appContext;

    public LinkedProjectsRepository(@NonNull Context context) {
        this.appContext = context.getApplicationContext();
    }

    @NonNull
    public List<IncomingPitchCardUi> loadIncomingPitchCards() {
        AppDatabase db = AppDatabase.get(appContext);
        List<InvestorPitchEntity> pitches = AppDatabase.get(appContext)
                .investorPitchDao()
                .listAllOrderByCreatedDesc();
        List<ChallengeSubmissionEntity> submissions = db
                .challengeSubmissionDao()
                .listAllOrdered();

        List<TimedIncomingCard> merged = new ArrayList<>();
        for (InvestorPitchEntity pitch : pitches) {
            ProjectEntity project = db.projectDao().getById(pitch.getProjectId());
            if (project == null) {
                continue;
            }
            merged.add(new TimedIncomingCard(
                    mapPitchToCard(pitch, project),
                    pitch.getCreatedAt()
            ));
        }

        for (ChallengeSubmissionEntity submission : submissions) {
            ProjectEntity project = db.projectDao().getById(submission.getProjectId());
            merged.add(new TimedIncomingCard(
                    mapChallengeSubmissionToCard(submission, project),
                    submission.getSubmittedAt()
            ));
        }

        if (merged.isEmpty()) {
            return demoCards();
        }
        Collections.sort(merged, (a, b) -> Long.compare(b.timestampMs, a.timestampMs));

        List<IncomingPitchCardUi> out = new ArrayList<>();
        for (TimedIncomingCard row : merged) {
            out.add(row.card);
        }
        return out;
    }

    @NonNull
    private IncomingPitchCardUi mapPitchToCard(
            @NonNull InvestorPitchEntity pitch,
            @NonNull ProjectEntity project
    ) {
        boolean amber = isEdTechStyle(project.getIndustry());
        String pitchLink = project.getPitchDriveLink();
        String github = project.getGithubLink();
        String mvp = project.getMvpLink();
        String tractionLink = project.getTractionLink();
        return new IncomingPitchCardUi(
                pitch.getId(),
                project.getId(),
                project.getName(),
                formatPitchReceivedTime(pitch.getCreatedAt()),
                pitch.getTeamWhyUs(),
                pitch.getValidationMarket(),
                amber,
                pitchSubtitle(pitchLink),
                githubSubtitle(project.getUpdatedAt()),
                mvpSubtitle(project.getMvpSavedAt(), project.getUpdatedAt()),
                tractionSubtitle(pitch),
                emptyToNull(pitchLink),
                emptyToNull(github),
                emptyToNull(mvp),
                emptyToNull(tractionLink),
                emptyToNull(project.getContactPhone()),
                emptyToNull(pitch.getTractionUsers()),
                emptyToNull(pitch.getTractionMrr()),
                emptyToNull(pitch.getTractionGrowth())
        );
    }

    @NonNull
    private IncomingPitchCardUi mapChallengeSubmissionToCard(
            @NonNull ChallengeSubmissionEntity submission,
            @Nullable ProjectEntity project
    ) {
        String pitchLink = submission.getPitchLink();
        String mvpLink = submission.getMvpLink();
        String githubLink = project != null ? project.getGithubLink() : null;
        String tractionLink = project != null ? project.getTractionLink() : null;
        long updatedAt = project != null ? project.getUpdatedAt() : 0L;
        Long mvpSavedAt = project != null ? project.getMvpSavedAt() : null;
        return new IncomingPitchCardUi(
                0L,
                submission.getProjectId(),
                submission.getProjectName(),
                formatPitchReceivedTime(submission.getSubmittedAt()),
                submission.getMotivation(),
                appContext.getString(
                        R.string.incoming_challenge_submission_validation_format,
                        submission.getChallengeTitle()
                ),
                false,
                pitchSubtitle(pitchLink),
                githubSubtitle(updatedAt),
                mvpSubtitle(mvpSavedAt, updatedAt),
                appContext.getString(R.string.incoming_pitch_traction_placeholder),
                emptyToNull(pitchLink),
                emptyToNull(githubLink),
                emptyToNull(mvpLink),
                emptyToNull(tractionLink),
                project != null ? emptyToNull(project.getContactPhone()) : null,
                null,
                null,
                null
        );
    }

    private static boolean isEdTechStyle(@NonNull String industry) {
        String i = industry.toLowerCase(Locale.ROOT);
        return i.contains("edtech") || i.contains("edu") || i.contains("білім");
    }

    @NonNull
    private String pitchSubtitle(@Nullable String pitchLink) {
        if (!TextUtils.isEmpty(pitchLink)) {
            return appContext.getString(R.string.project_detail_pitch_subtitle_link);
        }
        return appContext.getString(R.string.incoming_pitch_row_pitch_default);
    }

    @NonNull
    private String githubSubtitle(long updatedAtMs) {
        if (updatedAtMs <= 0L) {
            return appContext.getString(R.string.incoming_pitch_row_no_update);
        }
        return appContext.getString(
                R.string.incoming_pitch_row_last_update,
                formatResourceTime(updatedAtMs)
        );
    }

    @NonNull
    private String mvpSubtitle(@Nullable Long mvpSavedAt, long projectUpdatedAt) {
        long t = mvpSavedAt != null ? mvpSavedAt : projectUpdatedAt;
        if (t <= 0L) {
            return appContext.getString(R.string.incoming_pitch_row_no_update);
        }
        return appContext.getString(
                R.string.incoming_pitch_row_last_update,
                formatResourceTime(t)
        );
    }

    @NonNull
    private String tractionSubtitle(@NonNull InvestorPitchEntity pitch) {
        String users = pitch.getTractionUsers() != null ? pitch.getTractionUsers().trim() : "";
        String mrr = pitch.getTractionMrr() != null ? pitch.getTractionMrr().trim() : "";
        if (users.isEmpty() && mrr.isEmpty()) {
            return appContext.getString(R.string.incoming_pitch_traction_placeholder);
        }
        return appContext.getString(R.string.incoming_pitch_traction_format, users, mrr);
    }

    @NonNull
    private String formatPitchReceivedTime(long createdAtMs) {
        if (createdAtMs <= 0L) {
            return "";
        }
        Calendar pitchDay = Calendar.getInstance();
        pitchDay.setTimeInMillis(createdAtMs);
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);
        boolean sameYmd = pitchDay.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR)
                && pitchDay.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR);
        SimpleDateFormat timeOnly = new SimpleDateFormat("HH:mm", Locale.getDefault());
        if (sameYmd) {
            return appContext.getString(R.string.incoming_pitch_yesterday_time, timeOnly.format(new Date(createdAtMs)));
        }
        DateFormat df = new SimpleDateFormat("d MMMM, HH:mm", new Locale("kk", "KZ"));
        try {
            return df.format(new Date(createdAtMs));
        } catch (Exception e) {
            DateFormat fb = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.getDefault());
            return fb.format(new Date(createdAtMs));
        }
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
    private static String emptyToNull(@Nullable String s) {
        if (s == null || s.trim().isEmpty()) {
            return null;
        }
        return s.trim();
    }

    @NonNull
    private List<IncomingPitchCardUi> demoCards() {
        List<IncomingPitchCardUi> demo = new ArrayList<>();
        demo.add(new IncomingPitchCardUi(
                -1L,
                -1L,
                appContext.getString(R.string.incoming_demo_smartpay_title),
                appContext.getString(R.string.incoming_demo_smartpay_time),
                appContext.getString(R.string.incoming_demo_smartpay_team),
                appContext.getString(R.string.incoming_demo_smartpay_validation),
                false,
                appContext.getString(R.string.incoming_pitch_row_pitch_default),
                appContext.getString(R.string.incoming_demo_github_sub),
                appContext.getString(R.string.incoming_demo_mvp_sub),
                appContext.getString(R.string.incoming_demo_traction_smartpay),
                "https://drive.google.com/",
                "https://github.com/",
                "https://example.com/mvp",
                null,
                appContext.getString(R.string.incoming_demo_smartpay_phone),
                "15,000",
                "$20,000",
                "18%"
        ));
        demo.add(new IncomingPitchCardUi(
                -2L,
                -2L,
                appContext.getString(R.string.incoming_demo_eduai_title),
                appContext.getString(R.string.incoming_demo_eduai_time),
                appContext.getString(R.string.incoming_demo_eduai_team),
                appContext.getString(R.string.incoming_demo_eduai_validation),
                true,
                appContext.getString(R.string.incoming_pitch_row_pitch_default),
                appContext.getString(R.string.incoming_demo_github_sub2),
                appContext.getString(R.string.incoming_demo_mvp_sub2),
                appContext.getString(R.string.incoming_demo_traction_eduai),
                "https://drive.google.com/",
                "https://github.com/",
                "https://example.com/mvp",
                null,
                appContext.getString(R.string.incoming_demo_eduai_phone),
                "5,000",
                "$2,500",
                "12%"
        ));
        return demo;
    }

    private static final class TimedIncomingCard {
        @NonNull
        final IncomingPitchCardUi card;
        final long timestampMs;

        TimedIncomingCard(@NonNull IncomingPitchCardUi card, long timestampMs) {
            this.card = card;
            this.timestampMs = timestampMs;
        }
    }
}
