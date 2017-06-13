package io.github.mattlavallee.budgetbeaver.models.adapters;

import android.content.Context;
import android.graphics.Color;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import static io.github.mattlavallee.budgetbeaver.R.attr.colorPrimary;

public class BudgetBeaverPreferencesCategory  extends PreferenceCategory {
    public BudgetBeaverPreferencesCategory (Context context) {
        super(context);
    }

    public BudgetBeaverPreferencesCategory (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BudgetBeaverPreferencesCategory (Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        TextView titleView = (TextView) view.findViewById(android.R.id.title);
        titleView.setTextColor(Color.parseColor("#339FE8"));
    }
}