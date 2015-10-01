package com.shipchung.api;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shipchung.config.Constants;
import com.shipchung.config.Variables;
import com.shipchung.util.Methods;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ToanNB on 8/5/2015.
 */
public class GetOrderItemsRequest {

    private GetOrderItemsRequestOnResult getOrderItemsRequestOnResult;

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
        String url_detail_pickup = Constants.URL_PICKUP_ORDER_ITEM + pickup_code +
                "?access_token=" + access_token + "&key=map";

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        client.get(context, url_detail_pickup, lvParams, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Variables.mStatusCode = 0;
                Variables.mStatusCode = statusCode;
                String content = new String(responseBody);
                Log.d("status_http", "onSuccess getDetailPickup: " + statusCode);
                Log.d("content ", "onSuccess getDetailPickup: " + content);
                try {
                    if (getOrderItemsRequestOnResult != null) {
                        getOrderItemsRequestOnResult.onGetOrderItemsRequestOnResult(true, content);
                    }
                } catch (NullPointerException e) {
                    Log.d("api_login: ", "onSuccess: " + "NullPointerException");
                    if (getOrderItemsRequestOnResult != null) {
                        getOrderItemsRequestOnResult.onGetOrderItemsRequestOnResult(false, content);
                    }
                } catch (Exception e) {
                    Log.d("api_login: ", "onSuccess: " + "Exception");
                    if (getOrderItemsRequestOnResult != null) {
                        getOrderItemsRequestOnResult.onGetOrderItemsRequestOnResult(false, content);
                    }
                } catch (OutOfMemoryError e) {
                    Log.d("api_login: ", "onSuccess: " + "OutOfMemoryError");
                    if (getOrderItemsRequestOnResult != null) {
                        getOrderItemsRequestOnResult.onGetOrderItemsRequestOnResult(false, content);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("status_code", "onFailure getDetailPickup: " + statusCode);
                Log.d("status_http", "onFailure getDetailPickup: " + statusCode);
                Variables.mStatusCode = 0;
                Variables.mStatusCode = statusCode;
                Methods.checkError(context, statusCode);
                if (getOrderItemsRequestOnResult != null) {
                }
            }
        });
    }

    public void getOrderItemsRequestOnResult(GetOrderItemsRequestOnResult getOrderItemsRequestOnResult) {
        this.getOrderItemsRequestOnResult = getOrderItemsRequestOnResult;
    }

    public interface GetOrderItemsRequestOnResult {
        void onGetOrderItemsRequestOnResult(boolean result, String data);
    }
}
