package coloring.org.jp.ktcc.full.app.activity.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import coloring.org.jp.ktcc.full.R;
import coloring.org.jp.ktcc.full.adapter.AdapterLayers;
import coloring.org.jp.ktcc.full.adapter.ListenerLayer;
import coloring.org.jp.ktcc.full.adapter.SimpleItemTouchHelperCallback;
import coloring.org.jp.ktcc.full.app.activity.MainActivity;
import coloring.org.jp.ktcc.full.custom_ui.ColorEffectView;
import coloring.org.jp.ktcc.full.custom_ui.ListenerMainLayers;
import coloring.org.jp.ktcc.full.custom_ui.MainLayers;
import coloring.org.jp.ktcc.full.custom_ui.fragment.MyFragment;
import coloring.org.jp.ktcc.full.filters.ColorEffectFilter;
import coloring.org.jp.ktcc.full.opencv.OutputCV;
import coloring.org.jp.ktcc.full.task.TaskSyncImage;
import coloring.org.jp.ktcc.full.util.AppConstants;
import coloring.org.jp.ktcc.full.util.CommonUtil;
import coloring.org.jp.ktcc.full.util.UtilPermission;

public class MainFragment extends MyFragment {
    MainLayers mainLayers;
    ImageButton btnAddPhoto;
    ImageButton btnClearAllLayer;
    ImageButton btnHistory;
    ImageButton btnAbout;
    ImageButton btnDone;
    ImageButton btnGuide;
    RecyclerView rvLayerPanel;
    ImageButton btnChangeImage;
    ImageButton btnColorEffect;
    ImageButton btnCrop;
    ImageButton btnFlip;
    ImageButton btnEdit;
    ImageButton btnDelete;
    FrameLayout btnPanelLayout;
    AppCompatImageView btnShow;
    ColorEffectView colorEffectView;
    RelativeLayout layoutPanel;
    ///////////////////////
    AdapterLayers adapterLayerPanel;
    Bitmap mBitmapChoosePhoto = null;
    ColorEffectFilter bgColorEffectFilter;
    Bitmap mBitmapOriginalBg = null;
    private boolean isFlip = false;
    private boolean isCircle = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        this.initAvailableView(root);
        this.initData();
        this.setEvent();
        return root;
    }

    public void initAvailableView(View root) {
        btnAddPhoto = (ImageButton) root.findViewById(R.id.btnAddPhoto);
        btnClearAllLayer = (ImageButton) root.findViewById(R.id.btnClearAllLayer);
        btnHistory = (ImageButton) root.findViewById(R.id.btnHistory);
        btnAbout = (ImageButton) root.findViewById(R.id.btnAbout);
        btnDone = (ImageButton) root.findViewById(R.id.btnDone);
        btnGuide = (ImageButton) root.findViewById(R.id.btnGuide);
        btnPanelLayout = (FrameLayout) root.findViewById(R.id.btnPanelLayout);
        btnShow = (AppCompatImageView) root.findViewById(R.id.btnShow);
        rvLayerPanel = (RecyclerView) root.findViewById(R.id.rvLayerPanel);
        btnChangeImage = (ImageButton) root.findViewById(R.id.btnChangeImage);
        btnColorEffect = (ImageButton) root.findViewById(R.id.btnColorEffect);
        btnCrop = (ImageButton) root.findViewById(R.id.btnCrop);
        btnFlip = (ImageButton) root.findViewById(R.id.btnFlip);
        btnEdit = (ImageButton) root.findViewById(R.id.btnEdit);
        btnDelete = (ImageButton) root.findViewById(R.id.btnDelete);
        mainLayers = (MainLayers) root.findViewById(R.id.mainLayers);
        colorEffectView = (ColorEffectView) root.findViewById(R.id.color_effect_view);
        layoutPanel = (RelativeLayout) root.findViewById(R.id.layout_layer_panel);
        bgColorEffectFilter = new ColorEffectFilter(getContext());
        disableAllEdit();

        //Test
       /* mainLayers.addBackground(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.test));
        mainLayers.addLayer(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.test));*/
        /**  mainLayers.addLayer(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.test));*/
        //
    }

    public void initData() {
        adapterLayerPanel = new AdapterLayers(getContext(), mainLayers.getBitmapLayers());
        rvLayerPanel.setAdapter(adapterLayerPanel);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapterLayerPanel);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(rvLayerPanel);
      //  UtilPermission.checkPermission(getActivity(), AppConstants.REQUEST_PERMISSION);

    }

    private ListenerLayer listenerLayer = new ListenerLayer() {
        @Override
        public void onMove(int fromPosition, int toPosition) {
            mainLayers.sortLayers(fromPosition, toPosition);
        }

        @Override
        public void onDeleted(int position) {
        }

        @Override
        public void onClick(int position) {
            mainLayers.selectLayer(position);
            disableEdit(false);
        }
        @Override
        public void onDoubleClick(int position) {
            mainLayers.doubleClickLayer(position);
            disableEdit(false);
        }
    };

    private ListenerMainLayers listenerMainLayers = new ListenerMainLayers() {
        @Override
        public void selectBackground() {
            adapterLayerPanel.cancelSelectLayer();
            setUpColorEffectView(true);
            disableEdit(true);
        }

        @Override
        public void selectLayer(int position) {
            adapterLayerPanel.selectLayer(position);
            setUpColorEffectView(false);
            disableEdit(false);
        }

        @Override
        public void copySelectLayer() {
            int lastIndex = mainLayers.getBitmapLayers().size() - 1;
            adapterLayerPanel.setPositionSelect(lastIndex);
            adapterLayerPanel.notifyDataSetChanged();
            rvLayerPanel.smoothScrollToPosition(lastIndex);
        }
    };

    private void setUpColorEffectView(boolean isBackground) {
        colorEffectView.initData(isBackground ? bgColorEffectFilter : mainLayers.getCurrentLayerSelected().getColorEffectFilter(), isBackground);
    }


    public void setEvent() {
        btnAddPhoto.setOnClickListener(btnListener);
        btnClearAllLayer.setOnClickListener(btnListener);
        btnHistory.setOnClickListener(btnListener);
        btnAbout.setOnClickListener(btnListener);
        btnDone.setOnClickListener(btnListener);
        btnGuide.setOnClickListener(btnListener);
        btnPanelLayout.setOnClickListener(btnListener);
        btnChangeImage.setOnClickListener(btnListener);
        btnColorEffect.setOnClickListener(btnListener);
        btnCrop.setOnClickListener(btnListener);
        btnFlip.setOnClickListener(btnListener);
        btnEdit.setOnClickListener(btnListener);
        btnDelete.setOnClickListener(btnListener);
        adapterLayerPanel.setListener(listenerLayer);
        mainLayers.setListenerMainLayers(listenerMainLayers);
        colorEffectView.setColorEffectListener(new ColorEffectView.ColorEffectListener() {
            @Override
            public void onChange(float opacity, float hue, float tint, float sepia, float temperature, float brightness, float contrast, float saturation, float blur, boolean isBackground) {
                if (isBackground) {
                    if (bgColorEffectFilter != null && mBitmapOriginalBg != null) {
                        bgColorEffectFilter.setAlpha(opacity);
                        bgColorEffectFilter.setHue(hue);
                        bgColorEffectFilter.setBrightness(brightness);
                        bgColorEffectFilter.setContrast(contrast);
                        bgColorEffectFilter.setSaturation(saturation);
                        bgColorEffectFilter.setSepia(sepia);
                        bgColorEffectFilter.setTemperature(temperature);
                        bgColorEffectFilter.setTint(tint);
                        bgColorEffectFilter.setBlur(blur);
                        updateBackground();
                    }
                } else {
                    if (mainLayers != null && mainLayers.getCurrentLayerSelected() != null) {
                        mainLayers.getCurrentLayerSelected().setColorEffectFilter(opacity, hue, tint, sepia, temperature, brightness, contrast, saturation, blur);
                    }

                }
            }

            @Override
            public void onTouchOutSide() {
                CommonUtil.slideUpDown(colorEffectView);
            }
        });

    }

    private final View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnAddPhoto:
                    REQUEST_SEND = AppConstants.REQUEST_CROP_IMAGE;
                    getListener().onSelectPhoto(false);
                 //openGalleryFragment(AppConstants.REQUEST_CROP_IMAGE);
                    break;
                case R.id.btnClearAllLayer:
                    removeAllLayer();
                    break;
                case R.id.btnHistory:
                    openHistoryFragment();
                    break;
                case R.id.btnGuide:
                    showWebStatic(getString(R.string.web_page_title_guide), AppConstants.PATH_GUIDE_PAGE);
                    break;
                case R.id.btnAbout:
                    showWebStatic(getString(R.string.web_page_title_about), AppConstants.PATH_ABOUT_PAGE);
                    break;
                case R.id.btnDone:
                    syncAllLayer();
                    break;
                case R.id.btnPanelLayout:
                    if (rvLayerPanel.getVisibility() == View.VISIBLE) {
                        expandPanelLayout(View.GONE);
                    } else {
                        expandPanelLayout(View.VISIBLE);
                    }
                    break;
                case R.id.btnChangeImage:
                    REQUEST_SEND = AppConstants.REQUEST_CROP_EDIT;
                    getListener().onSelectPhoto(false);

                    //openGalleryFragment(AppConstants.REQUEST_CROP_EDIT);
                    break;
                case R.id.btnColorEffect:
                    colorEffectLayer();
                    break;
                case R.id.btnCrop:
                    if (mainLayers.getCurrentLayerSelected() != null && mainLayers.getCurrentLayerSelected().getOriginalBitmap() != null) {
                        openCropFragment(AppConstants.REQUEST_CROP_EDIT, mainLayers.getCurrentLayerSelected().getOriginalBitmap());
                    }
                    break;
                case R.id.btnFlip:
                    flipLayer();
                    break;
                case R.id.btnEdit:
                    openEditFragment();
                    break;
                case R.id.btnDelete:
                    removeLayer();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onReceivePhoto(Bitmap bitmap, int requestSend) {
        super.onReceivePhoto(bitmap, requestSend);
        try {
            this.mBitmapChoosePhoto = bitmap;
            openCropFragment(requestSend, bitmap);

        } catch (Exception ex) {
            Toast.makeText(getContext(), R.string.can_not_read_file, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onReceiveBack(int requestSend) {
        if (requestSend == AppConstants.REQUEST_CROP_IMAGE) {
            Bitmap bmCrop = CropFragment.getCropBitmap();
            if (bmCrop == null) {
                bmCrop = this.mBitmapChoosePhoto;
            }
            if (bmCrop != null) {
                if (mainLayers.getBitmapBackground() == null) {
                 // check background is circle
                    isCircle = CropFragment.isCircle();
                    mBitmapOriginalBg = bmCrop;
                    mainLayers.addBackground(bmCrop);
                    btnDone.setEnabled(true);
                    btnClearAllLayer.setEnabled(true);
                    disableEdit(true);
                    bgColorEffectFilter.setBitmap(getContext(),mBitmapOriginalBg);
                } else {
                    if (rvLayerPanel != null) {
                        expandPanelLayout(View.VISIBLE);
                        mainLayers.addLayer(bmCrop);
                        int lastIndex = mainLayers.getBitmapLayers().size() - 1;
                        adapterLayerPanel.setPositionSelect(lastIndex);
                        adapterLayerPanel.notifyDataSetChanged();
                        rvLayerPanel.smoothScrollToPosition(lastIndex);
                        disableEdit(false);
                    }
                }
            }
        } else if (requestSend == AppConstants.REQUEST_CROP_EDIT) {
            Bitmap bmCrop = CropFragment.getCropBitmap();
            mainLayers.updateLayer(bmCrop, adapterLayerPanel.getPositionSelect(), true);
            if (rvLayerPanel != null) {
                adapterLayerPanel.notifyDataSetChanged();

            }
        }
    }

    private void openCropFragment(int requestSend, Bitmap bitmap) {
        if (bitmap == null)
            return;
        this.REQUEST_SEND = requestSend;
        CropFragment fm = CropFragment.newInstance(bitmap);
        pushFragment(null, fm, true);
       /* FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        tran.add(R.id.content_main, fm);
        tran.addToBackStack(null);
        tran.commit();*/
    }

    private void openColoringFragment(Bitmap bitmap) {
        if (bitmap == null)
            return;
        this.REQUEST_SEND = AppConstants.REQUEST_COLORING_IMAGE;
        ColoringFragment fm = ColoringFragment.newInstance(bitmap,isCircle);
        pushFragment(null, fm, true);
      /*  FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        tran.add(R.id.content_main, fm);
        tran.addToBackStack(null);
        tran.commit();*/
    }

    private void expandPanelLayout(int status) {
        CommonUtil.slideUpDown(layoutPanel,rvLayerPanel,status);
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

    private void flipLayer() {
        if (mainLayers != null) {
            if (mainLayers.getCurrentLayerSelected() != null) {
                mainLayers.getCurrentLayerSelected().flipHorizontal();
            } else {
                if (mBitmapOriginalBg != null) {
                    isFlip = !isFlip;
                    updateBackground();
                }
            }
        }
    }

    private void colorEffectLayer() {
        setUpColorEffectView(mainLayers.getCurrentLayerSelected() == null);
        if (mainLayers.getCurrentLayerSelected() != null || mainLayers.getBitmapBackground() != null) {
            CommonUtil.slideUpDown(colorEffectView);
            if (colorEffectView.getVisibility() == View.VISIBLE && rvLayerPanel.getVisibility() == View.VISIBLE) {
                expandPanelLayout(View.GONE);
            }
        }
    }

    private void disableEdit(boolean isBackground) {
        btnChangeImage.setEnabled(!isBackground);
        btnEdit.setEnabled(!isBackground);
        btnDelete.setEnabled(!isBackground);
        btnCrop.setEnabled(!isBackground);
        btnColorEffect.setEnabled(true);
        btnFlip.setEnabled(true);
    }

    private void disableAllEdit() {
        btnDone.setEnabled(false);
        btnClearAllLayer.setEnabled(false);
        btnChangeImage.setEnabled(false);
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);
        btnCrop.setEnabled(false);
        btnColorEffect.setEnabled(false);
        btnFlip.setEnabled(false);

    }

    private void syncAllLayer() {
        if (mainLayers != null && mainLayers.getBackgroundImageView() != null) {
            CommonUtil.showActionAlertYESORNO(getContext(), getString(R.string.dialog_warning_done_title), getString(R.string.dialog_warning_done_message),
                    getString(R.string.dialog_warning_done_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showDialogProgress();
                            mainLayers.hideFinderControl();
                            TaskSyncImage taskSyncImage = new TaskSyncImage(mainLayers, mainLayers.getBackgroundImageView(), new TaskSyncImage.TaskSyncImageListener() {
                                @Override
                                public void onDone(Bitmap bitmap) {
                                    openColoringFragment(bitmap);
                                    mainLayers.showFinderControl();
                                    hideDialogProgress();
                                }
                            }, isCircle);
                            taskSyncImage.execute();

                        }
                    }, getString(R.string.dialog_warning_done_no));

        }

    }

    private void openEditFragment() {
        if (mainLayers != null && mainLayers.getCurrentLayerSelected() != null) {
            Bitmap bitmap = mainLayers.getCurrentLayerSelected().getOriginalBitmap();
            if (bitmap != null) {
                this.REQUEST_SEND = AppConstants.REQUEST_EDIT_IMAGE;
                EditFragment fm = EditFragment.newInstance(bitmap);
                fm.setEditFragmentListener(new EditFragment.EditFragmentListener() {
                    @Override
                    public void onEditDone(Bitmap bitmap) {
                        if (adapterLayerPanel != null) {
                            mainLayers.updateLayer(bitmap, adapterLayerPanel.getPositionSelect(), false);
                            adapterLayerPanel.notifyDataSetChanged();

                        }
                    }
                });
                pushFragment(null, fm, true);
              /*  FragmentTransaction tran = getFragmentManager().beginTransaction();
                tran.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
                tran.add(R.id.content_main, fm);
                tran.addToBackStack(null);
                tran.commit();*/
            }
        }
    }

    private void removeLayer() {
        if (mainLayers != null && adapterLayerPanel != null) {
            CommonUtil.showActionAlertYESORNO(getContext(), getString(R.string.dialog_warning_remove_layer_title), getString(R.string.dialog_warning_remove_layer_message),
                    getString(R.string.dialog_warning_remove_layer_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mainLayers.removeLayer(adapterLayerPanel.getPositionSelect());
                            int selectIndex = adapterLayerPanel.getPositionSelect() > adapterLayerPanel.getItemCount()-1 ? adapterLayerPanel.getPositionSelect() - 1 : adapterLayerPanel.getPositionSelect();
                            adapterLayerPanel.setPositionSelect(selectIndex);
                            adapterLayerPanel.notifyDataSetChanged();
                            if (mainLayers.getBitmapLayers().size() == 0) {
                                expandPanelLayout(View.GONE);
                                mainLayers.selectBackground();
                                disableEdit(true);
                            }
                        }
                    }, getString(R.string.dialog_warning_remove_layer_no));

        }
    }

    private void removeAllLayer() {
        if (mainLayers != null && adapterLayerPanel != null && mainLayers.getBackgroundImageView() != null) {
            CommonUtil.showActionAlertYESORNO(getContext(), getString(R.string.dialog_warning_remove_all_layer_title), getString(R.string.dialog_warning_remove_all_layer_message),
                    getString(R.string.dialog_warning_remove_all_layer_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mainLayers.removeAllLayer();
                            adapterLayerPanel.notifyDataSetChanged();
                            expandPanelLayout(View.GONE);
                            resetBackground();
                            disableAllEdit();
                        }
                    }, getString(R.string.dialog_warning_remove_all_layer_no));


        }
    }
    private void resetBackground(){
        mBitmapOriginalBg = null;
        bgColorEffectFilter.reset();
        isFlip = false;
    }

    private void updateBackground() {
        Bitmap bitmap = mBitmapOriginalBg;

        if (bgColorEffectFilter != null) {
            bitmap = bgColorEffectFilter.apply();
        }
        if (isFlip) {
            bitmap = OutputCV.flip(bitmap, isFlip, false);
        }
        if (bitmap != null) {
            mainLayers.updateBackgound(bitmap);
        }
    }

    private void openHistoryFragment() {
        this.REQUEST_SEND = AppConstants.REQUEST_HISTORY;
        HistoryFragment fm = HistoryFragment.newInstance();
        pushFragment(null, fm, false);
      /*  FragmentTransaction tran = getFragmentManager().beginTransaction();
      //  tran.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        tran.add(R.id.content_main, fm);
        tran.addToBackStack(null);
        tran.commit();*/
    }

    private void showWebStatic(final String title, String path) {
        this.REQUEST_SEND = AppConstants.REQUEST_WEB_PAGE;

        Bundle bundle = new Bundle();
        bundle.putString(StaticWebFragment.PRE_TITLE_WEB, title);
        bundle.putString(StaticWebFragment.PRE_LINK_WEB, path);

        StaticWebFragment fm = new StaticWebFragment();
        pushFragment(bundle, fm, false);
        /*fragment.setArguments(bundle);
        FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        tran.add(R.id.content_main, fragment);
        tran.addToBackStack(null);
        tran.commit();*/
    }
    private void openGalleryFragment(final int requestSend) {
        this.REQUEST_SEND = AppConstants.REQUEST_GALLERY_PAGE;
        String galleryType = mBitmapOriginalBg==null?getString(R.string.fragment_background_title):getString(R.string.fragment_layer_title);
        GalleryFragment fm = GalleryFragment.newInstance(galleryType);
        fm.setGalleryFragmentListener(new GalleryFragment.GalleryFragmentListener() {
            @Override
            public void onSelectDone(Bitmap bitmap) {
                mBitmapChoosePhoto = bitmap;
                openCropFragment(requestSend, bitmap);
            }
        });
        pushFragment(null, fm, true);
      /*  FragmentTransaction tran = getFragmentManager().beginTransaction();
        tran.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        tran.add(R.id.content_main, fm);
        tran.addToBackStack(null);
        tran.commit();*/
    }
}

