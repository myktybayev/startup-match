package kz.diplomka.startupmatch.ui.home.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.data.local.AppDatabase;
import kz.diplomka.startupmatch.data.local.entity.ProjectEntity;
import kz.diplomka.startupmatch.ui.home.navigation.ProjectFlowExtras;

public class AddPitchDeckActivity extends AppCompatActivity {

    private long projectId = -1L;

    private EditText editPitchLink;
    private TextView textLinkedFileName;
    private TextView textLinkedMeta;
    private MaterialButton buttonCheckLink;
    private MaterialButton buttonSaveProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pitch_deck);

        projectId = getIntent().getLongExtra(ProjectFlowExtras.EXTRA_PROJECT_ID, -1L);
        if (projectId <= 0) {
            ProjectEntity latest = AppDatabase.get(this).projectDao().getLatest();
            if (latest != null) {
                projectId = latest.getId();
            }
        }

        bindViews();
        setupActions();
        loadExistingPitchLinkIfAny();
    }

    private void loadExistingPitchLinkIfAny() {
        if (projectId <= 0) {
            return;
        }
        ProjectEntity project = AppDatabase.get(this).projectDao().getById(projectId);
        if (project != null && !TextUtils.isEmpty(project.getPitchDriveLink())) {
            editPitchLink.setText(project.getPitchDriveLink());
        }
    }

    private void bindViews() {
        ImageView buttonBack = findViewById(R.id.buttonBack);
        editPitchLink = findViewById(R.id.editPitchLink);
        textLinkedFileName = findViewById(R.id.textLinkedFileName);
        textLinkedMeta = findViewById(R.id.textLinkedMeta);
        buttonCheckLink = findViewById(R.id.buttonCheckLink);
        buttonSaveProject = findViewById(R.id.buttonSaveProject);

        buttonBack.setOnClickListener(v -> finish());
    }

    private void setupActions() {
        buttonCheckLink.setOnClickListener(v -> {
            String link = editPitchLink.getText() == null ? "" : editPitchLink.getText().toString().trim();
            if (isDriveLinkValid(link)) {
                editPitchLink.setError(null);
                textLinkedFileName.setText(extractFileName(link));
                textLinkedMeta.setText(getString(R.string.add_pitch_link_connected_meta));
                Toast.makeText(this, getString(R.string.add_pitch_link_connected), Toast.LENGTH_SHORT).show();
            } else {
                editPitchLink.setError(getString(R.string.add_pitch_link_invalid));
            }
        });

        buttonSaveProject.setOnClickListener(v -> {
            if (projectId <= 0) {
                Toast.makeText(this, getString(R.string.error_no_project_for_pitch), Toast.LENGTH_SHORT).show();
                return;
            }
            String link = editPitchLink.getText() == null ? "" : editPitchLink.getText().toString().trim();
            if (!isDriveLinkValid(link)) {
                editPitchLink.setError(getString(R.string.add_pitch_link_invalid));
                Toast.makeText(this, getString(R.string.add_pitch_link_invalid), Toast.LENGTH_SHORT).show();
                return;
            }
            long now = System.currentTimeMillis();
            AppDatabase.get(this).projectDao().updatePitchDriveLink(projectId, link, now);
            Toast.makeText(this, getString(R.string.add_pitch_saved), Toast.LENGTH_SHORT).show();
        });
    }

    private boolean isDriveLinkValid(String link) {
        return !TextUtils.isEmpty(link)
                && (link.startsWith("http://") || link.startsWith("https://"))
                && link.contains("drive.google.com");
    }

    private String extractFileName(String link) {
        if (TextUtils.isEmpty(link)) return getString(R.string.add_pitch_current_file_default);
        String normalized = link.endsWith("/") ? link.substring(0, link.length() - 1) : link;
        int lastSlash = normalized.lastIndexOf('/');
        if (lastSlash < 0 || lastSlash >= normalized.length() - 1) {
            return getString(R.string.add_pitch_current_file_default);
        }
        String raw = normalized.substring(lastSlash + 1);
        // Keep compact label similar to Figma.
        return raw.length() > 18 ? raw.substring(0, 18) + "..." : raw;
    }
}

