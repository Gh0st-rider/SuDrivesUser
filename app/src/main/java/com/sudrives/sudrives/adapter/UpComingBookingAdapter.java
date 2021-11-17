package com.sudrives.sudrives.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.sudrives.sudrives.R;
import com.sudrives.sudrives.activity.BookingDetailActivity;
import com.sudrives.sudrives.databinding.ItemUpcomingMyBookingBinding;
import com.sudrives.sudrives.fragment.HomeLocationSelectFragment;
import com.sudrives.sudrives.fragment.LiveFragment;
import com.sudrives.sudrives.model.HistoryListModel;
import com.sudrives.sudrives.utils.DateUtil;
import com.sudrives.sudrives.utils.GlobalUtil;

import java.util.ArrayList;
import java.util.List;

public class UpComingBookingAdapter extends RecyclerView.Adapter<UpComingBookingAdapter.MyViewHolder> {
    private List<HistoryListModel> mList;
    private Context mContext;
    private MyViewHolder holder;

    public UpComingBookingAdapter(Context context) {
        this.mContext = context;
    }


    public void setList(ArrayList<HistoryListModel> list) {
        this.mList = new ArrayList<>();
        this.mList = list;

        notifyDataSetChanged();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_upcoming_my_booking, parent, false);

        holder = new MyViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
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

        if (model.getBooking_type().equals("0")) {
            if (GlobalUtil.getDate(Long.parseLong(model.getBooking_date()), "dd-MMM-yyyy").equalsIgnoreCase(DateUtil.getCurrentDateTime("dd-MMM-yyyy"))) {
                holder.binding.tvDeliveryDateVal.setText(mContext.getString(R.string.today) + "," + GlobalUtil.getDate(Long.parseLong(model.getBooking_date()), "hh:mm a"));

            } else {
                holder.binding.tvDeliveryDateVal.setText(GlobalUtil.getDate(Long.parseLong(model.getBooking_date()), "dd-MMM-yyyy,hh:mm a"));

            }
        } else {
            if (GlobalUtil.getDate(Long.parseLong(model.getBook_later_date_time()), "dd-MMM-yyyy").equalsIgnoreCase(DateUtil.getCurrentDateTime("dd-MMM-yyyy"))) {
                holder.binding.tvDeliveryDateVal.setText(mContext.getString(R.string.today) + "," + GlobalUtil.getDate(Long.parseLong(model.getBook_later_date_time()), "hh:mm a"));

            } else {
                holder.binding.tvDeliveryDateVal.setText(GlobalUtil.getDate(Long.parseLong(model.getBook_later_date_time()), "dd-MMM-yyyy,hh:mm a"));

            }
        }


        if (!model.getVehicle_name().equals("null") && model.getVehicle_name() != null && !model.getVehicle_name().isEmpty()) {

            holder.binding.tvTruckType.setText(model.getVehicle_name());

        } else {

            holder.binding.tvTruckType.setText("Not Accepted");

        }


        holder.binding.tvOtpHistoryUpcoming.setText("OTP: "+model.getStart_otp());


        switch (model.getBooking_status()) {
            case "1":
                holder.binding.tvStatus.setText(model.getStatus_name());
                holder.binding.tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
                holder.binding.ivStatus.setImageResource(R.drawable.selected_24dp);
                holder.binding.tvOtpHistoryUpcoming.setVisibility(View.VISIBLE);

                break;
            case "2":
                holder.binding.tvStatus.setText(model.getStatus_name());
                holder.binding.tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorGrayDark));
                holder.binding.ivStatus.setImageResource(R.drawable.car_placeholder);
                holder.binding.tvOtpHistoryUpcoming.setVisibility(View.GONE);

                break;
            case "3":
                holder.binding.tvStatus.setText(model.getStatus_name());
                holder.binding.tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorGreen));
                holder.binding.ivStatus.setImageResource(R.drawable.selected_24dp);
                holder.binding.tvOtpHistoryUpcoming.setVisibility(View.GONE);

                break;
            case "4":
                holder.binding.tvStatus.setText(model.getStatus_name());
                holder.binding.tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorRed));
                holder.binding.tvOtpHistoryUpcoming.setVisibility(View.GONE);
                //  holder.binding.ivStatus.setImageResource(R.mipmap.shape);

                break;
            case "5":
                holder.binding.tvStatus.setText(model.getStatus_name());
                holder.binding.tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorRed));
                holder.binding.tvOtpHistoryUpcoming.setVisibility(View.GONE);
                // holder.binding.ivStatus.setImageResource(R.mipmap.shape);

                break;
            case "6":
                holder.binding.tvStatus.setText(model.getStatus_name());
                holder.binding.tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
                holder.binding.ivStatus.setImageResource(R.drawable.pending_24dp);
                holder.binding.tvOtpHistoryUpcoming.setVisibility(View.VISIBLE);

                break;
            case "7":
                holder.binding.tvStatus.setText(model.getStatus_name());
                holder.binding.tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorRed));
                holder.binding.tvOtpHistoryUpcoming.setVisibility(View.GONE);
                //   holder.binding.ivStatus.setImageResource(R.mipmap.shape);

                break;

            case "8":
                holder.binding.tvStatus.setText(model.getStatus_name());
                holder.binding.tvStatus.setTextColor(mContext.getResources().getColor(R.color.colorRed));
                holder.binding.ivStatus.setImageResource(R.drawable.selected_24dp);
                holder.binding.tvOtpHistoryUpcoming.setVisibility(View.VISIBLE);

                break;


        }


        /*holder.binding.ivGoLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LiveFragment fragmentB=new LiveFragment();
                FragmentManager manager=  ((AppCompatActivity)mContext).getSupportFragmentManager();
                FragmentTransaction transaction=manager.beginTransaction();
                transaction.replace(R.id.frame_content,fragmentB).commit();

            }
        });*/


        // FontLoader.setHelRegular(holder.binding.tvOriginMyBooking, holder.binding.tvDestinationMyBooking, holder.binding.tvStatus, holder.binding.tvTruckType, holder.binding.tvDeliveryDateVal, holder.binding.tvBookingDetails);
        //  FontLoader.setHelBold(holder.binding.tvAmount);
        holder.binding.tvBookingDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //holder.binding.tvBookingDetails.setEnabled(false);
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
                        .putExtra("cancelbtnstatus", "true")
                        .putExtra("amount", model.getTotal_fare())
                        .putExtra("status", model.getBooking_status())
                        .putExtra("arrived", model.getDriver_arrived_pickup())
                        .putExtra("cancel_charge", model.getCancel_charge())
                        .putExtra("total_charge", model.getTotal_fare())
                        .putExtra("tripId", model.getId())
                        .putExtra("status_name", model.getStatus_name())
                        .putExtra("discount_amt", model.getDiscount_amount())
                        .putExtra("final_amt", model.getFinal_amount())
                        .putExtra("invoice_link", model.getInvoice_link())
                        .putExtra("sgst", model.getSgst())
                        .putExtra("cgst", model.getCgst())
                        .putExtra("tripid", model.getId())
                );
                // holder.binding.tvBookingDetails.setEnabled(true);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ItemUpcomingMyBookingBinding binding;

        public MyViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
