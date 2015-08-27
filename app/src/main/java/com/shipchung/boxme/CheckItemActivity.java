package com.shipchung.boxme;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shipchung.api.GetUidRequest;
import com.shipchung.bean.HistoryCheckItem;
import com.shipchung.config.Constants;
import com.shipchung.config.Variables;
import com.shipchung.custom.LoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import boxme.shipchung.com.boxmeapp.R;

/**
 * Created by ToanNB on 7/31/2015.
 */
public class CheckItemActivity extends Activity implements GetUidRequest.GetUidRequestOnResult{

    private TextView txtHeaderLabel;
    private ImageView imgProduct;
    private TextView txtScanUidItem;
    private TextView txtProductDetail;
    private TextView txtHistoryDetail;

    private LoadingDialog mLoadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_item_layout);

        initView();
        mLoadingDialog = new LoadingDialog(this);

    }

    public void initView(){
        txtHeaderLabel = (TextView) findViewById(R.id.check_item_header_label);
        imgProduct = (ImageView) findViewById(R.id.check_item_product_img);
        txtScanUidItem = (TextView) findViewById(R.id.check_item_scan_uid_item);
        txtProductDetail = (TextView) findViewById(R.id.check_item_product_detail);
        txtHistoryDetail = (TextView) findViewById(R.id.check_item_history_detail);

        txtHistoryDetail.setMovementMethod(new ScrollingMovementMethod());
    }

    private void showDialog(){
        if(mLoadingDialog != null && !mLoadingDialog.isShowing()){
            mLoadingDialog.show();
        }
    }
    private void hideDialog(){
        if(mLoadingDialog != null && mLoadingDialog.isShowing()){
            mLoadingDialog.dismiss();
        }
    }

    Timer timer = null;
    String str = "";
    boolean update;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((int) event.getUnicodeChar() > 0) {
            str += (char) event.getUnicodeChar();
            update = true;
        }

        if (keyCode == KeyEvent.KEYCODE_BACK){
            super.onBackPressed();
        }

        if (timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {
                    if (!update) {
                        // do somthing here
                        Message message = new Message();
                        message.obj = str;
                        updateHandler.sendMessage(message);
                        str = "";
                        timer.cancel();
                        timer = null;
                    } else {
                        update = false;
                    }
                }
            }, 0, 100);
        }

        return true;
    }

    Handler updateHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String sTemp = (String) msg.obj;
            if (sTemp == ""){
                //do nothing
            } else {
                String uid = sTemp.trim();
                txtScanUidItem.setText(uid);
                String first = sTemp.substring(0, 1);
                if (first.equalsIgnoreCase("U") && sTemp.length() > 1){
                    getUidDetail(uid);
                } else {
                    Toast.makeText(getApplicationContext(), "This is not UID!", Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    private void getUidDetail(String uid){
        showDialog();
        GetUidRequest getUidRequest = new GetUidRequest();
        getUidRequest.execute(this, Variables.mUserInfo.getAccessToken(), uid);
        getUidRequest.getUidRequestOnResult(this);
    }
    @Override
    public void onGetUidRequestOnResult(boolean result, String data) {
        hideDialog();
        Log.d("getUidRequestOnResult", "getUidRequestOnResult: " + data);

        try {
            JSONObject jsonObjectResult = new JSONObject(data);
            String productName = jsonObjectResult.getString(Constants.PRODUCTNAME);
//            String bsin = jsonObjectResult.getString(Constants.BSIN);
            String binid = jsonObjectResult.getString(Constants.BINID);
            String seller = jsonObjectResult.getString(Constants.SELLER);
            String size = jsonObjectResult.getString(Constants.VOLUME);
            int weight = jsonObjectResult.getInt(Constants.WEIGHT);
            String photoUrl = jsonObjectResult.getString(Constants.PHOTO);

            ArrayList<HistoryCheckItem> arrHistory = new ArrayList<>();
            JSONArray jsonArrayHistory = jsonObjectResult.getJSONArray(Constants.HISTORY);
            int jsonArrHistoryLen = jsonArrayHistory.length();
            for (int i = 0; i < jsonArrHistoryLen; i++){
                JSONObject jsonObjectTemp = jsonArrayHistory.getJSONObject(i);
                String employee = jsonObjectTemp.getString(Constants.EMPLOYEE);
                String history = jsonObjectTemp.getString(Constants.HISTORY);
                String location = jsonObjectTemp.getString(Constants.LOCATION);
                String created = jsonObjectTemp.getString(Constants.CREATED);
                arrHistory.add(new HistoryCheckItem(employee, history, location, created));
            }
            int arrHistorySize = arrHistory.size();
            String historyProduct = "";
            for (int j = 0; j < arrHistorySize; j++){
                String[] str = new String[arrHistorySize];
                str[j] = getResources().getString(R.string.stock_at_label) + arrHistory.get(j).getLocation()+ " " + "\n"
                        + getResources().getString(R.string.by_label) + " " + arrHistory.get(j).getEmployee() + " " +
                        getResources().getString(R.string.on_label) + " "+ getDate(Long.parseLong(arrHistory.get(j).getCreated())) + "\n"
                        +arrHistory.get(j).getHistory()+"\n\n";
                historyProduct += str[j];
            }
            txtHistoryDetail.setText(historyProduct);

            Log.d("photo", photoUrl);
            if (photoUrl != ""){
                new DownloadImageTask(imgProduct).execute(photoUrl);
            }

            String productDetail =    getResources().getString(R.string.product_label) + " "+ productName + "\n"
                                    + getResources().getString(R.string.binid_label)+":" + " "+ binid + "\n"
                                    + getResources().getString(R.string.seller_label) + " "+ seller + "\n"
                                    + getResources().getString(R.string.size_label) + " "+ size + "\n"
                                    + getResources().getString(R.string.weight_label) +  " "+weight + " "
                                    + getResources().getString(R.string.gram_label);
            txtProductDetail.setText(productDetail);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private String getDate(long timeStamp){
        try {
            DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        } catch (Exception e){
            return "Fail to convert date";
        }

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
