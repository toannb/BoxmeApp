package com.shipchung.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shipchung.bean.PickupBean;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import boxme.shipchung.com.boxmeapp.R;

/**
 * Created by ToanNB on 8/13/2015.
 */
public class PickupAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<PickupBean> mArrData;
    private LayoutInflater inflater = null;
    private boolean enableFocus = true;

    public PickupAdapter(Context context, ArrayList<PickupBean> arrData){
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

            holder.txtPickupCode = (TextView) convertView.findViewById(R.id.put_away_item_name);
            holder.txtItemAmount = (TextView) convertView.findViewById(R.id.put_away_item_mount_item);
            holder.txtDate = (TextView) convertView.findViewById(R.id.put_away_item_date);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txtPickupCode.setText(mArrData.get(pos).getPickupCode());
        String itemAmount = mContext.getResources().getString(R.string.putaway_listitem_amount_item) + " "+ mContext.getResources().getString(R.string.picked_label);
        holder.txtItemAmount.setText(String.format(itemAmount, mArrData.get(pos).getTotalUID()));
        String date = mContext.getResources().getString(R.string.putaway_listitem_time);
        String s = mArrData.get(pos).getCreateTime();
        long timeStamp = Long.parseLong(s);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        String createTime = getDate(timeStamp);
        String strDiff = "";
        try {
            Date date1 = sdf.parse(currentDateandTime);
            Date date2 = sdf.parse(createTime);
            long diff = date1.getTime() - date2.getTime();
            long diffInMinute = diff / 60000;
            int day;
            if (diffInMinute < 60){
                strDiff = diffInMinute + mContext.getResources().getString(R.string.minute_label);
            } else if (diffInMinute >= 60){
                int diffInHour = (int) diffInMinute / 60;
                int minute = (int) diffInMinute - diffInHour * 60;
                if (minute == 0){
                    strDiff = diffInHour + mContext.getResources().getString(R.string.hour_label);
                } else if (minute > 0){
                    strDiff = diffInHour + mContext.getResources().getString(R.string.hour_label)
                            + minute + mContext.getResources().getString(R.string.minute_label);
                }
            }
        } catch (ParseException e) {
            Log.d("time_error", "Fail to convert time");
            e.printStackTrace();
        }
        holder.txtDate.setText(String.format(date, strDiff));

        return convertView;
    }

    private String getDate(long timeStamp){
        try {
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        } catch (Exception e){
            return "Fail to convert date";
        }

    }

    @Override
    public boolean isEnabled(int position) {
        return enableFocus;
    }

    class ViewHolder{
        TextView txtPickupCode;
        TextView txtItemAmount;
        TextView txtDate;
    }
    public void setEnableFocus(boolean enableFocus){
        this.enableFocus = enableFocus;
    }
}
