package kz.diplomka.startupmatch.ui.сhallenges;

import static kz.diplomka.startupmatch.ui.сhallenges.ChallengeDetailActivity.EXTRA_CHALLENGE_ID;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.data.local.entity.ChallengeSubmissionEntity;
import kz.diplomka.startupmatch.databinding.FragmentChallengesBinding;
import kz.diplomka.startupmatch.ui.сhallenges.data.ChallengesRepository;
import kz.diplomka.startupmatch.ui.сhallenges.model.Challenge;
import kz.diplomka.startupmatch.ui.сhallenges.model.ChallengeType;

public class ChallengesFragment extends Fragment {

    private static final int[] CHIP_IDS = {
            R.id.chip_filter_all,
            R.id.chip_filter_ai,
            R.id.chip_filter_mobile,
            R.id.chip_filter_fintech,
            R.id.chip_filter_saas,
            R.id.chip_filter_web,
    };

    private FragmentChallengesBinding binding;
    private ChallengesRepository repository;
    private OpenChallengesAdapter openAdapter;
    private FeaturedChallengesAdapter featuredAdapter;
    private MyChallengeSubmissionsAdapter mySubmissionsAdapter;
    private int selectedFilterChipId = R.id.chip_filter_all;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentChallengesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = new ChallengesRepository(requireContext());

        binding.textChallengesInsight.setText(
                getString(R.string.challenges_insight_format, repository.getProfileMatchCount()));

        featuredAdapter = new FeaturedChallengesAdapter(requireContext());

        featuredAdapter.submit(repository.getFeaturedChallenges());
        featuredAdapter.setListener(id -> {
            Intent intent = new Intent(requireContext(), ChallengeDetailActivity.class);
            intent.putExtra("act", "startup");
            intent.putExtra(EXTRA_CHALLENGE_ID, id);
            startActivity(intent);
        });

        openAdapter = new OpenChallengesAdapter(requireContext());
        binding.recyclerOpenChallenges.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerOpenChallenges.setAdapter(openAdapter);
        openAdapter.submit(repository.filterOpenChallenges(null));
        openAdapter.setListener(this::openChallengeDetail);

        mySubmissionsAdapter = new MyChallengeSubmissionsAdapter(requireContext());
        binding.recyclerMySubmissions.setLayoutManager(
                new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        binding.recyclerMySubmissions.setAdapter(mySubmissionsAdapter);
        loadMySubmissions();

        setupFilterChips();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMySubmissions();
        if (binding != null && repository != null && openAdapter != null) {
            filterOpenChallenges(selectedFilterChipId);
        }
    }

    private void loadMySubmissions() {
        if (binding == null || repository == null || mySubmissionsAdapter == null) {
            return;
        }
        List<ChallengeSubmissionEntity> list = repository.listMyChallengeSubmissions();
        boolean has = !list.isEmpty();
        binding.textMySubmissionsEmpty.setVisibility(has ? View.GONE : View.VISIBLE);
        binding.recyclerMySubmissions.setVisibility(has ? View.VISIBLE : View.GONE);
        mySubmissionsAdapter.submit(list);
    }

    private void setupFilterChips() {
        View.OnClickListener listener = v -> selectChip(v.getId());
        for (int id : CHIP_IDS) {
            binding.getRoot().findViewById(id).setOnClickListener(listener);
        }
        selectChip(R.id.chip_filter_all);
    }

    private void selectChip(int selectedId) {
        selectedFilterChipId = selectedId;
        for (int id : CHIP_IDS) {
            TextView chip = binding.getRoot().findViewById(id);
            boolean selected = id == selectedId;
            chip.setBackgroundResource(selected
                    ? R.drawable.bg_challenge_filter_selected
                    : R.drawable.bg_challenge_filter_unselected);
            chip.setTextColor(ContextCompat.getColor(requireContext(),
                    selected ? R.color.white : R.color.investor_title));
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
    }
}
