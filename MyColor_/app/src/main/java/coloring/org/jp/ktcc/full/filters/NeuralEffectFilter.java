package coloring.org.jp.ktcc.full.filters;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

/**
 * Created by lenh on 2018/03/14.
 */

public class NeuralEffectFilter {
    private Bitmap origin;
    private Bitmap effect;

    Paint paint_1;
    Paint paint_2;
    public NeuralEffectFilter() {}
    public NeuralEffectFilter(Bitmap origin, Bitmap effect) {
        this.origin = origin;
        this.effect = effect;
        this.init();
    }
    private void init() {
        //this.effect = ImageProcessor.effectColorCartoon(this.origin);

        this.paint_1 = new Paint();
        this.paint_1.setAntiAlias(true);
        this.paint_1.setFilterBitmap(true);
        this.paint_1.setAlpha(255);

        this.paint_2 = new Paint();
        this.paint_2.setAntiAlias(true);
        this.paint_2.setFilterBitmap(true);
        this.paint_2.setAlpha(255);
        this.paint_2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));
    }

    public Bitmap getOrigin() {
        return origin;
    }

    public void setOrigin(Bitmap origin) {
        this.origin = origin;
    }

    public Bitmap getEffect() {
        return effect;
    }

    public void setEffect(Bitmap effect) {
        this.effect = effect;
    }

    /**
     * Range: 0 - 100
     * Default: 100
     * */
    public Bitmap filter(int radius) {
        Bitmap result = Bitmap.createBitmap(this.origin.getWidth(), this.origin.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        int effectRadius = (radius * 255) / 100;

        this.paint_1.setAlpha(255 - effectRadius);
        this.paint_2.setAlpha(effectRadius);
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        canvas.drawBitmap(this.origin, 0.0F, 0.0F, this.paint_1);
        canvas.drawBitmap(this.effect, 0.0F, 0.0F, this.paint_2);
        return result;
    }

}
