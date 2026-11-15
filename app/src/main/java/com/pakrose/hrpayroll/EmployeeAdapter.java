package com.pakrose.hrpayroll;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class EmployeeAdapter extends BaseAdapter {

    private final Context context;
    private final List<Employee> employees;

    public EmployeeAdapter(
            Context context, List<Employee> employees) {
        this.context = context;
        this.employees = employees;
    }

    @Override
    public int getCount() {
        return employees.size();
    }

    @Override
    public Employee getItem(int position) {
        return employees.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(
            int position, View convertView, ViewGroup parent) {

        Employee employee = getItem(position);

        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(18, 18, 18, 18);

        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.WHITE);
        background.setCornerRadius(22);
        background.setStroke(1, Color.rgb(225, 230, 238));
        card.setBackground(background);

        TextView initials = new TextView(context);
        String name = employee.getName() == null
                ? "" : employee.getName().trim();

        String letters = "";
        for (String part : name.split("\\s+")) {
            if (!part.isEmpty()) {
                letters += part.substring(0, 1).toUpperCase();
            }
            if (letters.length() == 2) break;
        }

        initials.setText(letters);
        initials.setTextColor(Color.WHITE);
        initials.setTextSize(18);
        initials.setTypeface(null, Typeface.BOLD);
        initials.setGravity(Gravity.CENTER);

        GradientDrawable circle = new GradientDrawable();
        circle.setShape(GradientDrawable.OVAL);
        circle.setColor(Color.rgb(15, 39, 71));
        initials.setBackground(circle);

        LinearLayout.LayoutParams avatarParams =
                new LinearLayout.LayoutParams(52, 52);
        avatarParams.setMargins(0, 0, 14, 0);
        card.addView(initials, avatarParams);

        LinearLayout details = new LinearLayout(context);
        details.setOrientation(LinearLayout.VERTICAL);

        TextView nameView = new TextView(context);
        nameView.setText(employee.getName());
        nameView.setTextColor(Color.rgb(25, 38, 56));
        nameView.setTextSize(16);
        nameView.setTypeface(null, Typeface.BOLD);

        TextView designation = new TextView(context);
        designation.setText(employee.getDesignation());
        designation.setTextColor(Color.GRAY);
        designation.setTextSize(13);

        TextView salary = new TextView(context);
        salary.setText(String.format(
                Locale.getDefault(),
                "Rs. %,.0f",
                employee.getBasicSalary()));
        salary.setTextColor(Color.rgb(15, 100, 75));
        salary.setTextSize(13);
        salary.setTypeface(null, Typeface.BOLD);

        details.addView(nameView);
        details.addView(designation);
        details.addView(salary);

        card.addView(details,
                new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1));

        return card;
    }
}
