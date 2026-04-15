package kz.diplomka.startupmatch.ui.investors;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.data.local.AppDatabase;
import kz.diplomka.startupmatch.data.local.entity.ProjectEntity;
import kz.diplomka.startupmatch.databinding.FragmentInvestorsBinding;

public class InvestorsFragment extends Fragment {

    private FragmentInvestorsBinding binding;
    private InvestorsAdapter investorsAdapter;
    private List<InvestorListItem> allInvestors = new ArrayList<>();
    private int selectedChipIndex = 0;
    @NonNull
    private InvestorFilterSheetState sheetState = InvestorFilterSheetState.empty();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentInvestorsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getParentFragmentManager().setFragmentResultListener(
                InvestorFilterBottomSheet.REQUEST_KEY,
                getViewLifecycleOwner(),
                (requestKey, bundle) -> {
                    if (bundle.getBoolean(InvestorFilterBottomSheet.EXTRA_CLEARED, false)) {
                        sheetState = InvestorFilterSheetState.empty();
                    } else {
                        InvestorFilterSheetState s = (InvestorFilterSheetState) bundle.getSerializable(
                                InvestorFilterBottomSheet.EXTRA_STATE);
                        if (s != null) {
                            sheetState = s;
                        }
                    }
                    applyCombinedFilter();
                });

