package kz.diplomka.startupmatch.ui.investors;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.databinding.BottomSheetInvestorsFilterBinding;

/**
 * Figma FilterBottomSheet — инвесторларды кеңейтілген сүзгілеу.
 */
public class InvestorFilterBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG_SHEET = "InvestorFilterBottomSheet";
    public static final String REQUEST_KEY = "investor_filter_sheet";
    public static final String ARG_INVESTORS = "investors";
    public static final String ARG_CHIP_INDEX = "chip_index";
    public static final String ARG_INITIAL_STATE = "initial_state";
    public static final String EXTRA_STATE = "state";
    public static final String EXTRA_CLEARED = "cleared";

    private BottomSheetInvestorsFilterBinding binding;

    @NonNull
    public static InvestorFilterBottomSheet newInstance(
            @NonNull ArrayList<InvestorListItem> investors,
            int chipIndex,
            @Nullable InvestorFilterSheetState initialState) {
        InvestorFilterBottomSheet f = new InvestorFilterBottomSheet();
        Bundle b = new Bundle();
        b.putSerializable(ARG_INVESTORS, investors);
        b.putInt(ARG_CHIP_INDEX, chipIndex);
        b.putSerializable(ARG_INITIAL_STATE, initialState != null ? initialState : InvestorFilterSheetState.empty());
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.ThemeOverlay_StartupMatch_BottomSheetDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = BottomSheetInvestorsFilterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = requireArguments();
        InvestorFilterSheetState initial =
                (InvestorFilterSheetState) args.getSerializable(ARG_INITIAL_STATE);
        if (initial == null) {
            initial = InvestorFilterSheetState.empty();
        }
        applyStateToUi(initial);

        Runnable updateCount = this::updateApplyButtonCount;
        setupSingleSelect(
                new TextView[]{
                        binding.chipStageIdea,
                        binding.chipStageMvp,
                        binding.chipStageGrowth
                },
                updateCount);
        setupMultiToggle(
                new TextView[]{
                        binding.chipIndAi,
                        binding.chipIndFintech,
                        binding.chipIndEdtech,
                        binding.chipIndSaas
                },
                updateCount);
        setupSingleSelect(
                new TextView[]{
                        binding.chipTicket1,
                        binding.chipTicket2,
                        binding.chipTicket3
                },
                updateCount);
        setupSingleSelect(
                new TextView[]{
                        binding.chipGeoKz,
                        binding.chipGeoCa,
                        binding.chipGeoGlobal
                },
                updateCount);
        setupSingleSelect(
                new TextView[]{
                        binding.chipStatusGuest,
                        binding.chipStatusVerified,
                        binding.chipStatusExperienced
                },
                updateCount);

        binding.switchActiveInvestors.setOnCheckedChangeListener((b, v) -> updateApplyButtonCount());
        binding.switchChallenges.setOnCheckedChangeListener((b, v) -> updateApplyButtonCount());

        binding.textSheetClearHeader.setOnClickListener(v -> clearAndDismiss());
        binding.buttonSheetClearFooter.setOnClickListener(v -> clearAndDismiss());
        binding.buttonSheetApply.setOnClickListener(v -> applyAndDismiss());

        updateApplyButtonCount();
    }

    private void setupSingleSelect(@NonNull TextView[] chips, @NonNull Runnable onChange) {
        for (TextView tv : chips) {
            tv.setOnClickListener(v -> {
                for (TextView t : chips) {
                    t.setSelected(t == v);
                }
                onChange.run();
            });
        }
    }

    private void setupMultiToggle(@NonNull TextView[] chips, @NonNull Runnable onChange) {
        for (TextView tv : chips) {
            tv.setOnClickListener(v -> {
                tv.setSelected(!tv.isSelected());
                onChange.run();
            });
        }
    }

    private void applyStateToUi(@NonNull InvestorFilterSheetState s) {
        clearSelectionUi();
        if (s.stage != null) {
            if ("Idea".equalsIgnoreCase(s.stage)) {
                binding.chipStageIdea.setSelected(true);
            } else if ("MVP".equalsIgnoreCase(s.stage)) {
                binding.chipStageMvp.setSelected(true);
            } else if ("Growth".equalsIgnoreCase(s.stage)) {
                binding.chipStageGrowth.setSelected(true);
            }
        }
        for (String ind : s.industries) {
            if ("AI".equalsIgnoreCase(ind)) {
                binding.chipIndAi.setSelected(true);
            }
            if ("FinTech".equalsIgnoreCase(ind)) {
                binding.chipIndFintech.setSelected(true);
            }
            if ("EdTech".equalsIgnoreCase(ind)) {
                binding.chipIndEdtech.setSelected(true);
            }
            if ("SaaS".equalsIgnoreCase(ind)) {
                binding.chipIndSaas.setSelected(true);
            }
        }
        if (s.ticketTier != null) {
            switch (s.ticketTier) {
                case "1_10":
                    binding.chipTicket1.setSelected(true);
                    break;
                case "10_50":
                    binding.chipTicket2.setSelected(true);
                    break;
                case "50_plus":
                    binding.chipTicket3.setSelected(true);
                    break;
                default:
                    break;
            }
        }
        if (s.geo != null) {
            String kz = getString(R.string.investors_filter_geo_kz);
            String ca = getString(R.string.investors_filter_geo_ca);
            String gl = getString(R.string.investors_filter_geo_global);
            if (kz.equalsIgnoreCase(s.geo)) {
                binding.chipGeoKz.setSelected(true);
            } else if (ca.equalsIgnoreCase(s.geo)) {
                binding.chipGeoCa.setSelected(true);
            } else if (gl.equalsIgnoreCase(s.geo)) {
                binding.chipGeoGlobal.setSelected(true);
            }
        }
        if (s.status != null) {
            if ("Guest".equalsIgnoreCase(s.status)) {
                binding.chipStatusGuest.setSelected(true);
            } else if ("Verified".equalsIgnoreCase(s.status)) {
                binding.chipStatusVerified.setSelected(true);
            } else if ("Experienced".equalsIgnoreCase(s.status)) {
                binding.chipStatusExperienced.setSelected(true);
            }
        }
        binding.switchActiveInvestors.setChecked(s.activeInvestorsOnly);
        binding.switchChallenges.setChecked(s.challengesOnly);
    }

    private void clearSelectionUi() {
        TextView[] all = new TextView[]{
                binding.chipStageIdea, binding.chipStageMvp, binding.chipStageGrowth,
                binding.chipIndAi, binding.chipIndFintech, binding.chipIndEdtech, binding.chipIndSaas,
                binding.chipTicket1, binding.chipTicket2, binding.chipTicket3,
                binding.chipGeoKz, binding.chipGeoCa, binding.chipGeoGlobal,
                binding.chipStatusGuest, binding.chipStatusVerified, binding.chipStatusExperienced
        };
        for (TextView tv : all) {
            tv.setSelected(false);
        }
        binding.switchActiveInvestors.setChecked(false);
        binding.switchChallenges.setChecked(false);
    }

    @NonNull
    private InvestorFilterSheetState collectStateFromUi() {
        String stage = null;
        if (binding.chipStageIdea.isSelected()) {
            stage = "Idea";
        } else if (binding.chipStageMvp.isSelected()) {
            stage = "MVP";
        } else if (binding.chipStageGrowth.isSelected()) {
            stage = "Growth";
        }
        ArrayList<String> industries = new ArrayList<>();
        if (binding.chipIndAi.isSelected()) {
            industries.add("AI");
        }
        if (binding.chipIndFintech.isSelected()) {
            industries.add("FinTech");
        }
        if (binding.chipIndEdtech.isSelected()) {
            industries.add("EdTech");
        }
        if (binding.chipIndSaas.isSelected()) {
            industries.add("SaaS");
        }
        String ticket = null;
        if (binding.chipTicket1.isSelected()) {
            ticket = "1_10";
        } else if (binding.chipTicket2.isSelected()) {
            ticket = "10_50";
        } else if (binding.chipTicket3.isSelected()) {
            ticket = "50_plus";
        }
        String geo = null;
        if (binding.chipGeoKz.isSelected()) {
            geo = getString(R.string.investors_filter_geo_kz);
        } else if (binding.chipGeoCa.isSelected()) {
            geo = getString(R.string.investors_filter_geo_ca);
        } else if (binding.chipGeoGlobal.isSelected()) {
            geo = getString(R.string.investors_filter_geo_global);
        }
        String status = null;
        if (binding.chipStatusGuest.isSelected()) {
            status = "Guest";
        } else if (binding.chipStatusVerified.isSelected()) {
            status = "Verified";
        } else if (binding.chipStatusExperienced.isSelected()) {
            status = "Experienced";
        }
        return new InvestorFilterSheetState(
                stage,
                industries,
                ticket,
                geo,
                status,
                binding.switchActiveInvestors.isChecked(),
                binding.switchChallenges.isChecked());
    }

    @SuppressWarnings("unchecked")
    private void updateApplyButtonCount() {
        Bundle args = requireArguments();
        ArrayList<InvestorListItem> all =
                (ArrayList<InvestorListItem>) args.getSerializable(ARG_INVESTORS);
        int chipIndex = args.getInt(ARG_CHIP_INDEX, 0);
        if (all == null) {
            all = new ArrayList<>();
        }
        String[] chipLabels = getResources().getStringArray(R.array.investor_filter_labels);
        InvestorFilterSheetState provisional = collectStateFromUi();
        int n = InvestorFilterSheetState.countMatching(all, chipIndex, chipLabels, provisional);
        binding.buttonSheetApply.setText(getString(R.string.investors_filter_see_results, n));
    }

    private void applyAndDismiss() {
        InvestorFilterSheetState state = collectStateFromUi();
        Bundle b = new Bundle();
        b.putSerializable(EXTRA_STATE, state);
        b.putBoolean(EXTRA_CLEARED, false);
        getParentFragmentManager().setFragmentResult(REQUEST_KEY, b);
        dismiss();
    }

    private void clearAndDismiss() {
        Bundle b = new Bundle();
        b.putBoolean(EXTRA_CLEARED, true);
        getParentFragmentManager().setFragmentResult(REQUEST_KEY, b);
        dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
