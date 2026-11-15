
package com.pakrose.hrpayroll;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SalarySlip {

    public static File generate(
            Context context,
            String employeeName,
            String designation,
            String employeeId,
            String month,
            double basicSalary,
            double overtime,
            double unpaidDeduction,
            double advance,
            double otherDeduction) {

        PdfDocument pdf = new PdfDocument();

        PdfDocument.PageInfo info =
                new PdfDocument.PageInfo.Builder(
                        595, 842, 1).create();

        PdfDocument.Page page = pdf.startPage(info);

        android.graphics.Canvas canvas = page.getCanvas();

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setColor(android.graphics.Color.rgb(16, 35, 65));
        paint.setTextSize(22);
        paint.setTypeface(Typeface.create(
                Typeface.DEFAULT, Typeface.BOLD));

        canvas.drawText("PAK ROSE ENTERPRISES", 40, 55, paint);

        paint.setTextSize(12);
        paint.setColor(android.graphics.Color.GRAY);
        paint.setTypeface(Typeface.DEFAULT);

        canvas.drawText("HR & PAYROLL MANAGEMENT", 40, 78, paint);

        paint.setColor(android.graphics.Color.rgb(16, 35, 65));
        canvas.drawRect(40, 100, 555, 103, paint);

        paint.setTextSize(19);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        canvas.drawText("SALARY SLIP", 40, 140, paint);

        paint.setTextSize(12);
        paint.setTypeface(Typeface.DEFAULT);

        int y = 180;

        y = line(canvas, paint, "Employee Name", employeeName, y);
        y = line(canvas, paint, "Designation", designation, y);
        y = line(canvas, paint, "Employee ID", employeeId, y);
        y = line(canvas, paint, "Salary Month", month, y);

        y += 20;

        paint.setTypeface(Typeface.DEFAULT_BOLD);
        canvas.drawText("EARNINGS", 40, y, paint);
        y += 25;

        paint.setTypeface(Typeface.DEFAULT);

        y = line(canvas, paint, "Basic Salary",
                money(basicSalary), y);

        y = line(canvas, paint, "Overtime",
                money(overtime), y);

        y += 20;

        paint.setTypeface(Typeface.DEFAULT_BOLD);
        canvas.drawText("DEDUCTIONS", 40, y, paint);
        y += 25;

        paint.setTypeface(Typeface.DEFAULT);

        y = line(canvas, paint, "Unpaid Leave",
                money(unpaidDeduction), y);

        y = line(canvas, paint, "Advance",
                money(advance), y);

        y = line(canvas, paint, "Other Deductions",
                money(otherDeduction), y);

        double net = Math.max(0,
                basicSalary + overtime
                - unpaidDeduction - advance - otherDeduction);

        y += 30;

        paint.setColor(android.graphics.Color.rgb(16, 35, 65));
        paint.setTextSize(17);
        paint.setTypeface(Typeface.DEFAULT_BOLD);

        canvas.drawText("NET SALARY: " + money(net),
                40, y, paint);

        y += 80;

        paint.setColor(android.graphics.Color.GRAY);
        paint.setTextSize(10);
        paint.setTypeface(Typeface.DEFAULT);

        String date = new SimpleDateFormat(
                "dd MMM yyyy", Locale.US).format(new Date());

        canvas.drawText("Generated on: " + date, 40, y, paint);
        canvas.drawText("Authorized Signature: __________________",
                330, y, paint);

        pdf.finishPage(page);

        File directory = new File(
                context.getExternalFilesDir(
                        Environment.DIRECTORY_DOCUMENTS),
                "PakRoseSalarySlips");

        if (!directory.exists()) directory.mkdirs();

        String safeName = employeeName.replaceAll(
                "[^a-zA-Z0-9_-]", "_");

        File file = new File(directory,
                "SalarySlip_" + safeName + "_" +
                System.currentTimeMillis() + ".pdf");

        try {
            FileOutputStream output =
                    new FileOutputStream(file);

            pdf.writeTo(output);
            output.close();
            pdf.close();

            return file;

        } catch (Exception e) {
            pdf.close();
            return null;
        }
    }

    private static int line(
            android.graphics.Canvas canvas,
            Paint paint,
            String label,
            String value,
            int y) {

        paint.setTextSize(12);
        paint.setColor(android.graphics.Color.DKGRAY);

        canvas.drawText(label, 45, y, paint);
        canvas.drawText(value == null ? "" : value,
                300, y, paint);

        return y + 28;
    }

    private static String money(double amount) {
        return "Rs. " + String.format(
                Locale.US, "%,.2f", amount);
    }
}
