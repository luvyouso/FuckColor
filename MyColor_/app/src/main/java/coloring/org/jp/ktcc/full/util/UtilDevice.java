package coloring.org.jp.ktcc.full.util;

import android.app.Activity;
import android.util.DisplayMetrics;

/**
 * Created by nguyen on 10/20/2017.
 */

public class UtilDevice {
    private static DisplayMetrics displayMetrics;
    public static void initUtilDevice(Activity activity){
        UtilDevice.initScreen(activity);
    }
    public static DisplayMetrics getScreen(){
        return displayMetrics;
    }

    public static DisplayMetrics getScreen(Activity activity){
        if (displayMetrics == null){
            UtilDevice.initScreen(activity);
            return displayMetrics;
        }else {
            return displayMetrics;
        }
    }

    private static void initScreen(Activity activity){
        UtilDevice.displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //int height = displayMetrics.heightPixels;
        //int width = displayMetrics.widthPixels;
    }
}
