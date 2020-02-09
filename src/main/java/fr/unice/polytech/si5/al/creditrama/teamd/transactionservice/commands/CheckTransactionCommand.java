package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands;

import lombok.ToString;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@ToString
public class CheckTransactionCommand {

    @TargetAggregateIdentifier
    private String uuid;

    public CheckTransactionCommand(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
