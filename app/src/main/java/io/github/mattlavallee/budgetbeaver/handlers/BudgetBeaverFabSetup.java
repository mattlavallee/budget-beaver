package io.github.mattlavallee.budgetbeaver.handlers;

import androidx.fragment.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.github.clans.fab.FloatingActionMenu;

public class BudgetBeaverFabSetup {
    public static void addFabToView(FragmentActivity activity, LayoutInflater inflater,
                                    int parentContainer, int fabLayoutId, int fabId){
        RelativeLayout parentView = (RelativeLayout)activity.findViewById(parentContainer);
        //remove existing FAB
        removeExistingFab(parentView);

        FloatingActionMenu childLayout = (FloatingActionMenu)inflater.inflate(fabLayoutId,
                (ViewGroup)activity.findViewById(fabId));
        childLayout.setClosedOnTouchOutside(true);
        parentView.addView(childLayout);

        //add new fab
        setFabLayoutParameters(childLayout);

        childLayout.setClosedOnTouchOutside(true);
    }

    public static void removeExistingFab(RelativeLayout parent){
        int childCount = parent.getChildCount();
        View lastChild = parent.getChildAt(childCount - 1);
        if(lastChild instanceof FloatingActionMenu) {
            parent.removeViewAt(childCount - 1);
        }
    }
    private static void setFabLayoutParameters(View layout){
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)layout.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
    }
}
