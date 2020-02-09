package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.Transaction;

public class TransferDoneEvent {
    private String bankUuid;
    private Transaction transaction;

    public TransferDoneEvent(String bankUuid, Transaction transaction) {
        this.bankUuid = bankUuid;
        this.transaction = transaction;
    }

    public String getBankUuid() {
        return bankUuid;
    }

    public void setBankUuid(String bankUuid) {
        this.bankUuid = bankUuid;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
}
