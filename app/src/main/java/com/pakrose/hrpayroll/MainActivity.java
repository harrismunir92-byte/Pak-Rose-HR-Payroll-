package com.pakrose.hrpayroll;

import android.app.*;
import android.os.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.*;
import android.widget.*;
import android.database.Cursor;
import android.text.InputType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {

    private final int NAVY = Color.rgb(16, 35, 65);
    private final int BLUE = Color.rgb(35, 85, 145);
    private final int BG = Color.rgb(245, 247, 251);

    private LinearLayout root, content, nav;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new AppDatabase(this);
        showDashboard();
    }

    private TextView text(String value, int size,
                          int color, boolean bold) {
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

        header.addView(text("PAK ROSE ENTERPRISES",
                13, Color.LTGRAY, true));
        header.addView(text(title, 23, Color.WHITE, true));

        root.addView(header);

        ScrollView scroll = new ScrollView(this);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(16, 16, 16, 20);
        scroll.addView(content);

        root.addView(scroll,
                new LinearLayout.LayoutParams(-1, 0, 1));

        nav = new LinearLayout(this);
        nav.setBackgroundColor(Color.WHITE);
        nav.setElevation(10);
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
        b.setPadding(4, 16, 4, 16);
        b.setLayoutParams(
                new LinearLayout.LayoutParams(0, -2, 1));
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

        box.addView(text(title, 13, Color.GRAY, true));
        box.addView(text(value, 25, NAVY, true));
        box.addView(text(subtitle, 12, Color.GRAY, false));

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

    private EditText field(String hint, int inputType) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setSingleLine(true);
        e.setTextSize(15);
        e.setInputType(inputType);
        e.setPadding(14, 12, 14, 12);
        return e;
    }

    private void showDashboard() {
        setup("Dashboard");

        String today = new SimpleDateFormat(
                "EEEE, dd MMMM yyyy",
                Locale.getDefault()).format(new Date());

        content.addView(text(today, 13, Color.GRAY, false));

        int employees = db.getEmployeeCount();

        heading("Company Overview");
        card("TOTAL EMPLOYEES",
                String.valueOf(employees),
                "Registered active employees");

        card("PRESENT TODAY", "0",
                "Attendance summary");

        card("MONTHLY PAYROLL", "Rs. 0",
                "Payroll summary");

        heading("Quick Actions");
        button("Add New Employee", this::addEmployeeForm);
        button("View Employees", this::showEmployees);
        button("Mark Attendance", this::showAttendance);
        button("View Payroll", this::showPayroll);
        button("Office Settings", this::showSettings);
    }

    private void showEmployees() {
    setup("Employees");
    heading("Employee Management");

    Cursor c = db.getEmployees();

    if (c.getCount() == 0) {
        content.addView(text("No employees added yet.",
                14, Color.GRAY, false));
    }

    while (c.moveToNext()) {
        final int id = c.getInt(
                c.getColumnIndexOrThrow("id"));

        final String name = c.getString(
                c.getColumnIndexOrThrow("name"));

        final String phone = c.getString(
                c.getColumnIndexOrThrow("phone"));

        final String designation = c.getString(
                c.getColumnIndexOrThrow("designation"));

        final String joining = c.getString(
                c.getColumnIndexOrThrow("joining_date"));

        final double salary = c.getDouble(
                c.getColumnIndexOrThrow("basic_salary"));

        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setPadding(16, 14, 16, 14);
        item.setBackgroundColor(Color.WHITE);

        item.addView(text(name, 18, NAVY, true));
        item.addView(text(designation, 13, Color.GRAY, false));
        item.addView(text("Rs. " + String.format(
                Locale.getDefault(), "%,.0f", salary),
                15, NAVY, true));

        LinearLayout actions = new LinearLayout(this);

        Button edit = new Button(this);
        edit.setText("EDIT");
        edit.setOnClickListener(v ->
                editEmployeeForm(id, name, phone,
                        designation, joining, salary));

        Button delete = new Button(this);
        delete.setText("DELETE");
        delete.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Delete Employee")
                        .setMessage("Are you sure?")
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Delete",
                                (dialog, which) -> {
                                    db.deleteEmployee(id);
                                    Toast.makeText(this,
                                            "Employee deleted",
                                            Toast.LENGTH_SHORT).show();
                                    showEmployees();
                                }).show());

        actions.addView(edit,
                new LinearLayout.LayoutParams(0, -2, 1));
        actions.addView(delete,
                new LinearLayout.LayoutParams(0, -2, 1));

        item.addView(actions);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, 0, 0, 12);
        content.addView(item, params);
    }

    c.close();

    button("Add New Employee", this::addEmployeeForm);
}

        c.close();

        button("Add New Employee", this::addEmployeeForm);
    }
