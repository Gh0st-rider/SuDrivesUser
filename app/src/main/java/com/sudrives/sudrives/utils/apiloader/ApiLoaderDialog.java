package com.sudrives.sudrives.utils.apiloader;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sudrives.sudrives.R;
import com.wang.avi.AVLoadingIndicatorView;


/**
 * Created by hemanth on 11/28/2016.
 */

public class ApiLoaderDialog extends DialogFragment {

    static ApiLoaderDialog mApiLoaderDialog;
    AVLoadingIndicatorView mprogressBar;
    private View rootView;

    public static ApiLoaderDialog newInstance() {
        mApiLoaderDialog = new ApiLoaderDialog();
        return mApiLoaderDialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.circular_android_progress_bar, null, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controlInitialization(rootView);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar);
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    private void controlInitialization(View view) {
        mprogressBar=(AVLoadingIndicatorView)view.findViewById(R.id.material_design_linear_spin_fade_loader);
    }

}
