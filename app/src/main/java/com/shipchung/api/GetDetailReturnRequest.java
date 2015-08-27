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

/**
 * Created by ToanNB on 8/21/2015.
 */
public class GetDetailReturnRequest {

    private GetDetailReturnRequestOnResult detailReturnRequestOnResult;

    public void execute(final Context context, String access_token, String return_code) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("access_token", access_token);
        } catch (JSONException e) {
            e.fillInStackTrace();
        }

        String data = jsonObject.toString();

        Log.d("api_login: ","execute: " + data);
        String url_detail_return = Constants.URL_CREATE_RETURN_CODE + "/" + return_code +
                "?access_token=" + access_token;

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        client.get(context, url_detail_return, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String content = new String(responseBody);
                Log.d("status_http", "onSuccess getDetailReturn: " + statusCode);
                Log.d("content ", "onSuccess getDetailReturn: " + content);
                try {
                    if (detailReturnRequestOnResult != null) {
                        detailReturnRequestOnResult.onGetDetailReturnRequestOnResult(true, content);
                    }
                } catch (NullPointerException e) {
                    Log.d("api_login: ", "onSuccess: " + "NullPointerException");
                    if (detailReturnRequestOnResult != null) {
                        detailReturnRequestOnResult.onGetDetailReturnRequestOnResult(false, content);
                    }
                } catch (Exception e) {
                    Log.d("api_login: ", "onSuccess: " + "Exception");
                    if (detailReturnRequestOnResult != null) {
                        detailReturnRequestOnResult.onGetDetailReturnRequestOnResult(false, content);
                    }
                } catch (OutOfMemoryError e) {
                    Log.d("api_login: ", "onSuccess: " + "OutOfMemoryError");
                    if (detailReturnRequestOnResult != null) {
                        detailReturnRequestOnResult.onGetDetailReturnRequestOnResult(false, content);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("status_code", "onFailure getDetailReturn: " + statusCode);
                Methods.checkError(context, statusCode);
                if (detailReturnRequestOnResult != null) {
                }
            }
        });
    }

    public void getDetailReturnRequestOnResult(GetDetailReturnRequestOnResult detailReturnRequestOnResult) {
        this.detailReturnRequestOnResult = detailReturnRequestOnResult;
    }

    public interface GetDetailReturnRequestOnResult {
        void onGetDetailReturnRequestOnResult(boolean result, String data);
    }
}
