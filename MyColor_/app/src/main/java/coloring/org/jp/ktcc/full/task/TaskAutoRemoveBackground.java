package coloring.org.jp.ktcc.full.task;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import coloring.org.jp.ktcc.full.opencv.OutputCV;

/**
 * Created by anh.trinh on 12/21/2017.
 */

public class TaskAutoRemoveBackground extends AsyncTask<String, Bitmap, Bitmap> {
        Bitmap bitmap;
    public interface AutoRemoveBackgroundListener {
        void onDone(Bitmap bitmap);
    }

    AutoRemoveBackgroundListener mAutoRemoveBackgroundListener;

    public void setAutoRemoveBackgroundListener(AutoRemoveBackgroundListener listener) {
        this.mAutoRemoveBackgroundListener = listener;
    }
    public TaskAutoRemoveBackground(Bitmap bitmap, AutoRemoveBackgroundListener autoRemoveBackgroundListener) {
        this.bitmap = bitmap;
        this.mAutoRemoveBackgroundListener = autoRemoveBackgroundListener;
    }
    @Override
    protected Bitmap doInBackground(String... strings) {
        Bitmap resultBitmap = OutputCV.autoRemoveBackground(this.bitmap);
        return resultBitmap;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }


    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        if (result != null) {
            if (mAutoRemoveBackgroundListener != null) {
                mAutoRemoveBackgroundListener.onDone(result);
            }

            this.cancel(true);
        }
    }
}




