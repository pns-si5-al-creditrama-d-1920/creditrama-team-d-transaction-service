package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events;

public class TransactionClosedEvent extends Event {

    public TransactionClosedEvent(String uuid) {
        this.uuid = uuid;
    }
}
