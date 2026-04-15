package kz.diplomka.startupmatch.ui.investor_role;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import org.json.JSONArray;

import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.data.local.entity.InvestorChallengeEntity;
import kz.diplomka.startupmatch.data.local.session.AddChallengeDraftPrefs;
import kz.diplomka.startupmatch.ui.сhallenges.data.ChallengesRepository;
import kz.diplomka.startupmatch.databinding.ActivityAddNewChallengeBinding;
import kz.diplomka.startupmatch.databinding.DialogAddRequirementBinding;
import kz.diplomka.startupmatch.databinding.ItemChallengeRequirementDynamicBinding;

public class AddNewChallengeActivity extends AppCompatActivity {

    public static final String EXTRA_INVESTOR_NAME = "extra.add_challenge.investor_name";
    public static final String EXTRA_INVESTOR_PHOTO_URI = "extra.add_challenge.investor_photo_uri";

    private static final String STATE_DEADLINE_MS = "state.add_challenge.deadline_ms";
    private static final String STATE_SELECTED_TAGS = "state.add_challenge.selected_tags";
    private static final String STATE_CUSTOM_REQUIREMENTS = "state.add_challenge.custom_requirements";
    private static final String STATE_REWARD = "state.add_challenge.reward";

    private static final int REWARD_NONE = 0;
    private static final int REWARD_INVESTMENT = 1;
    private static final int REWARD_PILOT = 2;

    @Nullable
    private Calendar deadlineCalendar;

    private int rewardSelection = REWARD_NONE;
    @NonNull
    private final LinkedHashSet<String> selectedTagKeys = new LinkedHashSet<>();

    @NonNull
    private final ArrayList<String> customRequirements = new ArrayList<>();

    @NonNull
    public static Intent newIntent(
            @NonNull Context context,
            @Nullable String investorDisplayName,
            @Nullable String investorPhotoUriString
    ) {
        Intent i = new Intent(context, AddNewChallengeActivity.class);
        if (!TextUtils.isEmpty(investorDisplayName)) {
            i.putExtra(EXTRA_INVESTOR_NAME, investorDisplayName.trim());
        }
        if (!TextUtils.isEmpty(investorPhotoUriString)) {
            i.putExtra(EXTRA_INVESTOR_PHOTO_URI, investorPhotoUriString.trim());
        }
        return i;
    }

    private ActivityAddNewChallengeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNewChallengeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bindInvestorHeader();

