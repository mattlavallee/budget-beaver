package io.github.mattlavallee.budgetbeaver.handlers;

import android.graphics.Color;
import com.google.android.material.snackbar.Snackbar;
import android.view.View;
import android.widget.TextView;

public class SnackBarHandler {
    private static void setViewColor( Snackbar snack, int viewId, int color){
        TextView view = (TextView)snack.getView().findViewById(viewId);
        view.setTextColor(color);
    }

    public static Snackbar generateActionableSnackBar( View view, String snackText){
        Snackbar snack = Snackbar.make(view, snackText, Snackbar.LENGTH_LONG);

        setViewColor( snack, com.google.android.material.R.id.snackbar_text, Color.WHITE);
        setViewColor( snack, com.google.android.material.R.id.snackbar_action, Color.CYAN);

        return snack;
    }

    public static Snackbar generateSnackBar(View view, String snackText){
        Snackbar snack = Snackbar.make(view, snackText, Snackbar.LENGTH_SHORT);

        setViewColor( snack, com.google.android.material.R.id.snackbar_text, Color.CYAN);

        return snack;
    }
}
