package com.shipchung.api;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shipchung.config.Constants;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by ToanNB on 8/11/2015.
 */
public class MapPutawayRequest {
    private MapPutAwayRequestOnResult mapPutAwayRequestOnResult;

    public void execute(Context context, String access_token, String uid, String binid) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("BinIDSuggestion", binid);
            jsonObject.put("BinIDUpdate", binid);
            jsonObject.put("Confirm", 1);
        } catch (JSONException e) {
            e.fillInStackTrace();
        }
        String data = jsonObject.toString();
        Log.d("data_mapping", "data: " + data);

        RequestParams lvParams = new RequestParams();
        lvParams.put("params", data);
        String url_map_putaway = Constants.URL_PUT_AWAY_MAPPING + uid +
                "?access_token=" + access_token;
        Log.d("link_mapping", url_map_putaway);
        StringEntity entity = null;
        try {
            entity = new StringEntity(data.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String contentType = "application/json";

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        client.put(context, url_map_putaway, entity, contentType, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String content = new String(responseBody);
                Log.d("success", "onSuccess Mapping: " + statusCode);
                try {
                    if (mapPutAwayRequestOnResult != null) {
                        mapPutAwayRequestOnResult.onMapPutAwayRequestOnResult(true, content);
                    }
                } catch (NullPointerException e) {
                    if (mapPutAwayRequestOnResult != null) {
                        mapPutAwayRequestOnResult.onMapPutAwayRequestOnResult(false, content);
                    }
                } catch (Exception e) {
                    if (mapPutAwayRequestOnResult != null) {
                        mapPutAwayRequestOnResult.onMapPutAwayRequestOnResult(false, content);
                    }
                } catch (OutOfMemoryError e) {
                    if (mapPutAwayRequestOnResult != null) {
                        mapPutAwayRequestOnResult.onMapPutAwayRequestOnResult(false, content);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("status_code", "onFailure mapPutawayRequest: " + statusCode);
                String content = new String(responseBody);
                Log.d("fail", "onFailure Mapping: " + statusCode + "\n" + content);
                if (mapPutAwayRequestOnResult != null) {
                    mapPutAwayRequestOnResult.onMapPutAwayRequestOnResult(false, content);
                }
            }
        });
    }

    public void mapPutAwayRequestOnResult(MapPutAwayRequestOnResult mapPutAwayRequestOnResult) {
        this.mapPutAwayRequestOnResult = mapPutAwayRequestOnResult;
    }

    public interface MapPutAwayRequestOnResult {
        void onMapPutAwayRequestOnResult(boolean result, String data);
    }
}
