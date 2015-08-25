package com.shipchung.boxme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;

import com.shipchung.adapter.PickupAdapter;
import com.shipchung.api.GetPickupListRequest;
import com.shipchung.bean.PickupBean;
import com.shipchung.config.Constants;
import com.shipchung.config.Variables;
import com.shipchung.custom.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import boxme.shipchung.com.boxmeapp.R;

/**
 * Created by ToanNB on 7/31/2015.
 */
public class PickupActivity extends Activity implements GetPickupListRequest.GetPickupRequestOnResult, AdapterView.OnItemClickListener{

    private TextView txtPickUpLabel;
    private AutoCompleteTextView txtSearchPickup;
    private ListView mListView;
    private ArrayList<PickupBean> mArrData;
    private PickupAdapter mPickupAdapter;
    private LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.putaway_listing_layout);

        mLoadingDialog = new LoadingDialog(this);
        initView();
        getListPickup();
    }

    public void initView(){
        txtPickUpLabel = (TextView) findViewById(R.id.putaway_listing_txt);
        txtSearchPickup = (AutoCompleteTextView) findViewById(R.id.search_putaway_txt);
        txtPickUpLabel.setText(getResources().getString(R.string.pickup_listing_label));
        txtSearchPickup.setHint(getResources().getString(R.string.pickup_search_pickup_listing));

        mArrData = new ArrayList<>();
        mListView = (ListView) findViewById(R.id.putaway_listing_listview);
        mPickupAdapter = new PickupAdapter(this, mArrData);
        mListView.setAdapter(mPickupAdapter);
        mListView.setOnItemClickListener(this);
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

    public void getListPickup(){
        showDialog();
        GetPickupListRequest pickupListRequest = new GetPickupListRequest();
        pickupListRequest.execute(this, Variables.mUserInfo.getAccessToken());
        pickupListRequest.getPickupRequestOnResult(this);
    }

    @Override
    public void onGetPickupRequestOnResult(boolean result, String data) {
        hideDialog();
        Log.d("getPickupOnResult", "getPickupOnResult: " + data);
        try {
            JSONObject jsonResultObject = new JSONObject(data);
            int total_item = jsonResultObject.getInt(Constants.TOTAL_ITEM);
            JSONObject jsonObjectEmbeded = jsonResultObject.getJSONObject(Constants.EMBEDDED);
            JSONArray jsonArrayPickup = jsonObjectEmbeded.getJSONArray(Constants.PICKUP);
            int TotalUID, TotalSKU, PickupID, TotalOrder, Status;
            String PickupCode, StatusName, EmployeeName, UpdateTime, AssignName, EmployeeCode, CreateTime;
            for (int i = 0; i < jsonArrayPickup.length(); i++){
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
//            mPickupAdapter.notifyDataSetChanged();

            mPickupAdapter = new PickupAdapter(this, mArrData);
            mListView.setAdapter(mPickupAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
        Intent intent = new Intent(getBaseContext(), PickupMapingActivity.class);
        intent.putExtra("pickup_code", mArrData.get(pos).getPickupCode());
        startActivity(intent);
    }
}
