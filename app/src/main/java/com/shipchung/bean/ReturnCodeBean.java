package com.shipchung.bean;

/**
 * Created by ToanNB on 8/21/2015.
 */
public class ReturnCodeBean {
    private int status;
    private String updated;
    private String phone, name, created;
    private int receive;
    private String oc;
    private int item;
    private String rc;
    private int employee;
    private String tc;

    public ReturnCodeBean(){

    }

    public ReturnCodeBean(String rc, int item, String created, String tc){
        this.rc = rc;
        this.item = item;
        this.created = created;
        this.tc = tc;
    }

    public String getRc() {
        return rc;
    }

    public void setRc(String rc) {
        this.rc = rc;
    }

    public int getItem() {
        return item;
    }

    public void setItem(int item) {
        this.item = item;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}
