package com.sudrives.sudrives.utils.listdialog;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.sudrives.sudrives.R;

import java.util.ArrayList;


/**
 * Created by Nikhat on 10/16/2018.
 */

public class DialogListAdapter extends RecyclerView.Adapter<DialogListAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<DialogListModel> mOptArr;
    dialogItemSelectedInterface dialogListener;

    public DialogListAdapter(Context context, dialogItemSelectedInterface listener) {
        this.mContext = context;
        this.dialogListener = listener;
    }

    public void setList(ArrayList<DialogListModel> optArr) {
        this.mOptArr = new ArrayList<>();
        this.mOptArr = optArr;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_list_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        DialogListModel model = mOptArr.get(position);
        holder.mItemTv.setText(model.name.trim());
        holder.mItemTv.setGravity(Gravity.CENTER);
       // FontLoader.setOpensansRegular(holder.mItemTv);
        if(model.imgId==0)
        {
            holder.mItemIv.setVisibility(View.GONE);
        }else {
            holder.mItemIv.setVisibility(View.VISIBLE);
            holder.mItemIv.setImageResource(model.imgId);
        }

        holder.mRowRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogListener.dialogItem(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mOptArr.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mItemIv;
        TextView mItemTv;
        private LinearLayout mRowRl;

        public ViewHolder(View itemView) {
            super(itemView);
            mItemIv = (ImageView) itemView.findViewById(R.id.img_view);
            mItemTv = (TextView) itemView.findViewById(R.id.item_tv);
            mRowRl = (LinearLayout) itemView.findViewById(R.id.row_rl);

//            FontLoader.setLatoRegularTypeface(mItemTv);
        }
    }

}
