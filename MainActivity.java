package com.pakrose.hrpayroll;

import android.app.*;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.*;
import android.widget.*;
import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
    final int navy=Color.rgb(18,35,61), teal=Color.rgb(0,150,145), bg=Color.rgb(245,247,250);
    DB db; LinearLayout root, content; String page="Dashboard";
    public void onCreate(Bundle b){super.onCreate(b); db=new DB(this); show("Dashboard");}
    TextView text(String s,int size,int color,boolean bold){ TextView t=new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color); if(bold)t.setTypeface(null,Typeface.BOLD); t.setPadding(0,5,0,5); return t; }
    GradientDrawable shape(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(radius);return d;}
    LinearLayout col(){LinearLayout l=new LinearLayout(this);l.setOrientation(1);return l;}
    LinearLayout row(){LinearLayout l=new LinearLayout(this);l.setOrientation(0);l.setGravity(Gravity.CENTER_VERTICAL);return l;}
    void show(String p){
        page=p; root=col();root.setBackgroundColor(bg);setContentView(root);
        LinearLayout header=col();header.setPadding(20,22,20,18);header.setBackgroundColor(navy);
        header.addView(text("PAK ROSE ENTERPRISES",13,0xff9bd9d5,true));
        header.addView(text("HR & PAYROLL",23,Color.WHITE,true));
        header.addView(text("Attendance  •  Salary  •  Employees",12,0xffd5dfeb,false));root.addView(header);
        ScrollView sc=new ScrollView(this);content=col();content.setPadding(16,16,16,20);sc.addView(content);root.addView(sc,new LinearLayout.LayoutParams(-1,0,1));
        if(p.equals("Dashboard")) dashboard(); else if(p.equals("Employees")) employees(); else if(p.equals("Attendance")) attendance(); else payroll();
        LinearLayout nav=row();nav.setBackgroundColor(Color.WHITE);String[] tabs={"Dashboard","Employees","Attendance","Payroll"};
        for(String s:tabs){TextView b=text(s,12,page.equals(s)?teal:navy,true);b.setGravity(Gravity.CENTER);b.setPadding(4,17,4,17);nav.addView(b,new LinearLayout.LayoutParams(0,-2,1));b.setOnClickListener(v->show(s));}root.addView(nav);
    }
    void card(String title,String value,String sub){LinearLayout c=col();c.setPadding(16,14,16,14);c.setBackground(shape(Color.WHITE,18));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.bottomMargin=12;content.addView(c,lp);c.addView(text(title,13,0xff64748b,true));c.addView(text(value,25,navy,true));c.addView(text(sub,12,0xff64748b,false));}
    void heading(String s){content.addView(text(s,19,navy,true));}
    void dashboard(){Cursor c=db.getReadableDatabase().rawQuery("SELECT COUNT(*) FROM employees",null);c.moveToFirst();int n=c.getInt(0);c.close();card("TOTAL EMPLOYEES",String.valueOf(n),"Registered team members");card("TODAY",new SimpleDateFormat("dd MMM yyyy",Locale.getDefault()).format(new Date()),"Attendance date");card("MONTHLY PAYROLL", "Rs. "+money(db.totalSalary()),"Based on saved basic salaries");heading("Quick actions");button("＋  Add employee",()->addEmployee());button("✓  Mark attendance",()->show("Attendance"));button("▤  View payroll",()->show("Payroll"));}
    String money(long x){return String.format(Locale.US,"%,d",x);}
    void button(String s,Runnable r){Button b=new Button(this);b.setText(s);b.setAllCaps(false);b.setTextColor(Color.WHITE);b.setBackground(shape(teal,14));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.bottomMargin=10;content.addView(b,lp);b.setOnClickListener(v->r.run());}
    void employees(){heading("Employee directory");button("＋  Add new employee",()->addEmployee());Cursor c=db.getReadableDatabase().rawQuery("SELECT id,name,role,salary FROM employees ORDER BY name",null);if(c.getCount()==0)content.addView(text("No employees yet. Add your first employee.",14,0xff64748b,false));while(c.moveToNext()){LinearLayout box=col();box.setPadding(14,12,14,12);box.setBackground(shape(Color.WHITE,14));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.bottomMargin=9;content.addView(box,lp);box.addView(text(c.getString(1),16,navy,true));box.addView(text(c.getString(2)+"  •  Rs. "+money(c.getLong(3))+"/month",13,0xff64748b,false));}c.close();}
    void addEmployee(){LinearLayout form=col();EditText name=new EditText(this);name.setHint("Employee full name");EditText role=new EditText(this);role.setHint("Designation (optional)");EditText salary=new EditText(this);salary.setHint("Monthly salary (PKR)");salary.setInputType(2);form.addView(name);form.addView(role);form.addView(salary);new AlertDialog.Builder(this).setTitle("Add employee").setView(form).setNegativeButton("Cancel",null).setPositiveButton("Save",(d,w)->{String nm=name.getText().toString().trim();if(nm.isEmpty()){toast("Name is required");return;}long sal=0;try{sal=Long.parseLong(salary.getText().toString());}catch(Exception e){}ContentValues v=new ContentValues();v.put("name",nm);v.put("role",role.getText().toString());v.put("salary",sal);db.getWritableDatabase().insert("employees",null,v);show("Employees");}).show();}
    void attendance(){heading("Daily attendance");TextView date=text(new SimpleDateFormat("EEEE, dd MMM yyyy",Locale.getDefault()).format(new Date()),14,0xff64748b,false);content.addView(date);Cursor c=db.getReadableDatabase().rawQuery("SELECT id,name FROM employees ORDER BY name",null);while(c.moveToNext()){final int id=c.getInt(0);LinearLayout line=row();line.setPadding(12,8,8,8);line.setBackground(shape(Color.WHITE,12));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.bottomMargin=8;content.addView(line,lp);TextView nm=text(c.getString(1),14,navy,true);line.addView(nm,new LinearLayout.LayoutParams(0,-2,1));Spinner sp=new Spinner(this);String[] opts={"P","A","PL","UL"};sp.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,opts));line.addView(sp);sp.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener(){boolean first=true;public void onNothingSelected(android.widget.AdapterView<?> a){}public void onItemSelected(android.widget.AdapterView<?> a,View v,int pos,long row){if(first){first=false;return;}String day=new SimpleDateFormat("yyyy-MM-dd",Locale.US).format(new Date());db.saveAttendance(id,day,opts[pos]);toast("Attendance saved");}});}c.close();if(db.countEmployees()==0)content.addView(text("Add employees first to mark attendance.",14,0xff64748b,false));}
    void payroll(){heading("Monthly payroll overview");long total=db.totalSalary();card("BASIC MONTHLY PAYROLL","Rs. "+money(total),"Total of employee basic salaries");content.addView(text("Salary estimate",16,navy,true));content.addView(text("This first edition stores employee salaries and daily attendance. Advanced deductions, overtime, advances and printable salary slips will be added in the next development stage.",13,0xff64748b,false));button("＋  Add employee",()->addEmployee());}
    void toast(String s){Toast.makeText(this,s,Toast.LENGTH_SHORT).show();}
    class DB extends SQLiteOpenHelper{
        DB(Context c){super(c,"pakrose_hr_v2.db",null,1);}
        public void onCreate(SQLiteDatabase d){d.execSQL("CREATE TABLE employees(id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,role TEXT,salary INTEGER)");d.execSQL("CREATE TABLE attendance(id INTEGER PRIMARY KEY AUTOINCREMENT,employee_id INTEGER,day TEXT,status TEXT,UNIQUE(employee_id,day))");}
        public void onUpgrade(SQLiteDatabase d,int o,int n){}
        int countEmployees(){Cursor c=getReadableDatabase().rawQuery("SELECT COUNT(*) FROM employees",null);c.moveToFirst();int x=c.getInt(0);c.close();return x;}
        long totalSalary(){Cursor c=getReadableDatabase().rawQuery("SELECT COALESCE(SUM(salary),0) FROM employees",null);c.moveToFirst();long x=c.getLong(0);c.close();return x;}
        void saveAttendance(int id,String day,String status){ContentValues v=new ContentValues();v.put("employee_id",id);v.put("day",day);v.put("status",status);getWritableDatabase().insertWithOnConflict("attendance",null,v,SQLiteDatabase.CONFLICT_REPLACE);}
    }
}