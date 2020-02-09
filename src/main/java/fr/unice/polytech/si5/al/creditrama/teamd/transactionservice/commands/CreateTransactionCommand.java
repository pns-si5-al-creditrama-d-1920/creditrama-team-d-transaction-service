package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.TransactionState;
import lombok.ToString;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.time.LocalDateTime;

@ToString
public class CreateTransactionCommand {

    @TargetAggregateIdentifier
    private String uuid;

    private String sourceAccount;

    private String destAccount;

    private double amount;

    private LocalDateTime createdTransaction;

    private TransactionState transactionState;

    private short code;

    public CreateTransactionCommand(String uuid, String sourceAccount, String destAccount, double amount, LocalDateTime createdTransaction, TransactionState transactionState, short code) {
        this.uuid = uuid;
        this.sourceAccount = sourceAccount;
        this.destAccount = destAccount;
        this.amount = amount;
        this.createdTransaction = createdTransaction;
        this.transactionState = transactionState;
        this.code = code;
    }

    public String getUuid() {
        return uuid;
    }

    public String getSourceAccount() {
        return sourceAccount;
    }

    public String getDest() {
        return destAccount;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getCreatedTransaction() {
        return createdTransaction;
    }

    public TransactionState getTransactionState() {
        return transactionState;
    }

    public short getCode() {
        return code;
    }

    public void setCode(short code) {
        this.code = code;
    }
}