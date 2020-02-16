package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events;

import lombok.Getter;

public class AwaitingTransferEvent extends Event {

    public AwaitingTransferEvent(String uuid) {
        this.uuid = uuid;
    }
}
