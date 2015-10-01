package com.shipchung.boxme;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.shipchung.config.Variables;

import boxme.shipchung.com.boxmeapp.R;

/**
 * Created by ToanNB on 7/31/2015.
 */
public class MenuFunctionActivity extends Activity implements View.OnClickListener{

    private TextView txtUserName;
    private TextView txtPutaway;
    private TextView txtPickup;
    private TextView txtChangeLocation;
    private TextView txtCheckItem;
    private TextView txtCreateReturnCode;
    private TextView txtReturn;
    private TextView txtLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_functions_layout);

        initView();
    }

    public void initView(){
        txtUserName = (TextView) findViewById(R.id.username_txt);
        txtPutaway = (TextView) findViewById(R.id.putaway_txt);
        txtPickup = (TextView) findViewById(R.id.pickup_txt);
        txtChangeLocation = (TextView) findViewById(R.id.change_location_txt);
        txtCheckItem = (TextView) findViewById(R.id.check_item_txt);
        txtCreateReturnCode = (TextView) findViewById(R.id.create_return_code_txt);
        txtReturn = (TextView) findViewById(R.id.return_txt);
        txtLogout = (TextView) findViewById(R.id.logout_txt);
        txtUserName.setText(Variables.mUserInfo.getFullName());

        txtPutaway.setText(Html.fromHtml(getResources().getString(R.string.function_putaway)));
        txtPickup.setText(Html.fromHtml(getResources().getString(R.string.function_pickup)));
        txtChangeLocation.setText(Html.fromHtml(getResources().getString(R.string.function_change_location)));
        txtCheckItem.setText(Html.fromHtml(getResources().getString(R.string.function_check_item)));
        txtCreateReturnCode.setText(Html.fromHtml(getResources().getString(R.string.function_create_return_code)));
        txtReturn.setText(Html.fromHtml(getResources().getString(R.string.function_return)));
        txtLogout.setText(Html.fromHtml(getResources().getString(R.string.function_logout)));

        txtPutaway.setOnClickListener(this);
        txtPickup.setOnClickListener(this);
        txtChangeLocation.setOnClickListener(this);
        txtCheckItem.setOnClickListener(this);
        txtCreateReturnCode.setOnClickListener(this);
        txtReturn.setOnClickListener(this);
        txtLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.putaway_txt:
                Intent intent = new Intent(getApplicationContext(), PutawayActivity.class);
                startActivity(intent);
                break;
            case R.id.pickup_txt:
                Intent intent1 = new Intent(getApplicationContext(), PickupActivity.class);
                startActivity(intent1);
                break;
            case R.id.change_location_txt:
                Intent intent2 = new Intent(getApplicationContext(), ChangeLocationActivity.class);
                startActivity(intent2);
                break;
            case R.id.check_item_txt:
                Intent intent3 = new Intent(getApplicationContext(), CheckItemActivity.class);
                startActivity(intent3);
                break;
            case R.id.create_return_code_txt:
                Intent intent4 = new Intent(getApplicationContext(), CreateReturnCodeActivity.class);
                startActivity(intent4);
                break;
            case R.id.return_txt:
                Intent intent5 = new Intent(getApplicationContext(), ReturnActivity.class);
                startActivity(intent5);
                break;
            case R.id.logout_txt:
                SharedPreferences.Editor editor = getSharedPreferences("UserData", 0).edit();
                editor.clear(); //clear all stored data
                editor.commit();
                Intent intentLogout = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intentLogout);
                break;
        }
    }
}
