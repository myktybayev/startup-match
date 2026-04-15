package kz.diplomka.startupmatch.ui.investors.data;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.List;

import kz.diplomka.startupmatch.data.local.AppDatabase;
import kz.diplomka.startupmatch.data.local.entity.InvestorPitchEntity;

/**
 * Инвесторға жіберілген pitch жазбаларын локальды сақтау.
 */
public final class InvestorPitchRepository {

    private final AppDatabase db;

    public InvestorPitchRepository(@NonNull Context context) {
        db = AppDatabase.get(context.getApplicationContext());
    }

    public long savePitch(@NonNull InvestorPitchEntity entity) {
        return db.investorPitchDao().insert(entity);
    }

    @NonNull
    public List<InvestorPitchEntity> listForProject(long projectId) {
        return db.investorPitchDao().listForProject(projectId);
    }

    @NonNull
    public List<InvestorPitchEntity> listAllOrderByCreatedDesc() {
        return db.investorPitchDao().listAllOrderByCreatedDesc();
    }

    public void deletePitch(long pitchId) {
        db.investorPitchDao().deleteById(pitchId);
    }
}
