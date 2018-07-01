package coloring.org.jp.ktcc.full.filters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Log;

/**
 * Created by lenh on 2017/11/10.
 */

public class ColorEffectFilter {
    // [0, 255] -> Default = 255
    private float alpha = 255.0F;
    final ColorMatrix alphaMatrix = new ColorMatrix();

    // [0, 360] -> Default = 180
    private float hue = 180.0F;
    final ColorMatrix hueMatrix = new ColorMatrix();

    // [0, 510] -> Default = 255
    private float tint = 255.0F;
    final ColorMatrix tintMatrix = new ColorMatrix();

    // [0, 510] -> Default = 255
    private float sepia = 255.0F;
    final ColorMatrix sepiaMatrix = new ColorMatrix();

    // [0, 510] -> Default = 255
    private float temperature = 255.0F;
    final ColorMatrix temperatureMatrix = new ColorMatrix();

    // [0, 510] -> Default = 255
    private float brightness = 255.0F;
    final ColorMatrix brightnessMatrix = new ColorMatrix();

    // [0, 510] -> Default = 255
    private float contrast = 255.0F;
    final ColorMatrix contrastMatrix = new ColorMatrix();

    // [0, 200] -> Default = 100
    private float saturation = 100.0F;
    final ColorMatrix saturationMatrix = new ColorMatrix();
    // [0, 510] -> Default = 255
    private float blur = 255.0F;
    private BlurEffectFilter blurEffectFilter = new BlurEffectFilter();

    private final ColorMatrix colorMatrix = new ColorMatrix();
    Paint paint = new Paint();
    private Context  context;

    public ColorEffectFilter(Context context) {
        this.paint.setAntiAlias(true);
        this.paint.setFilterBitmap(true);
        this.context = context;
    }
    public ColorEffectFilter(Context context, Bitmap bitmap) {
        this.paint.setAntiAlias(true);
        this.paint.setFilterBitmap(true);
        this.context = context;
        blurEffectFilter = new BlurEffectFilter(context,bitmap);
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
        float paramFloat = Math.min(Math.max(alpha, 0.0F), 255.0F) / 255.0F;
        this.alphaMatrix.set(new float[] { 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, paramFloat, 0.0F });
    }
    public float getHue() {
        return hue;
    }

    public void setHue(float hue) {
        this.hue = hue;
        float paramFloat = Math.min(Math.max(hue, 0.0F), 360.0F);
        if (paramFloat == 180.0F) {
            this.hueMatrix.reset();
            return;
        }
        float f1 = (paramFloat - 180.0F) / 180.0F * 3.1415927F;
        paramFloat = (float)Math.cos(f1);
        f1 = (float)Math.sin(f1);
        this.hueMatrix.set(new float[] { 0.213F + 0.787F * paramFloat + f1 * -0.213F, paramFloat * -0.715F + 0.715F + f1 * -0.715F, -0.072F * paramFloat + 0.072F + 0.928F * f1, 0.0F, 0.0F, 0.213F + paramFloat * -0.213F + 0.143F * f1, 0.28500003F * paramFloat + 0.715F + 0.14F * f1, -0.072F * paramFloat + 0.072F + -0.283F * f1, 0.0F, 0.0F, 0.213F + paramFloat * -0.213F + -0.787F * f1, paramFloat * -0.715F + 0.715F + f1 * 0.715F, f1 * 0.072F + (paramFloat * 0.928F + 0.072F), 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F });
    }

    public float getTint() {
        return tint;
    }

    public void setTint(float tint) {
        this.tint = tint;
        float paramFloat = (Math.min(Math.max(tint, 0.0F), 510.0F) - 255.0F) / 255.0F / 5.0F;
        this.tintMatrix.set(new float[] { 1.0F + paramFloat, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F - paramFloat, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, paramFloat * 2.0F + 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F });
    }

    public float getSepia() {
        return sepia;
    }

    public void setSepia(float sepia) {
        this.sepia = sepia;
        float paramFloat = Math.min(Math.max(sepia, 0.0F), 510.0F);
        if (paramFloat == 255.0F) {
            this.sepiaMatrix.reset();
            return;
        }
        if (paramFloat < 255.0F) {
            float f1 = (255.0F - paramFloat) / 255.0F;
            this.sepiaMatrix.set(new float[] { 1.0F - f1, 0.0F, 0.0F, 0.0F, 255.0F - paramFloat, 0.0F, 1.0F - f1, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F - f1, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F });
            return;
        }
        paramFloat = (paramFloat - 255.0F) / 255.0F;
        this.sepiaMatrix.set(new float[] { 1.0F - 0.787F * paramFloat, 0.715F * paramFloat, 0.072F * paramFloat, 0.0F, 0.0F, 0.202F * paramFloat, 1.0F - 0.321F * paramFloat, 0.068F * paramFloat, 0.0F, 0.0F, 0.175F * paramFloat, 0.586F * paramFloat, 1.0F - paramFloat * 0.941F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F });
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
        float paramFloat = (Math.min(Math.max(temperature, 0.0F), 510.0F) - 255.0F) / 255.0F / 5.0F;
        this.temperatureMatrix.set(new float[] { 1.0F + paramFloat, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F - paramFloat, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F });
    }

