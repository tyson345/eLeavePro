package com.csd.leaveapp.model;

import java.time.LocalDate;

public class LeaveRequest {
    private long id;
    private String studentUsn;
    private String studentName;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String reason;
    private int numberOfDays;
    private LeaveStatus status;

    public LeaveRequest() {
    }

    public LeaveRequest(long id, String studentUsn, String studentName, LocalDate fromDate, LocalDate toDate,
                        String reason, int numberOfDays, LeaveStatus status) {
        this.id = id;
        this.studentUsn = studentUsn;
        this.studentName = studentName;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.reason = reason;
        this.numberOfDays = numberOfDays;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStudentUsn() {
        return studentUsn;
    }

    public void setStudentUsn(String studentUsn) {
        this.studentUsn = studentUsn;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getNumberOfDays() {
        return numberOfDays;
    }

    public void setNumberOfDays(int numberOfDays) {
        this.numberOfDays = numberOfDays;
    }

    public LeaveStatus getStatus() {
        return status;
    }

    public void setStatus(LeaveStatus status) {
        this.status = status;
    }
}
