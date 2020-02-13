package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class RejectTransactionCommand {

    @TargetAggregateIdentifier
    private String uuid;

    public RejectTransactionCommand(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
