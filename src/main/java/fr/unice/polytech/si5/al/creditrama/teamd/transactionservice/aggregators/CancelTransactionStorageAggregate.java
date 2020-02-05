package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.aggregators;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.client.BankAccountClient;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands.CancelTransactionStorageCommand;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events.TransactionRejectedEvent;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.Transaction;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class CancelTransactionStorageAggregate {

    @AggregateIdentifier
    private String uuid;
    private Transaction transaction;

    public CancelTransactionStorageAggregate() {
        // Required by Axon to build a default Aggregate prior to Event Sourcing
    }

    @CommandHandler
    public CancelTransactionStorageAggregate(CancelTransactionStorageCommand cancelTransactionStorageCommand, BankAccountClient bankAccountClient) {
        System.out.println("Dans @CommandHandler CancelTransactionStorageCommand " + cancelTransactionStorageCommand.toString());
        Transaction t = cancelTransactionStorageCommand.getTransaction();

        //compensiate accounts
        bankAccountClient.updateBankAccount(t.getSource().getIban(), t.getSource().getBalance() + t.getAmount());
        bankAccountClient.updateBankAccount(t.getDest().getIban(), t.getDest().getBalance() - t.getAmount());

        apply(new TransactionRejectedEvent(cancelTransactionStorageCommand.getUuid(), t));
    }

    @EventHandler
    protected void on(TransactionRejectedEvent transactionRejectedEvent) {
        System.out.println("Dans @EventSourcingHandler on " + transactionRejectedEvent.toString());
        this.transaction = transactionRejectedEvent.getTransaction();
        this.uuid = transactionRejectedEvent.getUuid();
    }
}
