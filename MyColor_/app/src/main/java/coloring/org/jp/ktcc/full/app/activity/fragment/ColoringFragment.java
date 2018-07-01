package coloring.org.jp.ktcc.full.app.activity.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.print.PrintHelper;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import coloring.org.jp.ktcc.full.R;
import coloring.org.jp.ktcc.full.adapter.EffectAdapter;
import coloring.org.jp.ktcc.full.adapter.SealAdapter;
import coloring.org.jp.ktcc.full.adapter.SketchAdapter;
import coloring.org.jp.ktcc.full.app.App;
import coloring.org.jp.ktcc.full.app.activity.PrintScreenActivity;
import coloring.org.jp.ktcc.full.custom_ui.ColorEffectView;
import coloring.org.jp.ktcc.full.custom_ui.EraserToolView;
import coloring.org.jp.ktcc.full.custom_ui.FingerSeal;
import coloring.org.jp.ktcc.full.custom_ui.Layer;
import coloring.org.jp.ktcc.full.custom_ui.fragment.MyFragment;
import coloring.org.jp.ktcc.full.filters.ColorEffectFilter;
import coloring.org.jp.ktcc.full.filters.NeuralEffectFilter;
import coloring.org.jp.ktcc.full.opencv.OutputCV;
import coloring.org.jp.ktcc.full.task.TaskEffectImage;
import coloring.org.jp.ktcc.full.task.TaskSaveHistory;
import coloring.org.jp.ktcc.full.task.TaskShare;
import coloring.org.jp.ktcc.full.task.TaskSyncImage;
import coloring.org.jp.ktcc.full.util.AppConstants;
import coloring.org.jp.ktcc.full.util.CommonUtil;
import coloring.org.jp.ktcc.full.util.JSONSharedPreferences;
import coloring.org.jp.ktcc.full.util.UtilBitmap;
import coloring.org.jp.ktcc.full.util.UtilFile;
import coloring.org.jp.ktcc.full.util.UtilPermission;
import io.reactivex.annotations.NonNull;

