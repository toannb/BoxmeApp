package com.shipchung.boxme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.shipchung.adapter.PickupedUIDItemAdapter;
import com.shipchung.api.GetDetailPickupRequest;
import com.shipchung.api.UndoPickupRequest;
import com.shipchung.bean.UIDItemBean;
import com.shipchung.config.Constants;
import com.shipchung.config.Variables;
import com.shipchung.custom.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import boxme.shipchung.com.boxmeapp.R;

/**
 * Created by ToanNB on 8/10/2015.
 */
public class PickupedActivity extends Activity implements UndoPickupRequest.UndoPickupRequestOnResult,
        GetDetailPickupRequest.GetDetailPickupRequestOnResult, View.OnClickListener{

    private ArrayList<UIDItemBean> mArrData;
    private ListView mListView;
    private PickupedUIDItemAdapter mAdapter;

    private TextView txtTitle;
    public static TextView txtRemainItemPickuped;
    private LoadingDialog mLoadingDialog;
    private String mUID;
    private String mPickupCode;
    public boolean undoSuccess;
    private int undoPos;
    private Button btnBack;
    private String mOrderCode = "";
    private int mTotalUIDPickuped = 0;

    public PickupedActivity(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.putawayed_layout);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mPickupCode = extras.getString("pickup_code");
            mOrderCode = extras.getString("order_code");
            mTotalUIDPickuped = extras.getInt("total_uid_pickuped");
        }
        initView();
        getDetailPickup();
        mLoadingDialog = new LoadingDialog(this);
    }

    private void initView(){
        btnBack = (Button) findViewById(R.id.btnBackPutawayMapping);
        String btnText = getResources().getString(R.string.putawayed_btn_back);
        btnBack.setText(btnText);
        btnBack.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(this);
        txtTitle = (TextView) findViewById(R.id.putawayed_title_id);
        txtTitle.setText(getResources().getString(R.string.pickuped_label));
        txtRemainItemPickuped = (TextView) findViewById(R.id.putawayed_remain_item_pickuped);
        mArrData = new ArrayList<>();

        mAdapter = new PickupedUIDItemAdapter(this, PickupedActivity.this, mArrData);
        mListView = (ListView) findViewById(R.id.putawayed_listview);
        mListView.setAdapter(mAdapter);

//        final SwipeDetector swipeDetector = new SwipeDetector(this, mPickupCode, 2);
//        mListView.setOnTouchListener(swipeDetector);
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if (swipeDetector.swipeDetected()) {
//                    // do the onSwipe action
//                } else {
//                    // do the onItemClick action
//                }
//            }
//        });
    }

    private void showDialog(){
        if(mLoadingDialog != null && !mLoadingDialog.isShowing()){
            mLoadingDialog.show();
        }
    }
    private void hideDialog(){
        if(mLoadingDialog != null && mLoadingDialog.isShowing()){
            mLoadingDialog.dismiss();
        }
    }

    public void undoPickup(String uid, int pos){
        showDialog();
        undoPos = pos;
        UndoPickupRequest undoPickupRequest = new UndoPickupRequest();
        undoPickupRequest.execute(this, Variables.mUserInfo.getAccessToken(), mPickupCode, uid);
        undoPickupRequest.undoPickupRequestOnResult(this);
    }

    @Override
    public void onUndoPickupRequestOnResult(boolean result, String data) {
        hideDialog();
        Log.d("undoPickupResult","undoPickupResult: " + data);

        try {
            JSONObject jsonObjResult = new JSONObject(data);
            boolean success = jsonObjResult.getBoolean("Success");
            undoSuccess = success;
            if (success){
                    mArrData.remove(undoPos);
                    mAdapter.notifyDataSetChanged();
                txtRemainItemPickuped.setText(String.format(getResources().getString(
                        R.string.putawayed_remain_item_pickuped), mArrData.size()));
                    Toast.makeText(this, "Undo Success", Toast.LENGTH_LONG).show();
//                mAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateData(){
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
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

            txtRemainItemPickuped.setText(String.format(getResources().getString(
                    R.string.putawayed_remain_item_pickuped), uid_total));

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

                mArrData.add(new UIDItemBean(status, bindId, updateTime, uid, productStatusname,
                        bsin, statusName, order, productStatus, productName));
            }
//            mAdapter.notifyDataSetChanged();
            mListView.setAdapter(mAdapter);
        } catch (JSONException e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btnBackPutawayMapping:
                Intent intentBack = new Intent(getApplicationContext(), PickupMapingActivity.class);
                intentBack.putExtra("order_code", mOrderCode);
                intentBack.putExtra("pickup_code", mPickupCode);
                intentBack.putExtra("total_uid_pickuped", mTotalUIDPickuped);
                startActivity(intentBack);
                finish();
                break;
        }
    }
}
