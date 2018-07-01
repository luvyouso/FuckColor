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

/**
 * Created by anh.trinh on 12/7/2017.
 */

public class EraserToolView extends RelativeLayout {

    private static final int LAYOUT_RESOURCE_ID = R.layout.layout_eraser_tool;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.bt_done)
    ImageButton bt_done;
    @BindView(R.id.sb_process)
    SeekBar sbProcess;
    private String toolName;
    private float defaultValue;
    private float maxValue;

    public interface EraserToolViewListener {
        void onSeekBarChange(int i);
        void onProcessSeekBarChange(int i);
        void onDone();
    }

    EraserToolViewListener mEraserToolViewListener;

    public void setEraserToolView(EraserToolViewListener listener) {
        this.mEraserToolViewListener = listener;
    }

    /**
     * @param context The context which view is running on.
     * @return New ItemListCollections object.
     * @since v0.1
     */
    public static EraserToolView newInstance(final Context context) {
        if (context == null) {
            return null;
        }

        return new EraserToolView(context);
    }

    /**
     * The constructor with current context.
     *
     * @param context The context which view is running on.
     * @since v0.1
     */
    public EraserToolView(final Context context) {
        this(context, null);
    }

    /**
     * The constructor with current context.
     *
     * @param context The context which view is running on.
     * @param attrs   The initialize attributes set.
     * @since v0.1
     */
    public EraserToolView(final Context context,
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
    public EraserToolView(final Context context,
                          final AttributeSet attrs,
                          final int defStyleAttr) {
        super(context,
                attrs,
                defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.EraserTool, defStyleAttr, 0);
        toolName = a.getString(R.styleable.EraserTool_name);
        maxValue = a.getFloat(R.styleable.EraserTool_max_value, 0.0f);
        defaultValue = a.getFloat(R.styleable.EraserTool_default_value, 0.0f);

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

            sbProcess.setVisibility(VISIBLE);
            tvTitle.setVisibility(VISIBLE);
            tvTitle.setText(toolName);
            int process = (int) (defaultValue * 100 / maxValue);
            sbProcess.setProgress(process);
            sbProcess.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                    if (mEraserToolViewListener != null) {
                        mEraserToolViewListener.onProcessSeekBarChange(i);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (mEraserToolViewListener != null) {
                        mEraserToolViewListener.onSeekBarChange(seekBar.getProgress());
                    }
                }
            });



    }

    @OnClick(R.id.bt_done)
    public void onClick() {
        setVisibility(GONE);
        if (mEraserToolViewListener != null) {
            mEraserToolViewListener.onDone();

        }
    }

    public float getDefaultValue() {
        return defaultValue;
    }

    public int getValue() {
        double value = (maxValue * sbProcess.getProgress()) / 100;
        return (int) value;
    }

    public float getMaxValue() {
        return maxValue;
    }
    public void setMaxValue(float value) {
         maxValue = value;
    }
    public int getProcess() {
        return sbProcess.getProgress();
    }

    public void setProcess(float value) {
        float process = value * 100 / maxValue;
        sbProcess.setProgress((int) process);
    }
}
