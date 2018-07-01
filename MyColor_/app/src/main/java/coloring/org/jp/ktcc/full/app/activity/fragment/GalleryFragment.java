package coloring.org.jp.ktcc.full.app.activity.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import coloring.org.jp.ktcc.full.R;
import coloring.org.jp.ktcc.full.adapter.ImageAdapter;
import coloring.org.jp.ktcc.full.custom_ui.fragment.MyFragment;

public class GalleryFragment extends MyFragment {
    @BindView(R.id.btnBack)
    ImageButton btnBack;
    private static final String ARG_GALLERY_TYPE = "type";
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.layoutTop)
    RelativeLayout layoutTop;
    @BindView(R.id.gridview)
    GridView gridviewGallery;
    private String type;
    private Integer[] mThumbIds;

    public interface GalleryFragmentListener {
        void onSelectDone(Bitmap bitmap);
    }

    GalleryFragmentListener mGalleryFragmentListener;

    public void setGalleryFragmentListener(GalleryFragmentListener listener) {
        this.mGalleryFragmentListener = listener;
    }

    public static GalleryFragment newInstance(String type) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GALLERY_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type = getArguments().getString(ARG_GALLERY_TYPE);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        ButterKnife.bind(this, root);
        this.initData();

        return root;
    }


    public void initData() {
        tvTitle.setText(type);

        if (type.equals(getString(R.string.fragment_background_title))) {
            mThumbIds = mThumbBackgroundIds;
        } else {
            mThumbIds = mThumbLayerIds;
            ;
        }
        ImageAdapter imageAdapter = new ImageAdapter(getActivity(), mThumbIds);
        gridviewGallery.setAdapter(imageAdapter);

        gridviewGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                popFragment();
                if (mGalleryFragmentListener != null) {
                    if (type.equals(getString(R.string.fragment_background_title))) {
                        mGalleryFragmentListener.onSelectDone(BitmapFactory.decodeResource(getResources(),
                                mBackgroundIds[position]));
                    } else {
                        mGalleryFragmentListener.onSelectDone(BitmapFactory.decodeResource(getResources(),
                                mLayerIds[position]));
                    }

                }

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @OnClick(R.id.btnBack)
    public void onViewClicked() {
        popFragment();
    }

    // references to our images
    private Integer[] mThumbBackgroundIds = {
            R.drawable.bg_1, R.drawable.bg_4,
            R.drawable.bg_2, R.drawable.bg_5,
            R.drawable.bg_3, R.drawable.bg_6,
            R.drawable.bg_7, R.drawable.bg_8,
            R.drawable.bg_9, R.drawable.bg_10,
            R.drawable.bg_11, R.drawable.bg_12,
            R.drawable.bg_13, R.drawable.bg_14,
            R.drawable.bg_15,
    };
    private Integer[] mBackgroundIds = {
            R.drawable.img_bg_1, R.drawable.img_bg_4,
            R.drawable.img_bg_2, R.drawable.img_bg_5,
            R.drawable.img_bg_3, R.drawable.img_bg_6,
            R.drawable.img_bg_7, R.drawable.img_bg_8,
            R.drawable.img_bg_9, R.drawable.img_bg_10,
            R.drawable.img_bg_11, R.drawable.img_bg_12,
            R.drawable.img_bg_13, R.drawable.img_bg_14,
            R.drawable.img_bg_15,

    };
    private Integer[] mLayerIds = {
            R.drawable.layer_1, R.drawable.layer_2,
            R.drawable.layer_3, R.drawable.layer_4,
            R.drawable.layer_5, R.drawable.layer_6,
            R.drawable.layer_7, R.drawable.layer_8,
            R.drawable.layer_9, R.drawable.layer_10,
            R.drawable.layer_11, R.drawable.layer_12,
            R.drawable.layer_13, R.drawable.layer_14,
            R.drawable.layer_15, R.drawable.layer_16,
            R.drawable.layer_17, R.drawable.layer_18,
            R.drawable.layer_19, R.drawable.layer_20,
            R.drawable.layer_21, R.drawable.layer_22,
            R.drawable.layer_23, R.drawable.layer_24,
            R.drawable.layer_25, R.drawable.layer_26,
            R.drawable.layer_27, R.drawable.layer_28,
            R.drawable.layer_29, R.drawable.layer_30,
            R.drawable.layer_31, R.drawable.layer_32,
            R.drawable.layer_33, R.drawable.layer_34,
            R.drawable.layer_35, R.drawable.layer_36,
            R.drawable.layer_37, R.drawable.layer_38,
            R.drawable.layer_39, R.drawable.layer_40,
            R.drawable.layer_41, R.drawable.layer_42,
            R.drawable.img_bg_6, R.drawable.img_bg_7,
            R.drawable.img_bg_8, R.drawable.img_bg_9,
            R.drawable.img_bg_11,


    };
    private Integer[] mThumbLayerIds = {
            R.drawable.img_layer_1, R.drawable.img_layer_2,
            R.drawable.img_layer_3, R.drawable.img_layer_4,
            R.drawable.img_layer_5, R.drawable.img_layer_6,
            R.drawable.img_layer_7, R.drawable.img_layer_8,
            R.drawable.img_layer_9, R.drawable.img_layer_10,
            R.drawable.img_layer_11, R.drawable.img_layer_12,
            R.drawable.img_layer_13, R.drawable.img_layer_14,
            R.drawable.img_layer_15, R.drawable.img_layer_16,
            R.drawable.img_layer_17, R.drawable.img_layer_18,
            R.drawable.img_layer_19, R.drawable.img_layer_20,
            R.drawable.img_layer_21, R.drawable.img_layer_22,
            R.drawable.img_layer_23, R.drawable.img_layer_24,
            R.drawable.img_layer_25, R.drawable.img_layer_26,
            R.drawable.img_layer_27, R.drawable.img_layer_28,
            R.drawable.img_layer_29, R.drawable.img_layer_30,
            R.drawable.img_layer_31, R.drawable.img_layer_32,
            R.drawable.img_layer_33, R.drawable.img_layer_34,
            R.drawable.img_layer_35, R.drawable.img_layer_36,
            R.drawable.img_layer_37, R.drawable.img_layer_38,
            R.drawable.img_layer_39, R.drawable.img_layer_40,
            R.drawable.img_layer_41, R.drawable.img_layer_42,
            R.drawable.bg_6, R.drawable.bg_7,
            R.drawable.bg_8, R.drawable.bg_9,
            R.drawable.bg_11,


    };
}
