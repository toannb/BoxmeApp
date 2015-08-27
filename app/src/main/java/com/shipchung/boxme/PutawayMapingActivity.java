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

import com.shipchung.adapter.UIDITemAdapter;
import com.shipchung.api.GetDetailPutAwayRequest;
import com.shipchung.api.MapPutawayRequest;
import com.shipchung.bean.UIDItemBean;
import com.shipchung.config.Constants;
import com.shipchung.config.Variables;
import com.shipchung.custom.LoadingDialog;
import com.shipchung.util.Methods;
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
public class PutawayMapingActivity extends Activity implements
        GetDetailPutAwayRequest.GetDetailPutAwayRequestOnResult,
        MapPutawayRequest.MapPutAwayRequestOnResult {

    private TextView txtRemainUidItem;
    private TextView txtScanLocalBINID;
    private TextView txtScanUidItem;
    private TextView txtPutawayName;
    private String mCodePutaway;
    private ArrayList<UIDItemBean> mArrUIDItem = new ArrayList<>();
    private ListView mListView;
    private UIDITemAdapter mAdapter;

    private LoadingDialog mLoadingDialog;

    private String mUID;
    private String priviousBinID = "";
    private String priviousUid = "";
    private boolean isBinIdChanged = false;
    private boolean isUidChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.putaway_mapping_layout);
        mLoadingDialog = new LoadingDialog(PutawayMapingActivity.this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mCodePutaway = extras.getString("code_putaway");
        }

        initView();
        getDetailPutAway();

    }

    public void initView() {
        txtRemainUidItem = (TextView) findViewById(R.id.putaway_mapping_uid_remaining_txt);
        txtScanLocalBINID = (TextView) findViewById(R.id.scan_location_binid_txt);
        txtScanUidItem = (TextView) findViewById(R.id.putaway_mapping_scan_uid_item_txt);
        txtPutawayName = (TextView) findViewById(R.id.putaway_mapping_packet_name_txt);
        mListView = (ListView) findViewById(R.id.putaway_mapping_listview);
        mAdapter = new UIDITemAdapter(this, mArrUIDItem);
        mListView.setAdapter(mAdapter);
        Log.d("gettext", "txtSccanLocalBINID.getText(): " + txtScanLocalBINID.getText().toString());
        Log.d("gettext", "txtUidItem.getText(): " + txtScanUidItem.getText().toString());


        txtPutawayName.setText(mCodePutaway);

        final SwipeDetector swipeDetector = new SwipeDetector(this, mCodePutaway, 0);
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
                String first = sTemp.substring(0, 1);
//                try {
//                    int x = Integer.parseInt(first);
//                    txtScanUidItem.setText((String) msg.obj);
//                } catch (Exception e) {
//                    txtScanLocalBINID.setText(sTemp);
//                }
                if (first.equalsIgnoreCase("U") && sTemp.length() > 1) {
                    txtScanUidItem.setText((String) msg.obj);
                } else if (sTemp.length() >= 8) {
                    if (sTemp.contains("-")) {
                        txtScanLocalBINID.setText((String) msg.obj);
                    }
                } else {
                    String content = getResources().getString(R.string.error_putaway_mapping_not_binid_uidy);
                    int color = getResources().getColor(R.color.error_color);
                    Methods.alertNotify(PutawayMapingActivity.this, content, color);
                }

                Log.d("gettext1", "txtSccanLocalBINID.getText(): " + txtScanLocalBINID.getText().toString());
                Log.d("gettext1", "txtUidItem.getText(): " + txtScanUidItem.getText().toString());
                String binid = txtScanLocalBINID.getText().toString();
                String uid = txtScanUidItem.getText().toString();
                if (binid.length() > 0 && binid.equals(priviousBinID)) {
                    isBinIdChanged = true;
                } else if (binid.length() > 0 && !binid.equals(priviousBinID)) {
                    isBinIdChanged = true;
                    priviousBinID = binid;
                }
                if (uid.length() > 0 && uid.equals(priviousUid)) {
                    isUidChanged = false;
                } else if (uid.length() > 0 && !uid.equals(priviousUid)) {
                    boolean isHasUid = false;
                    for (int i = 0; i < mArrUIDItem.size(); i++){
                        if (mArrUIDItem.get(i).getUID().equals(uid)){
                            isUidChanged = true;
                            priviousUid = uid;
                            isHasUid = true;
                        }
                    }
                    if (!isHasUid){
                        String content = getResources().getString(R.string.error_uid_not_found);
                        content = String.format(content, uid);
                        int color = getResources().getColor(R.color.error_color);
                        Methods.alertNotify(PutawayMapingActivity.this, content, color);
                        txtScanUidItem.setText("");
                    }
                }

                if (isBinIdChanged && isUidChanged && mArrUIDItem.size() > 0) {
                    mapPutAway(uid, binid);
                    int size = mArrUIDItem.size();
                    for (int i = 0; i <= size - 1; i++) {
                        if (mArrUIDItem.get(i).getUID().equals(uid)) {
                            Variables.mArrUIDPutawayed.add(mArrUIDItem.get(i));
                            mArrUIDItem.remove(mArrUIDItem.get(i));
                            mAdapter.notifyDataSetChanged();
                            PutawayedActivity putawayedActivity = new PutawayedActivity();
                            putawayedActivity.updateData();
                            i--;
                            size--;
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

    private void getDetailPutAway() {
        showDialog();
        GetDetailPutAwayRequest listPutAwayRequest = new GetDetailPutAwayRequest();
        listPutAwayRequest.execute(this, Variables.mUserInfo.getAccessToken(), "assigned", "mapping", mCodePutaway);
        listPutAwayRequest.getDetailPutAwayRequestOnResult(this);
    }

    @Override
    public void onGetDetailPutAwayRequestOnResult(boolean result, String data) {
        hideDialog();
        Log.d("detailPutawayOnResult", "getDetailPutawayOnResult: " + data);
        try {
            JSONObject jsonResult = new JSONObject(data);
            int uid_total = jsonResult.getInt(Constants.TOTAL);

            String string = getResources().getString(R.string.putaway_mapping_remain_uid);
            txtRemainUidItem.setText(String.format(string, uid_total));

            JSONArray itemList = jsonResult.getJSONArray(Constants.ITEMLIST);
            for (int i = 0; i < itemList.length(); i++) {
                JSONObject tempJSONObject = itemList.getJSONObject(i);
                int status = tempJSONObject.getInt(Constants.STATUS);
                String bindId = tempJSONObject.getString(Constants.BINID);
                String uid = tempJSONObject.getString(Constants.UID);
                String productName = tempJSONObject.getString(Constants.PRODUCTNAME);
                String productImage = tempJSONObject.getString(Constants.PRODUCTIMAGE);
                String statusName = tempJSONObject.getString(Constants.STATUSNAME);
                String createTime = tempJSONObject.getString(Constants.CREATETIME);

                mArrUIDItem.add(new UIDItemBean(status, bindId, uid, productName, productImage, statusName, createTime));
            }
            mAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.fillInStackTrace();
        }
    }

    private void mapPutAway(String uid, String binid) {
        showDialog();
        MapPutawayRequest mapPutawayRequest = new MapPutawayRequest();
        mapPutawayRequest.execute(this, Variables.mUserInfo.getAccessToken(), uid, binid);
        mapPutawayRequest.mapPutAwayRequestOnResult(this);
    }

    @Override
    public void onMapPutAwayRequestOnResult(boolean result, String data) {
        hideDialog();
        Log.d("mapPutAwayResult", "mapPutAwayResult: " + data);
    }
}
