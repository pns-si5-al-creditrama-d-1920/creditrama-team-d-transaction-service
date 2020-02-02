package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.aggregators;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands.CancelTransactionStorageCommand;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events.TransactionApprovedEvent;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events.TransactionStorageCancelledEvent;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.exception.DatabaseWriteException;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.Transaction;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.TransactionState;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
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
    public CancelTransactionStorageAggregate(CancelTransactionStorageCommand cancelTransactionStorageCommand) {
        System.out.println("Dans @CommandHandler CancelTransactionStorageCommand " + cancelTransactionStorageCommand.toString());
        Transaction transaction = cancelTransactionStorageCommand.getTransaction();
        transaction.setTransactionState(TransactionState.CANCEL);

        //save transaction
        cancelTransactionStorageCommand.getTransactionRepository().save(transaction);

        apply(new TransactionStorageCancelledEvent(cancelTransactionStorageCommand.getUuid(), transaction));
    }

    @EventSourcingHandler
    protected void on(TransactionStorageCancelledEvent transactionStorageCancelledEvent) {
        System.out.println("Dans @EventSourcingHandler on " + transactionStorageCancelledEvent.toString());
        this.transaction = transactionStorageCancelledEvent.getTransaction();
        this.uuid = transactionStorageCancelledEvent.getUuid();
    }
}
