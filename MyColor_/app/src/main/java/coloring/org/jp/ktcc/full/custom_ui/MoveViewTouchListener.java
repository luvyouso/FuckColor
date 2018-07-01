package coloring.org.jp.ktcc.full.custom_ui;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
/**
 * Created by anh.trinh on 11/27/2017.
 */
public class MoveViewTouchListener
        implements View.OnTouchListener
{
    private GestureDetector mGestureDetector;
    private View mView;
    private MotionEvent event;

    public interface MoveViewListener {
        void onViewChange(float x, float y, MotionEvent event);
        void onDoneChange(float x, float y);
    }

    MoveViewListener mMoveViewListener;

    public void setMoveViewListener(MoveViewListener listener) {
        this.mMoveViewListener = listener;
    }
    public MoveViewTouchListener(View view, MoveViewListener moveViewListener)
    {
        mGestureDetector = new GestureDetector(view.getContext(), mGestureListener);
        mView = view;
        this.mMoveViewListener = moveViewListener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
       // Log.e("MotionEvent", event.getAction()+"");
        this.event = event;
        if(mMoveViewListener!= null && event.getAction() == 1){
            mMoveViewListener.onDoneChange(mView.getX(), mView.getY());
            mMoveViewListener.onViewChange(mView.getX(),mView.getY(),event);
        }
        return mGestureDetector.onTouchEvent(event);
    }

    private GestureDetector.OnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener()
    {
        private float mMotionDownX, mMotionDownY;

        @Override
        public boolean onDown(MotionEvent e)
        {
         //   Log.e("MotionEvent e ", e.getAction()+"");
            mMotionDownX = e.getRawX() - mView.getTranslationX();
            mMotionDownY = e.getRawY() - mView.getTranslationY();
          /*  mView.setTranslationX(e.getRawX() + mView.getWidth());
            mView.setTranslationY(e.getRawY() +mView.getHeight());*/
            if(mMoveViewListener!= null){
                mMoveViewListener.onViewChange(mView.getX(),mView.getY(),event);
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        {
            mView.setTranslationX(e2.getRawX() - mMotionDownX);
            mView.setTranslationY(e2.getRawY() - mMotionDownY);
            if(mMoveViewListener!= null){
                mMoveViewListener.onViewChange(mView.getX(),mView.getY(),event);
            }
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
         //   Log.e("MotionEvent e3 ", e.getAction()+"");
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return super.onDoubleTap(e);
        }
    };
}