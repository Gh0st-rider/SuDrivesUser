package com.sudrives.sudrives.adapter;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sudrives.sudrives.R;
import com.sudrives.sudrives.databinding.ItemCancelorderBinding;
import com.sudrives.sudrives.model.SelectPackageModel;
import com.sudrives.sudrives.utils.FontLoader;

import java.util.ArrayList;
import java.util.List;

public class SelectPackageAdapter extends RecyclerView.Adapter<SelectPackageAdapter.MyViewHolder> {


    private Context mContext;
    private List<SelectPackageModel> mlist;
    private SelectPackageAdapter.AdapterCallback adapterCallback;


    public SelectPackageAdapter(Context mContext, List<SelectPackageModel> mlist, SelectPackageAdapter.AdapterCallback adapterCallback) {
        this.mContext = mContext;
        this.mlist = mlist;
        this.adapterCallback = adapterCallback;

    }

    public SelectPackageAdapter(Context mContext) {
        this.mContext = mContext;


    }

    @NonNull
    @Override
    public SelectPackageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cancelorder, parent, false);

        return new SelectPackageAdapter.MyViewHolder(itemView);


    }


    @Override
    public void onBindViewHolder(@NonNull SelectPackageAdapter.MyViewHolder holder, final int position) {

        SelectPackageModel reportAnswerListModel = mlist.get(position);
        holder.binding.radioButton.setText(reportAnswerListModel.getPackage_name());
        holder.binding.radioButton.setChecked(reportAnswerListModel.isSelected);
        holder.binding.radioButton.setTag(new Integer(position));

        if (reportAnswerListModel.isSelected) {
            FontLoader.setHelBold(holder.binding.radioButton);
        } else {
            FontLoader.setHelRegular(holder.binding.radioButton);

        }


    }


    public void setList(ArrayList<SelectPackageModel> mdatalist, SelectPackageAdapter.AdapterCallback adapterCallback) {
        this.mlist = mdatalist;
        this.adapterCallback = adapterCallback;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {

        return mlist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ItemCancelorderBinding binding;

        public MyViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);


            binding.radioButton.setOnClickListener(view -> {

                for (int i = 0; i < mlist.size(); i++) {
                    if (getAdapterPosition() == i) {

                        if (mlist.get(i).isSelected)
                            mlist.get(i).isSelected = false;
                        else
                            mlist.get(i).isSelected = true;
                    } else {
                        mlist.get(i).isSelected = false;
                    }

                }
                adapterCallback.onClick(mlist.get(getAdapterPosition()).getPackage_name().toString(),mlist.get(getAdapterPosition()).getId().toString(),mlist.get(getAdapterPosition()).getCity_assign_id().toString());
                notifyDataSetChanged();

            });


        }
    }


    public interface AdapterCallback {
        public void onClick(String packageName, String id,String vehicle_city_id);
    }
}