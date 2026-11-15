
package com.pakrose.hrpayroll;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "pakrose_hr.db";
    private static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE employees (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "designation TEXT," +
                "phone TEXT," +
                "salary REAL DEFAULT 0," +
                "joining_date TEXT," +
                "active INTEGER DEFAULT 1)");

        db.execSQL("CREATE TABLE attendance (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "employee_id INTEGER NOT NULL," +
                "work_date TEXT NOT NULL," +
                "status TEXT DEFAULT 'Present'," +
                "check_in TEXT," +
                "check_out TEXT," +
                "late_minutes INTEGER DEFAULT 0," +
                "early_minutes INTEGER DEFAULT 0," +
                "overtime_minutes INTEGER DEFAULT 0," +
                "UNIQUE(employee_id, work_date))");

        db.execSQL("CREATE TABLE office_settings (" +
                "setting_key TEXT PRIMARY KEY," +
                "setting_value TEXT)");

        saveDefault(db, "office_start", "09:00");
        saveDefault(db, "office_end", "17:00");
        saveDefault(db, "late_grace", "10");
        saveDefault(db, "overtime_after", "480");
        saveDefault(db, "break_minutes", "60");
        saveDefault(db, "weekly_off", "Sunday");
    }

    private void saveDefault(SQLiteDatabase db,
                             String key, String value) {
        ContentValues cv = new ContentValues();
        cv.put("setting_key", key);
        cv.put("setting_value", value);
        db.insert("office_settings", null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion, int newVersion) {
        // Future database upgrades will be handled here.
    }

    public long addEmployee(String name, String designation,
                            String phone, double salary,
                            String joiningDate) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("name", name);
        cv.put("designation", designation);
        cv.put("phone", phone);
        cv.put("salary", salary);
        cv.put("joining_date", joiningDate);

        return db.insert("employees", null, cv);
    }

    public Cursor getEmployees() {
        return getReadableDatabase().rawQuery(
                "SELECT * FROM employees WHERE active=1 ORDER BY name",
                null);
    }

    public int updateEmployee(int id, String name,
                              String designation, String phone,
                              double salary, String joiningDate) {

        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("designation", designation);
        cv.put("phone", phone);
        cv.put("salary", salary);
        cv.put("joining_date", joiningDate);

        return getWritableDatabase().update(
                "employees", cv, "id=?",
                new String[]{String.valueOf(id)});
    }

    public int deleteEmployee(int id) {
        ContentValues cv = new ContentValues();
        cv.put("active", 0);

        return getWritableDatabase().update(
                "employees", cv, "id=?",
                new String[]{String.valueOf(id)});
    }

    public long markAttendance(int employeeId, String date,
                               String status, String checkIn,
                               String checkOut) {

        ContentValues cv = new ContentValues();
        cv.put("employee_id", employeeId);
        cv.put("work_date", date);
        cv.put("status", status);
        cv.put("check_in", checkIn);
        cv.put("check_out", checkOut);

        return getWritableDatabase().insertWithOnConflict(
                "attendance", null, cv,
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    public Cursor getAttendance(String date) {
        return getReadableDatabase().rawQuery(
                "SELECT attendance.*, employees.name " +
                "FROM attendance INNER JOIN employees " +
                "ON attendance.employee_id=employees.id " +
                "WHERE work_date=? ORDER BY employees.name",
                new String[]{date});
    }

    public void saveSetting(String key, String value) {
        ContentValues cv = new ContentValues();
        cv.put("setting_key", key);
        cv.put("setting_value", value);

        getWritableDatabase().insertWithOnConflict(
                "office_settings", null, cv,
                SQLiteDatabase.CONFLICT_REPLACE);
    }

    public String getSetting(String key) {
        Cursor c = getReadableDatabase().query(
                "office_settings",
                new String[]{"setting_value"},
                "setting_key=?",
                new String[]{key},
                null, null, null);

        String value = "";

        if (c.moveToFirst()) {
            value = c.getString(0);
        }

        c.close();
        return value;
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
}
