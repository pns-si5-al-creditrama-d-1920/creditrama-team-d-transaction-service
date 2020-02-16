package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events;

import lombok.Getter;

@Getter
public class TransferCancelledEvent extends Event {
    private String sourceIban;
    private String destIban;
    private double amount;

    public TransferCancelledEvent(String uuid, String sourceIban, String destIban, double amount) {
        this.uuid = uuid;
        this.sourceIban = sourceIban;
        this.destIban = destIban;
        this.amount = amount;
    }
}
