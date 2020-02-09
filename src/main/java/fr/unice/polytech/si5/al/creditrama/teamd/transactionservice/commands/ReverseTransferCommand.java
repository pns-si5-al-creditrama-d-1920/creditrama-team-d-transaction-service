package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.Transaction;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class ReverseTransferCommand {

    @TargetAggregateIdentifier
    private String bankUuid;
    private String uuid;
    private Transaction transaction;

    public ReverseTransferCommand(String bankUuid, String uuid, Transaction transaction) {
        this.bankUuid = bankUuid;
        this.uuid = uuid;
        this.transaction = transaction;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getBankUuid() {
        return bankUuid;
    }

    public void setBankUuid(String bankUuid) {
        this.bankUuid = bankUuid;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
}
