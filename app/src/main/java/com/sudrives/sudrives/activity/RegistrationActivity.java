package com.sudrives.sudrives.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;

import androidx.databinding.DataBindingUtil;

import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sudrives.sudrives.R;

import com.sudrives.sudrives.adapter.CityAdapter;
import com.sudrives.sudrives.adapter.StateAdapter;
import com.sudrives.sudrives.databinding.ActivityRegisterBinding;


import com.sudrives.sudrives.model.CityModel;
import com.sudrives.sudrives.model.RegisterModel;
import com.sudrives.sudrives.model.StatesModel;
import com.sudrives.sudrives.utils.AppDialogs;
import com.sudrives.sudrives.utils.BaseActivity;
import com.sudrives.sudrives.utils.Config;
import com.sudrives.sudrives.utils.ErrorLayout;
import com.sudrives.sudrives.utils.FontLoader;
import com.sudrives.sudrives.utils.GlobalUtil;
import com.sudrives.sudrives.utils.KeyboardUtil;
import com.sudrives.sudrives.utils.SessionPref;

import com.sudrives.sudrives.utils.server.BaseModel;
import com.sudrives.sudrives.utils.server.BaseRequest;
import com.sudrives.sudrives.utils.server.CallBackResponse;
import com.sudrives.sudrives.utils.server.JsonElementUtil;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class RegistrationActivity extends BaseActivity {
    private BaseRequest mBaseRequest;
    private final String TAG = LoginActivity.class.getSimpleName();
    private ActivityRegisterBinding mBinding;
    private Context mContext;
    private String mMobileStr = "", mGenderStr = "", mFirstName = "", mLastName = "", mEmail = "", mOTP = "", mPromocode = "", mUserId = "", title = "";
    private ErrorLayout mErrorLayout;
    private SessionPref mSessionPref;
    private BaseModel mBaseModel;
    private Intent intent;
    private RegisterModel mRegisterModel;
    private int layoutPopup;
    private String mSelectedLang = "",strStateId="",strCityId="";
    private TextView spinnerText;
    private int selPosition = 0;
    private ArrayList<StatesModel.Result> stateList = new ArrayList<>();
    private ArrayList<CityModel.Result> cityList = new ArrayList<>();
    private double lat = 0.0, longi = 0.0;

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FacebookSdk.sdkInitialize(getApplicationContext());
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_register);
        GlobalUtil.setStatusBarColor(RegistrationActivity.this, getResources().getColor(R.color.colorPrimaryDark));
        getData();
        getControls();
        //  fcmToken = FirebaseInstanceId.getInstance().getToken();

        KeyboardUtil.setupUI(mBinding.rlMain, this);




    }

    private void getData() {

        lat = getIntent().getDoubleExtra("lat", 0.0);
        longi = getIntent().getDoubleExtra("long", 0.0);

    }

    /*
     * initialize all controls
     * */
    private void getControls() {
        mContext = RegistrationActivity.this;
        mSessionPref = new SessionPref(mContext);
        mErrorLayout = new ErrorLayout(this, findViewById(R.id.error_layout));
        if (checkConnection()) {

            getStatesApi();
            cityList.add(0, new CityModel.Result("0", "0", getResources().getString(R.string.select_city), "0"));
            CityAdapter stateAdapter = new CityAdapter(mContext, cityList);
            mBinding.spinnerCity.setAdapter(stateAdapter);
            mBinding.spinnerCity.setSelection(0);

        } else {
            mErrorLayout.showAlert(getAppString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
        }
        mBinding.btnRegister.setOnClickListener(this::registerBtnClick);
      //  mBinding.clickToReadPolicy.setOnClickListener(this::policyClick);
        mBinding.tvChange.setOnClickListener(this::changeTextClick);

        mBinding.tvHavePromo.setOnClickListener(this::HaveAPromoClick);
        mBinding.tvByLoggingInYouAreAgreeingToOur.setTypeface(null, Typeface.NORMAL);

        /*
        *select state
       */
        mBinding.spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint


                } else {
                    if (checkConnection()) {
                        strStateId=stateList.get(position).id;
                        getCityApi(strStateId);

                    } else {
                       // mErrorLayout.showAlert(getAppString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
                        Toast.makeText(mContext, getAppString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        /*
        *select city
       */
        mBinding.spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // Disable the first item from Spinner
                    // First item will be use for hint


                } else {

                    strCityId= cityList.get(position).id;


                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        intent = getIntent();
        mMobileStr = intent.getStringExtra("mobile");
        mBinding.tvMobileNo.setText(mMobileStr);

        //  setLocal("en");

        try {
            if (SessionPref.getDataFromPrefLang(mContext, SessionPref.LANGUAGE) != null) {
                String localeString = SessionPref.getDataFromPrefLang(mContext, SessionPref.LANGUAGE);
                if (localeString.equals("hi")) {
                    setLocal(localeString);
                    mSelectedLang = getAppString(R.string.Hindi);
                    Config.SELECTED_LANG = getAppString(R.string.hindi);


                } else {
                    setLocal(localeString);

                    mSelectedLang = getAppString(R.string.English);
                    Config.SELECTED_LANG = getAppString(R.string.english);

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


      //  mBinding.tvByLoggingInYouAreAgreeingToOur.setLinkTextColor(getResources().getColor(R.color.terms_privacy_clr)); // default link color for clickable span, we can also set it in xml by android:textColorLink=""

        makeLinks(mBinding.tvByLoggingInYouAreAgreeingToOur, new String[]{
                getString(R.string.terms_of_services), getString(R.string.privacy_policy)
        }, new ClickableSpan[]{
                normalLinkClickSpan, noUnderLineClickSpan
        });


        mBinding.radiogroup.clearCheck();
        mBinding.rbMale.setChecked(true);
        mGenderStr = getAppString(R.string.male);
        mBinding.radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);

                if (checkedId == R.id.rb_male) {

                    FontLoader.setHelBold(mBinding.rbMale);
                    FontLoader.setHelRegular(mBinding.rbFemale, mBinding.rbOther);
                    mGenderStr = rb.getText().toString();

                } else if (checkedId == R.id.rb_female) {
                    FontLoader.setHelBold(mBinding.rbFemale);
                    FontLoader.setHelRegular(mBinding.rbMale, mBinding.rbOther);
                    mGenderStr = rb.getText().toString();
                } else if (checkedId == R.id.rb_other) {
                    FontLoader.setHelBold(mBinding.rbOther);
                    FontLoader.setHelRegular(mBinding.rbMale, mBinding.rbFemale);
                    mGenderStr = rb.getText().toString();
                }
            }
        });


        FontLoader.setHelRegular(mBinding.tvByLoggingInYouAreAgreeingToOur, mBinding.rbFemale, mBinding.rbOther, mBinding.tvChange, mBinding.tvHavePromo, mBinding.tvMobileNo, mBinding.etFirstName, mBinding.etPromocode);
        FontLoader.setHelBold(mBinding.btnRegister, mBinding.rbMale);


    }

    private void makeLinks(TextView textView, String[] links, ClickableSpan[] clickableSpans) {

        try {
            SpannableString spannableString = new SpannableString(textView.getText());
            if (spannableString != null) {
                if (spannableString.length() > 0) {
                    for (int i = 0; i < links.length; i++) {
                        ClickableSpan clickableSpan = clickableSpans[i];
                        String link = links[i];

                        int startIndexOfLink = textView.getText().toString().indexOf(link);
                        spannableString.setSpan(clickableSpan, startIndexOfLink,
                                startIndexOfLink + link.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }
            textView.setHighlightColor(Color.TRANSPARENT); // prevent TextView change background when highlight
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setText(spannableString, TextView.BufferType.SPANNABLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setLocal(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();

        if (Build.VERSION.SDK_INT >= 17) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }
        // config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        SessionPref.saveDataIntoSharedPrefLang(getApplicationContext(), SessionPref.LANGUAGE, lang);
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        setTextOnViewAccordingLanguage(lang);
    }

    private void setTextOnViewAccordingLanguage(String lang) {
        mBinding.tvChange.setText(getAppString(R.string.change));
        if (mFirstName.isEmpty()) {
            mBinding.etFirstName.setHint(getAppString(R.string.full_name));
        }

        mBinding.etPromocode.setHint(getAppString(R.string.referral_code));
        mBinding.tvHavePromo.setText(getAppString(R.string.have_a_referral_code));
        mBinding.tvByLoggingInYouAreAgreeingToOur.setText(getAppString(R.string.by_logging_in_you_are_agreed_to_our));
        mBinding.btnRegister.setText(getAppString(R.string.register));
        makeLinks(mBinding.tvByLoggingInYouAreAgreeingToOur, new String[]{
                getString(R.string.terms_of_services), getString(R.string.privacy_policy)
        }, new ClickableSpan[]{
                normalLinkClickSpan, noUnderLineClickSpan
        });
    }

    ClickableSpan noUnderLineClickSpan = new ClickableSpan() {
        @Override
        public void onClick(View view) {
            Intent i=new Intent(mContext,WebviewActivity.class);
            i.putExtra("tool_title","Policy");
            i.putExtra("value","https://sudrives.com/privacyforuser.html");
            startActivity(i);
            //privacyClick(view);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
            ds.setColor(getResources().getColor(R.color.terms_privacy_clr)); // specific color for this link
        }
    };

    ClickableSpan normalLinkClickSpan = new ClickableSpan() {
        @Override
        public void onClick(View view) {
            //Toast.makeText(getApplicationContext(), "Normal Link", Toast.LENGTH_SHORT).show();
           // termsAndConditionClick(view);
            Intent i=new Intent(mContext,WebviewActivity.class);
            i.putExtra("tool_title","Policy");
            i.putExtra("value","https://sudrives.com/terms&condition.html");
            startActivity(i);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
            ds.setColor(getResources().getColor(R.color.terms_privacy_clr)); // specific color for this link
        }
    };

    private void privacyClick(View view) {
        if (checkConnection()) {
            conditionApi(Config.PRIVACYPOLICY, getString(R.string.Privacy_policy));
        } else {
            mErrorLayout.showAlert(getAppString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
        }
    }

    //terms and condition click
    private void termsAndConditionClick(View view) {
        if (checkConnection()) {
            conditionApi(Config.TOF, getString(R.string.Terms_of_services));
        } else {
            mErrorLayout.showAlert(getAppString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
        }
    }

    //change text click
    private void changeTextClick(View view) {
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    //Promo text click
    private void HaveAPromoClick(View view) {
        mBinding.tvHavePromo.setVisibility(View.GONE);
        mBinding.llpromocode.setVisibility(View.VISIBLE);

    }

    //register button click
    private void registerBtnClick(View view) {
        getValues();
        if (isValidAll()) {
            mBinding.btnRegister.setEnabled(false);
            if (checkConnection()) {
                RegisterAPI();

            } else {
                mBinding.btnRegister.setEnabled(true);
                dialogClickForAlert(false, getAppString(R.string.no_internet_connection));
            }
        }
    }

    /*
     * get all values
     * */
    private void getValues() {
        mMobileStr = getTvString(mBinding.tvMobileNo);
        mFirstName = getEtString(mBinding.etFirstName);
        mEmail = getEtString(mBinding.etEmailId);
        mPromocode = getTvString(mBinding.etPromocode);
    }


    /*
     * check all validations
     * */

    private boolean isValidAll() {
        if (TextUtils.isEmpty(mFirstName)) {
            mErrorLayout.showAlert(getAppString(R.string.Please_enter_full_name), ErrorLayout.MsgType.Error, true);
            return false;
        } else if (TextUtils.isEmpty(mEmail)) {
            mErrorLayout.showAlert(getAppString(R.string.Please_enter_email_id), ErrorLayout.MsgType.Error, true);
            return false;
        }else if (TextUtils.isEmpty(strStateId)) {
            mErrorLayout.showAlert(getAppString(R.string.Please_select_state), ErrorLayout.MsgType.Error, true);
            return false;
        } else if (TextUtils.isEmpty(strCityId)) {
            mErrorLayout.showAlert(getAppString(R.string.Please_select_city), ErrorLayout.MsgType.Error, true);
            return false;
        } else if (TextUtils.isEmpty(mGenderStr)) {
            mErrorLayout.showAlert(getAppString(R.string.Please_select_gender), ErrorLayout.MsgType.Error, true);
            return false;
        } else if (!mBinding.checkboxTermsNPrivacy.isChecked()) {
            mErrorLayout.showAlert(getAppString(R.string.please_accept_terms_n_comditions), ErrorLayout.MsgType.Error, true);
            return false;
        }
        return true;
    }

    /**
     * Error dialog for the API error messages
     *
     * @param msg :- Message which we need to show.
     */
    public void dialogClickForAlert(boolean isSuccess, String msg) {

        int drawable;

        if (isSuccess) {
            drawable = R.drawable.success_24dp;
            title = getString(R.string.confirmation);
            layoutPopup = R.layout.success_dialog;
        } else {
            drawable = R.drawable.failed_24dp;
            title = "";
            layoutPopup = R.layout.failure_dialog;
        }

        AppDialogs.singleButtonVersionDialog(RegistrationActivity.this, layoutPopup, title, drawable, msg,
                getString(R.string.ok),
                new AppDialogs.SingleButoonCallback() {
                    @Override
                    public void singleButtonSuccess(String from) {
                        if (isSuccess) {
                            Intent intent = new Intent(mContext, VerificationActivity.class);
                            intent.putExtra("registerModel", mRegisterModel);
                            intent.putExtra("loginType", "register");
                            intent.putExtra("userid", mUserId);
                            intent.putExtra("lat", lat);
                            intent.putExtra("long", longi);
                            startActivity(intent);
                            finish();
                        } else {
                            finish();
                        }

                    }
                }, true);


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    /***************    Implement API    *************************/
    private void RegisterAPI() {
        mBaseRequest = new BaseRequest(mContext, true);
        JsonObject jsonObj = JsonElementUtil.getJsonObject("mobile", mMobileStr, "user_role", Config.TYPE_USER_VAl, "promocode", mPromocode);
        mBaseRequest.setBaseRequest(jsonObj, "api/mobile_verification", "", Config.TIMEZONE, Config.SELECTED_LANG, Config.DEVICE_TOKEN, Config.DEVICE_TYPE, new CallBackResponse() {
            @Override
            public void getResponse(Object response) {
                mBinding.btnRegister.setEnabled(true);
                mBaseModel = new BaseModel(mContext);
                if (mBaseModel.isParse(response.toString())) {
                    JSONObject jsonObj = null;
                    try {
                        jsonObj = new JSONObject(response.toString());
                        if (jsonObj.getString("userid") != null) {
                            mRegisterModel = new RegisterModel();
                            mRegisterModel.setFirstname(mFirstName);
                            mRegisterModel.setLastname(mLastName);
                            mRegisterModel.setMobileNumber(mMobileStr);
                            mRegisterModel.setEmail(mEmail);
                            mRegisterModel.setGender(mGenderStr);
                            mRegisterModel.setCity(strCityId);
                            mRegisterModel.setState(strStateId);
                            mUserId = jsonObj.getString("userid").toString();
                            dialogClickForAlert(true, mBaseModel.Message);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    dialogClickForAlert(false, mBaseModel.Message.toString());
                }
            }

            @Override
            public void getError(Object response, String error) {
                mBinding.btnRegister.setEnabled(true);

                if (!checkConnection()) {
                    dialogClickForAlert(false, getAppString(R.string.something_went_wrong));
                } else {
                    dialogClickForAlert(false, getAppString(R.string.something_went_wrong));
                }
            }
        }, false);


    }


    private void conditionApi(String type, String title) {
        mBaseRequest = new BaseRequest(mContext, true);
        JsonObject jsonObj = JsonElementUtil.getJsonObject("page_name", type);
        mBaseRequest.setBaseRequest(jsonObj, "CommonController/pages", mSessionPref.user_userid, Config.TIMEZONE, Config.SELECTED_LANG, Config.DEVICE_TOKEN, Config.DEVICE_TYPE, new CallBackResponse() {
            @Override
            public void getResponse(Object response) {
                mBaseModel = new BaseModel(mContext);
                if (mBaseModel.isParse(response.toString())) {
                    String msg = null;
                    try {
                        msg = mBaseModel.getResultObject().getString("discription").toString();

                        AppDialogs.singleButtonVersionDialog(mContext, R.layout.terms_condition_dialog, title, R.drawable.close_24dp, msg,
                                getString(R.string.ok),
                                new AppDialogs.SingleButoonCallback() {
                                    @Override
                                    public void singleButtonSuccess(String from) {
                                    }
                                }, true);


                    } catch (JSONException e) {
                        e.printStackTrace();
                        mErrorLayout.showAlert(getAppString(R.string.something_went_wrong), ErrorLayout.MsgType.Error, true);
                    }


                } else {
                    mErrorLayout.showAlert(mBaseModel.Message, ErrorLayout.MsgType.Error, true);
                }
            }

            @Override
            public void getError(Object response, String error) {
                if (!checkConnection()) {
                    mErrorLayout.showAlert(error.toString(), ErrorLayout.MsgType.Error, false);
                } else {
                    mErrorLayout.showAlert(error.toString(), ErrorLayout.MsgType.Error, true);
                }

            }
        }, false);


    }

    private void getStatesApi() {
        mBaseRequest = new BaseRequest(mContext, true);
        mBaseRequest.setBaseRequest(new JsonObject(), "Api/getStates", mSessionPref.user_userid, Config.TIMEZONE, Config.SELECTED_LANG, Config.DEVICE_TOKEN, Config.DEVICE_TYPE, new CallBackResponse() {
            @Override
            public void getResponse(Object response) {

                mBaseModel = new BaseModel(mContext);
                if (mBaseModel.isParse(response.toString())) {

                    try {

                        StatesModel mStatesModel = new Gson().fromJson(response.toString(), StatesModel.class);
                        stateList.clear();
                        stateList = mStatesModel.result;
                        stateList.add(0, new StatesModel.Result("0", getResources().getString(R.string.select_state), "0"));
                        StateAdapter stateAdapter = new StateAdapter(mContext, stateList);
                        mBinding.spinnerState.setAdapter(stateAdapter);
                        mBinding.spinnerState.setSelection(0);


                    } catch (Exception e) {
                        e.printStackTrace();
                        mErrorLayout.showAlert(getAppString(R.string.something_went_wrong), ErrorLayout.MsgType.Error, true);
                    }


                } else {
                    mErrorLayout.showAlert(mBaseModel.Message, ErrorLayout.MsgType.Error, true);
                }
            }

            @Override
            public void getError(Object response, String error) {
                if (!checkConnection()) {
                    mErrorLayout.showAlert(error.toString(), ErrorLayout.MsgType.Error, false);
                } else {
                    mErrorLayout.showAlert(error.toString(), ErrorLayout.MsgType.Error, true);
                }

            }
        }, false);


    }

    private void getCityApi(String state_id) {
        mBaseRequest = new BaseRequest(mContext, true);
        JsonObject jsonObj = JsonElementUtil.getJsonObject("state_id", state_id);
        mBaseRequest.setBaseRequest(jsonObj, "Api/getCities", mSessionPref.user_userid, Config.TIMEZONE, Config.SELECTED_LANG, Config.DEVICE_TOKEN, Config.DEVICE_TYPE, new CallBackResponse() {
            @Override
            public void getResponse(Object response) {

                mBaseModel = new BaseModel(mContext);
                if (mBaseModel.isParse(response.toString())) {

                    try {

                        CityModel cityModel = new Gson().fromJson(response.toString(), CityModel.class);
                        cityList.clear();
                        cityList = cityModel.result;
                        cityList.add(0, new CityModel.Result("0", "0", getResources().getString(R.string.select_city), "0"));
                        CityAdapter stateAdapter = new CityAdapter(mContext, cityList);
                        mBinding.spinnerCity.setAdapter(stateAdapter);
                        mBinding.spinnerCity.setSelection(0);


                    } catch (Exception e) {
                        e.printStackTrace();
                        mErrorLayout.showAlert(getAppString(R.string.something_went_wrong), ErrorLayout.MsgType.Error, true);
                    }


                } else {
                    mErrorLayout.showAlert(mBaseModel.Message, ErrorLayout.MsgType.Error, true);
                }
            }

            @Override
            public void getError(Object response, String error) {
                if (!checkConnection()) {
                    mErrorLayout.showAlert(error.toString(), ErrorLayout.MsgType.Error, false);
                } else {
                    mErrorLayout.showAlert(error.toString(), ErrorLayout.MsgType.Error, true);
                }

            }
        }, false);

    }
}
