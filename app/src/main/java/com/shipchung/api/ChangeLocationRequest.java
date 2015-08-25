package com.shipchung.api;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shipchung.config.Constants;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by ToanNB on 8/17/2015.
 */
public class ChangeLocationRequest {
    private ChangeLocationRequestOnResult changeLocationRequestOnResult;

    public void execute(Context context, String access_token, String uid, String binid, String status) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Uid", uid);
            jsonObject.put("BinId", binid);
            jsonObject.put("status", status);
        } catch (JSONException e) {
            e.fillInStackTrace();
        }
        String data = jsonObject.toString();
        Log.d("data_mapping", "data: " + data);

        String url_change_location = Constants.URL_CHANGE_LOCATION +
                "?access_token=" + access_token;
        Log.d("link_change_location", url_change_location);
        StringEntity entity = null;
        try {
            entity = new StringEntity(data.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String contentType = "application/json";

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        client.put(context, url_change_location, entity, contentType, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String content = new String(responseBody);
                Log.d("success", "onSuccess ChangeLocation: " + statusCode);
                try {
                    if (changeLocationRequestOnResult != null) {
                        changeLocationRequestOnResult.onChangeLocationRequestOnResult(true, content);
                    }
                } catch (NullPointerException e) {
                    if (changeLocationRequestOnResult != null) {
                        changeLocationRequestOnResult.onChangeLocationRequestOnResult(false, content);
                    }
                } catch (Exception e) {
                    if (changeLocationRequestOnResult != null) {
                        changeLocationRequestOnResult.onChangeLocationRequestOnResult(false, content);
                    }
                } catch (OutOfMemoryError e) {
                    if (changeLocationRequestOnResult != null) {
                        changeLocationRequestOnResult.onChangeLocationRequestOnResult(false, content);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("status_code", "onFailure ChangeLocation: " + statusCode);
                String content = new String(responseBody);
                Log.d("fail", "onFailure ChangeLocation: " + statusCode + "\n" + content);
                if (changeLocationRequestOnResult != null) {
                    changeLocationRequestOnResult.onChangeLocationRequestOnResult(false, content);
                }
            }
        });
    }

    public void changeLocationRequestOnResult(ChangeLocationRequestOnResult changeLocationRequestOnResult) {
        this.changeLocationRequestOnResult = changeLocationRequestOnResult;
    }

    public interface ChangeLocationRequestOnResult {
        void onChangeLocationRequestOnResult(boolean result, String data);
    }
}
