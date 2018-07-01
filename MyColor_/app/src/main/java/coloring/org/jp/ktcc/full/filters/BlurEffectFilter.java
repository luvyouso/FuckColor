package coloring.org.jp.ktcc.full.filters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import coloring.org.jp.ktcc.full.opencv.OutputCV;

/**
 * Created by lenh on 2017/11/10.
 */

public class BlurEffectFilter {
    float radius = 255.0F;
    float template = 1.0F;
    Paint paint_1;
    Paint paint_2;
    private RenderScript renderScript = null;
    private ScriptIntrinsicBlur scriptIntrinsicBlur = null;
    private Allocation allocationInput = null;
    private Allocation allocationOutput = null;
    private Bitmap origin;
    private Context context;
    private Bitmap sharpen;

    public BlurEffectFilter() {

    }
    public BlurEffectFilter(Context context, Bitmap origin) {
        this.origin = origin;
        this.context = context;
        this.initFilter();
    }

    public void initFilter() {
        this.sharpen = OutputCV.sharpen(this.origin);
        this.renderScript = RenderScript.create(context);
        this.scriptIntrinsicBlur = ScriptIntrinsicBlur.create(this.renderScript, Element.U8_4(this.renderScript));

        this.paint_1 = new Paint();
        this.paint_1.setAntiAlias(true);
        this.paint_1.setFilterBitmap(true);
        this.paint_1.setAlpha(255);

        this.paint_2 = new Paint();
        this.paint_2.setAntiAlias(true);
        this.paint_2.setFilterBitmap(true);
        this.paint_2.setAlpha(0);
        this.paint_2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));

        if (this.allocationInput != null) {
            this.allocationInput.destroy();
            this.allocationInput = null;
        }
        if (this.allocationOutput != null) {
            this.allocationOutput.destroy();
            this.allocationOutput = null;
        }

        this.allocationInput = Allocation.createFromBitmap(this.renderScript, this.origin);
        this.allocationOutput = Allocation.createTyped(this.renderScript, this.allocationInput.getType());
    }

    public void setOrigin(Bitmap origin) {
        this.origin = origin;
        this.initFilter();
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public Bitmap filter() {
        if(origin == null)
            return null ;
        Bitmap result = Bitmap.createBitmap(this.origin.getWidth(), this.origin.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);

        if (this.radius < 255.0F)
        {
            int blurRadius = (int)(255.0F - this.radius);
            this.paint_1.setAlpha(255 - blurRadius);
            this.paint_2.setAlpha(blurRadius);
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            canvas.drawBitmap(this.origin, 0.0F, 0.0F, this.paint_1);
            canvas.drawBitmap(this.sharpen, 0.0F, 0.0F, this.paint_2);
            return result;
        }
        if (this.radius == 255.0F)
        {
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            canvas.drawBitmap(this.origin, 0.0F, 0.0F, null);
            return result;
        }
        float f1 = Math.min((this.radius - 255.0F) * 10.0F / 255.0F * this.template, 25.0F);
        this.scriptIntrinsicBlur.setRadius(f1);
        this.scriptIntrinsicBlur.setInput(this.allocationInput);
        this.scriptIntrinsicBlur.forEach(this.allocationOutput);
        this.allocationOutput.copyTo(result);
        return result;
    }
}