private void editEmployeeForm(
        int id,
        String oldName,
        String oldPhone,
        String oldDesignation,
        String oldJoining,
        double oldSalary) {

    setup("Edit Employee");
    heading("Update Employee Details");

    EditText name = field("Full name",
            InputType.TYPE_CLASS_TEXT);
    EditText designation = field("Designation",
            InputType.TYPE_CLASS_TEXT);
    EditText phone = field("Phone number",
            InputType.TYPE_CLASS_PHONE);
    EditText joining = field("Joining date (YYYY-MM-DD)",
            InputType.TYPE_CLASS_DATETIME |
            InputType.TYPE_DATETIME_VARIATION_DATE);
    EditText salary = field("Monthly basic salary",
            InputType.TYPE_CLASS_NUMBER |
            InputType.TYPE_NUMBER_FLAG_DECIMAL);

    name.setText(oldName);
    designation.setText(oldDesignation);
    phone.setText(oldPhone);
    joining.setText(oldJoining);
    salary.setText(String.valueOf(oldSalary));

    content.addView(name);
    content.addView(designation);
    content.addView(phone);
    content.addView(joining);
    content.addView(salary);

    button("Save Changes", () -> {
        String n = name.getText().toString().trim();
        String d = designation.getText().toString().trim();
        String p = phone.getText().toString().trim();
        String j = joining.getText().toString().trim();
        String s = salary.getText().toString().trim();

        if (n.isEmpty() || s.isEmpty()) {
            Toast.makeText(this,
                    "Name and salary are required",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(s);

            if (amount < 0) {
                throw new Exception();
            }

            boolean updated = db.updateEmployee(
                    id, n, p, d, j, amount);

            Toast.makeText(this,
                    updated ? "Employee updated"
                            : "Update failed",
                    Toast.LENGTH_SHORT).show();

            showEmployees();

        } catch (Exception e) {
            salary.setError("Enter valid salary");
        }
    });
}
    private void addEmployeeForm() {
        setup("New Employee");

        heading("Employee Details");

        EditText name = field("Full name",
                InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        EditText designation = field("Designation",
                InputType.TYPE_CLASS_TEXT);

        EditText phone = field("Phone number",
                InputType.TYPE_CLASS_PHONE);

        EditText joining = field("Joining date (YYYY-MM-DD)",
                InputType.TYPE_CLASS_DATETIME |
                InputType.TYPE_DATETIME_VARIATION_DATE);

        EditText salary = field("Monthly basic salary",
                InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_DECIMAL);

        content.addView(name);
        content.addView(designation);
        content.addView(phone);
        content.addView(joining);
        content.addView(salary);

        button("Save Employee", () -> {
            String n = name.getText().toString().trim();
            String d = designation.getText().toString().trim();
            String p = phone.getText().toString().trim();
            String j = joining.getText().toString().trim();
            String s = salary.getText().toString().trim();

            if (n.isEmpty()) {
                name.setError("Name required");
                return;
            }

            if (s.isEmpty()) {
                salary.setError("Salary required");
                return;
            }

            double amount;

            try {
                amount = Double.parseDouble(s);
                if (amount < 0) throw new Exception();
            } catch (Exception e) {
                salary.setError("Enter a valid salary");
                return;
            }

            long result = db.addEmployee(
                    n, p, d, j, amount, "");

            if (result != -1) {
                Toast.makeText(this,
                        "Employee saved successfully",
                        Toast.LENGTH_SHORT).show();
                showEmployees();
            } else {
                Toast.makeText(this,
                        "Unable to save employee",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    
private void showAttendance() {
    setup("Attendance");
    heading("Daily Employee Attendance");

    String today = new SimpleDateFormat(
            "yyyy-MM-dd", Locale.getDefault())
            .format(new Date());

    String displayDate = new SimpleDateFormat(
            "dd MMMM yyyy", Locale.getDefault())
            .format(new Date());

    content.addView(text(
            displayDate, 15, NAVY, true));

    Cursor employees = db.getActiveEmployees();

    java.util.ArrayList<Integer> ids =
            new java.util.ArrayList<>();

    java.util.ArrayList<String> names =
            new java.util.ArrayList<>();

    while (employees.moveToNext()) {
        ids.add(employees.getInt(0));
        names.add(employees.getString(1));
    }
    employees.close();

    if (ids.isEmpty()) {
        heading("No Employees Found");
        button("Add Employee", this::addEmployeeForm);
        return;
    }

    heading("Select Employee");

    Spinner employeeSpinner = new Spinner(this);

    ArrayAdapter<String> adapter =
            new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_item,
                    names);

    adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item);

    employeeSpinner.setAdapter(adapter);
    content.addView(employeeSpinner);

    button("CHECK IN", () -> {
        int employeeId = ids.get(
                employeeSpinner.getSelectedItemPosition());

        String time = new SimpleDateFormat(
                "HH:mm", Locale.getDefault())
                .format(new Date());

        boolean saved = db.checkIn(
                employeeId, today, time);

        Toast.makeText(this,
                saved ? "Check-in saved: " + time
                      : "Check-in failed",
                Toast.LENGTH_SHORT).show();

        showAttendance();
    });

    button("CHECK OUT", () -> {
        int employeeId = ids.get(
                employeeSpinner.getSelectedItemPosition());

        String time = new SimpleDateFormat(
                "HH:mm", Locale.getDefault())
                .format(new Date());

        boolean saved = db.checkOut(
                employeeId, today, time);

        Toast.makeText(this,
                saved ? "Check-out saved: " + time
                      : "Check-in record not found",
                Toast.LENGTH_SHORT).show();

        showAttendance();
    });

    heading("Today's Attendance Records");

    Cursor records = db.getAttendanceForDate(today);

    if (records.getCount() == 0) {
        content.addView(text(
                "No attendance marked yet.",
                14, Color.GRAY, false));
    }

    while (records.moveToNext()) {
        String employeeName = records.getString(
                records.getColumnIndexOrThrow("name"));

        String in = records.getString(
                records.getColumnIndexOrThrow("check_in"));

        String out = records.getString(
                records.getColumnIndexOrThrow("check_out"));

        card(employeeName,
                "In: " + (in == null ? "--" : in),
                "Out: " + (out == null ? "--" : out));
    }

    records.close();
}


    private void showPayroll() {
        setup("Payroll");
        heading("Salary Management");

        card("MONTHLY SALARY", "Rs. 0",
                "Basic salary total");
        card("OVERTIME", "Rs. 0",
                "Overtime amount");
        card("DEDUCTIONS", "Rs. 0",
                "Advances and deductions");
        card("NET PAYABLE", "Rs. 0",
                "Final salary");

        content.addView(text(
                "Payroll calculations and salary slips will be connected next.",
                13, Color.GRAY, false));
    }

    private void showSettings() {
        setup("Office Settings");

        heading("Adjustable Office Timings");

        EditText start = field("Office start e.g. 09:00",
                InputType.TYPE_CLASS_DATETIME |
                InputType.TYPE_DATETIME_VARIATION_TIME);

        EditText end = field("Office end e.g. 17:00",
                InputType.TYPE_CLASS_DATETIME |
                InputType.TYPE_DATETIME_VARIATION_TIME);

        EditText grace = field("Late grace minutes",
                InputType.TYPE_CLASS_NUMBER);

        EditText overtime = field("Overtime threshold in minutes",
                InputType.TYPE_CLASS_NUMBER);

        EditText breakTime = field("Break duration in minutes",
                InputType.TYPE_CLASS_NUMBER);

        start.setText(db.getSetting("office_start", "09:00"));
        end.setText(db.getSetting("office_end", "17:00"));
        grace.setText(db.getSetting("late_grace", "10"));
        overtime.setText(db.getSetting("overtime_after", "480"));
        breakTime.setText(db.getSetting("break_minutes", "60"));

        content.addView(start);
        content.addView(end);
        content.addView(grace);
        content.addView(overtime);
        content.addView(breakTime);

        button("Save Office Settings", () -> {
            String a = start.getText().toString().trim();
            String b = end.getText().toString().trim();
            String g = grace.getText().toString().trim();
            String o = overtime.getText().toString().trim();
            String br = breakTime.getText().toString().trim();

            if (!validTime(a) || !validTime(b)) {
                Toast.makeText(this,
                        "Time format must be HH:mm",
                        Toast.LENGTH_LONG).show();
                return;
            }

            try {
                if (Integer.parseInt(g) < 0 ||
                    Integer.parseInt(o) < 0 ||
                    Integer.parseInt(br) < 0) {
                    throw new Exception();
                }
            } catch (Exception e) {
                Toast.makeText(this,
                        "Enter valid minutes",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            db.saveSetting("office_start", a);
            db.saveSetting("office_end", b);
            db.saveSetting("late_grace", g);
            db.saveSetting("overtime_after", o);
            db.saveSetting("break_minutes", br);

            Toast.makeText(this,
                    "Office settings saved",
                    Toast.LENGTH_SHORT).show();
        });

        heading("Security");
        button("Set / Change PIN", this::setPin);
    }

    private boolean validTime(String value) {
        try {
            return value.matches(
                    "([01][0-9]|2[0-3]):[0-5][0-9]");
        } catch (Exception e) {
            return false;
        }
    }

    private void setPin() {
        EditText pin = field("Enter new 4-8 digit PIN",
                InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_VARIATION_PASSWORD);

        new AlertDialog.Builder(this)
                .setTitle("App Security PIN")
                .setView(pin)
                .setPositiveButton("Save", (dialog, which) -> {
                    SecurityManager security =
                            new SecurityManager(this);

                    if (security.setPin(
                            pin.getText().toString())) {
                        Toast.makeText(this,
                                "PIN saved",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this,
                                "Enter 4 to 8 digits",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
