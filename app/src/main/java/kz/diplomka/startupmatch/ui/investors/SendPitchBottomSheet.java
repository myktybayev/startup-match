package kz.diplomka.startupmatch.ui.investors;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.data.local.entity.InvestorPitchEntity;
import kz.diplomka.startupmatch.databinding.BottomSheetSendPitchBinding;
import kz.diplomka.startupmatch.ui.investors.data.InvestorPitchRepository;

/**
 * Figma SendPitch (node 83:700) — инвесторға pitch жіберу формасы.
 */
public class SendPitchBottomSheet extends BottomSheetDialogFragment {

    public static final String TAG = "SendPitchBottomSheet";
    private static final String ARG_PROJECT_ID = "project_id";
    private static final String ARG_INVESTOR = "investor";

    private BottomSheetSendPitchBinding binding;
    private InvestorPitchRepository repository;

    @NonNull
    public static SendPitchBottomSheet newInstance(long projectId, @NonNull InvestorListItem investor) {
        SendPitchBottomSheet f = new SendPitchBottomSheet();
        Bundle b = new Bundle();
        b.putLong(ARG_PROJECT_ID, projectId);
        b.putSerializable(ARG_INVESTOR, investor);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.ThemeOverlay_StartupMatch_BottomSheetDialog);
        repository = new InvestorPitchRepository(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = BottomSheetSendPitchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = requireArguments();
        long projectId = args.getLong(ARG_PROJECT_ID, -1L);
        InvestorListItem investor = readInvestor(args);
        if (investor == null) {
            dismiss();
            return;
        }

        binding.buttonCloseSheet.setOnClickListener(v -> dismiss());
        binding.buttonSubmitPitch.setOnClickListener(v -> submit(projectId, investor));
    }

    @Nullable
    private InvestorListItem readInvestor(@NonNull Bundle args) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return args.getSerializable(ARG_INVESTOR, InvestorListItem.class);
        }
        return (InvestorListItem) args.getSerializable(ARG_INVESTOR);
    }

    private void submit(long projectId, @NonNull InvestorListItem investor) {
        if (projectId <= 0) {
            Toast.makeText(requireContext(), R.string.send_pitch_error_project, Toast.LENGTH_SHORT).show();
            return;
        }
        String users = safeTrim(binding.editTractionUsers.getText());
        String mrr = safeTrim(binding.editTractionMrr.getText());
        String growth = safeTrim(binding.editTractionGrowth.getText());
        String team = safeTrim(binding.editTeamWhyUs.getText());
        String validation = safeTrim(binding.editValidationMarket.getText());
        if (TextUtils.isEmpty(users)
                || TextUtils.isEmpty(mrr)
                || TextUtils.isEmpty(growth)
                || TextUtils.isEmpty(team)
                || TextUtils.isEmpty(validation)) {
            Toast.makeText(requireContext(), R.string.send_pitch_error_fields, Toast.LENGTH_SHORT).show();
            return;
        }
        long now = System.currentTimeMillis();
        InvestorPitchEntity entity =
                new InvestorPitchEntity(
                        projectId,
                        investor.name,
                        investor.role,
                        users,
                        mrr,
                        growth,
                        team,
                        validation,
                        now);
        repository.savePitch(entity);
        Toast.makeText(requireContext(), R.string.send_pitch_saved, Toast.LENGTH_SHORT).show();
        dismiss();
    }

    @NonNull
    private static String safeTrim(@Nullable CharSequence s) {
        return s == null ? "" : s.toString().trim();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
