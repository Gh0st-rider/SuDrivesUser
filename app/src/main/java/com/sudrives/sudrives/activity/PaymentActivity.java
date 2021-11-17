package com.sudrives.sudrives.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.sudrives.sudrives.R;
import com.sudrives.sudrives.utils.Config;
import com.sudrives.sudrives.utils.SessionPref;
import com.sudrives.sudrives.utils.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity implements View.OnClickListener, PaymentResultWithDataListener {

    ImageButton back;
    Context mContext = this;

    AlertDialog show;

    String wallet_amount;
    TextView walletBalance;
    EditText addAmount;
    Button addMoney;

    SessionPref sessionPref;

    Checkout checkout;

    String amount ="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            wallet_amount = extras.getString("payment_amount");
        }

        checkout = new Checkout();
        Checkout.preload(getApplicationContext());


        sessionPref = new SessionPref(mContext);


        back = findViewById(R.id.iv_payment);
        back.setOnClickListener(this);


        addMoney = findViewById(R.id.bt_add_money);
        addMoney.setOnClickListener(this);

        addAmount = findViewById(R.id.et_amount);

        walletBalance = findViewById(R.id.tv_wallet_amount_add_money);
        walletBalance.setText(wallet_amount + " cr");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_add_money:

                if (TextUtils.isEmpty(addAmount.getText().toString())) {

                    addAmount.setError("please fill the amount");

                } else {


                    getOrderID();
                }


                break;

            case R.id.iv_payment:


                finish();

                break;

        }
    }

    private void getOrderID() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.GENERATE_ORDER_ID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("getOrderID", response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    int status = jsonResponse.getInt("status");
                    String message = jsonResponse.getString("message");
                    if (status == 1) {

                        JSONObject jsonObject = jsonResponse.getJSONObject("result");
                        amount = jsonObject.getString("amount");
                        String orderId = jsonObject.getString("order_id");
                        String reciept_no = jsonObject.getString("receipt");

                        startPayment(amount, orderId, reciept_no);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("vollyerror", String.valueOf(error));
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("payment_type", "Razorpay");
                map.put("amount", addAmount.getText().toString());
                map.put("using_type", "Wallet");
                Log.e("saveAmount", map + "");
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
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(mContext).addToRequestQueue(stringRequest);
    }


    public void startPayment(final String amount, final String orderId, final String receipt) {

        checkout.setKeyID("rzp_live_Qo2n35b2sGbJKK");
        /**
         * Instantiate Checkout
         */

        /**
         * Set your logo here
         */
        checkout.setImage(R.drawable.app_logo);

        /**
         * Reference to current activity
         */
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();
            options.put("name", "SuDrives");
            options.put("description", "Wallet" + receipt);
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("order_id", orderId);//from response of step 3.
            options.put("theme.color", "#3399cc");
            options.put("currency", "INR");
            options.put("amount", amount);//pass amount in currency subunits
            options.put("prefill.email", sessionPref.user_email);
            options.put("prefill.contact", sessionPref.user_mobile);
            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            checkout.open(activity, options);

        } catch (Exception e) {
            Log.e("TAG", "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        Log.e("successss","success");
        saveAmount(paymentData.getPaymentId(),paymentData.getSignature(),paymentData.getOrderId(),"Success",paymentData.getUserEmail(),paymentData.getUserContact());

    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {

        Log.e("onPaymentError","onPaymentError");

        if (paymentData!=null){

            saveAmount(paymentData.getPaymentId(),paymentData.getSignature(),paymentData.getOrderId(),"Failed",paymentData.getUserEmail(),paymentData.getUserContact());

        }else {

            showFailedDialog();

        }

    }


    public void showSuccessDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = getLayoutInflater();
        View d = inflater.inflate(R.layout.popup_payment_success, null);
        alertDialog.setView(d);
        show = alertDialog.show();

        Button ok = d.findViewById(R.id.btn_popup_done);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show.dismiss();
                startActivity(new Intent(mContext,MyWalletActivity.class));
                finish();
            }
        });
    }

    public void showFailedDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = getLayoutInflater();
        View d = inflater.inflate(R.layout.popup_payment_fail, null);
        alertDialog.setView(d);
        show = alertDialog.show();

        Button ok = d.findViewById(R.id.btn_popup_failed);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // addAmount.setHint(R.string.enter_amount);
                show.dismiss();
            }
        });
    }


    private void saveAmount(String payment_id,String signature,String order_id,String status,String user_email,String user_contact) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.AMOUNT_SAVE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("getprofile", response);
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    int status = jsonResponse.getInt("status");
                    String message = jsonResponse.getString("message");
                    if (status == 1) {

                        showSuccessDialog();

                    } else {

                      showFailedDialog();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("vollyerror", String.valueOf(error));
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("payment_id", payment_id);
                map.put("razorpay_signature", signature);
                map.put("razorpay_order_id", order_id);
                map.put("payment_status", status);
                map.put("amount",addAmount.getText().toString());
                map.put("email",user_email);
                map.put("mobile",user_contact);
                map.put("token",sessionPref.token);
                map.put("user_id", sessionPref.user_userid);
                Log.e("saveAmount", map + "");
                return map;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000, 5, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(mContext).addToRequestQueue(stringRequest);
    }
}