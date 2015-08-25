package com.shipchung.boxme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.costum.android.widget.LoadMoreListView;
import com.shipchung.adapter.ReturnAdapter;
import com.shipchung.api.GetListReturnCodeRequest;
import com.shipchung.bean.ReturnCodeBean;
import com.shipchung.config.Constants;
import com.shipchung.config.Variables;
import com.shipchung.custom.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import boxme.shipchung.com.boxmeapp.R;

/**
 * Created by ToanNB on 8/21/2015.
 */
public class ReturnActivity extends Activity implements GetListReturnCodeRequest.GetListReturnCodeRequestOnResult,
        AdapterView.OnItemClickListener{

    private TextView txtTitle;
    private TextView txtTrackerLabel;
    private TextView txtRemainItem;
    private TextView txtScanReturnCode;
    private TextView txtScanUid;
    private LoadMoreListView mListView;
    private ArrayList<ReturnCodeBean> mArrReturn;
    private ReturnAdapter mAdapter;
    private LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.putaway_mapping_layout);

        initView();
        mLoadingDialog = new LoadingDialog(this);
        getListReturnCode();
    }

    public void initView(){
        txtTitle = (TextView) findViewById(R.id.putaway_mapping_txt);
        txtTrackerLabel = (TextView) findViewById(R.id.putaway_mapping_packet_name_txt);
        txtRemainItem = ( TextView) findViewById(R.id.putaway_mapping_uid_remaining_txt);
        txtScanReturnCode = (TextView) findViewById(R.id.scan_location_binid_txt);
        txtScanUid = (TextView) findViewById(R.id.putaway_mapping_scan_uid_item_txt);
        mListView = (LoadMoreListView) findViewById(R.id.putaway_mapping_listview);

        mArrReturn = new ArrayList<>();

        txtScanUid.setVisibility(View.GONE);
        txtScanReturnCode.setText(getResources().getString(R.string.return_scan_return_code));
        txtRemainItem.setVisibility(View.GONE);
        txtTrackerLabel.setText(getResources().getString(R.string.return_tracker_label));
        txtTitle.setText(getResources().getString(R.string.function_return));
        mListView.setOnItemClickListener(this);
    }

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

    public void getListReturnCode(){
        showDialog();
        GetListReturnCodeRequest getListReturnCodeRequest = new GetListReturnCodeRequest();
        getListReturnCodeRequest.execute(this, Variables.mUserInfo.getAccessToken());
        getListReturnCodeRequest.getListReturnCodeRequestOnResult(this);
    }

    @Override
    public void onGetListReturnCodeRequestOnResult(boolean result, String data) {
        hideDialog();
        Log.d("listReturnCode", "getListReturnCodeRusult: " + data);
        try {
            JSONObject jsonObjectResult = new JSONObject(data);
            int total_item = jsonObjectResult.getInt(Constants.TOTAL_ITEM);
            JSONObject jsonObjectEmbedded = jsonObjectResult.getJSONObject(Constants.EMBEDDED);
            JSONArray jsonArrData = jsonObjectEmbedded.getJSONArray(Constants.DATA);
            int size = jsonArrData.length();
            for(int i = 0; i < size; i++){
                JSONObject jsonObjectTemp = jsonArrData.getJSONObject(i);
                String created = jsonObjectTemp.getString("created");
                int item = jsonObjectTemp.getInt(Constants.ITEM);
                String rc = jsonObjectTemp.getString(Constants.RC);
                String tc = jsonObjectTemp.getString(Constants.TC);
                mArrReturn.add(new ReturnCodeBean(rc, item,created, tc));
            }
            mAdapter = new ReturnAdapter(this, mArrReturn);
            mListView.setAdapter(mAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
        Intent intent = new Intent(getBaseContext(), RestockReturnActivity.class);
        intent.putExtra("return_code", mArrReturn.get(pos).getRc());
        startActivity(intent);
    }
}
