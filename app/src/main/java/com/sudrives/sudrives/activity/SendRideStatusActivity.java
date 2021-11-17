package com.sudrives.sudrives.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import androidx.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.ContactsContract;
import androidx.core.app.ActivityCompat;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.sudrives.sudrives.R;

import com.sudrives.sudrives.adapter.SendRideStatusAdapter;
import com.sudrives.sudrives.databinding.ActivitySendRideStatusBinding;
import com.sudrives.sudrives.model.ContactBean;
import com.sudrives.sudrives.utils.BaseActivity;
import com.sudrives.sudrives.utils.Config;
import com.sudrives.sudrives.utils.ErrorLayout;
import com.sudrives.sudrives.utils.FontLoader;
import com.sudrives.sudrives.utils.GlobalUtil;
import com.sudrives.sudrives.utils.KeyboardUtil;
import com.sudrives.sudrives.utils.SessionPref;
import com.sudrives.sudrives.utils.server.BaseModel;
import com.sudrives.sudrives.utils.server.BaseRequest;
import com.sudrives.sudrives.utils.server.SocketConnection;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.emitter.Emitter;

public class SendRideStatusActivity extends BaseActivity implements SendRideStatusAdapter.AdapterCallback {
    private ActivitySendRideStatusBinding mBinding;
    private Context mContext;
    private BaseRequest mBaseRequesth;
    private BaseModel mBaseModel;
    private boolean flag = true;
    private int pageCount = 0;
    public boolean isLoadMore = false;
    private ErrorLayout mErrorLayout;
    private SessionPref mSessionPref;
    private SendRideStatusAdapter mAdapter;
    private List<ContactBean> contactList = new ArrayList<>();
    private ArrayList<String> contactListAutoNUmberComplte = new ArrayList<>();
    private ArrayList<String> contactListAutoNameComplte = new ArrayList<>();
    private ArrayList<String> contactListSendName = new ArrayList<>();
    private ArrayList<String> contactListSendNUmner = new ArrayList<>();

    public static final int PERMISSION_ALL = 134;
    private static final int INITIAL_REQUEST = 222;
    private static final String[] INITIAL_PERMS = {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,


    };
    private ArrayList<String> mArrayNUmber = new ArrayList<>();
    private ArrayList<String> mArrayName = new ArrayList<>();

