package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.Transaction;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class ReverseTransferCommand {

    @TargetAggregateIdentifier
    private String uuid;

    private Transaction transaction;

    public ReverseTransferCommand(String uuid, Transaction transaction) {
        this.uuid = uuid;
        this.transaction = transaction;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
}
