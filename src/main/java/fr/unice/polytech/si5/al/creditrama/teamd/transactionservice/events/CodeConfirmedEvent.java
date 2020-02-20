package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.Transaction;
import lombok.Getter;

@Getter
public class CodeConfirmedEvent extends Event {
    private Transaction transaction;

    public CodeConfirmedEvent(String uuid, Transaction transaction) {
        this.uuid = uuid;
        this.transaction = transaction;
    }
}
