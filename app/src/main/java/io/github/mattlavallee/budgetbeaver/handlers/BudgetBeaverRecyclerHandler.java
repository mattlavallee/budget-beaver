package io.github.mattlavallee.budgetbeaver.handlers;


import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class BudgetBeaverRecyclerHandler {
    public static RecyclerView createFragmentRecyclerView(int viewId, View parent, Context context){
        RecyclerView recyclerViewLayout = (RecyclerView)parent.findViewById(viewId);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        recyclerViewLayout.setLayoutManager(llm);
        recyclerViewLayout.setHasFixedSize(true);

        return recyclerViewLayout;
    }

    public static void updateViewVisibility(View parent, int recyclerId, int textViewId, int contentSize) {
        RecyclerView recyclerViewLayout = (RecyclerView)parent.findViewById(recyclerId);
        TextView emptyMessage = (TextView)parent.findViewById(textViewId);

        if(contentSize == 0) {
            recyclerViewLayout.setVisibility(View.GONE);
            emptyMessage.setVisibility(View.VISIBLE);
        } else {
            recyclerViewLayout.setVisibility(View.VISIBLE);
            emptyMessage.setVisibility(View.GONE);
        }
    }
}
