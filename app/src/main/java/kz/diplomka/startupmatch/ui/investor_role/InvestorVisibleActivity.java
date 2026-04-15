package kz.diplomka.startupmatch.ui.investor_role;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.databinding.ActivityInvestorVisibleBinding;

public class InvestorVisibleActivity extends AppCompatActivity {

    private static final String EXTRA_INVESTOR_NAME = "extra.investor.name";
    private static final String EXTRA_INVESTOR_LEVEL = "extra.investor.level";

    @NonNull
    public static Intent newIntent(
            @NonNull Context context,
            @NonNull String investorName,
            @NonNull String investorLevel
    ) {
        Intent i = new Intent(context, InvestorVisibleActivity.class);
        i.putExtra(EXTRA_INVESTOR_NAME, investorName);
        i.putExtra(EXTRA_INVESTOR_LEVEL, investorLevel);
        return i;
    }

    private ActivityInvestorVisibleBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInvestorVisibleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String investorName = getIntent().getStringExtra(EXTRA_INVESTOR_NAME);
        String investorLevel = getIntent().getStringExtra(EXTRA_INVESTOR_LEVEL);
        if (investorName == null || investorName.trim().isEmpty()) {
            investorName = getString(R.string.investor_name_role);
        }
        if (investorLevel == null || investorLevel.trim().isEmpty()) {
            investorLevel = getString(R.string.investor_visible_default_level);
        }

        binding.textWelcomeName.setText(
                getString(R.string.investor_visible_welcome_name_format, investorName));
        binding.textCurrentLevel.setText(investorLevel);

        binding.buttonBack.setOnClickListener(v -> finish());
        binding.buttonStartVerification.setOnClickListener(v ->
                Toast.makeText(this, R.string.investor_visible_open, Toast.LENGTH_SHORT).show());
        binding.buttonOpenExperience.setOnClickListener(v ->
                Toast.makeText(this, R.string.investor_visible_open, Toast.LENGTH_SHORT).show());
        binding.buttonGetExperienced.setOnClickListener(v ->
                Toast.makeText(this, R.string.investor_visible_open, Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