public class ColoringFragment extends MyFragment {
    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.btnSave)
    ImageButton btnSave;
    @BindView(R.id.btnRefresh)
    ImageButton btnRefresh;
    @BindView(R.id.btnPrint)
    ImageButton btnPrint;
    @BindView(R.id.btnShare)
    ImageButton btnShare;
    @BindView(R.id.layout_top)
    LinearLayout layoutTop;
    @BindView(R.id.imgColoring)
    AppCompatImageView imgColoring;
    @BindView(R.id.rvLayerPanel)
    RecyclerView rvLayerPanel;
    @BindView(R.id.layout_center)
    FrameLayout layoutCenter;
    @BindView(R.id.btnColorEffect)
    ImageButton btnColorEffect;
    @BindView(R.id.btnCrop)
    ImageButton btnCrop;
    @BindView(R.id.btnFlip)
    ImageButton btnFlip;
    @BindView(R.id.btnSketch)
    ImageButton btnSketch;
    @BindView(R.id.layout_bottom)
    LinearLayout layoutBottom;
    @BindView(R.id.color_effect_view)
    ColorEffectView colorEffectView;
    @BindView(R.id.rvEffectPanel)
    RecyclerView rvEffectPanel;
    @BindView(R.id.rvSealPanel)
    RecyclerView rvSealPanel;
    @BindView(R.id.btnEffect)
    ImageButton btnEffect;
    @BindView(R.id.btnSeal)
    ImageButton btnSeal;
    @BindView(R.id.fingerSeal)
    FingerSeal fingerSeal;
    @BindView(R.id.frameSeal)
    FrameLayout frameSeal;
    @BindView(R.id.tvSeal)
    TextView tvSeal;
    @BindView(R.id.tvProcess)
    TextView tvProcess;
    @BindView(R.id.sbFilterEffect)
    SeekBar sbFilterEffect;
    @BindView(R.id.btnOk)
    Button btnOk;
    @BindView(R.id.layoutGuideEffect)
    RelativeLayout layoutGuideEffect;
    @BindView(R.id.btnShow)
    AppCompatImageView btnShow;
    @BindView(R.id.btnPanelLayout)
    FrameLayout btnPanelLayout;
    @BindView(R.id.layout_seal_panel)
    RelativeLayout layoutSealPanel;
    @BindView(R.id.toolSeal)
    EraserToolView toolSeal;

    private Bitmap mBitmapOriginal;
    private Bitmap mBitmapCurrent;
    private ColorEffectFilter colorEffectFilter;
    private boolean isFlip = false;
    private SketchAdapter sketchAdapter;
    private SealAdapter sealAdapter;
    private EffectAdapter effectAdapter;
    private List<String> sketchs;
    private List<String> effects;
    private List<String> seals;
    private NeuralEffectFilter neuralEffectFilter;


    private static final String ARG_BITMAP = "bitmap";
    private static final String ARG_IS_CIRCLE = "isCircle";
    private int sealIndex = -1;
    private boolean isCircle = false;
    private boolean isChangeColorEffect = false;


    public static ColoringFragment newInstance(Bitmap bm, boolean isCircle) {
        ColoringFragment fragment = new ColoringFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_BITMAP, bm);
        args.putBoolean(ARG_IS_CIRCLE, isCircle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBitmapCurrent = getArguments().getParcelable(ARG_BITMAP);
            mBitmapOriginal = getArguments().getParcelable(ARG_BITMAP);
            isCircle = getArguments().getBoolean(ARG_IS_CIRCLE, false);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_coloring, container, false);
        ButterKnife.bind(this, root);
        this.initData();
        return root;
    }


    public void initData() {
        imgColoring.setImageBitmap(mBitmapCurrent);
        colorEffectFilter = new ColorEffectFilter(getContext(), mBitmapCurrent);
        colorEffectView.initData(colorEffectFilter, false);
        colorEffectView.setColorEffectListener(new ColorEffectView.ColorEffectListener() {
            @Override
            public void onChange(float opacity, float hue, float tint, float sepia, float temperature, float brightness, float contrast, float saturation, float blur, boolean isBackground) {
                isChangeColorEffect = true;
                colorEffectFilter.setAlpha(opacity);
                colorEffectFilter.setHue(hue);
                colorEffectFilter.setBrightness(brightness);
                colorEffectFilter.setContrast(contrast);
                colorEffectFilter.setSaturation(saturation);
                colorEffectFilter.setSepia(sepia);
                colorEffectFilter.setTemperature(temperature);
                colorEffectFilter.setTint(tint);
                colorEffectFilter.setBlur(blur);
                setImage();

            }

            @Override
            public void onTouchOutSide() {
                CommonUtil.slideUpDown(colorEffectView);
            }
        });
        getSketchImage();
        getEffectImage();

        sbFilterEffect.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (neuralEffectFilter != null) {
                    tvProcess.setText(progress + "%");
                    mBitmapCurrent = neuralEffectFilter.filter(progress);
                    if (colorEffectFilter != null) {
                        colorEffectFilter.setBitmap(getContext(), mBitmapCurrent);
                        colorEffectView.setColorEffectFilter(colorEffectFilter);
                    }
                    setImage();
                }


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (neuralEffectFilter != null) {
                    tvProcess.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        tvProcess.setVisibility(View.GONE);
                    }

                }, 200);


            }
        });
        btnRefresh.setEnabled(false);

        getSealImage();
        setupFrameSeal();
        fingerSeal.setFingerListener(new FingerSeal.FingerCachetListener() {
            @Override
            public void onTouchEdit() {
                showSealDialog(sealIndex);
            }

            @Override
            public void onTouchRemove() {
                frameSeal.removeView(fingerSeal.getLayer());
                fingerSeal.removeView(fingerSeal.getLayer());
                fingerSeal.removeLayer();
                fingerSeal.setVisibility(View.GONE);
                toolSeal.setVisibility(View.GONE);
            }
        });
        fingerSeal.setVisibility(View.GONE);

        toolSeal.setEraserToolView(new EraserToolView.EraserToolViewListener() {
            @Override
            public void onSeekBarChange(int i) {
            }

            @Override
            public void onProcessSeekBarChange(int i) {
                fingerSeal.setWidth(toolSeal.getValue());
            }

            @Override
            public void onDone() {
                updateSealFeature();
            }
        });
        toolSeal.setMaxValue(fingerSeal.getMaxWidth());

    }

    private void updateSealFeature() {
        if (layoutSealPanel.getVisibility() == View.VISIBLE) {
            fingerSeal.hideControl();
            btnSeal.setImageResource(R.drawable.ic_seal);
        } else {
            fingerSeal.showControl();
            btnSeal.setImageResource(R.drawable.ic_seal_a);
            if (fingerSeal.getLayer() != null) {
                toolSeal.setVisibility(View.VISIBLE);
            }
        }

        if ((rvSealPanel.getVisibility() == View.GONE || rvSealPanel.getVisibility() == View.INVISIBLE) && layoutSealPanel.getVisibility() == View.VISIBLE) {
            layoutSealPanel.setVisibility(View.GONE);
        } else {
            CommonUtil.slideUpDown(layoutSealPanel);
        }

    }


    @OnClick({R.id.btnBack, R.id.btnSave, R.id.btnRefresh, R.id.btnPrint, R.id.btnShare, R.id.btnColorEffect, R.id.btnCrop, R.id.btnFlip, R.id.btnSketch, R.id.btnEffect, R.id.btnSeal, R.id.imgColoring, R.id.fingerSeal, R.id.btnOk})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                popFragment();
                break;
            case R.id.btnSave:
                saveImage();
                break;
            case R.id.btnRefresh:
                doRefresh();
                break;
            case R.id.btnPrint:
                doPrint();
                break;
            case R.id.btnShare:
                doShare();
                break;
            case R.id.btnColorEffect:
                CommonUtil.slideUpDown(colorEffectView);
                if (rvLayerPanel.getVisibility() == View.VISIBLE) {
                    rvLayerPanel.setVisibility(View.GONE);
                }
                if (layoutSealPanel.getVisibility() == View.VISIBLE) {
                    layoutSealPanel.setVisibility(View.GONE);
                }
                if (rvEffectPanel.getVisibility() == View.VISIBLE) {
                    rvEffectPanel.setVisibility(View.GONE);
                }
                sbFilterEffect.setVisibility(View.GONE);
                fingerSeal.hideControl();
                btnEffect.setImageResource(R.drawable.ic_effect);
                btnSketch.setImageResource(R.drawable.ic_sketch);
                btnSeal.setImageResource(R.drawable.ic_seal);
                break;
            case R.id.btnCrop:
                openCropFragment();
                break;
            case R.id.btnFlip:
                flipLayer();
                break;
            case R.id.btnSketch:
                if (layoutSealPanel.getVisibility() == View.VISIBLE) {
                    CommonUtil.slideUpDown(layoutSealPanel);
                }
                if (rvEffectPanel.getVisibility() == View.VISIBLE) {
                    CommonUtil.slideUpDown(rvEffectPanel);
                }

                btnEffect.setImageResource(R.drawable.ic_effect);
                if (rvLayerPanel.getVisibility() == View.VISIBLE) {
                    btnSketch.setImageResource(R.drawable.ic_sketch);
                } else {
                    btnSketch.setImageResource(R.drawable.ic_sketch_a);
                }
                btnSeal.setImageResource(R.drawable.ic_seal);
                CommonUtil.slideUpDown(rvLayerPanel);

                //rvLayerPanel.setVisibility(status);
                // rvLayerPanel.setVisibility(rvLayerPanel.getVisibility()==View.GONE?View.VISIBLE:View.GONE);
                sbFilterEffect.setVisibility(View.GONE);
                fingerSeal.hideControl();
                break;
            case R.id.btnEffect:
                if (!CommonUtil.isNetworkAvailable(getContext()) && rvEffectPanel.getVisibility() != View.VISIBLE) {
                    CommonUtil.showNetworkConnectionError(getContext(), null);
                } else {
                    if (rvLayerPanel.getVisibility() == View.VISIBLE) {
                        CommonUtil.slideUpDown(rvLayerPanel);
                    }
                    if (layoutSealPanel.getVisibility() == View.VISIBLE) {
                        CommonUtil.slideUpDown(layoutSealPanel);
                    }

                    if (rvEffectPanel.getVisibility() == View.VISIBLE) {
                        sbFilterEffect.setVisibility(View.GONE);
                        btnEffect.setImageResource(R.drawable.ic_effect);
                    } else {
                        sbFilterEffect.setVisibility(View.VISIBLE);
                        btnEffect.setImageResource(R.drawable.ic_effect_a);

                    }
                    CommonUtil.slideUpDown(rvEffectPanel);
                    btnSketch.setImageResource(R.drawable.ic_sketch);
                    btnSeal.setImageResource(R.drawable.ic_seal);

                }
                fingerSeal.hideControl();
                break;
            case R.id.btnSeal:
                sbFilterEffect.setVisibility(View.GONE);
                if (rvLayerPanel.getVisibility() == View.VISIBLE) {
                    CommonUtil.slideUpDown(rvLayerPanel);
                }
                if (rvEffectPanel.getVisibility() == View.VISIBLE) {
                    CommonUtil.slideUpDown(rvEffectPanel);
                }
                btnEffect.setImageResource(R.drawable.ic_effect);
                btnSketch.setImageResource(R.drawable.ic_sketch);
                updateSealFeature();
                if (rvSealPanel.getVisibility() == View.GONE) {
                    expandEffectPanelLayout(View.VISIBLE);
                }

                break;
            case R.id.imgColoring:
                //  fingerSeal.hideControl();
                break;
            case R.id.fingerSeal:
                // fingerSeal.showControl();
                break;
            case R.id.btnOk:
                layoutGuideEffect.setVisibility(View.GONE);
                break;
        }
    }

    private void flipLayer() {
        isFlip = !isFlip;
        setImage();

    }

    private void doRefresh() {
        mBitmapCurrent = mBitmapOriginal;
        isFlip = false;
        isChangeColorEffect = false;
        colorEffectFilter.reset();
        colorEffectFilter.setBitmap(getContext(), mBitmapCurrent);
        colorEffectView.initData(colorEffectFilter, false);
        getSketchImage();
        sketchAdapter.setCurrentIndex(-1);
        btnEffect.setEnabled(true);
        setImage();
        btnRefresh.setEnabled(false);

    }

    private void doPrint() {
        showDialogProgress();
        TaskSyncImage taskSyncImage = new TaskSyncImage(layoutCenter, mBitmapCurrent, new TaskSyncImage.TaskSyncImageListener() {
            @Override
            public void onDone(Bitmap bitmap) {
                File f = new File(Environment.getExternalStorageDirectory() + File.separator + "imageprint.jpg");
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                UtilFile.saveBitmap(bitmap, f.getAbsolutePath());
                hideDialogProgress();
                Intent intent = new Intent(getActivity(), PrintScreenActivity.class);
                intent.putExtra(PrintScreenActivity.EXTRA_BITMAP, f.getAbsolutePath());
                startActivity(intent);

            }
        }, isCircle);
        taskSyncImage.execute();
//        syncAllLayer(AppConstants.TASK_PRINT_IMAGE, "", "");
    }

    private void doShare() {
        showDialogProgress();
        syncAllLayer(AppConstants.TASK_SHARE_IMAGE, "", "");

    }

    @Override
    public void onReceiveBack(int requestSend) {
        if (requestSend == AppConstants.REQUEST_CROP_EDIT) {
            Bitmap bmCrop = CropFragment.getCropBitmap();
            mBitmapCurrent = bmCrop;
            mBitmapOriginal = bmCrop;
            if (colorEffectFilter != null && getContext() != null && mBitmapCurrent != null) {
                colorEffectFilter.setBitmap(getContext(), mBitmapCurrent);
                colorEffectView.setColorEffectFilter(colorEffectFilter);
            }
            getSketchImage();
            getEffectImage();
            setupFrameSeal();
            setImage();

        }
    }

    private void openCropFragment() {
        Bitmap bitmap = mBitmapOriginal;
        if (bitmap == null)
            return;
        this.REQUEST_SEND = AppConstants.REQUEST_CROP_EDIT;
        CropFragment fm = CropFragment.newInstance(bitmap);
        pushFragment(null, fm, true);
        /*FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        tran.add(R.id.content_main, fm);
        tran.addToBackStack(null);
        tran.commit();*/
    }

    private Bitmap updateImage() {
        if (mBitmapCurrent != mBitmapOriginal) {
            btnRefresh.setEnabled(true);
        } else {
            btnRefresh.setEnabled(false);
        }
        if (isChangeColorEffect) {
            btnRefresh.setEnabled(true);
        }
        Bitmap bitmap = mBitmapCurrent;

        if (colorEffectFilter != null) {
            bitmap = colorEffectFilter.apply();
        }
        if (isFlip) {
            btnRefresh.setEnabled(true);
            bitmap = OutputCV.flip(bitmap, isFlip, false);
        }

        return bitmap;
    }

    private void setImage() {
        imgColoring.setImageBitmap(updateImage());
    }

    private void saveImage() {
        if (UtilPermission.isCameraPermissionOn(getContext()) && UtilPermission.isReadExternalPermissionOn(getContext()) && UtilPermission.isWriteExternalPermissionOn(getContext())) {
            showInputDialog();
        } else {
            String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(getActivity(), permissions, AppConstants.REQUEST_PERMISSION_FRAGMENT);
        }

    }

    private void getSketchImage() {
        sketchAdapter = new SketchAdapter(mBitmapCurrent, isCircle);
        sketchAdapter.setClickListener(new SketchAdapter.ClickListener() {
            @Override
            public void onClick(Bitmap bitmap) {
                btnEffect.setEnabled(false);
                mBitmapCurrent = bitmap;
                if (colorEffectFilter != null) {
                    colorEffectFilter.setBitmap(getContext(), mBitmapCurrent);
                    colorEffectView.setColorEffectFilter(colorEffectFilter);
                }
                setImage();
            }
        });
        rvLayerPanel.setAdapter(sketchAdapter);
    }

    private void getEffectImage() {

        if (effects == null) {
            effects = new ArrayList<>();
        }
        effects.clear();
        String[] effectType = getResources().getStringArray(R.array.array_effect_type);
        final String[] effectTypeEn = getResources().getStringArray(R.array.array_effect_type_en);
        for (String s : effectType) {
            effects.add(s);
        }
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        for (int i = 0; i < effects.size(); i++) {
            bitmaps.add(i, BitmapFactory.decodeResource(getResources(), mThumbEffect[i]));
        }
        effectAdapter = new EffectAdapter(effects, mBitmapOriginal, bitmaps);
        effectAdapter.setClickListener(new EffectAdapter.ClickListener() {
            @Override
            public void onClick(Bitmap bitmap, boolean isLoad, final int position) {
                neuralEffectFilter = null;
                if (isLoad) {
                    showDialogProgress();
                    TaskEffectImage taskEffectImage = new TaskEffectImage(effectTypeEn[position], mBitmapOriginal, new TaskEffectImage.TaskMosaicListener() {
                        @Override
                        public void onDone(Bitmap bitmap, String type) {
                            hideDialogProgress();
                            finishEffectImage(bitmap);
                            effectAdapter.setBitmapEffect(bitmap, position);
                        }

                        @Override
                        public void onFail(String error) {
                            hideDialogProgress();
                            Toast.makeText(getContext(), R.string.text_message_effect_error, Toast.LENGTH_LONG).show();
                        }
                    }, isCircle);
                    taskEffectImage.execute();
                } else {
                    finishEffectImage(bitmap);
                }
            }
        });
        rvEffectPanel.setAdapter(effectAdapter);
    }

    private void finishEffectImage(Bitmap bitmap) {
        if (getContext() == null)
            return;
        if (JSONSharedPreferences.getFirstComeEffect(getContext())) {
            layoutGuideEffect.setVisibility(View.VISIBLE);
            JSONSharedPreferences.saveFirstComeEffect(getContext(), false);
        }
        sbFilterEffect.setProgress(100);
        mBitmapCurrent = bitmap;
        if (colorEffectFilter != null) {
            colorEffectFilter.setBitmap(getContext(), mBitmapCurrent);
            colorEffectView.setColorEffectFilter(colorEffectFilter);
        }
        getSketchImage();
        setImage();
        neuralEffectFilter = new NeuralEffectFilter(mBitmapOriginal, bitmap);
    }

    private void getSealImage() {

        if (seals == null) {
            seals = new ArrayList<String>();
        }
        seals.clear();
        String[] sealFont = getResources().getStringArray(R.array.array_seal_font_name);
        for (String s : sealFont
                ) {
            seals.add(s);

        }
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        for (int i = 0; i < seals.size(); i++) {
            bitmaps.add(i, BitmapFactory.decodeResource(getResources(),
                    mThumbSeal[i]));
        }
        sealAdapter = new SealAdapter(seals, bitmaps, getContext());
        sealAdapter.setClickListener(new SealAdapter.ClickListener() {
            @Override
            public void onClick(Bitmap bitmap, int position) {
                sealIndex = position;
                String seal = JSONSharedPreferences.getSeal(getContext());
                if (fingerSeal.getLayer() == null) {
                    showSealDialog(position);
                } else {
                    frameSeal.removeView(fingerSeal.getLayer());
                    fingerSeal.removeView(fingerSeal.getLayer());
                    setupSeal(seal, App.listSealFont[sealIndex], 200);
                }
            }
        });
        rvSealPanel.setAdapter(sealAdapter);
    }

    private void setupFrameSeal() {
        if (mBitmapCurrent == null) {
            return;
        }
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(mBitmapCurrent.getWidth(), mBitmapCurrent.getHeight());
        lp.gravity = Gravity.CENTER;
        frameSeal.setLayoutParams(lp);
        fingerSeal.setxMax(mBitmapCurrent.getWidth());
        fingerSeal.setyMax(mBitmapCurrent.getHeight());

    }

    private void setupSeal(String name, String fontPath, int size) {
        String newSeal = "";
        for (int i = 0; i < name.toCharArray().length; i++) {
            String s = String.valueOf(name.toCharArray()[i]);
            if (i < name.toCharArray().length - 1) {
                newSeal += s + "\n";
            } else {
                newSeal += s;
            }
        }

        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), fontPath);
        tvSeal.setTypeface(face, Typeface.BOLD);

        tvSeal.setIncludeFontPadding(false);
        tvSeal.setText(newSeal);
        tvSeal.setTextSize(size);
        if (sealIndex == 1) {
            tvSeal.setPadding(5, 30, 5, 30);
        } else {
            tvSeal.setPadding(0, 0, 0, 0);
        }

        tvSeal.setDrawingCacheEnabled(true);
        tvSeal.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        tvSeal.layout(0, 0, tvSeal.getMeasuredWidth(), tvSeal.getMeasuredHeight());

        Bitmap bitmap = null;
        if (tvSeal.getDrawingCache() == null) {
            bitmap = UtilBitmap.loadLargeBitmapFromView(tvSeal);
        } else {
            bitmap = Bitmap.createBitmap(tvSeal.getDrawingCache());
        }
        addLayerToSeal(bitmap);
        toolSeal.setProcess(fingerSeal.getCurrentWidth());
        toolSeal.setVisibility(View.VISIBLE);
    }

    public void addLayerToSeal(Bitmap bm) {
        Layer layer = new Layer(getContext());
        layer.setOriginalBitmap(bm, true);
        frameSeal.addView(layer);
        fingerSeal.setLayer(layer);
        fingerSeal.setVisibility(View.VISIBLE);
        hideDialogProgress();


    }

    protected void showInputDialog() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View promptView = layoutInflater.inflate(R.layout.layout_dialog_input_file_info, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edtFileName);
        final RadioButton rdTypePng = (RadioButton) promptView.findViewById(R.id.rbTypePNG);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton(getString(R.string.dialog_add_file_save), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (!editText.getText().toString().equals("")) {
                            String type = getString(R.string.dialog_add_file_type_jpg);
                            if (rdTypePng.isChecked()) {
                                type = getString(R.string.dialog_add_file_type_png);
                            }

                            showDialogProgress();
                            syncAllLayer(AppConstants.TASK_SAVE_IMAGE, editText.getText().toString(), type);
                        }
                    }
                })
                .setNegativeButton(getString(R.string.dialog_add_file_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                CommonUtil.hideSoftKeyboard(getContext());
                            }
                        });

        // create an alert dialog
        final AlertDialog alert = alertDialogBuilder.create();
        alert.show();
        ((AlertDialog) alert).getButton(AlertDialog.BUTTON_POSITIVE)
                .setEnabled(false);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Check if edittext is empty
                if (TextUtils.isEmpty(s)) {
                    // Disable ok button
                    ((AlertDialog) alert).getButton(
                            AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    // Something into edit text. Enable the button.
                    ((AlertDialog) alert).getButton(
                            AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == AppConstants.REQUEST_PERMISSION_FRAGMENT) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            showInputDialog();
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    protected void showSealDialog(final int indexSeal) {
        if (indexSeal < 0)
            return;

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View promptView = layoutInflater.inflate(R.layout.layout_dialog_input_seal, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edtFileName);
        editText.setText(JSONSharedPreferences.getSeal(getContext()));

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton(getString(R.string.dialog_add_seal_next), null)
                .setNegativeButton(getString(R.string.dialog_add_seal_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                CommonUtil.hideSoftKeyboard(getContext());
                            }
                        });


        // create an alert dialog
        final AlertDialog alert = alertDialogBuilder.create();
        alert.show();
        alert.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editText.getText().toString().equals("") && editText.getText().toString().length() < 5) {
                    JSONSharedPreferences.saveSeal(getContext(), editText.getText().toString());
                    frameSeal.removeView(fingerSeal.getLayer());
                    fingerSeal.removeView(fingerSeal.getLayer());
                    setupSeal(editText.getText().toString(), App.listSealFont[sealIndex], 200);
                    CommonUtil.hideSoftKeyboard(getContext());
                    alert.cancel();
                } else {
                    Toast.makeText(getContext(), getString(R.string.dialog_add_seal_error_message), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void syncAllLayer(final String task, final String name, final String type) {
        fingerSeal.hideControl();
        layoutSealPanel.setVisibility(View.GONE);
        rvEffectPanel.setVisibility(View.GONE);
        rvLayerPanel.setVisibility(View.GONE);
        btnEffect.setImageResource(R.drawable.ic_effect);
        btnSketch.setImageResource(R.drawable.ic_sketch);
        btnSeal.setImageResource(R.drawable.ic_seal);
        sbFilterEffect.setVisibility(View.GONE);
        TaskSyncImage taskSyncImage = new TaskSyncImage(layoutCenter, mBitmapCurrent, new TaskSyncImage.TaskSyncImageListener() {
            @Override
            public void onDone(Bitmap bitmap) {
                if (task.equals(AppConstants.TASK_SAVE_IMAGE)) {
                    TaskSaveHistory taskSaveHistory = new TaskSaveHistory(bitmap, name, type, new TaskSaveHistory.SaveHistoryListener() {
                        @Override
                        public void onDone(Boolean isSuccess) {
                            hideDialogProgress();
                            String message = getString(R.string.dialog_add_file_message_success);
                            if (!isSuccess) {
                                message = getString(R.string.dialog_add_file_message_fail);
                            }
                            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                        }
                    });
                    taskSaveHistory.execute();

                } else {
                    if (task.equals(AppConstants.TASK_PRINT_IMAGE)) {

                        Bitmap printBitmap = UtilBitmap.addBufferBitmap(bitmap);
                        PrintHelper printHelper = new PrintHelper(getContext());
                        // Set the desired scale mode.
                        printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);

                        // Get the bitmap for the ImageView's drawable.
                        // Print the bitmap.
                        printHelper.printBitmap("Print Bitmap", printBitmap);
                    } else {
                        TaskShare taskShare = new TaskShare(getContext(), bitmap, new TaskShare.TaskShareListener() {
                            @Override
                            public void onDone(Bitmap bitmap) {
                                hideDialogProgress();
                            }
                        });
                        taskShare.execute();
                    }
                }

            }
        }, isCircle);
        taskSyncImage.execute();


    }

    private Integer[] mThumbEffect = {
            R.drawable.effect_mosaic, R.drawable.effect_comic, R.drawable.effect_tokyo, R.drawable.effect_femme, R.drawable.effect_marvel,
    };
    private Integer[] mThumbSeal = {
            R.drawable.font_kointai, R.drawable.font_insoutai,
    };


    @OnClick(R.id.btnPanelLayout)
    public void onViewClicked() {
        if (rvSealPanel.getVisibility() == View.VISIBLE) {
            expandEffectPanelLayout(View.GONE);
        } else {
            expandEffectPanelLayout(View.VISIBLE);
        }
    }

    private void expandEffectPanelLayout(int status) {

        CommonUtil.slideUpDown(layoutSealPanel, rvSealPanel, status);
        ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) btnPanelLayout.getLayoutParams();
        if (status == View.VISIBLE) {
            //   int valueInPixels = (int) getResources().getDimension(R.dimen.size_image_panel_layout);
            //  marginParams.setMargins(0, 0, 0, valueInPixels);
            btnShow.setImageResource(R.drawable.ic_hide);
            RotateAnimation rotate = new RotateAnimation(360, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(500);
            rotate.setInterpolator(new LinearInterpolator());
            btnShow.startAnimation(rotate);
        } else {
            //  marginParams.setMargins(0, 0, 0, 0);
            btnShow.setImageResource(R.drawable.ic_show);
            RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate.setDuration(500);
            rotate.setInterpolator(new LinearInterpolator());
            btnShow.startAnimation(rotate);

        }

        //rvLayerPanel.setVisibility(status);
    }
}
