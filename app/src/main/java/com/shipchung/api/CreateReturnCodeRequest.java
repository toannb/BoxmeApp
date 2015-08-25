package com.shipchung.api;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shipchung.config.Constants;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by ToanNB on 8/20/2015.
 */
public class CreateReturnCodeRequest {
    private CreateReturnCodeRequestOnResult createReturnCodeRequestOnResult;

    public void execute(Context context, String access_token, JSONArray jsonArray) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("data", jsonArray);
        } catch (JSONException e) {
            e.fillInStackTrace();
        }
        String data = jsonObject.toString();
        Log.d("data_createReturnCode", "data: " + data);

        String url_create_return_code = Constants.URL_CREATE_RETURN_CODE + "?access_token=" + access_token;
        Log.d("link_create_return_code", url_create_return_code);
        StringEntity entity = null;
        try {
            entity = new StringEntity(data.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String contentType = "application/json";

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        client.post(context, url_create_return_code, entity, contentType, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String content = new String(responseBody);
                Log.d("success", "onSuccess Create Return Code: " + statusCode);
                try {
                    if (createReturnCodeRequestOnResult != null) {
                        createReturnCodeRequestOnResult.onCreateReturnCodeRequestOnResult(true, content);
                    }
                } catch (NullPointerException e) {
                    if (createReturnCodeRequestOnResult != null) {
                        createReturnCodeRequestOnResult.onCreateReturnCodeRequestOnResult(false, content);
                    }
                } catch (Exception e) {
                    if (createReturnCodeRequestOnResult != null) {
                        createReturnCodeRequestOnResult.onCreateReturnCodeRequestOnResult(false, content);
                    }
                } catch (OutOfMemoryError e) {
                    if (createReturnCodeRequestOnResult != null) {
                        createReturnCodeRequestOnResult.onCreateReturnCodeRequestOnResult(false, content);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("status_code", "onFailure CreateReturnCode: " + statusCode);
                String content = new String(responseBody);
                Log.d("fail", "onFailure CreateReturnCode: " + statusCode + "\n" + content);
                if (createReturnCodeRequestOnResult != null) {
                    createReturnCodeRequestOnResult.onCreateReturnCodeRequestOnResult(false, content);
                }
            }
        });
    }

    public void createReturnCodeRequestOnResult(CreateReturnCodeRequestOnResult createReturnCodeRequestOnResult) {
        this.createReturnCodeRequestOnResult = createReturnCodeRequestOnResult;
    }

    public interface CreateReturnCodeRequestOnResult {
        void onCreateReturnCodeRequestOnResult(boolean result, String data);
    }
}
