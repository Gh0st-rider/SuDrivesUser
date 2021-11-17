package com.sudrives.sudrives.model;

public class TransactionModel {

    private int transation_id;
    private String before_transation_amount, after_transation_amount, transation_amount, status, transation_type, transation_date;


    public TransactionModel(int transation_id, String before_transation_amount, String after_transation_amount, String transation_amount, String status, String transation_type, String transation_date) {
        this.transation_id = transation_id;
        this.before_transation_amount = before_transation_amount;
        this.after_transation_amount = after_transation_amount;
        this.transation_amount = transation_amount;
        this.status = status;
        this.transation_type = transation_type;
        this.transation_date = transation_date;
    }

    public int getTransation_id() {
        return transation_id;
    }

    public void setTransation_id(int transation_id) {
        this.transation_id = transation_id;
    }

    public String getBefore_transation_amount() {
        return before_transation_amount;
    }

    public void setBefore_transation_amount(String before_transation_amount) {
        this.before_transation_amount = before_transation_amount;
    }

    public String getAfter_transation_amount() {
        return after_transation_amount;
    }

    public void setAfter_transation_amount(String after_transation_amount) {
        this.after_transation_amount = after_transation_amount;
    }

    public String getTransation_amount() {
        return transation_amount;
    }

    public void setTransation_amount(String transation_amount) {
        this.transation_amount = transation_amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransation_type() {
        return transation_type;
    }

    public void setTransation_type(String transation_type) {
        this.transation_type = transation_type;
    }

    public String getTransation_date() {
        return transation_date;
    }

    public void setTransation_date(String transation_date) {
        this.transation_date = transation_date;
    }
}
