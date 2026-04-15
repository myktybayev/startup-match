package kz.diplomka.startupmatch.ui.investor_role.model;

import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kz.diplomka.startupmatch.data.local.entity.TeamMemberEntity;
import kz.diplomka.startupmatch.ui.investor_role.data.InvestorDemoProjectIds;

public final class TeamMemberRowUi {

    @NonNull
    public final String name;
    @NonNull
    public final String role;
    @NonNull
    public final String bio;
    @NonNull
    public final String chipOne;
    @NonNull
    public final String chipTwo;
    @NonNull
    public final String linkLine;
    @Nullable
    public final String avatarUri;

    public TeamMemberRowUi(
            @NonNull String name,
            @NonNull String role,
            @NonNull String bio,
            @NonNull String chipOne,
            @NonNull String chipTwo,
            @NonNull String linkLine,
            @Nullable String avatarUri
    ) {
        this.name = name;
        this.role = role;
        this.bio = bio;
        this.chipOne = chipOne;
        this.chipTwo = chipTwo;
        this.linkLine = linkLine;
        this.avatarUri = avatarUri;
    }

    /**
     * Инвестор демо карточкалары үшін жоба id-сына байланысты әртүрлі команда (базада жазба жоқ).
     * Белгісіз теріс id үшін id-ден детерминирленген вариант таңдалады.
     */
    @NonNull
    public static List<TeamMemberRowUi> placeholderTeamForDemoProject(long projectId) {
        long id = projectId;
        if (id < 0L && !InvestorDemoProjectIds.isKnownDemo(id)) {
            int v = (int) (Math.abs(id) % 4);
            if (v == 0) {
                id = InvestorDemoProjectIds.SMARTPAY_KZ;
            } else if (v == 1) {
                id = InvestorDemoProjectIds.EDU_AI;
            } else if (v == 2) {
                id = InvestorDemoProjectIds.MEDLINK_AI;
            } else {
                id = InvestorDemoProjectIds.NOMAD_LEDGER;
            }
        }
        if (id == InvestorDemoProjectIds.SMARTPAY_KZ) {
            return Collections.unmodifiableList(demoTeamSmartPay());
        }
        if (id == InvestorDemoProjectIds.EDU_AI) {
            return Collections.unmodifiableList(demoTeamEduAi());
        }
        if (id == InvestorDemoProjectIds.MEDLINK_AI) {
            return Collections.unmodifiableList(demoTeamMedLink());
        }
        if (id == InvestorDemoProjectIds.NOMAD_LEDGER) {
            return Collections.unmodifiableList(demoTeamNomadLedger());
        }
        return Collections.emptyList();
    }

    @NonNull
    private static List<TeamMemberRowUi> demoTeamSmartPay() {
        List<TeamMemberRowUi> list = new ArrayList<>();
        list.add(new TeamMemberRowUi(
                "Ерлан Б.",
                "CEO",
                "Төлем инфрақұрылымы және банк серіктестіктері. MRR өсімін басқарады.",
                "FinTech",
                "B2B SaaS",
                "LinkedIn: linkedin.com/in/erlan-smartpay-demo",
                null
        ));
        list.add(new TeamMemberRowUi(
                "Мәдина А.",
                "CTO",
                "PCI-DSS, карта токенизациясы, API қауіпсіздігі. Микросервистік архитектура.",
                "Security",
                "Kotlin",
                "LinkedIn: linkedin.com/in/madina-smartpay-demo",
                null
        ));
        list.add(new TeamMemberRowUi(
                "Данияр С.",
                "CFO",
                "Юнит-экономика, инвесторлық есептер, қаржылық модельдер.",
                "Finance",
                "Fundraising",
                "LinkedIn: linkedin.com/in/daniyar-smartpay-demo",
                null
        ));
        list.add(new TeamMemberRowUi(
                "Айжан Т.",
                "Head of Compliance",
                "Реттеу және KYC/AML процестері, локалды нормалар.",
                "Compliance",
                "KYC",
                "LinkedIn: linkedin.com/in/aizhan-smartpay-demo",
                null
        ));
        return list;
    }

