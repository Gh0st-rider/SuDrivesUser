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
import com.sudrives.sudrives.databinding.ItemCabTypeBinding;
import com.sudrives.sudrives.model.CabTypeModel;
import com.sudrives.sudrives.utils.FontLoader;

import java.util.ArrayList;
import java.util.List;

public class RentalCabTypeAdapter extends RecyclerView.Adapter<RentalCabTypeAdapter.MyViewHolder> {

    private List<CabTypeModel> mList;
    private Context mContext;
    private RentalCabTypeAdapter.MyViewHolder holder;
    private String tag;
    private int rawPosition = 0;
    private RentalCabTypeAdapter.AdapterCabCallback adapterCallback;

    public RentalCabTypeAdapter(Context context, String tag, RentalCabTypeAdapter.AdapterCabCallback adapterCallback) {
        this.mContext = context;
        this.tag = tag;
        this.adapterCallback = adapterCallback;
    }


    public void setList(ArrayList<CabTypeModel> list, RentalCabTypeAdapter.AdapterCabCallback adapterCallback) {
        this.mList = new ArrayList<>();
        this.mList = list;
        this.adapterCallback = adapterCallback;
        notifyDataSetChanged();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        private ItemCabTypeBinding binding;

        MyViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
        }
    }

    @Override
    public RentalCabTypeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cab_type, parent, false);

        holder = new RentalCabTypeAdapter.MyViewHolder(v);

        return holder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(RentalCabTypeAdapter.MyViewHolder holder, int position) {
        final CabTypeModel model = mList.get(position);

        holder.binding.tvCabType.setText(model.getVehicle_name());
        holder.binding.tvCabTypeDes.setText(model.getVehicle_discription());

        holder.binding.tvCabAmount.setText(Html.fromHtml("\u20B9 " + model.getAmount()));
        if (tag.equalsIgnoreCase("outstation")) {
            holder.binding.ivArrow.setVisibility(View.VISIBLE);
            //  holder.binding.rlCabType.setEnabled(false);
            if (rawPosition == position) {
                Glide.with(mContext).load(mList.get(position).getVehicle_sel_img()).into(holder.binding.ivCabType);
                holder.binding.ivCabType.setBackgroundResource(R.drawable.circle_yellow_filled_boarder);
                holder.binding.tvCabType.setTextColor(mContext.getResources().getColor(R.color.colorGrayDark));
            } else {
                holder.binding.ivCabType.setBackgroundResource(R.color.colorWhite);
                Glide.with(mContext).load(mList.get(position).getVehicle_img()).into(holder.binding.ivCabType);
                holder.binding.tvCabType.setTextColor(mContext.getResources().getColor(R.color.colorGrayLight));
            }
        } else {
            holder.binding.ivArrow.setVisibility(View.GONE);
            //holder.binding.rlCabType.setEnabled(true);
            if (rawPosition == position) {
                holder.binding.tvCabType.setTextColor(mContext.getResources().getColor(R.color.colorGrayDark));
                Glide.with(mContext).load(mList.get(position).getVehicle_sel_img()).into(holder.binding.ivCabType);
                holder.binding.ivCabType.setBackgroundResource(R.drawable.circle_yellow_filled_boarder);
                adapterCallback.onClickCab(mList.get(position).getId(), mList.get(position).getPackage_id(), mList.get(position).getVehicle_id());
            } else {
                holder.binding.tvCabType.setTextColor(mContext.getResources().getColor(R.color.colorGrayLight));
                holder.binding.ivCabType.setBackgroundResource(R.color.colorWhite);
                Glide.with(mContext).load(mList.get(position).getVehicle_img()).into(holder.binding.ivCabType);
            }
        }

        holder.binding.rlCabType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterCallback.onClickCab(mList.get(position).getId(), mList.get(position).getPackage_id(), mList.get(position).getVehicle_id());
                if (tag.equalsIgnoreCase("outstation")) {

                    // mContext.startActivity(new Intent(mContext, ConfirmYourBooking.class));
                } else {
                    rawPosition = position;
                    if (rawPosition == position) {
                        Glide.with(mContext).load(mList.get(position).getVehicle_sel_img()).into(holder.binding.ivCabType);
                        holder.binding.ivCabType.setBackgroundResource(R.drawable.circle_yellow_filled_boarder);
                        holder.binding.tvCabType.setTextColor(mContext.getResources().getColor(R.color.colorGrayDark));
                    } else {
                        holder.binding.tvCabType.setTextColor(mContext.getResources().getColor(R.color.colorGrayLight));
                        holder.binding.ivCabType.setBackgroundResource(R.color.colorWhite);
                        Glide.with(mContext).load(mList.get(position).getVehicle_img()).into(holder.binding.ivCabType);
                    }
                    notifyDataSetChanged();
                }

            }
        });


        FontLoader.setHelRegular(holder.binding.tvCabType, holder.binding.tvCabTypeDes,holder.binding.tvCabAmount);
       // FontLoader.setHelBold(holder.binding.tvCabAmount);


    }


    @Override
    public int getItemCount() {

        return mList.size();
    }


    public interface AdapterCabCallback {
        public void onClickCab(String id, String packageId, String vehicleId);
    }

}
