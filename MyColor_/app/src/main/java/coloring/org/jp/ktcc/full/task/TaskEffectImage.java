package coloring.org.jp.ktcc.full.task;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import coloring.org.jp.ktcc.full.util.UtilBitmap;

/**
 * Created by anh.trinh on 03/13/2017.
 */

public class TaskEffectImage extends AsyncTask<String, Bitmap, Bitmap> {
    String type;
    Bitmap bitmap;
    String error;
    boolean isCircle;

    public interface TaskMosaicListener {
        void onDone(Bitmap bitmap, String type);
        void onFail(String error);
    }

    TaskMosaicListener mTaskMosaicListener;

    public void setTaskMosaicListener(TaskMosaicListener listener) {
        this.mTaskMosaicListener = listener;
    }

    public TaskEffectImage(String type, Bitmap bitmap, TaskMosaicListener taskMosaicListener, boolean isCircle) {

        this.type = type;
        this.bitmap = bitmap;
        this.mTaskMosaicListener = taskMosaicListener;
        this.isCircle = isCircle;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {

        return uploadFile(this.bitmap, this.type);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }


    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        if (result != null) {
            if (mTaskMosaicListener != null) {
                mTaskMosaicListener.onDone(result, type);

            }

            this.cancel(true);
        }
        if (mTaskMosaicListener != null && error!= null) {
            mTaskMosaicListener.onFail(error);
        }
    }

    @SuppressWarnings("deprecation")
    private Bitmap uploadFile(final Bitmap bitmap, String type) {
        Bitmap result = null;
        error = null;
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://160.16.104.65:8000/stylizing");

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addPart("image", new StringBody(UtilBitmap.convertBitmapToBase64(bitmap)));
            builder.addPart("style", new StringBody(type.toLowerCase()));
           // Log.e("Base64", UtilBitmap.convertBitmapToBase64(bitmap));


            httppost.setEntity(builder.build());

            // Making server call
            HttpResponse response = httpclient.execute(httppost);

            HttpEntity r_entity = response.getEntity();

            int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode == 200) {
                try {
                    String resp_body = EntityUtils.toString(response.getEntity());
                    Log.v("resp_body", resp_body.toString());
                    JSONObject jsobj = new JSONObject(resp_body);
                    if (jsobj.has("result")) {
                        String image = jsobj.getString("result");
                        result = UtilBitmap.convertBase64ToBitmap(image);
                        if(isCircle){
                            result = UtilBitmap.getCroppedBitmap(result);
                        }
                    }

                } catch (Exception e) {
                    Log.e("sometag", e.getMessage());
                    error = "Error occurred! "+ e.getMessage();
                }

            } else {
                error = "Error occurred! Http Status Code: "
                        + statusCode;
            }

        } catch (ClientProtocolException e) {
            error = e.toString();
        } catch (IOException e) {
            error = e.toString();
        }
        return result;

    }
}




