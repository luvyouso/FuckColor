package coloring.org.jp.ktcc.full.custom_ui.fragment.listener;

import android.graphics.Bitmap;
import android.net.Uri;

/**
 * Created by nguyen on 10/19/2017.
 */

public interface ReceiveFromActivityListener {
    void onReceivePhoto(Uri[] results);
    void onReceivePhoto(Bitmap bitmap, int requestSend);
    void onReceiveBack( int requestSent);
}
