package coloring.org.jp.ktcc.full.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;

import coloring.org.jp.ktcc.full.R;

/**
 * Created by nguyen on 11/3/2017.
 */


public class AdapterLayers extends RecyclerView.Adapter<AdapterLayers.ItemViewHolder> implements ItemTouchHelperAdapter {
    private  ArrayList<Bitmap> data;
    private ListenerLayer mListener;
    private int positionSelect = -1;
    public int getPositionSelect() {
        return positionSelect;
    }

    public void setPositionSelect(int positionSelect) {
        this.positionSelect = positionSelect;
    }
    public void setListener(ListenerLayer mListener) {
        this.mListener = mListener;
    }
    public AdapterLayers(Context context, ArrayList<Bitmap> data){
        this.data = data;
    }

    @Override
    public void onItemDismiss(int position) {
        data.remove(position);
        notifyItemRemoved(position);
        if (mListener != null){
            mListener.onDeleted(position);
        }
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(data, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(data, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        if (mListener != null){
            mListener.onMove(fromPosition, toPosition);
        }

        return true;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.item_image, parent, false);
        ItemViewHolder vh = new ItemViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, final int position) {
        if (positionSelect == -1 || positionSelect != position){
            holder.image.setBackground(null);
        }else{
            holder.image.setBackgroundResource(R.drawable.border_finder);
        }
        holder.image.setImageBitmap(data.get(position));
    }
    public void selectLayer(int position){
        positionSelect = position;
        notifyDataSetChanged();
    }
    public void cancelSelectLayer(){
        positionSelect = -1;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public AppCompatImageView image;
        public ItemViewHolder(View v) {
            super(v);
            image = v.findViewById(R.id.image);
            image.setBackground(null);
            image.setOnClickListener(null);
            image.setOnClickListener(new DoubleClickListener() {
                @Override
                public void onSingleClick(View v) {
                    int p = getAdapterPosition();
                    selectLayer(p);
                    if (mListener != null){
                        mListener.onClick(p);
                    }
                }

                @Override
                public void onDoubleClick(View v) {
                    int p = getAdapterPosition();
                    selectLayer(p);
                    if (mListener != null){
                        mListener.onDoubleClick(p);
                    }
                }
            });
        }
    }
    public abstract class DoubleClickListener implements View.OnClickListener {

        private static final long DOUBLE_CLICK_TIME_DELTA = 300;//milliseconds

        long lastClickTime = 0;

        @Override
        public void onClick(View v) {
            long clickTime = System.currentTimeMillis();
            if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA){
                onDoubleClick(v);
                lastClickTime = 0;
            } else {
                onSingleClick(v);
            }
            lastClickTime = clickTime;
        }

        public abstract void onSingleClick(View v);
        public abstract void onDoubleClick(View v);
    }
}


