package kz.diplomka.startupmatch.ui.home.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

import kz.diplomka.startupmatch.data.local.AppDatabase;
import kz.diplomka.startupmatch.data.local.entity.ProjectEntity;
import kz.diplomka.startupmatch.ui.home.module.AddProjectFormData;

public class AddProjectRepository {

    private final Context appContext;

    public AddProjectRepository(@NonNull Context context) {
        this.appContext = context.getApplicationContext();
    }

    /**
     * Persists a new project row and returns its id.
     */
    public long insertProject(@NonNull AddProjectFormData data) {
        long now = System.currentTimeMillis();
        ProjectEntity entity = new ProjectEntity(
                data.getProjectName().trim(),
                data.getIndustry().trim(),
                data.getTargetAudience().trim(),
                data.getMarket().trim(),
                data.getShortDescription().trim(),
                data.getFullDescription().trim(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                now,
                now
        );
        return AppDatabase.get(appContext).projectDao().insert(entity);
    }

    public List<String> getIndustries() {
        return Arrays.asList(
                "FinTech (Қаржы технологиялары)",
                "EdTech (Білім технологиялары)",
                "HealthTech (Денсаулық технологиялары)",
                "E-commerce (Электронды коммерция)",
                "AgriTech (Ауыл шаруашылығы технологиялары)",
                "Logistics (Логистика)",
                "Real Estate (Тұрғын үй және жылжымайтын мүлік)",
                "Gaming (Ойын индустриясы)",
                "Cybersecurity (Киберқауіпсіздік)",
                "SaaS (Бағдарламалық қызмет)"
        );
    }

    public List<String> getMarketOptions() {
        return Arrays.asList("Локал", "Глобал");
    }

    public boolean isFormValid(AddProjectFormData data) {
        return isFilled(data.getProjectName())
                && isFilled(data.getIndustry())
                && isFilled(data.getTargetAudience())
                && isFilled(data.getMarket())
                && isFilled(data.getShortDescription())
                && isFilled(data.getFullDescription());
    }

    private boolean isFilled(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
