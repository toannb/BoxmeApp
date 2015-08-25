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
 * Created by ToanNB on 8/14/2015.
 */
public class MapPickupRequest {
    private MapPickupRequestOnResult mapPickupRequestOnResult;

    public void execute(Context context, String access_token, String pickup_code, String uid, String binid) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("UID", uid);
            jsonObject.put("BinID", binid);
            jsonObject.put("Action", "Update");
        } catch (JSONException e) {
            e.fillInStackTrace();
        }
        String data = jsonObject.toString();
        Log.d("data_mapping", "data: " + data);

        String url_map_pickup = Constants.URL_PICKUP + "/" + pickup_code +
                "?access_token=" + access_token;
        Log.d("link_mapping", url_map_pickup);
        StringEntity entity = null;
        try {
            entity = new StringEntity(data.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String contentType = "application/json";

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        client.put(context, url_map_pickup, entity, contentType, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String content = new String(responseBody);
                Log.d("success", "onSuccess Mapping: " + statusCode);
                try {
                    if (mapPickupRequestOnResult != null) {
                        mapPickupRequestOnResult.onMapPickupRequestOnResult(true, content);
                    }
                } catch (NullPointerException e) {
                    if (mapPickupRequestOnResult != null) {
                        mapPickupRequestOnResult.onMapPickupRequestOnResult(false, content);
                    }
                } catch (Exception e) {
                    if (mapPickupRequestOnResult != null) {
                        mapPickupRequestOnResult.onMapPickupRequestOnResult(false, content);
                    }
                } catch (OutOfMemoryError e) {
                    if (mapPickupRequestOnResult != null) {
                        mapPickupRequestOnResult.onMapPickupRequestOnResult(false, content);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("status_code", "onFailure MapPickupRequest: " + statusCode);
                String content = new String(responseBody);
                Log.d("fail", "onFailure Mapping: " + statusCode + "\n" + content);
                if (mapPickupRequestOnResult != null) {
                    mapPickupRequestOnResult.onMapPickupRequestOnResult(false, content);
                }
            }
        });
    }

    public void mapPickupRequestOnResult(MapPickupRequestOnResult mapPickupRequestOnResult) {
        this.mapPickupRequestOnResult = mapPickupRequestOnResult;
    }

    public interface MapPickupRequestOnResult {
        void onMapPickupRequestOnResult(boolean result, String data);
    }
}
