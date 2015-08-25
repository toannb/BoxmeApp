package com.shipchung.bean;

/**
 * Created by ToanNB on 8/3/2015.
 */
public class PutAwayBean {

    private int Status;
    private String PutAwayCode;
    private int NumberUID;
    private String Assigner;
    private String StatusName;
    private String EmployeeName;
    private String CreateTime;

    public PutAwayBean(){};

    public PutAwayBean(int Status, String PutAwayCode, int NumberUID, String Assigner,
                       String StatusName, String EmployeeName, String CreateTime){
        this.Status = Status;
        this.PutAwayCode = PutAwayCode;
        this.NumberUID = NumberUID;
        this.Assigner = Assigner;
        this.StatusName = StatusName;
        this.EmployeeName = EmployeeName;
        this.CreateTime = CreateTime;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getPutAwayCode() {
        return PutAwayCode;
    }

    public void setPutAwayCode(String putAwayCode) {
        PutAwayCode = putAwayCode;
    }

    public int getNumberUID() {
        return NumberUID;
    }

    public void setNumberUID(int numberUID) {
        NumberUID = numberUID;
    }

    public String getAssigner() {
        return Assigner;
    }

    public void setAssigner(String assigner) {
        Assigner = assigner;
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
