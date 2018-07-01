package coloring.org.jp.ktcc.full.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

/**
 * Created by nguyen on 10/31/2017.
 */

public class UtilBitmap {
    public static final String IMAGE_JPG = "image/jpeg";
    public static final String IMAGE_PNG = "image/png";
    public static final String IMAGE_WEBP = "image/webp";
    //public static final String IMAGE_GIF = "image/gif";
    public static boolean isSupport(String fullPath){
        BitmapFactory.Options options = getBitmapOptions(fullPath);
        String mimeType = options.outMimeType;//mimeType = "image/png" or "image/jpeg" or "image/webp"
        if (mimeType.equals(IMAGE_JPG)){
            return true;
        }
        if (mimeType.equals(IMAGE_PNG)){
            return true;
        }
        if (mimeType.equals(IMAGE_WEBP)){
            return true;
        }
        options.inBitmap.recycle();
        return false;
    }
    public static Bitmap.CompressFormat getCompressFormatImage(String fullPath) {
        BitmapFactory.Options options = getBitmapOptions(fullPath);
        String mimeType = options.outMimeType;//mimeType = "image/png" or "image/jpeg" or "image/webp"
        if (mimeType.equals(IMAGE_JPG)){
            return Bitmap.CompressFormat.JPEG;
        }
        if (mimeType.equals(IMAGE_PNG)){
            return Bitmap.CompressFormat.PNG;
        }
        if (mimeType.equals(IMAGE_WEBP)){
            return Bitmap.CompressFormat.WEBP;
        }
        options.inBitmap.recycle();
        return null;
    }
    private static BitmapFactory.Options getBitmapOptions(String fullPath){
        BitmapFactory.Options optionsMeta = new BitmapFactory.Options();
        optionsMeta.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fullPath, optionsMeta);
        return optionsMeta;
    }
    public static  Bitmap getBitmapFromImageView(ImageView imageView)
    {
        BitmapDrawable bitmapDrawable = ((BitmapDrawable) imageView.getDrawable());
        Bitmap bitmap;
        if(bitmapDrawable==null){
            imageView.buildDrawingCache();
            bitmap = imageView.getDrawingCache();
            imageView.buildDrawingCache(false);
        }else
        {
            bitmap = bitmapDrawable .getBitmap();
        }

        return bitmap;
    }
    public static Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
            // remove white background
           // canvas.drawColor(Color.TRANSPARENT);
        view.draw(canvas);
        Log.e("Bitmap size",": "+ getBitmapByteCount(returnedBitmap)/1024);
        return returnedBitmap;
    }
    public static Bitmap scaleCenterCrop(Bitmap source, int newHeight, int newWidth) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();
        if(newHeight>sourceHeight){
            newHeight = sourceHeight;
        }
        if(newWidth> sourceWidth){
            newWidth = sourceWidth;
        }
        int x = (sourceWidth - newWidth)/2;
        int y = (sourceHeight - newHeight)/2;

        return   Bitmap.createBitmap(source, x,y,newWidth, newHeight);
    }
    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }
    public static int getBitmapByteCount(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1)
            return bitmap.getRowBytes() * bitmap.getHeight();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return bitmap.getByteCount();
        return bitmap.getAllocationByteCount();
    }
    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    public static Bitmap convertBase64ToBitmap(String base64Str) throws IllegalArgumentException
    {
        byte[] decodedBytes = Base64.decode(
                base64Str.substring(base64Str.indexOf(",")  + 1),
                Base64.DEFAULT
        );

        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static String convertBitmapToBase64(Bitmap bitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }
    public static Bitmap addBorderBitmap(Bitmap bitmap){
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int radius = Math.min(h / 2, w / 2);
        Bitmap output = Bitmap.createBitmap(w + 24, h + 24, Bitmap.Config.ARGB_8888);

        Paint p = new Paint();
        p.setAntiAlias(true);

        Canvas c = new Canvas(output);
        c.drawARGB(0, 0, 0, 0);
        p.setStyle(Paint.Style.FILL);

        c.drawCircle((w / 2) + 12, (h / 2) + 12, radius, p);

        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        c.drawBitmap(bitmap, 12, 12, p);
        p.setXfermode(null);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.WHITE);
        p.setStrokeWidth(3);
        c.drawCircle((w / 2) + 12, (h / 2) + 12, radius, p);
        UtilFile.saveImageToHistory(output, "print", ".png");
        return output;
    }
    public static Bitmap addBufferBitmap(Bitmap bitmap){

        int buffer_size = 24;
        int buffer_radius = buffer_size / 2;

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Bitmap output = Bitmap.createBitmap(w + buffer_size, h + buffer_size, Bitmap.Config.ARGB_8888);

        Paint p = new Paint();
        p.setAntiAlias(true);

        Canvas c = new Canvas(output);
        c.drawARGB(0, 0, 0, 0);

        // Draw buffer
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(2);
        p.setColor(Color.TRANSPARENT);
        c.drawRect(0, 0, w + buffer_radius, h + buffer_radius, p);

        // Draw source bitmap
        c.drawColor(0, PorterDuff.Mode.CLEAR);
        c.drawBitmap(bitmap, buffer_radius, buffer_radius, null);
        // sUtilFile.saveImageToHistory(output, "print", ".png");
        return output;
    }

    public static Bitmap loadLargeBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(b);
        v.draw(c);
        return b;
    }
}
