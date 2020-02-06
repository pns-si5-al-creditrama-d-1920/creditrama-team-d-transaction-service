package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.Transaction;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class CancelTransactionStorageCommand {

    @TargetAggregateIdentifier
    private String uuid;
    private Transaction transaction;

    public CancelTransactionStorageCommand(String uuid, Transaction transaction) {
        this.transaction = transaction;
        this.uuid = uuid;
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
}
