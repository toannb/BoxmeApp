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
 * Created by ToanNB on 8/11/2015.
 */
public class UndoPutAwayRequest {
    private UndoPutAwayRequestOnResult undoPutAwayRequestOnResult;

    public void execute(Context context, String access_token, String uid) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("access_token", access_token);
        } catch (JSONException e) {
            e.fillInStackTrace();
        }

        String url_undo_putaway = Constants.URL_PUT_AWAY_UNDO + uid +
                "?access_token=" + access_token;

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        client.delete(context, url_undo_putaway, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String content = new String(responseBody);
                try {
                    if (undoPutAwayRequestOnResult != null) {
                        undoPutAwayRequestOnResult.onUndoPutAwayRequestOnResult(true, content);
                    }
                } catch (NullPointerException e) {
                    if (undoPutAwayRequestOnResult != null) {
                        undoPutAwayRequestOnResult.onUndoPutAwayRequestOnResult(false, content);
                    }
                } catch (Exception e) {
                    if (undoPutAwayRequestOnResult != null) {
                        undoPutAwayRequestOnResult.onUndoPutAwayRequestOnResult(false, content);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("status_code", "onFailure undoPutaway: " + statusCode);
                String content = new String(responseBody);
                if (undoPutAwayRequestOnResult != null) {
                    undoPutAwayRequestOnResult.onUndoPutAwayRequestOnResult(false, content);
                }
            }
        });
    }

    public void undoPutAwayRequestOnResult(UndoPutAwayRequestOnResult undoPutAwayRequestOnResult) {
        this.undoPutAwayRequestOnResult = undoPutAwayRequestOnResult;
    }

    public interface UndoPutAwayRequestOnResult {
        void onUndoPutAwayRequestOnResult(boolean result, String data);
    }
}
