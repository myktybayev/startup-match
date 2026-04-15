package kz.diplomka.startupmatch.ui.сhallenges.data;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.data.local.AppDatabase;
import kz.diplomka.startupmatch.data.local.entity.ChallengeSubmissionEntity;
import kz.diplomka.startupmatch.data.local.entity.InvestorChallengeEntity;
import kz.diplomka.startupmatch.ui.investor_role.ChallengeTagKeys;
import kz.diplomka.startupmatch.ui.investors.InvestorListItem;
import kz.diplomka.startupmatch.ui.сhallenges.model.Challenge;
import kz.diplomka.startupmatch.ui.сhallenges.model.ChallengeDetail;
import kz.diplomka.startupmatch.ui.сhallenges.model.ChallengeType;
import kz.diplomka.startupmatch.ui.сhallenges.model.FeaturedBadge;
import kz.diplomka.startupmatch.ui.сhallenges.model.FeaturedChallenge;

/**
 * Тапсырмалар деректері: инвестор жариялаған тапсырмалар ({@code investor_challenges}) + мок карточкалар.
 */
public class ChallengesRepository {

    private final Context appContext;

    public ChallengesRepository(@NonNull Context context) {
        this.appContext = context.getApplicationContext();
    }

    /**
     * Команда жіберген тапсырма жауаптары (DB), жаңа → ескі.
     */
    @NonNull
    public List<ChallengeSubmissionEntity> listMyChallengeSubmissions() {
        return AppDatabase.get(appContext).challengeSubmissionDao().listAllOrdered();
    }

    /**
     * Профильге сәйкес challenge саны (insight жолы).
     */
    public int getProfileMatchCount() {
        return 3;
    }

    @NonNull
    public List<FeaturedChallenge> getFeaturedChallenges() {
        String title = appContext.getString(R.string.challenges_featured_title);
        String body = appContext.getString(R.string.challenges_featured_body);
        String deadline = appContext.getString(R.string.challenges_featured_deadline);
        return Arrays.asList(
                new FeaturedChallenge(9101L, FeaturedBadge.ACTIVE, title, body, deadline),
                new FeaturedChallenge(9102L, FeaturedBadge.SUBMITTED, title, body, deadline)
        );
    }

    /**
     * Инвестор жариялаған тапсырмалар + мок ашық тапсырмалар (мок id: 9001–9005).
     */
    @NonNull
    public List<Challenge> getOpenChallenges() {
        List<Challenge> out = new ArrayList<>();
        for (InvestorChallengeEntity e : AppDatabase.get(appContext).investorChallengeDao().listAllOrdered()) {
            out.add(mapEntityToChallenge(e));
        }
        out.addAll(mockOpenChallenges());
        return out;
    }

    /**
     * Дерекқорға инвестор тапсырмасын сақтау.
     */
    public long insertInvestorChallenge(@NonNull InvestorChallengeEntity entity) {
        return AppDatabase.get(appContext).investorChallengeDao().insert(entity);
    }

    @NonNull
    private List<Challenge> mockOpenChallenges() {
        return Arrays.asList(
                new Challenge(
                        9001L,
                        ChallengeType.SAAS,
                        appContext.getString(R.string.challenges_open_1_title),
                        appContext.getString(R.string.challenges_open_1_prize),
                        appContext.getString(R.string.challenges_open_1_deadline),
                        Arrays.asList(
                                appContext.getString(R.string.challenges_tag_saas),
                                appContext.getString(R.string.challenges_tag_web)
                        )
                ),
                new Challenge(
                        9002L,
                        ChallengeType.AI,
                        appContext.getString(R.string.challenges_open_2_title),
                        appContext.getString(R.string.challenges_open_2_prize),
                        appContext.getString(R.string.challenges_open_2_deadline),
                        Arrays.asList(
                                appContext.getString(R.string.challenges_tag_ai),
                                appContext.getString(R.string.challenges_tag_fintech)
                        )
                ),
                new Challenge(
                        9003L,
                        ChallengeType.MOBILE,
                        appContext.getString(R.string.challenges_open_3_title),
                        appContext.getString(R.string.challenges_open_3_prize),
                        appContext.getString(R.string.challenges_open_3_deadline),
                        Arrays.asList(appContext.getString(R.string.challenges_tag_mobile))
                ),
                new Challenge(
                        9004L,
                        ChallengeType.FINTECH,
                        appContext.getString(R.string.challenges_open_fintech_title),
                        appContext.getString(R.string.challenges_open_fintech_prize),
                        appContext.getString(R.string.challenges_open_fintech_deadline),
                        Arrays.asList(appContext.getString(R.string.challenges_tag_fintech))
                ),
                new Challenge(
                        9005L,
                        ChallengeType.WEB,
                        appContext.getString(R.string.challenges_open_web_title),
                        appContext.getString(R.string.challenges_open_web_prize),
                        appContext.getString(R.string.challenges_open_web_deadline),
                        Arrays.asList(
                                appContext.getString(R.string.challenges_tag_web),
                                appContext.getString(R.string.challenges_tag_saas)
                        )
                )
        );
    }

