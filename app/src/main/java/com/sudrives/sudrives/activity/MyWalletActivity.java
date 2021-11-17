package com.sudrives.sudrives.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.sudrives.sudrives.R;
import com.sudrives.sudrives.adapter.TransactionAdapter;
import com.sudrives.sudrives.model.TransactionModel;
import com.sudrives.sudrives.utils.Config;
import com.sudrives.sudrives.utils.GlobalUtil;
import com.sudrives.sudrives.utils.SessionPref;
import com.sudrives.sudrives.utils.VolleySingleton;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyWalletActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton back;
    Context mContext = this;
    Button addMoney;

    TextView walletAmount;
    TextView noTransaction;

    AVLoadingIndicatorView progressBar;
    RecyclerView recyclerTransaction;
    TransactionAdapter transactionAdapter;
    public static ArrayList<TransactionModel> list;

    String wallet_amout;

    SessionPref sessionPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wallet);


       sessionPref = new SessionPref(mContext);


        back = findViewById(R.id.iv_wallet);
        back.setOnClickListener(this);

        progressBar = findViewById(R.id.material_design_linear_spin_fade_loader);
        recyclerTransaction = findViewById(R.id.recycler_transaction);
        recyclerTransaction.setHasFixedSize(true);
        list = new ArrayList<>();
        recyclerTransaction.setLayoutManager(new LinearLayoutManager(mContext));
        transactionAdapter = new TransactionAdapter(mContext, list);
        recyclerTransaction.setAdapter(transactionAdapter);


        noTransaction = findViewById(R.id.no_transaction_wallet);
        addMoney = findViewById(R.id.btn_add_money);
        addMoney.setOnClickListener(this);
        walletAmount = findViewById(R.id.tv_wallet_amount);

        getTransaction();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_money:

                Intent i = new Intent(mContext,PaymentActivity.class);
                i.putExtra("payment_amount",wallet_amout);
                startActivity(i);

                break;

            case R.id.iv_wallet:


                finish();

                break;

        }
    }


    private void getTransaction() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.BASE_URL_NEW + "fetchpaymentdetails?user_id=" + sessionPref.user_userid +
                "&token=" + sessionPref.token, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                Log.e("PointResponse", response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    int status = jsonResponse.getInt("status");
                    String message = jsonResponse.getString("message");
                    if (status == 1) {
                        JSONObject jsonObject = jsonResponse.getJSONObject("data");

                        wallet_amout = jsonObject.optString("wallet_amout");

                        walletAmount.setText(wallet_amout+ " cr");
                        //SharedPreference.write(SharedPreference.WALLET_BALANCE,wallet_amout);

                        JSONArray jsonArray = jsonObject.getJSONArray("transation_details");
                        if (jsonArray.length() != 0) {
                            noTransaction.setVisibility(View.GONE);
                            Gson gson = new Gson();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                TransactionModel transactionModel = gson.fromJson(String.valueOf(jsonArray.get(i)), TransactionModel.class);
                                list.add(transactionModel);
                            }

                        } else {

                            noTransaction.setVisibility(View.VISIBLE);
                        }

                    } else {
                        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                    }

                    transactionAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Log.e("vollyerror", String.valueOf(error));
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(mContext).addToRequestQueue(stringRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(mContext,HomeMenuActivity.class));
        finish();
    }
}
