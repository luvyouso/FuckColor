package coloring.org.jp.ktcc.full.app.activity.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import org.opencv.core.Point;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import coloring.org.jp.ktcc.full.R;
import coloring.org.jp.ktcc.full.custom_ui.EditLayerView;
import coloring.org.jp.ktcc.full.custom_ui.EraserToolView;
import coloring.org.jp.ktcc.full.custom_ui.fragment.MyFragment;
import coloring.org.jp.ktcc.full.opencv.OutputCV;
import coloring.org.jp.ktcc.full.task.TaskAutoRemoveBackground;

public class EditFragment extends MyFragment {
    @BindView(R.id.tgMode)
    ToggleButton tgMode;

    public interface EditFragmentListener {
        void onEditDone(Bitmap bitmap);
    }

    EditFragmentListener mEditFragmentListener;

    public void setEditFragmentListener(EditFragmentListener listener) {
        this.mEditFragmentListener = listener;
    }

    @BindView(R.id.btnLeftRotate)
    ImageButton btnLeftRotate;
    @BindView(R.id.btnRightRotate)
    ImageButton btnRightRotate;
    @BindView(R.id.btnDone)
    ImageButton btnDone;
    @BindView(R.id.editLayerView)
    EditLayerView editLayerView;
    @BindView(R.id.btnAutoRemove)
    ImageButton btnAutoRemove;
    @BindView(R.id.btnSemiAutoRemove)
    ImageButton btnSemiAutoRemove;
    @BindView(R.id.btnEraser)
    ImageButton btnEraser;
    @BindView(R.id.btnRepair)
    ImageButton btnRepair;
    @BindView(R.id.toolEraser)
    EraserToolView toolEraser;
    @BindView(R.id.toolSemi)
    EraserToolView toolSemi;
    private Bitmap mBitmap;


    private static final String ARG_BITMAP = "bitmap";


    public static EditFragment newInstance(Bitmap bm) {
        EditFragment fragment = new EditFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_BITMAP, bm);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBitmap = getArguments().getParcelable(ARG_BITMAP);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit, container, false);
        ButterKnife.bind(this, root);
        this.initData();

        return root;
    }


    public void initData() {
        editLayerView.addBitmap(mBitmap);
        editLayerView.setEditLayerViewListener(new EditLayerView.EditLayerViewListener() {
            @Override
            public void onDone() {

            }

            @Override
            public void onDoneSemiChange(float x, float y) {
                semiAutoRemove(x, y);

            }

            @Override
            public void onCurrentIndexChange(int index, int sizeIndex) {
                if (sizeIndex == 0) {
                    btnLeftRotate.setEnabled(false);
                    btnRightRotate.setEnabled(false);
                } else {
                    if (index == 0) {
                        btnLeftRotate.setEnabled(false);
                        btnRightRotate.setEnabled(true);
                    } else {
                        if (index == sizeIndex - 1) {
                            btnLeftRotate.setEnabled(true);
                            btnRightRotate.setEnabled(false);
                        } else {
                            btnLeftRotate.setEnabled(true);
                            btnRightRotate.setEnabled(true);
                        }
                    }
                }
            }
        });
        toolEraser.setEraserToolView(new EraserToolView.EraserToolViewListener() {
            @Override
            public void onSeekBarChange(int i) {
            }

            @Override
            public void onProcessSeekBarChange(int i) {
                editLayerView.setEraserMaskSize(i);
            }

            @Override
            public void onDone() {
                editLayerView.showEraserMask(false);
                tgMode.setEnabled(true);
            }
        });
        toolSemi.setEraserToolView(new EraserToolView.EraserToolViewListener() {
            @Override
            public void onSeekBarChange(int i) {
            }

            @Override
            public void onDone() {
                editLayerView.showSemiMask(false);
                tgMode.setEnabled(true);
            }

            @Override
            public void onProcessSeekBarChange(int i) {

            }
        });
        btnLeftRotate.setEnabled(false);
        btnRightRotate.setEnabled(false);
        tgMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editLayerView.setUsePen(isChecked);
            }
        });
        tgMode.setChecked(false);
        editLayerView.setUsePen(false);
    }

    private Bitmap getBitmapFromDrawImageView() {
        return editLayerView.getDrawBitmap();
    }



    @OnClick({R.id.btnLeftRotate, R.id.btnRightRotate, R.id.btnDone, R.id.btnAutoRemove, R.id.btnSemiAutoRemove, R.id.btnEraser})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnLeftRotate:
                editLayerView.undo();
                break;
            case R.id.btnRightRotate:
                editLayerView.redo();
                break;
            case R.id.btnDone:
                if (mEditFragmentListener != null) {
                    mEditFragmentListener.onEditDone(editLayerView.getDrawBitmap());
                }
                popFragment();
                break;
            case R.id.btnAutoRemove:
                autoRemoveBackground();
                break;
            case R.id.btnSemiAutoRemove:
                toolSemi.setVisibility(View.VISIBLE);
                editLayerView.showSemiMask(true);
                tgMode.setEnabled(false);
                break;
            case R.id.btnEraser:
                toolEraser.setVisibility(View.VISIBLE);
                editLayerView.setEraserMaskSize(toolEraser.getValue());
                editLayerView.setEraserSize(toolEraser.getValue());
                editLayerView.showEraserMask(true);
                tgMode.setEnabled(false);
                break;
        }
    }

    private void semiAutoRemove(float x, float y) {
        Bitmap currentBitmap = getBitmapFromDrawImageView();
        if (x < 0 || y < 0 || x > currentBitmap.getWidth() || y > currentBitmap.getHeight()) {

            return;
        }
        Log.e("Done semi ", Math.floor(x) + " - " + Math.floor(y) + " Threshold " + toolSemi.getValue());
        Bitmap resultBitmap = OutputCV.semiAutoRemove(currentBitmap, new Point(Math.floor(x), Math.floor(y)), toolSemi.getValue());
        editLayerView.addBitmap(resultBitmap);
    }

    private void autoRemoveBackground() {
        showDialogProgress();
        TaskAutoRemoveBackground taskAutoRemoveBackground = new TaskAutoRemoveBackground(getBitmapFromDrawImageView(), new TaskAutoRemoveBackground.AutoRemoveBackgroundListener() {
            @Override
            public void onDone(Bitmap bitmap) {
                editLayerView.addBitmap(bitmap);
                hideDialogProgress();
            }
        });
        taskAutoRemoveBackground.execute();


    }

}
