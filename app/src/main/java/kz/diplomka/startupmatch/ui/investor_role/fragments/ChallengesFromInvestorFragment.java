package kz.diplomka.startupmatch.ui.investor_role.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.databinding.FragmentInvestorChallengesBinding;
import kz.diplomka.startupmatch.ui.сhallenges.ChallengeDetailActivity;
import kz.diplomka.startupmatch.ui.сhallenges.OpenChallengesAdapter;
import kz.diplomka.startupmatch.ui.сhallenges.data.ChallengesRepository;
import kz.diplomka.startupmatch.ui.сhallenges.model.Challenge;
import kz.diplomka.startupmatch.ui.сhallenges.model.ChallengeType;

/**
 * Инвестор «Тапсырмалар» экраны (Figma 153:557). Ашық тапсырмалар тізімі мен сүзгілер
 * {@link kz.diplomka.startupmatch.ui.сhallenges.ChallengesFragment} логикасын қайта қолданады.
 */
public class ChallengesFromInvestorFragment extends Fragment {

    private static final int[] CHIP_IDS = {
            R.id.chip_filter_all,
            R.id.chip_filter_ai,
            R.id.chip_filter_mobile,
            R.id.chip_filter_fintech,
            R.id.chip_filter_saas,
            R.id.chip_filter_web,
    };

    private FragmentInvestorChallengesBinding binding;
    private ChallengesRepository repository;
    private OpenChallengesAdapter openAdapter;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentInvestorChallengesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = new ChallengesRepository(requireContext());

        binding.textChallengesInsight.setText(
                getString(R.string.challenges_insight_format, repository.getProfileMatchCount()));

        openAdapter = new OpenChallengesAdapter(requireContext());
        binding.recyclerOpenChallenges.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerOpenChallenges.setAdapter(openAdapter);
        openAdapter.submit(repository.filterOpenChallenges(null));
        openAdapter.setListener(this::openChallengeDetail);

        View.OnClickListener addSoon = v -> Toast.makeText(
                requireContext(),
                R.string.investor_challenges_add_soon,
                Toast.LENGTH_SHORT
        ).show();
        binding.cardAddChallenge.setOnClickListener(addSoon);
        binding.buttonAddChallenge.setOnClickListener(addSoon);

        setupFilterChips();
    }

    private void setupFilterChips() {
        View.OnClickListener listener = v -> selectChip(v.getId());
        for (int id : CHIP_IDS) {
            binding.getRoot().findViewById(id).setOnClickListener(listener);
        }
        selectChip(R.id.chip_filter_all);
    }

    private void selectChip(int selectedId) {
        for (int id : CHIP_IDS) {
            TextView chip = binding.getRoot().findViewById(id);
            boolean selected = id == selectedId;
            chip.setBackgroundResource(selected
                    ? R.drawable.bg_challenge_filter_selected
                    : R.drawable.bg_challenge_filter_unselected);
            chip.setTextColor(ContextCompat.getColor(
                    requireContext(),
                    selected ? R.color.white : R.color.investor_text_secondary
            ));
        }
        filterOpenChallenges(selectedId);
    }

    private void filterOpenChallenges(int selectedChipId) {
        ChallengeType type = chipIdToChallengeType(selectedChipId);
        openAdapter.submit(repository.filterOpenChallenges(type));
    }

    private void openChallengeDetail(@NonNull Challenge challenge) {
        startActivity(ChallengeDetailActivity.newIntent(requireContext(), challenge.getId()));
    }

    @Nullable
    private static ChallengeType chipIdToChallengeType(int chipId) {
        if (chipId == R.id.chip_filter_all) {
            return null;
        }
        if (chipId == R.id.chip_filter_ai) {
            return ChallengeType.AI;
        }
        if (chipId == R.id.chip_filter_mobile) {
            return ChallengeType.MOBILE;
        }
        if (chipId == R.id.chip_filter_fintech) {
            return ChallengeType.FINTECH;
        }
        if (chipId == R.id.chip_filter_saas) {
            return ChallengeType.SAAS;
        }
        if (chipId == R.id.chip_filter_web) {
            return ChallengeType.WEB;
        }
        return null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        repository = null;
        openAdapter = null;
    }
}
