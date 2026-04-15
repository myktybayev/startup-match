package kz.diplomka.startupmatch.ui.investor_role.data;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import kz.diplomka.startupmatch.data.local.AppDatabase;
import kz.diplomka.startupmatch.data.local.entity.ProjectEntity;
import kz.diplomka.startupmatch.ui.investor_role.model.InvestorProjectSuggestionUi;

import static kz.diplomka.startupmatch.ui.investor_role.data.InvestorDemoProjectIds.EDU_AI;
import static kz.diplomka.startupmatch.ui.investor_role.data.InvestorDemoProjectIds.MEDLINK_AI;
import static kz.diplomka.startupmatch.ui.investor_role.data.InvestorDemoProjectIds.NOMAD_LEDGER;
import static kz.diplomka.startupmatch.ui.investor_role.data.InvestorDemoProjectIds.SMARTPAY_KZ;

public final class InvestorHomeRepository {

    @NonNull
    private final Context appContext;

    public InvestorHomeRepository(@NonNull Context context) {
        this.appContext = context.getApplicationContext();
    }

    @NonNull
    public List<InvestorProjectSuggestionUi> getStartupProjectSuggestions() {
        List<ProjectEntity> projects = AppDatabase.get(appContext).projectDao().getAll();
        List<InvestorProjectSuggestionUi> out = new ArrayList<>();
        for (ProjectEntity p : projects) {
            int score = estimateScore(p);
            String traction = buildTractionLine(p);
            String product = buildProductLine(p);
            out.add(new InvestorProjectSuggestionUi(
                    p.getId(),
                    p.getName(),
                    p.getIndustry(),
                    score,
                    traction,
                    product,
                    p.getPitchDriveLink()
            ));
        }
        // Талап: базадағы проекттер алдымен, демо блок соңында бірге көрсетілсін.
        out.addAll(demoSuggestions());
        return out;
    }

    public static int estimateScoreForProject(@NonNull ProjectEntity p) {
        return estimateScore(p);
    }

    @NonNull
    public static String tractionLineForProject(@NonNull ProjectEntity p) {
        return buildTractionLine(p);
    }

    @NonNull
    public static String productLineForProject(@NonNull ProjectEntity p) {
        return buildProductLine(p);
    }

    private static int estimateScore(@NonNull ProjectEntity p) {
        int score = 60;
        if (!TextUtils.isEmpty(p.getPitchDriveLink())) score += 10;
        if (!TextUtils.isEmpty(p.getMvpLink())) score += 12;
        if (!TextUtils.isEmpty(p.getGithubLink())) score += 8;
        if (!TextUtils.isEmpty(p.getTractionLink())) score += 10;
        if (p.getFullDescription().length() > 120) score += 5;
        return Math.min(score, 98);
    }

    @NonNull
    private static String buildTractionLine(@NonNull ProjectEntity p) {
        if (!TextUtils.isEmpty(p.getTractionLink())) {
            return "Тартымдылық: сілтеме бар";
        }
        return "Тартымдылық: әлі жүктелмеген";
    }

    @NonNull
    private static String buildProductLine(@NonNull ProjectEntity p) {
        if (!TextUtils.isEmpty(p.getMvpLink())) {
            return "Өнім: MVP дайын";
        }
        return "Өнім: Idea/Prototype";
    }

    /**
     * InvestorHome экранында recycler_suggestions үшін демо 4 жоба.
     * Базада проект болмаған кезде fallback ретінде көрсетіледі.
     */
    @NonNull
    private static List<InvestorProjectSuggestionUi> demoSuggestions() {
        List<InvestorProjectSuggestionUi> demo = new ArrayList<>();
        demo.add(new InvestorProjectSuggestionUi(
                SMARTPAY_KZ,
                "SmartPay KZ",
                "FinTech",
                92,
                "Тартымдылық: $12k MRR",
                "Өнім: MVP дайын",
                "https://drive.google.com/"
        ));
        demo.add(new InvestorProjectSuggestionUi(
                EDU_AI,
                "EduAI",
                "EdTech",
                85,
                "Пайдаланушылар: 10k+",
                "Өнім: Beta-нұсқа",
                "https://drive.google.com/"
        ));
        demo.add(new InvestorProjectSuggestionUi(
                MEDLINK_AI,
                "MedLink AI",
                "Health",
                88,
                "Клиникалармен 6 пилот іске қосылған",
                "AI triage MVP тестте",
                "https://drive.google.com/"
        ));
        demo.add(new InvestorProjectSuggestionUi(
                NOMAD_LEDGER,
                "Nomad Ledger",
                "Web",
                81,
                "Тартымдылық: алғашқы 40 B2B клиент",
                "Өнім: Production-ready",
                "https://drive.google.com/"
        ));
        return demo;
    }
}
