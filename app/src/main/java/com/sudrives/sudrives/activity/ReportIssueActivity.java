package com.sudrives.sudrives.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import androidx.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Handler;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.sudrives.sudrives.R;
import com.sudrives.sudrives.adapter.VehicleTypesAdapter;
import com.sudrives.sudrives.classes.NotificationApi;
import com.sudrives.sudrives.databinding.ActivityReportIssueBinding;
import com.sudrives.sudrives.fcm.ConfigNotif;
import com.sudrives.sudrives.fcm.NotificationUtils;
import com.sudrives.sudrives.model.ReportIssueModel;
import com.sudrives.sudrives.utils.BaseActivity;
import com.sudrives.sudrives.utils.Config;
import com.sudrives.sudrives.utils.ErrorLayout;
import com.sudrives.sudrives.utils.FontLoader;
import com.sudrives.sudrives.utils.GlobalUtil;
import com.sudrives.sudrives.utils.Image_Picker;
import com.sudrives.sudrives.utils.SessionPref;
import com.sudrives.sudrives.utils.listdialog.DialogListFragment;
import com.sudrives.sudrives.utils.listdialog.DialogListModel;
import com.sudrives.sudrives.utils.network_communication.AppConstants;
import com.sudrives.sudrives.utils.network_communication.NetworkConn;
import com.sudrives.sudrives.utils.server.BaseModel;
import com.sudrives.sudrives.utils.server.BaseRequest;
import com.sudrives.sudrives.utils.server.CallBackResponse;
import com.sudrives.sudrives.utils.server.ConnectivityReceiver;
import com.sudrives.sudrives.utils.server.JsonElementUtil;
import com.google.gson.JsonObject;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ReportIssueActivity extends BaseActivity implements NetworkConn.onRequestResponse {
    private ActivityReportIssueBinding mBinding;
    private Context mContext;
    private BaseRequest mBaseRequest;
    private SessionPref mSessionPref;
    private BaseModel mBaseModel;
    private ErrorLayout mErrorLayout;
    private ArrayList<ReportIssueModel> mReportIssueList;
    private ArrayList<DialogListModel> mHaulageUsageTypeDialogList;
    String mHaulageUsageTypeStr = "", mTopicIdStr = "", mEmailStr = "", mCommentStr, mReportPath = "", mMobileNoStr = "", mBookingId = "", mtripId = "";
    private Image_Picker image_picker;
    private static final int INITIAL_REQUEST = 222;
    private static final String[] INITIAL_PERMS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_report_issue);
        GlobalUtil.setStatusBarColor(ReportIssueActivity.this, getResources().getColor(R.color.colorPrimaryDark));

        getControl();
    }


    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    private void notificationClick(View view) {
        startActivity(new Intent(ReportIssueActivity.this, NotificationActivity.class));
    }

    /*
     * initialize all controls
     * */
    private void getControl() {
        mContext = ReportIssueActivity.this;
        mSessionPref = new SessionPref(mContext);

        mReportIssueList = new ArrayList<>();
        mHaulageUsageTypeDialogList = new ArrayList<>();

        image_picker = new Image_Picker(mContext);

        mErrorLayout = new ErrorLayout(this, findViewById(R.id.error_layout));
        mBinding.toolBar.tvTitle.setText(getString(R.string.report_issue));
        mBinding.toolBar.ibLeft.setVisibility(View.VISIBLE);
        mBinding.toolBar.ibRight1.setVisibility(View.VISIBLE);
        mBinding.toolBar.ibLeft.setImageResource(R.drawable.arrow_back_24dp);
        mBinding.toolBar.ibRight1.setImageResource(R.drawable.notification_icon);
        mBinding.toolBar.ibLeft.setOnClickListener(this::navigationBackScreen);
        mBinding.toolBar.ibRight1.setOnClickListener(this::notificationClick);
        mBinding.ivAttachImage.setOnClickListener(this::pickImage);
        mBinding.rlPicSelect.setOnClickListener(this::pickImage);
        mBinding.btnSubmit.setOnClickListener(this::callReportApi);
        FontLoader.setHelRegular(mBinding.toolBar.tvTitle,mBinding.tvBookingId, mBinding.tvTopicT, mBinding.tvCommentT, mBinding.etWriteEmailT, mBinding.tvWriteMobNumT);
        FontLoader.setHelBold(mBinding.btnSubmit);

     getIntentData();

        if (checkConnection()) {
            callingReportList();

        } else {
            mErrorLayout.showAlert(getAppString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, true);
        }


        mBinding.etWriteMobNum.setText(mSessionPref.user_mobile);
        mBinding.etWriteEmail.setText(mSessionPref.user_email);

    }

    //getIntent Data
    private void getIntentData() {
        Intent i = getIntent();
        mBookingId = i.getStringExtra("booking_id");
        mtripId = i.getStringExtra("trip_id");
        mBinding.tvBookingId.setText(mBookingId);

    }

    private void callReportApi(View view) {
        //register button click

        getValues();
        if (isValidAlls()) {
            if (checkConnection()) {
                //  updateProfileApi();

                networkConn.makeRequest(mContext,
                        networkConn.createFormDataRequest(ReportIssueActivity.this, AppConstants.REPORT_ISSUE_URL,
                                getRequestForReportIssue()), AppConstants.EVENT_REPORT_ISSUE,
                        this, mErrorLayout);
            } else {
                // dialogClickForAlert(false, getAppString(R.string.no_internet_connection));
            }
        }

    }

    private void getValues() {
        mCommentStr = getEtString(mBinding.etComment);
        mEmailStr = getEtString(mBinding.etWriteEmail);
        mMobileNoStr = getTvString(mBinding.etWriteMobNum);

    }

    /*
     * check all validations
     * */

    private boolean isValidAlls() {
        if (TextUtils.isEmpty(mTopicIdStr)) {
            mErrorLayout.showAlert(getAppString(R.string.please_select_topic), ErrorLayout.MsgType.Error, true);
            return false;
        } else if (TextUtils.isEmpty(mCommentStr)) {
            mErrorLayout.showAlert(getAppString(R.string.Please_enter_comment), ErrorLayout.MsgType.Error, true);
            return false;
        } else if (TextUtils.isEmpty(mMobileNoStr)) {
            mErrorLayout.showAlert(getAppString(R.string.Please_enter_mobile_num), ErrorLayout.MsgType.Error, true);
            return false;
        } else if (mMobileNoStr.length() < 10) {
            mErrorLayout.showAlert(getAppString(R.string.Please_enter_valid_mobile_num), ErrorLayout.MsgType.Error, true);
            return false;
        }
        return true;
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

        image_picker.imageOptions();
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

    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //  isTakePic = true;
        if (requestCode == Image_Picker.CAMERA_REQUEST) {
           // Log.e("dfgfdgfdg", "ffffffff: " + resultCode + "  :::: " + RESULT_OK + " :::::: " + RESULT_CANCELED);

            switch (resultCode) {
                case RESULT_OK:

                    image_picker.cropImage(this, Uri.fromFile(Image_Picker.IMAGE_PATH), 1);

                    break;

            }
        } else {

            if (requestCode == Image_Picker.GALLERY_REQUEST) {

                switch (resultCode) {
                    case RESULT_OK:
                        Uri selectedImageURI = data.getData();
                        image_picker.cropImage(this, selectedImageURI, 1);


                        break;


                }
            } else {
                if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (resultCode == RESULT_OK) {
                        Uri resultUri = result.getUri();
                        mReportPath = image_picker.getRealPathFromURI(resultUri);


                        mBinding.tvUploadPicture.setText(new File(mReportPath).getName());

                        mBinding.tvOptional.setVisibility(View.GONE);
                        mBinding.tvUploadPicture.setTextColor(getResources().getColor(R.color.colorGrayDark));

                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                        Exception error = result.getError();
                      //  Log.e("error", "profilePicError :::: " + error.getMessage());
                        error.printStackTrace();
                    }
                } else {

                    super.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }

    /**
     * get Request for  signup
     */
    private RequestBody getRequestForReportIssue() {

        if (mReportPath.isEmpty() || mReportPath.equals("") || mReportPath == null) {
            return new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("userid", mSessionPref.user_userid)
                    .addFormDataPart("trip_id", mtripId)
                    .addFormDataPart("topic_id", mTopicIdStr)
                    .addFormDataPart("mobile_no", mMobileNoStr)
                    .addFormDataPart("email", mEmailStr)
                    .addFormDataPart("comments", mCommentStr)


                    .build();
        } else {
            return new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("userid", mSessionPref.user_userid)
                    .addFormDataPart("trip_id", mtripId)
                    .addFormDataPart("topic_id", mTopicIdStr)
                    .addFormDataPart("mobile_no", mMobileNoStr)
                    .addFormDataPart("email", mEmailStr)
                    .addFormDataPart("comments", mCommentStr)
                    .addFormDataPart("picture", "ReportIssue.png", RequestBody.create(NetworkConn.MEDIA_TYPE_PNG, new File(mReportPath)))

                    .build();
        }
    }


    private void callingReportList() {


        mBaseRequest = new BaseRequest(mContext, true);

        JsonObject jsonObj = JsonElementUtil.getJsonObject("types", Config.REPORTISSUE);


        mBaseRequest.setBaseRequest(jsonObj, "CommonController/get_types", mSessionPref.user_userid, Config.TIMEZONE, Config.SELECTED_LANG, Config.DEVICE_TOKEN, Config.DEVICE_TYPE, new CallBackResponse() {
            @Override
            public void getResponse(Object response) {
                mBaseModel = new BaseModel(mContext);
                if (mBaseModel.isParse(response.toString())) {
                    try {

                       // Log.d("-->>", response.toString());

                        mReportIssueList.addAll(ReportIssueModel.getList(mContext, mBaseModel.getResultObject().getJSONArray("report_issue")));
                        for (int i = 0; i < mReportIssueList.size(); i++) {
                            mHaulageUsageTypeDialogList.add(new DialogListModel(mReportIssueList.get(i).getName(), 0));
                        }

                        VehicleTypesAdapter mVehicleTypesAdapter = new VehicleTypesAdapter(mContext, mHaulageUsageTypeDialogList);
                        mBinding.spinnerTopic.setAdapter(mVehicleTypesAdapter);


                        mBinding.spinnerTopic.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                if (position == 0) {
                                    // Disable the first item from Spinner
                                    // First item will be use for hint
                                    mTopicIdStr = "";
                                } else {
                                    mTopicIdStr = mReportIssueList.get(position).getId();
                                }

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
     * Navigation to the Back screen
     *
     * @param view
     */
    private void navigationBackScreen(View view) {
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onResponse(String response, String eventType) {

        if (eventType.equalsIgnoreCase(AppConstants.EVENT_REPORT_ISSUE) || eventType.equalsIgnoreCase(AppConstants.EVENT_REPORT_ISSUE)) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String strMessage = jsonObject.getString(AppConstants.KEY_MESSAGE);
                int apiResultStatus = jsonObject.getInt(AppConstants.KEY_STATUS);
                //Log.e("resp  ", jsonObject.toString());

                if (apiResultStatus == AppConstants.STATUS_SUCCESS_VALUE) {
                    mErrorLayout.showAlert(strMessage, ErrorLayout.MsgType.Info, true);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 2000);


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
        mErrorLayout.showAlert(mContext.getString(R.string.something_went_wrong), ErrorLayout.MsgType.Info, true);
    }


    ///broadcast messaging

    private void notificationListApi() {
        String user_id = "";

        user_id = mSessionPref.user_userid;

        mBaseRequest = new BaseRequest(mContext, false);
        JsonObject jsonObj = JsonElementUtil.getJsonObject("userid", mSessionPref.user_userid, "token", mSessionPref.token, "language", Config.SELECTED_LANG, "page", "");
        mBaseModel = new BaseModel(mContext);
        String finalUser_id = user_id;

        //isLoadMore=false;
        mBaseRequest.setBaseRequest(jsonObj, "api/get_notifications", user_id, Config.TIMEZONE, Config.SELECTED_LANG, Config.DEVICE_TOKEN, Config.DEVICE_TYPE, new CallBackResponse() {
            @Override
            public void getResponse(Object response) {

                try {
                    if (response != null) {
                        JSONObject JsonRes = new JSONObject(response.toString());
                        if (JsonRes.getString("status").equals("1")) {
                            JSONArray arr = JsonRes.getJSONArray("result");


                            int i = Integer.parseInt(JsonRes.optString("notification_unread", ""));

                            if (i == 0) {
                                mBinding.toolBar.tvNotificationBadge.setVisibility(View.GONE);

                            } else {
                                mBinding.toolBar.tvNotificationBadge.setVisibility(View.VISIBLE);
                                mBinding.toolBar.tvNotificationBadge.setText(String.valueOf(i));

                            }


                        }
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }


            }

            @Override
            public void getError(Object response, String error) {
                if (!checkConnection()) {
                    //    mErrorLayout.showAlert(getResources().getString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
                } else {
                    //   mErrorLayout.showAlert(error.toString(), ErrorLayout.MsgType.Error, true);
                }

            }
        }, false);


    }

    BroadcastReceiver mRegistrationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            // checking for type intent filter
           // Log.e("i m here", "i m here");
            // new push notification is received

            if (intent.getAction().equals(ConfigNotif.PUSH_NOTIFICATION_USER)) {
                // new push notification is received


                String response = intent.getStringExtra("response");

                if (ConnectivityReceiver.isConnected()) {
                    //notificationListApi();
                    new NotificationApi(mContext, mSessionPref.user_userid, mBinding.toolBar.tvNotificationBadge);
                } else {
                    mErrorLayout.showAlert(getResources().getString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
                }

            }

        }
    };

    @Override
    protected void onResume() {
        super.onResume();


        if (ConnectivityReceiver.isConnected()) {
            //notificationListApi();
            new NotificationApi(mContext, mSessionPref.user_userid, mBinding.toolBar.tvNotificationBadge);
        } else {
            mErrorLayout.showAlert(getResources().getString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(ConfigNotif.PUSH_NOTIFICATION_USER));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onStop();
    }
}