        deadlineCalendar = Calendar.getInstance();
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_DEADLINE_MS)) {
            deadlineCalendar.setTimeInMillis(savedInstanceState.getLong(STATE_DEADLINE_MS));
            binding.textDeadlineValue.setText(formatDeadlineLabel(deadlineCalendar));
        } else {
            syncDeadlineCalendarFromCurrentLabel();
            ensureDeadlineNotInPast();
        }

        restoreSelectedTags(savedInstanceState);
        restoreCustomRequirements(savedInstanceState);
        if (savedInstanceState != null) {
            rewardSelection = savedInstanceState.getInt(STATE_REWARD, REWARD_NONE);
        }
        setupChallengeTagChips();
        setupRewardRows();
        applyRewardVisual();

        binding.buttonBack.setOnClickListener(v -> finish());
        binding.rowDeadline.setOnClickListener(v -> showDeadlineDatePicker());
        binding.textDeadlineValue.setOnClickListener(v -> showDeadlineDatePicker());
        binding.rowAddRequirement.setOnClickListener(v -> showAddRequirementDialog());
        binding.addRequirement.setOnClickListener(v -> showAddRequirementDialog());
        binding.buttonPublish.setOnClickListener(v -> publishChallengeDraft());
    }

    @NonNull
    public Set<String> getSelectedChallengeTagKeys() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(selectedTagKeys));
    }

    private void publishChallengeDraft() {
        String error = validateForm();
        if (error != null) {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            return;
        }
        String title = binding.editChallengeTitle.getText() != null
                ? binding.editChallengeTitle.getText().toString().trim()
                : "";
        String description = binding.editDescription.getText() != null
                ? binding.editDescription.getText().toString().trim()
                : "";
        String teamFit = binding.editTeamFit.getText() != null
                ? binding.editTeamFit.getText().toString().trim()
                : "";
        String deadlineLabel = deadlineCalendar != null
                ? formatDeadlineLabel(deadlineCalendar)
                : "";
        long deadlineMs = deadlineCalendar != null ? deadlineCalendar.getTimeInMillis() : System.currentTimeMillis();

        JSONArray reqJson = new JSONArray();
        for (String r : customRequirements) {
            reqJson.put(r);
        }

        String rewardType = rewardSelection == REWARD_INVESTMENT
                ? InvestorChallengeEntity.REWARD_INVESTMENT
                : InvestorChallengeEntity.REWARD_PILOT;

        String filterType = InvestorChallengeTypeMapper.fromTagKeys(selectedTagKeys).name();

        String investorName = getIntent().getStringExtra(EXTRA_INVESTOR_NAME);
        if (TextUtils.isEmpty(investorName)) {
            investorName = getString(R.string.investor_name_role);
        } else {
            investorName = investorName.trim();
        }
        String investorRole = getString(R.string.add_new_challenge_investor_role);
        String photoUri = getIntent().getStringExtra(EXTRA_INVESTOR_PHOTO_URI);
        if (photoUri != null) {
            photoUri = photoUri.trim();
        }

        InvestorChallengeEntity entity = new InvestorChallengeEntity(
                title,
                description,
                teamFit,
                deadlineMs,
                deadlineLabel,
                joinTagKeys(selectedTagKeys),
                reqJson.toString(),
                rewardType,
                filterType,
                investorName,
                investorRole,
                TextUtils.isEmpty(photoUri) ? null : photoUri,
                getString(R.string.challenge_detail_seed_badge),
                System.currentTimeMillis()
        );

        new ChallengesRepository(this).insertInvestorChallenge(entity);
        AddChallengeDraftPrefs.clearSelectedTagKeys(this);
        Toast.makeText(this, R.string.add_new_challenge_toast_published_success, Toast.LENGTH_SHORT).show();
        finish();
    }

    @NonNull
    private String joinTagKeys(@NonNull LinkedHashSet<String> keys) {
        StringBuilder sb = new StringBuilder();
        for (String k : keys) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(k);
        }
        return sb.toString();
    }

    @Nullable
    private String validateForm() {
        String title = binding.editChallengeTitle.getText() != null
                ? binding.editChallengeTitle.getText().toString().trim()
                : "";
        if (title.length() < 3) {
            return getString(R.string.add_new_challenge_error_title);
        }
        String description = binding.editDescription.getText() != null
                ? binding.editDescription.getText().toString().trim()
                : "";
        if (description.length() < 20) {
            return getString(R.string.add_new_challenge_error_description);
        }
        if (selectedTagKeys.isEmpty()) {
            return getString(R.string.add_new_challenge_error_tags);
        }
        String teamFit = binding.editTeamFit.getText() != null
                ? binding.editTeamFit.getText().toString().trim()
                : "";
        if (teamFit.length() < 10) {
            return getString(R.string.add_new_challenge_error_team_fit);
        }
        if (customRequirements.isEmpty()) {
            return getString(R.string.add_new_challenge_error_requirements);
        }
        if (rewardSelection == REWARD_NONE) {
            return getString(R.string.add_new_challenge_error_reward);
        }
        if (deadlineCalendar == null) {
            return getString(R.string.add_new_challenge_error_deadline_past);
        }
        Calendar today = Calendar.getInstance();
        stripTime(today);
        Calendar d = (Calendar) deadlineCalendar.clone();
        stripTime(d);
        if (d.before(today)) {
            return getString(R.string.add_new_challenge_error_deadline_past);
        }
        return null;
    }

    private static void stripTime(@NonNull Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    private void setupRewardRows() {
        binding.rowRewardInvestment.setOnClickListener(v -> {
            rewardSelection = REWARD_INVESTMENT;
            applyRewardVisual();
        });
        binding.rowRewardPilot.setOnClickListener(v -> {
            rewardSelection = REWARD_PILOT;
            applyRewardVisual();
        });
    }

    private void applyRewardVisual() {
        int invBg = rewardSelection == REWARD_INVESTMENT
                ? R.drawable.bg_add_challenge_chip_primary
                : R.drawable.bg_add_challenge_field;
        int pilBg = rewardSelection == REWARD_PILOT
                ? R.drawable.bg_add_challenge_chip_primary
                : R.drawable.bg_add_challenge_field;
        binding.rowRewardInvestment.setBackgroundResource(invBg);
        binding.rowRewardPilot.setBackgroundResource(pilBg);
    }

    private void restoreSelectedTags(@Nullable Bundle savedInstanceState) {
        selectedTagKeys.clear();
        if (savedInstanceState != null) {
            ArrayList<String> arr = savedInstanceState.getStringArrayList(STATE_SELECTED_TAGS);
            if (arr != null) {
                for (String k : arr) {
                    if (isKnownTagKey(k)) {
                        selectedTagKeys.add(k);
                    }
                }
                return;
            }
        }
        for (String k : AddChallengeDraftPrefs.getSelectedTagKeys(this)) {
            if (isKnownTagKey(k)) {
                selectedTagKeys.add(k);
            }
        }
    }

    private static boolean isKnownTagKey(@Nullable String k) {
        if (k == null || k.isEmpty()) {
            return false;
        }
        for (String known : ChallengeTagKeys.allKeys()) {
            if (known.equals(k)) {
                return true;
            }
        }
        return false;
    }

    private void setupChallengeTagChips() {
        bindTagView(binding.tagChallengeFintech, ChallengeTagKeys.FINTECH);
        bindTagView(binding.tagChallengeWeb, ChallengeTagKeys.WEB);
        bindTagView(binding.tagChallengeApi, ChallengeTagKeys.API);
        bindTagView(binding.tagChallengeB2b, ChallengeTagKeys.B2B);
        bindTagView(binding.tagChallengePayments, ChallengeTagKeys.PAYMENTS);
    }

    private void bindTagView(@NonNull TextView view, @NonNull String key) {
        applyTagVisual(view, selectedTagKeys.contains(key));
        view.setOnClickListener(v -> toggleTag(key, view));
    }

    private void toggleTag(@NonNull String key, @NonNull TextView view) {
        if (selectedTagKeys.contains(key)) {
            selectedTagKeys.remove(key);
        } else {
            selectedTagKeys.add(key);
        }
        applyTagVisual(view, selectedTagKeys.contains(key));
        AddChallengeDraftPrefs.setSelectedTagKeys(this, selectedTagKeys);
    }

    private void applyTagVisual(@NonNull TextView view, boolean selected) {
        if (selected) {
            view.setBackgroundResource(R.drawable.bg_add_challenge_chip_primary);
            view.setTextColor(ContextCompat.getColor(this, R.color.investor_chip_text));
        } else {
            view.setBackgroundResource(R.drawable.bg_command_tag_pill);
            view.setTextColor(ContextCompat.getColor(this, R.color.investor_title));
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (deadlineCalendar != null) {
            outState.putLong(STATE_DEADLINE_MS, deadlineCalendar.getTimeInMillis());
        }
        outState.putStringArrayList(STATE_SELECTED_TAGS, new ArrayList<>(selectedTagKeys));
        outState.putStringArrayList(STATE_CUSTOM_REQUIREMENTS, new ArrayList<>(customRequirements));
        outState.putInt(STATE_REWARD, rewardSelection);
    }

    private void showDeadlineDatePicker() {
        if (deadlineCalendar == null) {
            deadlineCalendar = Calendar.getInstance();
        }
        int y = deadlineCalendar.get(Calendar.YEAR);
        int m = deadlineCalendar.get(Calendar.MONTH);
        int d = deadlineCalendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    deadlineCalendar.set(Calendar.YEAR, year);
                    deadlineCalendar.set(Calendar.MONTH, monthOfYear);
                    deadlineCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    deadlineCalendar.set(Calendar.HOUR_OF_DAY, 0);
                    deadlineCalendar.set(Calendar.MINUTE, 0);
                    deadlineCalendar.set(Calendar.SECOND, 0);
                    deadlineCalendar.set(Calendar.MILLISECOND, 0);
                    binding.textDeadlineValue.setText(formatDeadlineLabel(deadlineCalendar));
                },
                y,
                m,
                d
        );
        dialog.setTitle(getString(R.string.add_new_challenge_deadline_picker_title));
        dialog.show();
    }

    private void syncDeadlineCalendarFromCurrentLabel() {
        if (deadlineCalendar == null) {
            deadlineCalendar = Calendar.getInstance();
        }
        CharSequence label = binding.textDeadlineValue.getText();
        if (label == null) {
            return;
        }
        String s = label.toString().trim();
        if (s.isEmpty()) {
            return;
        }
        try {
            SimpleDateFormat df = new SimpleDateFormat("d MMMM", new Locale("kk", "KZ"));
            df.setLenient(false);
            Date parsed = df.parse(s);
            if (parsed != null) {
                Calendar parsedCal = Calendar.getInstance();
                parsedCal.setTime(parsed);
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                parsedCal.set(Calendar.YEAR, currentYear);
                deadlineCalendar.setTimeInMillis(parsedCal.getTimeInMillis());
            }
        } catch (ParseException ignored) {
            // layout мәнін парселемесек, бүгінгі күн қалдырамыз
        }
    }

    /** Жариялау валидациясы үшін deadline бүгіннен бұрын болмауы керек. */
    private void ensureDeadlineNotInPast() {
        if (deadlineCalendar == null) {
            deadlineCalendar = Calendar.getInstance();
        }
        Calendar today = Calendar.getInstance();
        stripTime(today);
        Calendar d = (Calendar) deadlineCalendar.clone();
        stripTime(d);
        if (d.before(today)) {
            deadlineCalendar = Calendar.getInstance();
            deadlineCalendar.add(Calendar.DAY_OF_MONTH, 14);
            deadlineCalendar.set(Calendar.HOUR_OF_DAY, 0);
            deadlineCalendar.set(Calendar.MINUTE, 0);
            deadlineCalendar.set(Calendar.SECOND, 0);
            deadlineCalendar.set(Calendar.MILLISECOND, 0);
            binding.textDeadlineValue.setText(formatDeadlineLabel(deadlineCalendar));
        }
    }

    @NonNull
    private static String formatDeadlineLabel(@NonNull Calendar cal) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("d MMMM", new Locale("kk", "KZ"));
            return df.format(cal.getTime());
        } catch (Exception e) {
            DateFormat fb = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
            return fb.format(cal.getTime());
        }
    }

    private void restoreCustomRequirements(@Nullable Bundle savedInstanceState) {
        customRequirements.clear();
        binding.requirementList.removeAllViews();
        if (savedInstanceState != null) {
            ArrayList<String> arr = savedInstanceState.getStringArrayList(STATE_CUSTOM_REQUIREMENTS);
            if (arr != null) {
                for (String s : arr) {
                    if (!TextUtils.isEmpty(s)) {
                        customRequirements.add(s.trim());
                    }
                }
            }
        }
        for (String text : customRequirements) {
            addRequirementRow(text);
        }
    }

    private void showAddRequirementDialog() {
        DialogAddRequirementBinding dialogBinding = DialogAddRequirementBinding.inflate(getLayoutInflater());
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogBinding.getRoot())
                .create();
        dialogBinding.buttonRequirementCancel.setOnClickListener(v -> dialog.dismiss());
        dialogBinding.buttonRequirementSave.setOnClickListener(v -> {
            CharSequence cs = dialogBinding.editRequirementText.getText();
            String text = cs != null ? cs.toString().trim() : "";
            if (TextUtils.isEmpty(text)) {
                Toast.makeText(this, R.string.add_new_challenge_requirement_empty, Toast.LENGTH_SHORT).show();
                return;
            }
            customRequirements.add(text);
            addRequirementRow(text);
            dialog.dismiss();
        });
        dialog.show();
    }

    private void addRequirementRow(@NonNull String text) {
        ItemChallengeRequirementDynamicBinding rowBinding = ItemChallengeRequirementDynamicBinding.inflate(
                getLayoutInflater(), binding.requirementList, false);
        rowBinding.textRequirementBody.setText(text);
        binding.requirementList.addView(rowBinding.getRoot());
    }

    private void bindInvestorHeader() {
        String name = getIntent().getStringExtra(EXTRA_INVESTOR_NAME);
        if (TextUtils.isEmpty(name)) {
            name = getString(R.string.investor_name_role);
        }
        binding.investorName.setText(name);

        String uriStr = getIntent().getStringExtra(EXTRA_INVESTOR_PHOTO_URI);
        if (!TextUtils.isEmpty(uriStr)) {
            try {
                binding.investorPhoto.setImageURI(Uri.parse(uriStr));
            } catch (Exception ignored) {
                binding.investorPhoto.setImageResource(R.drawable.figma_add_challenge_investor_avatar);
            }
        } else {
            binding.investorPhoto.setImageResource(R.drawable.figma_add_challenge_investor_avatar);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
