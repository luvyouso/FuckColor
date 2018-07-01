package coloring.org.jp.ktcc.full.app.activity.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import coloring.org.jp.ktcc.full.R;
import coloring.org.jp.ktcc.full.databinding.FragmentDialogInitPrintBinding;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DialogInitPrintFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DialogInitPrintFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DialogInitPrintFragment extends DialogFragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private FragmentDialogInitPrintBinding mDialogInitPrintBinding;

    public DialogInitPrintFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DialogInitPrintFragment.
     */
    public static DialogInitPrintFragment newInstance(String param1, String param2) {
        DialogInitPrintFragment fragment = new DialogInitPrintFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.dialog_init_layer);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().setCanceledOnTouchOutside(false);
        mDialogInitPrintBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dialog_init_print, container, false);
        return mDialogInitPrintBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //confirm
        mDialogInitPrintBinding.mButtonOk.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onFragmentInteraction(mDialogInitPrintBinding.mSpinnerOutputLayer.getSelectedItem().toString(),
                        mDialogInitPrintBinding.mSpinnerOption.getSelectedItem().toString());
            }
            dismiss();
        });
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mListener != null) {
            mListener.onFragmentInteractionDismiss();
        }
        super.onDismiss(dialog);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String layer, String option);

        void onFragmentInteractionDismiss();

    }
}