    public float getBrightness() {
        return brightness;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
        float f1 = (Math.min(Math.max(brightness, 0.0F), 510.0F) - 255.0F) / 255.0F;
        float paramFloat = 1.0F - f1;
        f1 *= 255.0F;
        brightnessMatrix.set(new float[] { paramFloat, 0.0F, 0.0F, 0.0F, f1, 0.0F, paramFloat, 0.0F, 0.0F, f1, 0.0F, 0.0F, paramFloat, 0.0F, f1, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F });
    }

    public float getContrast() {
        return contrast;
    }

    public void setContrast(float contrast) {
        this.contrast = contrast;
        float paramFloat = (Math.min(Math.max(contrast, 0.0F), 510.0F) - 255.0F) / 255.0F + 1.0F;
        float f1 = 127.5F * (1.0F - paramFloat);
        contrastMatrix.set(new float[] { paramFloat, 0.0F, 0.0F, 0.0F, f1, 0.0F, paramFloat, 0.0F, 0.0F, f1, 0.0F, 0.0F, paramFloat, 0.0F, f1, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F });
    }

    public float getSaturation() {
        return saturation;
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
        float paramFloat = Math.min(Math.max(saturation, 0.0F), 200.0F) / 100.0F;
        saturationMatrix.reset();
        saturationMatrix.setSaturation(paramFloat);
    }
    public float getBlur() {
        return blur;
    }
    public void setBlur(float blur) {
        this.blur = blur;

    }


    public void reset() {
        alpha = 255.0F;
        alphaMatrix.reset();
        hue = 180.0F;
        alphaMatrix.reset();
        tint = 255.0F;
        tintMatrix.reset();
        sepia = 255.0F;
        sepiaMatrix.reset();
        temperature = 255.0F;
        temperatureMatrix.reset();
        brightness = 255.0F;
        brightnessMatrix.reset();
        contrast = 255.0F;
        contrastMatrix.reset();
        saturation = 100.0F;
        saturationMatrix.reset();
        blur = 255.0F;

        colorMatrix.reset();
        alphaMatrix.reset();
        hueMatrix.reset();
        tintMatrix.reset();
        sepiaMatrix.reset();
        temperatureMatrix.reset();
        brightnessMatrix.reset();
        contrastMatrix.reset();
        saturationMatrix.reset();
    }

    public Bitmap apply() {
        Log.e("ColorFilter"," Alpha "+alpha+ " Hue "+hue+" Brightness "+brightness+" Contrast "+contrast+
                " Saturation "+saturation+ " Sepia "+sepia+" Temperature "+temperature+" Tint "+tint+" Blur "+blur);
        blurEffectFilter.setRadius(getBlur());
        Bitmap   bitmap = blurEffectFilter.filter();
        Bitmap resultBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultBitmap);
        this.paint.setColorFilter(this.collect());
        canvas.drawBitmap(bitmap, 0, 0, this.paint);
        return resultBitmap;
    }
    private Bitmap applyBlurEffect(Bitmap bitmap) {
       blurEffectFilter = new BlurEffectFilter(context,bitmap);
       blurEffectFilter.setRadius(getBlur());
       return blurEffectFilter.filter();
    }

    public ColorMatrixColorFilter collect() {
        this.colorMatrix.reset();
        this.colorMatrix.postConcat(this.alphaMatrix);
        this.colorMatrix.postConcat(this.hueMatrix);
        this.colorMatrix.postConcat(this.tintMatrix);
        this.colorMatrix.postConcat(this.sepiaMatrix);
        this.colorMatrix.postConcat(this.temperatureMatrix);
        this.colorMatrix.postConcat(this.brightnessMatrix);
        this.colorMatrix.postConcat(this.contrastMatrix);
        this.colorMatrix.postConcat(this.saturationMatrix);
        return new ColorMatrixColorFilter(this.colorMatrix);
    }
    public void setBitmap(Context context,Bitmap bitmap){
        blurEffectFilter = new BlurEffectFilter(context, bitmap);
    }

}
