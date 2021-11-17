package com.sudrives.sudrives.adapter;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sudrives.sudrives.R;
import com.sudrives.sudrives.databinding.ItemSendRideStatusBinding;
import com.sudrives.sudrives.model.ContactBean;
import com.sudrives.sudrives.utils.FontLoader;

import java.util.ArrayList;
import java.util.List;

public class SendRideStatusAdapter extends RecyclerView.Adapter<SendRideStatusAdapter.MyViewHolder> {


    private Context mContext;
    private List<ContactBean> mlist = new ArrayList<>();
    private List<ContactBean> mlistFiltered = new ArrayList<>();
    private SendRideStatusAdapter.AdapterCallback adapterCallback;
    int maxCount = 3, count = 0;


    public SendRideStatusAdapter(Context mContext, List<ContactBean> mlist, SendRideStatusAdapter.AdapterCallback adapterCallback) {
        this.mContext = mContext;
        this.mlist = mlist;
        this.mlistFiltered.addAll(mlist);
        this.adapterCallback = adapterCallback;

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_send_ride_status, parent, false);
        MyViewHolder holder = new SendRideStatusAdapter.MyViewHolder(itemView);
        return holder;

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final ContactBean model = mlist.get(position);

        holder.binding.tvName.setText(model.getName());
        holder.binding.tvMobileNo.setText(model.getPhone_number());


        if (model.getIsChecked().equals("0")) {
            holder.binding.checkbox.setChecked(false);


        } else {
            holder.binding.checkbox.setChecked(true);
        }
        holder.binding.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.binding.checkbox.isChecked()) {
                    if (model.getIsChecked().equals("0")) {
                        adapterCallback.onClick(model.getName(), model.getPhone_number(), true);
                        model.setIsChecked("1");
                        count++;
                    }
                } else {
                    if (model.getIsChecked().equals("1")) {
                        adapterCallback.onClick(model.getName(), model.getPhone_number(), false);
                        model.setIsChecked("0");
                        count--;
                    }
                }
                if (count > maxCount) {
                    holder.binding.checkbox.setChecked(false);
                    count = maxCount;
                    Toast.makeText(mContext, R.string.you_can_select_max_3_contacts, Toast.LENGTH_SHORT).show();
                    if (model.getIsChecked().equals("1")) {
                        model.setIsChecked("0");

                    }
                }

            }
        });


    }


    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public void setDataIntoList(List<ContactBean> contactList) {
        this.mlist = contactList;
        this.mlistFiltered.addAll(mlist);

        notifyDataSetChanged();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ItemSendRideStatusBinding binding;

        public MyViewHolder(View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
            FontLoader.setHelRegular(binding.tvName, binding.tvMobileNo);


        }
    }

    public interface AdapterCallback {
        public void onClick(String name, String number, boolean check);
    }


    /**
     * Help and support Search functionality
     */
    public void searchHelpAndSupport(String searchText) {
        mlist.clear();

        if (searchText.isEmpty()) {
            mlist.addAll(mlistFiltered);
        } else {
            searchText = searchText.toLowerCase();
            try {


                for (ContactBean item : mlistFiltered) {
                    if (item.getName() != null && item.getPhone_number() != null) {
                        if (item.getName().toLowerCase().contains(searchText) || item.getPhone_number().toLowerCase().contains(searchText)) {
                            mlist.add(item);

                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        notifyDataSetChanged();
    }


}
