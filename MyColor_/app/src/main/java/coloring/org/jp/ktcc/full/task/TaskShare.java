package coloring.org.jp.ktcc.full.task;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by anh.trinh on 12/21/2017.
 */

public class TaskShare extends AsyncTask<String, Bitmap, Bitmap> {
        Bitmap bitmap;
        Context context;
    public interface TaskShareListener {
        void onDone(Bitmap bitmap);
    }

    TaskShareListener mTaskShareListener;

    public void setTaskShareListener(TaskShareListener listener) {
        this.mTaskShareListener = listener;
    }
    public TaskShare(Context context,Bitmap bitmap, TaskShareListener taskShareListener) {
        this.context = context;
        this.bitmap = bitmap;
        this.mTaskShareListener = taskShareListener;
    }
    @Override
    protected Bitmap doInBackground(String... strings) {
        File cache = context.getExternalCacheDir();
        File sharefile = new File(cache, "share.png");
        try {
            FileOutputStream out = new FileOutputStream(sharefile);
            this.bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (IOException e) {

        }
        // Now send it out to share
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("image/*");
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + sharefile));
        try {
            context.startActivity(Intent.createChooser(share, "Share photo"));
        }catch (Exception e) {

        }
        return this.bitmap;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }


    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        if (result != null) {
            if (mTaskShareListener != null) {
                mTaskShareListener.onDone(result);
            }

            this.cancel(true);
        }
    }
}




