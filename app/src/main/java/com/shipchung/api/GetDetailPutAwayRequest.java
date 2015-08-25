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
 * Created by ToanNB on 8/5/2015.
 */
public class GetDetailPutAwayRequest {

    private GetDetailPutAwayRequestOnResult detailPutAwayRequestOnResult;

    public void execute(Context context, String access_token, String status, String key, String code_put_away) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("access_token", access_token);
            jsonObject.put("status", status);
            jsonObject.put("key", key);
        } catch (JSONException e) {
            e.fillInStackTrace();
        }

        String data = jsonObject.toString();

        RequestParams lvParams = new RequestParams();
        lvParams.put("params", data);
        Log.d("api_login: ","execute: " + data);
        String url_detail_putaway = Constants.URL_PUT_AWAY + code_put_away +
                "?access_token=" + access_token+ "&status=assigned&key=mapping";

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        client.get(context, url_detail_putaway, lvParams, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String content = new String(responseBody);
                Log.d("status_http", "onSuccess getDetailPutAway: " + statusCode);
                Log.d("content ", "onSuccess getDetailPutAway: " + content);
                try {
                    if (detailPutAwayRequestOnResult != null) {
                        detailPutAwayRequestOnResult.onGetDetailPutAwayRequestOnResult(true, content);
                    }
                } catch (NullPointerException e) {
                    Log.d("api_login: ", "onSuccess: " + "NullPointerException");
                    if (detailPutAwayRequestOnResult != null) {
                        detailPutAwayRequestOnResult.onGetDetailPutAwayRequestOnResult(false, content);
                    }
                } catch (Exception e) {
                    Log.d("api_login: ", "onSuccess: " + "Exception");
                    if (detailPutAwayRequestOnResult != null) {
                        detailPutAwayRequestOnResult.onGetDetailPutAwayRequestOnResult(false, content);
                    }
                } catch (OutOfMemoryError e) {
                    Log.d("api_login: ", "onSuccess: " + "OutOfMemoryError");
                    if (detailPutAwayRequestOnResult != null) {
                        detailPutAwayRequestOnResult.onGetDetailPutAwayRequestOnResult(false, content);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("status_code", "onFailure getDetailPutaway: " + statusCode);
                String content = new String(responseBody);
                Log.d("status_http", "onFailure getDetailPutAway: " + statusCode);
                if (detailPutAwayRequestOnResult != null) {
                    Log.d("api_login: ", "onFailure: " + content);
                    detailPutAwayRequestOnResult.onGetDetailPutAwayRequestOnResult(false, content);
                }
            }
        });
    }

    public void getDetailPutAwayRequestOnResult(GetDetailPutAwayRequestOnResult detailPutAwayRequestOnResult) {
        this.detailPutAwayRequestOnResult = detailPutAwayRequestOnResult;
    }

    public interface GetDetailPutAwayRequestOnResult {
        void onGetDetailPutAwayRequestOnResult(boolean result, String data);
    }
}
