package com.csd.leaveapp.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String usn;
    private String name;
    private String password;
    private Role role;
    private double cgpa;
    private int semester;
    private int leaveBalance;
    private boolean active;
    private List<SemesterRecord> semesterRecords = new ArrayList<>();

    public User() {
    }

    public User(String usn, String name, String password, Role role, double cgpa, int semester, int leaveBalance) {
        this.usn = usn;
        this.name = name;
        this.password = password;
        this.role = role;
        this.cgpa = cgpa;
        this.semester = semester;
        this.leaveBalance = leaveBalance;
        this.active = true;
    }

    public String getUsn() {
        return usn;
    }

    public void setUsn(String usn) {
        this.usn = usn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public double getCgpa() {
        return cgpa;
    }

    public void setCgpa(double cgpa) {
        this.cgpa = cgpa;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public int getLeaveBalance() {
        return leaveBalance;
    }

    public void setLeaveBalance(int leaveBalance) {
        this.leaveBalance = leaveBalance;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<SemesterRecord> getSemesterRecords() {
        return semesterRecords;
    }

    public void setSemesterRecords(List<SemesterRecord> semesterRecords) {
        this.semesterRecords = semesterRecords;
    }
}
