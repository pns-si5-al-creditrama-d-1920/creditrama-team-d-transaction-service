package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events;

import lombok.ToString;

@ToString
public class TransactionStoredEvent {
    private String uuid;

    public TransactionStoredEvent(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
