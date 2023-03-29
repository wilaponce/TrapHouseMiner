package com.rih.easymoney.model;

public class ProofModel {

    public int id;
    public String sender;
    public String mocions;

    public int getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMocions() {
        return mocions;
    }

    public void setMocions(String mocions) {
        this.mocions = mocions;
    }
}