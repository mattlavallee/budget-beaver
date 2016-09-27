package io.github.mattlavallee.budgetbeaver;


import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class BudgetBeaverRecyclerHandler {
    public static RecyclerView createFragmentRecyclerView(int viewId, View parent, Context context){
        RecyclerView recyclerViewLayout = (RecyclerView)parent.findViewById(viewId);
        LinearLayoutManager llm = new LinearLayoutManager(context);
        recyclerViewLayout.setLayoutManager(llm);
        recyclerViewLayout.setHasFixedSize(true);

        return recyclerViewLayout;
    }
}
