package io.github.mattlavallee.budgetbeaver.models.adapters;


import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import io.github.mattlavallee.budgetbeaver.R;
import io.github.mattlavallee.budgetbeaver.models.Account;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder>{
    private ArrayList<Account> accounts;

    public AccountAdapter(ArrayList<Account> allAccounts){
        accounts = allAccounts;
    }

    public static class AccountViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView accountName;

        AccountViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            accountName = (TextView) itemView.findViewById(R.id.info_text);
        }
    }

    @Override
    public AccountAdapter.AccountViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.account_row, viewGroup, false);
        AccountViewHolder viewHolder = new AccountViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AccountViewHolder viewHolder, int position) {
        Account account = accounts.get(position);
        viewHolder.accountName.setText( account.getName() );
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }
}