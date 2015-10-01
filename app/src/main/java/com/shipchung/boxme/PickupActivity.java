package com.shipchung.boxme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.shipchung.adapter.PickupAdapter;
import com.shipchung.api.GetOrderItemsRequest;
import com.shipchung.api.GetPickupListRequest;
import com.shipchung.bean.LineItem;
import com.shipchung.bean.PickupBean;
import com.shipchung.config.Constants;
import com.shipchung.config.Variables;
import com.shipchung.custom.LoadingDialog;
import com.shipchung.util.Methods;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import boxme.shipchung.com.boxmeapp.R;

/**
 * Created by ToanNB on 7/31/2015.
 */
public class PickupActivity extends Activity implements GetPickupListRequest.GetPickupRequestOnResult,
        AdapterView.OnItemClickListener, GetOrderItemsRequest.GetOrderItemsRequestOnResult {

    private TextView txtPickUpLabel;
    private TextView txtSearchPickup;
    private TextView txtScanUid;
    private ListView mListView;
    private ArrayList<PickupBean> mArrData;
    private PickupAdapter mPickupAdapter;
    private LoadingDialog mLoadingDialog;
    private String mOrderCode= "";
    private String mPickupCode = "";
    private int mStatusCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.putaway_listing_layout);

        mLoadingDialog = new LoadingDialog(this);
        initView();
        getListPickup();
    }

    public void initView() {
        txtPickUpLabel = (TextView) findViewById(R.id.putaway_listing_txt);
        txtSearchPickup = (TextView) findViewById(R.id.search_putaway_txt);
        txtScanUid = (TextView) findViewById(R.id.putaway_mapping_scan_uid_txt);
        txtScanUid.setVisibility(View.GONE);
        txtPickUpLabel.setText(getResources().getString(R.string.pickup_listing_label));
        txtSearchPickup.setHint(getResources().getString(R.string.pickup_search_pickup_listing));

        mArrData = new ArrayList<>();
        mListView = (ListView) findViewById(R.id.putaway_listing_listview);
        mPickupAdapter = new PickupAdapter(this, mArrData);
        mListView.setAdapter(mPickupAdapter);
        mListView.setOnItemClickListener(this);
    }

    Timer timer = null;
    String str = "";
    boolean update;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((int) event.getUnicodeChar() > 0) {
            str += (char) event.getUnicodeChar();
            update = true;
        }

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            super.onBackPressed();
        }

        if (timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {
                    if (!update) {
                        // do somthing here
                        Message message = new Message();
                        message.obj = str;
                        updateHandler.sendMessage(message);
                        str = "";
                        timer.cancel();
                        timer = null;
                    } else {
                        update = false;
                    }
                }
            }, 0, 100);
        }
        if (mPickupAdapter != null) {
            mPickupAdapter.setEnableFocus(false);
        }
        return true;
    }

    Handler updateHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            String sTemp = (String) msg.obj;
            Log.d("test", "a" + sTemp);
            if (sTemp == "") {
                //do nothing
            } else {
                if (sTemp.length() >= 5) {
                    sTemp = sTemp.trim();
                    if (sTemp.substring(0, 2).equalsIgnoreCase("PK") && sTemp.length() > 3) {
                        mPickupCode = sTemp;
                        boolean isHasPickup = false;
                        for (int i = 0; i < mArrData.size(); i++){
                            if (mArrData.get(i).getPickupCode().equalsIgnoreCase(mPickupCode)){
                                isHasPickup = true;
                                Intent intentListOrder = new Intent(getApplicationContext(), PickupListingOrderActivity.class);
                                intentListOrder.putExtra("pickup_code", mPickupCode);
                                startActivity(intentListOrder);
                                finish();
                            }
                        }
                        if (!isHasPickup) {
                            String content = getResources().getString(R.string.error_pickup_not_found);
                            int color = getResources().getColor(R.color.error_color);
                            Methods.alertNotify(PickupActivity.this, content, color);
                        }
                    } else {
                        String content = getResources().getString(R.string.error_pickup_not_pickup);
                        int color = getResources().getColor(R.color.error_color);
                        Methods.alertNotify(PickupActivity.this, content, color);
                    }
                }
            }
            if (mPickupAdapter != null) {
                mPickupAdapter.setEnableFocus(true);
            }
        }
    };

    private void showDialog() {
        if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
    }

    private void hideDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    public void getListPickup() {
//        showDialog();
        GetPickupListRequest pickupListRequest = new GetPickupListRequest();
        pickupListRequest.execute(this, Variables.mUserInfo.getAccessToken());
        pickupListRequest.getPickupRequestOnResult(this);
    }

    @Override
    public void onGetPickupRequestOnResult(boolean result, String data) {
//        hideDialog();
        Log.d("getPickupOnResult", "getPickupOnResult: " + data);
        try {
            JSONObject jsonResultObject = new JSONObject(data);
            int total_item = jsonResultObject.getInt(Constants.TOTAL_ITEM);
            JSONObject jsonObjectEmbeded = jsonResultObject.getJSONObject(Constants.EMBEDDED);
            JSONArray jsonArrayPickup = jsonObjectEmbeded.getJSONArray(Constants.PICKUP);
            int TotalUID, TotalSKU, PickupID, TotalOrder, Status;
            String PickupCode, StatusName, EmployeeName, UpdateTime, AssignName, EmployeeCode, CreateTime;
            for (int i = 0; i < jsonArrayPickup.length(); i++) {
                JSONObject jsonObjectTemp = jsonArrayPickup.getJSONObject(i);
                TotalUID = jsonObjectTemp.getInt(Constants.TOTALUID);
                TotalSKU = jsonObjectTemp.getInt(Constants.TOTALSKU);
                PickupID = jsonObjectTemp.getInt(Constants.PICKUPID);
                PickupCode = jsonObjectTemp.getString(Constants.PICKUPCODE);
                StatusName = jsonObjectTemp.getString(Constants.STATUSNAME);
                TotalOrder = jsonObjectTemp.getInt(Constants.TOTALORDER);
                EmployeeName = jsonObjectTemp.getString(Constants.EMPLOYEENAME);
                Status = jsonObjectTemp.getInt(Constants.STATUS);
                UpdateTime = jsonObjectTemp.getString(Constants.UPDATETIME);
                AssignName = jsonObjectTemp.getString(Constants.ASSIGNNAME);
                EmployeeCode = jsonObjectTemp.getString(Constants.EMPLOYEECODE);
                CreateTime = jsonObjectTemp.getString(Constants.CREATETIME);
                mArrData.add(new PickupBean(TotalUID, TotalSKU, PickupID, PickupCode, StatusName,
                        TotalOrder, EmployeeName, Status, UpdateTime, AssignName, EmployeeCode, CreateTime));
            }
            mPickupAdapter.notifyDataSetChanged();

//            mPickupAdapter = new PickupAdapter(this, mArrData);
//            mListView.setAdapter(mPickupAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void getOrderItemsPickup() {
        showDialog();
        GetOrderItemsRequest orderItemsRequest = new GetOrderItemsRequest();
        orderItemsRequest.execute(this, Variables.mUserInfo.getAccessToken(), mOrderCode);
        orderItemsRequest.getOrderItemsRequestOnResult(this);
    }

    @Override
    public void onGetOrderItemsRequestOnResult(boolean result, String data) {
        hideDialog();
        Log.d("detailPickupOnResult", "getDetailPickupOnResult: " + data);
        Variables.mArrLineItem.clear();
        try {
            JSONObject jsonResult = new JSONObject(data);
            mPickupCode = jsonResult.getString(Constants.PICKUPCODE);
            for (int k = 0; k < mArrData.size(); k++){
                if (mArrData.get(k).getPickupCode().equals(mPickupCode)){
                    if (Variables.mStatusCode == 200) {
                        Intent intent = new Intent(getBaseContext(), PickupMapingActivity.class);
                        intent.putExtra("order_code", mOrderCode);
                        intent.putExtra("pickup_code", mPickupCode);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            JSONArray jsonArrayItem = jsonResult.getJSONArray(Constants.LINEITEM);
            for (int i = 0; i < jsonArrayItem.length(); i++) {
                JSONObject tempJSONObject = jsonArrayItem.getJSONObject(i);
                String orderNumber = tempJSONObject.getString(Constants.ORDERNUMBER);
                String sku = tempJSONObject.getString(Constants.SKU);
                int quantityPickup = tempJSONObject.getInt(Constants.QUANTITYPICKUP);
                int quantity = tempJSONObject.getInt(Constants.QUANTITY);
                String productName = tempJSONObject.getString(Constants.PRODUCTNAME);

                Variables.mArrLineItem.add(new LineItem(orderNumber, sku, quantityPickup, quantity, productName));
            }
//            mAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
        Intent intentListOrder = new Intent(getApplicationContext(), PickupListingOrderActivity.class);
        intentListOrder.putExtra("pickup_code", mArrData.get(pos).getPickupCode());
        startActivity(intentListOrder);
        finish();
    }
}
