package coloring.org.jp.ktcc.full.custom_ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import coloring.org.jp.ktcc.full.R;
import coloring.org.jp.ktcc.full.filters.ColorEffectFilter;
import coloring.org.jp.ktcc.full.util.CommonUtil;

/**
 * Created by anh.trinh on 11/30/2017.
 */

public class ColorEffectView extends RelativeLayout implements ColorEffectItem.ColorEffectItemListener {

    private static final int LAYOUT_RESOURCE_ID = R.layout.layout_color_effect;
    // [0, 255] -> Default = 255
    @BindView(R.id.sb_opacity)
    ColorEffectItem sbOpacity;
    // [0, 510] -> Default = 255
    @BindView(R.id.sb_tint)
    ColorEffectItem sbTint;
    // [0, 510] -> Default = 255
    @BindView(R.id.sb_temperature)
    ColorEffectItem sbTemperature;
    // [0, 510] -> Default = 255
    @BindView(R.id.sb_blur)
    ColorEffectItem sbBlur;
    // [0, 360] -> Default = 180
    @BindView(R.id.sb_hue)
    ColorEffectItem sbHue;
    // [0, 510] -> Default = 255
    @BindView(R.id.sb_sepia)
    ColorEffectItem sbSepia;
    // [0, 510] -> Default = 255
    @BindView(R.id.sb_brightness)
    ColorEffectItem sbBrightness;
    // [0, 510] -> Default = 255
    @BindView(R.id.sb_contrast)
    ColorEffectItem sbContrast;
    // [0, 200] -> Default = 100
    @BindView(R.id.sb_saturation)
    ColorEffectItem sbSaturation;

    @BindView(R.id.layout_contain)
    LinearLayout mLayoutContain;
    @BindView(R.id.layout_main)
    RelativeLayout mLayoutMain;
    @BindView(R.id.background)
    View mBackground;

    boolean isBackground;

    @Override
    public void onSeckBarChange(int i) {
        if (mColorEffectListener != null && this.getVisibility() == VISIBLE) {
                mColorEffectListener.onChange(getOpacityValue(),getHueValue(),getTintValue(),getSepiaValue(),getTemperatureValue(),getBrightnessValue(),getContrastValue(),getSaturationValue(),getBlurValue(), isBackground);
            }

    }

    @Override
    public void onSeekBarStartChange(String name) {
        hideAndShowView(name, true);
    }

    @Override
    public void onSeekBarEndChange(String name) {
        hideAndShowView(name, false);
    }

