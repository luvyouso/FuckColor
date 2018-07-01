package coloring.org.jp.ktcc.full.custom_ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import coloring.org.jp.ktcc.full.R;
import coloring.org.jp.ktcc.full.app.activity.MainActivity;
import coloring.org.jp.ktcc.full.util.CommonUtil;
import coloring.org.jp.ktcc.full.util.UtilDevice;

/**
 * Created by nguyen on 11/6/2017.
 */
public class FingerSeal extends RelativeLayout {

    public interface FingerCachetListener {
        void onTouchEdit();

        void onTouchRemove();
    }

    public FingerCachetListener getFingerListener() {
        return fingerListener;
    }

    public void setFingerListener(FingerCachetListener fingerListener) {
        this.fingerListener = fingerListener;
    }

    private FingerCachetListener fingerListener;

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private static final int ROTATION = 3;
    private static final int ZOOM_BUTTON_Y = 4;

    //View
    ImageButton btnZoomY;
    ImageButton btnRemove;
    ImageButton btnRotation360;
    ImageButton btnRotation90;
    ImageButton btnEdit;
    FrameLayout centerFinder;

    private int widthScreen;
    private int heightScreen;

    Layer mLayer = null;
    private int currentMode = NONE;
    private int widthLayer = 0;
    private int heightLayer = 0;
    private float ratioLayer = 0;
    private int marginLayer = 0;

    FrameLayout.LayoutParams paramsFinger;
    private ListenerFinger listenerFinger;
    private int minFinger;

    float xBegin = 0;
    float yBegin = 0;
    float xMax = 0;
    float yMax = 0;
    private float xCenter = 0;
    private float yCenter = 0;
    float angleBegin = 0;
    private float radiusBegin = 0;

    public FingerSeal(Context context) {
        super(context);
        init();
    }

    public FingerSeal(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FingerSeal(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setxMax(float xMax) {
        this.xMax = xMax;
    }

    public void setyMax(float yMax) {
        this.yMax = yMax;
    }

    public Layer getLayer() {
        return mLayer;
    }

    public void setLayer(Layer layer) {
        this.mLayer = layer;
        moveFingerToLayerSelect();
    }

    public void setListenerFinger(ListenerFinger listenerFinger) {
        this.listenerFinger = listenerFinger;
    }

    private void init() {
        marginLayer = getMarginLayer();
        minFinger = getMinFinger();
        DisplayMetrics displayMetrics = UtilDevice.getScreen((Activity) getContext());
        widthScreen = displayMetrics.widthPixels;
        heightScreen = displayMetrics.heightPixels;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View root = inflater.inflate(R.layout.finger_seal, this);
        this.initAvailableView(root);
        this.setEventFinger();
    }

    public int getMarginLayer() {
        return getResources().getDimensionPixelOffset(R.dimen.margin_border_finder) + getResources().getDimensionPixelOffset(R.dimen.border_finder) + 20;
    }

    public int getMinFinger() {
        return getResources().getDimensionPixelOffset(R.dimen.min_finger_seal);
    }

    public void moveFingerToLayerSelect() {
        if (mLayer == null) {
            return;
        }
        setAlpha(0f);
        paramsFinger = (FrameLayout.LayoutParams) getLayoutParams();
        int newWidth = mLayer.getLayoutParams().width + marginLayer * 2;
        int newHeight = mLayer.getLayoutParams().height + marginLayer * 2;

        boolean isMinWidth = newWidth < minFinger ? true : false;
        boolean isMinHeight = newHeight < minFinger ? true : false;

        paramsFinger.width = isMinWidth ? minFinger : newWidth;
        paramsFinger.height = isMinHeight ? minFinger : newHeight;

        setLayoutParams(paramsFinger);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                int sizeCenterW = (int) ((getWidth() - marginLayer * 2 - mLayer.getWidth()) / 2f);
                int sizeCenterH = (int) ((getHeight() - marginLayer * 2 - mLayer.getHeight()) / 2f);
                float x = mLayer.getX() - marginLayer - sizeCenterW;
                float y = mLayer.getY() - marginLayer - sizeCenterH;
                animate().x(x).y(y).setDuration(0).start();
                setAlpha(1f);
            }
        });
        setRotation(mLayer.getRotation());

