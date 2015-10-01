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
import android.widget.Button;
import android.widget.TextView;

import com.costum.android.widget.LoadMoreListView;
import com.shipchung.adapter.LineItemAdapter;
import com.shipchung.api.GetListOrdersRequest;
import com.shipchung.bean.LineItem;
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
 * Created by ToanNB on 9/28/2015.
 */
public class PickupListingOrderActivity extends Activity implements View.OnClickListener,
        GetListOrdersRequest.GetListOrdersRequestOnResult, AdapterView.OnItemClickListener {

    private TextView txtPickupLabel;
    private TextView txtPickupCode;
    private TextView txtRemainUid;
    private TextView txtScanOrder;
    private TextView txtScanUid;
    private Button btnNext;
    private LoadMoreListView mListView;
    private LineItemAdapter mAdapter;
    private ArrayList<LineItem> mArrData;
    private LoadingDialog mLoadingDialog;
    private String mPickupCode = "";
    private String mBinID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.putaway_mapping_layout);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mPickupCode = extras.getString("pickup_code");
        }
        initView();
        getListOrdersPickup();
    }

    public void initView() {
        mLoadingDialog = new LoadingDialog(this);
        txtPickupLabel = (TextView) findViewById(R.id.putaway_mapping_txt);
        txtPickupLabel.setText(getResources().getString(R.string.pickup_updating_label));
        txtScanOrder = (TextView) findViewById(R.id.scan_location_binid_txt);
        txtScanOrder.setHint(getResources().getString(R.string.pickup_search_order));
        txtScanUid = (TextView) findViewById(R.id.putaway_mapping_scan_uid_item_txt);
        txtScanUid.setVisibility(View.GONE);
        txtPickupCode = (TextView) findViewById(R.id.putaway_mapping_packet_name_txt);
        txtPickupCode.setText(mPickupCode);
        txtRemainUid = (TextView) findViewById(R.id.putaway_mapping_uid_remaining_txt);

        btnNext = (Button) findViewById(R.id.btnNextPutawayed);
        btnNext.setOnClickListener(this);

        mListView = (LoadMoreListView) findViewById(R.id.putaway_mapping_listview);
        mArrData = new ArrayList<>();
        mAdapter = new LineItemAdapter(this, mArrData);
        mListView.setAdapter(mAdapter);
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
            Intent intentPickupMapping = new Intent(getApplicationContext(), PickupActivity.class);
            startActivity(intentPickupMapping);
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
                if (first.equalsIgnoreCase("O") && sTemp.length() > 3) {
                    txtScanOrder.setText((String) msg.obj);
                    Intent intentMapping = new Intent(getApplicationContext(), PickupMapingActivity.class);
                    intentMapping.putExtra("order_code", sTemp);
                    intentMapping.putExtra("pickup_code", mPickupCode);
                    startActivity(intentMapping);
                    finish();
                } else {
                    String content = getResources().getString(R.string.error_not_order);
                    int color = getResources().getColor(R.color.error_color);
                    Methods.alertNotify(PickupListingOrderActivity.this, content, color);
                }
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

    private void getListOrdersPickup() {
        showDialog();
        GetListOrdersRequest listOrdersRequest = new GetListOrdersRequest();
        listOrdersRequest.execute(this, Variables.mUserInfo.getAccessToken(), mPickupCode);
        listOrdersRequest.getListOrdersRequestOnResult(this);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnNextPutawayed:
//                Intent
                break;
        }
    }


    @Override
    public void onGetListOrdersRequestOnResult(boolean result, String data) {
        Log.d("list_order", "getListOrdersOnResult: " + data);
        try {
            int total = 0;
            JSONObject jsonResult = new JSONObject(data);
            JSONArray list = jsonResult.getJSONArray("List");
            for (int j = 0; j < list.length(); j++) {
                JSONObject tempJSONObjectList = list.getJSONObject(j);
                total = (int) tempJSONObjectList.getLong(Constants.TOTAL);
                JSONArray lineItems = tempJSONObjectList.getJSONArray(Constants.LINEITEM);
                for (int i = 0; i < lineItems.length(); i++) {
                    JSONObject tempObject = lineItems.getJSONObject(i);
                    int quantityOrder = tempObject.getInt(Constants.QUANTITYORDER);
                    String binID = tempObject.getString("BinId");
                    String bsin = tempObject.getString(Constants.BSIN);
                    String productName = tempObject.getString(Constants.PRODUCTNAME);
                    String order = tempObject.getString(Constants.ORDER);
                    int quantity = tempObject.getInt(Constants.QUANTITY);
                    int status = tempObject.getInt(Constants.STATUS);
                    if (status == 0) {
                    mArrData.add(new LineItem(order, binID, quantityOrder, quantity, bsin, productName));
                    }
                }
            }
            String remain = getResources().getString(R.string.pickup_order_remain);
            txtRemainUid.setText(String.format(remain, mArrData.size(), total));
            mAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        hideDialog();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intentPickupMapping = new Intent(getApplicationContext(), PickupMapingActivity.class);
        intentPickupMapping.putExtra("order_code", mArrData.get(i).getOrderNumber());
        intentPickupMapping.putExtra("pickup_code", mPickupCode);
        intentPickupMapping.putExtra("binid", mArrData.get(i).getBinID());
        startActivity(intentPickupMapping);
        finish();
    }
}
