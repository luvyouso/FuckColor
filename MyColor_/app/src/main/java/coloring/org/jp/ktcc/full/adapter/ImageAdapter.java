package coloring.org.jp.ktcc.full.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import coloring.org.jp.ktcc.full.R;

/**
 * Created by anh.trinh on 2/1/2018.
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private Integer[] mThumbIds;

    public ImageAdapter(Context c,Integer[] ids) {
        mContext = c;
        mThumbIds = ids;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return mThumbIds[position];
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {

            gridView = new View(mContext);
            gridView = inflater.inflate(R.layout.item_gallery, null);

        } else {
            gridView = (View) convertView;
        }
        ImageView imageView = (ImageView) gridView
                .findViewById(R.id.image);

        imageView.setImageResource(mThumbIds[position]);

        return gridView;

    }


}