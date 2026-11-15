package com.pakrose.hrpayroll;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Calendar;

public class MainActivity extends Activity {
    private final int NAVY = Color.rgb(18, 39, 68);
    private final int NAVY_DARK = Color.rgb(12, 27, 49);
    private final int TEAL = Color.rgb(0, 150, 136);
    private final int BG = Color.rgb(245, 247, 251);
    private final int TEXT = Color.rgb(38, 50, 68);
    private final int MUTED = Color.rgb(115, 128, 145);
    private DB db;
    private LinearLayout root, content;
    private String currentPage = "Home";

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        db = new DB();
        showPage("Home");
    }

    private int dp(float n) { return (int)(n * getResources().getDisplayMetrics().density + 0.5f); }
    private String today() { return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date()); }
    private String month() { return new SimpleDateFormat("yyyy-MM", Locale.US).format(new Date()); }

    private GradientDrawable shape(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radius));
        return d;
    }

    private TextView label(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value); t.setTextSize(size); t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }

    private LinearLayout vertical() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        return l;
    }

    private LinearLayout horizontal() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.HORIZONTAL);
        l.setGravity(Gravity.CENTER_VERTICAL);
        return l;
    }

    private void pad(View v, int left, int top, int right, int bottom) {
        v.setPadding(dp(left), dp(top), dp(right), dp(bottom));
    }

    private void showPage(String page) {
        currentPage = page;
        root = vertical();
        root.setBackgroundColor(BG);

        LinearLayout header = vertical();
        header.setBackground(shape(NAVY, 0));
        pad(header, 20, 18, 20, 18);
        TextView brand = label("PAK ROSE ENTERPRISES", 17, Color.WHITE, true);
        TextView sub = label("HUMAN RESOURCES  •  PAYROLL", 10, Color.rgb(194, 210, 225), true);
        header.addView(brand);
        header.addView(sub);
        root.addView(header);

        ScrollView scroll = new ScrollView(this);
        content = vertical();
        pad(content, 16, 16, 16, 18);
        scroll.addView(content, new ScrollView.LayoutParams(-1, -2));
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));

        LinearLayout nav = horizontal();
        nav.setBackgroundColor(Color.WHITE);
        nav.setElevation(dp(8));
        String[] tabs = {"Home", "Employees", "Attendance", "Payroll", "Reports"};
        for (String tab : tabs) {
            TextView item = label(tab, 10, currentPage.equals(tab) ? TEAL : MUTED, currentPage.equals(tab));
            item.setGravity(Gravity.CENTER);
            pad(item, 2, 14, 2, 14);
            nav.addView(item, new LinearLayout.LayoutParams(0, -2, 1));
            item.setOnClickListener(v -> showPage(tab));
        }
        root.addView(nav);
        setContentView(root);

        if (page.equals("Home")) dashboard();
        else if (page.equals("Employees")) employees();
        else if (page.equals("Attendance")) attendance();
        else if (page.equals("Payroll")) payroll();
        else reports();
    }

    private void heading(String title, String subtitle) {
        content.addView(label(title, 23, TEXT, true));
        if (subtitle != null) {
            TextView s = label(subtitle, 12, MUTED, false);
            pad(s, 0, 3, 0, 14);
            content.addView(s);
        }
    }

    private void card(LinearLayout parent, View child) {
        LinearLayout box = vertical();
        box.setBackground(shape(Color.WHITE, 16));
        box.setElevation(dp(2));
        pad(box, 16, 14, 16, 14);
        box.addView(child);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.bottomMargin = dp(10);
        parent.addView(box, lp);
    }

    private void metricCard(String title, String value, String hint) {
        LinearLayout box = vertical();
        box.setBackground(shape(Color.WHITE, 16));
        box.setElevation(dp(2));
        pad(box, 14, 14, 14, 14);
        box.addView(label(title, 11, MUTED, true));
        TextView v = label(value, 25, NAVY, true);
        pad(v, 0, 7, 0, 2);
        box.addView(v);
        box.addView(label(hint, 10, TEAL, false));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, -2, 1);
        lp.setMargins(dp(4), dp(4), dp(4), dp(4));
        metricsRow.addView(box, lp);
    }
    private LinearLayout metricsRow;

    private void dashboard() {
        heading("Dashboard", "Your company overview at a glance");
        metricsRow = horizontal();
        metricCard("EMPLOYEES", String.valueOf(db.count("employees")), "Registered staff");
        metricCard("PRESENT TODAY", String.valueOf(db.attendanceCount(today(), "P")), "Today's attendance");
        content.addView(metricsRow);
        metricsRow = horizontal();
        metricCard("ABSENT TODAY", String.valueOf(db.attendanceCount(today(), "A")), "Today's absence");
        metricCard("MONTHLY PAYROLL", money(db.totalPayroll()), "Basic monthly salaries");
        content.addView(metricsRow);

        TextView quickTitle = label("Quick actions", 17, TEXT, true);
        pad(quickTitle, 0, 12, 0, 10);
        content.addView(quickTitle);
        actionButton("＋  Add new employee", TEAL, v -> addEmployee());
        actionButton("✓  Mark today's attendance", NAVY, v -> showPage("Attendance"));
        actionButton("▤  View payroll", NAVY, v -> showPage("Payroll"));

        LinearLayout info = vertical();
        info.addView(label("Welcome to Pak Rose HR & Payroll", 14, NAVY, true));
        info.addView(label("Manage employee records, daily attendance and monthly salary information in one place.", 12, MUTED, false));
        card(content, info);
    }

    private void actionButton(String title, int color, View.OnClickListener click) {
        Button b = new Button(this);
        b.setText(title);
        b.setTextColor(Color.WHITE);
        b.setTextSize(13);
        b.setAllCaps(false);
        b.setBackground(shape(color, 12));
        b.setOnClickListener(click);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, dp(48));
        lp.bottomMargin = dp(9);
        content.addView(b, lp);
    }

    private String money(long amount) { return "Rs. " + String.format(Locale.US, "%,d", amount); }

    private void employees() {
        heading("Employees", "Manage your company workforce");
        actionButton("＋  Add employee", TEAL, v -> addEmployee());
        Cursor c = db.employees();
        if (c.getCount() == 0) {
            emptyState("No employees added yet", "Tap Add employee to create your first employee record.");
        }
        while (c.moveToNext()) {
            final int id = c.getInt(0);
            LinearLayout details = vertical();
            details.addView(label(c.getString(1), 16, NAVY, true));
            details.addView(label(c.getString(2) + "  •  " + c.getString(3), 11, MUTED, false));
            details.addView(label(money(c.getLong(4)) + " / month", 13, TEAL, true));
            card(content, details);
        }
        c.close();
    }

    private void addEmployee() {
        LinearLayout form = vertical();
        pad(form, 4, 4, 4, 0);
        EditText name = field("Full name");
        EditText role = field("Designation");
        EditText phone = field("Phone number");
        EditText salary = field("Monthly basic salary (Rs.)");
        salary.setInputType(2);
        form.addView(name); form.addView(role); form.addView(phone); form.addView(salary);
        new AlertDialog.Builder(this).setTitle("Add employee").setView(form)
            .setPositiveButton("Save", (d, w) -> {
                String n = name.getText().toString().trim();
                String r = role.getText().toString().trim();
                String p = phone.getText().toString().trim();
                String s = salary.getText().toString().trim();
                if (n.isEmpty() || s.isEmpty()) {
                    toast("Name and salary are required");
                    return;
                }
                long amount;
                try { amount = Long.parseLong(s); } catch (Exception e) { toast("Enter a valid salary"); return; }
                db.addEmployee(n, r.isEmpty() ? "Employee" : r, p, amount);
                toast("Employee saved");
                showPage("Employees");
            }).setNegativeButton("Cancel", null).show();
    }

    private EditText field(String hint) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setTextSize(14);
        e.setSingleLine(true);
        return e;
    }

    private void attendance() {
        heading("Attendance", "Daily attendance • " + today());
        Cursor c = db.employees();
        if (c.getCount() == 0) emptyState("No employees found", "Add employees before marking attendance.");
        while (c.moveToNext()) {
            final int id = c.getInt(0);
            String name = c.getString(1);
            LinearLayout row = vertical();
            row.addView(label(name, 15, NAVY, true));
            Spinner status = new Spinner(this);
            String[] choices = {"Present (P)", "Absent (A)", "Paid Leave (PL)", "Unpaid Leave (UL)"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, choices);
            status.setAdapter(adapter);
            String saved = db.status(id, today());
            if ("A".equals(saved)) status.setSelection(1);
            else if ("PL".equals(saved)) status.setSelection(2);
            else if ("UL".equals(saved)) status.setSelection(3);
            row.addView(status);
            Button save = new Button(this);
            save.setText("Save attendance");
            save.setAllCaps(false);
            save.setOnClickListener(v -> {
                String selected = status.getSelectedItem().toString();
                String code = selected.startsWith("Absent") ? "A" :
                    selected.startsWith("Paid") ? "PL" :
                    selected.startsWith("Unpaid") ? "UL" : "P";
                db.saveAttendance(id, today(), code);
                toast("Attendance saved for " + name);
            });
            row.addView(save);
            card(content, row);
        }
        c.close();
    }

    private void payroll() {
        heading("Payroll", "Monthly salary overview • " + month());
        long total = db.totalPayroll();
        LinearLayout summary = vertical();
        summary.addView(label("TOTAL BASIC PAYROLL", 11, MUTED, true));
        summary.addView(label(money(total), 27, NAVY, true));
        summary.addView(label("Based on employee monthly basic salaries", 11, TEAL, false));
        card(content, summary);
        Cursor c = db.employees();
        if (c.getCount() == 0) emptyState("No payroll data", "Add employees with their monthly salary to view payroll.");
        while (c.moveToNext()) {
            LinearLayout row = horizontal();
            LinearLayout left = vertical();
            left.addView(label(c.getString(1), 14, NAVY, true));
            left.addView(label(c.getString(2), 11, MUTED, false));
            row.addView(left, new LinearLayout.LayoutParams(0, -2, 1));
            row.addView(label(money(c.getLong(4)), 14, TEAL, true));
            card(content, row);
        }
        c.close();
        TextView note = label("Note: This version shows basic salary totals. Overtime, advances, deductions and finalized payslips will be added in the next payroll module.", 11, MUTED, false);
        pad(note, 4, 8, 4, 8);
        content.addView(note);
    }

    private void reports() {
        heading("Reports", "Attendance snapshot and company totals");
        int total = db.count("employees");
        int p = db.attendanceCount(today(), "P");
        int a = db.attendanceCount(today(), "A");
        int pl = db.attendanceCount(today(), "PL");
        int ul = db.attendanceCount(today(), "UL");
        reportLine("Total employees", String.valueOf(total));
        reportLine("Present today", String.valueOf(p));
        reportLine("Absent today", String.valueOf(a));
        reportLine("Paid leave today", String.valueOf(pl));
        reportLine("Unpaid leave today", String.valueOf(ul));
        reportLine("Monthly basic payroll", money(db.totalPayroll()));
        TextView note = label("Reports are currently an on-device summary. Excel export and PDF salary slips are planned for the next module.", 11, MUTED, false);
        pad(note, 4, 12, 4, 8);
        content.addView(note);
    }

    private void reportLine(String key, String value) {
        LinearLayout line = horizontal();
        line.addView(label(key, 13, TEXT, false), new LinearLayout.LayoutParams(0, -2, 1));
        line.addView(label(value, 14, NAVY, true));
        card(content, line);
    }

    private void emptyState(String title, String detail) {
        LinearLayout box = vertical();
        box.setGravity(Gravity.CENTER);
        pad(box, 18, 24, 18, 24);
        box.addView(label(title, 16, NAVY, true));
        TextView d = label(detail, 12, MUTED, false);
        d.setGravity(Gravity.CENTER);
        box.addView(d);
        card(content, box);
    }

    private void toast(String message) { Toast.makeText(this, message, Toast.LENGTH_SHORT).show(); }

    private class DB extends SQLiteOpenHelper {
        DB() { super(MainActivity.this, "PakRoseHR.db", null, 2); }
        @Override public void onCreate(SQLiteDatabase d) {
            d.execSQL("CREATE TABLE employees (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, role TEXT, phone TEXT, salary INTEGER NOT NULL)");
            d.execSQL("CREATE TABLE attendance (id INTEGER PRIMARY KEY AUTOINCREMENT, employee_id INTEGER, date TEXT, status TEXT, UNIQUE(employee_id,date))");
        }
        @Override public void onUpgrade(SQLiteDatabase d, int oldV, int newV) {
            if (oldV < 2) {
                try { d.execSQL("ALTER TABLE employees ADD COLUMN role TEXT"); } catch (Exception ignored) {}
                try { d.execSQL("ALTER TABLE employees ADD COLUMN phone TEXT"); } catch (Exception ignored) {}
            }
        }
        int count(String table) {
            Cursor c = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM " + table, null);
            c.moveToFirst(); int n = c.getInt(0); c.close(); return n;
        }
        int attendanceCount(String date, String status) {
            Cursor c = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM attendance WHERE date=? AND status=?", new String[]{date, status});
            c.moveToFirst(); int n = c.getInt(0); c.close(); return n;
        }
        Cursor employees() { return getReadableDatabase().rawQuery("SELECT id,name,COALESCE(role,'Employee'),COALESCE(phone,''),salary FROM employees ORDER BY name", null); }
        void addEmployee(String name, String role, String phone, long salary) {
            ContentValues v = new ContentValues(); v.put("name", name); v.put("role", role); v.put("phone", phone); v.put("salary", salary);
            getWritableDatabase().insert("employees", null, v);
        }
        long totalPayroll() {
            Cursor c = getReadableDatabase().rawQuery("SELECT COALESCE(SUM(salary),0) FROM employees", null);
            c.moveToFirst(); long n = c.getLong(0); c.close(); return n;
        }
        void saveAttendance(int employee, String date, String status) {
            ContentValues v = new ContentValues(); v.put("employee_id", employee); v.put("date", date); v.put("status", status);
            getWritableDatabase().insertWithOnConflict("attendance", null, v, SQLiteDatabase.CONFLICT_REPLACE);
        }
        String status(int employee, String date) {
            Cursor c = getReadableDatabase().rawQuery("SELECT status FROM attendance WHERE employee_id=? AND date=?", new String[]{String.valueOf(employee), date});
            String s = c.moveToFirst() ? c.getString(0) : "P"; c.close(); return s;
        }
    }
                }
