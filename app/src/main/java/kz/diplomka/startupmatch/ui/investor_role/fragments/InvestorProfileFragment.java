package kz.diplomka.startupmatch.ui.investor_role.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.data.local.AppDatabase;
import kz.diplomka.startupmatch.data.local.entity.AuthUserEntity;
import kz.diplomka.startupmatch.data.local.session.AuthRolePrefs;
import kz.diplomka.startupmatch.data.local.session.InvestorSessionPrefs;
import kz.diplomka.startupmatch.databinding.FragmentInvestorProfileBinding;
import kz.diplomka.startupmatch.ui.authentication.RolePageActivity;
import kz.diplomka.startupmatch.ui.investor_role.InvestorVisibleActivity;
import kz.diplomka.startupmatch.util.WhatsAppUtils;

public class InvestorProfileFragment extends Fragment {

    private FragmentInvestorProfileBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentInvestorProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindInvestorIdentity();
        bindCounters();
        setupActions();
    }

    @Override
    public void onResume() {
        super.onResume();
        bindInvestorIdentity();
        bindCounters();
    }

    private void bindInvestorIdentity() {
        if (binding == null) {
            return;
        }
        String name = InvestorSessionPrefs.getDisplayName(requireContext());
        if (name == null || name.trim().isEmpty()) {
            name = getString(R.string.investor_name_role);
        }
        binding.textProfileName.setText(name);

        AuthUserEntity latestInvestor = AppDatabase.get(requireContext())
                .authUserDao()
                .getLatestByRole(AuthRolePrefs.ROLE_INVESTOR);
        if (latestInvestor != null && latestInvestor.getEmail() != null
                && !latestInvestor.getEmail().trim().isEmpty()) {
            binding.textProfileEmail.setText(latestInvestor.getEmail().trim());
        } else {
            binding.textProfileEmail.setText(R.string.investor_profile_default_email);
        }
    }

    private void bindCounters() {
        if (binding == null) {
            return;
        }
        AppDatabase db = AppDatabase.get(requireContext());
        int savedProjects = db.projectDao().getAll().size();
        int activeChallenges = db.challengeSubmissionDao().listAllOrdered().size();
        binding.textSavedProjectsCount.setText(String.valueOf(savedProjects));
        binding.textActiveChallengesCount.setText(String.valueOf(activeChallenges));
    }

    private void setupActions() {
        if (binding == null) {
            return;
        }
        binding.buttonProfileSettings.setOnClickListener(v ->
                Toast.makeText(requireContext(), R.string.profile_toast_soon, Toast.LENGTH_SHORT).show());
        binding.rowMyChallenges.setOnClickListener(v -> navigateInvestorTab(R.id.investor_navigation_challenges));
        binding.rowSavedStartups.setOnClickListener(v -> navigateInvestorTab(R.id.investor_navigation_projects));


        binding.rowInvestorStatus.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), InvestorVisibleActivity.class))
        );

        binding.rowHelpSupport.setOnClickListener(v ->
                WhatsAppUtils.openChatOrToast(
                        requireContext(),
                        "77023310762",
                        getString(R.string.profile_help_whatsapp_message)));
        binding.rowLogout.setOnClickListener(v -> logout());
    }

    private void navigateInvestorTab(int destinationId) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_investor);
        navController.navigate(destinationId);
    }

    private void logout() {
        startActivity(new Intent(requireContext(), RolePageActivity.class));
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
