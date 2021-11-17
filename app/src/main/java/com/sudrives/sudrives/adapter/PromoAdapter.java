package com.sudrives.sudrives.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.sudrives.sudrives.R;
import com.sudrives.sudrives.model.PromoModel;
import com.sudrives.sudrives.utils.Config;
import com.sudrives.sudrives.utils.SessionPref;
import com.sudrives.sudrives.utils.VolleySingleton;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PromoAdapter extends RecyclerView.Adapter<PromoAdapter.RecentViewHolder> {

    private Context context;
    private ArrayList<PromoModel> list;
    private String tripAmount;

    public PromoAdapter(Context context, ArrayList<PromoModel> list, String tripAmount) {
        this.context = context;
        this.list = list;
        this.tripAmount = tripAmount;
    }

    View view;

    @NonNull
    @Override
    public RecentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        view = inflater.inflate(R.layout.item_promo_code, viewGroup, false);
        return new RecentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecentViewHolder holder, int position) {
        final PromoModel promoModel = list.get(position);


        holder.tittle.setText(promoModel.getPromotions_title());
        holder.time.setText(date(promoModel.getEnd_date()));
        holder.description.setText(promoModel.getDescription());
        SessionPref sessionPref = new SessionPref(context);

        holder.applyCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StringRequest stringRequest = new StringRequest(Request.Method.POST, Config.GET_PROMOAPPLIED,
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

                                        JSONObject jsonObject1 = jsonObject.getJSONObject("data");


                                        Intent returnIntent = new Intent();
                                        returnIntent.putExtra("coupon_title",promoModel.getPromotions_title());
                                        returnIntent.putExtra("coupon_discount",jsonObject1.getString("discount_amount"));
                                        returnIntent.putExtra("coupon_code",promoModel.getCode());
                                        returnIntent.putExtra("trip_amount", jsonObject1.getString("trip_amount"));
                                        returnIntent.putExtra("trip_discount_amount", jsonObject1.getString("trip_discount_amount"));
                                        ((Activity) context).setResult(Activity.RESULT_OK,returnIntent);
                                        ((Activity) context).finish();

                                    } else {
                                        Toast.makeText(context, "Cannot apply coupon please check description!", Toast.LENGTH_LONG).show();
                                    }

                                } catch (Exception e) {
                                    Log.e("", e.toString());
                                }

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(context, "Server error please try after sometime", Toast.LENGTH_LONG).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<>();
                        map.put("user_id", sessionPref.user_userid);

                        map.put("coupon_id", promoModel.getId()+"");
                        map.put("trip_amount", tripAmount);

                        Log.e("okk", map + "");
                        return map;
                    }
                    /*@Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("userid", sessionPref.user_userid);
                        params.put("token", sessionPref.token);
                        return params;
                    }*/
                };
                RetryPolicy retryPolicy = new DefaultRetryPolicy(60000, 30, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                stringRequest.setRetryPolicy(retryPolicy);
                stringRequest.setShouldCache(true);
                VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);

               /* ;*/

            }
        });

        if (tripAmount.equalsIgnoreCase("0.0") || tripAmount.isEmpty()){
            Log.e("dsds", "yes"+tripAmount);
            holder.applyCoupon.setVisibility(View.GONE);
        }else {
            Log.e("dsds", "yes"+tripAmount);
            holder.applyCoupon.setVisibility(View.VISIBLE);

        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class RecentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tittle,description,time;
        Button applyCoupon;

        public RecentViewHolder(@NonNull View itemView) {
            super(itemView);

            tittle = itemView.findViewById(R.id.tv_promo_tittle);
            description = itemView.findViewById(R.id.tv_promo_detail);
            time = itemView.findViewById(R.id.tv_promo_end);
            applyCoupon = itemView.findViewById(R.id.btn_apply_coupon);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
          //  final PromoModel promoModel = list.get(getAdapterPosition());

        }
    }

    public String date(String date1) {
        Date date = null;
        String stringDate= "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = formatter.parse(date1);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            stringDate = dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return stringDate;
    }




}
