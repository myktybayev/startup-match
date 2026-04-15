package kz.diplomka.startupmatch.ui.home.command;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.data.local.entity.InvestorPitchEntity;
import kz.diplomka.startupmatch.ui.investors.data.InvestorPitchRepository;
import kz.diplomka.startupmatch.ui.home.navigation.ProjectFlowExtras;

/**
 * Команда → Инвесторлар табы: жобаға байланысты pitch жіберілген инвесторлар тізімі (Figma LinkedInvestors).
 */
public class LinkedInvestorsFragment extends Fragment {

    private static final String ARG_PROJECT_ID = ProjectFlowExtras.EXTRA_PROJECT_ID;

    private long projectId = -1L;
    private RecyclerView recycler;
    private View emptyView;
    private LinkedInvestorsAdapter adapter;
    private InvestorPitchRepository repository;

    public static LinkedInvestorsFragment newInstance(long projectId) {
        LinkedInvestorsFragment f = new LinkedInvestorsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PROJECT_ID, projectId);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            projectId = args.getLong(ARG_PROJECT_ID, -1L);
        }
        repository = new InvestorPitchRepository(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_linked_investors, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recycler = view.findViewById(R.id.recyclerLinkedInvestors);
        emptyView = view.findViewById(R.id.textLinkedInvestorsEmpty);
        adapter = new LinkedInvestorsAdapter();
        recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        recycler.setAdapter(adapter);
        loadLinkedInvestors();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadLinkedInvestors();
    }

    private void loadLinkedInvestors() {
        if (projectId <= 0 || repository == null || adapter == null) {
            showEmpty(true);
            return;
        }
        List<InvestorPitchEntity> entities = repository.listForProject(projectId);
        List<LinkedInvestorUiModel> models =
                LinkedInvestorDisplayMapper.fromEntities(requireContext(), entities);
        adapter.submit(models);
        showEmpty(models.isEmpty());
    }

    private void showEmpty(boolean empty) {
        if (recycler == null || emptyView == null) {
            return;
        }
        recycler.setVisibility(empty ? View.GONE : View.VISIBLE);
        emptyView.setVisibility(empty ? View.VISIBLE : View.GONE);
    }
}