    @NonNull
    private static List<TeamMemberRowUi> demoTeamEduAi() {
        List<TeamMemberRowUi> list = new ArrayList<>();
        list.add(new TeamMemberRowUi(
                "Сәуле Н.",
                "CEO",
                "EdTech өнім стратегиясы, мектептермен пилоттар, контент саясаты.",
                "EdTech",
                "B2C",
                "LinkedIn: linkedin.com/in/saule-eduai-demo",
                null
        ));
        list.add(new TeamMemberRowUi(
                "Бекзат О.",
                "Chief Learning Scientist",
                "NLP, персонализация, бағалау модельдері. PhD Computer Science.",
                "ML",
                "NLP",
                "Portfolio: github.com/bekzat-eduai-demo",
                null
        ));
        list.add(new TeamMemberRowUi(
                "Гүлнар К.",
                "Head of Curriculum",
                "Мектеп бағдарламаларымен интеграция, педагогикалық дизайн.",
                "Curriculum",
                "K12",
                "LinkedIn: linkedin.com/in/gulnar-eduai-demo",
                null
        ));
        list.add(new TeamMemberRowUi(
                "Тимур Р.",
                "Lead Mobile",
                "iOS/Android, офлайн режим, синхронизация.",
                "Mobile",
                "Swift",
                "LinkedIn: linkedin.com/in/timur-eduai-demo",
                null
        ));
        return list;
    }

    @NonNull
    private static List<TeamMemberRowUi> demoTeamMedLink() {
        List<TeamMemberRowUi> list = new ArrayList<>();
        list.add(new TeamMemberRowUi(
                "Ляззат Е.",
                "CEO",
                "Клиникалық сату, пилоттар, медициналық серіктестер желісі.",
                "Health",
                "Hospitals",
                "LinkedIn: linkedin.com/in/lyazzat-medlink-demo",
                null
        ));
        list.add(new TeamMemberRowUi(
                "Дәрігер Қуандық М.",
                "Chief Medical Advisor",
                "Клиникалық хаттамалар, triage логикасы, дәрігерлер кеңесі.",
                "Clinical",
                "Triage",
                "LinkedIn: linkedin.com/in/kuandyq-medlink-demo",
                null
        ));
        list.add(new TeamMemberRowUi(
                "Асылбек Ж.",
                "CTO",
                "HL7/FHIR интеграциясы, деректер қорғауы, on-prem шешімдер.",
                "Interop",
                "FHIR",
                "LinkedIn: linkedin.com/in/asylbek-medlink-demo",
                null
        ));
        list.add(new TeamMemberRowUi(
                "Камила Ш.",
                "Regulatory Lead",
                "Медициналық бағдарламалық өнімді тіркеу, құжаттама.",
                "Regulatory",
                "MDR",
                "LinkedIn: linkedin.com/in/kamila-medlink-demo",
                null
        ));
        return list;
    }

    @NonNull
    private static List<TeamMemberRowUi> demoTeamNomadLedger() {
        List<TeamMemberRowUi> list = new ArrayList<>();
        list.add(new TeamMemberRowUi(
                "Руслан В.",
                "CEO",
                "B2B SaaS сату, үлкен шоттар, халықаралық кеңею.",
                "Web",
                "B2B",
                "LinkedIn: linkedin.com/in/ruslan-nomad-demo",
                null
        ));
        list.add(new TeamMemberRowUi(
                "Жанар Д.",
                "CPO",
                "Өнім жол картасы, onboarding, activation метрикалары.",
                "Product",
                "Analytics",
                "LinkedIn: linkedin.com/in/zhanar-nomad-demo",
                null
        ));
        list.add(new TeamMemberRowUi(
                "Ерболат П.",
                "Lead Backend",
                "Жоғары жүктеме, есеп айырысу дұрыстығы, event-driven архитектура.",
                "Backend",
                "PostgreSQL",
                "Portfolio: github.com/yerbolat-nomad-demo",
                null
        ));
        list.add(new TeamMemberRowUi(
                "Айгерім Л.",
                "Head of Customer Success",
                "B2B қолдау, SLA, churn алдын алу.",
                "CS",
                "Onboarding",
                "LinkedIn: linkedin.com/in/aigerim-nomad-demo",
                null
        ));
        return list;
    }

