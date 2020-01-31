package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands;

import lombok.ToString;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@ToString
public class ReceiveTransactionCommand {

    @TargetAggregateIdentifier
    private final String uuid;

    private String sourceIban;

    private String destIban;

    private Double amount;

    public ReceiveTransactionCommand(String uuid, String sourceIban, String destIban, Double amount) {
        System.out.println("uuid : " + uuid);
        this.uuid = uuid;
        this.sourceIban = sourceIban;
        this.destIban = destIban;
        this.amount = amount;
    }

    public String getUuid() {
        return uuid;
    }

    public String getSourceIban() {
        return sourceIban;
    }

    public void setSourceIban(String sourceIban) {
        this.sourceIban = sourceIban;
    }

    public String getDestIban() {
        return destIban;
    }

    public void setDestIban(String destIban) {
        this.destIban = destIban;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
