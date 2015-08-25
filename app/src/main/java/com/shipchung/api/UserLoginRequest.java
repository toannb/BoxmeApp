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
 * Created by ToanNB on 8/3/2015.
 */
public class UserLoginRequest {

    private UserLoginRequestOnResult userLoginRequestOnResult;

    public void execute(Context context, String userName, String password) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Username", userName);
            jsonObject.put("Password", password);
        } catch (JSONException e) {
            e.fillInStackTrace();
        }

        String data = jsonObject.toString();

//        RequestParams lvParams = new RequestParams();
//        lvParams.put("function", "login");
//        lvParams.put("params", data);
//        lvParams.put("os_system", Constants.OS_SYSTEM);
        StringEntity entity = null;
            try {
                entity = new StringEntity(jsonObject.toString());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        Log.d("api_login: ","execute: " + data);

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        client.post(context, Constants.URL_LOGIN, entity, "application/json", new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String content = new String(responseBody);
                Log.d("status_http", "onSuccess Login: " + statusCode);
                Log.d("content", "onSuccess Login: " + content);
                try {
                    if (userLoginRequestOnResult != null) {
                        userLoginRequestOnResult.onUserLoginRequestOnResult(true, content);
                    }
                } catch (NullPointerException e) {
                    Log.d("api_login: ", "onSuccess: " + "NullPointerException");
                    if (userLoginRequestOnResult != null) {
                        userLoginRequestOnResult.onUserLoginRequestOnResult(false, content);
                    }
                } catch (Exception e) {
                    Log.d("api_login: ", "onSuccess: " + "Exception");
                    if (userLoginRequestOnResult != null) {
                        userLoginRequestOnResult.onUserLoginRequestOnResult(false, content);
                    }
                } catch (OutOfMemoryError e) {
                    Log.d("api_login: ", "onSuccess: " + "OutOfMemoryError");
                    if (userLoginRequestOnResult != null) {
                        userLoginRequestOnResult.onUserLoginRequestOnResult(false, content);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("status_code","onFailure Login: " + statusCode);
                String content = new String(responseBody);
                Log.d("content", "onFailure Login: " + content);
                if (userLoginRequestOnResult != null) {
                    userLoginRequestOnResult.onUserLoginRequestOnResult(false, content);
                }
            }
        });
    }

    public void getUserLoginRequestOnResult(UserLoginRequestOnResult userLoginRequestOnResult) {
        this.userLoginRequestOnResult = userLoginRequestOnResult;
    }

    public interface UserLoginRequestOnResult {
        void onUserLoginRequestOnResult(boolean result, String data);
    }
}
