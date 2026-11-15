
package com.pakrose.hrpayroll;

import android.app.*;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {

    DBHelper db;
    LinearLayout main;

    int navy = Color.rgb(17, 31, 58);
    int teal = Color.rgb(0, 150, 136);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DBHelper();
        showDashboard();
    }

    String today() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US)
                .format(new Date());
    }

    TextView makeText(String value, int size, int color) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setPadding(0, 10, 0, 10);
        return t;
    }

    void setupScreen(String title) {
        ScrollView scroll = new ScrollView(this);
        main = new LinearLayout(this);
        main.setOrientation(LinearLayout.VERTICAL);
        main.setPadding(20, 20, 20, 20);
        main.setBackgroundColor(Color.rgb(245, 247, 251));

        TextView heading = makeText(title, 23, Color.WHITE);
        heading.setTypeface(null, Typeface.BOLD);

        LinearLayout header = new LinearLayout(this);
        header.setPadding(18, 20, 18, 20);
        header.setBackgroundColor(navy);
        header.addView(heading);

        main.addView(header);
        scroll.addView(main);
        setContentView(scroll);
    }

    void addCard(String title, String value) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(18, 12, 18, 12);
        card.setBackgroundColor(Color.WHITE);

        card.addView(makeText(title, 14, Color.GRAY));

        TextView number = makeText(value, 25, navy);
        number.setTypeface(null, Typeface.BOLD);
        card.addView(number);

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, 8, 0, 8);
        main.addView(card, p);
    }

    void addButton(String label, View.OnClickListener action) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextColor(Color.WHITE);
        b.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(navy));
        b.setOnClickListener(action);

        LinearLayout.LayoutParams p =
                new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(0, 6, 0, 6);
        main.addView(b, p);
    }

    void showDashboard() {
        setupScreen("PAK ROSE ENTERPRISES");

        main.addView(makeText("HR & PAYROLL MANAGEMENT", 13, teal));
        main.addView(makeText("Dashboard", 23, navy));

        addCard("Total Employees",
                String.valueOf(db.employeeCount()));

        addCard("Today's Present",
                String.valueOf(db.attendanceCount(today(), "P")));

        addCard("Today's Absent",
                String.valueOf(db.attendanceCount(today(), "A")));

        addButton("EMPLOYEE MANAGEMENT",
                v -> employeesScreen());

        addButton("DAILY ATTENDANCE",
                v -> attendanceScreen());

        addButton("SALARY OVERVIEW",
                v -> salaryScreen());

        main.addView(makeText(
                "Pak Rose Enterprises © 2026", 12, Color.GRAY));
    }

    void addBackButton() {
        addButton("BACK TO DASHBOARD", v -> showDashboard());
    }

    void employeesScreen() {
        setupScreen("Employee Management");
        addBackButton();

        addButton("+ ADD NEW EMPLOYEE", v -> addEmployee());

        Cursor c = db.getEmployees();

        while (c.moveToNext()) {
            String name = c.getString(1);
            String salary = c.getString(2);

            addCard(name, "Monthly Salary: Rs. " + salary);
        }

        c.close();
    }

    void addEmployee() {
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(15, 10, 15, 10);

        EditText name = new EditText(this);
        name.setHint("Employee Name");
        form.addView(name);

        EditText salary = new EditText(this);
        salary.setHint("Monthly Salary (Rs.)");
        salary.setInputType(2);
        form.addView(salary);

        new AlertDialog.Builder(this)
                .setTitle("New Employee")
                .setView(form)
                .setPositiveButton("SAVE", (dialog, which) -> {
                    String n = name.getText().toString().trim();
                    String s = salary.getText().toString().trim();

                    if (n.isEmpty() || s.isEmpty()) {
                        Toast.makeText(this,
                                "Please enter all details",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    db.addEmployee(n, s);
                    employeesScreen();
                })
                .setNegativeButton("CANCEL", null)
                .show();
    }

    void attendanceScreen() {
        setupScreen("Daily Attendance");
        addBackButton();

        main.addView(makeText("Date: " + today(), 16, teal));

        Cursor c = db.getEmployees();

        while (c.moveToNext()) {
            final int id = c.getInt(0);
            String name = c.getString(1);

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.VERTICAL);
            row.setPadding(12, 10, 12, 10);
            row.setBackgroundColor(Color.WHITE);

            row.addView(makeText(name, 17, navy));

            Spinner status = new Spinner(this);
            String[] choices = {
                    "Present (P)",
                    "Absent (A)",
                    "Paid Leave (PL)",
                    "Unpaid Leave (UL)"
            };

            ArrayAdapter<String> adapter =
                    new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_item,
                            choices);

            adapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item);

            status.setAdapter(adapter);
            row.addView(status);

            Button save = new Button(this);
            save.setText("SAVE ATTENDANCE");

            save.setOnClickListener(v -> {
                String selected =
                        status.getSelectedItem().toString();

                String code = "P";

                if (selected.startsWith("Absent")) code = "A";
                if (selected.startsWith("Paid")) code = "PL";
                if (selected.startsWith("Unpaid")) code = "UL";

                db.saveAttendance(id, today(), code);

                Toast.makeText(this,
                        name + " attendance saved",
                        Toast.LENGTH_SHORT).show();
            });

            row.addView(save);

            LinearLayout.LayoutParams p =
                    new LinearLayout.LayoutParams(-1, -2);
            p.setMargins(0, 8, 0, 8);

            main.addView(row, p);
        }

        c.close();
    }

    void salaryScreen() {
        setupScreen("Salary Overview");
        addBackButton();

        Cursor c = db.getEmployees();
        long total = 0;

        while (c.moveToNext()) {
            String name = c.getString(1);
            String salary = c.getString(2);

            try {
                total += Long.parseLong(salary);
            } catch (Exception ignored) {}

            addCard(name, "Monthly Salary: Rs. " + salary);
        }

        c.close();

        addCard("Total Monthly Payroll",
                "Rs. " + total);
    }

    class DBHelper extends SQLiteOpenHelper {

        DBHelper() {
            super(MainActivity.this,
                    "PakRoseHR.db", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            database.execSQL(
                    "CREATE TABLE employees (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT," +
                    "salary TEXT)");

            database.execSQL(
                    "CREATE TABLE attendance (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "employee_id INTEGER," +
                    "date TEXT," +
                    "status TEXT," +
                    "UNIQUE(employee_id,date))");
        }

        @Override
        public void onUpgrade(SQLiteDatabase database,
                              int oldVersion, int newVersion) {}

        void addEmployee(String name, String salary) {
            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("salary", salary);

            getWritableDatabase().insert(
                    "employees", null, values);
        }

        Cursor getEmployees() {
            return getReadableDatabase().rawQuery(
                    "SELECT * FROM employees ORDER BY name",
                    null);
        }

        int employeeCount() {
            Cursor c = getReadableDatabase().rawQuery(
                    "SELECT COUNT(*) FROM employees", null);
            c.moveToFirst();
            int count = c.getInt(0);
            c.close();
            return count;
        }

        void saveAttendance(int employeeId,
                            String date, String status) {
            ContentValues values = new ContentValues();
            values.put("employee_id", employeeId);
            values.put("date", date);
            values.put("status", status);

            getWritableDatabase().insertWithOnConflict(
                    "attendance", null, values,
                    SQLiteDatabase.CONFLICT_REPLACE);
        }

        int attendanceCount(String date, String status) {
            Cursor c = getReadableDatabase().rawQuery(
                    "SELECT COUNT(*) FROM attendance " +
                    "WHERE date=? AND status=?",
                    new String[]{date, status});

            c.moveToFirst();
            int count = c.getInt(0);
            c.close();
            return count;
        }
    }
}
