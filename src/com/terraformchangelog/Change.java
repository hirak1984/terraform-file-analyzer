package com.terraformchangelog;

public class Change {
    private String address;
    private String id;
    private String type;
    private String action;

    public Change() {

    }

    public Change(String address, String type, String action) {
        this.address = address;
        this.type = type;
        this.action = action;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }     

}
