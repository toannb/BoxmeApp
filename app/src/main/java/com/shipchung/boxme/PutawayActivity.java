package com.shipchung.boxme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.costum.android.widget.LoadMoreListView;
import com.shipchung.adapter.PutAwayAdapter;
import com.shipchung.api.GetListPutAwayRequest;
import com.shipchung.bean.PutAwayBean;
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
public class PutawayActivity extends Activity implements GetListPutAwayRequest.GetListPutAwayRequestOnResult,
        AdapterView.OnItemClickListener{

    private TextView txtHeaderTitle;
    private TextView txtPutAwayRemain;
    private AutoCompleteTextView txtSearchPutAway;
    private TextView txtScanUid;
    private LoadMoreListView mListView;
    private PutAwayAdapter mPutAwayAdapter;
    private ArrayAdapter<String> mStringAdapter;
    private ArrayList<String> mArrPutaway;
    private ArrayList<PutAwayBean> mArrData;
    private Handler mHandler;

    private int page_count = 3;
    private int current_page = 1;
    private int item_remain = 0;

    private LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.putaway_listing_layout);

        mLoadingDialog = new LoadingDialog(PutawayActivity.this);
        initView();
        mHandler = new Handler();
        getListPutAway();
    }

    private void initView() {
        txtPutAwayRemain = (TextView) findViewById(R.id.putaway_remaining_txt);
        txtSearchPutAway = (AutoCompleteTextView) findViewById(R.id.search_putaway_txt);
        txtScanUid = (TextView) findViewById(R.id.putaway_mapping_scan_uid_txt);
        txtScanUid.setVisibility(View.GONE);
        mListView = (LoadMoreListView) findViewById(R.id.putaway_listing_listview);
        mArrData = new ArrayList<>();
        mArrPutaway = new ArrayList<>();
        mStringAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mArrPutaway);
        txtSearchPutAway.setAdapter(mStringAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (current_page <= page_count){
                    getListPutAway();
                }
                mListView.onLoadMoreComplete();
            }
        });
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

    private void getListPutAway() {
        showDialog();
        GetListPutAwayRequest listPutAwayRequest = new GetListPutAwayRequest();
        listPutAwayRequest.execute(this, Variables.mUserInfo.getAccessToken(), "assigned", current_page);
        listPutAwayRequest.getListPutAwayRequestOnResult(this);
    }

    @Override
    public void onGetListPutAwayRequestOnResult(boolean result, String data) {
        hideDialog();
        Log.d("ListPutawayOnResult: ", "onGetListPutAwayRequestOnResult " + data);

        try {
            JSONObject jsonResult = new JSONObject(data);
            Variables.mArrPutAway = new ArrayList<>();

            int total_item = jsonResult.getInt(Constants.TOTAL_ITEM);
            item_remain = total_item;
            page_count = jsonResult.getInt(Constants.PAGECOUNT);
            current_page++;
            txtPutAwayRemain.setText(getResources().getString(R.string.putaway_listing_remaining) + " " + item_remain);

            JSONObject embeddedJsonObject = jsonResult.getJSONObject(Constants.EMBEDDED);
            JSONArray jsonArrPutAway = embeddedJsonObject.getJSONArray(Constants.PUT_AWAY);
            for (int i = 0; i < jsonArrPutAway.length(); i++) {
                JSONObject jsonObject = jsonArrPutAway.getJSONObject(i);
                int status = jsonObject.getInt(Constants.STATUS);
                String putAwayCode = jsonObject.getString(Constants.PUTAWAYCODE);
                int numberUID = jsonObject.getInt(Constants.NUMBERUID);
                String assigner = jsonObject.getString(Constants.ASSIGNER);
                String statusName = jsonObject.getString(Constants.STATUSNAME);
                String employeeName = jsonObject.getString(Constants.EMPLOYEENAME);
                String createTime = jsonObject.getString(Constants.CREATETIME);

                mArrData.add(new PutAwayBean(status, putAwayCode, numberUID,
                        assigner, statusName, employeeName, createTime));
                mArrPutaway.add(putAwayCode);
            }
            mPutAwayAdapter = new PutAwayAdapter(this, mArrData);
            mListView.setAdapter(mPutAwayAdapter);
            mStringAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
        Intent intent = new Intent(getBaseContext(), PutawayMapingActivity.class);
        intent.putExtra("code_putaway", mArrPutaway.get(pos));
        startActivity(intent);
    }

}