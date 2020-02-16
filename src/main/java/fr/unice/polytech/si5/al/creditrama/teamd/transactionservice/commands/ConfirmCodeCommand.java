package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@AllArgsConstructor
@ToString
@Getter
public class ConfirmCodeCommand {

    @TargetAggregateIdentifier
    private String uuid;
    private short code;
}
