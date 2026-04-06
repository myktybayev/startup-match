package kz.diplomka.startupmatch.ui.investors;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.flexbox.FlexboxLayout;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.data.local.AppDatabase;
import kz.diplomka.startupmatch.data.local.entity.ProjectEntity;
import kz.diplomka.startupmatch.databinding.ActivityInvestorsCabinetBinding;
import kz.diplomka.startupmatch.ui.home.navigation.ProjectFlowExtras;

public class InvestorsCabinetActivity extends AppCompatActivity {

    /**
     * @param investorsList толық тізім (сүзгіден тыс контекст); болмаса null.
     */
    @NonNull
    public static Intent newIntent(
            @NonNull Context context,
            @NonNull InvestorListItem investor,
            @Nullable ArrayList<InvestorListItem> investorsList) {
        return newIntent(context, investor, investorsList, -1L);
    }

    /**
     * @param projectId ағымдағы жоба id; ≤0 болса, экранда соңғы жоба қолданылады.
     */
    @NonNull
    public static Intent newIntent(
            @NonNull Context context,
            @NonNull InvestorListItem investor,
            @Nullable ArrayList<InvestorListItem> investorsList,
            long projectId) {
        Intent i = new Intent(context, InvestorsCabinetActivity.class);
        i.putExtra(InvestorsCabinetExtras.EXTRA_INVESTOR, investor);
        if (investorsList != null) {
            i.putExtra(InvestorsCabinetExtras.EXTRA_INVESTORS_LIST, investorsList);
        }
        i.putExtra(ProjectFlowExtras.EXTRA_PROJECT_ID, projectId);
        return i;
    }

    private ActivityInvestorsCabinetBinding binding;
    private long projectId = -1L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInvestorsCabinetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        InvestorListItem investor = readInvestor();
        if (investor == null) {
            finish();
            return;
        }
        projectId = resolveProjectId();
        @SuppressWarnings("deprecation")
        ArrayList<InvestorListItem> list =
                (ArrayList<InvestorListItem>) getIntent().getSerializableExtra(
                        InvestorsCabinetExtras.EXTRA_INVESTORS_LIST);

