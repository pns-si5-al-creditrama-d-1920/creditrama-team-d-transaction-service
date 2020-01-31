package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.BankAccount;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.TransactionState;
import lombok.ToString;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.time.LocalDateTime;

@ToString
public class CreateTransactionCommand {

    @TargetAggregateIdentifier
    private String uuid;

    private BankAccount sourceAccount;

    private BankAccount destAccount;

    private double amount;

    private LocalDateTime createdTransaction;

    private TransactionState transactionState;

    public CreateTransactionCommand(String uuid, BankAccount sourceAccount, BankAccount destAccount, double amount, LocalDateTime createdTransaction, TransactionState transactionState) {
        this.uuid = uuid;
        this.sourceAccount = sourceAccount;
        this.destAccount = destAccount;
        this.amount = amount;
        this.createdTransaction = createdTransaction;
        this.transactionState = transactionState;
    }

    public String getUuid() {
        return uuid;
    }

    public BankAccount getSource() {
        return sourceAccount;
    }

    public BankAccount getDest() {
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
}