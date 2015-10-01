package com.shipchung.boxme;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.rey.material.app.ThemeManager;
import com.shipchung.adapter.ChangeLocationAdapter;
import com.shipchung.api.ChangeLocationRequest;
import com.shipchung.bean.ChangeLocationBean;
import com.shipchung.config.Constants;
import com.shipchung.config.Variables;
import com.shipchung.custom.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import boxme.shipchung.com.boxmeapp.R;

/**
 * Created by ToanNB on 7/31/2015.
 */
public class ChangeLocationActivity extends Activity implements ChangeLocationRequest.ChangeLocationRequestOnResult {
    private TextView txtLocationLabel;
    private TextView txtUpdateLabel;
    private TextView txtScanUidItem;
    private TextView txtScanBinIdLocation;
    private TextView txtRemain;
    private TextView txtScanStatusCode;
    private ListView mListView;
    private static LoadingDialog mLoadingDialog;
    private ArrayList<ChangeLocationBean> mArrData;
    private ChangeLocationAdapter mAdapter;
    private boolean isBinIdChanged = false;
    private boolean isUidChanged = false;
    private boolean isStatus = false;
    private Spinner mSpinnerStatus;
    private ArrayAdapter<String> mAdapterStatus;
    private String mStatus = "Stocked";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.init(this, 1, 0, null);
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.putaway_mapping_layout);
        setContentView(R.layout.putaway_change_location_layout);
        initView();
        initStatus();
        mLoadingDialog = new LoadingDialog(this);
    }

    public void initView() {
        txtLocationLabel = (TextView) findViewById(R.id.putaway_mapping_txt);
        txtUpdateLabel = (TextView) findViewById(R.id.putaway_mapping_packet_name_txt);
        txtRemain = (TextView) findViewById(R.id.putaway_mapping_uid_remaining_txt);
        txtScanUidItem = (TextView) findViewById(R.id.scan_location_binid_txt);
        txtScanBinIdLocation = (TextView) findViewById(R.id.putaway_mapping_scan_uid_item_txt);
//        txtScanStatusCode = (TextView) findViewById(R.id.scan_status_txt);
//        txtScanStatusCode.setVisibility(View.VISIBLE);
        mSpinnerStatus = (Spinner) findViewById(R.id.spinner_status);
        mArrData = new ArrayList<>();
        mListView = (ListView) findViewById(R.id.putaway_mapping_listview);

        txtScanUidItem.setHint(getResources().getString(R.string.putaway_mapping_scan_uid_item));
        txtScanBinIdLocation.setHint(getResources().getString(R.string.update_scan_new_binid));
        txtRemain.setVisibility(View.GONE);
        txtLocationLabel.setText(getResources().getString(R.string.change_location_label));
        txtUpdateLabel.setText(getResources().getString(R.string.change_location_update));
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
                String first = sTemp.substring(0, 1);
                if (sTemp.length() >= 8) {
                    if (sTemp.contains("-")) {
                        txtScanBinIdLocation.setText(sTemp);
                        isBinIdChanged = true;
                        txtScanUidItem.setText("");
                    }
                    if (sTemp.substring(0, 1).equals("U") && isBinIdChanged) {
                        txtScanUidItem.setText(sTemp);
                        isUidChanged = true;
                    }
                }
                if (txtScanBinIdLocation.getText().length() == 0) {
                    isBinIdChanged = false;
                } else if (txtScanUidItem.getText().length() == 0) {
                    isUidChanged = false;
                }
                if (isBinIdChanged && isUidChanged) {
                    String binid = txtScanBinIdLocation.getText().toString();
                    String uid = txtScanUidItem.getText().toString();
//                    String status = txtScanStatusCode.getText().toString();
                    changeLocation(uid, binid, mStatus);
                    txtScanBinIdLocation.setText("");
                    txtScanUidItem.setText("");
                    Log.d("gettext1", "txtSccanLocalBINID.getText(): " + binid);
                    Log.d("gettext1", "txtUidItem.getText(): " + uid);
                }
            }
        }
    };

    private void initStatus() {
        mAdapterStatus = initAdapter();
        mAdapterStatus.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        mAdapterStatus.add("Stocked");
        mAdapterStatus.add("Damaged");
        mAdapterStatus.add("Stocked");

        mSpinnerStatus.setAdapter(mAdapterStatus);
        mSpinnerStatus.setFocusable(false);
//        spinnerSex.setSelection(adapterSex.getCount());
        mSpinnerStatus.setSelection(mAdapterStatus.getCount());
        mSpinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    mStatus = "Stocked";
                    ((TextView) view.findViewById(android.R.id.text1)).setText("Stocked");
                } else if (position == 1) {
                    mStatus = "Damaged";
                    ((TextView) view.findViewById(android.R.id.text1)).setText("Damaged");
                }

                ((TextView) view.findViewById(android.R.id.text1)).setTextColor(getResources().getColor(R.color.white));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public ArrayAdapter<String> initAdapter() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView) v.findViewById(android.R.id.text1)).setText("");
                    ((TextView) v.findViewById(android.R.id.text1)).setHint(getItem(getCount()));
                    ((TextView) v.findViewById(android.R.id.text1)).setHintTextColor(getResources().getColor(R.color.white));
                    ((TextView) v.findViewById(android.R.id.text1)).setTextColor(getResources().getColor(R.color.white));
                }

                return v;
            }

            @Override
            public int getCount() {
                return super.getCount() - 1;
            }

        };
        return adapter;
    }


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

    private void changeLocation(String uid, String binid, String status) {
        showDialog();
        ChangeLocationRequest changeLocationRequest = new ChangeLocationRequest();
        changeLocationRequest.execute(this, Variables.mUserInfo.getAccessToken(), uid, binid, status);
        changeLocationRequest.changeLocationRequestOnResult(this);
    }

    @Override
    public void onChangeLocationRequestOnResult(boolean result, String data) {
        hideDialog();
        Log.d("changeLocation", "onChangeLocationResult: " + data);
        try {
            JSONObject jsonObjectResult = new JSONObject(data);
            String status = jsonObjectResult.getString(Constants.STATUS);
            String uid = jsonObjectResult.getString(Constants.UID);
            String bsin = jsonObjectResult.getString(Constants.BSIN);
            String oldBinId = jsonObjectResult.getString(Constants.OLD_BIN_ID);
            String productName = jsonObjectResult.getString("Productname");
            String newBinId = jsonObjectResult.getString(Constants.NEW_BIN_ID);
            mArrData.add(new ChangeLocationBean(status, uid, bsin, oldBinId, productName, newBinId));

            mAdapter = new ChangeLocationAdapter(this, mArrData);
            mListView.setAdapter(mAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
