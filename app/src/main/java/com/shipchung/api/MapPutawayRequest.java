package com.shipchung.api;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shipchung.boxme.PutawayMapingActivity;
import com.shipchung.config.Constants;
import com.shipchung.config.Variables;
import com.shipchung.util.Methods;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import boxme.shipchung.com.boxmeapp.R;

/**
 * Created by ToanNB on 8/11/2015.
 */
public class MapPutawayRequest {
    private MapPutAwayRequestOnResult mapPutAwayRequestOnResult;

    public void execute(final Context context, String access_token, String uid, String binid) {
        Variables.mStatusCode = -1;
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
                Variables.mStatusCode = statusCode;
                String content = new String(responseBody);
                String content1 = context.getResources().getString(R.string.success_mapping_putaway);
                Methods.successNotify(context, content1);
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
                Variables.mStatusCode = statusCode;
                Log.d("status_code", "onFailure mapPutawayRequest: " + statusCode);
                PutawayMapingActivity.hideDialog();
                Methods.checkError(context, statusCode);
                if (mapPutAwayRequestOnResult != null) {
//                    mapPutAwayRequestOnResult.onMapPutAwayRequestOnResult(false, content);
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
