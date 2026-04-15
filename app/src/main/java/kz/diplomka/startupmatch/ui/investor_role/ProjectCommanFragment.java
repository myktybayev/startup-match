package kz.diplomka.startupmatch.ui.investor_role;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kz.diplomka.startupmatch.data.local.AppDatabase;
import kz.diplomka.startupmatch.data.local.entity.TeamMemberEntity;
import kz.diplomka.startupmatch.databinding.FragmentProjectCommanBinding;
import kz.diplomka.startupmatch.ui.investor_role.model.TeamMemberRowUi;

public class ProjectCommanFragment extends Fragment {

    @NonNull
    public static ProjectCommanFragment newInstance(@NonNull Bundle projectArgs) {
        ProjectCommanFragment fragment = new ProjectCommanFragment();
        fragment.setArguments(new Bundle(projectArgs));
        return fragment;
    }

    private FragmentProjectCommanBinding binding;
    private TeamMemberCardsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentProjectCommanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new TeamMemberCardsAdapter(requireContext());
        binding.recyclerTeamMembers.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerTeamMembers.setAdapter(adapter);
        loadTeamRows();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTeamRows();
    }

    private void loadTeamRows() {
        if (binding == null || adapter == null) {
            return;
        }
        Bundle args = getArguments();
        long projectId = args != null
                ? args.getLong(ProjectDetailActivity.EXTRA_PROJECT_ID, -1L)
                : -1L;

        List<TeamMemberRowUi> rows = new ArrayList<>();

        if (projectId > 0L) {
            List<TeamMemberEntity> entities = AppDatabase.get(requireContext())
                    .teamMemberDao()
                    .listForProject(projectId);
            if (entities != null) {
                for (TeamMemberEntity e : entities) {
                    rows.add(TeamMemberRowUi.fromEntity(e));
                }
            }
        }

        if (!rows.isEmpty()) {
            binding.textTeamEmpty.setVisibility(View.GONE);
            adapter.submit(rows, projectId);
            return;
        }

        if (projectId > 0L) {
            binding.textTeamEmpty.setVisibility(View.VISIBLE);
            adapter.submit(Collections.emptyList(), projectId);
            return;
        }

        List<TeamMemberRowUi> demo = TeamMemberRowUi.placeholderTeamForDemoProject(projectId);
        if (!demo.isEmpty()) {
            binding.textTeamEmpty.setVisibility(View.GONE);
            adapter.submit(demo, projectId);
            return;
        }

        binding.textTeamEmpty.setVisibility(View.VISIBLE);
        adapter.submit(Collections.emptyList(), projectId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        adapter = null;
    }
}
