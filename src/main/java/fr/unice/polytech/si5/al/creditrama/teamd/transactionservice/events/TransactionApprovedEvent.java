package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events;

public class TransactionApprovedEvent extends Event {

    public TransactionApprovedEvent(String uuid) {
        this.uuid = uuid;
    }
}
