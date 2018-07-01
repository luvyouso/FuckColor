package coloring.org.jp.ktcc.full.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import coloring.org.jp.ktcc.full.R;
import coloring.org.jp.ktcc.full.task.TaskSketchImage;

/**
 * Created by anh.trinh on 12/19/2017.
 */

public class SealAdapter extends RecyclerView.Adapter<SealAdapter.ViewHolder> {

    private ArrayList<Bitmap> mBitmapList;
    private Context mContext;

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
        notifyDataSetChanged();
    }

    private int currentIndex = -1;
    private List<String> mSketchNames;

    public ClickListener getClickListener() {
        return clickListener;
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    private ClickListener clickListener;
    public interface ClickListener {
        void onClick(Bitmap bitmap, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvType)
        TextView tvType;
        @BindView(R.id.image)
        AppCompatImageView imageView;
        @BindView(R.id.loadingImage)
        ProgressBar progressBar;
        @BindView(R.id.item_sketch)
        LinearLayout itemSketch;
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
    public SealAdapter(List<String> sketchNames, ArrayList<Bitmap> bitmaps, Context context) {
        this.mContext = context;
        this.mSketchNames = sketchNames;
        this.mBitmapList = bitmaps;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sketch_image, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        if(mBitmapList== null || mBitmapList.size()<=position|| mBitmapList.get(position)== null){
            holder.progressBar.setVisibility(View.VISIBLE);
        }else{
            holder.progressBar.setVisibility(View.GONE);
            holder.imageView.setImageBitmap( mBitmapList.get(position));
        }

        if(mSketchNames!=null && position<mSketchNames.size()) {
            holder.tvType.setText(mSketchNames.get(position));
        }

        holder.itemSketch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentIndex = position;
                notifyDataSetChanged();
                if ( clickListener != null) {
                    clickListener.onClick(mBitmapList.get(position), position);
                }
            }
        });
        if(currentIndex == position){
            holder.itemSketch.setBackgroundResource(R.drawable.border_finder);
        }else{
            holder.itemSketch.setBackgroundResource(R.color.transparent);
        }
    }

    @Override
    public int getItemCount() {
        return mSketchNames.size();
    }

}
