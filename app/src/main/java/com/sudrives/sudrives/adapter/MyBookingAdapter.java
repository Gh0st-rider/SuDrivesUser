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
import com.sudrives.sudrives.activity.BookingDetailActivity;

import com.sudrives.sudrives.activity.ReportIssueActivity;
import com.sudrives.sudrives.databinding.ItemMyBookingBinding;
import com.sudrives.sudrives.model.HistoryListModel;
import com.sudrives.sudrives.utils.DateUtil;
import com.sudrives.sudrives.utils.FontLoader;
import com.sudrives.sudrives.utils.GlobalUtil;

import java.util.ArrayList;
import java.util.List;

public class MyBookingAdapter extends RecyclerView.Adapter<MyBookingAdapter.MyViewHolder> {

    private List<HistoryListModel> mList;
    private Context mContext;
    private MyBookingAdapter.MyViewHolder holder;

    public MyBookingAdapter(Context context) {
        this.mContext = context;
    }


    public void setList(ArrayList<HistoryListModel> list) {
        this.mList = new ArrayList<>();
        this.mList = list;

        notifyDataSetChanged();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        private ItemMyBookingBinding binding;

        MyViewHolder(View view) {
            super(view);
            binding = DataBindingUtil.bind(view);
        }
    }

    @Override
    public MyBookingAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_booking, parent, false);

        holder = new MyBookingAdapter.MyViewHolder(v);

        return holder;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(MyBookingAdapter.MyViewHolder holder, int position) {
        final HistoryListModel model = mList.get(position);


        if (model.getType_of_booking().equals("0") || model.getType_of_booking().equals("2")) {

            holder.binding.rlsourceLocation.setVisibility(View.GONE);
            holder.binding.lnrBookingDetailsLocation.setVisibility(View.VISIBLE);


        } else {
            holder.binding.rlsourceLocation.setVisibility(View.VISIBLE);
            holder.binding.lnrBookingDetailsLocation.setVisibility(View.GONE);

        }


        holder.binding.tvOriginMyBooking.setText(model.getBook_from_address());
        holder.binding.tvDestinationMyBooking.setText(model.getBook_to_address());

        holder.binding.sourceLocationLayout.etSourceAddress.setText(model.getBook_from_address());


        if (GlobalUtil.getDate(Long.parseLong(model.getBooking_date()), "dd-MMM-yyyy").equalsIgnoreCase(DateUtil.getCurrentDateTime("dd-MMM-yyyy"))) {
            holder.binding.tvDeliveryDateVal.setText(mContext.getString(R.string.today) + "," + GlobalUtil.getDate(Long.parseLong(model.getBooking_date()), "hh:mm a"));

        } else {
            holder.binding.tvDeliveryDateVal.setText(GlobalUtil.getDate(Long.parseLong(model.getBooking_date()), "dd-MMM-yyyy,hh:mm a"));

        }


        if (model.getVehicle_name().equalsIgnoreCase("null")) {

            holder.binding.tvAmount.setText("Request not accepted");

        } else {

            holder.binding.tvAmount.setText(Html.fromHtml("\u20B9 " + model.getTotal_fare()));

        }

        if (!model.getVehicle_name().equals("null") && model.getVehicle_name() != null && !model.getVehicle_name().isEmpty()) {

            holder.binding.tvTruckType.setText(model.getVehicle_name());

        } else {

            holder.binding.tvTruckType.setText("Not Accepted");

        }


        switch (model.getBooking_status()) {
            case "1":
                holder.binding.llStatusMyBooking.setVisibility(View.VISIBLE);
                holder.binding.tvStatus.setText(model.getStatus_name());
                holder.binding.tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
                holder.binding.ivStatus.setImageResource(R.drawable.selected_24dp);
                holder.binding.ivStatusCancel.setVisibility(View.GONE);


                break;
            case "2":
                holder.binding.llStatusMyBooking.setVisibility(View.VISIBLE);
                holder.binding.ivStatusCancel.setVisibility(View.GONE);
                holder.binding.tvStatus.setText(model.getStatus_name());
                holder.binding.tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorGrayDark));
                holder.binding.ivStatus.setImageResource(R.drawable.car_placeholder);


                break;
            case "3":

                holder.binding.llStatusMyBooking.setVisibility(View.VISIBLE);
                holder.binding.ivStatusCancel.setVisibility(View.GONE);
                holder.binding.tvStatus.setText(model.getStatus_name());
                holder.binding.tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
                holder.binding.ivStatus.setImageResource(R.drawable.selected_24dp);

                break;
            case "4":
                holder.binding.ivStatusCancel.setVisibility(View.VISIBLE);
                holder.binding.llStatusMyBooking.setVisibility(View.GONE);
                holder.binding.tvStatus.setText(model.getStatus_name());
                holder.binding.tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorRed));


                break;
            case "5":
                holder.binding.ivStatusCancel.setVisibility(View.VISIBLE);
                holder.binding.llStatusMyBooking.setVisibility(View.GONE);
                holder.binding.tvStatus.setText(model.getStatus_name());
                holder.binding.tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorRed));


                break;
            case "6":
                holder.binding.ivStatusCancel.setVisibility(View.GONE);
                holder.binding.llStatusMyBooking.setVisibility(View.VISIBLE);
                holder.binding.tvStatus.setText(model.getStatus_name());
                holder.binding.tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorYellow));
                holder.binding.ivStatus.setImageResource(R.drawable.pending_24dp);

                break;
            case "7":
                holder.binding.ivStatusCancel.setVisibility(View.VISIBLE);
                holder.binding.llStatusMyBooking.setVisibility(View.GONE);
                holder.binding.tvStatus.setText(model.getStatus_name());
                holder.binding.tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorRed));


                break;

            case "8":
                holder.binding.ivStatusCancel.setVisibility(View.GONE);
                holder.binding.tvStatus.setText(model.getStatus_name());
                holder.binding.tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
                holder.binding.ivStatus.setImageResource(R.drawable.selected_24dp);

                break;

        }
        if (model.getBooking_status().equals("4") || model.getBooking_status().equals("5") || model.getBooking_status().equals("7")) {
            holder.binding.tvReportIssue.setVisibility(View.GONE);
            holder.binding.vBooking.setVisibility(View.GONE);


        } else {
            holder.binding.tvReportIssue.setVisibility(View.VISIBLE);
            holder.binding.vBooking.setVisibility(View.VISIBLE);

        }

        holder.binding.tvReportIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, ReportIssueActivity.class)
                        .putExtra("booking_id", model.getBooking_id())
                        .putExtra("trip_id", model.getId())

                );

            }
        });

        holder.binding.tvBookingDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mContext.startActivity(new Intent(mContext, BookingDetailActivity.class)
                        .putExtra("vehicleType", (model.getVehicle_name()))
                        .putExtra("driver_fname", model.getFirstname())
                        .putExtra("driver_lname", model.getLastname())
                        .putExtra("payment_mode", model.getPayment_mode())
                        .putExtra("driver_rating", model.getRating())
                        .putExtra("from_address", model.getBook_from_address())
                        .putExtra("to_address", model.getBook_to_address())
                        .putExtra("latFrom", (model.getBook_from_lat()))
                        .putExtra("longFrom", (model.getBook_from_long()))
                        .putExtra("latTo", (model.getBook_to_lat()))
                        .putExtra("longTo", (model.getBook_to_long()))
                        .putExtra("time", holder.binding.tvDeliveryDateVal.getText().toString())
                        .putExtra("booking_id", model.getBooking_id())
                        .putExtra("mobile", model.getMobile())
                        .putExtra("amount", model.getTotal_fare())
                        .putExtra("status", model.getBooking_status())
                        .putExtra("cancelbtnstatus", "false")
                        .putExtra("cancel_charge", model.getCancel_charge())
                        .putExtra("arrived", model.getDriver_arrived_pickup())
                        .putExtra("total_charge", model.getTotal_fare())
                        .putExtra("status_name", model.getStatus_name())
                        .putExtra("discount_amt", model.getDiscount_amount())
                        .putExtra("final_amt", model.getFinal_amount())
                        .putExtra("invoice_link", model.getInvoice_link())
                        .putExtra("sgst", model.getSgst())
                        .putExtra("cgst", model.getCgst())
                        .putExtra("tripid", model.getId())

                );
            }
        });


    }


    @Override
    public int getItemCount() {

        return mList.size();
    }
}
