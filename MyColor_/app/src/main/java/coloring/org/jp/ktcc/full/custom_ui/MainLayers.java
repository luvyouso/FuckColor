package coloring.org.jp.ktcc.full.custom_ui;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Collections;

import coloring.org.jp.ktcc.full.R;
import coloring.org.jp.ktcc.full.util.UtilDevice;

/**
 * Created by nguyen on 11/13/2017.
 */

public class MainLayers extends FrameLayout {
    private ListenerMainLayers listenerMainLayers = null;
    private View mRoot = null;
    private Bitmap mBitmapBackground = null;
    private ArrayList<Bitmap> bitmapLayers = new ArrayList<Bitmap>();
    private ArrayList<Layer> layers = new ArrayList<Layer>();
    private Finger finder;
    private View borderBackground;
    private FrameLayout.LayoutParams paramsBackground = null;
    private AppCompatImageView imageBackground = null;
    private int widthMain = 0;
    private int heightMain = 0;
    private int heightBar = 0;
    DisplayMetrics displayMetrics = null;

    public void setListenerMainLayers(ListenerMainLayers listenerMainLayers) {
        this.listenerMainLayers = listenerMainLayers;
    }

    public ArrayList<Bitmap> getBitmapLayers() {
        return bitmapLayers;
    }

    public MainLayers(Context context) {
        super(context);
        init();
    }

    public MainLayers(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MainLayers(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

//    public MainLayers(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        init();
//    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        this.mRoot = inflater.inflate(R.layout.main_layers, this);
        this.initAvailableView(mRoot);
        this.setEvent();
        displayMetrics = UtilDevice.getScreen((Activity) getContext());
        heightBar = (int) (getResources().getDimensionPixelOffset(R.dimen.height_bar) + 0.5f);
    }

    private void initAvailableView(View root) {
        finder = root.findViewById(R.id.finder);
        finder.setFingerListener(new Finger.FingerListener() {
            @Override
            public void onTouchCopy() {
                addLayer(getCurrentLayerSelected(), true);
                if (listenerMainLayers != null) {
                    listenerMainLayers.copySelectLayer();
                }

            }
        });
        finder.setVisibility(GONE);
        borderBackground = root.findViewById(R.id.borderBackground);
        borderBackground.setVisibility(GONE);
    }

    private void setEvent() {
        finder.setListenerFinger(listenerFinger);
    }

    public void selectLayer(int position) {
        if (position < 0 || layers.size() < position) {
            finder.selectBackground();
            return;
        }
        borderBackground.setVisibility(GONE);
        Layer layer = layers.get(position);
        boolean isResetPosition = false;
        if (layer.getWidth() > 0 && layer.getHeight() > 0) {

            Rect rectMain = new Rect();
            getHitRect(rectMain);

            Rect rectLayer = new Rect();
            layer.getHitRect(rectLayer);

            isResetPosition = !Rect.intersects(rectMain, rectLayer);
            //Log.d("nguyennguyen", Rect.intersects(rectMain, rectLayer) + "");
        }
        finder.selectLayer(layers.get(position), isResetPosition);
    }

    public void doubleClickLayer(int position) {
        if (position < 0 || layers.size() < position) {
            finder.selectBackground();
            return;
        }
        borderBackground.setVisibility(GONE);

        finder.selectLayer(layers.get(position), true);
    }

    public void addLayer(Bitmap bm) {
        bitmapLayers.add(bm);
        final int position = bitmapLayers.size() - 1;
        layers.add(new Layer(getContext()));
        layers.get(position).setOriginalBitmap(bm, true);
        addView(layers.get(position), position + 1);
        finder.addLayer(layers.get(position));
        selectLayer(position);
    }

    public void addLayer(Layer layer, boolean isCopy) {
        bitmapLayers.add(layer.getOriginalBitmap());
        final int position = bitmapLayers.size() - 1;
        layers.add(new Layer(getContext(), layer));
        layers.get(position).setOriginalBitmap(layer.getOriginalBitmap(), true, isCopy);
        addView(layers.get(position), position + 1);
        finder.addLayer(layers.get(position));
        selectLayer(position);
    }

    public void updateLayer(Bitmap bm, int position, boolean isCrop) {
        if (bitmapLayers != null && position > -1 && position < bitmapLayers.size()) {
            bitmapLayers.remove(position);
            bitmapLayers.add(position, bm);
            layers.get(position).setOriginalBitmap(bm, isCrop);
            finder.addLayer(layers.get(position));
        }

    }

    public void removeLayer(int position) {
        if (bitmapLayers != null && position > -1 && position < bitmapLayers.size()) {
            removeView(layers.get(position));
            bitmapLayers.remove(position);
            layers.remove(position);
            selectLayer(position == bitmapLayers.size() ? position - 1 : position);
        }

    }

    public void removeAllLayer() {
        if (bitmapLayers != null) {
            bitmapLayers.clear();
            for (Layer layer : layers
                    ) {
                removeView(layer);

            }
            layers.clear();
            removeView(imageBackground);
            finder.setVisibility(GONE);
            borderBackground.setVisibility(GONE);
            mBitmapBackground = null;
            imageBackground = null;
        }

    }

    public void updateBackgound(Bitmap bitmap) {
        mBitmapBackground = bitmap;
        imageBackground.setImageBitmap(bitmap);
    }


    public void addBackground(Bitmap bm) {
        borderBackground.setVisibility(VISIBLE);
        mBitmapBackground = bm;
        if (mBitmapBackground.getWidth() > mBitmapBackground.getHeight()) {
            ((Activity) getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            widthMain = displayMetrics.heightPixels;
            heightMain = displayMetrics.widthPixels - 2 * heightBar;
        } else {
            ((Activity) getContext()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            widthMain = displayMetrics.widthPixels;
            heightMain = displayMetrics.heightPixels - 2 * heightBar;
        }
        paramsBackground = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        paramsBackground.gravity = Gravity.CENTER;
        imageBackground = new AppCompatImageView(getContext());
        imageBackground.setLayoutParams(paramsBackground);
        imageBackground.setImageBitmap(bm);
        addView(imageBackground, 0);

        imageBackground.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                imageBackground.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                float ratioBitmap = (1.0f * mBitmapBackground.getWidth()) / mBitmapBackground.getHeight();
                float ratioMainLayer = (1.0f * widthMain) / heightMain;
                if (ratioBitmap > ratioMainLayer) {
                    paramsBackground.width = widthMain;
                    paramsBackground.height = (int) ((1.0f * mBitmapBackground.getHeight() * widthMain) / mBitmapBackground.getWidth());
                } else {
                    paramsBackground.width = (int) ((1.0f * mBitmapBackground.getWidth() * heightMain) / mBitmapBackground.getHeight());
                    paramsBackground.height = heightMain;
                }
                imageBackground.setLayoutParams(paramsBackground);
                borderBackground.setLayoutParams(paramsBackground);
            }
        });

        finder.selectBackground();
        imageBackground.setOnClickListener(clickListener);
    }

