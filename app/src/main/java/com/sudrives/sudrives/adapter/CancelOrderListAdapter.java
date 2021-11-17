package com.sudrives.sudrives.adapter;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.graphics.Typeface;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;


import com.sudrives.sudrives.R;
import com.sudrives.sudrives.databinding.ItemCancelorderBinding;
import com.sudrives.sudrives.model.CancelOrderListModel;

import java.util.ArrayList;
import java.util.List;

public class CancelOrderListAdapter extends RecyclerView.Adapter<CancelOrderListAdapter.MyViewHolder> {


    private Context mContext;
    private List<CancelOrderListModel> mlist;


    public CancelOrderListAdapter(Context mContext, List<CancelOrderListModel> mlist) {
        this.mContext = mContext;
        this.mlist = mlist;

    }

    @NonNull
    @Override
    public CancelOrderListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cancelorder, parent, false);

        return new CancelOrderListAdapter.MyViewHolder(itemView);


    }

    @Override
    public void onBindViewHolder(@NonNull CancelOrderListAdapter.MyViewHolder holder, final int position) {

        CancelOrderListModel reportAnswerListModel = mlist.get(position);
        holder.binding.radioButton.setText(reportAnswerListModel.getAnswer_report_type());
        holder.binding.radioButton.setChecked(reportAnswerListModel.isSelected);
        holder.binding.radioButton.setTag(new Integer(position));

        if (reportAnswerListModel.isSelected)
        {
            holder.binding.radioButton.setTypeface(null, Typeface.BOLD);
        }
        else {
            holder.binding.radioButton.setTypeface(null, Typeface.NORMAL);

        }

    }


    public void setList(ArrayList<CancelOrderListModel> mdatalist) {
        this.mlist = mdatalist;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RadioButton answer_report_type;
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
                notifyDataSetChanged();
            });


        }
    }


}