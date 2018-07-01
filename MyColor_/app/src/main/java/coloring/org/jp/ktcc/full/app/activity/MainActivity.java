package coloring.org.jp.ktcc.full.app.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import coloring.org.jp.ktcc.full.R;
import coloring.org.jp.ktcc.full.app.activity.fragment.CropFragment;
import coloring.org.jp.ktcc.full.custom_ui.fragment.MyFragment;
import coloring.org.jp.ktcc.full.custom_ui.fragment.listener.SendToActivityListener;
import coloring.org.jp.ktcc.full.opencv.ReadCV;
import coloring.org.jp.ktcc.full.util.AppConstants;
import coloring.org.jp.ktcc.full.util.UtilDevice;
import coloring.org.jp.ktcc.full.util.UtilFile;
import coloring.org.jp.ktcc.full.util.UtilPermission;

import static coloring.org.jp.ktcc.full.util.UtilFile.FOLDER_CHOOSE_PHOTO;

public class MainActivity extends AppCompatActivity implements SendToActivityListener {
    private static final int GET_FILE_REQUEST_CODE = 101;
    //  public static final int REQUEST_PERMISSION = 102;
    private String mCameraPath;
    boolean doubleBackToExitPressedOnce = false;
    private boolean isMultipleFile = false;
    int mBackStackSize = 0;
    Bitmap bitmap = null;
    protected Dialog dialogProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UtilDevice.initUtilDevice(this);
        setContentView(R.layout.activity_main);
        managerBack();
        //getActivity().setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    // Call from fragment
    @Override
    public void onSelectPhoto(boolean isMultipleFile) {
        this.isMultipleFile = isMultipleFile;
        checkPermission();
    }

    public void checkPermission() {
        if (UtilPermission.isCameraPermissionOn(this) && UtilPermission.isReadExternalPermissionOn(this) && UtilPermission.isWriteExternalPermissionOn(this)) {
            showSelectPhoto();
        } else {
            String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, permissions, AppConstants.REQUEST_PERMISSION_MAIN);
        }
    }

    public int getActionBarHeight() {
        final TypedArray ta = getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize});
        int actionBarHeight = (int) ta.getDimension(0, 0);
        return actionBarHeight;
    }

    private void showSelectPhoto() {
        Uri outputCamera = UtilFile.createURI(getApplicationContext(), FOLDER_CHOOSE_PHOTO);
        mCameraPath = outputCamera.getPath();
        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputCamera);
            cameraIntents.add(intent);
        }

        //Gallery.
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Filesystem (Document)
        //final Intent fsIntent = new Intent();
        //fsIntent.setType("*/*");
        //fsIntent.setAction(Intent.ACTION_GET_CONTENT);
        //cameraIntents.add(fsIntent);

        //Create the Chooser
        final Intent chooserIntent = Intent.createChooser(galleryIntent, getString(R.string.choose));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));
        startActivityForResult(chooserIntent, GET_FILE_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == AppConstants.REQUEST_PERMISSION_MAIN) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            onSelectPhoto(this.isMultipleFile);
        } else {
            Fragment visibleFm = getCurrentFragment();
            if (visibleFm != null) {
                ((MyFragment) visibleFm).onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    //@SuppressLint("RestrictedApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode != GET_FILE_REQUEST_CODE || resultCode != RESULT_OK) {
                mCameraPath = null;
                return;
            }
            if (data == null || data.getData() == null) {
                // If there is not data, then we may have taken a photo
                if (mCameraPath != null) {
                    bitmap = ReadCV.read(mCameraPath);
                    mCameraPath = null;
                }
            } else {
                Uri dataUri = data.getData();
                bitmap = UtilFile.getFileInfo(this, dataUri);
            }

            if (bitmap != null) {
                Fragment visibleFm = getCurrentFragment();
                if (visibleFm != null) {
                    ((MyFragment) visibleFm).onReceivePhoto(bitmap, ((MyFragment) visibleFm).REQUEST_SEND);
                }
            }
        } catch (Exception ex) {

        }
    }

    public Fragment getCurrentFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        Fragment fr = null;
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible()) {
                    fr = fragment;
                }
            }
        }
        return fr;
    }

    //Hardware
    @Override
    public void onBackPressed() {
        hideKeyboard();
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            CropFragment.setIsCircle(false);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getResources().getString(R.string.confirm_exit_app), Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    public boolean isBack() {
        boolean rs = false;
        FragmentManager manager = getSupportFragmentManager();
        if (manager != null) {
            int countCurrentStack = manager.getBackStackEntryCount();
            if (mBackStackSize > countCurrentStack) {
                rs = true;
            }
            mBackStackSize = countCurrentStack;
        }
        return rs;
    }

    private void managerBack() {
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (isBack()) {
                    Fragment fm = getCurrentFragment();
                    if (fm != null) {
                        ((MyFragment) fm).onReceiveBack(((MyFragment) fm).REQUEST_SEND);
                    }
                }
            }
        });
    }

    public void hideKeyboard() {
        try {
            View view = getCurrentFocus();
            if (view == null) {
                view = new View(this);
            }
            if (view != null) {
                InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }

        } catch (Exception ex) {
            Log.d("hideKeyboard:", ex.getMessage().toString());
        }
    }

    public void showDialogProgress() {

        if (isDialogProgressShowing()) {
            return;
        }

        dialogProgress = new Dialog(this);
        dialogProgress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogProgress.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogProgress.setContentView(R.layout.dialog_loading);
        dialogProgress.getWindow().getAttributes().width = WindowManager.LayoutParams.WRAP_CONTENT;
        ProgressBar progressBar = (ProgressBar) dialogProgress.findViewById(R.id.progressBar);
        //progressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(android.R.color.transparent), android.graphics.PorterDuff.Mode.SRC_ATOP);
        /*progressBar.getProgressDrawable().setColorFilter(
                getResources().getColor(R.color.app_color_light_bg), android.graphics.PorterDuff.Mode.SRC_IN);*/
        dialogProgress.setCanceledOnTouchOutside(false);

        dialogProgress.show();


    }

    public void hideDialogProgress() {
        if (dialogProgress == null)
            return;

        dialogProgress.dismiss();
        dialogProgress = null;
    }

    public boolean isDialogProgressShowing() {
        if (dialogProgress != null && dialogProgress.isShowing()) {
            return true;
        }
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle stateBundle) {
        int osVersion = android.os.Build.VERSION.SDK_INT;
        if (osVersion < Build.VERSION_CODES.N) {
            super.onSaveInstanceState(stateBundle);
        }
    }
}