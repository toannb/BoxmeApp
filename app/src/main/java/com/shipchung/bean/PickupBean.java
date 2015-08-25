package com.shipchung.bean;

/**
 * Created by ToanNB on 8/3/2015.
 */
public class PickupBean {

    private int TotalUID;
    private int TotalSKU;
    private int PickupID;
    private String PickupCode;
    private String AssignName;
    private String StatusName;
    private int Status;
    private int TotalOrder;
    private String UpdateTime;
    private String EmployeeCode;
    private String EmployeeName;
    private String CreateTime;

    public PickupBean() {
    }

    public PickupBean(int totalUID, int totalSKU, int pickupID, String pickupCode, String StatusName, int totalOrder,
                      String employeeName, int Status, String updateTime, String assignName, String employeeCode, String createTime) {
        this.TotalUID = totalUID;
        this.TotalSKU = totalSKU;
        this.PickupID = pickupID;
        this.PickupCode = pickupCode;
        this.TotalOrder = totalOrder;
        this.AssignName = assignName;
        this.StatusName = StatusName;
        this.Status = Status;
        this.UpdateTime = updateTime;
        this.EmployeeName = employeeName;
        this.EmployeeCode = employeeCode;
        this.CreateTime = createTime;
    }

    public int getTotalUID() {
        return TotalUID;
    }

    public void setTotalUID(int totalUID) {
        TotalUID = totalUID;
    }

    public int getTotalSKU() {
        return TotalSKU;
    }

    public void setTotalSKU(int totalSKU) {
        TotalSKU = totalSKU;
    }

    public int getPickupID() {
        return PickupID;
    }

    public void setPickupID(int pickupID) {
        PickupID = pickupID;
    }

    public int getTotalOrder() {
        return TotalOrder;
    }

    public void setTotalOrder(int totalOrder) {
        TotalOrder = totalOrder;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getPickupCode() {
        return PickupCode;
    }

    public void setPickupCode(String pickupCode) {
        PickupCode = pickupCode;
    }

    public String getAssignName() {
        return AssignName;
    }

    public void setAssignName(String assignName) {
        AssignName = assignName;
    }

    public String getStatusName() {
        return StatusName;
    }

    public void setStatusName(String statusName) {
        StatusName = statusName;
    }

    public String getEmployeeName() {
        return EmployeeName;
    }

    public void setEmployeeName(String employeeName) {
        EmployeeName = employeeName;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }
}
