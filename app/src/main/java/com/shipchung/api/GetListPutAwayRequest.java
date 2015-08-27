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
 * Created by ToanNB on 8/3/2015.
 */
public class GetListPutAwayRequest {

    private GetListPutAwayRequestOnResult listPutAwayRequestOnResult;

    public void execute(final Context context, String access_token, String status, int page) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("access_token", access_token);
        } catch (JSONException e) {
            e.fillInStackTrace();
        }

        String data = jsonObject.toString();

        RequestParams lvParams = new RequestParams();
        lvParams.put("params", data);
        Log.d("api_login: ","execute: " + data);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        String url_get_list_putaway = Constants.GET_LIST_PUT_AWAY + "?access_token=" + access_token+ "&status=assigned"+ "&page="+ page;

        client.get(context, url_get_list_putaway, lvParams, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("status_http: ", "onSuccess getListPutaway: " + statusCode);
                String content = new String(responseBody);
                Log.d("content", "getListPutaway: " + content);
                try {
                    if (listPutAwayRequestOnResult != null) {
                        listPutAwayRequestOnResult.onGetListPutAwayRequestOnResult(true, content);
                    }
                } catch (NullPointerException e) {
                    Log.d("api_login: ", "onSuccess: " + "NullPointerException");
                    if (listPutAwayRequestOnResult != null) {
                        listPutAwayRequestOnResult.onGetListPutAwayRequestOnResult(false, content);
                    }
                } catch (Exception e) {
                    Log.d("api_login: ", "onSuccess: " + "Exception");
                    if (listPutAwayRequestOnResult != null) {
                        listPutAwayRequestOnResult.onGetListPutAwayRequestOnResult(false, content);
                    }
                } catch (OutOfMemoryError e) {
                    Log.d("api_login: ", "onSuccess: " + "OutOfMemoryError");
                    if (listPutAwayRequestOnResult != null) {
                        listPutAwayRequestOnResult.onGetListPutAwayRequestOnResult(false, content);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("status_code", "onFailure getListPutaway: " + statusCode);
                Methods.checkError(context, statusCode);
                if (listPutAwayRequestOnResult != null) {
                }
            }
        });
    }

    public void getListPutAwayRequestOnResult(GetListPutAwayRequestOnResult listPutAwayRequestOnResult) {
        this.listPutAwayRequestOnResult = listPutAwayRequestOnResult;
    }

    public interface GetListPutAwayRequestOnResult {
        void onGetListPutAwayRequestOnResult(boolean result, String data);
    }
}
