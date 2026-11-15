
package com.pakrose.hrpayroll;

public class AttendanceRecord {

    private int id;
    private int employeeId;
    private String date;
    private String checkIn;
    private String checkOut;
    private int lateMinutes;
    private int earlyMinutes;
    private int overtimeMinutes;
    private String status;
    private String notes;

    public AttendanceRecord(
            int id,
            int employeeId,
            String date,
            String checkIn,
            String checkOut,
            int lateMinutes,
            int earlyMinutes,
            int overtimeMinutes,
            String status,
            String notes) {

        this.id = id;
        this.employeeId = employeeId;
        this.date = date;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.lateMinutes = lateMinutes;
        this.earlyMinutes = earlyMinutes;
        this.overtimeMinutes = overtimeMinutes;
        this.status = status;
        this.notes = notes;
    }

    public int getId() { return id; }
    public int getEmployeeId() { return employeeId; }
    public String getDate() { return date; }
    public String getCheckIn() { return checkIn; }
    public String getCheckOut() { return checkOut; }
    public int getLateMinutes() { return lateMinutes; }
    public int getEarlyMinutes() { return earlyMinutes; }
    public int getOvertimeMinutes() { return overtimeMinutes; }
    public String getStatus() { return status; }
    public String getNotes() { return notes; }

    public void setCheckIn(String value) { checkIn = value; }
    public void setCheckOut(String value) { checkOut = value; }
    public void setStatus(String value) { status = value; }
    public void setNotes(String value) { notes = value; }
}
