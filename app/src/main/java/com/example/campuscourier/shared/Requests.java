package com.example.campuscourier.shared;

import java.io.Serializable;

public class Requests implements Serializable {
    private String item, description, imageUri, imageStorageUri, date, time, location, urgency, status, userId, docId, supplierId, category;

    private boolean SupplierDone, RequestorDone;
    public Requests() {
    }

    public Requests(String item, String description, String imageUri, String imageStorageUri, String date, String time, String location, String urgency, String status, String userId, String docId, String supplierId, String category) {
        this.item = item;
        this.description = description;
        this.imageUri = imageUri;
        this.imageStorageUri = imageStorageUri;
        this.date = date;
        this.time = time;
        this.location = location;
        this.urgency = urgency;
        this.status = status;
        this.userId = userId;
        this.docId = docId;
        this.supplierId = supplierId;
        this.SupplierDone = false;
        this.RequestorDone = false;
        this.category = category;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getImageStorageUri() {
        return imageStorageUri;
    }

    public void setImageStorageUri(String imageStorageUri) {
        this.imageStorageUri = imageStorageUri;
    }

    public String getDate() {
        return date;
    }

    public int getYear(){
        String substring = date.substring(0, 4);
        return Integer.parseInt(substring);
    }
    public int getMonth(){
        if(date.charAt(4) == '0'){
            return Integer.parseInt(date.substring(5, 6))-1;
        }
        return Integer.parseInt(date.substring(4, 6))-1;
    }
    public int getDay(){
        if(date.charAt(6) == '0'){
            return Integer.parseInt(date.substring(7, 8));
        }
        return Integer.parseInt(date.substring(6, 8));
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public int getHour(){
        if(time.charAt(0) == '0'){
            return Integer.parseInt(time.substring(1, 2));
        }
        return Integer.parseInt(time.substring(0, 2));
    }
    public int getMinute(){
        if(time.charAt(2) == '0'){
            return Integer.parseInt(time.substring(3, 4));
        }
        return Integer.parseInt(time.substring(2, 4));
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public void setSupplierDone() { this.SupplierDone = true; }
    public void setRequestorDone() { this.RequestorDone = true; }

    public boolean isRequestorDone() {
        return RequestorDone;
    }
    public boolean isSupplierDone() {
        return SupplierDone;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
