package com.example.campuscourier.shared;

import java.io.Serializable;
import java.util.List;

public class Requests_2 implements Serializable {
    private String userId,ReportId,Description,OtherReason,status,docId;
    private List<String> SelectedDescription;


    public Requests_2() {
    }

    public Requests_2(String userId,String ReportId, String Description,String OtherReason, List<String> SelectedDescription,String docId) {
        this.ReportId = ReportId;
        this.Description = Description;
        this.SelectedDescription = SelectedDescription;
        this.OtherReason = OtherReason;
        this.userId = userId;
        this.status = "Pending";
        this.docId=docId;
    }

    public String getReportId() {
        return ReportId;
    }

    public void setReportId(String ReportId) {
        this.ReportId = ReportId;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        this.Description = description;
    }

    public List<String> getSelectedDescription(){return SelectedDescription;}

    public void setSelectedDescription(List<String> SelectedDescription){this.SelectedDescription = SelectedDescription;}

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOtherReason(){return OtherReason;}

    public void setOtherReason(String OtherReason){this.OtherReason = OtherReason;}

    public String getStatus(){return status;}

    public void setStatus(String status){this.status = status;}
    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }


}