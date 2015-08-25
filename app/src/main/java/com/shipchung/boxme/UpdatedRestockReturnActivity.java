package com.shipchung.boxme;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.shipchung.adapter.UIDITemAdapter;
import com.shipchung.config.Variables;
import com.shipchung.util.SwipeDetector;

import boxme.shipchung.com.boxmeapp.R;

/**
 * Created by ToanNB on 8/22/2015.
 */
public class UpdatedRestockReturnActivity extends Activity {

    private TextView txtUpdatedTitle;
    private TextView txtRemainUid;
    private ListView mListView;
    private UIDITemAdapter mAdapter;
    private String mReturnCode = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.putawayed_layout);

        Bundle extras = getIntent().getExtras();
        if (extras != null){
            mReturnCode = extras.getString("return_code");
        }

        initView();
    }

    public void initView(){
        txtUpdatedTitle = (TextView) findViewById(R.id.putawayed_title_id);
        txtRemainUid = (TextView) findViewById(R.id.putawayed_remain_item_pickuped);
        mListView = (ListView) findViewById(R.id.putawayed_listview);

        txtUpdatedTitle.setText(getResources().getString(R.string.updated_label));
        txtRemainUid.setVisibility(View.GONE);
        mAdapter = new UIDITemAdapter(this, Variables.mArrUidUpdatedRestockReturn, 5);
        mListView.setAdapter(mAdapter);


        final SwipeDetector swipeDetector = new SwipeDetector(this, mReturnCode, 6);
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
}
