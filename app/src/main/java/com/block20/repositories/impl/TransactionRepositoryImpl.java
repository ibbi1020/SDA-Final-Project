package com.block20.repositories.impl;

import com.block20.models.Transaction;
import com.block20.repositories.TransactionRepository;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepositoryImpl implements TransactionRepository {
    private List<Transaction> transactionTable = new ArrayList<>();

    @Override
    public void save(Transaction transaction) {
        transactionTable.add(transaction);
        System.out.println("Finance: Recorded payment of $" + transaction.getAmount() + " for " + transaction.getType());
    }

    @Override
    public List<Transaction> findAll() {
        return new ArrayList<>(transactionTable);
    }
}