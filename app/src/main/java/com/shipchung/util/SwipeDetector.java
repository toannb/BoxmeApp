package com.shipchung.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.shipchung.boxme.CreateReturnCodeActivity;
import com.shipchung.boxme.CreateReturnCodeUpdatedActivity;
import com.shipchung.boxme.PickupMapingActivity;
import com.shipchung.boxme.PickupedActivity;
import com.shipchung.boxme.PutawayMapingActivity;
import com.shipchung.boxme.PutawayedActivity;
import com.shipchung.boxme.RestockReturnActivity;
import com.shipchung.boxme.UpdatedRestockReturnActivity;

/**
 * Created by ToanNB on 8/5/2015.
 */
public class SwipeDetector implements View.OnTouchListener {
    private Context mContext;
    private String mCodePutaway;
    private int flag;
    public SwipeDetector(Context context, String mCodePutaway, int flag){
        this.mContext = context;
        this.mCodePutaway = mCodePutaway;
        this.flag = flag;
    }

    public static enum Action {
        LR, // Left to Right
        RL, // Right to Left
        TB, // Top to bottom
        BT, // Bottom to Top
        None // when no action was detected
    }

    private static final String logTag = "SwipeDetector";
    private static final int MIN_DISTANCE = 100;
    private float downX, downY, upX, upY;
    private Action mSwipeDetected = Action.None;

    public boolean swipeDetected() {
        return mSwipeDetected != Action.None;
    }

    public Action getAction() {
        return mSwipeDetected;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                mSwipeDetected = Action.None;
                return false; // allow other events like Click to be processed
            case MotionEvent.ACTION_UP:
                upX = event.getX();
                upY = event.getY();

                float deltaX = downX - upX;
                float deltaY = downY - upY;

                // horizontal swipe detection
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    // left or right
                    if (deltaX < 0 && flag == 1) {
                        Log.d("SwipeDetecter", "SwipeDetecter Right to left");
                        Intent intent = new Intent(mContext, PutawayMapingActivity.class);
                        intent.putExtra("code_putaway", mCodePutaway);
                        mContext.startActivity(intent);
                        ((Activity) mContext).finish();
                        mSwipeDetected = Action.LR;
                        return false;
                    }
                    if (deltaX > 0 && flag == 0) {
                        Log.d("SwipeDetecter", "SwipeDetecter Left to Right");
                        Intent intent = new Intent(mContext, PutawayedActivity.class);
                        intent.putExtra("code_putaway", mCodePutaway);
                        mContext.startActivity(intent);
                        ((Activity) mContext).finish();
                        mSwipeDetected = Action.RL;
                        return false;
                    }
                    if (deltaX < 0 && flag == 2) {
                        Log.d("SwipeDetecter", "SwipeDetecter Right to left");
                        Intent intent = new Intent(mContext, PickupMapingActivity.class);
                        intent.putExtra("pickup_code", mCodePutaway);
                        mContext.startActivity(intent);
                        ((Activity) mContext).finish();
                        mSwipeDetected = Action.LR;
                        return false;
                    }
                    if (deltaX > 0 && flag == 3) {
                        Log.d("SwipeDetecter", "SwipeDetecter Left to Right");
                        Intent intent = new Intent(mContext, PickupedActivity.class);
                        intent.putExtra("pickup_code", mCodePutaway);
                        mContext.startActivity(intent);
                        ((Activity) mContext).finish();
                        mSwipeDetected = Action.RL;
                        return false;
                    }
                    if (deltaX < 0 && flag == 4) {
                        Log.d("SwipeDetecter", "SwipeDetecter Right to left");
                        Intent intent = new Intent(mContext, CreateReturnCodeActivity.class);
                        intent.putExtra("shipment_code", mCodePutaway);
                        mContext.startActivity(intent);
                        ((Activity) mContext).finish();
                        mSwipeDetected = Action.LR;
                        return false;
                    }
                    if (deltaX > 0 && flag == 5) {
                        Log.d("SwipeDetecter", "SwipeDetecter Left to Right");
                        Intent intent = new Intent(mContext, CreateReturnCodeUpdatedActivity.class);
                        intent.putExtra("shipment_code", mCodePutaway);
                        mContext.startActivity(intent);
                        ((Activity) mContext).finish();
                        mSwipeDetected = Action.RL;
                        return false;
                    }
                    if (deltaX < 0 && flag == 6) {
                        Log.d("SwipeDetecter", "SwipeDetecter Right to left");
                        Intent intent = new Intent(mContext, RestockReturnActivity.class);
                        intent.putExtra("return_code", mCodePutaway);
                        mContext.startActivity(intent);
                        ((Activity) mContext).finish();
                        mSwipeDetected = Action.LR;
                        return false;
                    }
                    if (deltaX > 0 && flag == 7) {
                        Log.d("SwipeDetecter", "SwipeDetecter Left to Right");
                        Intent intent = new Intent(mContext, UpdatedRestockReturnActivity.class);
                        intent.putExtra("return_code", mCodePutaway);
                        mContext.startActivity(intent);
                        ((Activity) mContext).finish();
                        mSwipeDetected = Action.RL;
                        return false;
                    }
                } else if (Math.abs(deltaY) > MIN_DISTANCE) { // vertical swipe
                    // detection
                    // top or down
                    if (deltaY < 0) {
//                        Log.i(logTag, "Swipe Top to Bottom");
//                        mSwipeDetected = Action.TB;
//                        return false;
                    }
                    if (deltaY > 0) {
//                        Log.i(logTag, "Swipe Bottom to Top");
//                        mSwipeDetected = Action.BT;
//                        return false;
                    }
                }
                return false;
        }
        return false;
    }
}