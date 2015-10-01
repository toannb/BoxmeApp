package com.shipchung.boxme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.shipchung.adapter.PutawayedUIDItemAdapter;
import com.shipchung.api.UndoPutAwayRequest;
import com.shipchung.bean.UIDItemBean;
import com.shipchung.config.Constants;
import com.shipchung.config.Variables;
import com.shipchung.custom.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import boxme.shipchung.com.boxmeapp.R;

/**
 * Created by ToanNB on 8/10/2015.
 */
public class PutawayedActivity extends Activity implements UndoPutAwayRequest.UndoPutAwayRequestOnResult,
        View.OnClickListener {

    private ArrayList<UIDItemBean> mArrData;
    private ListView mListView;
    private PutawayedUIDItemAdapter mAdapter;

    public static TextView txtRemainItemPickuped;
    private LoadingDialog mLoadingDialog;
    private String mUID;
    private String mCodePutaway;
    public boolean undoSuccess;
    private int undoPos;
    private String mPickupCode = "";
    private Button btnBackPutawayMapping;

    public PutawayedActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.putawayed_layout);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mCodePutaway = extras.getString("code_putaway");
        }
        initView();
        mLoadingDialog = new LoadingDialog(this);
    }

    private void initView() {
        btnBackPutawayMapping = (Button) findViewById(R.id.btnBackPutawayMapping);
        String stringBack = getResources().getString(R.string.putawayed_btn_back);
        btnBackPutawayMapping.setText(stringBack);
        btnBackPutawayMapping.setVisibility(View.VISIBLE);
        btnBackPutawayMapping.setOnClickListener(this);
        txtRemainItemPickuped = (TextView) findViewById(R.id.putawayed_remain_item_pickuped);
        mArrData = new ArrayList<>();
        mArrData.add(new UIDItemBean(1, "bin1", "uid1", "product1", "", "statusname1", ""));
        mArrData.add(new UIDItemBean(1, "bin2", "uid2", "product2", "", "statusname2", ""));

        txtRemainItemPickuped.setText(String.format(getResources().getString(R.string.putawayed_remain_item_pickuped), Variables.mArrUIDPutawayed.size()));
        mAdapter = new PutawayedUIDItemAdapter(this, PutawayedActivity.this, Variables.mArrUIDPutawayed);
        mListView = (ListView) findViewById(R.id.putawayed_listview);
        mListView.setAdapter(mAdapter);

        /*
        final SwipeDetector swipeDetector = new SwipeDetector(this, mCodePutaway, 1);
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
        */
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

    public void undoPutAway(String uid, int pos) {
        showDialog();
        undoPos = pos;
        UndoPutAwayRequest undoPutAwayRequest = new UndoPutAwayRequest();
        undoPutAwayRequest.execute(this, Variables.mUserInfo.getAccessToken(), uid);
        undoPutAwayRequest.undoPutAwayRequestOnResult(this);
    }

    @Override
    public void onUndoPutAwayRequestOnResult(boolean result, String data) {
        hideDialog();
        Log.d("undoPutawayResult", "undoPutawayResult: " + data);

        try {
            JSONObject jsonObjResult = new JSONObject(data);
            boolean success = jsonObjResult.getBoolean(Constants.SUCCESS);
            undoSuccess = success;
            if (success) {
                Variables.mArrUIDPutawayed.remove(undoPos);
                txtRemainItemPickuped.setText(String.format(getResources().getString(R.string.putawayed_remain_item_pickuped),
                        Variables.mArrUIDPutawayed.size()));
                mAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateData() {
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnBackPutawayMapping:
                Intent intent = new Intent(getApplicationContext(), PutawayMapingActivity.class);
                intent.putExtra("code_putaway", mCodePutaway);
                intent.putExtra("pickup_code", mPickupCode);
                startActivity(intent);
                finish();
                break;
        }
    }
}
