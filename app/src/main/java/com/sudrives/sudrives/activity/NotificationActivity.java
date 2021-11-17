package com.sudrives.sudrives.activity;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sudrives.sudrives.R;
import com.sudrives.sudrives.databinding.ActivityNotificationBinding;
import com.sudrives.sudrives.databinding.ItemNotificationBinding;
import com.sudrives.sudrives.listeners.PaginationScrollListener;
import com.sudrives.sudrives.model.NotficationListModel;
import com.sudrives.sudrives.utils.AppDialogs;
import com.sudrives.sudrives.utils.BaseActivity;
import com.sudrives.sudrives.utils.Config;
import com.sudrives.sudrives.utils.ErrorLayout;
import com.sudrives.sudrives.utils.FontLoader;
import com.sudrives.sudrives.utils.GlobalUtil;
import com.sudrives.sudrives.utils.SessionPref;
import com.sudrives.sudrives.utils.server.BaseModel;
import com.sudrives.sudrives.utils.server.BaseRequest;
import com.sudrives.sudrives.utils.server.CallBackResponse;
import com.sudrives.sudrives.utils.server.ConnectivityReceiver;
import com.sudrives.sudrives.utils.server.JsonElementUtil;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends BaseActivity {
    private ActivityNotificationBinding mBinding;
    private Context mContext;
    private NotificationAdapter mAdapter;
    private boolean flag = true;
    private BaseRequest mBaseRequest;
    private BaseModel mBaseModel;
    private ArrayList<NotficationListModel> mNotificationlist;
    public LinearLayoutManager mLayoutManager;
    private int pageCount = 1;
    public boolean isLoadMore = false;
    private ErrorLayout mErrorLayout;
    private SessionPref mSessionPref;
    private String mMobileStr = "", mUserId = "", title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_notification);
        getControls();
        GlobalUtil.setStatusBarColor(NotificationActivity.this, getResources().getColor(R.color.colorPrimaryDark));


    }

    private void getControls() {
        mContext = NotificationActivity.this;
        mSessionPref = new SessionPref(mContext);
        mErrorLayout = new ErrorLayout(this, findViewById(R.id.error_layout));


        mBinding.toolBar.ibRight4.setText(getAppString(R.string.clear_all));
        mBinding.toolBar.ibRight4.setTextSize(12);

        mBinding.toolBar.ibRight1.setVisibility(View.GONE);
        mBinding.toolBar.ibRight1.setImageResource(R.drawable.notification_icon);

        mBinding.toolBar.tvNotificationBadge.setVisibility(View.GONE);

        mBinding.toolBar.tvTitle.setText(R.string.notifications);
        mBinding.toolBar.ibLeft.setImageResource(R.drawable.arrow_back_24dp);
        mBinding.toolBar.ibLeft.setOnClickListener(this::navigationBackScreen);
        mBinding.toolBar.ibRight4.setOnClickListener(this::clearAllClick);


        mAdapter = new NotificationAdapter(mContext);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mBinding.rvNotification.setLayoutManager(mLayoutManager);
        mBinding.rvNotification.setItemAnimator(new DefaultItemAnimator());

        mNotificationlist = new ArrayList<>();
        try {
            if (checkConnection()) {
                //AppApplication.getInstance().trackScreenView(getAppString(R.string.u_notification));
                notificationListApi(0);

                mBinding.toolBar.tvNotificationBadge.setVisibility(View.GONE);


            } else {
                dialogClickForAlert(false, getString(R.string.no_internet_connection));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mBinding.rvNotification.addOnScrollListener(new PaginationScrollListener(mLayoutManager) {
            @Override
            protected void loadMoreItems() {
                // mocking network delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        int lastCompletelyVisibleItemPosition = ((LinearLayoutManager) mBinding.rvNotification.getLayoutManager()).findLastVisibleItemPosition();
                        if (isLoadMore) {

                            isLoadMore = false;
                            pageCount = pageCount + 1;
                            mBinding.notificationCircularProgressBar.setVisibility(View.GONE);
                            if (checkConnection()) {

                                try {
                                    //   mAdapter.notifyItemInserted(lastCompletelyVisibleItemPosition-1);
                                    notificationListApi(lastCompletelyVisibleItemPosition + 1);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        }
                    }
                }, 1000);
            }

            @Override
            public int getTotalPageCount() {
                return 0;
            }

            @Override
            public boolean isLastPage() {
                return false;
            }

            @Override
            public boolean isLoading() {
                return false;
            }
        });

    }

    /**
     * Error dialog for the API error messages
     *
     * @param msg :- Message which we need to show.
     */
    public void dialogClickForAlert(boolean isSuccess, String msg) {
        int layoutPopup;
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


        AppDialogs.singleButtonVersionDialog(NotificationActivity.this, layoutPopup, title, drawable, msg,
                getString(R.string.ok),
                new AppDialogs.SingleButoonCallback() {
                    @Override
                    public void singleButtonSuccess(String from) {
                        finish();
                    }
                }, true);
    }


    /**
     * Clear click
     *
     * @param view
     */
    private void clearAllClick(View view) {
        int layoutPopup;

        layoutPopup = R.layout.duuble_button_dialog;

        AppDialogs.DoubleButtonWithCallBackVersionDialog(mContext, layoutPopup, true, false, 0, getString(R.string.are_you_sure_you_want_to_clear_all_notification),
                getString(R.string.clear_all), getString(R.string.clear_all),
                getString(R.string.cancel),
                new AppDialogs.Doublebuttonpincallback() {
                    @Override
                    public void doublebuttonok(String from) {
                        if (checkConnection()) {
                            clearNotification();

                        } else {

                        }
                    }
                }, new AppDialogs.DoublebuttonCancelcallback() {
                    @Override
                    public void doublebuttonCancel(String from) {


                    }


                }, new AppDialogs.Crosscallback() {


                    @Override
                    public void crossButtonCallback(String from) {

                    }


                });
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
    public void onPermissionsGranted(int requestCode) {

    }

    /***************    Implement API To get Data    *************************/
    private void notificationListApi(int position) {
        String user_id = "";
        user_id = mSessionPref.user_userid;

        mBaseRequest = new BaseRequest(mContext, true);
        JsonObject jsonObj = JsonElementUtil.getJsonObject("userid", mSessionPref.user_userid, "token", mSessionPref.token, "language", Config.SELECTED_LANG, "page", String.valueOf(pageCount));
        mBaseModel = new BaseModel(mContext);
        String finalUser_id = user_id;

        //isLoadMore=false;
        mBaseRequest.setBaseRequest(jsonObj, "api/get_notifications", user_id, Config.TIMEZONE, Config.SELECTED_LANG, Config.DEVICE_TOKEN, Config.DEVICE_TYPE, new CallBackResponse() {
            @Override
            public void getResponse(Object response) {
                GlobalUtil.setSelection(mBinding.notifyTop, true);

                try {
                     Log.e("Notification==>> ", response.toString());
                    if (mBaseModel.isParse(response.toString())) {
                        mBinding.txtNoDataFound.setVisibility(View.GONE);
                        mBinding.rvNotification.setVisibility(View.VISIBLE);
                        //mNotificationlist.clear();
                        JSONObject jsonObject = new JSONObject(response.toString());
                        mBinding.toolBar.ibRight4.setVisibility(View.VISIBLE);
                        int totalRecord = jsonObject.getInt("total_records");
                        if (NotficationListModel.getNotificationList(mBaseModel.getResultArray()).size() == 5) {

                            isLoadMore = true;

                        } else {
                            isLoadMore = false;
                        }

                        mNotificationlist.addAll(NotficationListModel.getNotificationList(mBaseModel.getResultArray()));

                        // Log.d("sizeeee", "" + mNotificationlist.size() + "  " + pageCount);
                        mAdapter.setList(mNotificationlist, finalUser_id);
                        if (flag) {

                            mBinding.rvNotification.setAdapter(mAdapter);
                            flag = false;
                        } else {
                            mAdapter.notifyItemInserted(mNotificationlist.size());
                        }

                        mAdapter.notifyDataSetChanged();

                        mBinding.rvNotification.getLayoutManager().scrollToPosition(position);


                        if (mBaseModel.getResultArray() != null) {

                            JSONObject jsonObj = new JSONObject(response.toString());

                            HomeMenuActivity.mbadgeCounts = Integer.parseInt(jsonObj.optString("notification_unread", ""));
                        }

                    } else {
                        //   mBinding.notificationCircularProgressBar.setVisibility(View.GONE);
                        if (mNotificationlist.size() != 0) {
                            //  Log.d("sizeeee", "" + mNotificationlist.size());
                            mAdapter.setList(mNotificationlist, finalUser_id);
                            mBinding.rvNotification.setAdapter(mAdapter);
                            mBinding.rvNotification.getLayoutManager().scrollToPosition(position);
                        } else {
                            mBinding.toolBar.ibRight4.setVisibility(View.GONE);
                            mBinding.txtNoDataFound.setVisibility(View.VISIBLE);
                            mBinding.rvNotification.setVisibility(View.VISIBLE);
                           // mBinding.txtNoDataFound.setText(mBaseModel.Message);
                            // mErrorLayout.showAlert(mBaseModel.Message, ErrorLayout.MsgType.Error, true);

                        }
                        isLoadMore = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mBinding.toolBar.ibRight4.setVisibility(View.GONE);
                }

            }

            @Override
            public void getError(Object response, String error) {
                mBinding.toolBar.ibRight4.setVisibility(View.GONE);
                if (!checkConnection()) {
                    mErrorLayout.showAlert(getResources().getString(R.string.no_internet_connection), ErrorLayout.MsgType.Error, false);
                } else {
                    mErrorLayout.showAlert(error.toString(), ErrorLayout.MsgType.Error, true);
                }

            }
        }, false);


    }

    /***************    API notification badge   *************************/
    private void clearNotification() {
        try {
            mBaseRequest = new BaseRequest(mContext, true);
            //   JsonObject jsonObj = JsonElementUtil.getJsonObject( "userid", mSessionPref.user_userid);
            // System.out.println(jsonObj);
            mBaseRequest.setBaseRequest(new JsonObject(), "api/clear_notification", mSessionPref.user_userid, Config.TIMEZONE, Config.SELECTED_LANG, Config.DEVICE_TOKEN, Config.DEVICE_TYPE, new CallBackResponse() {
                @Override
                public void getResponse(Object response) {

                    mBaseModel = new BaseModel(mContext);
                    if (mBaseModel.isParse(response.toString())) {
                        try {
                            mBinding.toolBar.ibRight4.setVisibility(View.GONE);
                            mErrorLayout.showAlert(mBaseModel.Message, ErrorLayout.MsgType.Info, true);
                            mBinding.txtNoDataFound.setVisibility(View.VISIBLE);
                            mBinding.rvNotification.setAdapter(null);
                            mNotificationlist.clear();
                            mBinding.rvNotification.setVisibility(View.GONE);
                            //mBinding.txtNoDataFound.setText(mBaseModel.Message);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        mErrorLayout.showAlert(mBaseModel.Message, ErrorLayout.MsgType.Error, true);

                    }
                }

                @Override
                public void getError(Object response, String error) {

                    mErrorLayout.showAlert(getAppString(R.string.something_went_wrong), ErrorLayout.MsgType.Error, true);

                }
            }, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Adapter
     */
    public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

        private List<NotficationListModel> mList;
        private Context mContext;
        private String user_id;

        public NotificationAdapter(Context context) {
            this.mContext = context;
        }

        private BaseModel mBaseModel;
        private BaseRequest mBaseRequest;

        public void setList(ArrayList<NotficationListModel> list, String user_id) {
            this.mList = new ArrayList<>();
            this.mList = list;
            this.user_id = user_id;
            notifyDataSetChanged();
        }


        class MyViewHolder extends RecyclerView.ViewHolder {
            private ItemNotificationBinding binding;

            MyViewHolder(View view) {
                super(view);
                binding = DataBindingUtil.bind(view);
            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_notification, parent, false);

            MyViewHolder holder = new MyViewHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            final NotficationListModel model = mList.get(position);
            holder.binding.tvNotificationMsg.setText(Html.fromHtml(model.noti_text.trim()));
            holder.binding.tvNotificationTitle.setText(Html.fromHtml(model.noti_title.trim()));


            try {

                holder.binding.tvNotificationTime.setText(model.noti_create.trim());

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (model.noti_status.equals("0")) {
                holder.binding.rlNotificationlist.setBackgroundResource(R.color.notification_unread);
            } else {
                holder.binding.rlNotificationlist.setBackgroundResource(R.color.colorWhite);

            }
            holder.binding.cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (model.noti_status.equals("0")) {
                        if (checkConnection()) {
                            model.setNoti_status("1");

                            callgetNotificationread(model.noti_id);
                            notifyDataSetChanged();

                        }
                    }
                }


            });
            holder.binding.rlNotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (model.noti_status.equals("0")) {
                        if (checkConnection()) {
                            model.setNoti_status("1");
                            callgetNotificationread(model.noti_id);
                            notifyDataSetChanged();

                        }
                    }
                }
            });
            holder.binding.tvNotificationMsg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (model.noti_status.equals("0")) {
                        if (checkConnection()) {
                            model.setNoti_status("1");
                            callgetNotificationread(model.noti_id);
                            notifyDataSetChanged();


                        }

                    }
                }
            });


        }

        @Override
        public int getItemCount() {
            return mList.size();
        }


        // Method to manually check connection status
        public boolean checkConnection() {
            boolean isConnected = ConnectivityReceiver.isConnected();
            return isConnected;
        }


        /***************    API notification badge   *************************/
        private void callgetNotificationread(String noti_id) {
            try {
                mBaseRequest = new BaseRequest(mContext, false);
                JsonObject jsonObj = JsonElementUtil.getJsonObject("notification_id", noti_id, "userid", mSessionPref.user_userid, "token", mSessionPref.token);
                // System.out.println(jsonObj);
                mBaseRequest.setBaseRequest(jsonObj, "api/notification_read", mSessionPref.user_userid, Config.TIMEZONE, Config.SELECTED_LANG, Config.DEVICE_TOKEN, Config.DEVICE_TYPE, new CallBackResponse() {
                    @Override
                    public void getResponse(Object response) {

                        mBaseModel = new BaseModel(mContext);
                        if (mBaseModel.isParse(response.toString())) {
                            try {
                                JSONObject JsonRes = new JSONObject(response.toString());
                                if (JsonRes.getString("status").equals("1")) {

                                    HomeMenuActivity.mbadgeCounts = Integer.parseInt(JsonRes.optString("notification_unread", ""));

                                    //mBinding.rvNotification.setBackgroundResource(R.color.white);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void getError(Object response, String error) {


                    }
                }, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}

