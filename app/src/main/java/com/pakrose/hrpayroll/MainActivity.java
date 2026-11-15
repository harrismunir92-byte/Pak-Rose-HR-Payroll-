package com.pakrose.hrpayroll;

import android.app.*;
import android.os.Bundle;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfDocument;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.text.*;
import java.util.*;

public class MainActivity extends Activity {
    static final int NAVY=Color.rgb(17,38,66), BLUE=Color.rgb(29,78,137), TEAL=Color.rgb(0,150,145);
    static final int BG=Color.rgb(244,247,251), INK=Color.rgb(31,43,59), MUTED=Color.rgb(112,126,145);
    DB db; LinearLayout root, body, nav; String page="Home"; int selectedEmployee=-1;
    TextView title; final String[] tabs={"Home","Employees","Attendance","Payroll","Reports"};
    @Override public void onCreate(Bundle b){super.onCreate(b); db=new DB(this); getWindow().setStatusBarColor(NAVY); getWindow().setNavigationBarColor(NAVY); render();}
    int dp(float n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
    TextView text(String s,int size,int color,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);if(bold)t.setTypeface(null,Typeface.BOLD);return t;}
    LinearLayout vertical(){LinearLayout l=new LinearLayout(this);l.setOrientation(1);return l;}
    LinearLayout horizontal(){LinearLayout l=new LinearLayout(this);l.setOrientation(0);l.setGravity(Gravity.CENTER_VERTICAL);return l;}
    GradientDrawable bg(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));return d;}
    void pad(View v,int a){v.setPadding(dp(a),dp(a),dp(a),dp(a));}
    Button button(String s){Button b=new Button(this);b.setText(s);b.setAllCaps(false);b.setTextColor(Color.WHITE);b.setTextSize(14);b.setBackground(bg(BLUE,12));return b;}
    void render(){
        root=vertical();root.setBackgroundColor(BG);setContentView(root);
        LinearLayout header=vertical();header.setBackgroundColor(NAVY);header.setPadding(dp(20),dp(18),dp(20),dp(18));
        title=text("Pak Rose Enterprises",20,Color.WHITE,true);header.addView(title);
        TextView sub=text("HR & PAYROLL  •  "+page.toUpperCase(Locale.ROOT),11,0xFFB9C9DC,true);sub.setPadding(0,dp(5),0,0);header.addView(sub);root.addView(header);
        ScrollView scroll=new ScrollView(this);body=vertical();body.setPadding(dp(16),dp(16),dp(16),dp(18));scroll.addView(body);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        nav=horizontal();nav.setBackgroundColor(Color.WHITE);nav.setElevation(dp(8));
        for(String t:tabs){TextView n=text(t,10,page.equals(t)?TEAL:MUTED,true);n.setGravity(Gravity.CENTER);n.setPadding(dp(5),dp(15),dp(5),dp(15));nav.addView(n,new LinearLayout.LayoutParams(0,-2,1));n.setOnClickListener(v->{page=t;render();});}
        root.addView(nav);
        if(page.equals("Home")) home(); else if(page.equals("Employees")) employees(); else if(page.equals("Attendance")) attendance(); else if(page.equals("Payroll")) payroll(); else reports();
    }
    void card(String heading,String value,String note){
        LinearLayout c=vertical();c.setBackground(bg(Color.WHITE,16));c.setElevation(dp(2));c.setPadding(dp(16),dp(14),dp(16),dp(14));
        c.addView(text(heading,12,MUTED,true));TextView v=text(value,24,NAVY,true);v.setPadding(0,dp(7),0,dp(3));c.addView(v);c.addView(text(note,11,MUTED,false));
        LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.bottomMargin=dp(10);body.addView(c,p);
    }
    void heading(String s){TextView t=text(s,17,INK,true);t.setPadding(0,dp(7),0,dp(12));body.addView(t);}
    void home(){
        Cursor e=db.getReadableDatabase().rawQuery("SELECT COUNT(*) FROM employees",null);e.moveToFirst();int count=e.getInt(0);e.close();
        String today=new SimpleDateFormat("yyyy-MM-dd",Locale.US).format(new Date());
        Cursor a=db.getReadableDatabase().rawQuery("SELECT COUNT(*) FROM attendance WHERE day=? AND status='P'",new String[]{today});a.moveToFirst();int present=a.getInt(0);a.close();
        Cursor p=db.getReadableDatabase().rawQuery("SELECT IFNULL(SUM(salary),0) FROM employees",null);p.moveToFirst();double total=p.getDouble(0);p.close();
        heading("Overview");card("TOTAL EMPLOYEES",String.valueOf(count),"Registered employee records");
        card("PRESENT TODAY",String.valueOf(present),"Attendance marked for "+today);
        card("MONTHLY BASIC PAYROLL",money(total),"Before deductions and advances");
        heading("Quick actions");addAction("＋  Add employee",()->employeeForm(null));
        addAction("✓  Mark today's attendance",()->{page="Attendance";render();});
        addAction("▤  View payroll",()->{page="Payroll";render();});
    }
    void addAction(String s,Runnable r){Button b=button(s);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.bottomMargin=dp(9);body.addView(b,p);b.setOnClickListener(v->r.run());}
    void employees(){
        heading("Employee directory");addAction("＋  Add new employee",()->employeeForm(null));
        Cursor c=db.getReadableDatabase().rawQuery("SELECT id,name,role,phone,salary,joining,photo FROM employees ORDER BY name",null);
        if(c.getCount()==0){empty("No employees yet. Add your first employee.");}
        while(c.moveToNext()){
            final int id=c.getInt(0);String name=c.getString(1),role=c.getString(2),phone=c.getString(3),join=c.getString(5);
            LinearLayout box=vertical();box.setBackground(bg(Color.WHITE,14));box.setPadding(dp(14),dp(12),dp(14),dp(12));
            TextView n=text(name,16,NAVY,true);box.addView(n);box.addView(text((role==null?"":role)+"  •  "+(phone==null?"":phone),12,MUTED,false));
            box.addView(text("Salary: "+money(c.getDouble(4))+" / month     Joined: "+(join==null?"—":join),12,INK,true));
            LinearLayout actions=horizontal();Button edit=smallButton("Edit"),del=smallButton("Delete");
            actions.addView(edit);actions.addView(del);box.addView(actions);
            edit.setOnClickListener(v->loadEmployee(id));del.setOnClickListener(v->confirmDelete(id,name));
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.bottomMargin=dp(10);body.addView(box,lp);
        }c.close();
    }
    Button smallButton(String s){Button b=button(s);b.setTextSize(12);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(42),1);p.rightMargin=dp(6);b.setLayoutParams(p);return b;}
    void empty(String s){TextView t=text(s,13,MUTED,false);t.setPadding(dp(12),dp(22),dp(12),dp(22));body.addView(t);}
    void loadEmployee(int id){Cursor c=db.getReadableDatabase().rawQuery("SELECT id,name,role,phone,salary,joining,photo FROM employees WHERE id=?",new String[]{String.valueOf(id)});if(c.moveToFirst()){HashMap<String,String> m=new HashMap<>();String[] keys={"id","name","role","phone","salary","joining","photo"};for(int i=0;i<keys.length;i++)m.put(keys[i],c.isNull(i)?"":c.getString(i));employeeForm(m);}c.close();}
    void confirmDelete(int id,String name){new AlertDialog.Builder(this).setTitle("Delete employee").setMessage("Delete "+name+"? Attendance history will also be removed.").setNegativeButton("Cancel",null).setPositiveButton("Delete",(d,w)->{SQLiteDatabase x=db.getWritableDatabase();x.delete("attendance","employee_id=?",new String[]{""+id});x.delete("employees","id=?",new String[]{""+id});render();}).show();}
    EditText field(LinearLayout box,String label,String value,String hint,int inputType){
        TextView l=text(label,12,MUTED,true);l.setPadding(0,dp(8),0,dp(4));box.addView(l);
        EditText e=new EditText(this);e.setSingleLine(true);e.setTextSize(14);e.setText(value==null?"":value);e.setHint(hint);e.setInputType(inputType);e.setBackground(bg(0xFFF2F5F9,10));e.setPadding(dp(12),dp(10),dp(12),dp(10));box.addView(e);return e;
    }
    void employeeForm(HashMap<String,String> m){
        boolean edit=m!=null;LinearLayout form=vertical();form.setPadding(dp(18),dp(8),dp(18),0);
        EditText name=field(form,"FULL NAME",edit?m.get("name"):"","Employee full name",android.text.InputType.TYPE_CLASS_TEXT|android.text.InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        EditText role=field(form,"DESIGNATION",edit?m.get("role"):"","e.g. Supervisor",android.text.InputType.TYPE_CLASS_TEXT);
        EditText phone=field(form,"PHONE NUMBER",edit?m.get("phone"):"","03XXXXXXXXX",android.text.InputType.TYPE_CLASS_PHONE);
        EditText salary=field(form,"MONTHLY BASIC SALARY (PKR)",edit?m.get("salary"):"","50000",android.text.InputType.TYPE_CLASS_NUMBER|android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        EditText joining=field(form,"JOINING DATE",edit?m.get("joining"):"","YYYY-MM-DD",android.text.InputType.TYPE_CLASS_DATETIME);
        Button save=button(edit?"Save changes":"Save employee");form.addView(save);ScrollView sc=new ScrollView(this);sc.addView(form);
        AlertDialog dialog=new AlertDialog.Builder(this).setTitle(edit?"Edit employee":"New employee").setView(sc).setNegativeButton("Cancel",null).create();
        save.setOnClickListener(v->{if(name.getText().toString().trim().isEmpty()){name.setError("Name required");return;}
            ContentValues cv=new ContentValues();cv.put("name",name.getText().toString().trim());cv.put("role",role.getText().toString().trim());cv.put("phone",phone.getText().toString().trim());
            cv.put("salary",num(salary.getText().toString()));cv.put("joining",joining.getText().toString().trim());
            if(edit)db.getWritableDatabase().update("employees",cv,"id=?",new String[]{m.get("id")});else db.getWritableDatabase().insert("employees",null,cv);
            dialog.dismiss();render();});
        dialog.show();
    }
    double num(String s){try{return Double.parseDouble(s.trim());}catch(Exception ex){return 0;}}
    String money(double n){return "Rs. "+new DecimalFormat("#,##0.00").format(n);}
    void attendance(){
        heading("Daily attendance");String today=new SimpleDateFormat("yyyy-MM-dd",Locale.US).format(new Date());
        EditText date=new EditText(this);date.setSingleLine(true);date.setText(today);date.setHint("YYYY-MM-DD");date.setBackground(bg(Color.WHITE,10));date.setPadding(dp(12),dp(10),dp(12),dp(10));body.addView(date);
        Button load=button("Load employees for this date");body.addView(load);
        LinearLayout list=vertical();body.addView(list);
        Runnable refresh=()->{list.removeAllViews();Cursor c=db.getReadableDatabase().rawQuery("SELECT id,name FROM employees ORDER BY name",null);
            while(c.moveToNext()){int id=c.getInt(0);String nm=c.getString(1);LinearLayout row=horizontal();row.setBackground(bg(Color.WHITE,12));row.setPadding(dp(10),dp(8),dp(10),dp(8));
                row.addView(text(nm,13,INK,true),new LinearLayout.LayoutParams(0,-2,1));
                Spinner sp=new Spinner(this);String[] opts={"P","A","PL","UL"};ArrayAdapter<String> ad=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,opts);sp.setAdapter(ad);
                Cursor old=db.getReadableDatabase().rawQuery("SELECT status FROM attendance WHERE employee_id=? AND day=?",new String[]{""+id,date.getText().toString()});
                if(old.moveToFirst()){String st=old.getString(0);for(int i=0;i<opts.length;i++)if(opts[i].equals(st))sp.setSelection(i);}old.close();
                row.addView(sp,new LinearLayout.LayoutParams(dp(90),-2));sp.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener(){public void onNothingSelected(android.widget.AdapterView<?> a){} public void onItemSelected(android.widget.AdapterView<?> a,View v,int pos,long l){ContentValues cv=new ContentValues();cv.put("employee_id",id);cv.put("day",date.getText().toString());cv.put("status",opts[pos]);db.getWritableDatabase().insertWithOnConflict("attendance",null,cv,SQLiteDatabase.CONFLICT_REPLACE);}});
                LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.bottomMargin=dp(7);list.addView(row,lp);
            }c.close();};
        load.setOnClickListener(v->refresh.run());refresh.run();
        body.addView(text("P = Present   A = Absent   PL = Paid Leave   UL = Unpaid Leave",11,MUTED,false));
    }
    void payroll(){
        heading("Monthly payroll");Calendar cal=Calendar.getInstance();String month=new SimpleDateFormat("yyyy-MM",Locale.US).format(cal.getTime());
        EditText monthField=new EditText(this);monthField.setText(month);monthField.setSingleLine(true);monthField.setHint("YYYY-MM");monthField.setBackground(bg(Color.WHITE,10));monthField.setPadding(dp(12),dp(10),dp(12),dp(10));body.addView(monthField);
        Button calc=button("Calculate payroll");body.addView(calc);LinearLayout results=vertical();body.addView(results);
        Runnable run=()->{results.removeAllViews();Cursor c=db.getReadableDatabase().rawQuery("SELECT id,name,salary FROM employees ORDER BY name",null);double grand=0;
            while(c.moveToNext()){int id=c.getInt(0);String name=c.getString(1);double salary=c.getDouble(2);String m=monthField.getText().toString();
                Cursor a=db.getReadableDatabase().rawQuery("SELECT SUM(CASE WHEN status IN ('P','PL') THEN 1 ELSE 0 END),SUM(CASE WHEN status='UL' THEN 1 ELSE 0 END),SUM(CASE WHEN status='A' THEN 1 ELSE 0 END) FROM attendance WHERE employee_id=? AND substr(day,1,7)=?",new String[]{""+id,m});
                int paid=0,ul=0,abs=0;if(a.moveToFirst()){paid=a.isNull(0)?0:a.getInt(0);ul=a.isNull(1)?0:a.getInt(1);abs=a.isNull(2)?0:a.getInt(2);}a.close();
                Calendar mc=Calendar.getInstance();try{mc.set(Integer.parseInt(m.substring(0,4)),Integer.parseInt(m.substring(5,7))-1,1);}catch(Exception ex){}
                int days=mc.getActualMaximum(Calendar.DAY_OF_MONTH);double deduction=(salary/ days)*(ul+abs);double net=Math.max(0,salary-deduction);grand+=net;
                LinearLayout card=vertical();card.setBackground(bg(Color.WHITE,12));card.setPadding(dp(13),dp(12),dp(13),dp(12));card.addView(text(name,15,NAVY,true));
                card.addView(text("Basic: "+money(salary)+"   |   Paid days: "+paid+"   |   Unpaid/Absent: "+(ul+abs),11,MUTED,false));
                card.addView(text("Deduction: "+money(deduction)+"   •   Net salary: "+money(net),13,TEAL,true));
                Button slip=smallButton("PDF payslip");card.addView(slip);slip.setOnClickListener(v->pdfSlip(name,m,salary,paid,ul+abs,deduction,net));
                LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.bottomMargin=dp(8);results.addView(card,lp);
            }c.close();TextView total=text("TOTAL NET PAYROLL: "+money(grand),16,NAVY,true);total.setPadding(0,dp(12),0,dp(20));results.addView(total);};
        calc.setOnClickListener(v->run.run());run.run();
    }
    void pdfSlip(String name,String month,double basic,int paid,int unpaid,double deduction,double net){
        try{PdfDocument doc=new PdfDocument();PdfDocument.PageInfo info=new PdfDocument.PageInfo.Builder(595,842,1).create();PdfDocument.Page p=doc.startPage(info);android.graphics.Canvas c=p.getCanvas();android.graphics.Paint paint=new android.graphics.Paint();
            paint.setColor(NAVY);paint.setTextSize(24);paint.setFakeBoldText(true);c.drawText("PAK ROSE ENTERPRISES",40,60,paint);
            paint.setColor(INK);paint.setTextSize(18);c.drawText("SALARY SLIP",40,110,paint);paint.setTextSize(14);paint.setFakeBoldText(false);
            String[] lines={"Employee: "+name,"Salary month: "+month,"Basic salary: "+money(basic),"Paid days: "+paid,"Unpaid / absent days: "+unpaid,"Deductions: "+money(deduction),"NET PAYABLE: "+money(net)};
            int y=170;for(String line:lines){c.drawText(line,45,y,paint);y+=42;}doc.finishPage(p);
            File f=new File(getExternalFilesDir(null),"SalarySlip_"+name.replaceAll("[^A-Za-z0-9]","_")+"_"+month+".pdf");FileOutputStream out=new FileOutputStream(f);doc.writeTo(out);out.close();doc.close();
            new AlertDialog.Builder(this).setMessage("PDF saved in app Documents:\n"+f.getAbsolutePath()).setPositiveButton("OK",null).show();
        }catch(Exception ex){toast("PDF error: "+ex.getMessage());}
    }
    void reports(){
        heading("Reports & data export");card("REPORTING PERIOD","Current month","Attendance and payroll summaries");
        addAction("Export employee list (CSV)",()->exportCsv("employees"));
        addAction("Export attendance (CSV)",()->exportCsv("attendance"));
        addAction("Export payroll summary (CSV)",()->exportCsv("payroll"));
        addAction("Backup database",()->backup());
        Cursor c=db.getReadableDatabase().rawQuery("SELECT COUNT(*) FROM attendance",null);c.moveToFirst();int n=c.getInt(0);c.close();card("ATTENDANCE RECORDS",String.valueOf(n),"Saved locally on this device");
        body.addView(text("Tip: Keep regular backups when changing phones or reinstalling the app.",12,MUTED,false));
    }
    void exportCsv(String type){
        StringBuilder s=new StringBuilder();
        Cursor c;
        if(type.equals("employees")){s.append("ID,Name,Designation,Phone,Monthly Salary,Joining Date\n");c=db.getReadableDatabase().rawQuery("SELECT id,name,role,phone,salary,joining FROM employees",null);}
        else if(type.equals("attendance")){s.append("Employee ID,Date,Status\n");c=db.getReadableDatabase().rawQuery("SELECT employee_id,day,status FROM attendance ORDER BY day",null);}
        else {s.append("Employee,Month,Basic Salary,Net Salary\n");c=db.getReadableDatabase().rawQuery("SELECT e.name,substr(a.day,1,7),e.salary FROM employees e LEFT JOIN attendance a ON e.id=a.employee_id GROUP BY e.id,substr(a.day,1,7)",null);}
        while(c.moveToNext()){for(int i=0;i<c.getColumnCount();i++){if(i>0)s.append(',');String v=c.getString(i);if(v!=null)s.append('"').append(v.replace("\"","\"\"")).append('"');}s.append('\n');}c.close();
        try{File f=new File(getExternalFilesDir(null),type+"_export.csv");FileOutputStream o=new FileOutputStream(f);o.write(s.toString().getBytes("UTF-8"));o.close();new AlertDialog.Builder(this).setMessage("CSV saved:\n"+f.getAbsolutePath()).setPositiveButton("OK",null).show();}catch(Exception ex){toast(ex.getMessage());}
    }
    void backup(){try{File src=getDatabasePath("pakrose.db");File dst=new File(getExternalFilesDir(null),"PakRose_Backup.db");FileInputStream in=new FileInputStream(src);FileOutputStream out=new FileOutputStream(dst);byte[] b=new byte[8192];int n;while((n=in.read(b))>0)out.write(b,0,n);in.close();out.close();toast("Backup saved: "+dst.getAbsolutePath());}catch(Exception ex){toast("Backup failed: "+ex.getMessage());}}
    void toast(String s){Toast.makeText(this,s,Toast.LENGTH_LONG).show();}
    static class DB extends SQLiteOpenHelper{
        DB(Context c){super(c,"pakrose.db",null,2);}
        public void onCreate(SQLiteDatabase d){
            d.execSQL("CREATE TABLE employees(id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT NOT NULL,role TEXT,phone TEXT,salary REAL DEFAULT 0,joining TEXT,photo TEXT)");
            d.execSQL("CREATE TABLE attendance(id INTEGER PRIMARY KEY AUTOINCREMENT,employee_id INTEGER,day TEXT,status TEXT,UNIQUE(employee_id,day))");
        }
        public void onUpgrade(SQLiteDatabase d,int old,int now){if(old<2){try{d.execSQL("ALTER TABLE employees ADD COLUMN joining TEXT");}catch(Exception ignored){}try{d.execSQL("ALTER TABLE employees ADD COLUMN photo TEXT");}catch(Exception ignored){}}}
    }
}
