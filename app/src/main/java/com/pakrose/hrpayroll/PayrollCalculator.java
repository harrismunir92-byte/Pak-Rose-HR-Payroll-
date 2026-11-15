
package com.pakrose.hrpayroll;

public class PayrollCalculator {

    public static class SalaryResult {
        public double basicSalary;
        public double overtimePay;
        public double deductions;
        public double advance;
        public double unpaidLeaveDeduction;
        public double netSalary;
    }

    public static SalaryResult calculate(
            double basicSalary,
            int daysInMonth,
            int unpaidLeaveDays,
            double overtimeHours,
            double overtimeRatePerHour,
            double advance,
            double otherDeductions) {

        SalaryResult result = new SalaryResult();

        result.basicSalary = Math.max(0, basicSalary);

        if (daysInMonth <= 0) daysInMonth = 30;

        unpaidLeaveDays = Math.max(0,
                Math.min(unpaidLeaveDays, daysInMonth));

        result.unpaidLeaveDeduction =
                (result.basicSalary / daysInMonth)
                * unpaidLeaveDays;

        result.overtimePay =
                Math.max(0, overtimeHours)
                * Math.max(0, overtimeRatePerHour);

        result.advance = Math.max(0, advance);
        result.deductions = Math.max(0, otherDeductions);

        result.netSalary =
                result.basicSalary
                - result.unpaidLeaveDeduction
                + result.overtimePay
                - result.advance
                - result.deductions;

        result.netSalary = Math.max(0, result.netSalary);

        return result;
    }

    public static double hourlyRate(double monthlySalary,
                                    int workingHoursPerMonth) {

        if (workingHoursPerMonth <= 0) return 0;

        return monthlySalary / workingHoursPerMonth;
    }

    public static String formatPKR(double amount) {
        return "Rs. " + String.format(
                java.util.Locale.US, "%,.2f", amount);
    }
}
