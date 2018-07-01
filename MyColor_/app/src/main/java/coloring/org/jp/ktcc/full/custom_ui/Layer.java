package coloring.org.jp.ktcc.full.custom_ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;

import coloring.org.jp.ktcc.full.R;
import coloring.org.jp.ktcc.full.filters.ColorEffectFilter;
import coloring.org.jp.ktcc.full.opencv.OutputCV;

/**
 * Created by nguyen on 11/15/2017.
 */

public class Layer extends android.support.v7.widget.AppCompatImageView {

    private int width;
    private int height;
    private int widthOriginal;
    private int heightOriginal;
    private boolean isShowFixRadio = false;
    private ColorEffectFilter colorEffectFilter = null;
    private Bitmap originalBitmap = null;
    private boolean isFlipHorizontal = false;
    private boolean isFlipVertical = false;


    public ColorEffectFilter getColorEffectFilter() {
        if (colorEffectFilter == null){
            colorEffectFilter = new ColorEffectFilter(getContext());
        }
        return colorEffectFilter;
    }
    public void setColorEffectFilter(float opacity, float hue, float tint, float sepia, float temperature, float brightness, float contrast, float saturation, float blur){
        if(this.colorEffectFilter == null)
            this.colorEffectFilter = new ColorEffectFilter(getContext());
        this.colorEffectFilter.setAlpha(opacity);
        this.colorEffectFilter.setHue(hue);
        this.colorEffectFilter.setBrightness(brightness);
        this.colorEffectFilter.setContrast(contrast);
        this.colorEffectFilter.setSaturation(saturation);
        this.colorEffectFilter.setSepia(sepia);
        this.colorEffectFilter.setTemperature(temperature);
        this.colorEffectFilter.setTint(tint);
        this.colorEffectFilter.setBlur(blur);
        updateBitmap(false);
    }
    public int getHeightOriginal() {
        return heightOriginal;
    }
    public int getWidthOriginal() {
        return widthOriginal;
    }
    public boolean isShowFixRadio() {
        return isShowFixRadio;
    }
    public void setShowFixRadio(boolean showFixRadio) {
        isShowFixRadio = showFixRadio;
    }
    public Layer(Context context) {
        super(context);
        init();
    }
    public Layer(Context context,Layer layer) {
        super(context);
        init();
        this.width = layer.getMeasuredWidth();
        this.height = layer.getMeasuredHeight();
        this.widthOriginal = layer.getWidthOriginal();
        this.heightOriginal = layer.getHeightOriginal();
        this.isShowFixRadio = layer.isShowFixRadio();
        this.originalBitmap = layer.getOriginalBitmap();
        if(colorEffectFilter == null){
            colorEffectFilter = new ColorEffectFilter(getContext(),originalBitmap);
        }
        this.colorEffectFilter.setBitmap(context,this.originalBitmap);
        this.setFlipHorizontal(layer.isFlipHorizontal());
        this.setFlipVertical(layer.isFlipVertical());
        this.setColorEffectFilter(layer.getColorEffectFilter().getAlpha(),layer.getColorEffectFilter().getHue(),layer.getColorEffectFilter().getTint(),layer.getColorEffectFilter().getSepia(),layer.getColorEffectFilter().getTemperature(),layer.getColorEffectFilter().getBrightness(),layer.getColorEffectFilter().getContrast(),layer.getColorEffectFilter().getSaturation(),layer.getColorEffectFilter().getBlur());


    }
    public Layer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public Layer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    public void init(){
        setScaleType(ScaleType.FIT_XY);
    }

    public void setLayerBitmap(Bitmap bm, boolean isCrop){
       this.setLayerBitmap(bm, isCrop, false);
    }
    public void setLayerBitmap(Bitmap bm, boolean isCrop, boolean isCopy){
        if(isCrop) {
            widthOriginal = bm.getWidth();
            heightOriginal = bm.getHeight();
            if(! isCopy){
            int dimenPix = (int) getResources().getDimension(R.dimen.size_finger);
            width = dimenPix;
            height = dimenPix;
            if (bm.getWidth() > bm.getHeight()) {
                height = (int) ((bm.getHeight() * 1.0f / bm.getWidth()) * width);
            } else {
                if (bm.getWidth() < bm.getHeight()) {
                    width = (int) ((bm.getWidth() * 1.0f / bm.getHeight()) * height);
                }
            }
            }
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width, height);
            lp.gravity = Gravity.CENTER;
            setLayoutParams(lp);
            setImageBitmap(bm);
            ((BitmapDrawable) getDrawable()).setAntiAlias(true);
        }else{
            setImageBitmap(bm);
        }
    }
    public Bitmap getOriginalBitmap() {
        return originalBitmap;
    }

    public void setOriginalBitmap(Bitmap originalBitmap, boolean isCrop) {
        this.setOriginalBitmap(originalBitmap, isCrop, false);
    }

    public void setOriginalBitmap(Bitmap originalBitmap, boolean isCrop, boolean isCopy) {
        this.originalBitmap = originalBitmap;

        if(colorEffectFilter ==null) {
            this.colorEffectFilter = new ColorEffectFilter(getContext(), originalBitmap);

        }
        this.colorEffectFilter.setBitmap(getContext(), this.originalBitmap);
        updateBitmap(isCrop, isCopy);
    }
    public boolean isFlipHorizontal() {
        return isFlipHorizontal;
    }

    public void setFlipHorizontal(boolean flipHorizal) {
        isFlipHorizontal = flipHorizal;
        updateBitmap(false);
    }

    public boolean isFlipVertical() {
        return isFlipVertical;
    }

    public void setFlipVertical(boolean flipVertical) {
        isFlipVertical = flipVertical;
        updateBitmap(false);
    }
    private void updateBitmap(boolean isCrop){
       this.updateBitmap(isCrop, false);
    }
    private void updateBitmap(boolean isCrop, boolean isCopy){
        Bitmap bitmap = originalBitmap;
        if(colorEffectFilter!=null){
            bitmap = colorEffectFilter.apply();
        }
        if(isFlipHorizontal || isFlipVertical){
            bitmap = OutputCV.flip(bitmap, isFlipHorizontal, isFlipVertical);
        }
        if(bitmap!=null){
            setLayerBitmap(bitmap, isCrop, isCopy);
        }
    }
    public void flipHorizontal(){
        setFlipHorizontal(!isFlipHorizontal);
    }
    public void flipVertical(){
        setFlipHorizontal(!isFlipVertical);
    }
    public void flip(){
        isFlipHorizontal =! isFlipHorizontal;
        isFlipVertical = ! isFlipVertical;
        updateBitmap(false);

    }


    }
