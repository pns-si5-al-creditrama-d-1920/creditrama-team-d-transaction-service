package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.time.LocalDateTime;

@AllArgsConstructor
@ToString
@Getter
public class CreateTransactionCommand {

    @TargetAggregateIdentifier
    private String uuid;
    private String sourceAccount;
    private String destAccount;
    private double amount;
    private LocalDateTime createdTransaction;
    private String transactionState;
    private short code;
}