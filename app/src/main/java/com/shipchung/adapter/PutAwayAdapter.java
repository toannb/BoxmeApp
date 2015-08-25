package com.shipchung.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shipchung.bean.PutAwayBean;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import boxme.shipchung.com.boxmeapp.R;

/**
 * Created by ToanNB on 8/4/2015.
 */
public class PutAwayAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<PutAwayBean> mArrData;
    private LayoutInflater inflater = null;

    public PutAwayAdapter(Context context, ArrayList<PutAwayBean> arrData){
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
        holder.txtPutAwayName.setText(mArrData.get(pos).getPutAwayCode());
        String itemAmount = mContext.getResources().getString(R.string.putaway_listitem_amount_item);
        holder.txtItemAmount.setText(String.format(itemAmount, mArrData.get(pos).getNumberUID()));
        String date = mContext.getResources().getString(R.string.putaway_listitem_time);
        String s = mArrData.get(pos).getCreateTime();
        long timeStamp = Long.parseLong(s);
        holder.txtDate.setText(String.format(date, getDate(timeStamp)));

        return convertView;
    }

    private String getDate(long timeStamp){
        try {
            DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        } catch (Exception e){
            return "Fail to convert date";
        }

    }

    class ViewHolder{
        TextView txtPutAwayName;
        TextView txtItemAmount;
        TextView txtDate;
    }
}
