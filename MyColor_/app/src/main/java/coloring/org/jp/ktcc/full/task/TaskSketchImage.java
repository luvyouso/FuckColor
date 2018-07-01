package coloring.org.jp.ktcc.full.task;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import coloring.org.jp.ktcc.full.opencv.OutputCV;
import coloring.org.jp.ktcc.full.util.AppConstants;
import coloring.org.jp.ktcc.full.util.UtilBitmap;

/**
 * Created by anh.trinh on 12/21/2017.
 */

public class TaskSketchImage extends AsyncTask<String, Bitmap, Bitmap> {
        Bitmap bitmap;
        int type;
        boolean isCircle;
    public interface TaskSketchImageListener {
        void onDone(Bitmap bitmap, int type);
    }

    TaskSketchImageListener mTaskSketchImageListener;

    public void setTaskSyncImageListener(TaskSketchImageListener listener) {
        this.mTaskSketchImageListener = listener;
    }
    public TaskSketchImage(Bitmap bitmap,int type, TaskSketchImageListener taskSketchImageListener, boolean isCircle) {
        this.bitmap = bitmap;
        this.type = type;
        this.mTaskSketchImageListener = taskSketchImageListener;
        this.isCircle = isCircle;
    }
    @Override
    protected Bitmap doInBackground(String... strings) {
        Bitmap result = null;
        switch (type+1){
            case AppConstants.SKETCH_TYPE_ABS:
                result = OutputCV.sketchAbs(this.bitmap);
                break;
            case AppConstants.SKETCH_TYPE_DIVIDEBOLD:
                result = OutputCV.sketchDivideBold(this.bitmap);
                break;
            case AppConstants.SKETCH_TYPE_DIVIDENORMAL:
                result = OutputCV.sketchDivideNormal(this.bitmap);
                break;
            case AppConstants.SKETCH_TYPE_LAPLACIAN:
                result = OutputCV.sketchLaplacian(this.bitmap);
                break;
            case AppConstants.SKETCH_TYPE_PENCILBOLD:
                result = OutputCV.sketchPencilBold(this.bitmap);
                break;
            case AppConstants.SKETCH_TYPE_PENCILLIGHT:
                result = OutputCV.sketchPencilLight(this.bitmap);
                break;
            case AppConstants.SKETCH_TYPE_PENCILNORMAL:
                result = OutputCV.sketchPencilNormal(this.bitmap);
                break;
            case AppConstants.SKETCH_TYPE_SOBEL:
                result = OutputCV.sketchSobel(this.bitmap);
                break;
            case AppConstants.SKETCH_TYPE_SOBELABS:
                result = OutputCV.sketchSobelAbs(this.bitmap);
                break;

        }
        if(isCircle){
            result = UtilBitmap.getCroppedBitmap(result);
        }
        return result;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }


    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        if (result != null) {
            if (mTaskSketchImageListener != null) {
                mTaskSketchImageListener.onDone(result, type);
            }

            this.cancel(true);
        }
    }
}




