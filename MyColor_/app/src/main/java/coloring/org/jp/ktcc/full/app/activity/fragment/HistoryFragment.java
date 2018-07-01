package coloring.org.jp.ktcc.full.app.activity.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.print.PrintHelper;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import coloring.org.jp.ktcc.full.R;
import coloring.org.jp.ktcc.full.adapter.HistoryAdapter;
import coloring.org.jp.ktcc.full.custom_ui.fragment.MyFragment;
import coloring.org.jp.ktcc.full.task.TaskShare;
import coloring.org.jp.ktcc.full.util.AppConstants;
import coloring.org.jp.ktcc.full.util.CommonUtil;
import coloring.org.jp.ktcc.full.util.UtilFile;
import coloring.org.jp.ktcc.full.util.UtilPermission;
import io.reactivex.annotations.NonNull;


public class HistoryFragment extends MyFragment {

    @BindView(R.id.btnBack)
    ImageButton btnBack;
    @BindView(R.id.btnClearAllHistory)
    ImageButton btnClearAllHistory;
    @BindView(R.id.btnRemoveFile)
    ImageButton btnRemoveFile;
    @BindView(R.id.btnPrint)
    ImageButton btnPrint;
    @BindView(R.id.btnShare)
    ImageButton btnShare;
    @BindView(R.id.layout_top)
    LinearLayout layoutTop;
    @BindView(R.id.imgColoring)
    AppCompatImageView imgColoring;
    @BindView(R.id.btnShow)
    AppCompatImageView btnShow;
    @BindView(R.id.btnPanelLayout)
    FrameLayout btnPanelLayout;
    @BindView(R.id.rvLayerPanel)
    RecyclerView rvLayerPanel;
    @BindView(R.id.layout_center)
    RelativeLayout layoutCenter;
    @BindView(R.id.tvNodata)
    TextView tvNodata;
    @BindView(R.id.tvFileName)
    TextView tvFileName;

    private HistoryAdapter sketchAdapter;
    private List<Bitmap> bitmaps;
    private ArrayList<String> files;
    private int currentIndex = -1;

