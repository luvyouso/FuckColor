package coloring.org.jp.ktcc.full.util;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by nguyen on 10/18/2017.
 */

public class UtilPermission {
    private UtilPermission() {
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean isCameraPermissionOn(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        return !(currentAPIVersion >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean isReadExternalPermissionOn(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        return !(currentAPIVersion >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean isWriteExternalPermissionOn(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        return !(currentAPIVersion >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED);
    }
    public static void checkPermission(Activity context, int permission) {
        if ( UtilPermission.isReadExternalPermissionOn(context) && UtilPermission.isWriteExternalPermissionOn(context)) {

        }else{
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(context, permissions, permission);
        }
    }
}