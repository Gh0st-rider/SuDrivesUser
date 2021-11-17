package com.sudrives.sudrives.model;

public class RecentBookingModel {

    int id;
    String book_from_address,book_from_lat,book_from_long,book_to_address,book_to_lat,book_to_long;


    public RecentBookingModel(int id, String book_from_address, String book_from_lat, String book_from_long, String book_to_address, String book_to_lat, String book_to_long) {
        this.id = id;
        this.book_from_address = book_from_address;
        this.book_from_lat = book_from_lat;
        this.book_from_long = book_from_long;
        this.book_to_address = book_to_address;
        this.book_to_lat = book_to_lat;
        this.book_to_long = book_to_long;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBook_from_address() {
        return book_from_address;
    }

    public void setBook_from_address(String book_from_address) {
        this.book_from_address = book_from_address;
    }

    public String getBook_from_lat() {
        return book_from_lat;
    }

    public void setBook_from_lat(String book_from_lat) {
        this.book_from_lat = book_from_lat;
    }

    public String getBook_from_long() {
        return book_from_long;
    }

    public void setBook_from_long(String book_from_long) {
        this.book_from_long = book_from_long;
    }

    public String getBook_to_address() {
        return book_to_address;
    }

    public void setBook_to_address(String book_to_address) {
        this.book_to_address = book_to_address;
    }

    public String getBook_to_lat() {
        return book_to_lat;
    }

    public void setBook_to_lat(String book_to_lat) {
        this.book_to_lat = book_to_lat;
    }

    public String getBook_to_long() {
        return book_to_long;
    }

    public void setBook_to_long(String book_to_long) {
        this.book_to_long = book_to_long;
    }
}
