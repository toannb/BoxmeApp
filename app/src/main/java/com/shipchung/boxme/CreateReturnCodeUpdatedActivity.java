package com.shipchung.boxme;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.andexert.library.RippleView;
import com.shipchung.adapter.UIDITemAdapter;
import com.shipchung.config.Variables;
import com.shipchung.util.SwipeDetector;

import boxme.shipchung.com.boxmeapp.R;

/**
 * Created by ToanNB on 8/20/2015.
 */
public class CreateReturnCodeUpdatedActivity extends Activity {

    private TextView txtTitle;
    public static TextView txtReturnRemain;
    private ListView mListView;
    private UIDITemAdapter mAdapter;
    private String mShipmentCode = "";
    private CreateReturnCodeActivity createReturnCodeActivity;
    private RippleView mRippleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.putawayed_layout);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mShipmentCode = extras.getString("shipment_code");
        }

        initView();
    }

    public void initView(){
//        mRippleView = (RippleView) findViewById(R.id.more);
//        mRippleView.setRippleColor(getResources().getColor(R.color.red));
        txtTitle = (TextView) findViewById(R.id.putawayed_title_id);
        txtReturnRemain = (TextView) findViewById(R.id.putawayed_remain_item_pickuped);
        mListView = (ListView) findViewById(R.id.putawayed_listview);
        createReturnCodeActivity = new CreateReturnCodeActivity();
        mAdapter = new UIDITemAdapter(this, createReturnCodeActivity.getContextClass(),
                CreateReturnCodeUpdatedActivity.this, Variables.mArrUidUpdated, 3);

        txtTitle.setText(getResources().getString(R.string.updated_label));
        txtReturnRemain.setText(String.format(getResources().getString(R.string.updated_item_return_remain), Variables.mArrUidUpdated.size()));
        mListView.setAdapter(mAdapter);

        final SwipeDetector swipeDetector = new SwipeDetector(this, mShipmentCode, 4);
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
    public CreateReturnCodeUpdatedActivity getContextClass(){
        return CreateReturnCodeUpdatedActivity.this;
    }
    public void updateData(){
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }
}
