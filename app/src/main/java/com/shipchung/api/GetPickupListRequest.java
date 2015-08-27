package com.shipchung.api;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shipchung.config.Constants;
import com.shipchung.util.Methods;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ToanNB on 8/11/2015.
 */
public class GetPickupListRequest {
    private GetPickupRequestOnResult getPickupRequestOnResult;

    public void execute(final Context context, String access_token) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("access_token", access_token);
        } catch (JSONException e) {
            e.fillInStackTrace();
        }

        String data = jsonObject.toString();

        RequestParams lvParams = new RequestParams();
        lvParams.put("params", data);
        String url_map_pickup = Constants.URL_PICKUP +
                "?access_token=" + access_token+ "&status=Assigned";
        Log.d("url_", url_map_pickup);
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        client.get(context, url_map_pickup, lvParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String content = new String(responseBody);
                Log.d("status_http", "onSuccess getPickupList: " + statusCode);
                Log.d("content", "onSuccess getPickupList" + content);
                try {
                    if (getPickupRequestOnResult != null) {
                        getPickupRequestOnResult.onGetPickupRequestOnResult(true, content);
                    }
                } catch (NullPointerException e) {
                    if (getPickupRequestOnResult != null) {
                        getPickupRequestOnResult.onGetPickupRequestOnResult(false, content);
                    }
                } catch (Exception e) {
                    if (getPickupRequestOnResult != null) {
                        getPickupRequestOnResult.onGetPickupRequestOnResult(false, content);
                    }
                } catch (OutOfMemoryError e) {
                    if (getPickupRequestOnResult != null) {
                        getPickupRequestOnResult.onGetPickupRequestOnResult(false, content);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("status_code", "onFailure getPickupList: " + statusCode);
                Methods.checkError(context, statusCode);
                if (getPickupRequestOnResult != null) {
                }
            }
        });
    }

    public void getPickupRequestOnResult(GetPickupRequestOnResult getPickupRequestOnResult) {
        this.getPickupRequestOnResult = getPickupRequestOnResult;
    }

    public interface GetPickupRequestOnResult {
        void onGetPickupRequestOnResult(boolean result, String data);
    }
}
