package com.sudrives.sudrives.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sudrives.sudrives.R;
import com.sudrives.sudrives.listeners.RecentSelect;
import com.sudrives.sudrives.model.PromoModel;
import com.sudrives.sudrives.model.RecentBookingModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RecentBookingAdapter extends RecyclerView.Adapter<RecentBookingAdapter.RecentViewHolder> {

    private Context context;
    private ArrayList<RecentBookingModel> list;
    private RecentSelect mListener;

    public RecentBookingAdapter(Context context, ArrayList<RecentBookingModel> list,RecentSelect Listener) {
        this.context = context;
        this.list = list;
        mListener = Listener;
    }

    View view;

    @NonNull
    @Override
    public RecentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        view = inflater.inflate(R.layout.item_recent_booking, viewGroup, false);
        return new RecentViewHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecentViewHolder holder, int position) {
        final RecentBookingModel recentBookingModel = list.get(position);


       // holder.tvSource.setText(recentBookingModel.getBook_from_address());
        holder.tvDestination.setText(recentBookingModel.getBook_to_address());




    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class RecentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvSource,tvDestination;
        RecentSelect mListener;


        public RecentViewHolder(@NonNull View itemView,RecentSelect listener) {
            super(itemView);

            mListener = listener;
           // tvSource = itemView.findViewById(R.id.tv_origin_recent_booking);
            tvDestination = itemView.findViewById(R.id.tv_destination_recent_booking);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            final RecentBookingModel recentBookingModel = list.get(getAdapterPosition());
            mListener.onRecentClick(recentBookingModel.getBook_from_lat(),recentBookingModel.getBook_from_long(),recentBookingModel.getBook_to_lat(),recentBookingModel.getBook_to_long());
        }
    }
}
