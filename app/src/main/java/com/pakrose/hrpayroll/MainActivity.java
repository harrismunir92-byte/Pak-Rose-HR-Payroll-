package com.pakrose.hrpayroll;

import android.app.*;
import android.os.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.*;
import android.widget.*;
import android.text.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {

    private final int NAVY = Color.rgb(16, 35, 65);
    private final int BLUE = Color.rgb(35, 85, 145);
    private final int BG = Color.rgb(245, 247, 251);
    private LinearLayout root, content, nav;
    private TextView pageTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showDashboard();
    }

    private TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        if (bold) t.setTypeface(null, Typeface.BOLD);
        return t;
    }

    private void setup(String title) {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setPadding(22, 22, 22, 18);
        header.setBackgroundColor(NAVY);

        TextView company = text("PAK ROSE ENTERPRISES", 13,
                Color.rgb(190, 210, 235), true);
        header.addView(company);

        pageTitle = text(title, 23, Color.WHITE, true);
        header.addView(pageTitle);

        root.addView(header);

        ScrollView scroll = new ScrollView(this);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(16, 16, 16, 20);
        scroll.addView(content);

        root.addView(scroll, new LinearLayout.LayoutParams(
                -1, 0, 1));

        nav = new LinearLayout(this);
        nav.setBackgroundColor(Color.WHITE);
        nav.setElevation(10);
        nav.setGravity(Gravity.CENTER);
        root.addView(nav);

        setContentView(root);
        addNavButton("Home", this::showDashboard);
        addNavButton("Employees", this::showEmployees);
        addNavButton("Attendance", this::showAttendance);
        addNavButton("Payroll", this::showPayroll);
        addNavButton("Settings", this::showSettings);
    }

    private void addNavButton(String label, Runnable action) {
        TextView b = text(label, 11, NAVY, true);
        b.setGravity(Gravity.CENTER);
        b.setPadding(5, 16, 5, 16);
        b.setLayoutParams(new LinearLayout.LayoutParams(0, -2, 1));
        b.setOnClickListener(v -> action.run());
        nav.addView(b);
    }

    private void heading(String value) {
        TextView t = text(value, 17, NAVY, true);
        t.setPadding(0, 18, 0, 10);
        content.addView(t);
    }

    private void card(String title, String value, String subtitle) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(18, 16, 18, 16);
        box.setBackgroundColor(Color.WHITE);
        box.setElevation(3);

        TextView a = text(title, 13, Color.GRAY, true);
        TextView b = text(value, 25, NAVY, true);
        TextView c = text(subtitle, 12, Color.GRAY, false);

        box.addView(a);
        box.addView(b);
        box.addView(c);

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, 0, 0, 12);
        content.addView(box, p);
    }

    private void button(String label, Runnable action) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextColor(Color.WHITE);
        b.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(BLUE));
        b.setOnClickListener(v -> action.run());

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, 7, 0, 7);
        content.addView(b, p);
    }

    private void showDashboard() {
        setup("Dashboard");

        String today = new SimpleDateFormat(
                "EEEE, dd MMMM yyyy", Locale.getDefault())
                .format(new Date());

        TextView date = text(today, 13, Color.GRAY, false);
        content.addView(date);

        heading("Company Overview");
        card("TOTAL EMPLOYEES", "0", "Registered employees");
        card("PRESENT TODAY", "0", "Today's attendance");
        card("MONTHLY PAYROLL", "Rs. 0", "Estimated salary");

        heading("Quick Actions");
        button("Add New Employee", this::showEmployees);
        button("Mark Attendance", this::showAttendance);
        button("View Payroll", this::showPayroll);
        button("Office Settings", this::showSettings);
    }

    private void showEmployees() {
        setup("Employees");
        heading("Employee Management");

        content.addView(text(
                "Employee records will be managed here.",
                14, Color.GRAY, false));

        button("Add New Employee", () -> addEmployeeForm());
    }

    private void addEmployeeForm() {
        setup("New Employee");

        EditText name = field("Employee full name");
        EditText designation = field("Designation");
        EditText phone = field("Phone number");
        EditText salary = field("Monthly basic salary");

        content.addView(name);
        content.addView(designation);
        content.addView(phone);
        content.addView(salary);

        button("Save Employee", () -> {
            if (name.getText().toString().trim().isEmpty()) {
                name.setError("Name is required");
                return;
            }
            Toast.makeText(this,
                    "Employee form ready. Database setup is next.",
                    Toast.LENGTH_LONG).show();
        });
    }

    private EditText field(String hint) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setSingleLine(true);
        e.setTextSize(15);
        e.setPadding(14, 12, 14, 12);
        return e;
    }

    private void showAttendance() {
        setup("Attendance");
        heading("Daily Attendance");

        TextView date = text(
                new SimpleDateFormat("dd MMMM yyyy",
                        Locale.getDefault()).format(new Date()),
                15, NAVY, true);
        content.addView(date);

        button("Check In", () -> showTime("Check In"));
        button("Check Out", () -> showTime("Check Out"));

        heading("Attendance Status");
        button("Present", () -> showStatus("Present"));
        button("Absent", () -> showStatus("Absent"));
        button("Paid Leave", () -> showStatus("Paid Leave"));
        button("Unpaid Leave", () -> showStatus("Unpaid Leave"));
    }

    private void showTime(String action) {
        String time = new SimpleDateFormat(
                "hh:mm a", Locale.getDefault()).format(new Date());
        new AlertDialog.Builder(this)
                .setTitle(action)
                .setMessage(action + " time: " + time)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showStatus(String status) {
        Toast.makeText(this,
                "Selected: " + status,
                Toast.LENGTH_SHORT).show();
    }

    private void showPayroll() {
        setup("Payroll");
        heading("Salary Management");
        card("MONTHLY SALARY", "Rs. 0", "Total basic salary");
        card("OVERTIME", "Rs. 0", "Overtime amount");
        card("DEDUCTIONS", "Rs. 0", "Advances and deductions");
        card("NET PAYABLE", "Rs. 0", "Final salary");
        button("Generate Salary Slips", () ->
                Toast.makeText(this,
                        "Payroll module will be connected next.",
                        Toast.LENGTH_SHORT).show());
    }

    private void showSettings() {
        setup("Office Settings");
        heading("Adjustable Office Timings");

        EditText start = field("Office start time e.g. 09:00 AM");
        EditText end = field("Office closing time e.g. 05:00 PM");
        EditText grace = field("Late grace minutes e.g. 10");
        EditText overtime = field("Overtime after minutes e.g. 480");
        EditText breakTime = field("Break duration in minutes");

        content.addView(start);
        content.addView(end);
        content.addView(grace);
        content.addView(overtime);
        content.addView(breakTime);

        button("Save Office Settings", () ->
                Toast.makeText(this,
                        "Settings screen ready. Saving system comes next.",
                        Toast.LENGTH_LONG).show());

        heading("Other Settings");
        button("PIN Security", () ->
                Toast.makeText(this, "PIN security module next.",
                        Toast.LENGTH_SHORT).show());
        button("Backup & Restore", () ->
                Toast.makeText(this, "Backup module next.",
                        Toast.LENGTH_SHORT).show());
    }
}
