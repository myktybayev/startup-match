package kz.diplomka.startupmatch.ui.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;

import de.hdodenhof.circleimageview.CircleImageView;
import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.data.local.AppDatabase;
import kz.diplomka.startupmatch.data.local.entity.ProjectEntity;
import kz.diplomka.startupmatch.ui.home.command.LinkedInvestorsFragment;
import kz.diplomka.startupmatch.ui.home.command.TeamMembersFragment;
import kz.diplomka.startupmatch.ui.home.navigation.ProjectFlowExtras;

public class CommandListActivity extends AppCompatActivity {

    private static final String TAG_TEAM = "team_members";
    private static final String TAG_INVESTORS = "linked_investors";

    private long projectId = -1L;

    private TextView textTeamName;
    private TextView textTeamMeta;
    private CircleImageView imageTeamAvatar;
    private TextView chipPitch;
    private ImageView buttonAddMember;
    private TabLayout tabLayout;

    private TeamMembersFragment teamMembersFragment;
    private LinkedInvestorsFragment linkedInvestorsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command_list);

        projectId = getIntent().getLongExtra(ProjectFlowExtras.EXTRA_PROJECT_ID, -1L);
        if (projectId <= 0) {
            ProjectEntity latest = AppDatabase.get(this).projectDao().getLatest();
            if (latest != null) {
                projectId = latest.getId();
            }
        }

        bindViews();
        setupHeader();
        refreshTeamHeaderMeta();
        setupTabsAndFragments(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupHeader();
        refreshTeamHeaderMeta();
    }

    private void bindViews() {
        textTeamName = findViewById(R.id.textTeamName);
        textTeamMeta = findViewById(R.id.textTeamMeta);
        imageTeamAvatar = findViewById(R.id.imageTeamAvatar);
        chipPitch = findViewById(R.id.chipPitch);
        buttonAddMember = findViewById(R.id.buttonAddMember);
        tabLayout = findViewById(R.id.tabLayoutCommand);

        FrameLayout buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> finish());
        buttonAddMember.setOnClickListener(v -> addCommandMember());
    }

    private void addCommandMember() {
        Intent intent = new Intent(this, AddCommandActivity.class);
        intent.putExtra(ProjectFlowExtras.EXTRA_PROJECT_ID, projectId);
        startActivity(intent);
    }

    /**
     * Тақырып жəне pitch чипі (TeamMembersFragment мазмұнынан бөлек).
     */
    public void setupHeader() {
        if (projectId <= 0) {
            textTeamName.setText(getString(R.string.home_team));
            chipPitch.setVisibility(View.GONE);
            return;
        }

        ProjectEntity project = AppDatabase.get(this).projectDao().getById(projectId);
        if (project == null) {
            Toast.makeText(this, getString(R.string.error_invalid_project), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        textTeamName.setText(project.getName());
        boolean hasPitch = !TextUtils.isEmpty(project.getPitchDriveLink());
        chipPitch.setVisibility(hasPitch ? View.VISIBLE : View.GONE);
    }

    /**
     * Карточкадағы мүше санау метасы (TeamMembersFragment жаңарғанда шақырылады).
     */
    public void refreshTeamHeaderMeta() {
        if (textTeamMeta == null) {
            return;
        }
        if (projectId <= 0) {
            textTeamMeta.setText(getString(R.string.command_list_team_meta_placeholder));
            return;
        }
        int n = AppDatabase.get(this).teamMemberDao().countForProject(projectId);
        if (n <= 0) {
            textTeamMeta.setText(getString(R.string.command_list_team_meta_placeholder));
        } else {
            textTeamMeta.setText(buildMeta(n));
        }
    }

    private String buildMeta(int membersCount) {
        return membersCount + " мүше · Early-stage startup · Investor-ready профиль";
    }

    private void setupTabsAndFragments(@Nullable Bundle savedInstanceState) {
        tabLayout.removeAllTabs();
        tabLayout.addTab(tabLayout.newTab().setText(R.string.command_list_tab_team));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.command_list_tab_investors));

        if (savedInstanceState == null) {
            teamMembersFragment = TeamMembersFragment.newInstance(projectId);
            linkedInvestorsFragment = LinkedInvestorsFragment.newInstance(projectId);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainerCommand, teamMembersFragment, TAG_TEAM)
                    .add(R.id.fragmentContainerCommand, linkedInvestorsFragment, TAG_INVESTORS)
                    .hide(linkedInvestorsFragment)
                    .commit();
        } else {
            teamMembersFragment = (TeamMembersFragment) getSupportFragmentManager().findFragmentByTag(TAG_TEAM);
            linkedInvestorsFragment = (LinkedInvestorsFragment) getSupportFragmentManager().findFragmentByTag(TAG_INVESTORS);
            if (teamMembersFragment == null || linkedInvestorsFragment == null) {
                teamMembersFragment = TeamMembersFragment.newInstance(projectId);
                linkedInvestorsFragment = LinkedInvestorsFragment.newInstance(projectId);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragmentContainerCommand, teamMembersFragment, TAG_TEAM)
                        .add(R.id.fragmentContainerCommand, linkedInvestorsFragment, TAG_INVESTORS)
                        .hide(linkedInvestorsFragment)
                        .commit();
            }
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switchCommandTab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void switchCommandTab(int position) {
        if (teamMembersFragment == null || linkedInvestorsFragment == null) {
            return;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (position == 0) {
            ft.show(teamMembersFragment).hide(linkedInvestorsFragment);
        } else {
            ft.hide(teamMembersFragment).show(linkedInvestorsFragment);
        }
        ft.commit();
    }
}
