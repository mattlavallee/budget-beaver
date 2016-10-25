package io.github.mattlavallee.budgetbeaver.models.adapters;


import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

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
            overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    final PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                    final Menu menu = popupMenu.getMenu();
                    final int transactionId = (Integer) view.getTag();

                    popupMenu.getMenuInflater().inflate(R.menu.transaction_popup_menu, menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int itemId = item.getItemId();
                            if (itemId == R.id.action_transaction_popup_edit) {
                                _container.editTransaction(transactionId);
                            } else if (itemId == R.id.action_transaction_popup_delete) {
                                _container.deleteTransaction(transactionId);
                            }
                            return false;
                        }
                    });

                    popupMenu.show();
                }
            });
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
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy");
        String strDate = formatter.format(transaction.getTransactionDate());
        viewHolder.transactionDescription.setText(strDate + ": " + transaction.getDescription());
        viewHolder.transactionAmount.setText(transaction.getFormattedAmount());

        viewHolder.overflow.setTag(transaction.getId());
    }

    @Override
    public int getItemCount(){
        return transactions.size();
    }
}
