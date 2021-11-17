package com.sudrives.sudrives.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sudrives.sudrives.R;
import com.sudrives.sudrives.model.StatesModel;

import java.util.ArrayList;

public class StateAdapter extends BaseAdapter {

    Context context;

    ArrayList<StatesModel.Result> dataBeans = new ArrayList<>();

    public StateAdapter(Context context, ArrayList<StatesModel.Result> dataBeans) {
        this.context = context;
        this.dataBeans = dataBeans;

    }

    @Override
    public int getCount() {
        return dataBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View row = inflater.inflate(R.layout.raw_itemlist_layout, parent, false);

        TextView txtTitle = (TextView) row.findViewById(R.id.tv_location_name);

        if (position == 0) {

            txtTitle.setTextSize(11);
            txtTitle.setTextColor(Color.parseColor("#9B9B9B"));



        } else {
            txtTitle.setTextSize(14);

        }


        txtTitle.setText(dataBeans.get(position).name);
        txtTitle.setPadding(0, 0, 0, 10);
        txtTitle.setTextSize(15);
        View devider = row.findViewById(R.id.view_devider);
        devider.setVisibility(View.GONE);


        return row;
    }


    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);



        View row = inflater.inflate(R.layout.raw_itemlist_layout, parent, false);

        TextView txtTitle = (TextView) row.findViewById(R.id.tv_location_name);

     if (position==0){
         txtTitle.setTextColor(Color.parseColor("#9B9B9B"));

        }else {
         txtTitle.setTextColor(Color.parseColor("#000000"));

        }
        txtTitle.setText(dataBeans.get(position).name);

        txtTitle.setPadding(30, 30, 30, 30);
        View devider = row.findViewById(R.id.view_devider);
        devider.setVisibility(View.VISIBLE);
        return row;
    }
}