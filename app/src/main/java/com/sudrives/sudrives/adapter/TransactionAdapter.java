package com.sudrives.sudrives.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.sudrives.sudrives.R;
import com.sudrives.sudrives.model.TransactionModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.RecentViewHolder> {

    private Context context;
    private ArrayList<TransactionModel> list;

    public TransactionAdapter(Context context, ArrayList<TransactionModel> list) {
        this.context = context;
        this.list = list;
    }

    View view;

    @NonNull
    @Override
    public RecentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        view = inflater.inflate(R.layout.item_transaction_amount, viewGroup, false);

        return new RecentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecentViewHolder holder, int position) {
        final TransactionModel transactionModel = list.get(position);

        String upperString = transactionModel.getTransation_type().substring(0, 1).toUpperCase() + transactionModel.getTransation_type().substring(1).toLowerCase();

        holder.date.setText(date(transactionModel.getTransation_date()));
        holder.closingBalance.setText("\u20B9 "+transactionModel.getAfter_transation_amount());

        if (transactionModel.getStatus().equalsIgnoreCase("Credited")) {

            holder.transactionAmount.setText("+" + transactionModel.getTransation_amount());
            holder.transactionAmount.setTextColor(ContextCompat.getColor(context, R.color.colorGreen));
            holder.nameTransaction.setText(upperString);

        } else {

            holder.transactionAmount.setText("-" + transactionModel.getTransation_amount());
            holder.transactionAmount.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
            holder.nameTransaction.setText(upperString);
        }

        String test = transactionModel.getTransation_type();
        char first = test.charAt(0);
        holder.nameFirst.setText(String.valueOf(first));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class RecentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView nameTransaction, transactionAmount, date, closingBalance, nameFirst;


        public RecentViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTransaction = itemView.findViewById(R.id.name_transaction);
            nameFirst = itemView.findViewById(R.id.text_first_transaction);
            transactionAmount = itemView.findViewById(R.id.transaction_amount);
            date = itemView.findViewById(R.id.transaction_time);
            closingBalance = itemView.findViewById(R.id.tv_amount_close);

            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            final TransactionModel transactionModel = list.get(getAdapterPosition());
        }
    }

    public String date(String date1) {

        Date date = null;
        String stringDate = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = formatter.parse(date1);

            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "hh:mm a");
            stringDate = dateFormat.format(date);
            //System.out.println(stringDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return stringDate;


    }
}
