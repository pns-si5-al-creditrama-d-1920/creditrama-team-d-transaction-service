package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events;

import lombok.Getter;

@Getter
public class TransactionStoredEvent extends Event {

    public TransactionStoredEvent(String uuid) {
        this.uuid = uuid;
    }
}
