package io.github.mattlavallee.budgetbeaver.models.adapters;


import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import io.github.mattlavallee.budgetbeaver.R;
import io.github.mattlavallee.budgetbeaver.fragments.AccountFragment;
import io.github.mattlavallee.budgetbeaver.models.Transaction;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>{
    private ArrayList<Transaction> transactions;
    private AccountFragment adapterContainer;

    public TransactionAdapter(ArrayList<Transaction> allTransactions, AccountFragment container){
        transactions = allTransactions;
        adapterContainer = container;
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder{
        AccountFragment _container;
        CardView cardView;
        TextView transactionAmount;
        TextView transactionLocation;
        TextView transactionDescription;
        View overflow;

        TransactionViewHolder(View itemView, AccountFragment container){
            super(itemView);
            _container = container;
            cardView = (CardView) itemView.findViewById(R.id.transaction_card_view);

            transactionLocation = (TextView) itemView.findViewById(R.id.account_transaction_location);
            transactionAmount = (TextView) itemView.findViewById(R.id.account_transaction_amount);
            transactionDescription = (TextView) itemView.findViewById(R.id.account_transaction_description);

            overflow = itemView.findViewById(R.id.transaction_overflow);
        }
    }

    @Override
    public TransactionAdapter.TransactionViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.transaction_row, viewGroup, false);
        TransactionViewHolder viewHolder = new TransactionViewHolder(view, adapterContainer);
        return viewHolder;
    }

    public void updateData(ArrayList<Transaction> data){
        transactions.clear();
        transactions = data;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(TransactionViewHolder viewHolder, int position){
        Transaction transaction = transactions.get(position);
        viewHolder.transactionLocation.setText(transaction.getLocation());
        viewHolder.transactionDescription.setText(transaction.getDescription());
        viewHolder.transactionAmount.setText(transaction.getFormattedAmount());

        viewHolder.overflow.setTag(transaction.getId());
    }

    @Override
    public int getItemCount(){
        return transactions.size();
    }
}
