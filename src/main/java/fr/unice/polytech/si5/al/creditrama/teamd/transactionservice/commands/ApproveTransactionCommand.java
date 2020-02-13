package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class ApproveTransactionCommand {

    @TargetAggregateIdentifier
    private String uuid;

    public ApproveTransactionCommand(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
