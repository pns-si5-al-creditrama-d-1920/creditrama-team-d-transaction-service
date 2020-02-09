package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands;

import lombok.ToString;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@ToString
public class StoreTransactionCommand {
    @TargetAggregateIdentifier
    private final String uuid;

    private String bankUuid;

    public StoreTransactionCommand(String uuid, String bankUuid) {
        this.uuid = uuid;
        this.bankUuid = bankUuid;
    }

    public String getBankUuid() {
        return bankUuid;
    }

    public void setBankUuid(String bankUuid) {
        this.bankUuid = bankUuid;
    }

    public String getUuid() {
        return uuid;
    }
}
