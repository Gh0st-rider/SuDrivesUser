package com.sudrives.sudrives.adapter;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.sudrives.sudrives.R;
import com.sudrives.sudrives.databinding.ItemOutstationCabTypeBinding;
import com.sudrives.sudrives.model.OutStationCabTypeModel;
import com.sudrives.sudrives.utils.FontLoader;

import java.util.ArrayList;
import java.util.List;

public class OutStationCabTypeAdapter extends RecyclerView.Adapter<OutStationCabTypeAdapter.MyViewHolder> {

    private List<OutStationCabTypeModel> mList;
    private Context mContext;
    private OutStationCabTypeAdapter.MyViewHolder holder;
    private String tag;
    private int rawPosition = 0;
    private OutStationCabTypeAdapter.AdapterCabCallback adapterCallback;

    public OutStationCabTypeAdapter(Context context, String tag, OutStationCabTypeAdapter.AdapterCabCallback adapterCallback) {
        this.mContext = context;
        this.tag = tag;
        this.adapterCallback = adapterCallback;
    }


    public void setList(ArrayList<OutStationCabTypeModel> list, OutStationCabTypeAdapter.AdapterCabCallback adapterCallback) {
        this.mList = new ArrayList<>();
        this.mList = list;
        this.adapterCallback = adapterCallback;
        notifyDataSetChanged();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        private ItemOutstationCabTypeBinding binding;

        MyViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
        }
    }

    @Override
    public OutStationCabTypeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_outstation_cab_type, parent, false);

        holder = new OutStationCabTypeAdapter.MyViewHolder(v);

        return holder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(OutStationCabTypeAdapter.MyViewHolder holder, int position) {
        final OutStationCabTypeModel model = mList.get(position);

        holder.binding.tvCabType.setText(model.getVehicle_name());
        holder.binding.tvCabTypeDes.setText(model.getVehicle_discription());

        holder.binding.tvCabAmount.setText(Html.fromHtml("\u20B9 " + model.getAmount()));
        holder.binding.ivArrow.setVisibility(View.VISIBLE);

        holder.binding.ivCabType.setBackgroundResource(R.color.colorWhite);
        Glide.with(mContext).load(mList.get(position).getVehicle_img()).into(holder.binding.ivCabType);
        holder.binding.tvCabType.setTextColor(mContext.getResources().getColor(R.color.colorGrayDark));


        holder.binding.rlCabType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                adapterCallback.onClickCab(model.getVehicle_name(),model.getVehicle_discription(), model.getVehicle_id(),
                        model.getKm_distance(),model.getAmount(),model.getCharge_per_km(),model.getTime(),model.getSgst(),model.getCgst(),model.getNight_charge()
                        ,model.getCancel_charges(),model.getVehicle_city_id());




            }
        });


        FontLoader.setHelRegular(holder.binding.tvCabType, holder.binding.tvCabTypeDes, holder.binding.tvCabAmount);
        // FontLoader.setHelBold(holder.binding.tvCabAmount);


    }


    @Override
    public int getItemCount() {

        return mList.size();
    }


    public interface AdapterCabCallback {
        public void onClickCab(String vehicleName, String vehicleDes, String vehicleId, String distance,
                               String amount,String baseFare,String time,String sgst,String cgst,String nightCharges,String cancelCharges,String vehicleCityId);
    }

}
