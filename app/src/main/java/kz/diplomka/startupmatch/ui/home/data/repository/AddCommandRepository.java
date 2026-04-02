package kz.diplomka.startupmatch.ui.home.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kz.diplomka.startupmatch.data.local.AppDatabase;
import kz.diplomka.startupmatch.data.local.dao.TeamMemberDao;
import kz.diplomka.startupmatch.data.local.entity.TeamMemberEntity;
import kz.diplomka.startupmatch.ui.home.module.CommandMemberData;

public class AddCommandRepository {

    private final TeamMemberDao teamMemberDao;
    private final long projectId;

    public AddCommandRepository(@NonNull Context context, long projectId) {
        this.teamMemberDao = AppDatabase.get(context.getApplicationContext()).teamMemberDao();
        this.projectId = projectId;
    }

    @NonNull
    public List<CommandMemberData> getMembers() {
        List<TeamMemberEntity> rows = teamMemberDao.listForProject(projectId);
        List<CommandMemberData> out = new ArrayList<>(rows.size());
        for (TeamMemberEntity row : rows) {
            out.add(fromEntity(row));
        }
        return out;
    }

    private static CommandMemberData fromEntity(@NonNull TeamMemberEntity row) {
        String portfolio = row.getPortfolio();
        return new CommandMemberData(
                row.getFullName(),
                row.getRole(),
                row.getExperience(),
                portfolio != null ? portfolio : "",
                row.getAvatarUri()
        );
    }

    public List<String> getRoleOptions() {
        return Arrays.asList(
                "Бас директор(CEO)",
                "Техникалық директор(CTO)",
                "Операциялық директор(COO)",
                "Маркетинг директоры(CMO)",
                "Қаржылық директор(CFO)",
                "Өнім менеджері(Product Manager)",
                "Программист(Developer)",
                "Сату менеджері(Sales Manager)",
                "Дизайнер(Designer)",
                "HR менеджері(HR Manager)"
        );
    }

    public boolean isMemberValid(CommandMemberData memberData) {
        return isFilled(memberData.getFullName())
                && isFilled(memberData.getRole())
                && isFilled(memberData.getExperience());
    }

    public void addMember(@NonNull CommandMemberData memberData) {
        String portfolio = memberData.getPortfolio();
        if (portfolio == null) {
            portfolio = "";
        }
        int order = teamMemberDao.countForProject(projectId);
        TeamMemberEntity entity = new TeamMemberEntity(
                projectId,
                memberData.getFullName().trim(),
                memberData.getRole().trim(),
                memberData.getExperience().trim(),
                portfolio.trim(),
                memberData.getAvatarUri(),
                order
        );
        teamMemberDao.insert(entity);
    }

    public int getMemberCount() {
        return teamMemberDao.countForProject(projectId);
    }

    private boolean isFilled(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
