package com.sudrives.sudrives.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.sudrives.sudrives.R;
import com.sudrives.sudrives.adapter.PromoAdapter;
import com.sudrives.sudrives.model.PromoModel;
import com.sudrives.sudrives.utils.Config;
import com.sudrives.sudrives.utils.SessionPref;
import com.sudrives.sudrives.utils.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OffersActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton back;
    Context mContext = this;

    RecyclerView recyclerPromo;
    PromoAdapter promoAdapter;
    public static ArrayList<PromoModel> promoList;
    ProgressBar progressBar;

    TextView noPromo;

    SessionPref sessionPref;

    String tripamount = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);

        if (getIntent().getExtras() != null){
            String tripAmount = getIntent().getStringExtra("tripAmount");

            tripamount = tripAmount;
            back = findViewById(R.id.iv_coupon);
            back.setOnClickListener(this);

            sessionPref = new SessionPref(mContext);

            progressBar = findViewById(R.id.progress_promo);
            recyclerPromo = findViewById(R.id.recycler_promo);
            recyclerPromo.setHasFixedSize(true);
            promoList = new ArrayList<>();
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
            recyclerPromo.setLayoutManager(layoutManager);
            promoAdapter = new PromoAdapter(mContext, promoList, tripAmount);
            recyclerPromo.setAdapter(promoAdapter);

            noPromo = findViewById(R.id.no_coupon);

            getPromoData();
        }

       // String tripAmount = getIntent().getStringExtra("tripAmount");
        back = findViewById(R.id.iv_coupon);
        back.setOnClickListener(this);

        sessionPref = new SessionPref(mContext);

        progressBar = findViewById(R.id.progress_promo);
        recyclerPromo = findViewById(R.id.recycler_promo);
        recyclerPromo.setHasFixedSize(true);
        promoList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recyclerPromo.setLayoutManager(layoutManager);
        promoAdapter = new PromoAdapter(mContext, promoList, tripamount);
        recyclerPromo.setAdapter(promoAdapter);

        noPromo = findViewById(R.id.no_coupon);

        getPromoData();    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_coupon:

                finish();

                break;
        }
    }

    private void getPromoData() {

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.BASE_URL_NEW + "getpromocode?user_id=" + sessionPref.user_userid +
                "&token=" + sessionPref.token, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                promoList.clear();
                Log.e("NotiResponse", response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    int status = jsonResponse.getInt("status");
                    String message = jsonResponse.getString("message");
                    if (status == 1) {
                        noPromo.setVisibility(View.GONE);
                        Toast.makeText(mContext, "Apply coupon and get discount", Toast.LENGTH_LONG).show();
                        JSONArray jsonArrayOffer = jsonResponse.getJSONArray("data");
                        Gson gson = new Gson();
                        for (int i = 0; i < jsonArrayOffer.length(); i++) {
                            PromoModel promoModel = gson.fromJson(String.valueOf(jsonArrayOffer.get(i)), PromoModel.class);
                            promoList.add(promoModel);
                        }
                    } else {
                        noPromo.setVisibility(View.VISIBLE);
                    }

                    promoAdapter.notifyDataSetChanged();

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
        }) ;
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(mContext).addToRequestQueue(stringRequest);
    }

}