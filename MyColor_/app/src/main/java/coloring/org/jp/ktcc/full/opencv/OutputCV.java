package coloring.org.jp.ktcc.full.opencv;

import android.graphics.Bitmap;
import android.graphics.Matrix;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.photo.Photo;

import coloring.org.jp.ktcc.full.util.UtilNative;

import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;

/**
 * Created by nguyen on 10/20/2017.
 */

public class OutputCV {
    public static Bitmap output(Bitmap bitmap, int percentBold, int percentNoise){
        Mat bold = getBold(bitmap, percentBold);
        Bitmap bmResult = null;
        if (percentNoise == 0){
            bmResult = Bitmap.createBitmap(bold.cols(), bold.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(bold, bmResult);
            bold.release();
        }else {
            Mat noise = getNoise(bold, percentNoise);
            bold.release();
            bmResult = Bitmap.createBitmap(noise.cols(), noise.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(noise, bmResult);
            noise.release();
        }
        return bmResult;
    }
    //Sobel Algorithm
    private static Mat getBold(Bitmap bitmap, int percentBold){
        Mat src = new Mat();
        Mat grad_x = new Mat();
        Mat grad_y = new Mat();
        Mat abs_grad_x = new Mat();
        Mat abs_grad_y = new Mat();
        Mat bitwise = new Mat();

        float percentDetect = percentBold/ 10.f;
        int scale = 1;
        int ddepth = CvType.CV_16S;
        Utils.bitmapToMat(bitmap, src);
        Imgproc.GaussianBlur(src, src, new Size(3, 3), 0, 0, Core.BORDER_DEFAULT);
        Imgproc.cvtColor(src, src, COLOR_BGR2GRAY);

        //Sobel
        Imgproc.Sobel(src, grad_x, ddepth, 1, 0, 3, scale, percentDetect, Core.BORDER_DEFAULT);
        Imgproc.Sobel(src, grad_y, ddepth, 0, 1, 3, scale, percentDetect, Core.BORDER_DEFAULT);
        Core.convertScaleAbs(grad_x, abs_grad_x);
        Core.convertScaleAbs(grad_y, abs_grad_y);
        grad_x.release();
        grad_y.release();
        Core.addWeighted(abs_grad_x, percentDetect, abs_grad_y, percentDetect, 0, src);
        abs_grad_x.release();
        abs_grad_y.release();
        Core.bitwise_not(src, bitwise);
        src.release();
        return bitwise;
    }

    //Remove noise Algorithm
    private static Mat getNoise(Mat bold , int percentNoise){
        Mat noise = new Mat();
        Photo.fastNlMeansDenoising(bold, noise, percentNoise, 7, 25);
        return noise;
    }

    /**
     * Sharpening image from bitmap
     * @param bitmap: Source image
     * */
    public static Bitmap sharpen(Bitmap bitmap) {
        if (bitmap == null || bitmap.getWidth() == 0 || bitmap.getHeight() == 0) {
            return null;
        }
        try {
            Mat input = ReadCV.bitmapToMat(bitmap, CvType.CV_8U);

            Mat sharpen = new Mat(input.size(), input.type());
            Mat kern = new Mat(3, 3, CvType.CV_8S);
            int row = 0, col = 0;
            kern.put(row, col, 0, -1, 0, -1, 5, -1, 0, -1, 0);

            Imgproc.filter2D(input, sharpen, input.depth(), kern);

            Bitmap resultBitmap = ReadCV.matToBitmap(sharpen);

            input.release();
            sharpen.release();
            return resultBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sharpening image from mat
     * @param mat: Source mat
     * */
    public static Mat sharpen(Mat mat) {
        if (mat == null || mat.cols() == 0 || mat.rows() == 0) {
            return null;
        }
        try {
            Mat dst = new Mat(mat.size(), mat.type());

            Mat kern = new Mat(3, 3, CvType.CV_8S);
            int row = 0, col = 0;
            kern.put(row, col, 0, -1, 0, -1, 5, -1, 0, -1, 0);

            Imgproc.filter2D(mat, dst, mat.depth(), kern);

            return dst;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Opencv sketch abs style
     * @param bitmap: Source image
     * */
    public static Bitmap sketchAbs(Bitmap bitmap) {
        if (bitmap == null || bitmap.getWidth() == 0 || bitmap.getHeight() == 0) {
            return null;
        }
        try {
            Mat sourceMat = ReadCV.bitmapToMat(bitmap, CvType.CV_8U);

            Mat grayMat = new Mat();
            Imgproc.cvtColor(sourceMat, grayMat, Imgproc.COLOR_RGB2GRAY);

            Mat dilate = new Mat();
            Imgproc.dilate(grayMat, dilate, new Mat(), new Point(1, 1), 1);

            Mat absdiffMat = new Mat();
            Core.absdiff(dilate, grayMat, absdiffMat);

            Mat bitNot = new Mat();
            Core.bitwise_not(absdiffMat, bitNot);
            Bitmap rsBitmap = ReadCV.matToBitmap(bitNot);

            sourceMat.release();
            grayMat.release();
            dilate.release();
            absdiffMat.release();
            bitNot.release();

            return rsBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sketch with divide Bold Normal
     * @param bitmap: Source image
     * */
    public static Bitmap sketchDivideNormal(Bitmap bitmap) {
        if (bitmap == null || bitmap.getWidth() == 0 || bitmap.getHeight() == 0) {
            return null;
        }
        try {
            Mat sourceMat = ReadCV.bitmapToMat(bitmap, CvType.CV_8UC3);

            Mat grayMat = new Mat();
            Imgproc.cvtColor(sourceMat, grayMat, Imgproc.COLOR_BGR2GRAY);

            Mat dilate = new Mat();
            Imgproc.dilate(grayMat, dilate, new Mat(), new Point(1, 1), 1);

            Mat divide = new Mat();
            Core.divide(grayMat, dilate, divide, 256);
            Bitmap rsBitmap = ReadCV.matToBitmap(divide);

            sourceMat.release();
            grayMat.release();
            dilate.release();
            divide.release();


            // Chuyển gray về màu BGR bình thuờg
//            Mat result = new Mat();
//            Imgproc.cvtColor(divide, result, Imgproc.COLOR_GRAY2BGR);

            return rsBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sketch with laplacian
     * @param bitmap: Source image
     * */
    public static Bitmap sketchLaplacian(Bitmap bitmap) {
        if (bitmap == null || bitmap.getWidth() == 0 || bitmap.getHeight() == 0) {
            return null;
        }
        try {
            Mat sourceMat = ReadCV.bitmapToMat(bitmap, CvType.CV_8UC3);

            Mat gray = new Mat();
            Imgproc.cvtColor(sourceMat, gray, Imgproc.COLOR_RGB2GRAY);

            Mat laplacian = new Mat();
            Imgproc.Laplacian(gray, laplacian, CvType.CV_8U, 1, 1, 9, Core.BORDER_DEFAULT);

            Mat threshold = new Mat();
            Imgproc.threshold(laplacian, threshold, 0, 255,  Imgproc.THRESH_BINARY  | Imgproc.THRESH_OTSU);

            Mat bitwise = new Mat();
            Core.bitwise_not(threshold, bitwise);


            Bitmap rsBitmap = ReadCV.matToBitmap(bitwise);

            sourceMat.release();
            gray.release();
            laplacian.release();
            threshold.release();
            bitwise.release();

            return rsBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sketch with Sobel method
     * @param bitmap: Source image
     * */
    public static Bitmap sketchSobel(Bitmap bitmap) {
        if (bitmap == null || bitmap.getWidth() == 0 || bitmap.getHeight() == 0) {
            return null;
        }
        try {
            Mat sourceMat = ReadCV.bitmapToMat(bitmap, CvType.CV_8UC3);

            // 4: number of channels in the destination image; if the parameter is 0, the number of the channels is derived automatically from src and code
            Mat gray = new Mat();
            Imgproc.cvtColor(sourceMat, gray, Imgproc.COLOR_RGB2GRAY);

            Mat gradX = new Mat();
            Mat gradY = new Mat();
            Imgproc.Sobel(gray, gradX, CvType.CV_32F, 1, 0, 3, 1.0, Core.BORDER_DEFAULT);
            Imgproc.Sobel( gray, gradY, CvType.CV_32F, 0, 1, 3, 1.0, Core.BORDER_DEFAULT);

            Mat gradient = new Mat();
            Core.subtract(gradX, gradY, gradient);
            Core.convertScaleAbs(gradient, gradient);
            //Core.convertScaleAbs(gradient, gradient, 10, 0);

            Mat inverted = new Mat();
            Core.bitwise_not(gradient, inverted);

            //Imgproc.cvtColor(gradient, gradient, Imgproc.COLOR_GRAY2BGRA, 4);
            Bitmap result = ReadCV.matToBitmap(inverted);

            sourceMat.release();
            gray.release();
            gradX.release();
            gradY.release();
            gradient.release();
            inverted.release();

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sketch with divide Bold Weight
     * @param bitmap: Source image
     * */
    public static Bitmap sketchDivideBold(Bitmap bitmap) {
        if (bitmap == null || bitmap.getWidth() == 0 || bitmap.getHeight() == 0) {
            return null;
        }
        try {
            Mat sourceMat = ReadCV.bitmapToMat(bitmap, CvType.CV_8UC3);

            Mat grayMat = new Mat();
            Imgproc.cvtColor(sourceMat, grayMat, Imgproc.COLOR_BGR2GRAY);

            Mat dilate = new Mat();
            Imgproc.dilate(grayMat, dilate, new Mat(), new Point(1, 1), 3);

            Mat divide = new Mat();
            Core.divide(grayMat, dilate, divide, 255);
            Bitmap rsBitmap = ReadCV.matToBitmap(divide);

            sourceMat.release();
            grayMat.release();
            dilate.release();
            divide.release();

            return rsBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sketch with sobel and abs and filter fast
     * @param bitmap: Source image
     * */
    public static Bitmap sketchSobelAbs(Bitmap bitmap) {
        if (bitmap == null || bitmap.getWidth() == 0 || bitmap.getHeight() == 0) {
            return null;
        }
        try {
            Mat src = ReadCV.bitmapToMat(bitmap, CvType.CV_8UC3);

            Imgproc.GaussianBlur(src, src, new Size(3, 3), 0, 0, Core.BORDER_DEFAULT);
            Imgproc.cvtColor(src, src, COLOR_BGR2GRAY);

            //Sobel
            Mat grad_x = new Mat();
            Imgproc.Sobel(src, grad_x, CvType.CV_16S, 1, 0, 3, 1, 1.5, Core.BORDER_DEFAULT);

            Mat grad_y = new Mat();
            Imgproc.Sobel(src, grad_y, CvType.CV_16S, 0, 1, 3, 1, 1.5, Core.BORDER_DEFAULT);

            Mat abs_grad_x = new Mat();
            Core.convertScaleAbs(grad_x, abs_grad_x);

            Mat abs_grad_y = new Mat();
            Core.convertScaleAbs(grad_y, abs_grad_y);

            Core.addWeighted(abs_grad_x, 1.5, abs_grad_y, 1.5, 0, src);

            Mat bitwise = new Mat();
            Core.bitwise_not(src, bitwise);

            Bitmap rsBitmap = ReadCV.matToBitmap(bitwise);

            src.release();
            grad_x.release();
            grad_y.release();
            abs_grad_x.release();
            abs_grad_y.release();
            bitwise.release();

            return rsBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sketch pencil style light
     * @param bitmap: Source image
     * */
    public static Bitmap sketchPencilLight(Bitmap bitmap) {
        if (bitmap == null || bitmap.getWidth() == 0 || bitmap.getHeight() == 0) {
            return null;
        }
        try {
            // Custom value
            int blurRadius = 10;
            int contrast = 30;
            Mat sourceMat = ReadCV.bitmapToMat(bitmap, CvType.CV_8UC3);
            if (sourceMat.channels() < 4) {
                Imgproc.cvtColor(sourceMat, sourceMat, Imgproc.COLOR_RGB2BGRA);
            }

            UtilNative.sketchPencil(sourceMat.getNativeObjAddr(), sourceMat.getNativeObjAddr(), blurRadius, contrast);

            Bitmap rsBitmap = ReadCV.matToBitmap(sourceMat);
            return rsBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sketch pencil style normal
     * @param bitmap: Source image
     * */
    public static Bitmap sketchPencilNormal(Bitmap bitmap) {
        if (bitmap == null || bitmap.getWidth() == 0 || bitmap.getHeight() == 0) {
            return null;
        }
        try {
            // Custom value
            int blurRadius = 20;
            int contrast = 50;
            Mat sourceMat = ReadCV.bitmapToMat(bitmap, CvType.CV_8UC3);
            if (sourceMat.channels() < 4) {
                Imgproc.cvtColor(sourceMat, sourceMat, Imgproc.COLOR_RGB2BGRA);
            }

            UtilNative.sketchPencil(sourceMat.getNativeObjAddr(), sourceMat.getNativeObjAddr(), blurRadius, contrast);

            Bitmap rsBitmap = ReadCV.matToBitmap(sourceMat);
            return rsBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sketch pencil style bold
     * @param bitmap: Source image
     * */
    public static Bitmap sketchPencilBold(Bitmap bitmap) {
        if (bitmap == null || bitmap.getWidth() == 0 || bitmap.getHeight() == 0) {
            return null;
        }
        try {
            // Custom value
            int blurRadius = 40;
            int contrast = 60;
            Mat sourceMat = ReadCV.bitmapToMat(bitmap, CvType.CV_8UC3);
            if (sourceMat.channels() < 4) {
                Imgproc.cvtColor(sourceMat, sourceMat, Imgproc.COLOR_RGB2BGRA);
            }

            UtilNative.sketchPencil(sourceMat.getNativeObjAddr(), sourceMat.getNativeObjAddr(), blurRadius, contrast);

            Bitmap rsBitmap = ReadCV.matToBitmap(sourceMat);
            return rsBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Auto remove background image
     * @param bitmap: Source image
     * */
    public static Bitmap autoRemoveBackground(Bitmap bitmap) {
        if (bitmap == null || bitmap.getWidth() == 0 || bitmap.getHeight() == 0) {
            return null;
        }
        try {
            Point topLeft = new Point(1, 1);
            Point bottomRight = new Point(bitmap.getWidth() - 1, bitmap.getHeight() - 1);

            Mat sourceMat = ReadCV.bitmapToMat(bitmap, CvType.CV_8UC3);

            Mat backupMat = sourceMat.clone();

            Imgproc.cvtColor(sourceMat , sourceMat , Imgproc.COLOR_BGRA2BGR);

            // Backgroud
            Mat background = new Mat(sourceMat.size(), CvType.CV_8UC3, new Scalar(255, 0, 0));

            Mat firstMask = new Mat();
            Rect rect = new Rect(topLeft, bottomRight);
            Mat bgModel = new Mat();
            Mat fgModel = new Mat();
            // Call grabcut
            Imgproc.grabCut(sourceMat, firstMask, rect, bgModel, fgModel, 5, Imgproc.GC_INIT_WITH_RECT);

            Mat source = new Mat(1, 1, CvType.CV_8U, new Scalar(Imgproc.GC_PR_FGD));
            Core.compare(firstMask, source, firstMask, Core.CMP_EQ);

            // Foreground
            Mat foreground = new Mat(sourceMat.size(), CvType.CV_8UC3, new Scalar(255, 255, 255));
            sourceMat.copyTo(foreground, firstMask);

            Scalar color = new Scalar(255, 0, 0, 255);
            Imgproc.rectangle(sourceMat, topLeft, bottomRight, color);

            Mat tmp = new Mat();
            Imgproc.resize(background, tmp, sourceMat.size());
            background = tmp;

            // Mask
            Mat mask;
            mask = new Mat(foreground.size(), CvType.CV_8UC1,  new Scalar(255, 255, 255));

            Imgproc.cvtColor(foreground, mask, Imgproc.COLOR_BGR2GRAY);

            Imgproc.threshold(mask, mask, 254, 255, Imgproc.THRESH_BINARY_INV);

            Mat clearMask = sharpen(mask);

            Mat vals = new Mat(1, 1, CvType.CV_8UC3, new Scalar(0.0));

            Mat dst = new Mat();
            background.copyTo(dst);
            background.setTo(vals, clearMask);
            Core.add(background, foreground, dst, clearMask);

            Scalar oldColor = new Scalar(255, 0, 0);
            Scalar newColor = new Scalar(0, 0, 0, 0);
            dst = replaceColor(dst, backupMat, oldColor, oldColor, newColor);

            bgModel.release();
            fgModel.release();
            firstMask.release();
            source.release();
            vals.release();
            mask.release();
            clearMask.release();
            sourceMat.release();
            foreground.release();
            background.release();
            tmp.release();

            Bitmap bmResult = ReadCV.matToBitmap(dst);
            return bmResult;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Semi auto remove background image
     * @param bitmap: Source image
     * @param seedPoint: User touch point
     * @param thredshold: Difference range check [0, 100] -> Default = 25
     * */
    public static Bitmap semiAutoRemove(Bitmap bitmap, Point seedPoint, int thredshold) {
        if (bitmap == null || bitmap.getWidth() == 0 || bitmap.getHeight() == 0) {
            return null;
        }
        try {
            Mat sourceMat = ReadCV.bitmapToMat(bitmap, CvType.CV_8UC3);

            Mat rbg = new Mat();
            Imgproc.cvtColor(sourceMat, rbg, Imgproc.COLOR_BGRA2BGR);

            Scalar newVal = new Scalar(255,0,0);
            Rect rect = new Rect();
            Scalar difference = new Scalar (thredshold, thredshold, thredshold);

            Imgproc.floodFill(rbg, new Mat(), seedPoint, newVal, rect, difference, difference, 4 + (255 << 8) + Imgproc.FLOODFILL_FIXED_RANGE);
            Mat transparent = replaceColor(rbg, sourceMat, newVal, newVal, new Scalar(0, 0, 0, 0));

            Bitmap bmResult = ReadCV.matToBitmap(transparent);

            sourceMat.release();
            rbg.release();
            transparent.release();

            return bmResult;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Replace Image color
     * @param rbg: Rbg image mat
     * @param rbga: Rbga image mat
     * @param lowDiffColor: low difference color
     * @param upDiffColor: up difference color
     * @param newColor: new Color
     * */
    public static Mat replaceColor(Mat rbg, Mat rbga, Scalar lowDiffColor, Scalar upDiffColor, Scalar newColor) {
        Mat mask = new Mat();
        Core.inRange(rbg, lowDiffColor, upDiffColor, mask);
        if (rbga == null) {
            rbga = rbg.clone();
        }
        if (rbga.channels() < 4) {
            Imgproc.cvtColor(rbga, rbga, Imgproc.COLOR_BGR2BGRA);
        }
        rbga.setTo(newColor, mask);
        mask.release();
        rbg.release();
        return rbga;
    }

    /**
     * Flip image
     * @param bitmap: Source image
     * @param horizontal: is flip x (true is flip, false is not)
     * @param vertical: is flip y (true is flip, false is not)
     * */
    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        if (bitmap == null || bitmap.getWidth() == 0 || bitmap.getHeight() == 0) {
            return null;
        }
        try {
            Matrix matrix = new Matrix();
            matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
