package com.shipchung.api;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shipchung.config.Constants;
import com.shipchung.util.Methods;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import boxme.shipchung.com.boxmeapp.R;

/**
 * Created by ToanNB on 8/11/2015.
 */
public class UndoPickupRequest {
    private UndoPickupRequestOnResult undoPickupRequestOnResult;

    public void execute(final Context context, String access_token, String pickup_code, String uid) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("access_token", access_token);
        } catch (JSONException e) {
            e.fillInStackTrace();
        }

        String url_undo_putaway = Constants.URL_PICKUP_UNDO + pickup_code+ "/" + uid +
                "?access_token=" + access_token;

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        client.delete(context, url_undo_putaway, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("status_code", "onSuccess UndoPickup: " + statusCode);
                String content = new String(responseBody);
                String content1 = context.getResources().getString(R.string.success_undo_pickup);
                Methods.successNotify(context, content1);
                try {
                    if (undoPickupRequestOnResult != null) {
                        undoPickupRequestOnResult.onUndoPickupRequestOnResult(true, content);
                    }
                } catch (NullPointerException e) {
                    if (undoPickupRequestOnResult != null) {
                        undoPickupRequestOnResult.onUndoPickupRequestOnResult(false, content);
                    }
                } catch (Exception e) {
                    if (undoPickupRequestOnResult != null) {
                        undoPickupRequestOnResult.onUndoPickupRequestOnResult(false, content);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("status_code", "onFailure UndoPickup: " + statusCode);
                Methods.checkError(context, statusCode);
                if (undoPickupRequestOnResult != null) {

                }
            }
        });
    }

    public void undoPickupRequestOnResult(UndoPickupRequestOnResult undoPutAwayRequestOnResult) {
        this.undoPickupRequestOnResult = undoPutAwayRequestOnResult;
    }

    public interface UndoPickupRequestOnResult {
        void onUndoPickupRequestOnResult(boolean result, String data);
    }
}
