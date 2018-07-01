package coloring.org.jp.ktcc.full.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.PointF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import coloring.org.jp.ktcc.full.R;

/**
 * Created by anh.trinh on 12/4/2017.
 */

public class CommonUtil {
    public static int SCREEN_WIDTH = 0;
    public static int SCREEN_HEIGHT = 0;

    public static void slideUpDown(final View view) {
        if (view.getVisibility() == View.VISIBLE) {
            Animation out = AnimationUtils.loadAnimation(view.getContext(), R.anim.exit_to_bottom);
            view.startAnimation(out);
            view.setVisibility(View.INVISIBLE);
        } else {
            Animation in = AnimationUtils.loadAnimation(view.getContext(), R.anim.enter_from_bottom);
            view.startAnimation(in);
            view.setVisibility(View.VISIBLE);
        }
    }

    public static void slideUpDown(final View viewParent, final View view, int status) {
        if (status == View.GONE) {
            Animation out = AnimationUtils.loadAnimation(view.getContext(), R.anim.exit_to_bottom);
            viewParent.startAnimation(out);
            view.setVisibility(View.GONE);
        } else {
            Animation in = AnimationUtils.loadAnimation(view.getContext(), R.anim.enter_from_bottom);
            viewParent.startAnimation(in);
            view.setVisibility(View.VISIBLE);
        }
    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public static PointF calculateScaledCoordinate(float oldX, float oldY, float centerX, float centerY, float scale) {
        PointF newPoint = new PointF();
        float newX = centerX * (1 - scale) + scale * oldX;
        float newY = centerY * (1 - scale) + scale * oldY;
        newPoint.set(newX, newY);
        return newPoint;
    }

    public static void showActionAlertYESORNO(Context context,
                                              String header,
                                              String message,
                                              String positiveLabel,
                                              DialogInterface.OnClickListener positiveAction,
                                              String negativeLabel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (header != null && header.length() != 0) {
            builder.setTitle(header);
        }


        Dialog dialog = builder.setMessage(message)
                .setCancelable(true)
                .setPositiveButton(positiveLabel,
                        positiveAction)
                .setNegativeButton(negativeLabel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        })
                .show();
        TextView messageText = (TextView) dialog.findViewById(android.R.id.message);
        messageText.setGravity(Gravity.CENTER);
    }

    public static void showNetworkConnectionError(Context context,
                                                  DialogInterface.OnClickListener positiveAction) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle(R.string.dialog_no_internet_title);
        builder.setMessage(R.string.dialog_no_internet_message)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_no_internet_ok,
                        positiveAction);
        builder.show();

    }

    /**
     * Hide the keyboard for the attached view
     */
    public static void hideSoftKeyboard(Context context,
                                        View view) {
        if (view != null) {
            InputMethodManager imm =
                    (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void hideSoftKeyboard(Context context) {
        ((Activity) context).getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public static void showSoftKeyboard(Context context,
                                        View view) {
        InputMethodManager imm =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view,
                0);
    }

    public static int getScreenHeight(Activity activity) {
        if (SCREEN_HEIGHT == 0) {
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager()
                    .getDefaultDisplay()
                    .getMetrics(metrics);

            SCREEN_HEIGHT = metrics.heightPixels > metrics.widthPixels ? metrics.heightPixels : metrics.widthPixels;
        }
        return SCREEN_HEIGHT;
    }

    public static int getScreenWidth(Activity activity) {
        if (SCREEN_WIDTH == 0) {
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager()
                    .getDefaultDisplay()
                    .getMetrics(metrics);
            SCREEN_WIDTH = metrics.heightPixels > metrics.widthPixels ? metrics.widthPixels : metrics.heightPixels;
        }
        return SCREEN_WIDTH;
    }

    public static double getScreenRatio(Activity activity) {
        return getScreenHeight(activity) / (double) getScreenWidth(activity);
    }

    public static boolean isTablet(Context ctx) {
        return (ctx.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static boolean isNetworkAvailable(Context ctx) {
        if (ctx == null)
            return false;

        ConnectivityManager cm =
                (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }


}
