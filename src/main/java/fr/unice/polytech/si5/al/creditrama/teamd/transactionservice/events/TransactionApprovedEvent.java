package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events;

public class TransactionApprovedEvent {
    private String uuid;

    public TransactionApprovedEvent(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
