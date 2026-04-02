package kz.diplomka.startupmatch.ui.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.List;

import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.ui.home.data.repository.AddProjectRepository;
import kz.diplomka.startupmatch.ui.home.module.AddProjectFormData;
import kz.diplomka.startupmatch.ui.home.navigation.ProjectFlowExtras;

public class AddProjectActivity extends AppCompatActivity {

    private AddProjectRepository repository;

    private EditText editProjectName;
    private Spinner spinnerIndustry;
    private View industryDropdownContainer;
    private EditText editTargetAudience;
    private EditText editShortDescription;
    private EditText editFullDescription;
    private MaterialButton buttonMarketLocal;
    private MaterialButton buttonMarketGlobal;
    private MaterialButton buttonNextStep;

    private String selectedMarket = "Локал";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        repository = new AddProjectRepository(this);
        bindViews();
        setupIndustryDropdown();
        setupMarketSwitch();
        setupActions();
    }

    private void bindViews() {
        editProjectName = findViewById(R.id.editProjectName);
        spinnerIndustry = findViewById(R.id.spinnerIndustry);
        industryDropdownContainer = findViewById(R.id.industryDropdownContainer);
        editTargetAudience = findViewById(R.id.editTargetAudience);
        editShortDescription = findViewById(R.id.editShortDescription);
        editFullDescription = findViewById(R.id.editFullDescription);
        buttonMarketLocal = findViewById(R.id.buttonMarketLocal);
        buttonMarketGlobal = findViewById(R.id.buttonMarketGlobal);
        buttonNextStep = findViewById(R.id.buttonNextStep);
        ImageView buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> finish());
    }

    private void setupIndustryDropdown() {
        List<String> industries = repository.getIndustries();

        // Placeholder as 0th item to detect "not selected" state.
        String placeholder = getString(R.string.add_project_industry_hint);
        List<String> items = new java.util.ArrayList<>();
        items.add(placeholder);
        items.addAll(industries);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                items
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIndustry.setAdapter(adapter);
        spinnerIndustry.setSelection(0);
    }

    private void setupMarketSwitch() {
        selectMarket("Локал");
        buttonMarketLocal.setOnClickListener(v -> selectMarket("Локал"));
        buttonMarketGlobal.setOnClickListener(v -> selectMarket("Глобал"));
    }

    private void selectMarket(String market) {
        selectedMarket = market;
        boolean isLocal = "Локал".equals(market);

        buttonMarketLocal.setBackgroundTintList(getColorStateList(
                isLocal ? R.color.white : R.color.transparent_gray
        ));
        buttonMarketGlobal.setBackgroundTintList(getColorStateList(
                isLocal ? R.color.transparent_gray : R.color.white
        ));

        buttonMarketLocal.setTextColor(getColor(isLocal ? R.color.onboarding_text_primary : R.color.onboarding_subtitle));
        buttonMarketGlobal.setTextColor(getColor(isLocal ? R.color.onboarding_subtitle : R.color.onboarding_text_primary));
    }

    private void setupActions() {
        buttonNextStep.setOnClickListener(v -> {
            validateFields();

            String industry = getSelectedIndustryOrEmpty();
            AddProjectFormData formData = new AddProjectFormData(
                    editProjectName.getText().toString(),
                    industry,
                    editTargetAudience.getText().toString(),
                    selectedMarket,
                    editShortDescription.getText().toString(),
                    editFullDescription.getText().toString()
            );

            if (repository.isFormValid(formData)) {
                long projectId = repository.insertProject(formData);
                Intent intent = new Intent(this, AddCommandActivity.class);
                intent.putExtra(ProjectFlowExtras.EXTRA_PROJECT_ID, projectId);
                startActivity(intent);
            } else {
                Toast.makeText(this, getString(R.string.add_project_fill_required), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void validateFields() {
        setFieldErrorIfEmpty(editProjectName, R.string.add_project_required_error);
        setFieldErrorIfEmpty(editTargetAudience, R.string.add_project_required_error);
        setFieldErrorIfEmpty(editShortDescription, R.string.add_project_required_error);
        setFieldErrorIfEmpty(editFullDescription, R.string.add_project_required_error);
        validateIndustryField();
    }

    private void setFieldErrorIfEmpty(EditText field, int errorResId) {
        String value = field.getText() == null ? "" : field.getText().toString().trim();
        field.setError(value.isEmpty() ? getString(errorResId) : null);
    }

    private void validateIndustryField() {
        boolean isValid = spinnerIndustry.getSelectedItemPosition() > 0;
        industryDropdownContainer.setBackgroundResource(
                isValid ? R.drawable.bg_add_project_input : R.drawable.bg_add_project_input_error
        );
    }

    private String getSelectedIndustryOrEmpty() {
        return spinnerIndustry.getSelectedItemPosition() > 0
                ? String.valueOf(spinnerIndustry.getSelectedItem())
                : "";
    }
}
