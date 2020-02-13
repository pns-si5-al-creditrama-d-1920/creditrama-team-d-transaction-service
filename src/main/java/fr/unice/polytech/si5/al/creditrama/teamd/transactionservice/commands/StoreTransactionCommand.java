package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands;

import lombok.ToString;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@ToString
public class StoreTransactionCommand {
    @TargetAggregateIdentifier
    private final String uuid;

    public StoreTransactionCommand(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }
}