    @OnClick({R.id.btnBack, R.id.btnClearAllHistory, R.id.btnRemoveFile, R.id.btnPrint, R.id.btnShare, R.id.btnPanelLayout, R.id.imgColoring})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnBack:
                popFragment();
                break;
            case R.id.btnClearAllHistory:
                doRemoveAllFiles();
                break;
            case R.id.btnRemoveFile:
                doRemoveFile();
                break;
            case R.id.btnPrint:
                doPrint();
                break;
            case R.id.btnShare:
                doShare();
                break;
            case R.id.btnPanelLayout:
                if (rvLayerPanel.getVisibility() == View.VISIBLE) {
                    expandPanelLayout(View.GONE);
                } else {
                    expandPanelLayout(View.VISIBLE);
                }
                break;
            case R.id.imgColoring:
                if(currentIndex>-1 && currentIndex<files.size()) {
                    displayFileName(getFileNamefromPath(files.get(currentIndex)));
                }
                break;
        }
    }
    private void expandPanelLayout(int status) {
        ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) btnPanelLayout.getLayoutParams();
        if (status == View.VISIBLE) {
            int valueInPixels = (int) getResources().getDimension(R.dimen.size_image_panel_layout);
            marginParams.setMargins(0, 0, 0, valueInPixels);
            btnShow.setImageResource(R.drawable.ic_hide);
        } else {
            marginParams.setMargins(0, 0, 0, 0);
            btnShow.setImageResource(R.drawable.ic_show);
        }
        CommonUtil.slideUpDown(rvLayerPanel);
        //rvLayerPanel.setVisibility(status);
    }
    private void setEnabledButton(boolean isEnable ){
        btnClearAllHistory.setEnabled(isEnable);
        btnRemoveFile.setEnabled(isEnable);
        btnPrint.setEnabled(isEnable);
        btnShare.setEnabled(isEnable);
    }


    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, root);
        this.initData();
        return root;
    }


    public void initData() {
        setEnabledButton(false);
        if (UtilPermission.isCameraPermissionOn(getContext()) && UtilPermission.isReadExternalPermissionOn(getContext()) && UtilPermission.isWriteExternalPermissionOn(getContext())){
            getHistoryImage();
        }else{
            String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(getActivity(), permissions, AppConstants.REQUEST_PERMISSION_FRAGMENT);
        }
    }
    private void setImage(int position){
        if(position>-1 && position<bitmaps.size()) {
            currentIndex = position;
            displayFileName(getFileNamefromPath(files.get(position)));
            imgColoring.setImageBitmap(bitmaps.get(position));
        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
    private String getFileNamefromPath(String path){
        if(path.isEmpty()){
            return "";
        }
        if(path.contains("/")){
            return path.substring(path.lastIndexOf("/")+1);
        }else{
            return path;
        }
    }


    private void doPrint() {
        if(currentIndex>-1 && currentIndex<bitmaps.size()) {
            PrintHelper printHelper = new PrintHelper(getContext());
            // Set the desired scale mode.
            printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);
            // Get the bitmap for the ImageView's drawable.
            // Print the bitmap.
            printHelper.printBitmap("Print Bitmap", bitmaps.get(currentIndex));
        }

    }

    private void doShare() {
        if(currentIndex>-1 && currentIndex<bitmaps.size()) {
            showDialogProgress();
            TaskShare taskShare = new TaskShare(getContext(),  bitmaps.get(currentIndex), new TaskShare.TaskShareListener() {
                @Override
                public void onDone(Bitmap bitmap) {
                    hideDialogProgress();
                }
            });
            taskShare.execute();
        }

    }
    public void getHistoryImage()
    {
        bitmaps = new ArrayList<>();
        files = new ArrayList<String>();// list of file paths
        File[] listFile;
        File file= new File(android.os.Environment.getExternalStorageDirectory(),UtilFile.FOLDER_HISTORY);

        if (file.isDirectory())
        {
            listFile = file.listFiles();
            if(listFile!=null && listFile.length>0) {
                Arrays.sort(listFile, new Comparator() {
                    public int compare(Object o1, Object o2) {

                        if (((File) o1).lastModified() > ((File) o2).lastModified()) {
                            return -1;
                        } else if (((File) o1).lastModified() < ((File) o2).lastModified()) {
                            return +1;
                        } else {
                            return 0;
                        }
                    }
                });
                for (int i = 0; i < listFile.length; i++)
                {
                    files.add(listFile[i].getAbsolutePath());
                    bitmaps.add(BitmapFactory.decodeFile(listFile[i].getAbsolutePath()));
                }
            }

        }
        if(bitmaps.size()<1){
            setEnabledButton(false);
            tvNodata.setVisibility(View.VISIBLE);
        }else {
            setEnabledButton(true);
            tvNodata.setVisibility(View.GONE);
            setImage(0);
            sketchAdapter = new HistoryAdapter(bitmaps);
            rvLayerPanel.setAdapter(sketchAdapter);
            expandPanelLayout(View.VISIBLE);
        }
        rvLayerPanel.addOnItemTouchListener(new HistoryAdapter.HistoryItemTouchListener(getContext(), rvLayerPanel, new HistoryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                setImage(position);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }
    private void doRemoveFile(){
        if(files == null)
            return;
        if(currentIndex>-1 && currentIndex<files.size()){
            CommonUtil.showActionAlertYESORNO(getContext(), getString(R.string.dialog_warning_remove_layer_title), getString(R.string.dialog_warning_remove_history_message),
                    getString(R.string.dialog_warning_remove_history_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            UtilFile.deleteFiles(files.get(currentIndex));
                            files.remove(currentIndex);
                            bitmaps.remove(currentIndex);
                            if(bitmaps.size()>0){
                                if(currentIndex>bitmaps.size()){
                                    currentIndex--;
                                }
                                imgColoring.setImageBitmap(bitmaps.get(currentIndex));
                                tvFileName.setText(getFileNamefromPath(files.get(currentIndex)));
                            }else {
                                currentIndex = -1;
                                tvNodata.setVisibility(View.VISIBLE);
                                setEnabledButton(false);
                            }
                            sketchAdapter.notifyDataSetChanged();
                        }

                    }, getString(R.string.dialog_warning_remove_history_no));

        }
    }
    private void doRemoveAllFiles(){
        if(files!=null && files.size()>0 ){
            CommonUtil.showActionAlertYESORNO(getContext(), getString(R.string.dialog_warning_remove_all_layer_title), getString(R.string.dialog_warning_remove_all_history_message),
                    getString(R.string.dialog_warning_remove_all_history_yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for (String path: files
                                 ) {
                                UtilFile.deleteFiles(path);
                            }

                            files.clear();
                            bitmaps.clear();
                            currentIndex = -1;
                            tvNodata.setVisibility(View.VISIBLE);
                            setEnabledButton(false);

                        }
                    }, getString(R.string.dialog_warning_remove_all_history_no));

        }
    }
    private void displayFileName(String fileName){
        tvFileName.setVisibility(View.VISIBLE);
        tvFileName.setText(fileName);
        final Animation out =   AnimationUtils.loadAnimation(getContext(),R.anim.fade_out);
        tvFileName.startAnimation(out);
        tvFileName.setVisibility(View.INVISIBLE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == AppConstants.REQUEST_PERMISSION_FRAGMENT) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            getHistoryImage();
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }



}
