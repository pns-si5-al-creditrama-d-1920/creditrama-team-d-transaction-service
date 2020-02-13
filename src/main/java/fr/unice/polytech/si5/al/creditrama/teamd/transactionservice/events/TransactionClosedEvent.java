package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events;

public class TransactionClosedEvent {
    private String uuid;

    public TransactionClosedEvent(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
