package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.Transaction;
import lombok.Getter;

@Getter
public class CodeConfirmedEvent extends Event {
    private Transaction transaction;
    private short code;

    public CodeConfirmedEvent(String uuid, Transaction transaction, short code) {
        this.uuid = uuid;
        this.transaction = transaction;
        this.code = code;
    }
}