    private ArrayList<String> mUserTopicList = new ArrayList<>();
    private ProgressDialog pDLoadContact;
    private ProgressDialog pDCallSocket;
    private ProgressDialog pDSendMsg;
    private boolean tag = true;
    private double lat_, long_;
    private String tripId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_send_ride_status);
        GlobalUtil.setStatusBarColor(SendRideStatusActivity.this, getResources().getColor(R.color.colorPrimaryDark));

        getControls();

    }

    private void getControls() {

        mContext = SendRideStatusActivity.this;
        mSessionPref = new SessionPref(mContext);
        mErrorLayout = new ErrorLayout(mContext, findViewById(R.id.error_layout));


        mAdapter = new SendRideStatusAdapter(mContext, contactList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mBinding.rvSendRideStatus.setLayoutManager(mLayoutManager);
        mBinding.rvSendRideStatus.setItemAnimator(new DefaultItemAnimator());
        mBinding.rvSendRideStatus.setAdapter(mAdapter);


        pDCallSocket = new ProgressDialog(mContext);
        pDCallSocket.setMessage(getAppString(R.string.please_wait));
        pDCallSocket.setCancelable(false);
        Bundle bundle = getIntent().getExtras();

        lat_ = bundle.getDouble("Clatitude");
        long_ = bundle.getDouble("Clongitude");
        tripId = bundle.getString("tripId");

        pDSendMsg = new ProgressDialog(mContext);
        pDSendMsg.setMessage(getAppString(R.string.please_wait));
        pDSendMsg.setCancelable(false);

        mBinding.toolBar.ibLeft.setVisibility(View.VISIBLE);
        mBinding.toolBar.ibRight1.setVisibility(View.GONE);
        mBinding.toolBar.ibLeft.setImageResource(R.drawable.close_24dp);
        mBinding.toolBar.tvRight.setText(getString(R.string.send));
        mBinding.toolBar.ibLeft.setOnClickListener(this::navigationBackScreen);
        mBinding.toolBar.tvRight.setOnClickListener(this::sendClick);
        mBinding.btnAddContacts.setOnClickListener(this::addContactClick);

        mBinding.toolBar.tvTitle.setText(getString(R.string.send_ride_status1));


        mArrayNUmber.clear();
        mArrayName.clear();

        FontLoader.setHelRegular(mBinding.toolBar.tvRight, mBinding.toolBar.tvTitle, mBinding.tvSendDirectly, mBinding.tvContacts, mBinding.etNameOrMobile);
        FontLoader.setHelBold(mBinding.btnAddContacts);


        mBinding.etNameOrMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String getValue = mBinding.etNameOrMobile.getText().toString().trim();

                /// HELP AND SUPPORT LOCAL SEARCH FUNCTIONALITY
                if (mAdapter != null) {
                    mAdapter.searchHelpAndSupport(getValue);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mBinding.tagsPrimaryTopic.setTagsTextColor(R.color.colorWhite);
        mBinding.tagsPrimaryTopic.setTagsWithSpacesEnabled(true);
        mBinding.tagsPrimaryTopic.setThreshold(1);


    }

    private void addContactClick(View view) {
        KeyboardUtil.hideKeyBoardDialog(mContext, mBinding.clTop);
        if (isValidAlls()) {
            mUserTopicList.clear();
            mUserTopicList.addAll(mArrayName);
            String[] topicArray = new String[mArrayName.size()];
            topicArray = mUserTopicList.toArray(topicArray);
            mBinding.tagsPrimaryTopic.setTags(topicArray);

            // System.out.println(mBinding.tagsPrimaryTopic.getTags());
            if (mBinding.tagsPrimaryTopic.getTags().size() != 0) {
                if (checkConnection()) {
                    JSONArray jsonArrayContact = new JSONArray();
                    JSONArray jsonArrayName = new JSONArray();

                    pDCallSocket.show();
                    try {


                        for (int i = 0; i < mArrayNUmber.size(); i++) {
                            if (mArrayNUmber.contains(mArrayNUmber.get(i))) {
                                jsonArrayContact.put(mArrayNUmber.get(i).replaceAll("[\\s\\-()]", ""));
                                jsonArrayName.put(mArrayName.get(i).toString());

                            }
                        }
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                addContact(jsonArrayName, jsonArrayContact);

                            }
                        }, 500);



                    } catch (Exception e) {
                        if (pDCallSocket != null) {
                            pDCallSocket.dismiss();
                        }
                        e.printStackTrace();
                    }


                } else {

                    mErrorLayout.showAlert(getAppString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, true);
                }
            } else {
                mErrorLayout.showAlert(getAppString(R.string.please_add_contact_number), ErrorLayout.MsgType.Error, true);

            }
        }


    }

    private boolean isValidAlls() {

        if (mArrayNUmber.size() == 0) {
            mErrorLayout.showAlert(getAppString(R.string.please_select_contact_number), ErrorLayout.MsgType.Error, true);
            return false;


        }
        return true;
    }

    private void sendClick(View view) {
        if (mBinding.tagsPrimaryTopic.getTags().size() != 0) {
            pDSendMsg.show();
            if (SocketConnection.isConnected()) {
              //  Log.e("I am connected", "I am connected");

                SocketConnection.attachSingleEventListener(Config.LISTENER_GET_EMERGENCY_SMS, getSendMsg);

                JSONObject jsonObject = new JSONObject();

                try {


                    jsonObject.put("userid", mSessionPref.user_userid);
                    //  jsonObject.put("token", mSessionPref.token);
                    jsonObject.put("language", Config.SELECTED_LANG);
                    jsonObject.put("tripid", tripId);
                    jsonObject.put("lat", lat_);
                    jsonObject.put("longi", long_);

                   // Log.d("send msg", jsonObject.toString());

                } catch (JSONException e) {
                    pDSendMsg.dismiss();
                    e.printStackTrace();
                }


                SocketConnection.emitToServer(Config.EMIT_EMERGENCY_SMS, jsonObject);
            } else {
                pDCallSocket.dismiss();
                // Log.e("send msg=======>", "Error");
            }
        } else {
            mErrorLayout.showAlert(getAppString(R.string.please_add_contact_number), ErrorLayout.MsgType.Error, true);
        }
    }

    private void navigationBackScreen(View view) {
        finish();

    }

    public void addContact(JSONArray jsonArrayName, JSONArray jsonArrayContact) {
        if (SocketConnection.isConnected()) {

            SocketConnection.attachSingleEventListener(Config.LISTNER_GET_EMERGENCY_CONTACT, getAddContact);

            JSONObject jsonObject = new JSONObject();

            try {

                // JSONObject jsonObjectgood = new JSONObject();


                jsonObject.put("userid", mSessionPref.user_userid);
                //  jsonObject.put("token", mSessionPref.token);
                jsonObject.put("language", Config.SELECTED_LANG);
                jsonObject.put("tripid", getIntent().getStringExtra("tripId"));
                jsonObject.put("contact_name", jsonArrayName);
                jsonObject.put("contact_number", jsonArrayContact);


               // Log.d("send msg", jsonObject.toString());

            } catch (JSONException e) {
                pDCallSocket.dismiss();
                e.printStackTrace();
            }


            SocketConnection.emitToServer(Config.EMIT_EMERGENCY_CONTACT, jsonObject);
        } else {
            pDCallSocket.dismiss();
           // Log.e("send msg=======>", "Error");
        }
    }

    // Socket Emit
    Emitter.Listener getSendMsg = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
           // Log.e(" getSendMsg", (String) args[0]);
            pDSendMsg.dismiss();
            getSendMsgResponse((String) args[0]);

        }
    };
    Emitter.Listener getAddContact = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
           // Log.e("Create Contact", (String) args[0]);
            pDCallSocket.dismiss();
            getContactResponse((String) args[0]);

        }
    };

    private void getContactResponse(String response) {
        BaseModel mBaseModel = new BaseModel(mContext);
        if (response != null) {
            try {
                JSONObject obj = new JSONObject(response.toString());
                if (mBaseModel.isParse(response)) {


                    if (obj.getString("status").equals("1")) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    mErrorLayout.showAlert(obj.getString("message"), ErrorLayout.MsgType.Info, true);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        });

                    } else {
                        //  progressDialog.dismiss();
                        mErrorLayout.showAlert(obj.getString("message"), ErrorLayout.MsgType.Error, true);
                    }


                } else {
                    //  progressDialog.dismiss();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mErrorLayout.showAlert(obj.getString("message"), ErrorLayout.MsgType.Error, true);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
                //  progressDialog.dismiss();
            }
        } else {
            //progressDialog.dismiss();
        }
    }

    private void getSendMsgResponse(String response) {
        BaseModel mBaseModel = new BaseModel(mContext);
        if (response != null) {
            try {
                JSONObject obj = new JSONObject(response.toString());
                if (mBaseModel.isParse(response)) {


                    if (obj.getString("status").equals("1")) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {

                                    Intent intent = new Intent();
                                    intent.putExtra("flag", "true");
                                    intent.putExtra("msg", obj.getString("message"));
                                    setResult(RESULT_OK, intent);
                                    finish();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        });

                    } else {

                        mErrorLayout.showAlert(obj.getString("message"), ErrorLayout.MsgType.Error, true);
                    }


                } else {
                    //  progressDialog.dismiss();
                    mErrorLayout.showAlert(obj.getString("message"), ErrorLayout.MsgType.Error, true);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                //  progressDialog.dismiss();
            }
        } else {
            //progressDialog.dismiss();
        }
    }

    @Override
    public void onClick(String name, String number, boolean checkStatus) {
        // System.out.println("total===>    " + mArrayNUmber);
        if (mArrayNUmber.size() < 3) {
            if (checkStatus) {
                if (!mArrayNUmber.contains(number)) {
                    mArrayNUmber.add(number);
                    mArrayName.add(name);
                }

            } else {
                // if (mArrayName.contains(number)) {
                try {
                    mArrayNUmber.remove(number);
                    mArrayName.remove(name);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // }

            }
        } else {

            if (mArrayNUmber.size() > 0 || mArrayNUmber.size() < 3) {
                if (mArrayNUmber.contains(number)) {

                    mArrayNUmber.remove(number);
                    mArrayName.remove(name);
                  //  System.out.println("Remove===>    " + mArrayNUmber);
                }
            }

        }


    }


    class FetchDeviceContact extends AsyncTask<Void, Integer, String> {
        protected void onPreExecute() {
            tag = false;
            if (pDLoadContact != null) {
                pDLoadContact.show();
            }
        }

        protected String doInBackground(Void... arg0) {

            contactList.clear();
            contactListAutoNUmberComplte.clear();
            contactListAutoNameComplte.clear();

            ContentResolver cr = getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);

            if ((cur != null ? cur.getCount() : 0) > 0) {
                contactList.clear();
                contactListAutoNUmberComplte.clear();
                contactListAutoNameComplte.clear();


                while (cur != null && cur.moveToNext()) {
                    String id = cur.getString(
                            cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(
                            ContactsContract.Contacts.DISPLAY_NAME));

                    if (cur.getInt(cur.getColumnIndex(
                            ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        Cursor pCur = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            String phoneNo = pCur.getString(pCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));
                      contactListAutoNameComplte.add(name);
                            contactListAutoNUmberComplte.add(phoneNo);
                            ContactBean bean = new ContactBean(name, phoneNo, "0");

                            if (phoneNo != null || name != null) {

                                contactList.add(bean);
                            }
                        }
                        pCur.close();
                    }
                }

            }
            if (cur != null) {
                cur.close();
                if (pDLoadContact != null) {
                    pDLoadContact.dismiss();
                }
            }

            return "";
        }

        protected void onPostExecute(String result) {

            /**
             * Setting the adapter here
             */

            try {
                if (pDLoadContact != null) {
                    pDLoadContact.dismiss();
                }

                mAdapter.setDataIntoList(contactList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //   Log.e("Contact list", contactList.size() + "=== " + contactList);


        }
    }

    public void checkPermission() {
        if (!hasPermissions(mContext, INITIAL_PERMS)) {
            ActivityCompat.requestPermissions((Activity) mContext, INITIAL_PERMS, PERMISSION_ALL);
        } else {
            try {
              //  Log.e("===========> ", "" + tag);
                if (tag) {
                    pDLoadContact = new ProgressDialog(mContext);
                    pDLoadContact.setMessage(getAppString(R.string.please_wait));
                    pDLoadContact.setCancelable(false);

                    new FetchDeviceContact().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {

            checkPermission();
            KeyboardUtil.hideKeyBoardDialog(mContext, mBinding.clTop);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }


}