        bindUi(investor);
        setupActions(investor, list != null ? list : new ArrayList<>());
    }

    private long resolveProjectId() {
        long id = getIntent().getLongExtra(ProjectFlowExtras.EXTRA_PROJECT_ID, -1L);
        if (id <= 0) {
            ProjectEntity latest = AppDatabase.get(this).projectDao().getLatest();
            if (latest != null) {
                id = latest.getId();
            }
        }
        return id;
    }

    @Nullable
    private InvestorListItem readInvestor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return getIntent().getSerializableExtra(InvestorsCabinetExtras.EXTRA_INVESTOR, InvestorListItem.class);
        }
        return (InvestorListItem) getIntent().getSerializableExtra(InvestorsCabinetExtras.EXTRA_INVESTOR);
    }

    private void bindUi(@NonNull InvestorListItem item) {
        binding.imageCabinetAvatar.setImageResource(item.avatarResId);
        binding.textCabinetName.setText(item.name);
        binding.textCabinetQuote.setText(item.quote);

        binding.badgeCabinetVerified.setVisibility(View.GONE);
        binding.badgeCabinetExperienced.setVisibility(View.GONE);
        binding.badgeCabinetGuest.setVisibility(View.GONE);
        switch (item.badge) {
            case VERIFIED:
                binding.badgeCabinetVerified.setVisibility(View.VISIBLE);
                break;
            case EXPERIENCED:
                binding.badgeCabinetExperienced.setVisibility(View.VISIBLE);
                break;
            case GUEST:
                binding.badgeCabinetGuest.setVisibility(View.VISIBLE);
                break;
        }

        if (item.challenges > 0) {
            binding.layoutCabinetBanner.setVisibility(View.VISIBLE);
            binding.textCabinetBanner.setText(getString(R.string.investors_cabinet_banner_response, item.challenges));
        } else {
            binding.layoutCabinetBanner.setVisibility(View.GONE);
        }

        binding.textCabinetStageValue.setText(buildStageLabel(item));
        binding.textCabinetTicketValue.setText(formatTicket(item));
        binding.textCabinetGeoValue.setText(TextUtils.join(", ", item.geoTags));
        bindSectorTags(item);
        binding.textCabinetAbout.setText(getString(R.string.investors_cabinet_about_body));

        binding.textCabinetStatViews.setText(String.valueOf(item.views));
        binding.textCabinetStatMessages.setText(String.valueOf(estimateMessages(item)));
        binding.textCabinetStatChallenges.setText(String.valueOf(item.challenges));

        binding.textChallenge1Title.setText(getString(R.string.investors_cabinet_challenge1_title));
        binding.textChallenge1Body.setText(getString(R.string.investors_cabinet_challenge1_body));
        binding.textChallenge1Days.setText(getString(R.string.investors_cabinet_days_left_format, 12));
        binding.textChallenge2Title.setText(getString(R.string.investors_cabinet_challenge2_title));
        binding.textChallenge2Body.setText(getString(R.string.investors_cabinet_challenge2_body));
        binding.textChallenge2Days.setText(getString(R.string.investors_cabinet_days_left_format, 5));

        int portfolioCount = Math.min(5, Math.max(1, item.industries.length));
        binding.textCabinetPortfolio.setText(getString(R.string.investors_cabinet_portfolio_format, portfolioCount));
    }

    private static int estimateMessages(@NonNull InvestorListItem item) {
        return Math.max(0, item.views * 3 / 8 + item.challenges * 5);
    }

    private void bindSectorTags(@NonNull InvestorListItem item) {
        FlexboxLayout flex = binding.layoutCabinetSectors;
        flex.removeAllViews();
        for (String tag : item.industries) {
            TextView tv = new TextView(this);
            tv.setText(tag);
            tv.setBackgroundResource(R.drawable.bg_investor_industry_tag);
            int pxH = dp(8);
            int pxV = dp(4);
            tv.setPadding(pxH, pxV, pxH, pxV);
            tv.setTextColor(ContextCompat.getColor(this, R.color.investor_chip_text));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            tv.setTypeface(ResourcesCompat.getFont(this, R.font.inter_semibold));
            FlexboxLayout.LayoutParams lp =
                    new FlexboxLayout.LayoutParams(
                            FlexboxLayout.LayoutParams.WRAP_CONTENT,
                            FlexboxLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, dp(6), dp(4));
            flex.addView(tv, lp);
        }
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density + 0.5f);
    }

    @NonNull
    private static String formatTicket(@NonNull InvestorListItem item) {
        return "$" + item.ticketMinK + "k – $" + item.ticketMaxK + "k";
    }

    @NonNull
    private static String buildStageLabel(@NonNull InvestorListItem item) {
        Set<String> set = new LinkedHashSet<>();
        for (String t : item.filterTags) {
            if ("Idea".equalsIgnoreCase(t) || "MVP".equalsIgnoreCase(t) || "Growth".equalsIgnoreCase(t)) {
                set.add(t);
            }
        }
        if (set.isEmpty()) {
            if (item.filterTags.length == 0) {
                return "—";
            }
            return TextUtils.join(" / ", item.filterTags);
        }
        return TextUtils.join(" / ", set);
    }

    private void setupActions(@NonNull InvestorListItem investor, @NonNull ArrayList<InvestorListItem> list) {
        binding.buttonBack.setOnClickListener(v -> finish());
        binding.buttonShare.setOnClickListener(v -> shareInvestor(investor));
        View.OnClickListener openPitch =
                v -> SendPitchBottomSheet.newInstance(projectId, investor)
                        .show(getSupportFragmentManager(), SendPitchBottomSheet.TAG);
        binding.buttonCabinetMessageHeader.setOnClickListener(openPitch);
        binding.buttonCabinetSendBottom.setOnClickListener(openPitch);
        binding.textCabinetSeeAllChallenges.setOnClickListener(
                v -> Toast.makeText(
                                this,
                                getString(R.string.investors_cabinet_challenges_see_all) + " (" + list.size() + ")",
                                Toast.LENGTH_SHORT)
                        .show());
    }

    private void shareInvestor(@NonNull InvestorListItem investor) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.investors_cabinet_share_subject, investor.name));
        intent.putExtra(Intent.EXTRA_TEXT, investor.name + "\n" + investor.ticketAndGeo + "\n" + investor.quote);
        startActivity(Intent.createChooser(intent, getString(R.string.share)));
    }
}