    public void addBackgroundPrint(Bitmap bm) {
        borderBackground.setVisibility(VISIBLE);
        mBitmapBackground = bm;
        if (mBitmapBackground.getWidth() > mBitmapBackground.getHeight()) {
//            ((Activity)getContext()).setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            widthMain = displayMetrics.heightPixels;
            heightMain = displayMetrics.widthPixels - 2 * heightBar;
        } else {
//            ((Activity)getContext()).setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            widthMain = displayMetrics.widthPixels;
            heightMain = displayMetrics.heightPixels - 2 * heightBar;
        }
        paramsBackground = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        paramsBackground.gravity = Gravity.CENTER;
        imageBackground = new AppCompatImageView(getContext());
        imageBackground.setLayoutParams(paramsBackground);
        imageBackground.setImageBitmap(bm);
        addView(imageBackground, 0);

        imageBackground.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                imageBackground.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                float ratioBitmap = (1.0f * mBitmapBackground.getWidth()) / mBitmapBackground.getHeight();
                float ratioMainLayer = (1.0f * widthMain) / heightMain;
                if (ratioBitmap > ratioMainLayer) {
                    paramsBackground.width = widthMain;
                    paramsBackground.height = (int) ((1.0f * mBitmapBackground.getHeight() * widthMain) / mBitmapBackground.getWidth());
                } else {
                    paramsBackground.width = (int) ((1.0f * mBitmapBackground.getWidth() * heightMain) / mBitmapBackground.getHeight());
                    paramsBackground.height = heightMain;
                }
                imageBackground.setLayoutParams(paramsBackground);
                borderBackground.setLayoutParams(paramsBackground);
            }
        });

        finder.selectBackground();
        imageBackground.setOnClickListener(clickListener);
    }

    OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            selectBackground();
            if (listenerMainLayers != null) {
                listenerMainLayers.selectBackground();
            }
        }
    };

    public void selectBackground() {
        borderBackground.setVisibility(VISIBLE);
        finder.selectBackground();

    }

    private ListenerFinger listenerFinger = new ListenerFinger() {
        @Override
        public void focusLayer(Layer layer) {
            imageBackground.setClickable(false);
            borderBackground.setVisibility(GONE);
            for (int i = 0; i < layers.size(); i++) {
                if (layers.get(i).equals(layer)) {
                    if (listenerMainLayers != null) {
                        listenerMainLayers.selectLayer(i);
                    }
                    break;
                }
            }
        }

        @Override
        public void unFocusLayer(Layer layer) {
            imageBackground.setClickable(true);
        }
    };

    public Layer getCurrentLayerSelected() {
        return finder.getLayer();
    }

    public AppCompatImageView getBackgroundImageView() {
        return imageBackground;
    }

    public Bitmap getBitmapBackground() {
        return mBitmapBackground;
    }

    public void sortLayers(int fromPosition, int toPosition) {
        //Remove all layer
        for (int i = 0; i < layers.size(); i++) {
            removeView(layers.get(i));
        }
        //Sort
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(layers, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(layers, i, i - 1);
            }
        }
        //Add all layer
        for (int i = 0; i < layers.size(); i++) {
            addView(layers.get(i), i + 1);
        }
    }

    public void hideFinderControl() {
        finder.hideControl();
        if (borderBackground.getVisibility() == VISIBLE) {
            borderBackground.setVisibility(GONE);
        }
    }

    public void showFinderControl() {
        finder.showControl();
        if (finder.getVisibility() == GONE) {
            borderBackground.setVisibility(VISIBLE);
        }
    }
}