    private void hideAndShowView(String seekbarName, boolean invisible) {
        mBackground.setVisibility(invisible ? INVISIBLE : VISIBLE);
        if (seekbarName.equals(getResources().getString(R.string.text_color_effect_opacity)) ){
            sbHue.setVisibility(invisible ? INVISIBLE : VISIBLE);
            sbBrightness.setVisibility(invisible ? INVISIBLE : VISIBLE);
            sbContrast.setVisibility(invisible ? INVISIBLE : VISIBLE);
            sbSaturation.setVisibility(invisible ? INVISIBLE : VISIBLE);
            sbSepia.setVisibility(invisible ? INVISIBLE : VISIBLE);
            sbTemperature.setVisibility(invisible ? INVISIBLE : VISIBLE);
            sbTint.setVisibility(invisible ? INVISIBLE : VISIBLE);
            sbBlur.setVisibility(invisible ? INVISIBLE : VISIBLE);
        } else{
            if (seekbarName.equals(getResources().getString(R.string.text_color_effect_hue))) {
                if (!isBackground) {
                    sbOpacity.setVisibility(invisible ? INVISIBLE : VISIBLE);
                }
                sbBrightness.setVisibility(invisible ? INVISIBLE : VISIBLE);
                sbContrast.setVisibility(invisible ? INVISIBLE : VISIBLE);
                sbSaturation.setVisibility(invisible ? INVISIBLE : VISIBLE);
                sbSepia.setVisibility(invisible ? INVISIBLE : VISIBLE);
                sbTemperature.setVisibility(invisible ? INVISIBLE : VISIBLE);
                sbTint.setVisibility(invisible ? INVISIBLE : VISIBLE);
                sbBlur.setVisibility(invisible ? INVISIBLE : VISIBLE);
            } else {
                if (seekbarName.equals(getResources().getString(R.string.text_color_effect_brightness))){
                    sbHue.setVisibility(invisible ? INVISIBLE : VISIBLE);
                    if (!isBackground) {
                        sbOpacity.setVisibility(invisible ? INVISIBLE : VISIBLE);
                    }
                    sbContrast.setVisibility(invisible ? INVISIBLE : VISIBLE);
                    sbSaturation.setVisibility(invisible ? INVISIBLE : VISIBLE);
                    sbSepia.setVisibility(invisible ? INVISIBLE : VISIBLE);
                    sbTemperature.setVisibility(invisible ? INVISIBLE : VISIBLE);
                    sbTint.setVisibility(invisible ? INVISIBLE : VISIBLE);
                    sbBlur.setVisibility(invisible ? INVISIBLE : VISIBLE);
                } else {
                    if (seekbarName.equals(getResources().getString(R.string.text_color_effect_contrast))) {
                        sbHue.setVisibility(invisible ? INVISIBLE : VISIBLE);
                        sbBrightness.setVisibility(invisible ? INVISIBLE : VISIBLE);
                        if (!isBackground) {
                            sbOpacity.setVisibility(invisible ? INVISIBLE : VISIBLE);
                        }
                        sbSaturation.setVisibility(invisible ? INVISIBLE : VISIBLE);
                        sbSepia.setVisibility(invisible ? INVISIBLE : VISIBLE);
                        sbTemperature.setVisibility(invisible ? INVISIBLE : VISIBLE);
                        sbTint.setVisibility(invisible ? INVISIBLE : VISIBLE);
                        sbBlur.setVisibility(invisible ? INVISIBLE : VISIBLE);
                    } else {
                        if (seekbarName.equals(getResources().getString(R.string.text_color_effect_saturation))) {
                            sbHue.setVisibility(invisible ? INVISIBLE : VISIBLE);
                            sbBrightness.setVisibility(invisible ? INVISIBLE : VISIBLE);
                            sbContrast.setVisibility(invisible ? INVISIBLE : VISIBLE);
                            if (!isBackground) {
                                sbOpacity.setVisibility(invisible ? INVISIBLE : VISIBLE);
                            }
                            sbSepia.setVisibility(invisible ? INVISIBLE : VISIBLE);
                            sbTemperature.setVisibility(invisible ? INVISIBLE : VISIBLE);
                            sbTint.setVisibility(invisible ? INVISIBLE : VISIBLE);
                            sbBlur.setVisibility(invisible ? INVISIBLE : VISIBLE);
                        } else {
                            if (seekbarName.equals(getResources().getString(R.string.text_color_effect_sepia))) {
                                sbHue.setVisibility(invisible ? INVISIBLE : VISIBLE);
                                sbBrightness.setVisibility(invisible ? INVISIBLE : VISIBLE);
                                sbContrast.setVisibility(invisible ? INVISIBLE : VISIBLE);
                                sbSaturation.setVisibility(invisible ? INVISIBLE : VISIBLE);
                                if (!isBackground) {
                                    sbOpacity.setVisibility(invisible ? INVISIBLE : VISIBLE);
                                }
                                sbTemperature.setVisibility(invisible ? INVISIBLE : VISIBLE);
                                sbTint.setVisibility(invisible ? INVISIBLE : VISIBLE);
                                sbBlur.setVisibility(invisible ? INVISIBLE : VISIBLE);
                            } else {
                                if (seekbarName.equals(getResources().getString(R.string.text_color_effect_temperature))) {
                                    sbHue.setVisibility(invisible ? INVISIBLE : VISIBLE);
                                    sbBrightness.setVisibility(invisible ? INVISIBLE : VISIBLE);
                                    sbContrast.setVisibility(invisible ? INVISIBLE : VISIBLE);
                                    sbSaturation.setVisibility(invisible ? INVISIBLE : VISIBLE);
                                    sbSepia.setVisibility(invisible ? INVISIBLE : VISIBLE);
                                    if (!isBackground) {
                                        sbOpacity.setVisibility(invisible ? INVISIBLE : VISIBLE);
                                    }
                                    sbTint.setVisibility(invisible ? INVISIBLE : VISIBLE);
                                    sbBlur.setVisibility(invisible ? INVISIBLE : VISIBLE);
                                } else {
                                    if (seekbarName.equals(getResources().getString(R.string.text_color_effect_tint)) ){
                                        sbHue.setVisibility(invisible ? INVISIBLE : VISIBLE);
                                        sbBrightness.setVisibility(invisible ? INVISIBLE : VISIBLE);
                                        sbContrast.setVisibility(invisible ? INVISIBLE : VISIBLE);
                                        sbSaturation.setVisibility(invisible ? INVISIBLE : VISIBLE);
                                        sbSepia.setVisibility(invisible ? INVISIBLE : VISIBLE);
                                        sbTemperature.setVisibility(invisible ? INVISIBLE : VISIBLE);
                                        if (!isBackground) {
                                            sbOpacity.setVisibility(invisible ? INVISIBLE : VISIBLE);
                                        }
                                        sbBlur.setVisibility(invisible ? INVISIBLE : VISIBLE);
                                    } else {
                                        if (seekbarName.equals(getResources().getString(R.string.text_color_effect_blur))) {
                                            sbHue.setVisibility(invisible ? INVISIBLE : VISIBLE);
                                            sbBrightness.setVisibility(invisible ? INVISIBLE : VISIBLE);
                                            sbContrast.setVisibility(invisible ? INVISIBLE : VISIBLE);
                                            sbSaturation.setVisibility(invisible ? INVISIBLE : VISIBLE);
                                            sbSepia.setVisibility(invisible ? INVISIBLE : VISIBLE);
                                            sbTemperature.setVisibility(invisible ? INVISIBLE : VISIBLE);
                                            sbTint.setVisibility(invisible ? INVISIBLE : VISIBLE);
                                            if (!isBackground) {
                                                sbOpacity.setVisibility(invisible ? INVISIBLE : VISIBLE);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    public interface ColorEffectListener {
        void onChange(float opacity, float hue, float tint, float sepia, float temperature, float brightness, float contrast, float saturation, float blur, boolean isBackground);

        void onTouchOutSide();
    }

    ColorEffectListener mColorEffectListener;

    public void setColorEffectListener(ColorEffectListener listener) {
        this.mColorEffectListener = listener;
    }

    /**
     * @param context The context which view is running on.
     * @return New ItemListCollections object.
     * @since v0.1
     */
    public static ColorEffectView newInstance(final Context context) {
        if (context == null) {
            return null;
        }

        return new ColorEffectView(context);
    }

    /**
     * The constructor with current context.
     *
     * @param context The context which view is running on.
     * @since v0.1
     */
    public ColorEffectView(final Context context) {
        this(context, null);
    }

    /**
     * The constructor with current context.
     *
     * @param context The context which view is running on.
     * @param attrs   The initialize attributes set.
     * @since v0.1
     */
    public ColorEffectView(final Context context,
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
    public ColorEffectView(final Context context,
                           final AttributeSet attrs,
                           final int defStyleAttr) {
        super(context,
                attrs,
                defStyleAttr);

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
        sbBlur.setColorEffectListener(this);
        sbHue.setColorEffectListener(this);
        sbSepia.setColorEffectListener(this);
        sbContrast.setColorEffectListener(this);
        sbBrightness.setColorEffectListener(this);
        sbSaturation.setColorEffectListener(this);
        sbOpacity.setColorEffectListener(this);
        sbTint.setColorEffectListener(this);
        sbTemperature.setColorEffectListener(this);
        if(CommonUtil.isTablet(getContext())) {
            LayoutParams paramsContain = (RelativeLayout.LayoutParams)mLayoutContain.getLayoutParams();
            paramsContain.height = (int)getResources().getDimension(R.dimen.scroll_height_415);
            mLayoutContain.setLayoutParams(paramsContain);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)sbBlur.getLayoutParams();
            params.height = (int)getResources().getDimension(R.dimen.height_45);
            sbBlur.setLayoutParams(params);
            sbHue.setLayoutParams(params);
            sbSepia.setLayoutParams(params);
            sbContrast.setLayoutParams(params);
            sbBrightness.setLayoutParams(params);
            sbSaturation.setLayoutParams(params);
            sbOpacity.setLayoutParams(params);
            sbTint.setLayoutParams(params);
            sbTemperature.setLayoutParams(params);


        }

    }
    public void initData(ColorEffectFilter colorEffectFilter, boolean isBackground){
        setIsbackground(isBackground);
        setColorEffectFilter(colorEffectFilter);
    }


    public void setColorEffectFilter(ColorEffectFilter colorEffectFilter) {
        setOpacityValue(colorEffectFilter.getAlpha());
        setHueValue(colorEffectFilter.getHue());
        setSepiaValue(colorEffectFilter.getSepia());
        setContrastValue(colorEffectFilter.getContrast());
        setBrightnessValue(colorEffectFilter.getBrightness());
        setSaturationValue(colorEffectFilter.getSaturation());
        setTintValue(colorEffectFilter.getTint());
        setTemperaturerValue(colorEffectFilter.getTemperature());
        setBlurValue(colorEffectFilter.getBlur());

    }

    @OnClick({R.id.vTouch, R.id.btClose})
    public void onClick() {
        if (mColorEffectListener != null) {
            mColorEffectListener.onTouchOutSide();
        }
    }

    private void setOpacityValue(float value) {

        sbOpacity.setProcess(value);
    }

    private float getOpacityValue() {
        // return  sbOpacity.getProcess()*(float)2.55;
        return sbOpacity.getValue();
    }

    private void setHueValue(float value) {
        sbHue.setProcess(value);
    }

    private float getHueValue() {
        //return  sbHue.getProcess()*(float)3.6;
        return sbHue.getValue();
    }

    private void setSepiaValue(float value) {
        sbSepia.setProcess(value);
    }

    private float getSepiaValue() {
        // return  sbSepia.getProcess()*(float)5.1;
        return sbSepia.getValue();
    }

    private void setContrastValue(float value) {
        sbContrast.setProcess(value);
    }

    private float getContrastValue() {
        // return  sbContrast.getProcess()*(float)5.1;
        return sbContrast.getValue();
    }

    private void setBrightnessValue(float value) {
        sbBrightness.setProcess(value);
    }

    private float getBrightnessValue() {
        //return  sbBrightness.getProcess()*(float)5.1;
        return sbBrightness.getValue();
    }

    private void setSaturationValue(float value) {
        sbSaturation.setProcess(value);
    }

    private float getSaturationValue() {
        //return  sbSaturation.getProcess()*(float)2.0;
        return sbSaturation.getValue();
    }

    private void setTintValue(float value) {
        sbTint.setProcess(value);
    }

    private float getTintValue() {
        // return  sbTint.getProcess()*(float)5.1;
        return sbTint.getValue();
    }

    private void setTemperaturerValue(float value) {
        sbTemperature.setProcess(value);
    }

    private float getTemperatureValue() {
        //  return  sbTemperature.getProcess()*(float)5.1;
        return sbTemperature.getValue();
    }

    private void setBlurValue(float value) {
        sbBlur.setProcess(value);
    }

    private float getBlurValue() {
        //return  sbBlur.getProcess()*(float)5.1;
        return sbBlur.getValue();
    }

    public void setIsbackground(boolean isbackground) {
        this.isBackground = isbackground;
        sbOpacity.setVisibility(isbackground ? GONE : VISIBLE);
    }


    private boolean getIsbackground() {
        return isBackground;
    }


}
