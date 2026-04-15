package kz.diplomka.startupmatch.ui.investor_role.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.data.local.session.InvestorSessionPrefs;
import kz.diplomka.startupmatch.databinding.FragmentInvestorHomeBinding;
import kz.diplomka.startupmatch.ui.investor_role.InvestorProjectSuggestionsAdapter;
import kz.diplomka.startupmatch.ui.investor_role.data.InvestorHomeRepository;
import kz.diplomka.startupmatch.ui.investor_role.model.InvestorProjectSuggestionUi;
import kz.diplomka.startupmatch.ui.investor_role.InvestorVisibleActivity;

public class InvestorHomeFragment extends Fragment {

    private enum SuggestionsFilter {
        ALL,
        FINTECH,
        EDTECH,
        HEALTH
    }

    private FragmentInvestorHomeBinding binding;
    private InvestorProjectSuggestionsAdapter suggestionsAdapter;
    private InvestorHomeRepository repository;
    @NonNull
    private final List<InvestorProjectSuggestionUi> allSuggestions = new ArrayList<>();
    @NonNull
    private SuggestionsFilter activeFilter = SuggestionsFilter.ALL;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentInvestorHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = new InvestorHomeRepository(requireContext());
        suggestionsAdapter = new InvestorProjectSuggestionsAdapter(requireContext());
        binding.recyclerSuggestions.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerSuggestions.setAdapter(suggestionsAdapter);
        bindInvestorName();

        binding.buttonFilter.setOnClickListener(v -> {
            String investorName = InvestorSessionPrefs.getDisplayName(requireContext());
            if (investorName == null || investorName.trim().isEmpty()) {
                investorName = getString(R.string.investor_name_role);
            }
            Intent intent = InvestorVisibleActivity.newIntent(
                    requireContext(),
                    investorName,
                    "Verified Investor");
            startActivity(intent);
        });
        setupFilterActions();
        loadSuggestions();
    }

    @Override
    public void onResume() {
        super.onResume();
        bindInvestorName();
        loadSuggestions();
    }

    private void bindInvestorName() {
        if (binding == null) {
            return;
        }
        String investorName = InvestorSessionPrefs.getDisplayName(requireContext());
        if (investorName == null || investorName.trim().isEmpty()) {
            investorName = getString(R.string.investor_name_role);
        }
        binding.investorName.setText(investorName);
    }

    private void loadSuggestions() {
        if (binding == null || repository == null || suggestionsAdapter == null) {
            return;
        }
        allSuggestions.clear();
        allSuggestions.addAll(repository.getStartupProjectSuggestions());
        applyActiveFilter();
    }

    private void setupFilterActions() {
        if (binding == null) {
            return;
        }
        binding.filterAll.setOnClickListener(v -> selectFilter(SuggestionsFilter.ALL));
        binding.filterFintech.setOnClickListener(v -> selectFilter(SuggestionsFilter.FINTECH));
        binding.filterEdtech.setOnClickListener(v -> selectFilter(SuggestionsFilter.EDTECH));
        binding.filterHealth.setOnClickListener(v -> selectFilter(SuggestionsFilter.HEALTH));
        updateFilterUi();
    }

    private void selectFilter(@NonNull SuggestionsFilter filter) {
        if (activeFilter == filter) {
            return;
        }
        activeFilter = filter;
        updateFilterUi();
        applyActiveFilter();
    }

    private void applyActiveFilter() {
        if (binding == null || suggestionsAdapter == null) {
            return;
        }
        List<InvestorProjectSuggestionUi> filtered = new ArrayList<>();
        for (InvestorProjectSuggestionUi item : allSuggestions) {
            if (matchesFilter(item)) {
                filtered.add(item);
            }
        }
        suggestionsAdapter.submit(filtered);
        boolean empty = filtered.isEmpty();
        binding.textEmptySuggestions.setVisibility(empty ? View.VISIBLE : View.GONE);
        binding.recyclerSuggestions.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private boolean matchesFilter(@NonNull InvestorProjectSuggestionUi item) {
        if (activeFilter == SuggestionsFilter.ALL) {
            return true;
        }
        String industry = item.industry.toLowerCase(Locale.ROOT);
        switch (activeFilter) {
            case FINTECH:
                return industry.contains("fintech") || industry.contains("fin tech");
            case EDTECH:
                return industry.contains("edtech") || industry.contains("ed tech");
            case HEALTH:
                return industry.contains("health") || industry.contains("med");
            case ALL:
            default:
                return true;
        }
    }

    private void updateFilterUi() {
        if (binding == null || getContext() == null) {
            return;
        }
        applyFilterChipStyle(
                binding.filterAllIconBg,
                binding.filterAllIcon,
                binding.filterAllText,
                activeFilter == SuggestionsFilter.ALL
        );
        applyFilterChipStyle(
                binding.filterFintechIconBg,
                binding.filterFintechIcon,
                binding.filterFintechText,
                activeFilter == SuggestionsFilter.FINTECH
        );
        applyFilterChipStyle(
                binding.filterEdtechIconBg,
                binding.filterEdtechIcon,
                binding.filterEdtechText,
                activeFilter == SuggestionsFilter.EDTECH
        );
        applyFilterChipStyle(
                binding.filterHealthIconBg,
                binding.filterHealthIcon,
                binding.filterHealthText,
                activeFilter == SuggestionsFilter.HEALTH
        );
    }

    private void applyFilterChipStyle(
            @NonNull View iconBg,
            @NonNull ImageView icon,
            @NonNull android.widget.TextView label,
            boolean selected
    ) {
        iconBg.setBackgroundResource(selected
                ? R.drawable.bg_filter_sheet_chip_selected
                : R.drawable.bg_command_tag_pill);
        icon.setColorFilter(ContextCompat.getColor(
                requireContext(),
                selected ? R.color.white : R.color.kvadrat_blue
        ));
        label.setTextColor(ContextCompat.getColor(
                requireContext(),
                selected ? R.color.kvadrat_blue : R.color.investor_text_secondary
        ));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        allSuggestions.clear();
        suggestionsAdapter = null;
        repository = null;
        binding = null;
    }
}
