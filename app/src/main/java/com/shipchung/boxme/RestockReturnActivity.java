package com.shipchung.boxme;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.costum.android.widget.LoadMoreListView;
import com.shipchung.adapter.UIDITemAdapter;
import com.shipchung.api.GetDetailReturnRequest;
import com.shipchung.api.RestockUidReturnRequest;
import com.shipchung.bean.UIDItemBean;
import com.shipchung.config.Variables;
import com.shipchung.custom.LoadingDialog;
import com.shipchung.util.SwipeDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import boxme.shipchung.com.boxmeapp.R;

/**
 * Created by ToanNB on 8/21/2015.
 */
public class RestockReturnActivity extends Activity implements GetDetailReturnRequest.GetDetailReturnRequestOnResult,
        RestockUidReturnRequest.RestockUidReturnRequestOnResult{

    private TextView txtRestockReturnLabel;
    private TextView txtReturnName;
    private TextView txtRemainOrder;
    private TextView txtScanShipment, txtScanUid, txtScanBinId;
    private LoadMoreListView mListView;
    private String mReturnCode = "";
    private ArrayList<UIDItemBean> mArrUid;
    private UIDITemAdapter mUidAdapter;
    private String uid = "";
    private String binid = "";
    private String shipment_code = "";
    private boolean isUidChanged = false;
    private boolean isBinIdChanged = false;

    private LoadingDialog mLoadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.putaway_mapping_layout);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            mReturnCode = extras.getString("return_code");
        }

        initView();
        mLoadingDialog = new LoadingDialog(this);
        getDetailReturn();
    }

    public void initView(){
        txtRestockReturnLabel = (TextView) findViewById(R.id.putaway_mapping_txt);
        txtReturnName = (TextView) findViewById(R.id.putaway_mapping_packet_name_txt);
        txtRemainOrder = (TextView) findViewById(R.id.putaway_mapping_uid_remaining_txt);
        txtScanShipment = (TextView) findViewById(R.id.scan_location_binid_txt);
        txtScanUid = (TextView) findViewById(R.id.putaway_mapping_scan_uid_item_txt);
        txtScanBinId = (TextView) findViewById(R.id.scan_status_txt);
        mListView = (LoadMoreListView) findViewById(R.id.putaway_mapping_listview);
        mArrUid = new ArrayList<>();
        txtReturnName.setText(mReturnCode);
        txtScanShipment.setVisibility(View.GONE);
        txtScanUid.setHint(getResources().getString(R.string.function_scan_uid));
        txtScanBinId.setHint(getResources().getString(R.string.putaway_mapping_scan_location_binid));
        txtScanBinId.setVisibility(View.VISIBLE);
        txtRestockReturnLabel.setText(getResources().getString(R.string.restock_return_label));

        final SwipeDetector swipeDetector = new SwipeDetector(this, mReturnCode, 7);
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
                sTemp = sTemp.trim();
                if (sTemp.length() >= 8){
                    if (sTemp.contains("-")){
                        binid = sTemp;
                        isBinIdChanged = true;
                        txtScanBinId.setText(sTemp);
                    } else if (sTemp.substring(0, 1).equals("U")){
                        boolean isHasUid = false;
                        for (int i = 0; i < mArrUid.size(); i++){
                            if (mArrUid.get(i).getUID().equals(sTemp)){
                                uid = sTemp;
                                isHasUid = true;
                                isUidChanged = true;
                                shipment_code = mArrUid.get(i).getShipmentCode();
                            }
                        }
                        if (!isHasUid){
                            Toast.makeText(getApplicationContext(), "Item is not found!", Toast.LENGTH_LONG).show();
                        }
                        txtScanUid.setText(sTemp);
                    }
                }

                if (isUidChanged && isBinIdChanged){
                    restockRutrnUid(shipment_code, uid, binid);
                    for (int i = 0; i < mArrUid.size(); i++){
                        if (mArrUid.get(i).getUID().equals(uid)){
                            mArrUid.remove(mArrUid.get(i));
                            mUidAdapter.notifyDataSetChanged();
                        }
                    }
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

    public void getDetailReturn(){
        showDialog();
        GetDetailReturnRequest getDetailReturnRequest = new GetDetailReturnRequest();
        getDetailReturnRequest.execute(this, Variables.mUserInfo.getAccessToken(), mReturnCode);
        getDetailReturnRequest.getDetailReturnRequestOnResult(this);
    }

    @Override
    public void onGetDetailReturnRequestOnResult(boolean result, String data) {
        hideDialog();
        Log.d("getDetailReturn", "getDetailReturnResult: " + data);

        try {
            JSONObject jsonObjectResult = new JSONObject(data);
            JSONArray jsonArrItem = jsonObjectResult.getJSONArray("items");
            mReturnCode = jsonObjectResult.getString("code");
            for (int i = 0; i < jsonArrItem.length(); i++){
                JSONObject jsonObjectTemp = jsonArrItem.getJSONObject(i);
                String sku = jsonObjectTemp.getString("sku");
                String status = jsonObjectTemp.getString("status");
                String product = jsonObjectTemp.getString("product");
                String uid = jsonObjectTemp.getString("uid");
                String shipment_code = jsonObjectTemp.getString("tracking");
                mArrUid.add(new UIDItemBean(sku, uid, status, product, shipment_code));
            }
            txtRemainOrder.setText(String.format(getResources().getString(R.string.restock_remain_order), mArrUid.size()));
            mUidAdapter = new UIDITemAdapter(this, mArrUid, 4);
            mListView.setAdapter(mUidAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void restockRutrnUid(String code, String uid, String binid){
        showDialog();
        RestockUidReturnRequest restockUidReturnRequest = new RestockUidReturnRequest();
        restockUidReturnRequest.execute(this, Variables.mUserInfo.getAccessToken(), code, uid, binid);
        restockUidReturnRequest.restockUidReturnRequestOnResult(this);
    }

    @Override
    public void onRestockUidReturnRequestOnResult(boolean result, String data) {
        hideDialog();
        Log.d("restockUidReturn", "restockUidReturnResult: " + data);

        try {
            JSONObject jsonObjectResult = new JSONObject(data);
            String sku = jsonObjectResult.getString("sku");
            String status = jsonObjectResult.getString("status");
            String uid = jsonObjectResult.getString("uid");
            String newBinID = jsonObjectResult.getString("new");
            String productName = jsonObjectResult.getString("name");

            Variables.mArrUidUpdatedRestockReturn.add(new UIDItemBean(sku, uid, status, productName, newBinID));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
