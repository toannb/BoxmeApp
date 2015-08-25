package com.shipchung.bean;

/**
 * Created by ToanNB on 8/5/2015.
 */
public class UIDItemBean {
    private int Status, ProductStatus;
    private String BinID, UID, ProductName, ProductImage, StatusName, CreateTime;
    private String UpdateTime, ProductStatusName, BSIN, Order;
    private String sku;
    private String statusReturn, shipmentCode;

    public UIDItemBean(){};

    public UIDItemBean(int status, String binID, String uid, String productName,
                       String productImage, String statusName, String createTime){
        this.Status = status;
        this.BinID = binID;
        this.UID = uid;
        this.ProductName = productName;
        this.ProductImage = productImage;
        this.StatusName = statusName;
        this.CreateTime = createTime;
    }

    public UIDItemBean(String sku, String uid, String productName){
        this.sku = sku;
        this.UID = uid;
        this.ProductName = productName;
    }

    public UIDItemBean(String sku, String uid, String statusReturn, String productName, String shipmentCode){
        this.sku = sku;
        this.UID = uid;
        this.statusReturn = statusReturn;
        this.ProductName = productName;
        this.shipmentCode = shipmentCode;
    }

    public UIDItemBean(String sku, String uid, String statusReturn, String productName){
        this.sku = sku;
        this.UID = uid;
        this.statusReturn = statusReturn;
        this.ProductName = productName;
    }

    public UIDItemBean(int status, String binid, String updateTime, String uid, String productStatusName,
                       String bsin, String statusName, String order, int productStatus, String productName){
        this.Status = status;
        this.BinID = binid;
        this.UpdateTime = updateTime;
        this.UID = uid;
        this.ProductStatusName = productStatusName;
        this.BSIN = bsin;
        this.StatusName = statusName;
        this.Order = order;
        this.ProductStatus = productStatus;
        this.ProductName = productName;
    }

    public String getStatusReturn() {
        return statusReturn;
    }

    public void setStatusReturn(String statusReturn) {
        this.statusReturn = statusReturn;
    }

    public String getShipmentCode() {
        return shipmentCode;
    }

    public void setShipmentCode(String shipmentCode) {
        this.shipmentCode = shipmentCode;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getOrder() {
        return Order;
    }

    public void setOrder(String order) {
        Order = order;
    }

    public String getUpdateTime() {
        return UpdateTime;
    }

    public void setUpdateTime(String updateTime) {
        UpdateTime = updateTime;
    }

    public int getProductStatus() {
        return ProductStatus;
    }

    public void setProductStatus(int productStatus) {
        ProductStatus = productStatus;
    }

    public String getProductStatusName() {
        return ProductStatusName;
    }

    public void setProductStatusName(String productStatusName) {
        ProductStatusName = productStatusName;
    }

    public String getBSIN() {
        return BSIN;
    }

    public void setBSIN(String BSIN) {
        this.BSIN = BSIN;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getBinID() {
        return BinID;
    }

    public void setBinID(String binID) {
        BinID = binID;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getProductImage() {
        return ProductImage;
    }

    public void setProductImage(String productImage) {
        ProductImage = productImage;
    }

    public String getStatusName() {
        return StatusName;
    }

    public void setStatusName(String statusName) {
        StatusName = statusName;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }
}