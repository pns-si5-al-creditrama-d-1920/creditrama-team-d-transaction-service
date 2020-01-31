package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.BankAccount;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.Transaction;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.TransactionState;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
public class TransactionCreatedEvent {

    private String uuid;

    private BankAccount source;

    private BankAccount dest;

    private double amount;

    private LocalDateTime createdTransaction;

    private TransactionState transactionState;

    public TransactionCreatedEvent(String uuid, BankAccount source, BankAccount dest, double amount, LocalDateTime createdTransaction, TransactionState transactionState) {
        this.uuid = uuid;
        this.source = source;
        this.dest = dest;
        this.amount = amount;
        this.createdTransaction = createdTransaction;
        this.transactionState = transactionState;
        System.out.println("l'event est créé : ");
    }

    public String getUuid() {
        return uuid;
    }

    public BankAccount getSource() {
        return source;
    }

    public BankAccount getDest() {
        return dest;
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

    public Transaction getTransaction() {
        return new Transaction(this.uuid, this.source, this.dest, this.amount, this.createdTransaction, this.transactionState);
    }
}
