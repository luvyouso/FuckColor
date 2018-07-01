package coloring.org.jp.ktcc.full.util;

/**
 * Created by lenh on 2017/11/08.
 */

public class UtilNative {
    private UtilNative() {
    }

    public static native void setSketchTexture(long texMatAddr);

    public static native void pencilSketchFilter(long srcMatAddr, long dstMatAddr, int sketchBlend, int contrast);

    public static native void colorSketchFilter(long srcMatAddr, long dstMatAddr, int sketchBlend, int contrast);

    public static native void sketchPencil(long srcMatAddr, long dstMatAddr, int blurRadius, int contrast);

    public static native void colorCartoonFilter(long srcMatAddr, long dstMatAddr, int thickness, int threshold);

    public static native void grayCartoonFilter(long srcMatAddr, long dstMatAddr, int thickness, int threshold);

    public static native void oilPaintFilter(long srcMatAddr, long dstMatAddr, int radius, int levels);

    public static native void waterColorFilter(long srcMatAddr, long dstMatAddr, int spatialRadius, int colorRadius, int maxLevels, int scaleFactor);
}
