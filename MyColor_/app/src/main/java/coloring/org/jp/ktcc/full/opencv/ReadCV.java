package coloring.org.jp.ktcc.full.opencv;

import android.graphics.Bitmap;
import android.util.DisplayMetrics;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import coloring.org.jp.ktcc.full.util.UtilDevice;

/**
 * Created by nguyen on 10/20/2017.
 */

public class ReadCV {
    public static Bitmap read(String path){
        //Get display
        Mat mat = Imgcodecs.imread(path);
        return resizeAuto(mat);
    }

    public static Bitmap resizeAuto(Bitmap bm) {
        //Get display
        DisplayMetrics displayMetrics = UtilDevice.getScreen();
        Mat mat = new Mat();
        Utils.bitmapToMat(bm, mat);
        //Detect resize
        float ratio = 1;
        int width = mat.cols();
        int height = mat.rows();
        if (width < height){
            ratio = height / (displayMetrics.heightPixels * 0.7f);
        }else {
            ratio = width / (displayMetrics.widthPixels * 0.7f);
        }
        ratio = ratio <= 1 ? 1 : 1 - (ratio - 1)/ratio;

        Imgproc.resize(mat, mat, new Size(width * ratio, height * ratio));
        //Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2RGBA);
        Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        return bitmap;
    }


    public static Bitmap resizeAuto(Mat mat) {
        //Get display
        DisplayMetrics displayMetrics = UtilDevice.getScreen();

        //Detect resize
        float ratio = 1;
        int width = mat.cols();
        int height = mat.rows();
        if (width < height){
            ratio = height / (displayMetrics.heightPixels * 0.7f);
        }else {
            ratio = width / (displayMetrics.widthPixels * 0.7f);
        }
        ratio = ratio <= 1 ? 1 : 1 - (ratio - 1)/ratio;

        Imgproc.resize(mat, mat, new Size(width * ratio, height * ratio));
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2RGBA);
        Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);

        return bitmap;
    }

    public static Bitmap read(String path, Size size){
        Mat mat = Imgcodecs.imread(path);
        Imgproc.resize(mat, mat, size);
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2RGBA);
        Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        return bitmap;
    }

    public static Mat bitmapToMat(Bitmap bitmap, int type) {
        Mat mat = new Mat(bitmap.getWidth(), bitmap.getHeight(), type);
        Utils.bitmapToMat(bitmap, mat);
        return mat;
    }

    public static Bitmap matToBitmap(Mat mat) {
        //if (mat.channels() < 4) {
        //    Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2RGBA);
        //}
        Bitmap bmp = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bmp, true);
        return bmp;
    }
}

