package com.shipchung.boxme;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.costum.android.widget.LoadMoreListView;
import com.rey.material.app.ThemeManager;
import com.shipchung.adapter.UIDITemAdapter;
import com.shipchung.api.CreateReturnCodeRequest;
import com.shipchung.api.GetListUidShipmentRequest;
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
 * Created by ToanNB on 8/18/2015.
 */
public class CreateReturnCodeActivity extends Activity implements GetListUidShipmentRequest.GetListUidShipmentRequestOnResult,
        AdapterView.OnItemClickListener, CreateReturnCodeRequest.CreateReturnCodeRequestOnResult{
    private TextView txtLocationLabel;
    private TextView txtUpdateLabel;
    private TextView txtScanShipment;
    private TextView txtScanUidItem;
    private TextView txtRemain;
    private TextView txtScanStatusCode;
    private LoadMoreListView mListView;
    private LoadingDialog mLoadingDialog;
    private UIDITemAdapter mAdapter;
    private boolean isShipmentChanged = false;
    private boolean isUidChanged = false;
    private boolean isStatus = true;
    private ImageView btnNew;
    private String mShipmentCode = "";

    private int page_count_uid = 1;
    private int current_page_uid = 1;
    private ArrayList<UIDItemBean> mArrUid;
    private CreateReturnCodeUpdatedActivity createReturnCodeUpdatedActivity;
    private RippleView mRippleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.init(this, 1, 0, null);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.putaway_mapping_layout);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            mShipmentCode = extras.getString("shipment_code");
        }
        initView();
        mLoadingDialog = new LoadingDialog(this);
    }

    public void initView() {
//        mRippleView = (RippleView) findViewById(R.id.more);
//        mRippleView.setRippleColor(getResources().getColor(R.color.white));
        txtLocationLabel = (TextView) findViewById(R.id.putaway_mapping_txt);
        txtUpdateLabel = (TextView) findViewById(R.id.putaway_mapping_packet_name_txt);
        txtRemain = (TextView) findViewById(R.id.putaway_mapping_uid_remaining_txt);
        txtScanShipment = (TextView) findViewById(R.id.scan_location_binid_txt);
        txtScanStatusCode = (TextView) findViewById(R.id.putaway_mapping_scan_uid_item_txt);
        txtScanUidItem = (TextView) findViewById(R.id.scan_status_txt);
        btnNew = (ImageView) findViewById(R.id.btn_new);
        btnNew.setVisibility(View.VISIBLE);
        txtScanUidItem.setVisibility(View.VISIBLE);
        mListView = (LoadMoreListView) findViewById(R.id.putaway_mapping_listview);
        mListView.setOnItemClickListener(this);
        txtScanStatusCode.setText(getResources().getString(R.string.function_status_accepted));
        txtScanShipment.setText(mShipmentCode);
        txtScanShipment.setHint(getResources().getString(R.string.function_scan_shipment_number));
        txtScanUidItem.setHint(getResources().getString(R.string.putaway_mapping_scan_uid_item));
        txtRemain.setVisibility(View.GONE);
        txtLocationLabel.setText(getResources().getString(R.string.function_create_return_code));
        txtUpdateLabel.setVisibility(View.GONE);

        createReturnCodeUpdatedActivity = new CreateReturnCodeUpdatedActivity();
        mAdapter = new UIDITemAdapter(this, CreateReturnCodeActivity.this,
                createReturnCodeUpdatedActivity.getContextClass(),Variables.mArrUidShipment, 2);
        mListView.setAdapter(mAdapter);

        final SwipeDetector swipeDetector = new SwipeDetector(this, mShipmentCode, 5);
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

        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createReturnCode();
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
                String first = sTemp.substring(0, 2);
                if (sTemp.length() >= 8) {
                    if (first.equals("SC")) {
                        Variables.shipment_code = sTemp;
                        isShipmentChanged = true;
                        if (txtScanShipment.getText().toString().equals(mShipmentCode)){
                        } else {
                            getListUidShipment(sTemp);
                        }
                        txtScanShipment.setText(sTemp);
                    }
                    if (txtScanShipment.getText().length()> 0){
                        isShipmentChanged = true;
                    }
                    if (sTemp.substring(0, 1).equals("U") && isShipmentChanged) {
                        txtScanUidItem.setText(sTemp);
                        isUidChanged = true;
                    }
                } else if (sTemp.equals("Accepted") || sTemp.equals("Damaged") || sTemp.equals("Wrong item")
                        || sTemp.equals("Defective")) {
                    txtScanStatusCode.setText(sTemp);
                    isStatus = true;
                }
                if (txtScanShipment.getText().length() == 0) {
                    isShipmentChanged = false;
                } else if (txtScanUidItem.getText().length() == 0) {
                    isUidChanged = false;
                } else if (txtScanStatusCode.getText().length() == 0) {
                    isStatus = false;
                }
                if (isShipmentChanged && isUidChanged && isStatus) {
                    String shipment_code = txtScanShipment.getText().toString();
                    String uid = txtScanUidItem.getText().toString();
                    String status = txtScanStatusCode.getText().toString();
                    for (int i = 0; i < Variables.mArrUidShipment.size(); i++) {
                        if (Variables.mArrUidShipment.get(i).getUID().equals(uid)) {
                            Variables.mArrUidUpdated.add(new UIDItemBean(Variables.mArrUidShipment.get(i).getSku(), uid, status,
                                    Variables.mArrUidShipment.get(i).getProductName(), shipment_code));
                            Variables.mArrUidShipment.remove(Variables.mArrUidShipment.get(i));
                            mAdapter.notifyDataSetChanged();
                            createReturnCodeUpdatedActivity.updateData();
                        }
                    }
//                }
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

    private void getListUidShipment(String shipment_code) {
        showDialog();
        GetListUidShipmentRequest getListUidShipmentRequest = new GetListUidShipmentRequest();
        getListUidShipmentRequest.execute(this, Variables.mUserInfo.getAccessToken(), shipment_code, current_page_uid);
        getListUidShipmentRequest.getListUidShipmentRequestOnResult(this);
    }

    @Override
    public void onGetListUidShipmentRequestOnResult(boolean result, String data) {
        hideDialog();
        Log.d("getListUidShipment", "getListUidShipmentOnResult: " + data);
        try {
            Variables.mArrUidShipment = new ArrayList<>();
            JSONObject jsonObjectResult = new JSONObject(data);
            page_count_uid = jsonObjectResult.getInt(Constants.PAGECOUNT);
            if (page_count_uid > 1) {
                current_page_uid++;
            }
            JSONObject jsonObjectEmbedded = jsonObjectResult.getJSONObject(Constants.EMBEDDED);
            JSONArray jsonArrayData = jsonObjectEmbedded.getJSONArray(Constants.DATA);
            int arrData_size = jsonArrayData.length();
            for (int i = 0; i < arrData_size; i++) {
                JSONObject jsonObjectTemp = jsonArrayData.getJSONObject(i);
                String sku = jsonObjectTemp.getString(Constants.SKU);
                String uid = jsonObjectTemp.getString(Constants.UID);
                String name = jsonObjectTemp.getString(Constants.NAME);
                Variables.mArrUidShipment.add(new UIDItemBean(sku, uid, name));
            }
            mAdapter = new UIDITemAdapter(this, CreateReturnCodeActivity.this,
                    createReturnCodeUpdatedActivity.getContextClass(),Variables.mArrUidShipment, 2);
            mListView.setAdapter(mAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public CreateReturnCodeActivity getContextClass(){
        return CreateReturnCodeActivity.this;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }
    public void updateData(){
        if(mAdapter != null){
            mAdapter.notifyDataSetChanged();
        }
    }

    public void createReturnCode(){
        showDialog();
        CreateReturnCodeRequest createReturnCodeRequest = new CreateReturnCodeRequest();
        createReturnCodeRequest.execute(this, Variables.mUserInfo.getAccessToken(), createArrayObject());
        createReturnCodeRequest.createReturnCodeRequestOnResult(this);
    }

    @Override
    public void onCreateReturnCodeRequestOnResult(boolean result, String data) {
        hideDialog();
        Log.d("createReturnCodeResult", "createReturnCodeResult: " + data);
        try {
            JSONObject jsonObjectResult = new JSONObject(data);
            boolean success = jsonObjectResult.getBoolean(Constants.SUCCESS);
            if (success){
                Toast.makeText(this, "Success", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public JSONArray createArrayObject(){
        int size = Variables.mArrUidUpdated.size();
        JSONArray jsonArrayUid = new JSONArray();

        for (int i = 0; i < size; i++){
            JSONObject jsonObjectTemp = new JSONObject();
            try {
                jsonObjectTemp.put("SKU", Variables.mArrUidUpdated.get(i).getSku());
                jsonObjectTemp.put("UID", Variables.mArrUidUpdated.get(i).getUID());
                jsonObjectTemp.put("status", Variables.mArrUidUpdated.get(i).getStatusReturn());
                jsonArrayUid.put(jsonObjectTemp);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArrayUid;
    }
}
