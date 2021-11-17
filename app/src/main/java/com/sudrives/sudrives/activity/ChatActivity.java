package com.sudrives.sudrives.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sudrives.sudrives.R;
import com.sudrives.sudrives.firebase.ChatAdapter;
import com.sudrives.sudrives.firebase.FetchMessagesModel;
import com.sudrives.sudrives.firebase.InsertMessagesModel;
import com.sudrives.sudrives.utils.Config;
import com.sudrives.sudrives.utils.SessionPref;
import com.sudrives.sudrives.utils.VolleySingleton;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    Context mContext = this;
    ImageView back;
    EditText etMag;
    ImageView btnSend;
    DatabaseReference databaseReference;
    List<FetchMessagesModel> list = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    public static final String Database_Path = "sudrives_chat";
    String driver_id,mTripId;
    String strDate;

    SessionPref sessionPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            driver_id = extras.getString("driver_id");
            mTripId = extras.getString("trip_id");
        }

        sessionPref = new SessionPref(mContext);

        back = findViewById(R.id.back_chat);
        back.setOnClickListener(this);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm");
        strDate = mdformat.format(calendar.getTime());

        recyclerView = findViewById(R.id.recycler_view_chat);
        recyclerView.setNestedScrollingEnabled(false);
        databaseReference = FirebaseDatabase.getInstance("https://sudrives-9d1c0-default-rtdb.firebaseio.com/").getReference(Database_Path).child("userChat");
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        databaseReference.child("driver" + driver_id).child("driver" + driver_id + "+user" + sessionPref.user_userid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    FetchMessagesModel studentDetails = dataSnapshot.getValue(FetchMessagesModel.class);
                    list.add(studentDetails);

                }

                adapter = new ChatAdapter(ChatActivity.this, list);
                recyclerView.setAdapter(adapter);
                recyclerView.scrollToPosition(1);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {


            }
        });

        btnSend = findViewById(R.id.btn_send_msg);
        etMag = findViewById(R.id.et_msg);
        InputFilter filter = new InputFilter() {
            boolean canEnterSpace = false;

            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {

                if(etMag.getText().toString().equals(""))
                {
                    canEnterSpace = false;
                }

                StringBuilder builder = new StringBuilder();

                for (int i = start; i < end; i++) {
                    char currentChar = source.charAt(i);

                    if (Character.isLetterOrDigit(currentChar) || currentChar == '_') {
                        builder.append(currentChar);
                        canEnterSpace = true;
                    }

                    if(Character.isWhitespace(currentChar) && canEnterSpace) {
                        builder.append(currentChar);
                    }


                }
                return builder.toString();
            }

        };
        etMag.setFilters(new InputFilter[]{filter});
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(etMag.getText().toString())) {

                    etMag.setError("Please write your message");

                } else {

                    InsertMessagesModel insertMessagesModel = new InsertMessagesModel();
                    //GetDataFromEditText();
                    String id1 = databaseReference.push().getKey();
                    String id = "driver" + driver_id + "+user" + sessionPref.user_userid;
                    // Adding name into class function object.
                    insertMessagesModel.setId(id1);
                    // Adding phone number into class function object.
                    insertMessagesModel.setMsg(etMag.getText().toString());
                    insertMessagesModel.setIsUser("user");
                    insertMessagesModel.setChatTime(strDate);

                    // Adding the both name and number values using student details class object using ID.
                    databaseReference.child("driver" + driver_id).child(id).child(id1).setValue(insertMessagesModel);
                    sendNotification(etMag.getText().toString());
                }

            }

        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_chat:

                finish();

                break;


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void sendNotification(String msg) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.CHAT_NOTIFICATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response_send_notif", response);
                        try {
                            //
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.optString("status");
                            String message = jsonObject.optString("message");
                            if (status.equalsIgnoreCase("1")) {

                                etMag.setText("");

                            } else {

                            }

                        } catch (Exception e) {
                            Log.e("", e.toString());
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext, "Server error please try after sometime", Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("send_user_id",driver_id);
                map.put("trip_id", mTripId);
                map.put("message",msg);
                Log.e("okk", map + "");
                return map;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", sessionPref.user_userid);
                params.put("token", sessionPref.token);
                return params;
            }
        };
        RetryPolicy retryPolicy = new DefaultRetryPolicy(60000, 30, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        stringRequest.setShouldCache(true);
        VolleySingleton.getInstance(mContext).addToRequestQueue(stringRequest);
    }

}