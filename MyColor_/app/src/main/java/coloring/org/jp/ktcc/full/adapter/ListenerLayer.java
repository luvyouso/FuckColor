package coloring.org.jp.ktcc.full.adapter;

/**
 * Created by nguyen on 11/17/2017.
 */
public interface ListenerLayer {
    void onMove(int fromPosition, int toPosition);
    void onDeleted(int position);
    void onClick(int position);
    void onDoubleClick(int position);
}