    @NonNull
    public List<Challenge> filterOpenChallenges(@Nullable ChallengeType type) {
        List<Challenge> all = getOpenChallenges();
        if (type == null) {
            return new ArrayList<>(all);
        }
        List<Challenge> out = new ArrayList<>();
        for (Challenge c : all) {
            if (c.getType() == type) {
                out.add(c);
            }
        }
        return out;
    }

    @Nullable
    private Challenge findOpenById(long id) {
        for (Challenge c : getOpenChallenges()) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    @Nullable
    private FeaturedChallenge findFeaturedById(long id) {
        for (FeaturedChallenge f : getFeaturedChallenges()) {
            if (f.getId() == id) {
                return f;
            }
        }
        return null;
    }

    /**
     * Толық деталь — ашық тапсырма немесе featured id бойынша.
     * Белгісіз id үшін бірінші ашық тапсырма қайтарылады.
     */
    @NonNull
    public ChallengeDetail getChallengeDetail(long id) {
        InvestorChallengeEntity published =
                AppDatabase.get(appContext).investorChallengeDao().getById(id);
        if (published != null) {
            return buildInvestorChallengeDetail(published);
        }
        Challenge open = findOpenById(id);
        if (open != null) {
            return buildOpenDetail(open);
        }
        FeaturedChallenge featured = findFeaturedById(id);
        if (featured != null) {
            return buildFeaturedDetail(featured);
        }
        List<Challenge> all = getOpenChallenges();
        if (!all.isEmpty()) {
            return buildOpenDetail(all.get(0));
        }
        throw new IllegalStateException("No challenge data");
    }

    @NonNull
    private ChallengeDetail buildOpenDetail(@NonNull Challenge open) {
        String categories = TextUtils.join(", ", open.getTags().toArray(new String[0]));
        String deadlineLine = appContext.getString(
                R.string.challenge_detail_deadline_line, open.getDeadline());
        return new ChallengeDetail(
                open.getId(),
                appContext.getString(R.string.challenge_detail_seed_badge),
                appContext.getString(R.string.challenge_detail_active_badge),
                open.getTitle(),
                deadlineLine,
                categories,
                investorProfileForOpenChallengeId(open.getId()),
                bodyForType(open.getType()),
                requirementsForOpenChallenge(open),
                outcomeTitleForOpen(open.getType()),
                outcomeBodyForOpen(open.getType()),
                null
        );
    }

    @NonNull
    private ChallengeDetail buildFeaturedDetail(@NonNull FeaturedChallenge featured) {
        String status = featured.getBadge() == FeaturedBadge.ACTIVE
                ? appContext.getString(R.string.challenge_detail_active_badge)
                : appContext.getString(R.string.challenges_badge_submitted);
        String categories = appContext.getString(R.string.challenges_tag_ai)
                + ", "
                + appContext.getString(R.string.challenges_tag_fintech);
        String deadlineLine = appContext.getString(
                R.string.challenge_detail_deadline_line, featured.getDeadline());
        return new ChallengeDetail(
                featured.getId(),
                appContext.getString(R.string.challenge_detail_seed_badge),
                status,
                featured.getTitle(),
                deadlineLine,
                categories,
                investorProfileForFeaturedId(featured.getId()),
                featured.getDescription(),
                requirementsForFeaturedId(featured.getId()),
                outcomeTitleForFeaturedId(featured.getId()),
                outcomeBodyForFeaturedId(featured.getId()),
                null
        );
    }

    @NonNull
    private ChallengeDetail buildInvestorChallengeDetail(@NonNull InvestorChallengeEntity e) {
        List<String> displayTags = tagsFromTagKeysCsv(e.getTagKeysCsv());
        String categories = TextUtils.join(", ", displayTags.toArray(new String[0]));
        String deadlineLine = appContext.getString(
                R.string.challenge_detail_deadline_line, e.getDeadlineLabel());
        String description = e.getDescription();
        if (!TextUtils.isEmpty(e.getTeamFit())) {
            description = description
                    + "\n\n"
                    + appContext.getString(R.string.add_new_challenge_label_fit)
                    + ": "
                    + e.getTeamFit();
        }
        InvestorListItem investor = investorFromEntity(e);
        List<String> requirements = parseRequirementsJson(e.getRequirementsJson());
        String outcomeTitle;
        String outcomeBody;
        if (InvestorChallengeEntity.REWARD_PILOT.equals(e.getRewardType())) {
            outcomeTitle = appContext.getString(R.string.add_new_challenge_reward_pilot_title).replace('\n', ' ');
            outcomeBody = appContext.getString(R.string.add_new_challenge_reward_pilot_body).replace('\n', ' ');
        } else {
            outcomeTitle = appContext.getString(R.string.add_new_challenge_reward_invest_title);
            outcomeBody = appContext.getString(R.string.add_new_challenge_reward_invest_body).replace('\n', ' ');
        }
        return new ChallengeDetail(
                e.getId(),
                e.getStageBadge(),
                appContext.getString(R.string.challenge_detail_active_badge),
                e.getTitle(),
                deadlineLine,
                categories,
                investor,
                description,
                requirements,
                outcomeTitle,
                outcomeBody,
                e.getInvestorPhotoUri()
        );
    }

    @NonNull
    private Challenge mapEntityToChallenge(@NonNull InvestorChallengeEntity e) {
        ChallengeType type;
        try {
            type = ChallengeType.valueOf(e.getFilterType());
        } catch (Exception ex) {
            type = ChallengeType.SAAS;
        }
        return new Challenge(
                e.getId(),
                type,
                e.getTitle(),
                prizeLabelForRewardType(e.getRewardType()),
                e.getDeadlineLabel(),
                tagsFromTagKeysCsv(e.getTagKeysCsv())
        );
    }

    @NonNull
    private String prizeLabelForRewardType(@NonNull String rewardType) {
        if (InvestorChallengeEntity.REWARD_PILOT.equals(rewardType)) {
            return appContext.getString(R.string.published_challenge_prize_pilot);
        }
        return appContext.getString(R.string.published_challenge_prize_invest);
    }

    @NonNull
    private InvestorListItem investorFromEntity(@NonNull InvestorChallengeEntity e) {
        List<String> industries = tagsFromTagKeysCsv(e.getTagKeysCsv());
        String[] ind = industries.isEmpty() ? new String[]{"—"} : industries.toArray(new String[0]);
        return new InvestorListItem(
                R.drawable.figma_add_challenge_investor_avatar,
                e.getInvestorName(),
                InvestorListItem.BadgeKind.VERIFIED,
                e.getInvestorRole(),
                ind,
                new String[]{"MVP"},
                10,
                100,
                new String[]{"Global"},
                "—",
                "—",
                0,
                0
        );
    }

    @NonNull
    private List<String> tagsFromTagKeysCsv(@Nullable String csv) {
        List<String> labels = new ArrayList<>();
        if (TextUtils.isEmpty(csv)) {
            return labels;
        }
        String[] parts = csv.split(",");
        for (String p : parts) {
            String k = p.trim();
            if (!k.isEmpty()) {
                labels.add(tagKeyToDisplayLabel(k));
            }
        }
        return labels;
    }

    @NonNull
    private String tagKeyToDisplayLabel(@NonNull String key) {
        switch (key) {
            case ChallengeTagKeys.FINTECH:
                return appContext.getString(R.string.add_new_challenge_tag_fintech);
            case ChallengeTagKeys.WEB:
                return appContext.getString(R.string.add_new_challenge_tag_web);
            case ChallengeTagKeys.API:
                return appContext.getString(R.string.add_new_challenge_tag_api);
            case ChallengeTagKeys.B2B:
                return appContext.getString(R.string.add_new_challenge_tag_b2b);
            case ChallengeTagKeys.PAYMENTS:
                return appContext.getString(R.string.add_new_challenge_tag_payments);
            default:
                return key;
        }
    }

    @NonNull
    private List<String> parseRequirementsJson(@Nullable String json) {
        List<String> out = new ArrayList<>();
        if (TextUtils.isEmpty(json)) {
            return out;
        }
        try {
            JSONArray a = new JSONArray(json);
            for (int i = 0; i < a.length(); i++) {
                out.add(a.getString(i));
            }
        } catch (JSONException ignored) {
        }
        return out;
    }

    /**
     * Әр ашық тапсырма әртүрлі инвестордың жариялағаны сияқты көрінуі үшін
     * ({@link kz.diplomka.startupmatch.ui.investors.InvestorsFragment} мысал дерегімен сәйкес).
     */
    @NonNull
    private InvestorListItem investorProfileForOpenChallengeId(long challengeId) {
        if (challengeId == 9001L) {
            return investorAyjanaSerik();
        }
        if (challengeId == 9002L) {
            return investorArunaCapital();
        }
        if (challengeId == 9003L) {
            return investorBekHorizon();
        }
        if (challengeId == 9004L) {
            return investorAskharEsen();
        }
        if (challengeId == 9005L) {
            return investorLunaVentures();
        }
        return investorAyjanaSerik();
    }

    @NonNull
    private InvestorListItem investorProfileForFeaturedId(long featuredId) {
        if (featuredId == 9101L) {
            return investorMeruertSadyk();
        }
        if (featuredId == 9102L) {
            return investorDanaVentures();
        }
        return investorMeruertSadyk();
    }

    @NonNull
    private InvestorListItem investorAyjanaSerik() {
        return new InvestorListItem(
                R.drawable.figma_investors_page2_1,
                "Айдана Серік",
                InvestorListItem.BadgeKind.VERIFIED,
                "Angel Investor",
                new String[]{"FinTech", "B2B SaaS", "Payments"},
                new String[]{"Idea", "MVP", "TicketSize", "FinTech"},
                25,
                120,
                new String[]{"Kazakhstan", "UAE"},
                "$25k – $120k • Kazakhstan, UAE",
                "“Backing payment infrastructure and regional fintech scaling.”",
                148,
                4);
    }

    @NonNull
    private InvestorListItem investorBekHorizon() {
        return new InvestorListItem(
                R.drawable.figma_investors_page2_2,
                "Bek Horizon",
                InvestorListItem.BadgeKind.GUEST,
                "Micro Fund",
                new String[]{"EdTech", "Mobile"},
                new String[]{"Idea", "MVP", "Growth"},
                10,
                35,
                new String[]{"Central Asia"},
                "$10k – $35k • Central Asia",
                "“Interested in practical mobile products with early engagement.”",
                76,
                1);
    }

    @NonNull
    private InvestorListItem investorArunaCapital() {
        return new InvestorListItem(
                R.drawable.figma_investors_page2_3,
                "Aruna Capital",
                InvestorListItem.BadgeKind.EXPERIENCED,
                "Seed Fund",
                new String[]{"AI", "SaaS", "Analytics"},
                new String[]{"MVP", "Growth", "AI", "TicketSize"},
                150,
                800,
                new String[]{"Global"},
                "$150k – $800k • Global",
                "“Looks for strong data moats and repeatable growth.”",
                312,
                6);
    }

    @NonNull
    private InvestorListItem investorAskharEsen() {
        return new InvestorListItem(
                R.drawable.figma_investor_avatar_1,
                "Асқар Есен",
                InvestorListItem.BadgeKind.VERIFIED,
                "Angel Investor",
                new String[]{"AI", "FinTech", "SaaS"},
                new String[]{"Idea", "MVP", "TicketSize"},
                10,
                100,
                new String[]{"Global"},
                "$10k – $100k • Global",
                "“Ex-founder, invests in MVP-stage startups”",
                120,
                3);
    }

    @NonNull
    private InvestorListItem investorLunaVentures() {
        return new InvestorListItem(
                R.drawable.figma_investors_page2_5,
                "Luna Ventures",
                InvestorListItem.BadgeKind.EXPERIENCED,
                "Venture Partner",
                new String[]{"Climate", "Energy", "DeepTech"},
                new String[]{"Growth", "TicketSize", "MVP"},
                200,
                1500,
                new String[]{"Europe", "MENA"},
                "$200k – $1.5M • Europe, MENA",
                "“Focused on climate infrastructure and deeptech scale-ups.”",
                226,
                3);
    }

    @NonNull
    private InvestorListItem investorMeruertSadyk() {
        return new InvestorListItem(
                R.drawable.figma_investor_avatar_2,
                "Меруерт Садық",
                InvestorListItem.BadgeKind.GUEST,
                "Venture Fund Partner",
                new String[]{"EdTech", "HealthTech"},
                new String[]{"Idea", "Growth"},
                100,
                500,
                new String[]{"Central Asia"},
                "$100k – $500k • Central Asia",
                "“Looking for scalable impact-driven startups”",
                85,
                1);
    }

    @NonNull
    private InvestorListItem investorDanaVentures() {
        return new InvestorListItem(
                R.drawable.figma_investor_avatar_3,
                "Dana Ventures",
                InvestorListItem.BadgeKind.VERIFIED,
                "Angel Syndicate",
                new String[]{"Climate", "Energy"},
                new String[]{"Idea", "MVP", "TicketSize"},
                25,
                75,
                new String[]{"Kazakhstan", "UAE"},
                "$25k – $75k • Kazakhstan, UAE",
                "“Passionate about green energy and sustainability”",
                92,
                2);
    }

    @NonNull
    private List<String> requirementsForOpenChallenge(@NonNull Challenge open) {
        switch (open.getType()) {
            case SAAS:
                return Arrays.asList(
                        appContext.getString(R.string.challenge_detail_req_saas_1),
                        appContext.getString(R.string.challenge_detail_req_saas_2),
                        appContext.getString(R.string.challenge_detail_req_saas_3),
                        appContext.getString(R.string.challenge_detail_req_saas_4));
            case AI:
                return Arrays.asList(
                        appContext.getString(R.string.challenge_detail_req_ai_1),
                        appContext.getString(R.string.challenge_detail_req_ai_2),
                        appContext.getString(R.string.challenge_detail_req_ai_3),
                        appContext.getString(R.string.challenge_detail_req_ai_4));
            case MOBILE:
                return Arrays.asList(
                        appContext.getString(R.string.challenge_detail_req_mobile_1),
                        appContext.getString(R.string.challenge_detail_req_mobile_2),
                        appContext.getString(R.string.challenge_detail_req_mobile_3),
                        appContext.getString(R.string.challenge_detail_req_mobile_4));
            case FINTECH:
                return Arrays.asList(
                        appContext.getString(R.string.challenge_detail_req_fintech_1),
                        appContext.getString(R.string.challenge_detail_req_fintech_2),
                        appContext.getString(R.string.challenge_detail_req_fintech_3),
                        appContext.getString(R.string.challenge_detail_req_fintech_4));
            case WEB:
                return Arrays.asList(
                        appContext.getString(R.string.challenge_detail_req_web_1),
                        appContext.getString(R.string.challenge_detail_req_web_2),
                        appContext.getString(R.string.challenge_detail_req_web_3),
                        appContext.getString(R.string.challenge_detail_req_web_4));
            default:
                return Arrays.asList(
                        appContext.getString(R.string.challenge_detail_req_fintech_1),
                        appContext.getString(R.string.challenge_detail_req_fintech_2),
                        appContext.getString(R.string.challenge_detail_req_fintech_3),
                        appContext.getString(R.string.challenge_detail_req_fintech_4));
        }
    }

    @NonNull
    private String outcomeTitleForOpen(@NonNull ChallengeType type) {
        switch (type) {
            case SAAS:
                return appContext.getString(R.string.challenge_detail_outcome_title_saas);
            case AI:
                return appContext.getString(R.string.challenge_detail_outcome_title_ai);
            case MOBILE:
                return appContext.getString(R.string.challenge_detail_outcome_title_mobile);
            case FINTECH:
                return appContext.getString(R.string.challenge_detail_outcome_title_fintech);
            case WEB:
                return appContext.getString(R.string.challenge_detail_outcome_title_web);
            default:
                return appContext.getString(R.string.challenge_detail_outcome_title_fintech);
        }
    }

    @NonNull
    private String outcomeBodyForOpen(@NonNull ChallengeType type) {
        switch (type) {
            case SAAS:
                return appContext.getString(R.string.challenge_detail_outcome_body_saas);
            case AI:
                return appContext.getString(R.string.challenge_detail_outcome_body_ai);
            case MOBILE:
                return appContext.getString(R.string.challenge_detail_outcome_body_mobile);
            case FINTECH:
                return appContext.getString(R.string.challenge_detail_outcome_body_fintech);
            case WEB:
                return appContext.getString(R.string.challenge_detail_outcome_body_web);
            default:
                return appContext.getString(R.string.challenge_detail_outcome_body_fintech);
        }
    }

    @NonNull
    private List<String> requirementsForFeaturedId(long featuredId) {
        if (featuredId == 9101L) {
            return Arrays.asList(
                    appContext.getString(R.string.challenge_detail_req_featured_active_1),
                    appContext.getString(R.string.challenge_detail_req_featured_active_2),
                    appContext.getString(R.string.challenge_detail_req_featured_active_3),
                    appContext.getString(R.string.challenge_detail_req_featured_active_4));
        }
        if (featuredId == 9102L) {
            return Arrays.asList(
                    appContext.getString(R.string.challenge_detail_req_featured_submitted_1),
                    appContext.getString(R.string.challenge_detail_req_featured_submitted_2),
                    appContext.getString(R.string.challenge_detail_req_featured_submitted_3),
                    appContext.getString(R.string.challenge_detail_req_featured_submitted_4));
        }
        return requirementsForFeaturedId(9101L);
    }

    @NonNull
    private String outcomeTitleForFeaturedId(long featuredId) {
        if (featuredId == 9102L) {
            return appContext.getString(R.string.challenge_detail_outcome_title_featured_submitted);
        }
        return appContext.getString(R.string.challenge_detail_outcome_title_featured_active);
    }

    @NonNull
    private String outcomeBodyForFeaturedId(long featuredId) {
        if (featuredId == 9102L) {
            return appContext.getString(R.string.challenge_detail_outcome_body_featured_submitted);
        }
        return appContext.getString(R.string.challenge_detail_outcome_body_featured_active);
    }

    @NonNull
    private String bodyForType(@NonNull ChallengeType type) {
        switch (type) {
            case SAAS:
                return appContext.getString(R.string.challenge_detail_body_saas);
            case AI:
                return appContext.getString(R.string.challenge_detail_body_ai);
            case MOBILE:
                return appContext.getString(R.string.challenge_detail_body_mobile);
            case FINTECH:
                return appContext.getString(R.string.challenge_detail_body_fintech);
            case WEB:
                return appContext.getString(R.string.challenge_detail_body_web);
            default:
                return appContext.getString(R.string.challenge_detail_body_fintech);
        }
    }
}
