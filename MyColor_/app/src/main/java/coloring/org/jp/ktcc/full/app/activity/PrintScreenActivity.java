package coloring.org.jp.ktcc.full.app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.Toast;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

import java.util.List;

import coloring.org.jp.ktcc.full.R;
import coloring.org.jp.ktcc.full.app.activity.fragment.DialogInitPrintFragment;
import coloring.org.jp.ktcc.full.databinding.ActivityPrintScreenBinding;
import coloring.org.jp.ktcc.full.util.CommonUtil;

public class PrintScreenActivity extends AppCompatActivity implements DialogInitPrintFragment.OnFragmentInteractionListener, ColorPickerDialogListener {

    public static final String TAG = PrintScreenActivity.class.getName();
    public static final String EXTRA_LAYER = "extra_layer";
    public static final String EXTRA_OPTION = "extra_option";
    public static final String EXTRA_BITMAP = "extra_bitmap";

    // Give your color picker dialog unique IDs if you have multiple dialogs.
    private final int DIALOG_ID = 0xF1231;

    private ActivityPrintScreenBinding mPrintScreenBinding;
    private DialogInitPrintFragment mDialogInitPrintFragment;
    private String mLayer;
    private String mOption;
    private String mPathBitmap;
    private String[] mListOption;
    private int mWidthScreen;
    private int mHeightScreen;
    private Bitmap mBitmapPrint;
    private Bitmap mBitmapBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListOption = getResources().getStringArray(R.array.array_option);
        mWidthScreen = CommonUtil.getScreenWidth(PrintScreenActivity.this);
        mHeightScreen = CommonUtil.getScreenHeight(PrintScreenActivity.this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mLayer = bundle.getString(EXTRA_LAYER);
            mOption = bundle.getString(EXTRA_OPTION);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
            mPathBitmap = bundle.getString(EXTRA_BITMAP);
            mBitmapPrint = BitmapFactory.decodeFile(mPathBitmap, bmOptions);
//            mBitmapPrint = bitmap.copy(bitmap.getConfig(), bitmap.isMutable());

            if (TextUtils.isEmpty(mLayer) && TextUtils.isEmpty(mOption)) {
                mDialogInitPrintFragment = new DialogInitPrintFragment();
                mDialogInitPrintFragment.show(getSupportFragmentManager(), TAG);
            }
        }

        if (!TextUtils.isEmpty(mLayer) && !TextUtils.isEmpty(mOption)) {
            if (mOption.equals(mListOption[1])) { //landscape
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
        mPrintScreenBinding = DataBindingUtil.setContentView(this, R.layout.activity_print_screen);
        if (!TextUtils.isEmpty(mLayer) && !TextUtils.isEmpty(mOption)) {
            initLayer(mLayer);
        }

        mPrintScreenBinding.mImageButtonBack.setOnClickListener(view -> finish());
        mPrintScreenBinding.mImageButtonDone.setOnClickListener(view -> {
        });

        /* color mode */
        mPrintScreenBinding.mImageButtonEffect.setOnClickListener(view -> {
            ColorPickerDialog.newBuilder()
                    .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                    .setAllowPresets(false)
                    .setDialogId(DIALOG_ID)
                    .setColor(Color.BLACK)
                    .setShowAlphaSlider(true)
                    .show(this);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("getScreenRatio", CommonUtil.getScreenRatio(PrintScreenActivity.this) + "");
        Log.e("width", CommonUtil.getScreenWidth(PrintScreenActivity.this) + "");
        Log.e("height", CommonUtil.getScreenHeight(PrintScreenActivity.this) + "");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onFragmentInteraction(String layer, String option) {
        if (option.equals(mListOption[0])) { //portrait
            mLayer = layer;
            mOption = option;
            initLayer(layer);
            return;
        }
        Intent intent = new Intent(getIntent());
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_LAYER, layer);
        bundle.putString(EXTRA_OPTION, option);
        bundle.putString(EXTRA_BITMAP, mPathBitmap);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    @Override
    public void onFragmentInteractionDismiss() {
        mDialogInitPrintFragment.dismiss();
        if (TextUtils.isEmpty(mLayer) && TextUtils.isEmpty(mOption)) {
            finish();
        }
    }

    private void initLayer(String layer) {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mBitmapBackground = Bitmap.createBitmap(mWidthScreen, (int) (mWidthScreen / 0.74), Bitmap.Config.ARGB_8888);
        } else {
            mBitmapBackground = Bitmap.createBitmap((int) (mWidthScreen / 0.74), mWidthScreen, Bitmap.Config.ARGB_8888);
        }
        mBitmapBackground.eraseColor(Color.WHITE);
        mPrintScreenBinding.mMainLayers.addBackgroundPrint(mBitmapBackground);
        mPrintScreenBinding.mMainLayers.addLayer(mBitmapPrint);
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        switch (dialogId) {
            case DIALOG_ID:
                mBitmapBackground.eraseColor(color);
                mPrintScreenBinding.mMainLayers.addBackgroundPrint(mBitmapBackground);
                break;
            default:
                break;
        }
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }
}
