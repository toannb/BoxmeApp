package com.shipchung.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shipchung.bean.UIDItemBean;
import com.shipchung.boxme.PickupedActivity;

import java.util.ArrayList;

import boxme.shipchung.com.boxmeapp.R;

/**
 * Created by ToanNB on 8/10/2015.
 */
public class PickupedUIDItemAdapter extends BaseAdapter{
    private Context mContext;
    private ArrayList<UIDItemBean> mArrData;
    private LayoutInflater inflater = null;
    private PickupedActivity pickupedActivity;

    public PickupedUIDItemAdapter(Context context, PickupedActivity pickupedActivity, ArrayList<UIDItemBean> arrData){
        this.mContext = context;
        this.mArrData = arrData;
        this.pickupedActivity = pickupedActivity;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mArrData.size();
    }

    @Override
    public Object getItem(int i) {
        return mArrData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int pos, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.put_away_item, viewGroup, false);

            holder.txtPutAwayName = (TextView) convertView.findViewById(R.id.put_away_item_name);
            holder.txtItemAmount = (TextView) convertView.findViewById(R.id.put_away_item_mount_item);
            holder.txtDate = (TextView) convertView.findViewById(R.id.put_away_item_date);
            holder.imgClear = (ImageView) convertView.findViewById(R.id.clear_icon);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txtPutAwayName.setText(mArrData.get(pos).getUID());
        holder.txtPutAwayName.setTextColor(mContext.getResources().getColor(R.color.tree));
        holder.txtItemAmount.setText(mContext.getResources().getString(R.string.bsin_label) + " " + mArrData.get(pos).getBSIN() + " - "
                +mArrData.get(pos).getProductName());
        holder.txtDate.setText(mContext.getResources().getString(R.string.picked_upcase_label));
        holder.imgClear.setVisibility(View.VISIBLE);
        holder.imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickupedActivity.undoPickup(mArrData.get(pos).getUID(), pos);
                PickupedActivity.txtRemainItemPickuped.setText(
                        String.format(mContext.getResources().getString(R.string.putawayed_remain_item_pickuped),
                                mArrData.size()));
            }
        });

        return convertView;
    }

    class ViewHolder{
        TextView txtPutAwayName;
        TextView txtItemAmount;
        TextView txtDate;
        ImageView imgClear;
    }
}
