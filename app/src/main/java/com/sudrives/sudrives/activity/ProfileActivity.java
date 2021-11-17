package com.sudrives.sudrives.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;


import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.compat.AutocompleteFilter;
import com.google.android.libraries.places.compat.Place;
import com.google.android.libraries.places.compat.ui.PlaceAutocomplete;
import com.sudrives.sudrives.R;


import com.sudrives.sudrives.databinding.ActivityProfileBinding;
import com.sudrives.sudrives.utils.AppDialogs;
import com.sudrives.sudrives.utils.BaseActivity;
import com.sudrives.sudrives.utils.Config;
import com.sudrives.sudrives.utils.ErrorLayout;
import com.sudrives.sudrives.utils.FontLoader;
import com.sudrives.sudrives.utils.GlobalUtil;

import com.sudrives.sudrives.utils.Image_Picker;
import com.sudrives.sudrives.utils.KeyboardUtil;
import com.sudrives.sudrives.utils.SessionPref;
import com.sudrives.sudrives.utils.ValidationUtil;
import com.sudrives.sudrives.utils.network_communication.AppConstants;
import com.sudrives.sudrives.utils.network_communication.NetworkConn;
import com.sudrives.sudrives.utils.server.BaseModel;
import com.sudrives.sudrives.utils.server.BaseRequest;
import com.sudrives.sudrives.utils.server.CallBackResponse;
import com.sudrives.sudrives.utils.server.JsonElementUtil;

