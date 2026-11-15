
package com.pakrose.hrpayroll;

public class PayrollRecord {

    private int id;
    private int employeeId;
    private String month;
    private double basicSalary;
    private double overtimeAmount;
    private double advance;
    private double deductions;
    private double bonus;
    private double netSalary;
    private String paymentStatus;
    private String paymentDate;

    public PayrollRecord(
            int id,
            int employeeId,
            String month,
            double basicSalary,
            double overtimeAmount,
            double advance,
            double deductions,
            double bonus,
            double netSalary,
            String paymentStatus,
            String paymentDate) {

        this.id = id;
        this.employeeId = employeeId;
        this.month = month;
        this.basicSalary = basicSalary;
        this.overtimeAmount = overtimeAmount;
        this.advance = advance;
        this.deductions = deductions;
        this.bonus = bonus;
        this.netSalary = netSalary;
        this.paymentStatus = paymentStatus;
        this.paymentDate = paymentDate;
    }

    public int getId() { return id; }
    public int getEmployeeId() { return employeeId; }
    public String getMonth() { return month; }
    public double getBasicSalary() { return basicSalary; }
    public double getOvertimeAmount() { return overtimeAmount; }
    public double getAdvance() { return advance; }
    public double getDeductions() { return deductions; }
    public double getBonus() { return bonus; }
    public double getNetSalary() { return netSalary; }
    public String getPaymentStatus() { return paymentStatus; }
    public String getPaymentDate() { return paymentDate; }

    public void setPaymentStatus(String value) {
        paymentStatus = value;
    }

    public void setPaymentDate(String value) {
        paymentDate = value;
    }

    public void setNetSalary(double value) {
        netSalary = value;
    }
}
