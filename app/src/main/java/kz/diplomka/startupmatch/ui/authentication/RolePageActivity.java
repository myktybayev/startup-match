package kz.diplomka.startupmatch.ui.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.data.local.session.AuthRolePrefs;

public class RolePageActivity extends AppCompatActivity {

    private MaterialCardView cardFounder;
    private MaterialCardView cardInvestor;
    private MaterialButton buttonRegister;
    private ImageView indicatorFounder;
    private ImageView indicatorInvestor;
    private TextView textLogin;
    private String selectedRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_page);

        cardFounder = findViewById(R.id.cardFounder);
        cardInvestor = findViewById(R.id.cardInvestor);
        buttonRegister = findViewById(R.id.buttonRegister);
        indicatorFounder = findViewById(R.id.viewSelectedIndicatorFounder);
        indicatorInvestor = findViewById(R.id.viewSelectedIndicatorInvestor);
        textLogin = findViewById(R.id.textLogin);

        cardFounder.setOnClickListener(v -> selectRole("founder"));
        cardInvestor.setOnClickListener(v -> selectRole("investor"));

        buttonRegister.setOnClickListener(v -> {
            AuthRolePrefs.setSelectedRole(RolePageActivity.this, selectedRole);
            startActivity(new Intent(RolePageActivity.this, RegistrationActivity.class));
        });

        textLogin.setOnClickListener(v ->
                startActivity(new Intent(RolePageActivity.this, LoginActivity.class)));

        buttonRegister.setEnabled(true);
        buttonRegister.setAlpha(1f);
        selectRole("founder");
    }

    private void selectRole(String role) {
        selectedRole = role;
        boolean founderSelected = "founder".equals(selectedRole);

        setSelectedState(cardFounder, indicatorFounder, founderSelected);
        setSelectedState(cardInvestor, indicatorInvestor, !founderSelected);
    }

    private void setSelectedState(MaterialCardView card, ImageView indicator, boolean selected) {
        card.setStrokeWidth(selected ? dpToPx(2) : dpToPx(0));
        card.setStrokeColor(getColor(selected
                ? R.color.onboarding_primary
                : R.color.role_card_stroke_default));
        card.setCardBackgroundColor(getColor(selected
                ? android.R.color.white
                : R.color.role_card_unselected_bg));
        indicator.setImageResource(selected
                ? R.drawable.role_icon_check
                : R.drawable.role_icon_circle);
        indicator.setVisibility(View.VISIBLE);
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density);
    }
}
