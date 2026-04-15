package kz.diplomka.startupmatch.ui.investor_role;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.data.local.session.InvestorSessionPrefs;
import kz.diplomka.startupmatch.databinding.ActivityInvestorRoleBinding;

public class InvestorRoleActivity extends AppCompatActivity {

    private ActivityInvestorRoleBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInvestorRoleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (InvestorSessionPrefs.getDisplayName(this) == null) {
            InvestorSessionPrefs.setDisplayName(this, getString(R.string.investor_name_role));
        }

        BottomNavigationView navView = binding.navViewInvestor;
        NavController navController = Navigation.findNavController(this, R.id.nav_host_investor);
        NavigationUI.setupWithNavController(navView, navController);
    }
}
