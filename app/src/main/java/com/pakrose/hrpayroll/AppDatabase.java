
package com.pakrose.hrpayroll;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppDatabase extends SQLiteOpenHelper {

    private static final String DB_NAME = "pakrose_hr_final.db";
    private static final int DB_VERSION = 1;

    public AppDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE employees (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "phone TEXT," +
                "designation TEXT," +
                "joining_date TEXT," +
                "basic_salary REAL DEFAULT 0," +
                "photo_path TEXT," +
                "active INTEGER DEFAULT 1)");

        db.execSQL("CREATE TABLE attendance (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "employee_id INTEGER," +
                "date TEXT," +
                "check_in TEXT," +
                "check_out TEXT," +
                "late_minutes INTEGER DEFAULT 0," +
                "early_minutes INTEGER DEFAULT 0," +
                "overtime_minutes INTEGER DEFAULT 0," +
                "status TEXT," +
                "notes TEXT)");

        db.execSQL("CREATE TABLE payroll (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "employee_id INTEGER," +
                "month TEXT," +
                "basic_salary REAL DEFAULT 0," +
                "overtime_amount REAL DEFAULT 0," +
                "advance REAL DEFAULT 0," +
                "deductions REAL DEFAULT 0," +
                "bonus REAL DEFAULT 0," +
                "net_salary REAL DEFAULT 0," +
                "payment_status TEXT DEFAULT 'Pending'," +
                "payment_date TEXT)");

        db.execSQL("CREATE TABLE office_settings (" +
                "setting_key TEXT PRIMARY KEY," +
                "setting_value TEXT)");

        insertSetting(db, "office_start", "09:00");
        insertSetting(db, "office_end", "17:00");
        insertSetting(db, "late_grace", "10");
        insertSetting(db, "overtime_after", "480");
        insertSetting(db, "break_minutes", "60");
        insertSetting(db, "weekly_off", "Sunday");
    }

    private void insertSetting(
            SQLiteDatabase db, String key, String value) {

        ContentValues cv = new ContentValues();
        cv.put("setting_key", key);
        cv.put("setting_value", value);
        db.insert("office_settings", null, cv);
    }

    public long addEmployee(
            String name,
            String phone,
            String designation,
            String joiningDate,
            double salary,
            String photoPath) {

        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("phone", phone);
        cv.put("designation", designation);
        cv.put("joining_date", joiningDate);
        cv.put("basic_salary", salary);
        cv.put("photo_path", photoPath);
        cv.put("active", 1);

        return getWritableDatabase().insert(
                "employees", null, cv);
    }

    public Cursor getEmployees() {
        return getReadableDatabase().rawQuery(
                "SELECT * FROM employees WHERE active=1 ORDER BY name",
                null);
    }

    public int getEmployeeCount() {
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM employees WHERE active=1",
                null);

        int count = 0;
        if (c.moveToFirst()) count = c.getInt(0);
        c.close();

        return count;
    }

    public boolean saveSetting(String key, String value) {
        ContentValues cv = new ContentValues();
        cv.put("setting_key", key);
        cv.put("setting_value", value);

        return getWritableDatabase().insertWithOnConflict(
                "office_settings",
                null,
                cv,
                SQLiteDatabase.CONFLICT_REPLACE
        ) != -1;
    }

    public String getSetting(String key, String fallback) {
        Cursor c = getReadableDatabase().query(
                "office_settings",
                new String[]{"setting_value"},
                "setting_key=?",
                new String[]{key},
                null, null, null);

        String value = fallback;

        if (c.moveToFirst()) {
            value = c.getString(0);
        }

        c.close();
        return value;
    }

    public long addPayroll(
            int employeeId,
            String month,
            double basicSalary,
            double overtime,
            double advance,
            double deductions,
            double bonus,
            double netSalary) {

        ContentValues cv = new ContentValues();
        cv.put("employee_id", employeeId);
        cv.put("month", month);
        cv.put("basic_salary", basicSalary);
        cv.put("overtime_amount", overtime);
        cv.put("advance", advance);
        cv.put("deductions", deductions);
        cv.put("bonus", bonus);
        cv.put("net_salary", netSalary);
        cv.put("payment_status", "Pending");

        return getWritableDatabase().insert(
                "payroll", null, cv);
    }

    public Cursor getPayroll(String month) {
        return getReadableDatabase().rawQuery(
                "SELECT payroll.*, employees.name " +
                "FROM payroll INNER JOIN employees " +
                "ON payroll.employee_id=employees.id " +
                "WHERE payroll.month=?",
                new String[]{month});
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db, int oldVersion, int newVersion) {
        // Future database migrations will be handled here.
    }
   
public Cursor getActiveEmployees() {
    return getReadableDatabase().rawQuery(
            "SELECT id, name FROM employees WHERE active=1 ORDER BY name",
            null
    );
}

public boolean checkIn(int employeeId, String date, String time) {
    SQLiteDatabase db = getWritableDatabase();

    Cursor c = db.rawQuery(
            "SELECT id FROM attendance WHERE employee_id=? AND date=?",
            new String[]{
                    String.valueOf(employeeId), date
            }
    );

    boolean success;

    if (c.moveToFirst()) {
        c.close();

        ContentValues cv = new ContentValues();
        cv.put("check_in", time);
        cv.put("status", "Present");

        success = db.update(
                "attendance", cv,
                "employee_id=? AND date=?",
                new String[]{
                        String.valueOf(employeeId), date
                }
        ) > 0;

    } else {
        c.close();

        ContentValues cv = new ContentValues();
        cv.put("employee_id", employeeId);
        cv.put("date", date);
        cv.put("check_in", time);
        cv.put("status", "Present");

        success = db.insert("attendance", null, cv) != -1;
    }

    return success;
}

public boolean checkOut(int employeeId, String date, String time) {
    ContentValues cv = new ContentValues();
    cv.put("check_out", time);

    return getWritableDatabase().update(
            "attendance",
            cv,
            "employee_id=? AND date=? AND check_in IS NOT NULL",
            new String[]{
                    String.valueOf(employeeId), date
            }
    ) > 0;
}

public Cursor getAttendanceForDate(String date) {
    return getReadableDatabase().rawQuery(
            "SELECT attendance.*, employees.name " +
            "FROM attendance INNER JOIN employees " +
            "ON attendance.employee_id=employees.id " +
            "WHERE attendance.date=? ORDER BY employees.name",
            new String[]{date}
    );
} 
}
