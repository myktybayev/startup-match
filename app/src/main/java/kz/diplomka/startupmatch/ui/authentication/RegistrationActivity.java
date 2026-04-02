package kz.diplomka.startupmatch.ui.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import kz.diplomka.startupmatch.MainActivity;
import kz.diplomka.startupmatch.R;

public class RegistrationActivity extends AppCompatActivity {
    Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        buttonRegister = findViewById(R.id.buttonRegister);

        buttonRegister.setOnClickListener(v ->
                startActivity(new Intent(RegistrationActivity.this, MainActivity.class)));

    }
}
