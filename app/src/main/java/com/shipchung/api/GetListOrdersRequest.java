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
 * Created by ToanNB on 8/5/2015.
 */
public class GetListOrdersRequest {

    private GetListOrdersRequestOnResult getListOrdersRequestOnResult;

    public void execute(final Context context, String access_token, String pickup_code) {
        final int[] status_code = new int[1];
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("access_token", access_token);
        } catch (JSONException e) {
            e.fillInStackTrace();
        }

        String data = jsonObject.toString();

        RequestParams lvParams = new RequestParams();
        lvParams.put("params", data);
        Log.d("api_login: ", "execute: " + data);
        String url_detail_pickup = Constants.URL_LIST_ORDER + pickup_code +
                "?access_token=" + access_token + "&page_size=1000";

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        client.get(context, url_detail_pickup, lvParams, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String content = new String(responseBody);
                Log.d("status_http", "onSuccess getDetailPickup: " + statusCode);
                Log.d("content ", "onSuccess getDetailPickup: " + content);
                try {
                    if (getListOrdersRequestOnResult != null) {
                        getListOrdersRequestOnResult.onGetListOrdersRequestOnResult(true, content);
                    }
                } catch (NullPointerException e) {
                    Log.d("api_login: ", "onSuccess: " + "NullPointerException");
                    if (getListOrdersRequestOnResult != null) {
                        getListOrdersRequestOnResult.onGetListOrdersRequestOnResult(false, content);
                    }
                } catch (Exception e) {
                    Log.d("api_login: ", "onSuccess: " + "Exception");
                    if (getListOrdersRequestOnResult != null) {
                        getListOrdersRequestOnResult.onGetListOrdersRequestOnResult(false, content);
                    }
                } catch (OutOfMemoryError e) {
                    Log.d("api_login: ", "onSuccess: " + "OutOfMemoryError");
                    if (getListOrdersRequestOnResult != null) {
                        getListOrdersRequestOnResult.onGetListOrdersRequestOnResult(false, content);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("status_code", "onFailure getDetailPickup: " + statusCode);
                Log.d("status_http", "onFailure getDetailPickup: " + statusCode);
                Methods.checkError(context, statusCode);
                if (getListOrdersRequestOnResult != null) {
                }
            }
        });
    }

    public void getListOrdersRequestOnResult(GetListOrdersRequestOnResult getListOrdersRequestOnResult) {
        this.getListOrdersRequestOnResult = getListOrdersRequestOnResult;
    }

    public interface GetListOrdersRequestOnResult {
        void onGetListOrdersRequestOnResult(boolean result, String data);
    }
}
