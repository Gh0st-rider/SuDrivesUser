package com.sudrives.sudrives.adapter;

import android.content.Context;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sudrives.sudrives.R;
import com.sudrives.sudrives.model.TruckBean;
import com.sudrives.sudrives.model.getpricesforvehicle.Result;
import com.sudrives.sudrives.utils.LatLngInterpolator;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

//adapter
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {


    private List<Result> results;
    private boolean mHorizontal;
    private boolean mPager;
    private Context mContext;
    private AdapterCallback adapterCallback;
    private String rupeeSymbol = "\u20B9";
    public int row_index=0;
    private String textReach = "reach by: ";

    public RecyclerViewAdapter(Context context,AdapterCallback adapterCallback, List<Result> results) {

        this.mContext=context;
        this.adapterCallback=adapterCallback;
        this.results = results;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vehicle, parent, false));

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Result result = results.get(holder.getAdapterPosition());
        holder.tv.setText(result.getVehicleName());
        holder.tv_fare.setText(rupeeSymbol+result.getFareOfDistance());
        //holder.llView.getLayoutParams().width=getScreenWidth()/5;
        Glide.with(mContext).load(result.getVehicleImage()).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.drawable.car_placeholder).into(holder.iv);

        String str = result.getEta();
        if (str.contains("hour") && !str.contains("day")) {

            if (str.contains("mins")) {
                str = str.replace("mins", "");

            } else if (str.contains("min")) {
                str = str.replace("min", "");

            }

            if (str.contains("hours")) {

                str = str.replace("hours", "/");
            } else {

                str = str.replace("hour", "/");
            }

            String hourconvert[] = str.split("/");
            String hour = hourconvert[0].trim();
            String min = hourconvert[1].trim();

            int mintotal = (Integer.valueOf(hour) * 60) + Integer.valueOf(min);

            Calendar calendar = Calendar.getInstance();
            System.out.println("Current Date = " + calendar.getTime());
            // Add 10 minutes to current date
            calendar.add(Calendar.MINUTE, mintotal);
            System.out.println("Updated Date = " + calendar.getTime());
            Date date = calendar.getTime();
            DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
            String formattedDate = dateFormat.format(date);
            str = formattedDate;
        } else if (str.contains("min")) {

            if (str.contains("mins")) {
                str = str.replace("mins", "");

            } else if (str.contains("min")) {
                str = str.replace("min", "");

            }
            Calendar calendar = Calendar.getInstance();
            System.out.println("Current Date = " + calendar.getTime());
            // Add 10 minutes to current date
            calendar.add(Calendar.MINUTE, Integer.valueOf(str.trim()));
            System.out.println("Updated Date = " + calendar.getTime());
            Date date = calendar.getTime();
            DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
            String formattedDate = dateFormat.format(date);
            str = formattedDate;
        }

        holder.eta.setText(textReach+str);

        switch (result.getVehicleId()) {
            case "1":
            case "2":
            case "3":
            case "6":
                holder.tv_capacity.setText("4");
                break;
            case "5":
                holder.tv_capacity.setText("6");
                break;
        }

        /*try{
            if (!mArrayVehicleFare.isEmpty()){

                holder.eta.setText("Reached by:"+mArrayEta.get(position));
                holder.tv_fare.setText(rupeeSymbol+mArrayVehicleFare.get(position));
                holder.tv_capacity.setText(mArrayCapacity.get(position));

            }
        }catch (Exception e){
            e.printStackTrace();
        }*/



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row_index=position;
                adapterCallback.onClick(position,holder.itemView);

                notifyDataSetChanged();
            }
        });


        if(row_index==position){
            holder.linearLayout.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.linearLayout.setVisibility(View.GONE);
        }
    }



    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView iv;
        public TextView tv,tv_fare,eta, tv_capacity;
        public ConstraintLayout llView;
        LinearLayout linearLayout;
        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            iv = (ImageView) itemView.findViewById(R.id.iv);
            tv = (TextView) itemView.findViewById(R.id.tv);
            llView = itemView.findViewById(R.id.llView);
            tv_fare = itemView.findViewById(R.id.tv_fare);
            eta = itemView.findViewById(R.id.eta);
            tv_capacity = itemView.findViewById(R.id.capacity);
            linearLayout = itemView.findViewById(R.id.ll_selected);
            //llView.setOnDragListener(null);




            llView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //llView.setBackground(ContextCompat.getDrawable(mContext,R.drawable.gradient_circle));
                   // for (int i=0; i<=)
                    adapterCallback.onClick(getAdapterPosition(),itemView);
                }
            });
        }

        @Override
        public void onClick(View v) {
         //   Log.d("App", mApps.get(getAdapterPosition()).getName());

        }
    }



    private int getScreenWidth(){
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        return width;
    }

    public interface  AdapterCallback{
        public void onClick(int pos, View view);
    }

}

