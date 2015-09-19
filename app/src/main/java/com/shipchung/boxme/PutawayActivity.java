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
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.costum.android.widget.LoadMoreListView;
import com.shipchung.adapter.PutAwayAdapter;
import com.shipchung.api.GetListPutAwayRequest;
import com.shipchung.bean.PutAwayBean;
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
 * Created by ToanNB on 7/31/2015.
 */
public class PutawayActivity extends Activity implements GetListPutAwayRequest.GetListPutAwayRequestOnResult,
        AdapterView.OnItemClickListener{

    private TextView txtHeaderTitle;
    private TextView txtPutAwayRemain;
    private TextView txtSearchPutAway;
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
        txtSearchPutAway = (TextView) findViewById(R.id.search_putaway_txt);
        txtScanUid = (TextView) findViewById(R.id.putaway_mapping_scan_uid_txt);
        txtScanUid.setVisibility(View.GONE);
        mListView = (LoadMoreListView) findViewById(R.id.putaway_listing_listview);
        mArrData = new ArrayList<>();
        mArrPutaway = new ArrayList<>();
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
        if (keyCode == KeyEvent.ACTION_DOWN){
            mPutAwayAdapter.setEnableFocus(false);
        }
        if (keyCode == KeyEvent.ACTION_UP){
            mPutAwayAdapter.setEnableFocus(true);
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
        mPutAwayAdapter.setEnableFocus(false);
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
                if (sTemp.length() >= 5) {
                    sTemp = sTemp.trim();
                    if (sTemp.substring(0, 2).equals("PA")) {
                        txtSearchPutAway.setText(sTemp);
                        boolean isHasPutaway = false;
                        for (int i = 0; i < mArrPutaway.size(); i++) {
                            if (mArrPutaway.get(i).equals(sTemp)) {
                                isHasPutaway = true;
                                Intent intent = new Intent(getBaseContext(), PutawayMapingActivity.class);
                                intent.putExtra("code_putaway", sTemp);
                                startActivity(intent);
                            }
                        }
                        if (!isHasPutaway) {
                            String content = getResources().getString(R.string.error_putaway_not_found);
                            int color = getResources().getColor(R.color.error_color);
                            Methods.alertNotify(PutawayActivity.this, content, color);
                        }
                        txtSearchPutAway.setText("");
                    } else {
                        String content = getResources().getString(R.string.error_putaway_not_putaway);
                        int color = getResources().getColor(R.color.error_color);
                        Methods.alertNotify(PutawayActivity.this, content, color);
                    }
                }
            }

            mPutAwayAdapter.setEnableFocus(true);
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