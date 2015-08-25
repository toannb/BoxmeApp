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
 * Created by ToanNB on 8/20/2015.
 */
public class RestockUidReturnRequest {
    private RestockUidReturnRequestOnResult restockUidReturnRequestOnResult;

    public void execute(Context context, String access_token, String code, String uid, String bin) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("code", code);
            jsonObject.put("uid", uid);
            jsonObject.put("bin", bin);
        } catch (JSONException e) {
            e.fillInStackTrace();
        }
        String data = jsonObject.toString();
        Log.d("data_restockUidReturn", "data: " + data);

        String url_create_return_code = Constants.URL_RESTOCK_UID_RETURN + "?access_token=" + access_token;
        Log.d("link_restockUidReturn", url_create_return_code);
        StringEntity entity = null;
        try {
            entity = new StringEntity(data.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String contentType = "application/json";

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        client.put(context, url_create_return_code, entity, contentType, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String content = new String(responseBody);
                Log.d("success", "onSuccess restockUidReturn: " + statusCode);
                try {
                    if (restockUidReturnRequestOnResult != null) {
                        restockUidReturnRequestOnResult.onRestockUidReturnRequestOnResult(true, content);
                    }
                } catch (NullPointerException e) {
                    if (restockUidReturnRequestOnResult != null) {
                        restockUidReturnRequestOnResult.onRestockUidReturnRequestOnResult(false, content);
                    }
                } catch (Exception e) {
                    if (restockUidReturnRequestOnResult != null) {
                        restockUidReturnRequestOnResult.onRestockUidReturnRequestOnResult(false, content);
                    }
                } catch (OutOfMemoryError e) {
                    if (restockUidReturnRequestOnResult != null) {
                        restockUidReturnRequestOnResult.onRestockUidReturnRequestOnResult(false, content);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("status_code", "onFailure restockUidReturn: " + statusCode);
                String content = new String(responseBody);
                Log.d("fail", "onFailure restockUidReturn: " + statusCode + "\n" + content);
                if (restockUidReturnRequestOnResult != null) {
                    restockUidReturnRequestOnResult.onRestockUidReturnRequestOnResult(false, content);
                }
            }
        });
    }

    public void restockUidReturnRequestOnResult(RestockUidReturnRequestOnResult restockUidReturnRequestOnResult) {
        this.restockUidReturnRequestOnResult = restockUidReturnRequestOnResult;
    }

    public interface RestockUidReturnRequestOnResult {
        void onRestockUidReturnRequestOnResult(boolean result, String data);
    }
}