        allInvestors = buildSampleInvestors();
        setupRecycler();
        setupFilterChips();
        binding.buttonInvestorsSearch.setOnClickListener(v -> {
        });
        binding.buttonInvestorsFilter.setOnClickListener(v -> {
            InvestorFilterBottomSheet sheet = InvestorFilterBottomSheet.newInstance(
                    new ArrayList<>(allInvestors),
                    selectedChipIndex,
                    sheetState);
            sheet.show(getParentFragmentManager(), InvestorFilterBottomSheet.TAG_SHEET);
        });
    }

    private void setupRecycler() {
        investorsAdapter = new InvestorsAdapter();
        investorsAdapter.setOnInvestorClickListener(item -> {
            long projectId = -1L;
            ProjectEntity project = AppDatabase.get(requireContext()).projectDao().getLatest();
            if (project != null) {
                projectId = project.getId();
            }
            Intent intent = InvestorsCabinetActivity.newIntent(
                    requireContext(),
                    item,
                    new ArrayList<>(allInvestors),
                    projectId);
            startActivity(intent);
        });
        binding.recyclerInvestors.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerInvestors.setAdapter(investorsAdapter);
        applyCombinedFilter();
    }

    /**
     * Горизонталь чиптер; әр басылғанда таңдалған индексті сақтайды.
     */
    private void setupFilterChips() {
        String[] labels = getResources().getStringArray(R.array.investor_filter_labels);
        LinearLayout row = binding.layoutFilterChips;
        row.removeAllViews();

        for (int i = 0; i < labels.length; i++) {
            TextView tv = new TextView(requireContext());
            tv.setText(labels[i]);
            tv.setBackgroundResource(R.drawable.bg_investor_filter_chip);
            tv.setTextColor(ContextCompat.getColorStateList(requireContext(), R.color.investor_filter_chip_text));
            tv.setPadding(dp(14), dp(6), dp(14), dp(6));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            tv.setTypeface(ResourcesCompat.getFont(requireContext(), R.font.inter_medium));

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMarginEnd(dp(8));
            tv.setLayoutParams(lp);
            tv.setClickable(true);

            final int index = i;
            tv.setOnClickListener(v -> {
                selectedChipIndex = index;
                for (int j = 0; j < row.getChildCount(); j++) {
                    row.getChildAt(j).setSelected(j == index);
                }
                applyCombinedFilter();
            });

            if (i == 0) {
                tv.setSelected(true);
            }
            row.addView(tv);
        }
    }

    private void applyCombinedFilter() {
        if (investorsAdapter == null) {
            return;
        }
        String[] labels = getResources().getStringArray(R.array.investor_filter_labels);
        List<InvestorListItem> out = new ArrayList<>();
        for (InvestorListItem item : allInvestors) {
            if (!InvestorFilterUtils.matchesChipIndex(item, selectedChipIndex, labels)) {
                continue;
            }
            if (!InvestorFilterSheetState.matches(item, sheetState)) {
                continue;
            }
            out.add(item);
        }
        investorsAdapter.submit(out);
    }

    /**
     * Мысал деректер: алдымен Figma InvestorsPage2 (node 91:1317), соңынан бұрынғы
     * экрандағы 5 инвестор.
     * Аватарлар: {@code figma_investors_page2_1..5.jpg},
     * {@code figma_investor_avatar_1..5.jpg}.
     */
    private List<InvestorListItem> buildSampleInvestors() {
        List<InvestorListItem> list = new ArrayList<>();
        list.addAll(Arrays.asList(
                new InvestorListItem(
                        R.drawable.figma_investors_page2_1,
                        "Айдана Серік",
                        InvestorListItem.BadgeKind.VERIFIED,
                        "Angel Investor",
                        new String[] { "FinTech", "B2B SaaS", "Payments" },
                        new String[] { "Idea", "MVP", "TicketSize", "FinTech" },
                        25,
                        120,
                        new String[] { "Kazakhstan", "UAE" },
                        "$25k – $120k • Kazakhstan, UAE",
                        "“Backing payment infrastructure and regional fintech scaling.”",
                        148,
                        4),
                new InvestorListItem(
                        R.drawable.figma_investors_page2_2,
                        "Bek Horizon",
                        InvestorListItem.BadgeKind.GUEST,
                        "Micro Fund",
                        new String[] { "EdTech", "Mobile" },
                        new String[] { "Idea", "MVP", "Growth" },
                        10,
                        35,
                        new String[] { "Central Asia" },
                        "$10k – $35k • Central Asia",
                        "“Interested in practical mobile products with early engagement.”",
                        76,
                        1),
                new InvestorListItem(
                        R.drawable.figma_investors_page2_3,
                        "Aruna Capital",
                        InvestorListItem.BadgeKind.EXPERIENCED,
                        "Seed Fund",
                        new String[] { "AI", "SaaS", "Analytics" },
                        new String[] { "MVP", "Growth", "AI", "TicketSize" },
                        150,
                        800,
                        new String[] { "Global" },
                        "$150k – $800k • Global",
                        "“Looks for strong data moats and repeatable growth.”",
                        312,
                        6),
                new InvestorListItem(
                        R.drawable.figma_investors_page2_4,
                        "Самат Алиев",
                        InvestorListItem.BadgeKind.VERIFIED,
                        "Angel Syndicate",
                        new String[] { "Logistics", "Marketplace" },
                        new String[] { "Idea", "MVP", "TicketSize" },
                        40,
                        90,
                        new String[] { "Kazakhstan" },
                        "$40k – $90k • Kazakhstan",
                        "“Supports founders solving regional supply chain inefficiencies.”",
                        94,
                        2),
                new InvestorListItem(
                        R.drawable.figma_investors_page2_5,
                        "Luna Ventures",
                        InvestorListItem.BadgeKind.EXPERIENCED,
                        "Venture Partner",
                        new String[] { "Climate", "Energy", "DeepTech" },
                        new String[] { "Growth", "TicketSize", "MVP" },
                        200,
                        1500,
                        new String[] { "Europe", "MENA" },
                        "$200k – $1.5M • Europe, MENA",
                        "“Focused on climate infrastructure and deeptech scale-ups.”",
                        226,
                        3)));
        list.addAll(Arrays.asList(
                new InvestorListItem(
                        R.drawable.figma_investor_avatar_1,
                        "Асқар Есен",
                        InvestorListItem.BadgeKind.VERIFIED,
                        "Angel Investor",
                        new String[] { "AI", "FinTech", "SaaS" },
                        new String[] { "Idea", "MVP", "TicketSize" },
                        10,
                        100,
                        new String[] { "Global" },
                        "$10k – $100k • Global",
                        "“Ex-founder, invests in MVP-stage startups”",
                        120,
                        3),
                new InvestorListItem(
                        R.drawable.figma_investor_avatar_2,
                        "Меруерт Садық",
                        InvestorListItem.BadgeKind.GUEST,
                        "Venture Fund Partner",
                        new String[] { "EdTech", "HealthTech" },
                        new String[] { "Idea", "Growth" },
                        100,
                        500,
                        new String[] { "Central Asia" },
                        "$100k – $500k • Central Asia",
                        "“Looking for scalable impact-driven startups”",
                        85,
                        1),
                new InvestorListItem(
                        R.drawable.figma_investor_avatar_3,
                        "Dana Ventures",
                        InvestorListItem.BadgeKind.VERIFIED,
                        "Angel Syndicate",
                        new String[] { "Climate", "Energy" },
                        new String[] { "Idea", "MVP", "TicketSize" },
                        25,
                        75,
                        new String[] { "Kazakhstan", "UAE" },
                        "$25k – $75k • Kazakhstan, UAE",
                        "“Passionate about green energy and sustainability”",
                        92,
                        2),
                new InvestorListItem(
                        R.drawable.figma_investor_avatar_4,
                        "Nurai Capital",
                        InvestorListItem.BadgeKind.EXPERIENCED,
                        "Seed Fund",
                        new String[] { "B2B SaaS", "Logistics", "AI" },
                        new String[] { "MVP", "Growth", "AI", "TicketSize" },
                        250,
                        1000,
                        new String[] { "CEE", "Central Asia" },
                        "$250k – $1M • CEE, Central Asia",
                        "“Backing bold founders with strong traction”",
                        340,
                        5),
                new InvestorListItem(
                        R.drawable.figma_investor_avatar_5,
                        "Timur Qaz Angels",
                        InvestorListItem.BadgeKind.GUEST,
                        "Micro Fund",
                        new String[] { "Marketplace", "Mobile" },
                        new String[] { "Idea", "MVP" },
                        15,
                        40,
                        new String[] { "Kazakhstan" },
                        "$15k – $40k • Kazakhstan",
                        "“Early-stage local consumer apps”",
                        45,
                        0)));
        return list;
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        investorsAdapter = null;
    }
}
