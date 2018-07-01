package coloring.org.jp.ktcc.full.custom_ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import coloring.org.jp.ktcc.full.R;

/**
 * Created by anh.trinh on 12/7/2017.
 */

public class SemiMaskView extends LinearLayout implements View.OnTouchListener{

    private static final int LAYOUT_RESOURCE_ID = R.layout.layout_semi_mask;
    @BindView(R.id.vArea)
    View vArea;
    @BindView(R.id.vTouch)
    View vTouch;

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    public interface EraserMaskViewListener {
        void onSeckBarChange(int i);
        void onDone();
    }

    SemiMaskView.EraserMaskViewListener mEraserMaskViewListener;

    public void setEraserMaskView(SemiMaskView.EraserMaskViewListener listener) {
        this.mEraserMaskViewListener = listener;
    }

    /**
     * @param context The context which view is running on.
     * @return New ItemListCollections object.
     * @since v0.1
     */
    public static SemiMaskView newInstance(final Context context) {
        if (context == null) {
            return null;
        }

        return new SemiMaskView(context);
    }

    /**
     * The constructor with current context.
     *
     * @param context The context which view is running on.
     * @since v0.1
     */
    public SemiMaskView(final Context context) {
        this(context, null);
    }

    /**
     * The constructor with current context.
     *
     * @param context The context which view is running on.
     * @param attrs   The initialize attributes set.
     * @since v0.1
     */
    public SemiMaskView(final Context context,
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
    public SemiMaskView(final Context context,
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



    }
    public void setAreaSize(int size){
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)vArea.getLayoutParams();
        float d = getContext().getResources().getDisplayMetrics().density;//get density
        int p = (int)(size * d);//size 80 is in pixel so multiply by d to get it in dp
        params.width = p; //substitute parameters for left, top, right, bottom
        vArea.setLayoutParams(params);
    }
    public View getvArea(){
        return vArea;
    }


}
