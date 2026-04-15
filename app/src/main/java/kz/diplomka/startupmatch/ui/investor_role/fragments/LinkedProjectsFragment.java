package kz.diplomka.startupmatch.ui.investor_role.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import kz.diplomka.startupmatch.databinding.FragmentLinkedProjectsBinding;
import kz.diplomka.startupmatch.ui.investor_role.LinkedIncomingPitchesAdapter;
import kz.diplomka.startupmatch.ui.investor_role.data.LinkedProjectsRepository;
import kz.diplomka.startupmatch.ui.investors.data.InvestorPitchRepository;

public class LinkedProjectsFragment extends Fragment implements LinkedIncomingPitchesAdapter.PitchDeleter {

    private FragmentLinkedProjectsBinding binding;
    private LinkedIncomingPitchesAdapter adapter;
    private LinkedProjectsRepository repository;
    private InvestorPitchRepository pitchRepository;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentLinkedProjectsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        repository = new LinkedProjectsRepository(requireContext());
        pitchRepository = new InvestorPitchRepository(requireContext());
        adapter = new LinkedIncomingPitchesAdapter(requireContext(), this);
        binding.recyclerIncoming.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerIncoming.setAdapter(adapter);
        loadCards();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCards();
    }

    private void loadCards() {
        if (binding == null || adapter == null || repository == null) {
            return;
        }
        adapter.submit(repository.loadIncomingPitchCards());
    }

    @Override
    public void deletePitch(long pitchId) {
        pitchRepository.deletePitch(pitchId);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter = null;
        repository = null;
        pitchRepository = null;
        binding = null;
    }
}
