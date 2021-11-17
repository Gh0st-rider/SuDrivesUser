package com.sudrives.sudrives.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.sudrives.sudrives.R;
import com.sudrives.sudrives.activity.ReportIssueDetailsActivity;
import com.sudrives.sudrives.databinding.ListitemReportissueBinding;
import com.sudrives.sudrives.model.ReportIssueListModel;
import com.sudrives.sudrives.utils.FontLoader;

import java.util.ArrayList;
import java.util.List;

public class ReportIssueAdapter extends RecyclerView.Adapter<ReportIssueAdapter.MyViewHolder> {

    private List<ReportIssueListModel> mList;
    private Context mContext;
    private ReportIssueAdapter.MyViewHolder holder;

    public ReportIssueAdapter(Context context) {
        this.mContext = context;
    }


    public void setList(ArrayList<ReportIssueListModel> list) {
        this.mList = new ArrayList<>();
        this.mList = list;
        notifyDataSetChanged();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        private ListitemReportissueBinding binding;

        MyViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(view);

        }
    }

    @Override
    public ReportIssueAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_reportissue, parent, false);

        holder = new ReportIssueAdapter.MyViewHolder(v);

        return holder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(ReportIssueAdapter.MyViewHolder holder, int position) {
        final ReportIssueListModel model = mList.get(position);

        FontLoader.setHelRegular(holder.binding.tvTruckType,holder.binding.tvOriginMyBooking,holder.binding.tvDestinationMyBooking
                ,holder.binding.tvTransactionId,holder.binding.tvDeliveryDateVal,holder.binding.tvStatus);
        FontLoader.setHelBold(holder.binding.tvBookingId,holder.binding.tvAmount);


        holder.binding.tvDeliveryDateVal.setText(model.getCreate_dt());



        holder.binding.tvAmount.setText(Html.fromHtml("\u20B9 " + model.getTrip_price()));

        if (model.getTypes().equalsIgnoreCase("booking")) {
            showData(holder, false);
            holder.binding.tvOriginMyBooking.setText(model.getBook_from_address());
            holder.binding.tvDestinationMyBooking.setText(model.getBook_to_address());
            holder.binding.tvTruckType.setText(model.getVehicle_name());
            holder.binding.tvBookingId.setText(mContext.getString(R.string.trip_id) + " : " + model.getBooking_id());

            switch (model.getStatus()) {
                case "Pending":
                    holder.binding.tvStatus.setText(model.getStatus());
                    holder.binding.tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorYellow));
                    holder.binding.ivStatus.setImageResource(R.drawable.pending_24dp);

                    break;
                case "Processed":
                    holder.binding.tvStatus.setText(model.getStatus());
                    holder.binding.tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
                    holder.binding.ivStatus.setImageResource(R.drawable.selected_24dp);

                    break;
                case "Completed":
                    holder.binding.tvStatus.setText(model.getStatus());
                    holder.binding.tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
                    holder.binding.ivStatus.setImageResource(R.drawable.selected_24dp);

                    break;

            }
        } else {
            showData(holder, true);
            holder.binding.tvBookingId.setText(mContext.getString(R.string.order_id) + " : " + model.getPaytm_orderid());
            holder.binding.tvTransactionId.setText(mContext.getString(R.string.transaction_id) + " : " + model.getPaytm_trxid());
            switch (model.getStatus()) {
                case "Pending":
                    holder.binding.tvTruckType.setText(model.getStatus());
                    holder.binding.tvTruckType.setTextColor(mContext.getResources().getColor(R.color.colorYellow));
                    holder.binding.ivTruckType.setImageResource(R.drawable.pending_24dp);

                    break;
                case "Processed":
                    holder.binding.tvTruckType.setText(model.getStatus());
                    holder.binding.tvTruckType.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
                    holder.binding.ivTruckType.setImageResource(R.drawable.selected_24dp);

                    break;
                case "Completed":
                    holder.binding.tvTruckType.setText(model.getStatus());
                    holder.binding.tvTruckType.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
                    holder.binding.ivTruckType.setImageResource(R.drawable.selected_24dp);

                    break;

            }
        }
        holder.binding.cvHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (model.getTypes().equalsIgnoreCase("booking")) {

                   mContext.startActivity(new Intent(mContext, ReportIssueDetailsActivity.class)
                                    .putExtra("show", "booking")
                                    .putExtra("fromAddress", (model.getBook_from_address()))
                                    .putExtra("toAddress", (model.getBook_to_address()))
                                    .putExtra("date", (model.getCreate_dt()))
                                    .putExtra("amount", (model.getTrip_price()))
                                    .putExtra("status", model.getStatus())
                                    .putExtra("vehicle_name", model.getVehicle_name())
                       .putExtra("booking_id", model.getBooking_id())
                        .putExtra("comment", model.getComments())


                    );

                }else {
                    mContext.startActivity(new Intent(mContext, ReportIssueDetailsActivity.class)
                            .putExtra("show", "refund")
                            .putExtra("date", (model.getCreate_dt()))
                            .putExtra("amount", (model.getTrip_price()))
                            .putExtra("status",  model.getStatus())
                            .putExtra("order_id", model.getPaytm_orderid())
                            .putExtra("transaction_id",  model.getPaytm_trxid())
                            .putExtra("comment", model.getComments())
                    );

                }


            }
        });


    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    private void showData(ReportIssueAdapter.MyViewHolder holder, boolean transaction) {
        if (transaction) {
            holder.binding.llTransactionId.setVisibility(View.VISIBLE);
            holder.binding.lnrLocation.setVisibility(View.GONE);
            holder.binding.llStatus.setVisibility(View.GONE);
            holder.binding.viewAmount.setVisibility(View.GONE);
            holder.binding.viewStatus.setVisibility(View.GONE);


        } else {
            holder.binding.llTransactionId.setVisibility(View.GONE);
            holder.binding.lnrLocation.setVisibility(View.VISIBLE);
            holder.binding.llStatus.setVisibility(View.VISIBLE);
            holder.binding.viewAmount.setVisibility(View.VISIBLE);
            holder.binding.viewStatus.setVisibility(View.VISIBLE);


        }
    }
}
