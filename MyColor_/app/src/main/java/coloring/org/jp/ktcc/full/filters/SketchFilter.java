package coloring.org.jp.ktcc.full.filters;

import android.graphics.Bitmap;

import coloring.org.jp.ktcc.full.opencv.OutputCV;

/**
 * Created by lenh on 2017/11/10.
 */

public class SketchFilter {
    // Abs
    public static final int ABS_STYLE = 1;
    // Divide normal
    public static final int DIVIDE_NORMAL_STYLE = 2;
    // Laplacian
    public static final int LAPLACIAN_STYLE = 3;
    // Sobel
    public static final int SOBEL_STYLE = 4;
    // Divide Bold
    public static final int DIVIDE_BOLD_STYLE = 5;
    // Sobel Abs
    public static final int SOBEL_ABS_STYLE = 6;
    // Pencil Light
    public static final int PENCIL_LIGHT_STYLE = 7;
    // Pencil Normal
    public static final int PENCIL_NORMAL_STYLE = 8;
    // Pencil Bold
    public static final int PENCIL_BOLD_STYLE = 9;
    // Saturation sketch
    public static final int SATURATION_STYLE = 10;
    // Canny Stylized
    public static final int CANNY_STYLE = 11;

    public static Bitmap filter(Bitmap bitmap, int styleNo, Object... options) {
        if (bitmap == null || bitmap.getWidth() == 0 || bitmap.getHeight() == 0) {
            return null;
        }
        switch (styleNo) {
            case ABS_STYLE: {
                return OutputCV.sketchAbs(bitmap);
            }
            case DIVIDE_NORMAL_STYLE: {
                return OutputCV.sketchDivideNormal(bitmap);
            }
            case LAPLACIAN_STYLE: {
                return OutputCV.sketchLaplacian(bitmap);
            }
            case SOBEL_STYLE: {
                return OutputCV.sketchSobel(bitmap);
            }
            case DIVIDE_BOLD_STYLE: {
                return OutputCV.sketchDivideBold(bitmap);
            }
            case SOBEL_ABS_STYLE: {
                return OutputCV.sketchSobelAbs(bitmap);
            }
            case PENCIL_LIGHT_STYLE: {
                return OutputCV.sketchPencilLight(bitmap);
            }
            case PENCIL_NORMAL_STYLE: {
                return OutputCV.sketchPencilNormal(bitmap);
            }
            case PENCIL_BOLD_STYLE: {
                return OutputCV.sketchPencilBold(bitmap);
            }
            default: return null;
        }
    }
}
