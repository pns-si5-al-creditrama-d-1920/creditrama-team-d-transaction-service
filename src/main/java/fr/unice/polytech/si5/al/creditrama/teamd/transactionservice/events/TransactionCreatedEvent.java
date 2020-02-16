package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.BankAccount;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TransactionCreatedEvent extends Event {
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
}
