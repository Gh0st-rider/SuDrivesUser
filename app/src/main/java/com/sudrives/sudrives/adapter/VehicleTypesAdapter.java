package com.sudrives.sudrives.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.sudrives.sudrives.R;
import com.sudrives.sudrives.utils.listdialog.DialogListModel;

import java.util.ArrayList;

public class VehicleTypesAdapter extends BaseAdapter {

    Context context;

    ArrayList<DialogListModel> dataBeans = new ArrayList<>();

    public VehicleTypesAdapter(Context context, ArrayList<DialogListModel> dataBeans) {
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


     //   RegistrationModel.ResultBean.VehicleTypesBean rowItem = getItem(position);

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


       // RegistrationModel.ResultBean.VehicleTypesBean rowItem = getItem(position);

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