package com.shipchung.boxme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.shipchung.adapter.LineItemAdapter;
import com.shipchung.api.GetOrderItemsRequest;
import com.shipchung.api.MapPickupRequest;
import com.shipchung.bean.LineItem;
import com.shipchung.bean.UIDItemBean;
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
public class PickupMapingActivity extends Activity implements
        GetOrderItemsRequest.GetOrderItemsRequestOnResult,
        MapPickupRequest.MapPickupRequestOnResult, View.OnClickListener {

    private TextView txtheader;
    private TextView txtRemainUidItem;
    private TextView txtScanLocalBinID;
    private TextView txtScanUidItem;
    private TextView txtPickupName;
    private String mOrderCode = "";
    private String mPickupCode = "";
    private ArrayList<UIDItemBean> mArrUIDItem = new ArrayList<>();
    private ListView mListView;
    private LineItemAdapter mAdapter;
    private ArrayList<LineItem> mArrData;

    private static LoadingDialog mLoadingDialog;

    private String mUID;
    private Button btnNextPicked;
    private int mTotalUIDPickuped = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.putaway_mapping_layout);
        mLoadingDialog = new LoadingDialog(PickupMapingActivity.this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mOrderCode = extras.getString("order_code");
            mPickupCode = extras.getString("pickup_code");
            mTotalUIDPickuped = extras.getInt("total_uid_pickuped");
        }

        initView();
        getOrderItemsPickup();

    }

    public void initView() {
        mArrData = new ArrayList<>();
        btnNextPicked = (Button) findViewById(R.id.btnNextPutawayed);
        btnNextPicked.setVisibility(View.VISIBLE);
        String strNumberOrder = getResources().getString(R.string.pickup_mapping_btn_next_picked);
        btnNextPicked.setText(String.format(strNumberOrder, mTotalUIDPickuped));
        btnNextPicked.setOnClickListener(this);
        txtheader = (TextView) findViewById(R.id.putaway_mapping_txt);
        txtRemainUidItem = (TextView) findViewById(R.id.putaway_mapping_uid_remaining_txt);
        txtScanLocalBinID = (TextView) findViewById(R.id.scan_location_binid_txt);
        txtScanUidItem = (TextView) findViewById(R.id.putaway_mapping_scan_uid_item_txt);
        txtPickupName = (TextView) findViewById(R.id.putaway_mapping_packet_name_txt);
        mListView = (ListView) findViewById(R.id.putaway_mapping_listview);
        mAdapter = new LineItemAdapter(this, mArrData, 1);
        mListView.setAdapter(mAdapter);

        Log.d("gettext", "txtUidItem.getText(): " + txtScanUidItem.getText().toString());
        txtScanLocalBinID.setVisibility(View.GONE);
        txtheader.setText(getResources().getString(R.string.pickup_updating_label));
        txtPickupName.setText(mOrderCode);
/*
        final SwipeDetector swipeDetector = new SwipeDetector(this, mPickupCode, 3);
        mListView.setOnTouchListener(swipeDetector);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (swipeDetector.swipeDetected()) {
                    // do the onSwipe action
                } else {
                    // do the onItemClick action
                }
            }
        });
        */
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
            Intent intentListOrder = new Intent(getApplicationContext(), PickupListingOrderActivity.class);
            intentListOrder.putExtra("pickup_code", mPickupCode);
            startActivity(intentListOrder);
            finish();
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
                String first = sTemp.substring(0, 1);
                if (first.equalsIgnoreCase("U") && sTemp.length() > 3) {
                    txtScanUidItem.setText((String) msg.obj);
                    String uid = sTemp;
                    mapPickup(uid);
                } else {
                    String content = getResources().getString(R.string.error_not_uid);
                    int color = getResources().getColor(R.color.error_color);
                    Methods.alertNotify(PickupMapingActivity.this, content, color);
                }
            }
        }
    };

    private void showDialog() {
        if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
    }

    public static void hideDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    private void getOrderItemsPickup() {
        showDialog();
        GetOrderItemsRequest orderItemsRequest = new GetOrderItemsRequest();
        orderItemsRequest.execute(this, Variables.mUserInfo.getAccessToken(), mOrderCode);
        orderItemsRequest.getOrderItemsRequestOnResult(this);
    }

    private void mapPickup(String uid) {
        showDialog();
        MapPickupRequest mapPickupRequest = new MapPickupRequest();
        mapPickupRequest.execute(this, Variables.mUserInfo.getAccessToken(), uid, mOrderCode);
        mapPickupRequest.mapPickupRequestOnResult(this);
    }

    @Override
    public void onMapPickupRequestOnResult(boolean result, String data) {
        hideDialog();
        Log.d("mapPutAwayResult", "mapPutAwayResult: " + data);

        if (Variables.mStatusCode == 200) {
            mTotalUIDPickuped++;
            String strNumberOrder = getResources().getString(R.string.pickup_mapping_btn_next_picked);
            btnNextPicked.setText(String.format(strNumberOrder, mTotalUIDPickuped));
            getOrderItemsPickup();
            Toast.makeText(getApplicationContext(), "Pickuped Success!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onGetOrderItemsRequestOnResult(boolean result, String data) {
        hideDialog();
        Log.d("getOrderItemOnResult", "getOrderItemPickupOnResult: " + data);
        try {
            JSONObject jsonResult = new JSONObject(data);
//            mPickupCode = jsonResult.getString(Constants.PICKUPCODE);

            JSONArray jsonArrayItem = jsonResult.getJSONArray(Constants.LINEITEM);
            for (int i = 0; i < jsonArrayItem.length(); i++) {
                JSONObject tempJSONObject = jsonArrayItem.getJSONObject(i);
                String orderNumber = tempJSONObject.getString(Constants.ORDERNUMBER);
                String binID = tempJSONObject.getString("BinId");
                String sku = tempJSONObject.getString(Constants.SKU);
                int quantityPickup = tempJSONObject.getInt(Constants.QUANTITYPICKUP);
                int quantity = tempJSONObject.getInt(Constants.QUANTITY);
                String productName = tempJSONObject.getString(Constants.PRODUCTNAME);
                if (quantity != quantityPickup) {
                    mArrData.add(new LineItem(orderNumber, binID, quantityPickup, quantity, sku, productName));
                }
            }
            txtRemainUidItem.setText(String.format(getResources().getString(R.string.putaway_mapping_remain_uid),
                    mArrData.size()));
            mAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btnNextPutawayed:
                Intent intent = new Intent(getApplicationContext(), PickupedActivity.class);
                intent.putExtra("order_code", mOrderCode);
                intent.putExtra("pickup_code", mPickupCode);
                intent.putExtra("total_uid_pickuped", mTotalUIDPickuped);
                startActivity(intent);
                finish();
                break;
        }
    }
}
