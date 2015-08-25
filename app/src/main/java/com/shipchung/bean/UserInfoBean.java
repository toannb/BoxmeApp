package com.shipchung.bean;

/**
 * Created by ToanNB on 8/3/2015.
 */
public class UserInfoBean {
    private String mTokenType;
    private String mScope;
    private String mAccessToken;
    private int mStatusCode;
    private String mMessage;
    private String mExpireIn;
    private String mFullName;
    private String mRefreshToken;

    public UserInfoBean(){};

    public UserInfoBean(String tokenType, String scope, String accessToken, int statusCode, String message,
                        String expireIn, String fullName, String refreshToken){
        this.mTokenType = tokenType;
        this.mScope = scope;
        this.mAccessToken = accessToken;
        this.mStatusCode = statusCode;
        this.mMessage = message;
        this.mExpireIn = expireIn;
        this.mFullName = fullName;
        this.mRefreshToken = refreshToken;
    }

    public String getTokenType() {
        return mTokenType;
    }

    public void setTokenType(String mTokenType) {
        this.mTokenType = mTokenType;
    }

    public String getScope() {
        return mScope;
    }

    public void setScope(String mScope) {
        this.mScope = mScope;
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public void setAccessToken(String mAccessToken) {
        this.mAccessToken = mAccessToken;
    }

    public int getStatusCode() {
        return mStatusCode;
    }

    public void setStatusCode(int mStatusCode) {
        this.mStatusCode = mStatusCode;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public String getExpireIn() {
        return mExpireIn;
    }

    public void setExpireIn(String mExpireIn) {
        this.mExpireIn = mExpireIn;
    }

    public String getFullName() {
        return mFullName;
    }

    public void setFullName(String mFullName) {
        this.mFullName = mFullName;
    }

    public String getRefreshToken() {
        return mRefreshToken;
    }

    public void setRefreshToken(String mRefreshToken) {
        this.mRefreshToken = mRefreshToken;
    }
}
