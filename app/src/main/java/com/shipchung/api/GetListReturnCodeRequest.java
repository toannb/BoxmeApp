package com.shipchung.api;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shipchung.config.Constants;
import com.shipchung.util.Methods;

import org.apache.http.Header;

/**
 * Created by ToanNB on 8/21/2015.
 */
public class GetListReturnCodeRequest {
    private GetListReturnCodeRequestOnResult getListReturnCodeRequestOnResult;

    public void execute(final Context context, String access_token) {
        String url_get_list_return_code = Constants.URL_CREATE_RETURN_CODE + "?access_token=" + access_token;
        Log.d("link_get_return_code", url_get_list_return_code);
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        client.get(context, url_get_list_return_code, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String content = new String(responseBody);
                Log.d("success", "onSuccess getListReturnCode: " + statusCode);
                try {
                    if (getListReturnCodeRequestOnResult != null) {
                        getListReturnCodeRequestOnResult.onGetListReturnCodeRequestOnResult(true, content);
                    }
                } catch (NullPointerException e) {
                    if (getListReturnCodeRequestOnResult != null) {
                        getListReturnCodeRequestOnResult.onGetListReturnCodeRequestOnResult(false, content);
                    }
                } catch (Exception e) {
                    if (getListReturnCodeRequestOnResult != null) {
                        getListReturnCodeRequestOnResult.onGetListReturnCodeRequestOnResult(false, content);
                    }
                } catch (OutOfMemoryError e) {
                    if (getListReturnCodeRequestOnResult != null) {
                        getListReturnCodeRequestOnResult.onGetListReturnCodeRequestOnResult(false, content);
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("status_code", "onFailure getListReturnCode: " + statusCode);
                Methods.checkError(context, statusCode);
                if (getListReturnCodeRequestOnResult != null) {
                }
            }
        });
    }

    public void getListReturnCodeRequestOnResult(GetListReturnCodeRequestOnResult getListReturnCodeRequestOnResult) {
        this.getListReturnCodeRequestOnResult = getListReturnCodeRequestOnResult;
    }

    public interface GetListReturnCodeRequestOnResult {
        void onGetListReturnCodeRequestOnResult(boolean result, String data);
    }
}
