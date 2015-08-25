package com.shipchung.bean;

/**
 * Created by ToanNB on 8/17/2015.
 */
public class ChangeLocationBean {
    private String status;
    private String uid;
    private String bsin;
    private String oldBinId;
    private String productName;
    private String newBinId;

    public ChangeLocationBean(){}
    public ChangeLocationBean(String status, String uid, String bsin, String oldBinId, String productName, String newBinId){
        this.status = status;
        this.uid = uid;
        this.bsin = bsin;
        this.oldBinId = oldBinId;
        this.productName = productName;
        this.newBinId = newBinId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getBsin() {
        return bsin;
    }

    public void setBsin(String bsin) {
        this.bsin = bsin;
    }

    public String getOldBinId() {
        return oldBinId;
    }

    public void setOldBinId(String oldBinId) {
        this.oldBinId = oldBinId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getNewBinId() {
        return newBinId;
    }

    public void setNewBinId(String newBinId) {
        this.newBinId = newBinId;
    }
}
