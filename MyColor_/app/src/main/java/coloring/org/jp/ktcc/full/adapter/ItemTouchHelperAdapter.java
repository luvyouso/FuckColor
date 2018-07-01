package coloring.org.jp.ktcc.full.adapter;

/**
 * Created by nguyen on 11/3/2017.
 */

public interface ItemTouchHelperAdapter {
    boolean onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);
}
