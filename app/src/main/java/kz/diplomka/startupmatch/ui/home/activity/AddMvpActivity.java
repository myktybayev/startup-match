package kz.diplomka.startupmatch.ui.home.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.data.local.AppDatabase;
import kz.diplomka.startupmatch.data.local.entity.ProjectEntity;
import kz.diplomka.startupmatch.ui.home.navigation.ProjectFlowExtras;

import java.util.Date;

public class AddMvpActivity extends AppCompatActivity {

    private long projectId = -1L;

    private EditText editMvpLink;
    private MaterialButton buttonSaveProject;
    private TextView textCurrentVersionDate;
    private TextView textMvpLastChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_mvp);

        projectId = getIntent().getLongExtra(ProjectFlowExtras.EXTRA_PROJECT_ID, -1L);
        if (projectId <= 0) {
            ProjectEntity latest = AppDatabase.get(this).projectDao().getLatest();
            if (latest != null) {
                projectId = latest.getId();
            }
        }

        ImageView buttonBack = findViewById(R.id.buttonBack);
        editMvpLink = findViewById(R.id.editMvpLink);
        buttonSaveProject = findViewById(R.id.buttonSaveProject);
        textCurrentVersionDate = findViewById(R.id.currentVersion);
        textMvpLastChange = findViewById(R.id.textMvpLastChange);

        buttonBack.setOnClickListener(v -> finish());

        loadExistingLinkIfAny();

        buttonSaveProject.setOnClickListener(v -> {
            if (projectId <= 0) {
                Toast.makeText(this, getString(R.string.error_no_project_for_mvp), Toast.LENGTH_SHORT).show();
                return;
            }
            String link = editMvpLink.getText() == null ? "" : editMvpLink.getText().toString().trim();
            if (!isHttpUrlValid(link)) {
                editMvpLink.setError(getString(R.string.add_mvp_link_invalid));
                Toast.makeText(this, getString(R.string.add_mvp_link_invalid), Toast.LENGTH_SHORT).show();
                return;
            }
            long now = System.currentTimeMillis();
            AppDatabase.get(this).projectDao().updateMvpLink(projectId, link, now, now);
            Toast.makeText(this, getString(R.string.add_mvp_saved), Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        });
    }

    private void loadExistingLinkIfAny() {
        if (projectId <= 0) {
            return;
        }
        ProjectEntity project = AppDatabase.get(this).projectDao().getById(projectId);
        if (project == null) {
            return;
        }
        if (!TextUtils.isEmpty(project.getMvpLink())) {
            editMvpLink.setText(project.getMvpLink());
        }
        applyCurrentVersionUi(resolveMvpSavedMillis(project));
    }

    /**
     * «Қазіргі нұсқа» күнін және «Соңғы өзгеріс» уақытын көрсету (mvp_saved_at).
     */
    private void applyCurrentVersionUi(long millis) {
        if (millis <= 0) {
            textCurrentVersionDate.setText(getString(R.string.add_mvp_ver_date_placeholder));
            textMvpLastChange.setText(getString(R.string.add_mvp_ver_last_change_placeholder));
            return;
        }
        Date d = new Date(millis);
        String dateStr = DateFormat.getMediumDateFormat(this).format(d);
        String timeStr = DateFormat.getTimeFormat(this).format(d);
        textCurrentVersionDate.setText(dateStr);
        textMvpLastChange.setText(getString(R.string.add_mvp_ver_last_change_format, timeStr));
    }

    private long resolveMvpSavedMillis(@NonNull ProjectEntity project) {
        Long mvpSaved = project.getMvpSavedAt();
        if (mvpSaved != null && mvpSaved > 0) {
            return mvpSaved;
        }
        if (!TextUtils.isEmpty(project.getMvpLink())) {
            return project.getUpdatedAt();
        }
        return 0L;
    }

    private boolean isHttpUrlValid(String link) {
        if (TextUtils.isEmpty(link)) {
            return false;
        }
        String t = link.trim();
        if (!t.startsWith("http://") && !t.startsWith("https://")) {
            return false;
        }
        try {
            android.net.Uri u = android.net.Uri.parse(t);
            return u.getHost() != null && !u.getHost().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}
