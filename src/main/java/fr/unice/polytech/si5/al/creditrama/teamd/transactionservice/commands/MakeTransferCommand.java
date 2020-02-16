package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@AllArgsConstructor
@ToString
@Getter
public class MakeTransferCommand {

    @TargetAggregateIdentifier
    private String uuid;
    private String sourceIban;
    private String destIban;
    private double amount;
}
