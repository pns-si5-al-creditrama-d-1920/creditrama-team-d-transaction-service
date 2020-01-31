package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.Transaction;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.repository.TransactionRepository;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class CancelTransactionStorageCommand {

    @TargetAggregateIdentifier
    private String uuid;

    private Transaction transaction;

    private TransactionRepository transactionRepository;

    public CancelTransactionStorageCommand(String uuid, Transaction transaction, TransactionRepository transactionRepository) {
        this.transaction = transaction;
        this.uuid = uuid;
        this.transactionRepository = transactionRepository;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public TransactionRepository getTransactionRepository() {
        return transactionRepository;
    }

    public void setTransactionRepository(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }
}
