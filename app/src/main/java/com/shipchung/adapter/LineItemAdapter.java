package com.shipchung.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.shipchung.bean.LineItem;

import java.util.ArrayList;

import boxme.shipchung.com.boxmeapp.R;

/**
 * Created by ToanNB on 9/21/2015.
 */
public class LineItemAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<LineItem> mArrLineItem;
    private LayoutInflater inflater = null;
    private int flag = 0;

    public LineItemAdapter(Context context, ArrayList<LineItem> arrLineItem) {
        this.mContext = context;
        this.mArrLineItem = arrLineItem;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public LineItemAdapter(Context context, ArrayList<LineItem> arrLineItem, int flag) {
        this.mContext = context;
        this.mArrLineItem = arrLineItem;
        this.flag = flag;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mArrLineItem.size();
    }

    @Override
    public Object getItem(int i) {
        return mArrLineItem.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.line_item_layout, viewGroup, false);

            holder.txtOrderName = (TextView) convertView.findViewById(R.id.line_item_oder_name);
            holder.txtProductName = (TextView) convertView.findViewById(R.id.line_item_product_name);
            holder.txtSku = (TextView) convertView.findViewById(R.id.line_item_sku);
            holder.txtPickup = (TextView) convertView.findViewById(R.id.line_item_pickup);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (flag == 0) {
            holder.txtOrderName.setText(mArrLineItem.get(pos).getOrderNumber());
            holder.txtProductName.setText(mContext.getResources().getString(R.string.product_name_label) + " " +
                    mArrLineItem.get(pos).getProductName());
            holder.txtSku.setText("BIN ID: " + mArrLineItem.get(pos).getBinID());
            holder.txtPickup.setText("Pickup: " + mArrLineItem.get(pos).getQuantity() + "/" +
                    mArrLineItem.get(pos).getQuantityPickup());
        }
        if (flag == 1) {
            holder.txtPickup.setVisibility(View.GONE);
            holder.txtProductName.setText(mContext.getResources().getString(R.string.product_name_label) + " " +
                    mArrLineItem.get(pos).getProductName());
            holder.txtSku.setText("BIN ID: " + mArrLineItem.get(pos).getBinID());
            holder.txtOrderName.setText(mArrLineItem.get(pos).getQuantityPickup() + "/" +
                    mArrLineItem.get(pos).getQuantity() + " x " + "BSIN " + mArrLineItem.get(pos).getBsin());
        }
        return convertView;
    }

    class ViewHolder {
        private TextView txtOrderName;
        private TextView txtProductName;
        private TextView txtSku;
        private TextView txtPickup;
    }
}
