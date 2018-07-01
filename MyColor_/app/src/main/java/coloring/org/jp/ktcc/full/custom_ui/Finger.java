package coloring.org.jp.ktcc.full.custom_ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
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
public class Finger extends RelativeLayout{

    public interface FingerListener {
        void onTouchCopy();
    }

    public FingerListener getFingerListener() {
        return fingerListener;
    }

    public void setFingerListener(FingerListener fingerListener) {
        this.fingerListener = fingerListener;
    }

    private FingerListener fingerListener;

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private static final int ROTATION = 3;
    private static final int ZOOM_BUTTON_Y = 4;
    private static final int ZOOM_BUTTON_X = 5;
    private static final int ZOOM_BUTTON_XY = 6;

    //View
    ImageButton btnFreeEdit;
    ImageButton btnZoomY;
    ImageButton btnZoomXY;
    ImageButton btnZoomX;
    ImageButton btnRotation360;
    ImageButton btnFixRatio;
    ImageButton btnRotation90;
    ImageButton btnCopy;
    FrameLayout centerFinder;
    ///////////////////////////////////////////////////////////////////
    Layer mLayer = null;
    private int currentMode = NONE;
    private int widthLayer = 0;
    private int heightLayer = 0;
    private int marginLayer = 0;
    private int sizeFinger = 0;
    private int widthScreen;
    private int heightScreen;
    private int heightActionBar;
    private int plus = 0;
    private int borderCenter = 0;
    private ListenerFinger listenerFinger;
    FrameLayout.LayoutParams paramsFinder;
    float xBegin = 0;
    float yBegin = 0;
    float angleBegin = 0;
    float oldDist = 1f;
    private float xCenter = 0;
    private float yCenter = 0;
    private float radiusBegin = 0;
    private float radiusOld = 0;
    private int minFinger;
    public Layer getLayer() {
        return mLayer;
    }
    public void setListenerFinger(ListenerFinger listenerFinger) {
        this.listenerFinger = listenerFinger;
    }
    public Finger(Context context) {
        super(context);
        init();
    }
    public Finger(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public Finger(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {
        marginLayer = getMarginLayer();
        sizeFinger = getSizeFinger();
        heightActionBar = getActionBarHeight();
        minFinger = getMinFinger();
        borderCenter = getBorderCenter();
        DisplayMetrics displayMetrics = UtilDevice.getScreen((Activity)getContext());
        widthScreen = displayMetrics.widthPixels;
        heightScreen = displayMetrics.heightPixels;
        plus = widthScreen > heightScreen ? widthScreen/2 : heightScreen/2;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View root = inflater.inflate(R.layout.finger, this);
        this.initAvailableView(root);
        this.setEventFinger();
    }
    public int getMarginLayer(){
        return getResources().getDimensionPixelOffset(R.dimen.margin_border_finder) + getResources().getDimensionPixelOffset(R.dimen.border_finder);
    }
    public int getSizeFinger(){
        return getResources().getDimensionPixelOffset(R.dimen.size_finger);
    }
    public int getActionBarHeight() {
        final TypedArray ta = getContext().getTheme().obtainStyledAttributes(new int[] {android.R.attr.actionBarSize});
        return (int) ta.getDimension(0, 0);
    }
    public int getMinFinger() {
        return getResources().getDimensionPixelOffset(R.dimen.min_finger);
    }

    public int getBorderCenter(){
        return getResources().getDimensionPixelOffset(R.dimen.border_finder);
    }
    public void moveFingerToLayerSelect(){
        if (mLayer == null){
            return;
        }
        setAlpha(0f);
        paramsFinder = (FrameLayout.LayoutParams)getLayoutParams();
        int newWidth = mLayer.getLayoutParams().width + marginLayer * 2;
        int newHeight = mLayer.getLayoutParams().height + marginLayer * 2;
        boolean isMinWidth =  newWidth < minFinger ? true : false;
        boolean isMinHeight = newHeight < minFinger ? true : false;
        paramsFinder.width = isMinWidth ? minFinger : newWidth;
        paramsFinder.height = isMinHeight ? minFinger : newHeight;
        setLayoutParams(paramsFinder);
        new Handler().post(new Runnable(){
            @Override
            public void run() {
                int sizeCenterW = (int) ((getWidth() - marginLayer * 2 - mLayer.getWidth())/2f);
                int sizeCenterH = (int) ((getHeight() - marginLayer * 2 - mLayer.getHeight())/2f);
                float x = mLayer.getX() - marginLayer - sizeCenterW;
                float y = mLayer.getY() - marginLayer - sizeCenterH;
                animate().x(x).y(y).setDuration(0).start();
                setAlpha(1f);
            }
        });
        setRotation(mLayer.getRotation());
    }
    public void selectLayer(final Layer layer, boolean isResetPosition){
        setVisibility(VISIBLE);
        mLayer = layer;
        autoShowHideButtonRatio();
        if (isResetPosition){
            int x = CommonUtil.getScreenWidth((Activity)getContext())/2 - layer.getWidth()/2;
            int y = CommonUtil.getScreenHeight((Activity)getContext())/2 - layer.getHeight()/2;
            mLayer.setX(x);
            mLayer.setY(y);
        }
        moveFingerToLayerSelect();
    }
    public void addLayer(Layer layer){
        this.mLayer = layer;
        setTouchLayer(layer);
        moveFingerToLayerSelect();
    }
    private void initAvailableView(View root) {
        centerFinder = (FrameLayout)root.findViewById(R.id.centerFinder);
        btnFreeEdit = (ImageButton) root.findViewById(R.id.btnFreeEdit);
        btnZoomY = (ImageButton) root.findViewById(R.id.btnZoomY);
        btnZoomXY = (ImageButton) root.findViewById(R.id.btnZoomXY);
        btnZoomX = (ImageButton) root.findViewById(R.id.btnZoomX);
        btnRotation360 = (ImageButton) root.findViewById(R.id.btnRotation360);
        btnFixRatio = (ImageButton) root.findViewById(R.id.btnFixRatio);
        btnFixRatio.setVisibility(GONE);
        btnRotation90 = (ImageButton) root.findViewById(R.id.btnRotation90);
        btnCopy = (ImageButton) root.findViewById(R.id.btnCopy);
    }
    private void setEventFinger(){
        setTouchCenterFinger();
        setTouchZoomX();
        setTouchZoomY();
        setTouchZoomXY();
        setClickRotation90();
        setTouchRotation360();
        setTouchFreeEdit();
        setClickFixRatio();
        setClickCopy();
    }
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private float spacing(float xOld, float yOld, float xNew, float yNew) {
        float x = xOld - xNew;
        float y = yOld - yNew;
        return (float) Math.sqrt(x * x + y * y);
    }
    private float rotationAngle(MotionEvent event) {
        double delta_x = event.getX(0) - event.getX(1);
        double delta_y = event.getY(0) - event.getY(1);
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }
    private float rotationAngle(float xOld, float yOld, float xNew, float yNew) {
        double delta_x = xOld - xNew;
        double delta_y = yOld - yNew;
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }
    private void drag(final float xC, final float yC){
        if (mLayer != null){
            float an = mLayer.getRotation();
            mLayer.setRotation(0);
            setRotation(0);
            int sizeCenterW = (int) ((getWidth() - marginLayer * 2 - mLayer.getWidth())/2f);
            int sizeCenterH = (int) ((getHeight() - marginLayer * 2 - mLayer.getHeight())/2f);
            float x = xC - marginLayer - sizeCenterW;
            float y = yC - marginLayer - sizeCenterH;
            animate().x(x).y(y).setDuration(0).start();
            mLayer.animate().x(xC).y(yC).setDuration(0).start();
            mLayer.setRotation(an);
            setRotation(an);
        }
    }
    private void rotate(float angle){
        //Finger
        animate().rotationBy(angle).setDuration(0).setInterpolator(new LinearInterpolator()).start();
        //Layer
        if (mLayer != null){
            mLayer.animate().rotationBy(angle).setDuration(0).setInterpolator(new LinearInterpolator()).start();
        }
    }
    private boolean isMinFinger(int width, int height){
        boolean w =  width < minFinger ? true : false;
        boolean h = height < minFinger ? true : false;
        return w || h;
    }
    private boolean isMinLayer(int width, int height){
        boolean w =  width < 150 ? true : false;
        boolean h = height < 150 ? true : false;
        return w || h;
    }
    private boolean isMaxFinger(int width, int height){
            return width > widthScreen + plus || height > heightScreen + plus;
    }
    private void zoomXY(float scale){
        int newWidth = (int) ( (widthLayer + marginLayer * 2) * scale + 0.5f);
        int newHeight = (int) ((heightLayer + marginLayer * 2) * scale + 0.5f);
        zoom(newWidth, newHeight);
    }
    private void zoomY(float scale){
        int newWidth = (int)((widthLayer + marginLayer * 2) + 0.5f);
        int newHeight = (int)((heightLayer + marginLayer * 2) * scale + 0.5f);
        zoom(newWidth, newHeight);
    }
    private void zoomX(float scale){
        int newWidth = (int)((widthLayer + marginLayer * 2) * scale + 0.5f);
        int newHeight = (int)((heightLayer + marginLayer * 2) + 0.5f);
        zoom(newWidth, newHeight);
    }
    private void zoom(int newWidth, int newHeight){
        if (isMaxFinger(newWidth, newHeight) || isMinLayer(newWidth, newHeight)){
            return;
        }
        //Finger
        boolean isMinWidth =  newWidth < minFinger ? true : false;
        boolean isMinHeight = newHeight < minFinger ? true : false;
        paramsFinder = (FrameLayout.LayoutParams) getLayoutParams();
        paramsFinder.width = isMinWidth ? minFinger : newWidth;
        paramsFinder.height = isMinHeight ? minFinger : newHeight;
        setLayoutParams(paramsFinder);

        //Layer
        if (mLayer != null){
            FrameLayout.LayoutParams paramsLayer = (FrameLayout.LayoutParams) mLayer.getLayoutParams();
            int w = newWidth - marginLayer * 2;
            paramsLayer.width = w < 0 ? 0 : w;
            int h = newHeight - marginLayer * 2;
            paramsLayer.height = h < 0 ? 0 : h;
            mLayer.setLayoutParams(paramsLayer);
        }
    }
    private void setTouchCenterFinger(){
        centerFinder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return handlingTouchCenter(event);
            }
        });
    }
    private void setTouchLayer(final Layer layer) {
        layer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mLayer = layer;
                return handlingTouchCenter(event);
            }
        });
    }
    public void selectBackground(){
        setVisibility(GONE);
        mLayer = null;
    }
    private void setTouchZoomY(){
        btnZoomY.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return handlingTouchButtonZoom(event, ZOOM_BUTTON_Y);
            }
        });
    }
    private void setTouchZoomX(){
        btnZoomX.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return handlingTouchButtonZoom(event, ZOOM_BUTTON_X);
            }
        });
    }
    private void setClickFixRatio(){
        btnFixRatio.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                handlingClickButtonFixRatio();
            }
        });
    }
    private void setClickCopy(){
        btnCopy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
             final  MainActivity mainActivity = (MainActivity) getContext();
               mainActivity.showDialogProgress();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(fingerListener!=null){
                            fingerListener.onTouchCopy();
                        }
                        mainActivity.hideDialogProgress();
                    }

                }, 400);
            }
        });
    }
    private void setTouchZoomXY(){
        btnZoomXY.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return handlingTouchButtonZoom(event, ZOOM_BUTTON_XY);
            }
        });
    }
    private void hideButtonRatio(){
        if (mLayer != null){
            mLayer.setShowFixRadio(false);
        }
        btnFixRatio.setVisibility(GONE);
    }
    private void showButtonRatio(){
        if (mLayer != null){
            mLayer.setShowFixRadio(true);
        }
        btnFixRatio.setVisibility(VISIBLE);
    }
    private void autoShowHideButtonRatio(){
        if (mLayer != null){
            if(mLayer.isShowFixRadio()){
                showButtonRatio();
            }else {
                hideButtonRatio();
            }
        }
    }
    private void setClickRotation90(){
        btnRotation90.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                handlingClickButtonRotation90();
            }
        });
    }
    private void setTouchRotation360(){
        btnRotation360.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return handlingTouchButtonRotation360(event);
            }
        });
    }
    private void setTouchFreeEdit(){
        btnFreeEdit.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return handlingTouchButtonFreeEdit(event);
            }
        });
    }
    ////////////////////////////////////////////////// Handling /////////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean handlingTouchCenter(final MotionEvent event){
        if(mLayer!=null) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    if (currentMode != NONE || mLayer == null) {
                        return false;//End touch
                    }
                    moveFingerToLayerSelect();
                    autoShowHideButtonRatio();
                    if (listenerFinger != null && mLayer != null) {
                        listenerFinger.focusLayer(mLayer);
                    }
                    paramsFinder = (FrameLayout.LayoutParams) getLayoutParams();
                    widthLayer = mLayer.getWidth();
                    heightLayer = mLayer.getHeight();
                    xBegin = mLayer.getX() - event.getRawX();//Note: getX() - event.getRawX() = -event.getX(0)
                    yBegin = mLayer.getY() - event.getRawY();
                    setVisibility(VISIBLE);
                    currentMode = DRAG;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:// point 2
                    if (event.getPointerCount() >= 2) {
                        oldDist = spacing(event);
                        if (oldDist > 10f) {
                            currentMode = ZOOM;
                            //Case: 2 point -> 1 point -> 2 point
                        }
                        angleBegin = rotationAngle(event);
                    }
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
                    if (currentMode == ZOOM) {
                        if (event.getPointerCount() >= 2) {
                            float newDist = spacing(event);
                            if (newDist > 10f) {
                                float scale = (newDist / oldDist);
                                zoomXY(scale);
                            }
                            rotate(rotationAngle(event) - angleBegin);
                        }
                    }
                    if (currentMode == DRAG) {
                        drag(event.getRawX() + xBegin, event.getRawY() + yBegin);
                    }
                    break;
                default:
                    break;
            }
        }
        return true;
    }
    private boolean handlingTouchButtonZoom(MotionEvent event, int typeZoom){
        if(mLayer!=null) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    if (currentMode != NONE || mLayer == null) {
                        return false;//End touch
                    }
                    if (typeZoom == ZOOM_BUTTON_X || typeZoom == ZOOM_BUTTON_Y) {
                        showButtonRatio();
                    }
                    paramsFinder = (FrameLayout.LayoutParams) getLayoutParams();
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
                        case ZOOM_BUTTON_X:
                            zoomX(scale);
                            break;
                        case ZOOM_BUTTON_XY:
                            zoomXY(scale);
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
    private void handlingClickButtonRotation90(){
        if (mLayer != null){
            float temp = mLayer.getRotation() % 90;
            if (temp != 0){
                rotate(-temp);
            }else {
                rotate( 90);
            }
        }
    }
    private boolean handlingTouchButtonRotation360(MotionEvent event){
        if(mLayer!=null) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    if (currentMode != NONE || mLayer == null) {
                        return false;//End touch
                    }
                    paramsFinder = (FrameLayout.LayoutParams) getLayoutParams();
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
    private boolean handlingTouchButtonFreeEdit(MotionEvent event){
        if(mLayer!=null) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    if (currentMode != NONE || mLayer == null) {
                        return false;//End touch
                    }
                    paramsFinder = (FrameLayout.LayoutParams) getLayoutParams();
                    widthLayer = mLayer.getWidth();
                    heightLayer = mLayer.getHeight();
                    xCenter = mLayer.getX() + mLayer.getWidth() / 2;
                    yCenter = mLayer.getY() + mLayer.getHeight() / 2;
                    radiusBegin = spacing(event.getRawX(), event.getRawY(), xCenter, yCenter);
                    angleBegin = rotationAngle(xCenter, yCenter, event.getX(), event.getY());
                    currentMode = ROTATION;
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
                    zoomXY(scale);
                    setRotation(currentAngle);
                    mLayer.setRotation(currentAngle);
                    //Rotate
                    rotate(rotationAngle(xCenter, yCenter, event.getX(), event.getY()) - angleBegin);
                    break;
                default:
                    break;
            }
        }
        return true;
    }
    private void handlingClickButtonFixRatio(){
            if (mLayer != null){
                hideButtonRatio();
                FrameLayout.LayoutParams paramsLayer = (FrameLayout.LayoutParams) mLayer.getLayoutParams();
                if(mLayer.getWidth() > mLayer.getHeight()){
                    float h = (mLayer.getWidth() * mLayer.getHeightOriginal() * 1.0f) / mLayer.getWidthOriginal();
                    if(h >heightScreen + plus )
                    {
                        h = heightScreen + plus;
                        float w = (h * mLayer.getWidthOriginal() * 1.0f) / mLayer.getHeightOriginal();
                        paramsLayer.width = (int)w;
                    }
                    paramsLayer.height = (int) h;
                }else {
                    float w = (mLayer.getHeight() * mLayer.getWidthOriginal() * 1.0f) / mLayer.getHeightOriginal();
                    if(w > widthScreen + plus){
                        w = widthScreen+ plus;
                        float h = (w * mLayer.getHeightOriginal() * 1.0f) / mLayer.getWidthOriginal();
                        paramsLayer.height = (int) h;
                    }
                    paramsLayer.width = (int)w;
                }
                mLayer.setLayoutParams(paramsLayer);
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        moveFingerToLayerSelect();
                    }
                });
            }
    }
    public void hideControl(){
        centerFinder.setVisibility(INVISIBLE);
        btnFreeEdit.setVisibility(INVISIBLE);
        btnZoomY.setVisibility(INVISIBLE);
        btnZoomXY.setVisibility(INVISIBLE);
        btnZoomX.setVisibility(INVISIBLE);
        btnRotation360.setVisibility(INVISIBLE);
        btnFixRatio.setVisibility(INVISIBLE);
        btnRotation90.setVisibility(INVISIBLE);
        btnCopy.setVisibility(INVISIBLE);
    }
    public void showControl(){
        centerFinder.setVisibility(VISIBLE);
        btnFreeEdit.setVisibility(VISIBLE);
        btnZoomY.setVisibility(VISIBLE);
        btnZoomXY.setVisibility(VISIBLE);
        btnZoomX.setVisibility(VISIBLE);
        btnRotation360.setVisibility(VISIBLE);
        btnFixRatio.setVisibility(GONE);
        btnRotation90.setVisibility(VISIBLE);
        btnCopy.setVisibility(VISIBLE);
    }
}