        widthLayer = mLayer.getLayoutParams().width;
        heightLayer = mLayer.getLayoutParams(). height;
        ratioLayer = (float) widthLayer / (float) heightLayer;
    }

    public void removeLayer() {
        this.mLayer = null;
    }

    private void initAvailableView(View root) {
        centerFinder = (FrameLayout) root.findViewById(R.id.centerFinder);
        btnZoomY = (ImageButton) root.findViewById(R.id.btnZoomY);
        btnRemove = (ImageButton) root.findViewById(R.id.btnRemove);
        btnRotation360 = (ImageButton) root.findViewById(R.id.btnRotation360);
        btnRotation90 = (ImageButton) root.findViewById(R.id.btnRotation90);
        btnEdit = (ImageButton) root.findViewById(R.id.btnEdit);
    }

    private void setEventFinger() {
        setTouchCenterFinger();
        setTouchRemoveSeal();
        setTouchZoomY();
        setClickRotation90();
        setTouchRotation360();
        setClickEdit();
    }

    private float spacing(float xOld, float yOld, float xNew, float yNew) {
        float x = xOld - xNew;
        float y = yOld - yNew;
        return (float) Math.sqrt(x * x + y * y);
    }

    private float rotationAngle(float xOld, float yOld, float xNew, float yNew) {
        double delta_x = xOld - xNew;
        double delta_y = yOld - yNew;
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }

    private void drag(final float xC, final float yC) {
        if (mLayer != null) {
            float an = mLayer.getRotation();
            mLayer.setRotation(0);
            setRotation(0);
            int sizeCenterW = (int) ((getWidth() - marginLayer * 2 - mLayer.getWidth()) / 2f);
            int sizeCenterH = (int) ((getHeight() - marginLayer * 2 - mLayer.getHeight()) / 2f);
            float x = xC - marginLayer - sizeCenterW;
            float y = yC - marginLayer - sizeCenterH;
            animate().x(x).y(y).setDuration(0).start();
            mLayer.animate().x(xC).y(yC).setDuration(0).start();
            mLayer.setRotation(an);
            setRotation(an);
        }
    }

    private void rotate(float angle) {
        //Finger
        animate().rotationBy(angle).setDuration(0).setInterpolator(new LinearInterpolator()).start();
        //Layer
        if (mLayer != null) {
            mLayer.animate().rotationBy(angle).setDuration(0).setInterpolator(new LinearInterpolator()).start();
        }
    }

    private boolean isMinLayer(int width, int height) {
        boolean w = width <= 0 ? true : false;
        boolean h = height <= 0 ? true : false;
        return w || h;
    }

    private boolean isMaxLayer(int width, int height) {
        return width > widthScreen / 2 || height > heightScreen / 2;
    }

    private void zoomY(float scale) {
        int newWidth = widthLayer;
        int newHeight = (int) (heightLayer * scale + 0.5f);
        zoom(newWidth, newHeight);
    }

    private void zoom(int newWidth, int newHeight) {
        if (isMaxLayer(newWidth, newHeight) || isMinLayer(newWidth, newHeight)) {
            return;
        }
        if (mLayer != null) {
            FrameLayout.LayoutParams paramsLayer = (FrameLayout.LayoutParams) mLayer.getLayoutParams();
            paramsLayer.width = newWidth;
            paramsLayer.height = newHeight;
            mLayer.setLayoutParams(paramsLayer);
        }
        ratioLayer = (float) newWidth / (float) newHeight;

        int newFingerWidth = newWidth + marginLayer * 2;
        int newFingerHeight = newHeight + marginLayer * 2;

        //Finger
        boolean isMinWidth = newFingerWidth < minFinger ? true : false;
        boolean isMinHeight = newFingerHeight < minFinger ? true : false;
        paramsFinger = (FrameLayout.LayoutParams) getLayoutParams();
        paramsFinger.width = isMinWidth ? minFinger : newFingerWidth;
        paramsFinger.height = isMinHeight ? minFinger : newFingerHeight;
        setLayoutParams(paramsFinger);
    }

    private void setTouchCenterFinger() {
        centerFinder.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return handleTouchCenter(event);
            }
        });
    }

    private void setTouchZoomY(){
         btnZoomY.setOnTouchListener(new OnTouchListener() {
             @Override
             public boolean onTouch(View v, MotionEvent event) {
                 return handleTouchButtonZoom(event, ZOOM_BUTTON_Y);
             }
         });
     }

    private void setTouchRemoveSeal() {
        btnRemove.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return handleRemoveSeal();
            }
        });
    }

    private void setClickEdit() {
        btnEdit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (fingerListener != null) {
                    fingerListener.onTouchEdit();
                }
            }
        });
    }

    private void setClickRotation90() {
        btnRotation90.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                handleClickButtonRotation90();
            }
        });
    }

    private void setTouchRotation360() {
        btnRotation360.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return handleTouchButtonRotation360(event);
            }
        });
    }

    ////////////////////////////////////////////////// Handling //////////////////////////////////////////////////////////////////////////////////
    private boolean handleTouchCenter(final MotionEvent event) {
        if (mLayer != null) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    if (currentMode != NONE || mLayer == null) {
                        return false;
                    }
                    moveFingerToLayerSelect();
                    if (listenerFinger != null && mLayer != null) {
                        listenerFinger.focusLayer(mLayer);
                    }
                    paramsFinger = (FrameLayout.LayoutParams) getLayoutParams();
                    widthLayer = mLayer.getWidth();
                    heightLayer = mLayer.getHeight();
                    xBegin = mLayer.getX() - event.getRawX();//Note: getX() - event.getRawX() = -event.getX(0)
                    yBegin = mLayer.getY() - event.getRawY();
                    setVisibility(VISIBLE);
                    currentMode = DRAG;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    break;
                case MotionEvent.ACTION_UP:// point 1
                    currentMode = NONE;
                    if (listenerFinger != null && mLayer != null) {
                        listenerFinger.unFocusLayer(mLayer);
                    }
                    break;
                case MotionEvent.ACTION_POINTER_UP:// point 2
                    currentMode = NONE;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (currentMode == DRAG) {
                        xCenter = mLayer.getX() + mLayer.getWidth() / 2;
                        yCenter = mLayer.getY() + mLayer.getHeight() / 2;
                        float xC = event.getRawX() + xBegin;
                        float yC = event.getRawY() + yBegin;
                        if (xC < - mLayer.getMeasuredWidth()/2)
                            xC = -mLayer.getMeasuredWidth()/2;
                        if (yC < - mLayer.getMeasuredHeight()/2)
                            yC = -mLayer.getMeasuredHeight()/2;

                        if (xC > xMax-mLayer.getMeasuredWidth()/2 )
                            xC = xMax-mLayer.getMeasuredWidth()/2 ;

                        if (yC > yMax - mLayer.getMeasuredHeight()/2 )
                            yC = yMax - mLayer.getMeasuredHeight()/2 ;

                        drag(xC, yC);
                    }
                    break;
                default:
                    break;
            }
        }
        return true;
    }

    private boolean handleRemoveSeal() {
        if (fingerListener != null) {
            fingerListener.onTouchRemove();
        }
        return true;
    }

    private boolean handleTouchButtonZoom(MotionEvent event, int typeZoom) {
        if (mLayer != null) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    if (currentMode != NONE || mLayer == null) {
                        return false;
                    }
                    paramsFinger = (FrameLayout.LayoutParams) getLayoutParams();
                    widthLayer = mLayer.getWidth();
                    heightLayer = mLayer.getHeight();
                    xCenter = mLayer.getX() + mLayer.getWidth() / 2;
                    yCenter = mLayer.getY() + mLayer.getHeight() / 2;
                    radiusBegin = spacing(event.getRawX(), event.getRawY(), xCenter, yCenter);
                    currentMode = ZOOM;
                    break;
                case MotionEvent.ACTION_UP:
                    currentMode = NONE;
                    break;
                case MotionEvent.ACTION_MOVE:
                    float newRadius = spacing(event.getRawX(), event.getRawY(), xCenter, yCenter);
                    float scale = (newRadius / radiusBegin);
                    float currentAngle = getRotation();
                    setRotation(0);
                    mLayer.setRotation(0);
                    switch (typeZoom) {
                        case ZOOM_BUTTON_Y:
                            zoomY(scale);
                            break;
                        default:
                            currentMode = NONE;
                            break;
                    }
                    setRotation(currentAngle);
                    mLayer.setRotation(currentAngle);
                    if (currentMode == NONE) {
                        return false;
                    }
                    break;
                default:
                    break;
            }
        }
        return true;
    }

    private void handleClickButtonRotation90() {
        if (mLayer != null) {
            float temp = mLayer.getRotation() % 90;
            if (temp != 0) {
                rotate(-temp);
            } else {
                rotate(90);
            }
        }
    }

    private boolean handleTouchButtonRotation360(MotionEvent event) {
        if (mLayer != null) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    if (currentMode != NONE || mLayer == null) {
                        return false;//End touch
                    }
                    paramsFinger = (FrameLayout.LayoutParams) getLayoutParams();
                    widthLayer = mLayer.getWidth();
                    heightLayer = mLayer.getHeight();
                    xCenter = getX() + getWidth() / 2;
                    yCenter = getY() + getHeight() / 2;

                    angleBegin = rotationAngle(xCenter, yCenter, event.getX(), event.getY());
                    currentMode = ROTATION;
                    break;
                case MotionEvent.ACTION_UP:
                    currentMode = NONE;
                    break;
                case MotionEvent.ACTION_MOVE:
                    rotate(-(rotationAngle(xCenter, yCenter, event.getX(), event.getY()) - angleBegin));
                    break;
                default:
                    break;
            }
        }
        return true;
    }
    ////////////////////////////////////////////////// #Handling //////////////////////////////////////////////////////////////////////////////////

    public void hideControl() {
        centerFinder.setVisibility(INVISIBLE);
        btnZoomY.setVisibility(INVISIBLE);
        btnRemove.setVisibility(INVISIBLE);
        btnRotation360.setVisibility(INVISIBLE);
        btnRotation90.setVisibility(INVISIBLE);
        btnEdit.setVisibility(INVISIBLE);
    }

    public void showControl() {
        if (centerFinder.getVisibility() != VISIBLE) {
            centerFinder.setVisibility(VISIBLE);
        }
        if (btnZoomY.getVisibility() != VISIBLE) {
            btnZoomY.setVisibility(VISIBLE);
        }
        if (btnRemove.getVisibility() != VISIBLE) {
            btnRemove.setVisibility(VISIBLE);
        }
        if (btnRotation360.getVisibility() != VISIBLE) {
            btnRotation360.setVisibility(VISIBLE);
        }
        if (btnRotation90.getVisibility() != VISIBLE) {
            btnRotation90.setVisibility(VISIBLE);
        }
        if (btnEdit.getVisibility() != VISIBLE) {
            btnEdit.setVisibility(VISIBLE);
        }
    }

    public float getMaxWidth(){
        return widthScreen / 2;
    }

    public float getCurrentWidth(){
        return widthLayer;
    }

    public void setWidth(int newWidth){
        if (newWidth == widthLayer) return;
        int newHeight =  Math.round(newWidth / ratioLayer);
        zoom(newWidth, newHeight);
    }
}