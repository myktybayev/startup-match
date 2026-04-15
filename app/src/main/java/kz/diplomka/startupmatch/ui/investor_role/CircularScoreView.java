package kz.diplomka.startupmatch.ui.investor_role;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import kz.diplomka.startupmatch.R;

/**
 * Donut-style score indicator: track ring + progress arc (rounded caps) + centered percent text.
 */
public class CircularScoreView extends View {

    private static final float START_ANGLE = -90f;

    private final Paint trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF arcRect = new RectF();

    private float strokeWidthPx;
    private int progressPercent;
    @NonNull
    private String label = "0%";

    public CircularScoreView(Context context) {
        super(context);
        init(context);
    }

    public CircularScoreView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CircularScoreView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(@NonNull Context c) {
        strokeWidthPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                6f,
                c.getResources().getDisplayMetrics()
        );

        trackPaint.setStyle(Paint.Style.STROKE);
        trackPaint.setStrokeWidth(strokeWidthPx);
        trackPaint.setColor(ContextCompat.getColor(c, R.color.ai_score_ring_track));
        trackPaint.setStrokeCap(Paint.Cap.ROUND);

        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(strokeWidthPx);
        progressPaint.setColor(ContextCompat.getColor(c, R.color.kvadrat_blue));
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        textPaint.setColor(ContextCompat.getColor(c, R.color.investor_title));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                18f,
                c.getResources().getDisplayMetrics()
        ));
        if (ResourcesCompat.getFont(c, R.font.inter_bold) != null) {
            textPaint.setTypeface(ResourcesCompat.getFont(c, R.font.inter_bold));
        } else {
            textPaint.setFakeBoldText(true);
        }
    }

    public void setScorePercent(int percent) {
        progressPercent = Math.max(0, Math.min(100, percent));
        label = progressPercent + "%";
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float half = strokeWidthPx / 2f;
        arcRect.set(half, half, w - half, h - half);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(arcRect, 0f, 360f, false, trackPaint);
        float sweep = 360f * (progressPercent / 100f);
        if (sweep > 0f) {
            canvas.drawArc(arcRect, START_ANGLE, sweep, false, progressPaint);
        }
        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        float textY = cy - (fm.ascent + fm.descent) / 2f;
        canvas.drawText(label, cx, textY, textPaint);
    }
}
