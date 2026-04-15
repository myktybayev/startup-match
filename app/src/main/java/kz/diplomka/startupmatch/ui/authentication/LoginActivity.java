package kz.diplomka.startupmatch.ui.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import kz.diplomka.startupmatch.MainActivity;
import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.data.local.AppDatabase;
import kz.diplomka.startupmatch.data.local.entity.AuthUserEntity;
import kz.diplomka.startupmatch.data.local.session.AuthRolePrefs;
import kz.diplomka.startupmatch.data.local.session.InvestorSessionPrefs;
import kz.diplomka.startupmatch.databinding.ActivityLoginBinding;
import kz.diplomka.startupmatch.ui.investor_role.InvestorRoleActivity;

/**
 * Электрондық пошта және құпия сөз арқылы жобаға кіру.
 */
public class LoginActivity extends AppCompatActivity {

    private static final int MIN_PASSWORD_LENGTH = 6;

    private ActivityLoginBinding binding;
    private boolean passwordVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonLogin.setOnClickListener(v -> attemptLogin());
        binding.textRegister.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class)));
        binding.buttonTogglePassword.setOnClickListener(v -> togglePasswordVisibility());
    }

    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        int sel = binding.editPassword.getSelectionEnd();
        if (passwordVisible) {
            binding.editPassword.setInputType(
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            binding.editPassword.setInputType(
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        if (sel >= 0 && sel <= binding.editPassword.getText().length()) {
            binding.editPassword.setSelection(sel);
        }
    }

    private void attemptLogin() {
        String email = binding.editEmail.getText() != null
                ? binding.editEmail.getText().toString().trim().toLowerCase()
                : "";
        String password = binding.editPassword.getText() != null
                ? binding.editPassword.getText().toString()
                : "";

        String error = validate(email, password);
        if (error != null) {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            return;
        }

        AuthUserEntity user = AppDatabase.get(this).authUserDao().getByEmail(email);
        if (user == null || !password.equals(user.getPassword())) {
            Toast.makeText(this, R.string.login_error_credentials, Toast.LENGTH_SHORT).show();
            return;
        }

        // Кіруден кейін сессияға рөл/ат синхрондалады.
        AuthRolePrefs.setSelectedRole(this, user.getRole());
        if (AuthRolePrefs.ROLE_INVESTOR.equals(user.getRole())) {
            InvestorSessionPrefs.setProfile(this, user.getFullName(), null);
            startActivity(new Intent(LoginActivity.this, InvestorRoleActivity.class));
        } else {
            InvestorSessionPrefs.setProfile(this, null, null);
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
        finish();
    }

    private String validate(@NonNull String email, @NonNull String password) {
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return getString(R.string.login_error_email);
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            return getString(R.string.login_error_password);
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
