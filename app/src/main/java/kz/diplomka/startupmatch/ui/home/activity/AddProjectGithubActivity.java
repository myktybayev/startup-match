package kz.diplomka.startupmatch.ui.home.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddProjectGithubActivity extends AppCompatActivity {

    private static final Pattern GITHUB_REPO =
            Pattern.compile("github\\.com/([^/]+)/([^/#?]+)", Pattern.CASE_INSENSITIVE);

    private long projectId = -1L;

    private EditText editGithubLink;
    private TextView textRepoTitle;
    private TextView textRepoUrl;
    private TextView textSyncBadge;
    private TextView textRepoBranchMeta;
    private MaterialButton buttonRefreshData;
    private MaterialButton buttonSaveProject;
    private View layoutRepoPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project_github);

        projectId = getIntent().getLongExtra(ProjectFlowExtras.EXTRA_PROJECT_ID, -1L);
        if (projectId <= 0) {
            ProjectEntity latest = AppDatabase.get(this).projectDao().getLatest();
            if (latest != null) {
                projectId = latest.getId();
            }
        }

        bindViews();
        setupActions();
        loadExistingLinkIfAny();
    }

    private void loadExistingLinkIfAny() {
        if (projectId <= 0) {
            resetPreviewUi();
            return;
        }
        ProjectEntity project = AppDatabase.get(this).projectDao().getById(projectId);
        if (project == null) {
            resetPreviewUi();
            return;
        }
        if (!TextUtils.isEmpty(project.getGithubLink())) {
            editGithubLink.setText(project.getGithubLink());
            applyPreviewFromUrl();
        } else {
            resetPreviewUi();
        }
    }

    private void bindViews() {
        ImageView buttonBack = findViewById(R.id.buttonBack);
        editGithubLink = findViewById(R.id.editGithubLink);
        textRepoTitle = findViewById(R.id.textRepoTitle);
        textRepoUrl = findViewById(R.id.textRepoUrl);
        textSyncBadge = findViewById(R.id.textSyncBadge);
        textRepoBranchMeta = findViewById(R.id.textRepoBranchMeta);
        buttonRefreshData = findViewById(R.id.buttonRefreshData);
        buttonSaveProject = findViewById(R.id.buttonSaveProject);
        layoutRepoPreview = findViewById(R.id.layoutRepoPreview);

        buttonBack.setOnClickListener(v -> finish());
    }

    private void setupActions() {
        buttonRefreshData.setOnClickListener(v -> {
            String url = editGithubLink.getText() == null ? "" : editGithubLink.getText().toString().trim();
            if (!isGithubUrlValid(url)) {
                editGithubLink.setError(getString(R.string.add_github_link_invalid));
                Toast.makeText(this, getString(R.string.add_github_link_invalid), Toast.LENGTH_SHORT).show();
                return;
            }
            editGithubLink.setError(null);
            applyPreviewFromUrl();
            Toast.makeText(this, getString(R.string.add_github_preview_updated), Toast.LENGTH_SHORT).show();
        });

        buttonSaveProject.setOnClickListener(v -> {
            if (projectId <= 0) {
                Toast.makeText(this, getString(R.string.error_no_project_for_github), Toast.LENGTH_SHORT).show();
                return;
            }
            String url = editGithubLink.getText() == null ? "" : editGithubLink.getText().toString().trim();
            if (!isGithubUrlValid(url)) {
                editGithubLink.setError(getString(R.string.add_github_link_invalid));
                Toast.makeText(this, getString(R.string.add_github_link_invalid), Toast.LENGTH_SHORT).show();
                return;
            }
            long now = System.currentTimeMillis();
            AppDatabase.get(this).projectDao().updateGithubLink(projectId, url, now);
            Toast.makeText(this, getString(R.string.add_github_saved), Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        });

        layoutRepoPreview.setOnClickListener(v -> openLastEnteredGithubLink());
    }

    private void openLastEnteredGithubLink() {
        String fromField = editGithubLink.getText() == null ? "" : editGithubLink.getText().toString().trim();
        String link = "";
        if (isGithubUrlValid(fromField)) {
            link = fromField;
        } else if (projectId > 0) {
            ProjectEntity p = AppDatabase.get(this).projectDao().getById(projectId);
            if (p != null && !TextUtils.isEmpty(p.getGithubLink())
                    && isGithubUrlValid(p.getGithubLink().trim())) {
                link = p.getGithubLink().trim();
            }
        }
        if (TextUtils.isEmpty(link)) {
            Toast.makeText(this, getString(R.string.add_github_open_no_link), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(link)));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, getString(R.string.add_github_open_failed), Toast.LENGTH_SHORT).show();
        }
    }

    private void resetPreviewUi() {
        textRepoTitle.setText(getString(R.string.add_github_preview_repo_placeholder));
        textRepoUrl.setText(getString(R.string.add_github_preview_url_placeholder));
        textSyncBadge.setVisibility(View.GONE);
        textRepoBranchMeta.setText(getString(R.string.add_github_branch_meta_placeholder));
    }

    private void applyPreviewFromUrl() {
        String url = editGithubLink.getText() == null ? "" : editGithubLink.getText().toString().trim();
        Matcher m = GITHUB_REPO.matcher(url);
        if (m.find() && isGithubUrlValid(url)) {
            String org = m.group(1);
            String repo = m.group(2);
            textRepoTitle.setText(org + " / " + repo);
            textRepoUrl.setText("github.com/" + org + "/" + repo);
            textSyncBadge.setVisibility(View.VISIBLE);
            textRepoBranchMeta.setText(getString(R.string.add_github_branch_meta_format, "main", "—"));
        } else {
            resetPreviewUi();
        }
    }

    private boolean isGithubUrlValid(String link) {
        if (TextUtils.isEmpty(link)) {
            return false;
        }
        String t = link.trim();
        if (!t.startsWith("http://") && !t.startsWith("https://")) {
            return false;
        }
        if (!t.toLowerCase().contains("github.com")) {
            return false;
        }
        return GITHUB_REPO.matcher(t).find();
    }
}
