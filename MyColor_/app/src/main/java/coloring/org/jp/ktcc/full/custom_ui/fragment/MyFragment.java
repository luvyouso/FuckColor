package coloring.org.jp.ktcc.full.custom_ui.fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import coloring.org.jp.ktcc.full.R;
import coloring.org.jp.ktcc.full.app.activity.MainActivity;
import coloring.org.jp.ktcc.full.custom_ui.fragment.listener.ReceiveFromActivityListener;
import coloring.org.jp.ktcc.full.custom_ui.fragment.listener.SendToActivityListener;

public class MyFragment extends Fragment implements ReceiveFromActivityListener {
    public int REQUEST_SEND;

    private SendToActivityListener mListener;
    public SendToActivityListener getListener() {
        return mListener;
    }
    @Override
    public void onReceivePhoto(Uri[] results){}

    @Override
    public void onReceivePhoto(Bitmap bitmap, int requestSent){

    }

    @Override
    public void onReceiveBack( int requestSent) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SendToActivityListener) {
            mListener = (SendToActivityListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
    }
    public void showDialogProgress(){
        //TODO: call to activity show dialog progress.
        MainActivity activity = (MainActivity) getActivity();
        if(activity!=null)
            activity.showDialogProgress();
    }

    public void hideDialogProgress(){
        //TODO: call to activity show dialog progress.
        MainActivity activity = (MainActivity) getActivity();
        if(activity!=null)
            activity.hideDialogProgress();
    }
    public void pushFragment(Bundle bundle, MyFragment myFragment, boolean isAnimation){
        if(bundle!= null){
            myFragment.setArguments(bundle);
        }
        FragmentTransaction tran = getFragmentManager().beginTransaction();
        if(isAnimation) {
            tran.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        }
        tran.add(R.id.content_main, myFragment);
        tran.addToBackStack(null);
        tran.commit();

    }
    public void popFragment(){
        if(getFragmentManager().getBackStackEntryCount()>0){
            getFragmentManager().popBackStack();
        }


    }


}
