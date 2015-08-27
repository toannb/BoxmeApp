package com.shipchung.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shipchung.bean.UIDItemBean;
import com.shipchung.boxme.CreateReturnCodeActivity;
import com.shipchung.boxme.CreateReturnCodeUpdatedActivity;
import com.shipchung.config.Variables;

import java.util.ArrayList;

import boxme.shipchung.com.boxmeapp.R;

/**
 * Created by ToanNB on 8/4/2015.
 */
public class UIDITemAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<UIDItemBean> mArrData;
    private LayoutInflater inflater = null;
    private int flag;
    private CreateReturnCodeActivity createReturnCodeActivity;
    private CreateReturnCodeUpdatedActivity createReturnCodeUpdatedActivity;

    public UIDITemAdapter(Context context, ArrayList<UIDItemBean> arrData){
        this.mContext = context;
        this.mArrData = arrData;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public UIDITemAdapter(Context context, ArrayList<UIDItemBean> arrData, int flag){
        this.mContext = context;
        this.mArrData = arrData;
        this.flag = flag;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public UIDITemAdapter(Context context, CreateReturnCodeActivity createReturnCodeActivity,
                          CreateReturnCodeUpdatedActivity createReturnCodeUpdatedActivity,ArrayList<UIDItemBean> arrData, int flag){
        this.mContext = context;
        this.mArrData = arrData;
        this.createReturnCodeActivity = createReturnCodeActivity;
        this.createReturnCodeUpdatedActivity = createReturnCodeUpdatedActivity;
        this.flag = flag;
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
        holder.txtItemAmount.setText(mArrData.get(pos).getProductName());
        holder.txtDate.setText(mContext.getResources().getString(R.string.binid_label) + mArrData.get(pos).getBinID());
        if (flag == 2){
            holder.txtPutAwayName.setText(mArrData.get(pos).getUID());
            holder.txtItemAmount.setText(mArrData.get(pos).getSku() + "-" + mArrData.get(pos).getProductName());
            holder.txtDate.setVisibility(View.GONE);
        }
        if (flag == 3){
            holder.txtPutAwayName.setText(mArrData.get(pos).getUID());
            holder.txtItemAmount.setText(mArrData.get(pos).getStatusReturn() + " - (" + mArrData.get(pos).getProductName() + ")");
            holder.txtDate.setText(mArrData.get(pos).getShipmentCode());
            holder.imgClear.setVisibility(View.VISIBLE);
            holder.imgClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Variables.mArrUidShipment.add(new UIDItemBean(mArrData.get(pos).getSku(),
                            mArrData.get(pos).getUID(), mArrData.get(pos).getProductName()));
                    Variables.mArrUidUpdated.remove(mArrData.get(pos));
                    createReturnCodeUpdatedActivity.txtReturnRemain.setText(String.format
                            (mContext.getResources().getString(R.string.updated_item_return_remain),
                            Variables.mArrUidUpdated.size()));
                    notifyDataSetChanged();
                    createReturnCodeActivity.updateData();
                }
            });
        }

        if (flag == 4){
            holder.txtPutAwayName.setText(mArrData.get(pos).getUID());
            holder.txtItemAmount.setText(mArrData.get(pos).getStatusReturn());
            holder.txtDate.setText(mArrData.get(pos).getProductName());
        }

        if (flag == 5){
            holder.txtPutAwayName.setText(mArrData.get(pos).getUID());
            holder.txtItemAmount.setText(mArrData.get(pos).getStatusReturn() + " - (" + mArrData.get(pos).getProductName() + ")");
            holder.txtDate.setText(mArrData.get(pos).getShipmentCode());
            holder.txtDate.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    class ViewHolder{
        TextView txtPutAwayName;
        TextView txtItemAmount;
        TextView txtDate;
        ImageView imgClear;
    }
}
