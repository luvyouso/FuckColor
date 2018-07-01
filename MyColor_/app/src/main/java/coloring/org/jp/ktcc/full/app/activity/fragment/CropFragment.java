package coloring.org.jp.ktcc.full.app.activity.fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;

import coloring.org.jp.ktcc.full.R;
import coloring.org.jp.ktcc.full.custom_ui.fragment.MyFragment;

public class CropFragment extends MyFragment {

    public static Bitmap getCropBitmap() {
        if (mCropBitmap == null){
            return null;
        }else {
            Bitmap bm = mCropBitmap.copy(mCropBitmap.getConfig(), mCropBitmap.isMutable());
            //mCropBitmap.recycle();
           // mCropBitmap = null;
            return bm;
        }
    }
    private static Bitmap mCropBitmap;
    private Bitmap mBitmap;

    public static void setIsCircle(boolean isCircle) {
        CropFragment.isCircle = isCircle;
    }

    public static boolean isCircle() {
        return isCircle;
    }

    private static boolean isCircle = false;


    private static final String ARG_BITMAP = "bitmap";
    private ImageButton btnLeftRotate;
    private ImageButton btnRightRotate;
    private ImageButton btnDone;
    private CropImageView mCropView;
    private Button buttonFitImage;
    private Button button1_1;//square
    private Button button3_4;
    private Button button4_3;
    private Button button9_16;
    private Button button16_9;
    private Button buttonCustom;// 7:5
    private Button buttonFree;
    private Button buttonCircle;
    private Button buttonShowCircleButCropAsSquare;

    public static CropFragment newInstance(Bitmap bm) {
        CropFragment fragment = new CropFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_BITMAP, bm);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mCropBitmap != null){
            mCropBitmap.recycle();
            mCropBitmap = null;
        }
        if (getArguments() != null) {
            mBitmap = getArguments().getParcelable(ARG_BITMAP);
        }
        isCircle = false;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_crop, container, false);
        this.initAvailableView(root);
        this.initData();
        this.setEvent();
        return root;
    }
    public void initAvailableView(View root){
        btnLeftRotate = (ImageButton)root.findViewById(R.id.btnLeftRotate);
        btnRightRotate = (ImageButton)root.findViewById(R.id.btnRightRotate);
        btnDone = (ImageButton)root.findViewById(R.id.btnDone);
        mCropView = (CropImageView)root.findViewById(R.id.cropImageView);
        mCropView.setCropMode(CropImageView.CropMode.FREE);
        buttonFitImage = (Button)root.findViewById(R.id.buttonFitImage);
        button1_1 = (Button)root.findViewById(R.id.button1_1);
        button3_4 = (Button)root.findViewById(R.id.button3_4);
        button4_3 = (Button)root.findViewById(R.id.button4_3);
        button9_16 = (Button)root.findViewById(R.id.button9_16);
        button16_9 = (Button)root.findViewById(R.id.button16_9);
        buttonCustom = (Button)root.findViewById(R.id.buttonCustom);
        buttonFree = (Button)root.findViewById(R.id.buttonFree);
        buttonCircle = (Button)root.findViewById(R.id.buttonCircle);
        buttonShowCircleButCropAsSquare = (Button)root.findViewById(R.id.buttonShowCircleButCropAsSquare);
    }
    public void initData(){
        mCropView.setImageBitmap(mBitmap);
    }
    public void setEvent(){
        btnLeftRotate.setOnClickListener(btnListener);
        btnRightRotate.setOnClickListener(btnListener);
        btnDone.setOnClickListener(btnListener);
        mCropView.setOnClickListener(btnListener);
        buttonFitImage.setOnClickListener(btnListener);
        button1_1.setOnClickListener(btnListener);
        button3_4.setOnClickListener(btnListener);
        button4_3.setOnClickListener(btnListener);
        button9_16.setOnClickListener(btnListener);
        button16_9.setOnClickListener(btnListener);
        buttonCustom.setOnClickListener(btnListener);
        buttonFree.setOnClickListener(btnListener);
        buttonCircle.setOnClickListener(btnListener);
        buttonShowCircleButCropAsSquare.setOnClickListener(btnListener);
    }

    private final View.OnClickListener btnListener = new View.OnClickListener() {
        @Override public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btnBack:
                   popFragment();
                    break;
                case R.id.btnLeftRotate:
                    mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D);
                    break;
                case R.id.btnRightRotate:
                    mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
                    break;
                case R.id.btnDone:
                    showDialogProgress();
                    mCropView.cropAsync(new CropCallback() {
                        @Override
                        public void onSuccess(Bitmap cropped) {
                            hideDialogProgress();
                            mCropBitmap = cropped;
                            popFragment();

                        }
                        @Override
                        public void onError(Throwable e) {
                        }
                    });
                    break;
                case R.id.buttonFitImage:
                    isCircle = false;
                    mCropView.setCropMode(CropImageView.CropMode.FIT_IMAGE);
                    break;
                case R.id.button1_1:
                    isCircle = false;
                    mCropView.setCropMode(CropImageView.CropMode.SQUARE);
                    break;
                case R.id.button3_4:
                    isCircle = false;
                    mCropView.setCropMode(CropImageView.CropMode.RATIO_3_4);
                    break;
                case R.id.button4_3:
                    isCircle = false;
                    mCropView.setCropMode(CropImageView.CropMode.RATIO_4_3);
                    break;
                case R.id.button9_16:
                    isCircle = false;
                    mCropView.setCropMode(CropImageView.CropMode.RATIO_9_16);
                    break;
                case R.id.button16_9:
                    isCircle = false;
                    mCropView.setCropMode(CropImageView.CropMode.RATIO_16_9);
                    break;
                case R.id.buttonCustom:
                    isCircle = false;
                    mCropView.setCustomRatio(7, 5);
                    break;
                case R.id.buttonFree:
                    isCircle = false;
                    mCropView.setCropMode(CropImageView.CropMode.FREE);
                    break;
                case R.id.buttonCircle:
                    isCircle =true;
                    mCropView.setCropMode(CropImageView.CropMode.CIRCLE);
                    break;
                case R.id.buttonShowCircleButCropAsSquare:
                    isCircle =true;
                    mCropView.setCropMode(CropImageView.CropMode.CIRCLE_SQUARE);
                    break;
                default:
                    break;
            }
        }
    };

}
