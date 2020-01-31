package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events;

import lombok.ToString;

@ToString
public class TransactionReceivedEvent {
    private String transactionUuid;

    private String sourceIban;

    private String destIban;

    private Double amount;

    public TransactionReceivedEvent() {
    }

    public TransactionReceivedEvent(String transactionUuid, String sourceIban, String destIban, Double amount) {
        this.transactionUuid = transactionUuid;
        this.sourceIban = sourceIban;
        this.destIban = destIban;
        this.amount = amount;
    }

    public String getTransactionUuid() {
        return transactionUuid;
    }

    public void setTransactionUuid(String transactionUuid) {
        this.transactionUuid = transactionUuid;
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
