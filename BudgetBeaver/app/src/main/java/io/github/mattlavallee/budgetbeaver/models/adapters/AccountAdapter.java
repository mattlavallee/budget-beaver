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

import java.util.ArrayList;

import io.github.mattlavallee.budgetbeaver.R;
import io.github.mattlavallee.budgetbeaver.data.DatabaseDispatcher;
import io.github.mattlavallee.budgetbeaver.fragments.OverviewFragment;
import io.github.mattlavallee.budgetbeaver.models.Account;
import io.github.mattlavallee.budgetbeaver.models.Transaction;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.AccountViewHolder> {
    private ArrayList<Account> accounts;
    private OverviewFragment adapterContainer;

    public AccountAdapter(ArrayList<Account> allAccounts, OverviewFragment container) {
        accounts = allAccounts;
        adapterContainer = container;
    }

    public static class AccountViewHolder extends RecyclerView.ViewHolder {
        OverviewFragment _container;
        CardView cardView;
        TextView accountName;
        TextView accountTotal;
        View overflow;

        AccountViewHolder(View itemView, OverviewFragment container) {
            super(itemView);
            _container = container;
            cardView = (CardView) itemView.findViewById(R.id.account_card_view);
            accountTotal = (TextView) itemView.findViewById(R.id.account_transaction_total);
            accountName = (TextView) itemView.findViewById(R.id.info_text);
            accountName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    _container.launchAccountFragment((Integer) view.getTag());
                }
            });
            overflow = itemView.findViewById(R.id.account_overflow);
            overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    final PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
                    final Menu menu = popupMenu.getMenu();
                    final int accountId = (Integer) view.getTag();

                    popupMenu.getMenuInflater().inflate(R.menu.account_popup_menu, menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int itemId = item.getItemId();
                            if (itemId == R.id.action_account_popup_edit) {
                                _container.editAccount(accountId);
                            } else if (itemId == R.id.action_account_popup_delete) {
                                _container.deleteAccount(accountId);
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
    public AccountAdapter.AccountViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.account_row, viewGroup, false);
        AccountViewHolder viewHolder = new AccountViewHolder(view, adapterContainer);
        return viewHolder;
    }

    public void updateData(ArrayList<Account> data){
        accounts.clear();
        accounts = data;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(AccountViewHolder viewHolder, int position) {
        Account account = accounts.get(position);
        viewHolder.accountName.setText(account.getName());
        viewHolder.accountName.setTag(account.getId());
        ArrayList<Transaction> transactions = new DatabaseDispatcher(adapterContainer.getContext())
                .Transactions.getTransactionsForAccount(account.getId());
        String totalForAccount = Transaction.getFormattedTotal(transactions);
        viewHolder.accountTotal.setText( totalForAccount );
        viewHolder.overflow.setTag(account.getId());
    }

    @Override
    public int getItemCount() {
        return accounts.size();
    }
}