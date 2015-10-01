package com.shipchung.bean;

/**
 * Created by ToanNB on 9/21/2015.
 */
public class LineItem {
    private String orderNumber;
    private String sku;
    private int quantityPickup;
    private int quantity;
    private String productName;
    private String binID;
    private String bsin;

    public LineItem(){

    }

    public LineItem(String orderNumber, String sku, int quantityPickup, int quantity, String productName){
        this.orderNumber = orderNumber;
        this.sku = sku;
        this.quantityPickup = quantityPickup;
        this.quantity = quantity;
        this.productName = productName;
    }

    public LineItem(String orderNumber, String binID, int quantityPickup, int quantity, String bsin, String productName){
        this.orderNumber = orderNumber;
        this.binID = binID;
        this.quantityPickup = quantityPickup;
        this.quantity = quantity;
        this.bsin = bsin;
        this.productName = productName;
    }

    public void setBsin(String bsin) {
        this.bsin = bsin;
    }

    public String getBsin() {
        return bsin;
    }

    public void setBinID(String binID) {
        this.binID = binID;
    }

    public String getBinID() {
        return binID;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getSku() {
        return sku;
    }

    public void setQuantityPickup(int quantityPickup) {
        this.quantityPickup = quantityPickup;
    }

    public int getQuantityPickup() {
        return quantityPickup;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductName() {
        return productName;
    }
}
