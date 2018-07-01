package coloring.org.jp.ktcc.full.util;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Random;

import coloring.org.jp.ktcc.full.opencv.ReadCV;

/**
 * Created by nguyen on 10/18/2017.
 */
//File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

public class UtilFile {
    public static final String FOLDER_CHOOSE_PHOTO = "choose_photo";// only camera or new google uri
    public static final String FOLDER_HISTORY = "History-Coloring-Full";


    ///////////////////////////////////////////////////////////////////
    private static final String TAG = UtilFile.class.getSimpleName();
    private static final String PARSE_URI = "content://downloads/public_downloads";

    private UtilFile() {
    }

    public static String getPathRoot(Context context){
        return Environment.getExternalStorageDirectory().getAbsoluteFile() + "/Android/data/" + context.getApplicationContext().getPackageName();
    }

    public static Uri createURI(Context context, String folderName){
        String pathRoot = UtilFile.getPathRoot(context);
        int random = new Random().nextInt(100);
        String fileName = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date()) + "_" + random;
        File file = new File(pathRoot + "/" + folderName, fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        /*
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        */
        return Uri.fromFile(file);
    }


    public static void copyFileOrDirectory(String srcDir, String dstDir) {

        try {
            File src = new File(srcDir);
            File dst = new File(dstDir, src.getName());

            if (src.isDirectory()) {

                String files[] = src.list();
                int filesLength = files.length;
                for (int i = 0; i < filesLength; i++) {
                    String src1 = (new File(src, files[i]).getPath());
                    String dst1 = dst.getPath();
                    copyFileOrDirectory(src1, dst1);

                }
            } else {
                copyFile(src, dst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean copyFile(String sourceFile, String destFile) throws IOException {
        return copyFile(new File(sourceFile), new File(destFile));
    }

    public static boolean copyFile(File sourceFile, File destFile) throws IOException {
        boolean rs = false;
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        FileChannel source = null;
        FileChannel destination = null;
        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
            rs = true;
        }catch (Exception ex){
            ex.printStackTrace();
        }
        finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
        return rs;
    }

    public static boolean saveBitmap(Bitmap bitmap, String fullPath) {
        boolean rs = false;
        if (bitmap != null) {
            try {
                FileOutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(fullPath);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100,  outputStream);
                    rs = true;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (outputStream != null) {
                            outputStream.flush();
                            outputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rs;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)//
    public static Bitmap getFileInfo(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return ReadCV.read(Environment.getExternalStorageDirectory() + "/" + split[1]);
                }
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse(PARSE_URI), Long.parseLong(id));
                return ReadCV.read( getDataColumnPath(context, contentUri, null, null) );
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };
                return ReadCV.read( getDataColumnPath(context, contentUri, selection, selectionArgs) );
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (isNewGooglePhotosUri(uri)) {
                String path = null;
                Bitmap bitmap = null;
                try {

                    InputStream input = context.getContentResolver().openInputStream(uri);
                    Bitmap bitmapTemp = BitmapFactory.decodeStream(input);
                    bitmap = ReadCV.resizeAuto(bitmapTemp);
                    bitmapTemp.recycle();
                    //File file = File.createTempFile("sendbird", ".jpg");
                    //bitmap.compress(Bitmap.CompressFormat.JPEG, 80, new BufferedOutputStream(new FileOutputStream(file)));
                    //path = file.getAbsolutePath();
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                return bitmap;
            } else {
                return ReadCV.read( getDataColumnPath(context, uri, null, null));
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return ReadCV.read(uri.getPath());
        }
        return null;
    }

    private static String getDataColumnPath(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String[] projection = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.SIZE,
        };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                String path = cursor.getString(column_index);
                //column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE);
                //String mime = cursor.getString(column_index);
                //column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
                //int size = cursor.getInt(column_index);
                //Hashtable<String, Object> value = new Hashtable<String, Object>();
                //if (path == null) {
                //    path = "";
                //}
                //value.put(ARG_PATH, path);
                //value.put(ARG_MIME, mime);
                //value.put(ARG_SIZE, size);
                return path;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isNewGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.contentprovider".equals(uri.getAuthority());
    }
    public static boolean saveImageToHistory(Bitmap bitmap, String fileName, String type){
        boolean isSuccess = true;
        OutputStream output;
        // Find the SD Card path
        File filepath = Environment.getExternalStorageDirectory();
        // Create a new folder in SD Card
        File dir = new File(filepath.getAbsolutePath()
                + "/"+FOLDER_HISTORY+"/");
        dir.mkdirs();

        // Create a name for the saved image
        File file = new File(dir, fileName+type);
        int index = 1;
        while (file.exists()){
            file = new File(dir, fileName+"("+index+")"+type);
            index ++;
        }

        try {

            output = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
            output.flush();
            output.close();
        }

        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            isSuccess = false;
        }
        return isSuccess;
    }
    public static void getHistoryImage()
    {
        ArrayList<String> f = new ArrayList<String>();// list of file paths
        File[] listFile;
        File file= new File(android.os.Environment.getExternalStorageDirectory(),FOLDER_HISTORY);

        if (file.isDirectory())
        {
            listFile = file.listFiles();
            Arrays.sort(listFile, new Comparator<File>() {
                public int compare(File f1, File f2) {
                    return Long.compare(f1.lastModified(), f2.lastModified());
                }
            });
            for (int i = 0; i < listFile.length; i++)
            {
                f.add(listFile[i].getAbsolutePath());
            }
        }
    }
    public static void deleteFiles(String path) {
        File file = new File(path);
        boolean deleted = file.delete();
    }
}