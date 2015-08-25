package com.shipchung.api;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shipchung.config.Constants;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ToanNB on 8/19/2015.
 */
public class GetListUidShipmentRequest {

    private GetListUidShipmentRequestOnResult listUidShipmentRequestOnResult;

    public void execute(Context context, String access_token, String shipment_code, int page) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("access_token", access_token);
        } catch (JSONException e) {
            e.fillInStackTrace();
        }

        String data = jsonObject.toString();

        RequestParams lvParams = new RequestParams();
        lvParams.put("params", data);
        Log.d("getListUidShipment: ","execute: " + data);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        String url_get_list_uid_shipment = Constants.URL_GET_UID_SHIPMENT + "?access_token=" + access_token+ "&code="+ shipment_code + "&page="+ page;

        client.get(context, url_get_list_uid_shipment, lvParams, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("status_http: ", "onSuccess getListUidShipment: " + statusCode);
                String content = new String(responseBody);
                Log.d("content", "getListUidShipment: " + content);
                try {
                    if (listUidShipmentRequestOnResult != null) {
                        listUidShipmentRequestOnResult.onGetListUidShipmentRequestOnResult(true, content);
                    }
                } catch (NullPointerException e) {
                    Log.d("getListUidShipment: ", "onSuccess: " + "NullPointerException");
                    if (listUidShipmentRequestOnResult != null) {
                        listUidShipmentRequestOnResult.onGetListUidShipmentRequestOnResult(false, content);
                    }
                } catch (Exception e) {
                    Log.d("getListUidShipment: ", "onSuccess: " + "Exception");
                    if (listUidShipmentRequestOnResult != null) {
                        listUidShipmentRequestOnResult.onGetListUidShipmentRequestOnResult(false, content);
                    }
                } catch (OutOfMemoryError e) {
                    Log.d("getListUidShipment: ", "onSuccess: " + "OutOfMemoryError");
                    if (listUidShipmentRequestOnResult != null) {
                        listUidShipmentRequestOnResult.onGetListUidShipmentRequestOnResult(false, content);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("status_code", "onFailure getListUidShipment: " + statusCode);
                String content = new String(responseBody);
                if (listUidShipmentRequestOnResult != null) {
                    Log.d("api_login: ", "onFailure getListUidShipment: " + content);
                    listUidShipmentRequestOnResult.onGetListUidShipmentRequestOnResult(false, content);
                }
            }
        });
    }

    public void getListUidShipmentRequestOnResult(GetListUidShipmentRequestOnResult listUidShipmentRequestOnResult) {
        this.listUidShipmentRequestOnResult = listUidShipmentRequestOnResult;
    }

    public interface GetListUidShipmentRequestOnResult {
        void onGetListUidShipmentRequestOnResult(boolean result, String data);
    }
}
