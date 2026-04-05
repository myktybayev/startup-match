package kz.diplomka.startupmatch.ui.home.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.data.local.AppDatabase;
import kz.diplomka.startupmatch.data.local.entity.ProjectEntity;
import kz.diplomka.startupmatch.ui.home.navigation.ProjectFlowExtras;

import org.json.JSONException;
import org.json.JSONObject;

public class AddTractionActivity extends AppCompatActivity {

    private static final String J_USERS = "users";
    private static final String J_MRR = "mrr";
    private static final String J_GROWTH = "growth";
    private static final String J_KPI = "kpi";

    private long projectId = -1L;

    private EditText editUsers;
    private EditText editMrr;
    private EditText editGrowth;
    private EditText editKpi;
    private MaterialButton buttonFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_traction);

        projectId = getIntent().getLongExtra(ProjectFlowExtras.EXTRA_PROJECT_ID, -1L);
        if (projectId <= 0) {
            ProjectEntity latest = AppDatabase.get(this).projectDao().getLatest();
            if (latest != null) {
                projectId = latest.getId();
            }
        }

        ImageView buttonBack = findViewById(R.id.buttonBack);
        editUsers = findViewById(R.id.editTractionUsers);
        editMrr = findViewById(R.id.editTractionMrr);
        editGrowth = findViewById(R.id.editTractionGrowth);
        editKpi = findViewById(R.id.editTractionKpi);
        buttonFinish = findViewById(R.id.buttonFinishPublish);

        buttonBack.setOnClickListener(v -> finish());
        loadExistingIfAny();

        buttonFinish.setOnClickListener(v -> {
            if (projectId <= 0) {
                Toast.makeText(this, getString(R.string.error_no_project_for_traction), Toast.LENGTH_SHORT).show();
                return;
            }
            if (!validateAllFields()) {
                Toast.makeText(this, getString(R.string.add_traction_fill_all), Toast.LENGTH_SHORT).show();
                return;
            }
            String json;
            try {
                json = buildJsonPayload();
            } catch (JSONException e) {
                Toast.makeText(this, getString(R.string.add_traction_save_error), Toast.LENGTH_SHORT).show();
                return;
            }
            long now = System.currentTimeMillis();
            AppDatabase.get(this).projectDao().updateTractionLink(projectId, json, now);
            Toast.makeText(this, getString(R.string.add_traction_saved), Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        });
    }

    private void loadExistingIfAny() {
        if (projectId <= 0) {
            return;
        }
        ProjectEntity project = AppDatabase.get(this).projectDao().getById(projectId);
        if (project == null) {
            return;
        }
        String raw = project.getTractionLink();
        if (TextUtils.isEmpty(raw)) {
            return;
        }
        applyJsonToFields(raw);
    }

    private void applyJsonToFields(@NonNull String raw) {
        String trimmed = raw.trim();
        if (!trimmed.startsWith("{")) {
            return;
        }
        try {
            JSONObject o = new JSONObject(trimmed);
            if (o.has(J_USERS)) {
                editUsers.setText(o.optString(J_USERS));
            }
            if (o.has(J_MRR)) {
                editMrr.setText(o.optString(J_MRR));
            }
            if (o.has(J_GROWTH)) {
                editGrowth.setText(o.optString(J_GROWTH));
            }
            if (o.has(J_KPI)) {
                editKpi.setText(o.optString(J_KPI));
            }
        } catch (JSONException ignored) {
        }
    }

    @NonNull
    private String buildJsonPayload() throws JSONException {
        JSONObject o = new JSONObject();
        o.put(J_USERS, editUsers.getText() == null ? "" : editUsers.getText().toString().trim());
        o.put(J_MRR, editMrr.getText() == null ? "" : editMrr.getText().toString().trim());
        o.put(J_GROWTH, editGrowth.getText() == null ? "" : editGrowth.getText().toString().trim());
        o.put(J_KPI, editKpi.getText() == null ? "" : editKpi.getText().toString().trim());
        return o.toString();
    }

    private boolean validateAllFields() {
        String u = editUsers.getText() == null ? "" : editUsers.getText().toString().trim();
        String m = editMrr.getText() == null ? "" : editMrr.getText().toString().trim();
        String g = editGrowth.getText() == null ? "" : editGrowth.getText().toString().trim();
        String k = editKpi.getText() == null ? "" : editKpi.getText().toString().trim();
        boolean ok = !u.isEmpty() && !m.isEmpty() && !g.isEmpty() && !k.isEmpty();
        if (u.isEmpty()) {
            editUsers.setError(getString(R.string.add_traction_field_required));
        }
        if (m.isEmpty()) {
            editMrr.setError(getString(R.string.add_traction_field_required));
        }
        if (g.isEmpty()) {
            editGrowth.setError(getString(R.string.add_traction_field_required));
        }
        if (k.isEmpty()) {
            editKpi.setError(getString(R.string.add_traction_field_required));
        }
        return ok;
    }
}
