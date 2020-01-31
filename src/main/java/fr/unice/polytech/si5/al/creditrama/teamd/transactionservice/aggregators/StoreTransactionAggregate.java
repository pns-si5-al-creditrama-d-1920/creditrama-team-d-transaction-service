package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.aggregators;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands.StoreTransactionCommand;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events.TransactionApprovedEvent;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events.TransactionStorageCancelledEvent;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.exception.DatabaseWriteException;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.Transaction;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.TransactionState;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.Random;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class StoreTransactionAggregate {

    @AggregateIdentifier
    private String uuid;

    private Transaction transaction;

    private boolean errorsOn;
    private int errorRate;

    public StoreTransactionAggregate() {
        // Required by Axon to build a default Aggregate prior to Event Sourcing
        this.errorsOn = false;
        this.errorRate = 5;
    }

    @CommandHandler
    public StoreTransactionAggregate(StoreTransactionCommand storeTransactionCommand) throws DatabaseWriteException {
        System.out.println("Dans @CommandHandler ApproveTransactionAggregate " + storeTransactionCommand.toString());
        Transaction transaction = storeTransactionCommand.getTransaction();
        transaction.setTransactionState(TransactionState.ACCEPTED);

        //save transaction
        storeTransactionCommand.getTransactionRepository().save(transaction);

        //FIXME 2 else needed ?
        //there was an error
        if (this.errorsOn) {
            Random random = new Random();
            int randomNumber = random.nextInt(100) + 1;
            if (randomNumber <= this.errorRate) {
                // throw new DatabaseWriteException("Error due to our fixed rate");
                apply(new TransactionStorageCancelledEvent(storeTransactionCommand.getUuid(), transaction));
            } else {
                apply(new TransactionApprovedEvent(storeTransactionCommand.getUuid(), transaction));
            }
        } else {
            apply(new TransactionApprovedEvent(storeTransactionCommand.getUuid(), transaction));
        }
    }

    @EventSourcingHandler
    protected void on(TransactionApprovedEvent transactionApprovedEvent) {
        System.out.println("Dans @EventSourcingHandler on " + transactionApprovedEvent.toString());
        this.transaction = transactionApprovedEvent.getTransaction();
        this.uuid = transactionApprovedEvent.getUuid();
        this.errorsOn = false;
        this.errorRate = 5;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
}