package com.shipchung.boxme;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.shipchung.adapter.PickupedUIDItemAdapter;
import com.shipchung.api.UndoPickupRequest;
import com.shipchung.bean.UIDItemBean;
import com.shipchung.config.Variables;
import com.shipchung.custom.LoadingDialog;
import com.shipchung.util.SwipeDetector;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import boxme.shipchung.com.boxmeapp.R;

/**
 * Created by ToanNB on 8/10/2015.
 */
public class PickupedActivity extends Activity implements UndoPickupRequest.UndoPickupRequestOnResult {

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

    public PickupedActivity(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.putawayed_layout);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mPickupCode = extras.getString("pickup_code");
        }
        initView();
        mLoadingDialog = new LoadingDialog(this);
    }

    private void initView(){
        txtTitle = (TextView) findViewById(R.id.putawayed_title_id);
        txtTitle.setText("Pickuped");
        txtRemainItemPickuped = (TextView) findViewById(R.id.putawayed_remain_item_pickuped);
        mArrData = new ArrayList<>();
        mArrData.add(new UIDItemBean(1, "bin1", "uid1", "product1", "", "statusname1", ""));
        mArrData.add(new UIDItemBean(1, "bin2", "uid2", "product2", "", "statusname2", ""));

        txtRemainItemPickuped.setText(String.format(getResources().getString(R.string.putawayed_remain_item_pickuped), Variables.mArrUIDPickuped.size()));
        mAdapter = new PickupedUIDItemAdapter(this, PickupedActivity.this, Variables.mArrUIDPickuped);
        mListView = (ListView) findViewById(R.id.putawayed_listview);
        mListView.setAdapter(mAdapter);

        final SwipeDetector swipeDetector = new SwipeDetector(this, mPickupCode, 2);
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
                    Variables.mArrUIDPickuped.remove(undoPos);
                    mAdapter.notifyDataSetChanged();
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
}
