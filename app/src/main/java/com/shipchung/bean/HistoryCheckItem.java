package com.shipchung.bean;

/**
 * Created by ToanNB on 8/17/2015.
 */
public class HistoryCheckItem {
    private String employee;
    private String history;
    private String location;
    private String created;

    public HistoryCheckItem(String employee, String history, String location, String created){
        this.employee = employee;
        this.history = history;
        this.location = location;
        this.created = created;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}
