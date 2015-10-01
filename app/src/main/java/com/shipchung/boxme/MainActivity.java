package com.shipchung.boxme;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.shipchung.api.UserLoginRequest;
import com.shipchung.bean.UserInfoBean;
import com.shipchung.config.Constants;
import com.shipchung.config.Variables;
import com.shipchung.custom.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import boxme.shipchung.com.boxmeapp.R;


public class MainActivity extends Activity implements UserLoginRequest.UserLoginRequestOnResult {

    private EditText txtEmailAccount;
    private EditText txtPassword;
    private Button btnLogin;
    private int errorCode;
    private String mUsername;
    private String mPassword;

    private static LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingDialog = new LoadingDialog(MainActivity.this);
        initView();
    }

    public void initView() {
        txtEmailAccount = (EditText) findViewById(R.id.main_screen_account_email_txt);
        txtPassword = (EditText) findViewById(R.id.main_screen_password_txt);
        btnLogin = (Button) findViewById(R.id.main_screen_login_btn);

        SharedPreferences prefs = getSharedPreferences("UserData", 0);
        String username = prefs.getString("username","");
        String pwd = prefs.getString("password","");
        if (username != "" && pwd != ""){
            txtEmailAccount.setText(username);
            txtPassword.setText(pwd);
            mUsername = username;
            mPassword = pwd;
            userLogin();
        }
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUsername = txtEmailAccount.getText().toString();
                mPassword = txtPassword.getText().toString();
                if (mUsername.equals("")){
                    Toast.makeText(getApplicationContext(), "Enter username, please!", Toast.LENGTH_LONG).show();
                } else if (mPassword.equals("")){
                    Toast.makeText(getApplicationContext(), "Enter password, please!", Toast.LENGTH_LONG).show();
                } else {
                    SharedPreferences prefs = getSharedPreferences("UserData", 0);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("username", mUsername);
                    editor.putString("password", mPassword);
                    editor.commit();
                    userLogin();
                }
            }
        });
    }

    private void showDialog() {
        if (mLoadingDialog != null && !mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
    }

    public static void hideDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    private void userLogin() {
        showDialog();
        UserLoginRequest userLoginRequest = new UserLoginRequest();
        userLoginRequest.execute(this, mUsername, mPassword);
        userLoginRequest.getUserLoginRequestOnResult(this);
    }


    @Override
    public void onUserLoginRequestOnResult(boolean result, String data) {
        hideDialog();
        Log.d("api", "onUserLoginRequestOnResult : " + data);

        Variables.mUserInfo = new UserInfoBean();
        try {
            JSONObject jsonResult = new JSONObject(data);
            int status_code = jsonResult.getInt(Constants.STATUS_CODE);
            errorCode = status_code;
            String message = jsonResult.getString(Constants.MESSAGE);
            if (status_code != 1) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            } else {
                String token_type = jsonResult.getString(Constants.TOKEN_TYPE);
                String scope = jsonResult.getString(Constants.SCOPE);
                String access_token = jsonResult.getString(Constants.ACCESS_TOKEN);
                String expire_in = jsonResult.getString(Constants.EXPIRES_IN);
                String full_name = jsonResult.getString(Constants.FULLNAME);
                String refresh_token = jsonResult.getString(Constants.REFRESH_TOKEN);

                Variables.mUserInfo = new UserInfoBean(token_type, scope,
                        access_token, status_code, message, expire_in, full_name, refresh_token);

                Intent intent = new Intent(getApplicationContext(), MenuFunctionActivity.class);
                startActivity(intent);
            }

        } catch (JSONException e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
