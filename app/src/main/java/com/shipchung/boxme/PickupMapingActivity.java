package com.shipchung.boxme;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.shipchung.adapter.UIDITemAdapter;
import com.shipchung.api.GetDetailPickupRequest;
import com.shipchung.api.MapPickupRequest;
import com.shipchung.bean.UIDItemBean;
import com.shipchung.config.Constants;
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
 * Created by ToanNB on 7/31/2015.
 */
public class PickupMapingActivity extends Activity implements
        GetDetailPickupRequest.GetDetailPickupRequestOnResult,
        MapPickupRequest.MapPickupRequestOnResult {

    private TextView txtheader;
    private TextView txtRemainUidItem;
    private TextView txtScanLocalBinID;
    private TextView txtScanUidItem;
    private TextView txtPickupName;
    private String mPickupCode;
    private ArrayList<UIDItemBean> mArrUIDItem = new ArrayList<>();
    private ListView mListView;
    private UIDITemAdapter mAdapter;

    private LoadingDialog mLoadingDialog;

    private String mUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.putaway_mapping_layout);
        mLoadingDialog = new LoadingDialog(PickupMapingActivity.this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mPickupCode = extras.getString("pickup_code");
        }

        initView();
        getDetailPickup();

    }

    public void initView() {
        txtheader = (TextView) findViewById(R.id.putaway_mapping_txt);
        txtRemainUidItem = (TextView) findViewById(R.id.putaway_mapping_uid_remaining_txt);
        txtScanLocalBinID = (TextView) findViewById(R.id.scan_location_binid_txt);
        txtScanUidItem = (TextView) findViewById(R.id.putaway_mapping_scan_uid_item_txt);
        txtPickupName = (TextView) findViewById(R.id.putaway_mapping_packet_name_txt);
        mListView = (ListView) findViewById(R.id.putaway_mapping_listview);
        mAdapter = new UIDITemAdapter(this, mArrUIDItem);
        mListView.setAdapter(mAdapter);

        Log.d("gettext", "txtUidItem.getText(): " + txtScanUidItem.getText().toString());
        txtScanLocalBinID.setVisibility(View.GONE);
        txtheader.setText("2.2 PICKUP UPDATING");
        txtPickupName.setText(mPickupCode);

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

        if (keyCode == KeyEvent.KEYCODE_BACK){
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
                String first = sTemp.substring(0, 1);
//                try {
//                    int x = Integer.parseInt(first);
//                    txtScanUidItem.setText((String) msg.obj);
//                } catch (Exception e) {
//                    txtScanLocalBINID.setText(sTemp);
//                }
                if (first.equalsIgnoreCase("U")){
                    txtScanUidItem.setText((String) msg.obj);
                    int size = mArrUIDItem.size();
                    for (int i = 0; i <= size - 1; i++){
                        if (mArrUIDItem.get(i).getUID().equals(sTemp)){
                            mapPickup(mArrUIDItem.get(i).getUID(), mArrUIDItem.get(i).getBinID());
                            Variables.mArrUIDPickuped.add(mArrUIDItem.get(i));
                            mArrUIDItem.remove(mArrUIDItem.get(i));
                            mAdapter.notifyDataSetChanged();
                            PickupedActivity pickupedActivity = new PickupedActivity();
                            pickupedActivity.updateData();
                            i--;
                            size--;
                            Toast.makeText(getApplicationContext(), "Pickuped Success!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Pickuped Failed!", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "This isn't UID!", Toast.LENGTH_LONG).show();
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

    private void getDetailPickup() {
        showDialog();
        GetDetailPickupRequest listPickupRequest = new GetDetailPickupRequest();
        listPickupRequest.execute(this, Variables.mUserInfo.getAccessToken(), mPickupCode);
        listPickupRequest.getDetailPickupRequestOnResult(this);
    }

    @Override
    public void onGetDetailPickupRequestOnResult(boolean result, String data) {
        hideDialog();
        Log.d("detailPickupOnResult", "getDetailPickupOnResult: " + data);
        try {
            JSONObject jsonResult = new JSONObject(data);
            int uid_total = jsonResult.getInt(Constants.TOTAL_ITEM);
            int page_count = jsonResult.getInt(Constants.PAGECOUNT);
            int page_size = jsonResult.getInt(Constants.PAGESIZE);
            String pickupCode = jsonResult.getString(Constants.PICKUPCODE);

            String remainItem = getResources().getString(R.string.pickup_mapping_remain);
            txtRemainUidItem.setText(String.format(remainItem, uid_total));

            JSONObject jsonObjectEmbedded = jsonResult.getJSONObject(Constants.EMBEDDED);
            JSONArray jsonArrayItem = jsonObjectEmbedded.getJSONArray(Constants.LINEITEM);
            for (int i = 0; i < jsonArrayItem.length(); i++) {
                JSONObject tempJSONObject = jsonArrayItem.getJSONObject(i);
                int status = tempJSONObject.getInt(Constants.STATUS);
                String bindId = tempJSONObject.getString(Constants.BINID);
                String updateTime = tempJSONObject.getString(Constants.UPDATETIME);
                String uid = tempJSONObject.getString(Constants.UID);
                String productStatusname = tempJSONObject.getString(Constants.PRODUCTSTATUSNAME);
                String bsin = tempJSONObject.getString(Constants.BSIN);
                String statusName = tempJSONObject.getString(Constants.STATUSNAME);
                String order = tempJSONObject.getString(Constants.ORDER);
                int productStatus = tempJSONObject.getInt(Constants.PRODUCTSTATUS);
                String productName = tempJSONObject.getString(Constants.PRODUCTNAME);

                mArrUIDItem.add(new UIDItemBean(status, bindId, updateTime, uid, productStatusname,
                        bsin, statusName, order, productStatus, productName));
            }
//            mAdapter.notifyDataSetChanged();
            mAdapter = new UIDITemAdapter(this, mArrUIDItem);
            mListView.setAdapter(mAdapter);
        } catch (JSONException e) {
            e.fillInStackTrace();
        }
    }

    private void mapPickup(String uid, String binid){
        showDialog();
        MapPickupRequest mapPickupRequest = new MapPickupRequest();
        mapPickupRequest.execute(this, Variables.mUserInfo.getAccessToken(), mPickupCode, uid, binid);
        mapPickupRequest.mapPickupRequestOnResult(this);
    }

    @Override
    public void onMapPickupRequestOnResult(boolean result, String data) {
        hideDialog();
        Log.d("mapPutAwayResult", "mapPutAwayResult: " + data);
    }
}
