package com.sudrives.sudrives.utils.listdialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.sudrives.sudrives.R;

import java.util.ArrayList;


/**
 * Created by Nikhat on 16/10/2018.
 */
public class DialogListFragment extends DialogFragment implements dialogItemSelectedInterface {

    private final String TAG = DialogListFragment.class.getSimpleName();
    static DialogListFragment mDialogListFragment;
    ArrayList<DialogListModel> mOptArrList;
    AlertDialog.Builder builder;
    View rootView;
    Button mOkBtn, mCancelBtn;
    public RecyclerView mRecyclerView;
    private TextView mTitleTv;
    private DialogListAdapter mOptionAdapter;
  //  private DialogListCameraAdapter mOptionCaleraAdapter;
    private String mTitleStr = "";
    RecyclerView.LayoutManager layoutManager;
    private RelativeLayout mDialogRl;

    /*
    *
    * Set options array list to show list options
    * 0 in not any icon in list and pass id in arraylist if any icon show inside list items
    * */
    public static DialogListFragment newInstance(String title, ArrayList<DialogListModel> arrList) {
        mDialogListFragment = new DialogListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        bundle.putParcelableArrayList("items_list", arrList);
        mDialogListFragment.setArguments(bundle);
        return mDialogListFragment;
    }


    public OnDialogClickedListener callback = null;

    public void setOnDialogClickedListener(OnDialogClickedListener l) {
        callback = l;
    }

    @Override
    public void dialogItem(int itemPos) {
        callback.selectedDialogItem(itemPos);
    }

    public interface OnDialogClickedListener {
        public void onDialogYes();

        public void onDialogNo();

        public void selectedDialogItem(int dialogPos);
    }


    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_list_view, container, false);
        return view;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();

        rootView = inflater.inflate(R.layout.dialog_list_view, null, false);
        builder = new AlertDialog.Builder(getActivity(), R.style.AppDialogTheme);
        builder.setView(rootView);
//        builder.setTitle(getArguments().getString("title"));

        return builder.create();
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mOptArrList = new ArrayList<>();
        mOkBtn = (Button) rootView.findViewById(R.id.ok_btn);
        mCancelBtn = (Button) rootView.findViewById(R.id.cancel_btn);
        mTitleTv = (TextView) rootView.findViewById(R.id.title_tv);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.options_rv);
        mDialogRl = (RelativeLayout) rootView.findViewById(R.id.dialog_rl);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);


      //  FontLoader.setOpenSansSemiBoldTypeface(mTitleTv, mOkBtn, mCancelBtn);
        //  FontLoader.setLatoBoldTypeface(mTitleTv, mOkBtn, mCancelBtn);




        mTitleStr = getArguments().getString("title");
        if (getArguments().getParcelableArrayList("items_list") != null) {

                mOptArrList = getArguments().getParcelableArrayList("items_list");

        }
        int size = mOptArrList.size();

            mOptionAdapter = new DialogListAdapter(getActivity(), this);
            mRecyclerView.setAdapter(mOptionAdapter);

            mOptionAdapter.setList(mOptArrList);



//        Log.v("size", "mOptArrList.size(): " + mOptArrList.size());
        int density = getResources().getDisplayMetrics().densityDpi;
        int height = 200;
        switch (density) {
            case DisplayMetrics.DENSITY_LOW:
             //   Log.v("ldpi", "ldpi " + (getActivity().getResources().getDimension(R.dimen.list_row_height)));
                height = 400;
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
               // Log.v("ldpi", "mdpi " + (getActivity().getResources().getDimension(R.dimen.list_row_height)));
                height = 400;
                break;
            case DisplayMetrics.DENSITY_HIGH:
             //   Log.v("ldpi", "hdpi " + (getActivity().getResources().getDimension(R.dimen.list_row_height)));
                height = 350;
                break;
            case DisplayMetrics.DENSITY_XHIGH:
               // Log.v("ldpi", "xhdpi " + (getActivity().getResources().getDimension(R.dimen.list_row_height)));
                height = 300;
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                height = 200;
               // Log.v("ldpi", "xxhdpi " + (getActivity().getResources().getDimension(R.dimen.list_row_height)));
                break;

            default:
              //  Log.v("ldpi", "default hdpi " + (getActivity().getResources().getDimension(R.dimen.list_row_height)));
                break;

        }


       // Log.v(TAG, "mOptArrList.size() :::::: " + mOptArrList.size());

        if (size <= 2) {
            ViewGroup.LayoutParams params = mRecyclerView.getLayoutParams();
//            params.height = 55* mOptArrList.size()-1;
//            params.height = GlobalUtil.pxToDp(getActivity() , 500)* mOptArrList.size();
            params.height = (int) (getActivity().getResources().getDimension(R.dimen.list_row_height) * (4));

            mRecyclerView.setLayoutParams(params);
        } else if (size <= 3) {
            ViewGroup.LayoutParams params = mRecyclerView.getLayoutParams();
//            params.height = 55* mOptArrList.size()-1;
//            params.height = GlobalUtil.pxToDp(getActivity() , 500)* mOptArrList.size();
            params.height = (int) (getActivity().getResources().getDimension(R.dimen.list_row_height) * (5));

            mRecyclerView.setLayoutParams(params);
        } else if (size < 6) {
            ViewGroup.LayoutParams params = mRecyclerView.getLayoutParams();
//            params.height = 55* mOptArrList.size()-1;
//            params.height = GlobalUtil.pxToDp(getActivity() , 500)* mOptArrList.size();
            params.height = (int) (getActivity().getResources().getDimension(R.dimen.list_row_height) * (7));

            mRecyclerView.setLayoutParams(params);

        } else {
            ViewGroup.LayoutParams params = mRecyclerView.getLayoutParams();
            params.height = (int) (getActivity().getResources().getDimension(R.dimen.list_row_height) * 10);
            mRecyclerView.setLayoutParams(params);
//            Log.v("height", "params.height: " + params.height + "    dp2222:::: " +
//                    getActivity().getResources().getDimension(R.dimen.list_row_height));
        }

        if (mTitleStr.isEmpty()) {
            mTitleTv.setVisibility(View.GONE);
        } else {
            if (mTitleStr.equalsIgnoreCase("camera")) {
                mTitleTv.setVisibility(View.GONE);
            } else {
                mTitleTv.setVisibility(View.VISIBLE);
                mTitleTv.setText(mTitleStr);
            }
        }

        mOkBtn.setVisibility(View.GONE);

        mOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onDialogYes();
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onDialogNo();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


}
