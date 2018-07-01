package coloring.org.jp.ktcc.full.custom_ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import coloring.org.jp.ktcc.full.R;
import coloring.org.jp.ktcc.full.util.CommonUtil;

/**
 * Created by anh.trinh on 12/7/2017.
 */

public class EditLayerView extends RelativeLayout {

    private static final int LAYOUT_RESOURCE_ID = R.layout.layout_edit_layer;
    @BindView(R.id.layout_edit_layer)
    FrameLayout layoutEdit;
    @BindView(R.id.image)
    DrawImageView image;
    @BindView(R.id.eraserMask)
    EraserMaskView eraserMask;
    @BindView(R.id.semiMask)
    SemiMaskView semiMask;
    private float eraserSize;

    public boolean isUsePen() {
        return isUsePen;
    }

    public void setUsePen(boolean usePen) {
        isUsePen = usePen;
        image.setUsePen(isUsePen);
    }

    private boolean isUsePen = false;
    // For UNDO, REDO;
    private ArrayList<Bitmap> historyBitmap = new ArrayList<Bitmap>();
    private int currentIndex = -1;
    public interface EditLayerViewListener {
        void onDone();
        void onDoneSemiChange(float x, float y);
        void onCurrentIndexChange(int index, int sizeIndex);
    }

    EditLayerViewListener mEditLayerViewListener;

    public void setEditLayerViewListener(EditLayerViewListener listener) {
        this.mEditLayerViewListener = listener;
    }

    /**
     * @param context The context which view is running on.
     * @return New ItemListCollections object.
     * @since v0.1
     */
    public static EditLayerView newInstance(final Context context) {
        if (context == null) {
            return null;
        }

        return new EditLayerView(context);
    }

    /**
     * The constructor with current context.
     *
     * @param context The context which view is running on.
     * @since v0.1
     */
    public EditLayerView(final Context context) {
        this(context, null);
    }

    /**
     * The constructor with current context.
     *
     * @param context The context which view is running on.
     * @param attrs   The initialize attributes set.
     * @since v0.1
     */
    public EditLayerView(final Context context,
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
    public EditLayerView(final Context context,
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

       image.setDrawImageViewListener(new DrawImageView.DrawImageViewListener() {
           @Override
           public void onTouchDown(float x, float y) {
               if(!isUsePen) {
                   if (eraserMask.getVisibility() == VISIBLE) {
                       eraserMask.setX(x);
                       eraserMask.setY(y - eraserMask.getHeight());
                   }
               }
           }
           @Override
           public void onEraserDone() {
               image.endClear();
               addBitmap(image.getDrawBitmap());
           }

           @Override
           public void onSemiAuto(float x, float y) {
               if(mEditLayerViewListener!=null){
                   Matrix myMatrix = new Matrix();
                   image.getMatrix().invert(myMatrix);
                   float[] pt = image.convertPoint(x,y);
                   myMatrix.mapPoints(pt);
                   mEditLayerViewListener.onDoneSemiChange(  pt[0], pt[1]);
               }
           }
       });
        eraserMask.setOnTouchListener(new MoveViewTouchListener(eraserMask, new MoveViewTouchListener.MoveViewListener() {
            @Override
            public void onViewChange(float x, float y, MotionEvent event) {
               /* float newX =(x+ eraserMask.getWidth()/2);
                float newY = (y+ eraserSize + CommonUtil.convertDpToPixel(10,getContext()));*/
                float newX =(x+ eraserMask.getWidth()/2);
                float newY = (y+ eraserSize/2+ CommonUtil.convertDpToPixel(10,getContext()));
               image.startClear(newX,newY, event );
            }

            @Override
            public void onDoneChange(float x, float y) {
               /* image.endClear();
                addBitmap(image.getDrawBitmap());*/
            }
        }));
        semiMask.setOnTouchListener(new MoveViewTouchListener(semiMask, new MoveViewTouchListener.MoveViewListener() {
            @Override
            public void onViewChange(float x, float y, MotionEvent event) {
            }

            @Override
            public void onDoneChange(float x, float y) {
                if(mEditLayerViewListener!=null){
                    Matrix myMatrix = new Matrix();
                    image.getMatrix().invert(myMatrix);
                    float[] pt = image.convertPoint(x+semiMask.getWidth()/2,y+semiMask.getWidth()/2);
                    myMatrix.mapPoints(pt);
                    mEditLayerViewListener.onDoneSemiChange(  pt[0], pt[1]);
                }

            }
        }));

    }
    public void addBitmap(Bitmap bitmap){
        if(historyBitmap.size()>currentIndex+1){
            for(int i= historyBitmap.size()-1;i>currentIndex ;i--){
                historyBitmap.remove(i);
            }
        }
        if(historyBitmap.size()==20){
            historyBitmap.remove(0);
        }
        historyBitmap.add(bitmap);
        currentIndex = historyBitmap.size()-1;
        if(mEditLayerViewListener!=null){
            mEditLayerViewListener.onCurrentIndexChange(currentIndex, historyBitmap.size());
        }
        setImageBitmap(historyBitmap.get(currentIndex));
    }
    private void updateCurrentIndex(int currentIndex){
        if(currentIndex<0 || currentIndex>= historyBitmap.size())
            return;

        this.currentIndex = currentIndex;
        if(mEditLayerViewListener!=null){
            mEditLayerViewListener.onCurrentIndexChange(currentIndex, historyBitmap.size());
        }
        setImageBitmap(historyBitmap.get(this.currentIndex));
    }
    public void undo(){
        updateCurrentIndex(currentIndex-1);
    }
    public void redo(){
        updateCurrentIndex(currentIndex+1);
    }
    public void showEraserMask(boolean isShow){
        if(!isUsePen){
            if(isShow){
                eraserMask.setVisibility(VISIBLE);
            }else{
                eraserMask.setVisibility(GONE);
            }
        }else {
            image.setIsEraser(isShow);
        }



    }
    public void showSemiMask(boolean isShow){
        if(!isUsePen){
            if(isShow){
                semiMask.setVisibility(VISIBLE);
            }else{
                semiMask.setVisibility(GONE);
            }
        }else {
            image.setSemiEraser(isShow);
        }

    }
    private void setImageBitmap(Bitmap bitmap){
        image.setImageBitmap(bitmap);
        image.setBp(bitmap);

    }
    public Bitmap getDrawBitmap(){
        return image.getDrawBitmap();
    }
    public void setEraserMaskSize(int size){
        eraserMask.setAreaSize(size);
        setEraserSize(size);
        /*float d = getContext().getResources().getDisplayMetrics().density;
         this.eraserSize = eraserSize *d;
        image.setSizeEraser(size *d);*/
    }
    public void setEraserSize(int eraserSize) {
        float d = getContext().getResources().getDisplayMetrics().density;
        this.eraserSize = eraserSize *d;
        image.setSizeEraser(eraserSize *d);
    }
}
