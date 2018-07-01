package coloring.org.jp.ktcc.full.adapter;

import android.graphics.Bitmap;
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
import coloring.org.jp.ktcc.full.util.AppConstants;

/**
 * Created by anh.trinh on 12/19/2017.
 */

public class SketchAdapter extends RecyclerView.Adapter<SketchAdapter.ViewHolder> {

    private ArrayList<Bitmap> mBitmapList;

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
        void onClick(Bitmap bitmap);
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
    public SketchAdapter( Bitmap bitmap, boolean isCircle) {
        if (mSketchNames == null) {
            mSketchNames = new ArrayList<>();
        }
        mSketchNames.clear();
        mSketchNames.add(AppConstants.SKETCH_ABS);
        mSketchNames.add(AppConstants.SKETCH_DIVIDEBOLD);
        mSketchNames.add(AppConstants.SKETCH_DIVIDENORMAL);
        mSketchNames.add(AppConstants.SKETCH_LAPLACIAN);
        mSketchNames.add(AppConstants.SKETCH_PENCILBOLD);
        mSketchNames.add(AppConstants.SKETCH_PENCILLIGHT);
        mSketchNames.add(AppConstants.SKETCH_PENCILNORMAL);
        mSketchNames.add(AppConstants.SKETCH_SOBEL);
        mSketchNames.add(AppConstants.SKETCH_SOBELABS);
        this.mBitmapList = new ArrayList<Bitmap>( this.mSketchNames.size());

        for (int i=0;i<mSketchNames.size();i++){
            TaskSketchImage taskSketchImage = new TaskSketchImage(bitmap, i, new TaskSketchImage.TaskSketchImageListener() {
                @Override
                public void onDone(Bitmap bitmap, int type) {
                    mBitmapList.add(type,bitmap);
                    notifyDataSetChanged();
                }
            }, isCircle);
            taskSketchImage.execute();
        }
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
                    clickListener.onClick(mBitmapList.get(position));
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
