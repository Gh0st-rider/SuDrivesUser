package com.sudrives.sudrives.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.sudrives.sudrives.R;
import com.sudrives.sudrives.databinding.ActivitySelectLanguageBinding;
import com.sudrives.sudrives.utils.BaseActivity;
import com.sudrives.sudrives.utils.Config;
import com.sudrives.sudrives.utils.FontLoader;
import com.sudrives.sudrives.utils.GlobalUtil;
import com.sudrives.sudrives.utils.SessionPref;

public class SelectLanguage extends BaseActivity {
    private ActivitySelectLanguageBinding mBinding;
    private Context mContext;
    private SessionPref mSessionPref;
    private String strLang = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_select_language);
        getControls();
        GlobalUtil.setStatusBarColor(SelectLanguage.this, getResources().getColor(R.color.colorPrimaryDark));


    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    private void getControls() {
        mContext = SelectLanguage.this;
        mSessionPref = new SessionPref(mContext);


        Config.SELECTED_LANG = getResources().getString(R.string.english);
        mBinding.llEnglish.setOnClickListener(this::onEnglishClick);
        mBinding.llHindi.setOnClickListener(this::onHindiClick);
        FontLoader.setHelRegular(
                mBinding.tvDes, mBinding.tvHindi, mBinding.tvHindi2);
        FontLoader.setHelBold(mBinding.tvTitle, mBinding.btnContinue, mBinding.tvEnglish);
        mBinding.ivEnglish.setVisibility(View.VISIBLE);
        mBinding.ivHindi.setVisibility(View.GONE);
        SessionPref.saveDataIntoSharedPrefLang(getApplicationContext(), SessionPref.LANGUAGE, "en");
        SessionPref.saveDataIntoSharedPrefLang(getApplicationContext(), SessionPref.IS_LANGUAGE_SELECTED, "true");


    }


    private void onHindiClick(View view) {
        SessionPref.saveDataIntoSharedPrefLang(getApplicationContext(), SessionPref.IS_LANGUAGE_SELECTED, "true");
        FontLoader.setHelBold(mBinding.tvHindi);
        FontLoader.setHelRegular(mBinding.tvEnglish);

        Config.SELECTED_LANG = getResources().getString(R.string.hindi);
        SessionPref.saveDataIntoSharedPrefLang(getApplicationContext(), SessionPref.LANGUAGE, "hi");
        mBinding.ivHindi.setVisibility(View.VISIBLE);
        mBinding.ivEnglish.setVisibility(View.GONE);

    }

    private void onEnglishClick(View view) {
        SessionPref.saveDataIntoSharedPrefLang(getApplicationContext(), SessionPref.IS_LANGUAGE_SELECTED, "true");
        FontLoader.setHelBold(mBinding.tvEnglish);
        FontLoader.setHelRegular(mBinding.tvHindi);

        Config.SELECTED_LANG = getResources().getString(R.string.english);
        SessionPref.saveDataIntoSharedPrefLang(getApplicationContext(), SessionPref.LANGUAGE, "en");
        mBinding.ivEnglish.setVisibility(View.VISIBLE);
        mBinding.ivHindi.setVisibility(View.GONE);

    }

    public void openLogin(View view) {
        Intent intent = new Intent(mContext, LoginActivity.class).putExtra("lat", getIntent().getStringExtra("lat")).putExtra("long", getIntent().getStringExtra("long"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

}
