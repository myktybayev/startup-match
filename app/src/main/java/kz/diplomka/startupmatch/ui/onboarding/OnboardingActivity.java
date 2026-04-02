package kz.diplomka.startupmatch.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import kz.diplomka.startupmatch.R;
import kz.diplomka.startupmatch.ui.authentication.RolePageActivity;

public class OnboardingActivity extends AppCompatActivity {

    private int currentPage = 0;

    private TextView textTitle;
    private TextView textSubtitle;
    private TextView textSkip;
    private MaterialButton buttonContinue;
    private ImageView imageHero;

    private LinearLayout[] bulletRows;
    private TextView[] bulletTexts;
    private View[] dots;

    private LinearLayout layoutHighlight;
    private TextView textHighlight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_onboarding);

        bindViews();
        renderPage();

        buttonContinue.setOnClickListener(v -> {
            if (currentPage < 3) {
                currentPage++;
                renderPage();
            } else {
                openMainAndFinish();
            }
        });

        textSkip.setOnClickListener(v -> openMainAndFinish());
    }

    private void bindViews() {
        textTitle = findViewById(R.id.textTitle);
        textSubtitle = findViewById(R.id.textSubtitle);
        textSkip = findViewById(R.id.textSkip);
        buttonContinue = findViewById(R.id.buttonContinue);
        imageHero = findViewById(R.id.imageHero);

        bulletRows = new LinearLayout[] {
                findViewById(R.id.bulletRow1),
                findViewById(R.id.bulletRow2),
                findViewById(R.id.bulletRow3),
                findViewById(R.id.bulletRow4),
                findViewById(R.id.bulletRow5)
        };

        bulletTexts = new TextView[] {
                findViewById(R.id.textBullet1),
                findViewById(R.id.textBullet2),
                findViewById(R.id.textBullet3),
                findViewById(R.id.textBullet4),
                findViewById(R.id.textBullet5)
        };

        dots = new View[] {
                findViewById(R.id.dot1),
                findViewById(R.id.dot2),
                findViewById(R.id.dot3),
                findViewById(R.id.dot4)
        };

        layoutHighlight = findViewById(R.id.layoutHighlight);
        textHighlight = findViewById(R.id.textHighlight);
    }

    private void renderPage() {
        imageHero.setImageResource(getHeroForPage(currentPage));
        textTitle.setText(getTitleForPage(currentPage));
        textSubtitle.setText(getSubtitleForPage(currentPage));

        String[] bullets = getBulletsForPage(currentPage);
        for (int i = 0; i < bulletRows.length; i++) {
            if (i < bullets.length) {
                bulletRows[i].setVisibility(View.VISIBLE);
                bulletTexts[i].setText(bullets[i]);
            } else {
                bulletRows[i].setVisibility(View.GONE);
            }
        }

        if (currentPage == 3) {
            layoutHighlight.setVisibility(View.VISIBLE);
            textHighlight.setText(R.string.onboarding_screen4_highlight);
            buttonContinue.setText(R.string.onboarding_start);
            textSkip.setVisibility(View.GONE);
        } else {
            layoutHighlight.setVisibility(View.GONE);
            buttonContinue.setText(R.string.onboarding_continue);
            textSkip.setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < dots.length; i++) {
            dots[i].setBackgroundResource(i == currentPage
                    ? R.drawable.bg_onboarding_dot_active
                    : R.drawable.bg_onboarding_dot_inactive);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) dots[i].getLayoutParams();
            params.width = dp(i == currentPage ? 24 : 8);
            params.height = dp(8);
            dots[i].setLayoutParams(params);
        }
    }

    private int dp(int value) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                value,
                getResources().getDisplayMetrics()
        );
    }

    private int getHeroForPage(int page) {
        switch (page) {
            case 1:
                return R.drawable.onboarding_img2;
            case 2:
                return R.drawable.onboarding_img3;
            case 3:
                return R.drawable.onboarding_img4;
            default:
                return R.drawable.onboarding_img1;
        }
    }

    private int getTitleForPage(int page) {
        switch (page) {
            case 1:
                return R.string.onboarding_screen2_title;
            case 2:
                return R.string.onboarding_screen3_title;
            case 3:
                return R.string.onboarding_screen4_title;
            default:
                return R.string.onboarding_screen1_title;
        }
    }

    private int getSubtitleForPage(int page) {
        switch (page) {
            case 1:
                return R.string.onboarding_screen2_subtitle;
            case 2:
                return R.string.onboarding_screen3_subtitle;
            case 3:
                return R.string.onboarding_screen4_subtitle;
            default:
                return R.string.onboarding_screen1_subtitle;
        }
    }

    private String[] getBulletsForPage(int page) {
        switch (page) {
            case 1:
                return new String[] {
                        getString(R.string.onboarding2_bullet_1),
                        getString(R.string.onboarding2_bullet_2),
                        getString(R.string.onboarding2_bullet_3),
                        getString(R.string.onboarding2_bullet_4),
                        getString(R.string.onboarding2_bullet_5)
                };
            case 2:
                return new String[] {
                        getString(R.string.onboarding3_bullet_1),
                        getString(R.string.onboarding3_bullet_2),
                        getString(R.string.onboarding3_bullet_3),
                        getString(R.string.onboarding3_bullet_4)
                };
            case 3:
                return new String[] {
                        getString(R.string.onboarding4_bullet_1),
                        getString(R.string.onboarding4_bullet_2),
                        getString(R.string.onboarding4_bullet_3)
                };
            default:
                return new String[] {
                        getString(R.string.onboarding_bullet_1),
                        getString(R.string.onboarding_bullet_2),
                        getString(R.string.onboarding_bullet_3)
                };
        }
    }

    private void openMainAndFinish() {
        startActivity(new Intent(OnboardingActivity.this, RolePageActivity.class));
        finish();
    }
}