import com.google.gson.JsonObject;
import com.theartofdev.edmodo.cropper.CropImage;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ProfileActivity extends BaseActivity implements NetworkConn.onRequestResponse {
    private ActivityProfileBinding mBinding;
    private Context mContext;
    private ErrorLayout mErrorLayout;
    private BaseRequest mBaseRequest;
    private BaseModel mBaseModel;
    private SessionPref mSessionPref;
    private String mRateStr = "", mRideId = "", driver_id = "";
    private final String TAG = ProfileActivity.class.getSimpleName();

    private Image_Picker image_picker;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private String mFnameStr = "", mLastStr = "", mAddressStr = "", mEmailStr = "", mUserid = "", mToken = "", mProfilePath = "", mMobileStr = "", title = "";

    private int layoutPopup;
    private static final int INITIAL_REQUEST = 222;
    private static final String[] INITIAL_PERMS = {

            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };


    public static final int REQUEST_CODE_HOME_ADDRESS = 1;
    public static final int REQUEST_CODE_WORK_ADDRESS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  GlobalUtil.setStatusBarGradiant(ProfileActivity.this);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
      //  GlobalUtil.setStatusBarColor(ProfileActivity.this, getResources().getColor(R.color.colorPrimaryDark));
        getControls();


    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    private void getControls() {
        mContext = ProfileActivity.this;
        image_picker = new Image_Picker(mContext);
        mErrorLayout = new ErrorLayout(this, findViewById(R.id.error_layout));
        mSessionPref = new SessionPref(mContext);
        mBinding.ivMyProfile.setEnabled(false);
        mBinding.tvProfileTitle.setText(getAppString(R.string.profile));
        mBinding.ivProfileLeft.setOnClickListener(this::backScreenClick);
        mBinding.btnProfileSave.setOnClickListener(this::callEditProfileApi);
        mBinding.ivCamera.setOnClickListener(this::pickImage);
        mBinding.ivMyProfile.setOnClickListener(this::pickImage);
        mBinding.ivProfileEdit.setOnClickListener(this::editableField);

        FontLoader.setHelRegular(mBinding.tvProfileTitle);


        mBinding.etHomeLocation.setOnClickListener(this::homeLocation);
        mBinding.etOfficeLocation.setOnClickListener(this::officeLocation);
        mBinding.etHomeLocation.setEnabled(false);
        mBinding.etOfficeLocation.setEnabled(false);



        if (!SessionPref.getDataFromPref(mContext, SessionPref.KEY_HOME_ADDRESS).isEmpty()) {
            mBinding.etHomeLocation.setText(SessionPref.getDataFromPref(mContext, SessionPref.KEY_HOME_ADDRESS));
        }

        if (!SessionPref.getDataFromPref(mContext, SessionPref.KEY_WORK_ADDRESS).isEmpty()) {
            mBinding.etOfficeLocation.setText(SessionPref.getDataFromPref(mContext, SessionPref.KEY_WORK_ADDRESS));
        }


        if (checkConnection()) {
            profileApi();

        } else {
            dialogClickForAlert(false, getResources().getString(R.string.no_internet_connection));
        }
    }

    private void editableField(View view) {
        mBinding.ivProfileEdit.setVisibility(View.INVISIBLE);
        mBinding.ivCamera.setVisibility(View.VISIBLE);

        if (mBinding.etMailaddress.getText().toString().equalsIgnoreCase(getAppString(R.string.not_available))) {

            mBinding.etMailaddress.setText("");
        }
        mBinding.etName.setFocusableInTouchMode(true);
        // mBinding.etLastName.setFocusableInTouchMode(true);
        mBinding.ivMyProfile.setEnabled(true);

        mBinding.etMailaddress.setHint(R.string.email_address);

        // mBinding.etAddress.setText("");
        mBinding.etMailaddress.setHintTextColor(getResources().getColor(R.color.colorGrayLight));

        mBinding.etMailaddress.setFocusableInTouchMode(true);
        mBinding.btnProfileSave.setVisibility(View.VISIBLE);
        mBinding.etName.setEnabled(true);

        mBinding.etName.setEnabled(true);
        mBinding.etMailaddress.setEnabled(true);
        mBinding.ivMyProfile.setEnabled(true);

        mBinding.etHomeLocation.setEnabled(true);
        mBinding.etOfficeLocation.setEnabled(true);

    }

    //pick image

    private void pickImage(View view) {

        startCheckPermissions();
    }

    //check permission
    void startCheckPermissions() {

        if (!canAccessExternalStorage()) {
            ActivityCompat.requestPermissions(this, INITIAL_PERMS, INITIAL_REQUEST);
        } else {
            pickPhoto();
        }
    }

    //image option
    private void pickPhoto() {
        mBinding.etName.setEnabled(false);
        mBinding.etMobileNo.setEnabled(false);
        mBinding.etMailaddress.setEnabled(false);
       final CharSequence[] opsChars = {mContext.getString(R.string.take_picture), mContext.getString(R.string.gallery)};
        new AlertDialog.Builder(new ContextThemeWrapper(mContext, R.style.DialogTheme))
                //.setTitle("Select")
                .setSingleChoiceItems(opsChars, 0, null)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        // Do something useful withe the position of the selected radio button

                        if (selectedPosition == 0) {
                            image_picker.openCameraApp();
                        } else if (selectedPosition == 1) {
                            image_picker.openGallery();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mBinding.etName.setEnabled(true);
                        mBinding.etMailaddress.setEnabled(true);

                    }
                })
                .show();
        //image_picker.imageOptions();
    }

    private boolean canAccessExternalStorage() {
        return (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE) && hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) && hasPermission(Manifest.permission.CAMERA));
    }

    private boolean hasPermission(String perm) {
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, perm));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case INITIAL_REQUEST: {

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    pickPhoto();
                }
            }
            break;

        }
    }

    private void callEditProfileApi(View view) {
        //register button click

        getValues();
        if (isValidAlls()) {
            if (checkConnection()) {
                //  updateProfileApi();

                networkConn.makeRequest(mContext,
                        networkConn.createFormDataRequest(ProfileActivity.this, AppConstants.PROFILE_UPDATE_URL,
                                getRequestForUpdateProfile()), AppConstants.EVENT_PROFILE_UPDATE,
                        this, mErrorLayout);
            } else {
                dialogClickForAlert(false, getAppString(R.string.no_internet_connection));
            }
        }

    }

    private void getValues() {
        mFnameStr = getEtString(mBinding.etName);
        mMobileStr = getEtString(mBinding.etMobileNo);
        mEmailStr = getEtString(mBinding.etMailaddress);

    }


    private boolean isValidAlls() {
        if (TextUtils.isEmpty(mFnameStr)) {
            mErrorLayout.showAlert(getAppString(R.string.Please_enter_full_name), ErrorLayout.MsgType.Error, true);
            return false;
        } else if (TextUtils.isEmpty(mMobileStr)) {
            mErrorLayout.showAlert(getAppString(R.string.Please_enter_mobile_num), ErrorLayout.MsgType.Error, true);
            return false;
        } else if (mMobileStr.length() < 10) {
            mErrorLayout.showAlert(getAppString(R.string.Please_enter_valid_mobile_num), ErrorLayout.MsgType.Error, true);
            return false;
        }
        //if (!TextUtils.isEmpty(mEmailStr)) {
            if (TextUtils.isEmpty(mEmailStr)) {
                mErrorLayout.showAlert(getAppString(R.string.Please_enter_email_id), ErrorLayout.MsgType.Error, true);
                return false;
            } else if (!ValidationUtil.isValidEmail(mEmailStr)) {
                mErrorLayout.showAlert(getAppString(R.string.Please_enter_valid_email_id), ErrorLayout.MsgType.Error, true);
                return false;
          //  }
        }/*else if (TextUtils.isEmpty(mGstnumStr)) {
            mErrorLayout.showAlert(getAppString(R.string.Please_enter_gst_no), ErrorLayout.MsgType.Error, true);
            return false;
        }*/ /*else if (TextUtils.isEmpty(mAddressStr)) {
            mErrorLayout.showAlert(getAppString(R.string.Please_enter_address), ErrorLayout.MsgType.Error, true);
            return false;
        }*/
        return true;
    }


    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mBinding.etName.setEnabled(true);
        mBinding.etMailaddress.setEnabled(true);


        //  isTakePic = true;
        if (requestCode == Image_Picker.CAMERA_REQUEST) {
            // Log.e("dfgfdgfdg", "ffffffff: " + resultCode + "  :::: " + RESULT_OK + " :::::: " + RESULT_CANCELED);

            switch (resultCode) {
                case RESULT_OK:

                    image_picker.cropImage(this, Uri.fromFile(Image_Picker.IMAGE_PATH), 0);

                    break;

            }
        } else {

            if (requestCode == Image_Picker.GALLERY_REQUEST) {

                switch (resultCode) {
                    case RESULT_OK:
                        Uri selectedImageURI = data.getData();
                        image_picker.cropImage(this, selectedImageURI, 0);
                        break;


                }
            } else {
                if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (resultCode == RESULT_OK) {
                        Uri resultUri = result.getUri();
                        mProfilePath = image_picker.getRealPathFromURI(resultUri);
                        Glide.with(mContext).load(image_picker.getRealPathFromURI(resultUri)).placeholder(R.drawable.profile_placeholder).into(mBinding.ivMyProfile);


                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                        Exception error = result.getError();
                        //     Log.e("error", "profilePicError :::: " + error.getMessage());
                        error.printStackTrace();
                    }
                } else {

                    super.onActivityResult(requestCode, resultCode, data);
                }
            }
        }

        if (requestCode == REQUEST_CODE_HOME_ADDRESS) {
            if (resultCode == RESULT_OK) {

                Place place = PlaceAutocomplete.getPlace(mContext, data);
                //call location based filter

                LatLng latLong;
                latLong = place.getLatLng();
                String mToLat = String.valueOf(latLong.latitude);
                String mToLong = String.valueOf(latLong.longitude);
                String mToaddress = (String) place.getAddress();// GlobalUtil.getAddress(mContext, latLong.latitude, latLong.longitude);

                mBinding.etHomeLocation.setText(mToaddress);

                SessionPref.saveDataIntoSharedPref(mContext, SessionPref.KEY_HOME_ADDRESS, mToaddress);
                SessionPref.saveDataIntoSharedPref(mContext, SessionPref.KEY_HOME_LAT, mToLat);
                SessionPref.saveDataIntoSharedPref(mContext, SessionPref.KEY_HOME_LONG, mToLong);



            }


        }
        if (requestCode == REQUEST_CODE_WORK_ADDRESS) {
            if (resultCode == RESULT_OK) {

                Place place = PlaceAutocomplete.getPlace(mContext, data);
                //call location based filter

                LatLng latLong;
                latLong = place.getLatLng();
                String mToLat = String.valueOf(latLong.latitude);
                String mToLong = String.valueOf(latLong.longitude);
                String mToaddress =  (String) place.getAddress();

                mBinding.etOfficeLocation.setText(mToaddress);
                SessionPref.saveDataIntoSharedPref(mContext, SessionPref.KEY_WORK_ADDRESS, mToaddress);
                SessionPref.saveDataIntoSharedPref(mContext, SessionPref.KEY_WORK_LAT, mToLat);
                SessionPref.saveDataIntoSharedPref(mContext, SessionPref.KEY_WORK_LONG, mToLong);
            }


        }


    }

    /*

    /**
     * Navigation to back screen.
     *
     * @param view
     */
    private void backScreenClick(View view) {

        try {
            KeyboardUtil.hideKeyBoardDialog(mContext, mBinding.clTop);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
        // overridePendingTransition(R.anim.move_in_left,R.anim.move_out_left);
    }


    public void dialogClickForAlert(boolean isSuccess, String msg) {
        int drawable;

        if (isSuccess) {
            drawable = R.drawable.success_24dp;
            title = getString(R.string.confirmation) + "!";
            layoutPopup = R.layout.success_dialog;
        } else {
            drawable = R.drawable.failed_24dp;
            title = "";
            layoutPopup = R.layout.failure_dialog;
        }


        AppDialogs.singleButtonVersionDialog(ProfileActivity.this, layoutPopup, title, drawable, msg,
                "Ok",
                new AppDialogs.SingleButoonCallback() {
                    @Override
                    public void singleButtonSuccess(String from) {

                    }
                }, true);
    }


    /***************    Implement API    *************************/


    private void profileApi() {


        mBaseRequest = new BaseRequest(mContext, true);

        JsonObject jsonObj = JsonElementUtil.getJsonObject("userid", mSessionPref.user_userid);


        mBaseRequest.setBaseRequest(jsonObj, "api/profile_detail", mSessionPref.user_userid, Config.TIMEZONE, Config.SELECTED_LANG, Config.DEVICE_TOKEN, Config.DEVICE_TYPE, new CallBackResponse() {
            @Override
            public void getResponse(Object response) {

                mBaseModel = new BaseModel(mContext);
                if (mBaseModel.isParse(response.toString())) {

                    String unique_no="",pic = "", first_name = "", city_name = "",last_name = "", mobile = "", email = "", gst_number = "", address = "", state_name = "";
                      Log.e(TAG, "Resp  :::::::  " + response);
                    // Log.e(TAG, "mSessionPref :::::::  " + mBaseModel.getResultObject());
                    try {
                        pic = mBaseModel.getResultObject().getString("profile_img");
                        first_name = mBaseModel.getResultObject().getString("first_name");
                        last_name = mBaseModel.getResultObject().getString("last_name");
                        mobile = mBaseModel.getResultObject().getString("mobile");
                        email = mBaseModel.getResultObject().getString("email");
                        gst_number = mBaseModel.getResultObject().getString("gst_number");
                        address = mBaseModel.getResultObject().getString("address");
                        city_name = mBaseModel.getResultObject().getString("city_name");
                        state_name = mBaseModel.getResultObject().getString("state_name");
                        mBinding.uniqueId.setText("CRN Number- "+mBaseModel.getResultObject().getString("unique_no"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (!pic.equals("")) {
                        Glide.with(mContext)
                                .load(pic)
                                .placeholder(R.drawable.profile_placeholder)
                                .error(R.drawable.profile_placeholder)
                                .into(mBinding.ivMyProfile);
                    }
                    if (!first_name.equals("")) {
                        mBinding.etName.setText(first_name);
                    } else {
                        mBinding.etName.setText(getAppString(R.string.not_available));
                    }


                        mBinding.etState.setText(state_name);
                        mBinding.etCity.setText(city_name);

                    if (!mobile.equals("")) {
                        mBinding.etMobileNo.setText(mobile);
                    }
                    if (!email.equalsIgnoreCase("")) {
                        mBinding.etMailaddress.setText(email);

                    } else {
                        mBinding.etMailaddress.setHint(getAppString(R.string.not_available));
                        mBinding.etMailaddress.setHintTextColor(getResources().getColor(R.color.colorGrayLight));
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


    /**
     * get Request for  signup
     */
    private RequestBody getRequestForUpdateProfile() {
        //  Log.e("###########", RequestBody.create(NetworkConn.MEDIA_TYPE_PNG, new File(strImagepath)).toString());
       /* JsonElementUtil.getJsonObject("firstname", mFnameStr, "lastname", mLastStr, "email", mEmailStr, "gst_number", mGstnumStr,
                "address", mAddressStr, "userid", mSessionPref.user_userid, "token", mSessionPref.token, "profile_img", mProfilePath);*/
        if (mProfilePath.isEmpty()) {
            return new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("firstname", mFnameStr)
                    .addFormDataPart("lastname", mLastStr)
                    .addFormDataPart("email", mEmailStr)
                    //.addFormDataPart("gst_number", mGstnumStr)
                    .addFormDataPart("address", mAddressStr)
                    .addFormDataPart("userid", mSessionPref.user_userid)
                    .addFormDataPart("token", mSessionPref.token)

                    .build();
        } else {
            return new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("firstname", mFnameStr)
                    .addFormDataPart("lastname", mLastStr)
                    .addFormDataPart("email", mEmailStr)
                  //  .addFormDataPart("gst_number", mGstnumStr)
                    .addFormDataPart("address", mAddressStr)
                    .addFormDataPart("userid", mSessionPref.user_userid)
                    .addFormDataPart("token", mSessionPref.token)
                    .addFormDataPart("profile_img", "Profile.png", RequestBody.create(NetworkConn.MEDIA_TYPE_PNG, new File(mProfilePath)))

                    .build();
        }
    }


    @Override
    public void onResponse(String response, String eventType) {
        if (eventType.equalsIgnoreCase(AppConstants.EVENT_PROFILE_UPDATE) || eventType.equalsIgnoreCase(AppConstants.EVENT_PROFILE_UPDATE)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                jsonObject.getJSONObject("result");
                String strMessage = jsonObject.getString(AppConstants.KEY_MESSAGE);
                int apiResultStatus = jsonObject.getInt(AppConstants.KEY_STATUS);
             //   Log.e("resp  ", jsonObject.toString());

                if (apiResultStatus == AppConstants.STATUS_SUCCESS_VALUE) {
                    mSessionPref.persistUser(jsonObject.getJSONObject("result"));
                    mBinding.ivCamera.setVisibility(View.GONE);
                    mBinding.btnProfileSave.setVisibility(View.GONE);

                    if (mEmailStr.length() != 0) {
                        mBinding.etMailaddress.setText(mEmailStr);

                    } else {
                        mBinding.etMailaddress.setText(getString(R.string.not_available));

                    }
                    mBinding.etMailaddress.setFocusableInTouchMode(false);
                    mBinding.etMailaddress.setSelected(false);
                    mBinding.ivProfileEdit.setVisibility(View.VISIBLE);
                    mBinding.etHomeLocation.setEnabled(false);
                    mBinding.etOfficeLocation.setEnabled(false);
                    mBinding.etName.setEnabled(false);
                    //  mBinding.etLastName.setEnabled(false);
                    mBinding.ivMyProfile.setEnabled(false);
                    // mBinding.flPicture.setFocusableInTouchMode(false);
                    mBinding.etName.setEnabled(false);
                    mBinding.etMailaddress.setEnabled(false);
                    mBinding.ivMyProfile.setEnabled(false);
                    mErrorLayout.showAlert(strMessage, ErrorLayout.MsgType.Info, true);
                } else {
                    mErrorLayout.showAlert(strMessage, ErrorLayout.MsgType.Error, true);
                }

            } catch (JSONException e) {

                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNoNetworkConnectivity() {

    }

    @Override
    public void onRequestRetry() {

    }

    @Override
    public void onRequestFailed() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        KeyboardUtil.hideKeyBoardDialog(mContext, mBinding.clTop);

    }

    private void homeLocation(View view) {

        openAutocompleteActivity(REQUEST_CODE_HOME_ADDRESS);

    }


    private void officeLocation(View view) {

        openAutocompleteActivity(REQUEST_CODE_WORK_ADDRESS);

    }

    private void openAutocompleteActivity(int request_code) {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            LatLngBounds latLngBounds = new LatLngBounds(new LatLng(22.719568, 75.857727),
                    new LatLng(22.719568, 75.857727));

            AutocompleteFilter autocompleteFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(Place.TYPE_COUNTRY)
                    .setCountry("IN")
                    .build();
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .setBoundsBias(latLngBounds)
                    .setFilter(autocompleteFilter)
                    .build(ProfileActivity.this);
            startActivityForResult(intent, request_code);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



