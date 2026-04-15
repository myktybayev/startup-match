package kz.diplomka.startupmatch.ui.investor_role.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.data.local.AppDatabase;
import kz.diplomka.startupmatch.data.local.entity.AuthUserEntity;
import kz.diplomka.startupmatch.data.local.session.AuthRolePrefs;
import kz.diplomka.startupmatch.databinding.FragmentLinkedProjectsBinding;
import kz.diplomka.startupmatch.ui.investor_role.LinkedIncomingPitchesAdapter;
import kz.diplomka.startupmatch.ui.investor_role.data.LinkedProjectsRepository;
import kz.diplomka.startupmatch.ui.investors.data.InvestorPitchRepository;
import kz.diplomka.startupmatch.util.WhatsAppUtils;

public class LinkedProjectsFragment extends Fragment implements LinkedIncomingPitchesAdapter.PitchActions {

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
    public void openProjectChat(long projectId, @NonNull String startupName, @Nullable String fallbackPhone) {
        String phone = fallbackPhone;
        AuthUserEntity founderAccount = AppDatabase.get(requireContext())
                .authUserDao()
                .getLatestByRole(AuthRolePrefs.ROLE_FOUNDER);
        if (founderAccount != null && !TextUtils.isEmpty(founderAccount.getPhone())) {
            phone = founderAccount.getPhone();
        }
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(requireContext(), R.string.incoming_pitch_no_phone, Toast.LENGTH_SHORT).show();
            return;
        }
        String message = getString(R.string.incoming_whatsapp_prefill, startupName);
        WhatsAppUtils.openChatOrToast(requireContext(), phone, message);
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
