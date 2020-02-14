package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.BankAccount;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
public class TransactionCreatedEvent {
    private String uuid;
    private BankAccount sourceAccount;

    private BankAccount destAccount;

    private double amount;

    private LocalDateTime createdTransaction;

    private String transactionState;

    private short code;

    public TransactionCreatedEvent(String uuid, BankAccount sourceAccount, BankAccount destAccount, double amount, LocalDateTime createdTransaction, String transactionState, short code) {
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

    public BankAccount getSourceAccount() {
        return sourceAccount;
    }

    public BankAccount getDestAccount() {
        return destAccount;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getCreatedTransaction() {
        return createdTransaction;
    }

    public String getTransactionState() {
        return transactionState;
    }

    public short getCode() {
        return code;
    }
}
