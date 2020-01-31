package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.Transaction;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.repository.TransactionRepository;
import lombok.ToString;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@ToString
public class StoreTransactionCommand {
    @TargetAggregateIdentifier
    private final String uuid;

    private Transaction transaction;

    private TransactionRepository transactionRepository;

    public StoreTransactionCommand(String uuid, Transaction transaction, TransactionRepository transactionRepository) {
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

    public TransactionRepository getTransactionRepository() {
        return transactionRepository;
    }

    public void setTransactionRepository(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }
}
