package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events;

import lombok.Getter;

@Getter
public class TransactionRejectedEvent extends Event {

    public TransactionRejectedEvent(String uuid) {
        this.uuid = uuid;
    }
}
