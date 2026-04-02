package kz.diplomka.startupmatch.ui.home.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import kz.diplomka.startupmatch.MainActivity;
import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.data.local.AppDatabase;
import kz.diplomka.startupmatch.data.local.entity.ProjectEntity;
import kz.diplomka.startupmatch.ui.home.data.repository.AddCommandRepository;
import kz.diplomka.startupmatch.ui.home.module.CommandMemberData;
import kz.diplomka.startupmatch.ui.home.navigation.ProjectFlowExtras;

public class AddCommandActivity extends AppCompatActivity {

    private static final int PICK_MEMBER_AVATAR_REQUEST = 2001;

    private AddCommandRepository repository;

    private TextView textMemberTitle;
    private EditText editMemberName;
    private Spinner spinnerRole;
    private View roleDropdownContainer;
    private EditText editExperience;
    private EditText editPortfolio;
    private HorizontalScrollView scrollMembers;
    private LinearLayout layoutMembersRow;
    private MaterialButton buttonAddMember;
    private MaterialButton buttonSaveProject;

    private ImageView imageMemberAvatar;
    private String selectedAvatarUri;

    private int currentMemberIndex = 1;

    private long projectId = -1L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_command);

        projectId = getIntent().getLongExtra(ProjectFlowExtras.EXTRA_PROJECT_ID, -1L);
        if (projectId <= 0) {
            Toast.makeText(this, getString(R.string.error_invalid_project), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        ProjectEntity project = AppDatabase.get(this).projectDao().getById(projectId);
        if (project == null) {
            Toast.makeText(this, getString(R.string.error_invalid_project), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        repository = new AddCommandRepository(this, projectId);
        bindViews();
        setupRoleDropdown();
        refreshMembersRow();
        setupActions();
    }

    private void bindViews() {
        ImageView buttonBack = findViewById(R.id.buttonBack);
        textMemberTitle = findViewById(R.id.textMemberTitle);
        editMemberName = findViewById(R.id.editMemberName);
        spinnerRole = findViewById(R.id.spinnerRole);
        roleDropdownContainer = findViewById(R.id.roleDropdownContainer);
        editExperience = findViewById(R.id.editExperience);
        editPortfolio = findViewById(R.id.editPortfolio);
        imageMemberAvatar = findViewById(R.id.imageMemberAvatar);
        scrollMembers = findViewById(R.id.scrollMembers);
        layoutMembersRow = findViewById(R.id.layoutMembersRow);
        buttonAddMember = findViewById(R.id.buttonAddMember);
        buttonSaveProject = findViewById(R.id.buttonSaveProject);

        buttonBack.setOnClickListener(v -> finish());
        textMemberTitle.setText(getString(R.string.add_command_member_format, currentMemberIndex));

        // Selecting avatar from gallery.
        imageMemberAvatar.setOnClickListener(v -> openGalleryPicker());
    }

    private void setupRoleDropdown() {
        List<String> roleOptions = repository.getRoleOptions();

        // Placeholder as 0th item to detect "not selected" state.
        String placeholder = getString(R.string.add_command_member_role_hint);
        List<String> items = new java.util.ArrayList<>();
        items.add(placeholder);
        items.addAll(roleOptions);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                items
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);
        spinnerRole.setSelection(0);
    }

    private void refreshMembersRow() {
        layoutMembersRow.removeAllViews();
        LayoutInflater inflater = getLayoutInflater();
        for (CommandMemberData member : repository.getMembers()) {
            View card = inflater.inflate(R.layout.item_command_member, layoutMembersRow, false);
            CommandMembersAdapter.bindMemberCard(card, member);
            layoutMembersRow.addView(card);
        }
        View addCard = inflater.inflate(R.layout.item_command_member_add, layoutMembersRow, false);
        layoutMembersRow.addView(addCard);
        layoutMembersRow.post(() -> scrollMembers.fullScroll(View.FOCUS_RIGHT));
    }

    private void setupActions() {
        buttonAddMember.setOnClickListener(v -> {
            CommandMemberData memberData = readCurrentMember();
            validateRequiredFields(memberData);
            if (repository.isMemberValid(memberData)) {
                repository.addMember(memberData);
                refreshMembersRow();
                currentMemberIndex++;
                resetFormForNextMember();
                Toast.makeText(this, getString(R.string.add_command_member_added), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.add_command_required_error), Toast.LENGTH_SHORT).show();
            }
        });

        buttonSaveProject.setOnClickListener(v -> {
            if (repository.getMemberCount() == 0) {
                Toast.makeText(this, getString(R.string.add_command_save_no_members), Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(
                    this,
                    getString(R.string.add_command_project_saved, repository.getMemberCount()),
                    Toast.LENGTH_SHORT
            ).show();
            Intent main = new Intent(this, MainActivity.class);
//            pitch.putExtra(ProjectFlowExtras.EXTRA_PROJECT_ID, projectId);
            startActivity(main);
        });
    }

    private CommandMemberData readCurrentMember() {
        String avatarUri = selectedAvatarUri;
        return new CommandMemberData(
                editMemberName.getText().toString(),
                getSelectedRoleOrEmpty(),
                editExperience.getText().toString(),
                editPortfolio.getText().toString(),
                avatarUri
        );
    }

    private void validateRequiredFields(CommandMemberData memberData) {
        setErrorIfEmpty(editMemberName, memberData.getFullName());
        setErrorIfEmpty(editExperience, memberData.getExperience());
        validateRoleField(memberData.getRole());
    }

    private void setErrorIfEmpty(EditText field, String value) {
        field.setError((value == null || value.trim().isEmpty())
                ? getString(R.string.add_command_field_required)
                : null);
    }

    private void resetFormForNextMember() {
        editMemberName.setText("");
        spinnerRole.setSelection(0);
        editExperience.setText("");
        editPortfolio.setText("");
        selectedAvatarUri = null;
        imageMemberAvatar.setImageResource(R.drawable.ic_dashboard_black_24dp);
        textMemberTitle.setText(getString(R.string.add_command_member_format, currentMemberIndex));
    }

    private void openGalleryPicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_MEMBER_AVATAR_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_MEMBER_AVATAR_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                String internalUri = persistAvatarToInternalStorage(uri);
                if (internalUri != null) {
                    selectedAvatarUri = internalUri;
                    imageMemberAvatar.setImageURI(Uri.parse(internalUri));
                } else {
                    selectedAvatarUri = null;
                    imageMemberAvatar.setImageResource(R.drawable.ic_dashboard_black_24dp);
                    Toast.makeText(this, getString(R.string.add_command_avatar_save_error), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String persistAvatarToInternalStorage(Uri sourceUri) {
        InputStream input = null;
        FileOutputStream output = null;
        try {
            input = getContentResolver().openInputStream(sourceUri);
            if (input == null) {
                return null;
            }

            File avatarsDir = new File(getFilesDir(), "member_avatars");
            if (!avatarsDir.exists() && !avatarsDir.mkdirs()) {
                return null;
            }

            String fileName = "avatar_" + System.currentTimeMillis() + ".jpg";
            File dest = new File(avatarsDir, fileName);
            output = new FileOutputStream(dest);

            byte[] buffer = new byte[8192];
            int len;
            while ((len = input.read(buffer)) > 0) {
                output.write(buffer, 0, len);
            }
            output.flush();
            return Uri.fromFile(dest).toString();
        } catch (Exception ignored) {
            return null;
        } finally {
            try {
                if (input != null) input.close();
            } catch (Exception ignored) {
            }
            try {
                if (output != null) output.close();
            } catch (Exception ignored) {
            }
        }
    }

    private void validateRoleField(String role) {
        boolean isValid = spinnerRole.getSelectedItemPosition() > 0 && role != null && !role.trim().isEmpty();
        roleDropdownContainer.setBackgroundResource(
                isValid ? R.drawable.bg_add_project_input : R.drawable.bg_add_project_input_error
        );
    }

    private String getSelectedRoleOrEmpty() {
        return spinnerRole.getSelectedItemPosition() > 0
                ? String.valueOf(spinnerRole.getSelectedItem())
                : "";
    }
}
