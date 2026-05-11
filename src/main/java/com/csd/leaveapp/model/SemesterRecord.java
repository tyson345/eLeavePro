package com.csd.leaveapp.model;

public class SemesterRecord {
    private int semester;
    private double sgpa;
    private int totalMarks;
    private int acquiredMarks;

    public SemesterRecord(int semester, double sgpa, int totalMarks, int acquiredMarks) {
        this.semester = semester;
        this.sgpa = sgpa;
        this.totalMarks = totalMarks;
        this.acquiredMarks = acquiredMarks;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public double getSgpa() {
        return sgpa;
    }

    public void setSgpa(double sgpa) {
        this.sgpa = sgpa;
    }

    public int getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }

    public int getAcquiredMarks() {
        return acquiredMarks;
    }

    public void setAcquiredMarks(int acquiredMarks) {
        this.acquiredMarks = acquiredMarks;
    }
}
