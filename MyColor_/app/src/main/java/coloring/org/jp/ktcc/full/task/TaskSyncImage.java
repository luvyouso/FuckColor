package coloring.org.jp.ktcc.full.task;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;

import coloring.org.jp.ktcc.full.util.UtilBitmap;

/**
 * Created by anh.trinh on 12/21/2017.
 */

public class TaskSyncImage extends AsyncTask<String, Bitmap, Bitmap> {
        View view;
        View viewCrop;
        Bitmap bitmapCrop;
        Boolean isCircle;
    public interface TaskSyncImageListener {
        void onDone(Bitmap bitmap);
    }

    TaskSyncImageListener mTaskSyncImageListener;

    public void setTaskSyncImageListener(TaskSyncImageListener listener) {
        this.mTaskSyncImageListener = listener;
    }
    public TaskSyncImage(View view,View viewCrop, TaskSyncImageListener taskSyncImageListener, boolean isCircle) {
        this.view = view;
        this.viewCrop = viewCrop;
        this.mTaskSyncImageListener = taskSyncImageListener;
        this.isCircle = isCircle;

    }
    public TaskSyncImage(View view,Bitmap bitmapCrop, TaskSyncImageListener taskSyncImageListener, boolean isCircle) {
        this.view = view;
        this.bitmapCrop = bitmapCrop;
        this.mTaskSyncImageListener = taskSyncImageListener;
        this.isCircle = isCircle;

    }
    @Override
    protected Bitmap doInBackground(String... strings) {
        Bitmap outBitmap = UtilBitmap.getBitmapFromView(view);
        if(viewCrop!=null) {
            outBitmap = UtilBitmap.scaleCenterCrop(outBitmap, viewCrop.getHeight(), viewCrop.getWidth());
        }
        if(bitmapCrop!=null){
            outBitmap = UtilBitmap.scaleCenterCrop(outBitmap, bitmapCrop.getHeight(), bitmapCrop.getWidth());
        }
        if(isCircle) {
         outBitmap = UtilBitmap.getCroppedBitmap(outBitmap);
        }
        return outBitmap;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }


    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        if (result != null) {
            if (mTaskSyncImageListener != null) {
                mTaskSyncImageListener.onDone(result);
            }

            this.cancel(true);
        }
    }
}