    @NonNull
    public static TeamMemberRowUi fromEntity(@NonNull TeamMemberEntity e) {
        List<String> chips = extractChips(e.getExperience(), e.getRole());
        String chip1 = chips.size() > 0 ? clipChip(chips.get(0)) : clipChip(e.getRole());
        String chip2 = chips.size() > 1 ? clipChip(chips.get(1)) : "";
        if (chip2.isEmpty() || chip2.equals(chip1)) {
            if (chips.size() > 2) {
                chip2 = clipChip(chips.get(2));
            }
        }
        if (chip2.isEmpty() || chip2.equals(chip1)) {
            chip2 = secondChipFromRole(e.getRole(), chip1);
        }
        return new TeamMemberRowUi(
                e.getFullName(),
                e.getRole(),
                e.getExperience(),
                chip1,
                chip2,
                formatLinkLine(e.getPortfolio()),
                e.getAvatarUri()
        );
    }

    @NonNull
    private static String secondChipFromRole(@NonNull String role, @NonNull String chip1) {
        String[] parts = role.trim().split("[/&]|\\s+");
        for (String p : parts) {
            String t = clipChip(p);
            if (t.length() >= 2 && !t.equals(chip1)) {
                return t;
            }
        }
        return "Startup";
    }

    @NonNull
    private static String clipChip(@NonNull String s) {
        String t = s.trim();
        if (t.length() > 28) {
            return t.substring(0, 25).trim() + "…";
        }
        return t;
    }

    @NonNull
    private static List<String> extractChips(@NonNull String experience, @NonNull String role) {
        List<String> out = new ArrayList<>();
        String[] parts = experience.split("[,;|•\n]");
        for (String p : parts) {
            String t = p.trim();
            if (t.length() >= 3 && out.size() < 2) {
                out.add(t);
            }
        }
        if (out.isEmpty() && !role.trim().isEmpty()) {
            out.add(role.trim());
        }
        return out;
    }

    @NonNull
    private static String formatLinkLine(@NonNull String portfolio) {
        String p = portfolio.trim();
        if (p.isEmpty()) {
            return "";
        }
        String lower = p.toLowerCase();
        if (lower.contains("linkedin")) {
            return "LinkedIn: " + stripScheme(p);
        }
        return "Portfolio: " + stripScheme(p);
    }

    @NonNull
    private static String stripScheme(@NonNull String url) {
        String t = url.trim();
        if (t.startsWith("http://") || t.startsWith("https://")) {
            int idx = t.indexOf("://");
            if (idx >= 0 && idx + 3 < t.length()) {
                return t.substring(idx + 3);
            }
        }
        return t;
    }

    public boolean hasClickableLink() {
        if (TextUtils.isEmpty(linkLine)) {
            return false;
        }
        return linkLine.contains("linkedin.com")
                || linkLine.contains("http")
                || linkLine.contains(".")
                || linkLine.contains("/");
    }

    @Nullable
    public Uri linkUri() {
        if (TextUtils.isEmpty(linkLine)) {
            return null;
        }
        String raw = linkLine;
        if (raw.startsWith("LinkedIn: ")) {
            raw = raw.substring("LinkedIn: ".length()).trim();
        } else if (raw.startsWith("Portfolio: ")) {
            raw = raw.substring("Portfolio: ".length()).trim();
        }
        if (raw.isEmpty()) {
            return null;
        }
        if (!raw.startsWith("http://") && !raw.startsWith("https://")) {
            raw = "https://" + raw;
        }
        try {
            return Uri.parse(raw);
        } catch (Exception e) {
            return null;
        }
    }
}
