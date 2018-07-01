package coloring.org.jp.ktcc.full.app.activity.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import coloring.org.jp.ktcc.full.R;
import coloring.org.jp.ktcc.full.custom_ui.fragment.MyFragment;

/**
 * Created by anh.trinh on 9/13/2016.
 */
public class StaticWebFragment
        extends MyFragment {

    public static final String PRE_TITLE_WEB = "title_web";
    public static final String PRE_LINK_WEB = "link_web";
    @BindView(R.id.ic_back)
    ImageView mIcBack;
    @BindView(R.id.tv_tool_bar_title)
    TextView mTvToolBarTitle;
    @BindView(R.id.webview)
    WebView mWebview;
    String title="";
    String path ="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_static_web, container, false);
        ButterKnife.bind(this, root);
        this.initData();

        return root;
    }


    protected void initData() {
        title= getArguments().getString(PRE_TITLE_WEB,
                                                       "");
        path= getArguments().getString(PRE_LINK_WEB,
                                        "");
        mTvToolBarTitle.setText(title);
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.loadUrl(path);
        mWebview.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                showDialogProgress();
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                hideDialogProgress();
                super.onPageFinished(view, url);
            }
        });

    }


    @Override
    public void onResume() {

        super.onResume();
    }

    @OnClick(R.id.ic_back)
    public void onClick() {
       popFragment();
    }
}
