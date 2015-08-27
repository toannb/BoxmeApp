package com.shipchung.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shipchung.bean.ChangeLocationBean;

import java.util.ArrayList;

import boxme.shipchung.com.boxmeapp.R;

/**
 * Created by ToanNB on 8/17/2015.
 */
public class ChangeLocationAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ChangeLocationBean> mArrData;
    private LayoutInflater inflater = null;

    public ChangeLocationAdapter(Context context, ArrayList<ChangeLocationBean> arrData){
        this.mContext = context;
        this.mArrData = arrData;
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
    public View getView(int pos, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.put_away_item, viewGroup, false);

            holder.txtPutAwayName = (TextView) convertView.findViewById(R.id.put_away_item_name);
            holder.txtItemAmount = (TextView) convertView.findViewById(R.id.put_away_item_mount_item);
            holder.txtDate = (TextView) convertView.findViewById(R.id.put_away_item_date);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txtPutAwayName.setText(mArrData.get(pos).getUid());
        holder.txtItemAmount.setText(mContext.getResources().getString(R.string.bsin_label)+" "
                + mArrData.get(pos).getBsin() + " - " + mArrData.get(pos).getProductName());
        holder.txtDate.setText(mContext.getString(R.string.binid_change_to) + mArrData.get(pos).getNewBinId());

        return convertView;
    }

    class ViewHolder{
        TextView txtPutAwayName;
        TextView txtItemAmount;
        TextView txtDate;
    }
}
