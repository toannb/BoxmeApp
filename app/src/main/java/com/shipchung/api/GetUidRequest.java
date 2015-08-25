package com.shipchung.api;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shipchung.config.Constants;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ToanNB on 8/5/2015.
 */
public class GetUidRequest {

    private GetUidRequestOnResult getUidRequestOnResult;

    public void execute(Context context, String access_token, String uid) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("access_token", access_token);
        } catch (JSONException e) {
            e.fillInStackTrace();
        }

        String data = jsonObject.toString();
        Log.d("api_login: ","execute: " + data);
        String url_uid = Constants.URL_UID + uid +
                "?access_token=" + access_token;

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        client.get(context, url_uid, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String content = new String(responseBody);
                Log.d("status_http", "onSuccess getUid: " + statusCode);
                Log.d("content ", "onSuccess getUid: " + content);
                try {
                    if (getUidRequestOnResult != null) {
                        getUidRequestOnResult.onGetUidRequestOnResult(true, content);
                    }
                } catch (NullPointerException e) {
                    Log.d("api_login: ", "onSuccess: " + "NullPointerException");
                    if (getUidRequestOnResult != null) {
                        getUidRequestOnResult.onGetUidRequestOnResult(false, content);
                    }
                } catch (Exception e) {
                    Log.d("api_login: ", "onSuccess: " + "Exception");
                    if (getUidRequestOnResult != null) {
                        getUidRequestOnResult.onGetUidRequestOnResult(false, content);
                    }
                } catch (OutOfMemoryError e) {
                    Log.d("api_login: ", "onSuccess: " + "OutOfMemoryError");
                    if (getUidRequestOnResult != null) {
                        getUidRequestOnResult.onGetUidRequestOnResult(false, content);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("status_code", "onFailure getUidRequest: " + statusCode);
                String content = new String(responseBody);
                if (getUidRequestOnResult != null) {
                    Log.d("api_login: ", "onFailure: " + content);
                    getUidRequestOnResult.onGetUidRequestOnResult(false, content);
                }
            }
        });
    }

    public void getUidRequestOnResult(GetUidRequestOnResult getUidRequestOnResult) {
        this.getUidRequestOnResult = getUidRequestOnResult;
    }

    public interface GetUidRequestOnResult {
        void onGetUidRequestOnResult(boolean result, String data);
    }
}
