package coloring.org.jp.ktcc.full.custom_ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import coloring.org.jp.ktcc.full.R;
import coloring.org.jp.ktcc.full.util.CommonUtil;

/**
 * Created by anh.trinh on 11/30/2017.
 */

public class ColorEffectItem extends RelativeLayout {

    private static final int LAYOUT_RESOURCE_ID = R.layout.layout_color_effect_item;
    @BindView(R.id.tv_color_effect_name)
    TextView tvColorEffectName;
    @BindView(R.id.bt_remove)
    ImageButton btRemove;
    @BindView(R.id.sb_process)
    SeekBar sbProcess;
    private String colorEffectName;
    private float defaultValue;
    private float maxValue;

    public interface ColorEffectItemListener {
        void onSeckBarChange(int i);
        void onSeekBarStartChange(String name);
        void onSeekBarEndChange(String name);
    }

    ColorEffectItemListener mColorEffectItemListener;

    public void setColorEffectListener(ColorEffectItemListener listener) {
        this.mColorEffectItemListener = listener;
    }

    /**
     * @param context The context which view is running on.
     * @return New ItemListCollections object.
     * @since v0.1
     */
    public static ColorEffectItem newInstance(final Context context) {
        if (context == null) {
            return null;
        }

        return new ColorEffectItem(context);
    }

    /**
     * The constructor with current context.
     *
     * @param context The context which view is running on.
     * @since v0.1
     */
    public ColorEffectItem(final Context context) {
        this(context, null);
    }

    /**
     * The constructor with current context.
     *
     * @param context The context which view is running on.
     * @param attrs   The initialize attributes set.
     * @since v0.1
     */
    public ColorEffectItem(final Context context,
                           final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * The constructor with current context.
     *
     * @param context      The context which view is running on.
     * @param attrs        The initialize attributes set.
     * @param defStyleAttr The default style.
     * @since v0.1
     */
    public ColorEffectItem(final Context context,
                           final AttributeSet attrs,
                           final int defStyleAttr) {
        super(context,
                attrs,
                defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ColorEffect, defStyleAttr, 0);
        colorEffectName = a.getString(R.styleable.ColorEffect_color_effect_item);
        maxValue = a.getFloat(R.styleable.ColorEffect_color_effect_item_max_value,0.0f);
        defaultValue = a.getFloat(R.styleable.ColorEffect_color_effect_item_default_value, 0.0f);

        this.initialize();
    }

    /**
     * @see View#setEnabled(boolean)
     * @since v0.1
     */
    @Override
    public void setEnabled(final boolean isEnabled) {
        this.setAlpha(isEnabled ?
                1f :
                0.5f);
        super.setEnabled(isEnabled);
    }

    /**
     * Reset injected resources created by ButterKnife.
     *
     * @since v0.1
     */
    public void resetInjectedResource() {
        ButterKnife.bind(this);
    }

    /**
     * Initialize UI sub views.
     *
     * @since v0.1
     */
    private void initialize() {

        final LayoutInflater layoutInflater = LayoutInflater.from(this.getContext());
        layoutInflater.inflate(LAYOUT_RESOURCE_ID,
                this,
                true);

        ButterKnife.bind(this,
                this);
        tvColorEffectName.setText(colorEffectName);
        int process = (int) (defaultValue * 100 / maxValue);
        sbProcess.setProgress(process);
        sbProcess.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mColorEffectItemListener != null) {
                    mColorEffectItemListener.onSeckBarChange(i);

                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mColorEffectItemListener != null) {
                    mColorEffectItemListener.onSeekBarStartChange(colorEffectName);

                }
                tvColorEffectName.setVisibility(INVISIBLE);
                btRemove.setVisibility(INVISIBLE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mColorEffectItemListener != null) {
                    mColorEffectItemListener.onSeekBarEndChange(colorEffectName);
                }
                tvColorEffectName.setVisibility(VISIBLE);
                btRemove.setVisibility(VISIBLE);
            }
        });
        if(CommonUtil.isTablet(getContext())) {
            sbProcess.setThumb(getResources().getDrawable(R.drawable.custom_thumb_seekbar_tablet));
            sbProcess.setThumbOffset(0);
        }


    }

    @OnClick(R.id.bt_remove)
    public void onClick() {
        int process = (int) (defaultValue * 100 / maxValue);
        sbProcess.setProgress(process);
        if (mColorEffectItemListener != null) {
            mColorEffectItemListener.onSeckBarChange(sbProcess.getProgress());

        }
    }

    public float getDefaultValue() {
        return defaultValue;
    }

    public float getValue() {
        double value = (maxValue /100)* sbProcess.getProgress();
        return (float)value ;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public int getProcess() {
        return sbProcess.getProgress();
    }

    public void setProcess(float value) {
        float process = value * 100 / maxValue;
        sbProcess.setProgress((int) process);
    }

}
