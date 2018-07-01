package coloring.org.jp.ktcc.full.task;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import coloring.org.jp.ktcc.full.util.UtilFile;

/**
 * Created by anh.trinh on 12/21/2017.
 */

public class TaskSaveHistory extends AsyncTask<String, Bitmap, Boolean> {
        Bitmap bitmap;
        String fileName;
        String fileType;
    public interface SaveHistoryListener {
        void onDone(Boolean isSuccess);
    }

    SaveHistoryListener mSaveHistoryListener;

    public void setSaveHistoryListener(SaveHistoryListener listener) {
        this.mSaveHistoryListener = listener;
    }
    public TaskSaveHistory(Bitmap bitmap, String fileName,String fileType, SaveHistoryListener saveHistoryListener) {
        this.bitmap = bitmap;
        this.fileName = fileName;
        this.fileType = fileType;
        this.mSaveHistoryListener = saveHistoryListener;
    }
    @Override
    protected Boolean doInBackground(String... strings) {

        return UtilFile.saveImageToHistory(this.bitmap, this.fileName, fileType);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }


    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (result != null) {
            if (mSaveHistoryListener != null) {
                mSaveHistoryListener.onDone(result);
            }

            this.cancel(true);
        }
    }
}




