package kz.diplomka.startupmatch.ui.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import kz.diplomka.startupmatch.MainActivity;
import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.data.local.AppDatabase;
import kz.diplomka.startupmatch.data.local.entity.AuthUserEntity;
import kz.diplomka.startupmatch.data.local.session.AuthRolePrefs;
import kz.diplomka.startupmatch.data.local.session.InvestorSessionPrefs;
import kz.diplomka.startupmatch.ui.investor_role.InvestorRoleActivity;

public class RegistrationActivity extends AppCompatActivity {

    private static final int MIN_PASSWORD_LENGTH = 6;
    private boolean passwordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        MaterialButton buttonRegister = findViewById(R.id.buttonRegister);
        TextView textLoginLink = findViewById(R.id.textLoginLink);
        TextView textSelectedRole = findViewById(R.id.textSelectedRole);
        EditText editFullName = findViewById(R.id.editFullName);
        EditText editEmail = findViewById(R.id.editEmail);
        EditText editPhone = findViewById(R.id.editPhone);
        EditText editPassword = findViewById(R.id.editPassword);
        ImageView showPassword = findViewById(R.id.showPassword);

        bindSelectedRoleLine(textSelectedRole);

        buttonRegister.setOnClickListener(v ->
                registerUser(editFullName, editEmail, editPhone, editPassword));

        textLoginLink.setOnClickListener(v ->
                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class)));

        showPassword.setOnClickListener(v -> togglePasswordVisibility(editPassword));
    }

    private void togglePasswordVisibility(EditText editPassword) {
        passwordVisible = !passwordVisible;
        int cursorPosition = editPassword.getSelectionEnd();
        if (passwordVisible) {
            editPassword.setInputType(
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        } else {
            editPassword.setInputType(
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        if (cursorPosition >= 0 && cursorPosition <= editPassword.getText().length()) {
            editPassword.setSelection(cursorPosition);
        }
    }

    private void registerUser(
            EditText editFullName,
            EditText editEmail,
            EditText editPhone,
            EditText editPassword
    ) {
        String fullName = editFullName.getText() != null
                ? editFullName.getText().toString().trim()
                : "";
        String email = editEmail.getText() != null
                ? editEmail.getText().toString().trim().toLowerCase()
                : "";
        String phone = editPhone.getText() != null
                ? normalizePhone(editPhone.getText().toString())
                : "";
        String password = editPassword.getText() != null
                ? editPassword.getText().toString()
                : "";
        String role = AuthRolePrefs.getSelectedRole(this);

        String error = validate(fullName, email, phone, password, role);
        if (error != null) {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            return;
        }

        AppDatabase db = AppDatabase.get(this);
        if (db.authUserDao().getByEmail(email) != null) {
            Toast.makeText(this, R.string.registration_error_email_exists, Toast.LENGTH_SHORT).show();
            return;
        }

        AuthUserEntity entity = new AuthUserEntity(
                fullName,
                email,
                password,
                phone,
                role,
                System.currentTimeMillis()
        );
        db.authUserDao().insert(entity);
        Toast.makeText(this, R.string.registration_success, Toast.LENGTH_SHORT).show();

        if (AuthRolePrefs.ROLE_INVESTOR.equals(role)) {
            // Инвестор аты келесі экрандарда (challenge/create/navigation) қолданылсын.
            InvestorSessionPrefs.setProfile(this, fullName, null);
            startActivity(new Intent(this, InvestorRoleActivity.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }

    private String validate(
            String fullName,
            String email,
            String phone,
            String password,
            String role
    ) {
        if (fullName.length() < 2) {
            return getString(R.string.registration_error_name);
        }
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return getString(R.string.registration_error_email);
        }
        if (!isValidPhone(phone)) {
            return getString(R.string.registration_error_phone);
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            return getString(R.string.registration_error_password);
        }
        if (!AuthRolePrefs.ROLE_FOUNDER.equals(role)
                && !AuthRolePrefs.ROLE_INVESTOR.equals(role)) {
            return getString(R.string.registration_error_role);
        }
        return null;
    }

    @NonNull
    private static String normalizePhone(@NonNull String raw) {
        return raw.replaceAll("[^0-9+]", "");
    }

    private static boolean isValidPhone(@NonNull String phone) {
        if (phone.isEmpty()) {
            return false;
        }
        String digits = phone.replaceAll("[^0-9]", "");
        return digits.length() >= 10 && digits.length() <= 15;
    }

    private void bindSelectedRoleLine(TextView textSelectedRole) {
        String role = AuthRolePrefs.getSelectedRole(this);
        if (AuthRolePrefs.ROLE_INVESTOR.equals(role)) {
            textSelectedRole.setText(R.string.registration_role_line_investor);
            textSelectedRole.setVisibility(View.VISIBLE);
        } else if (AuthRolePrefs.ROLE_FOUNDER.equals(role)) {
            textSelectedRole.setText(R.string.registration_role_line_founder);
            textSelectedRole.setVisibility(View.VISIBLE);
        } else {
            textSelectedRole.setVisibility(View.GONE);
        }
    }
}
