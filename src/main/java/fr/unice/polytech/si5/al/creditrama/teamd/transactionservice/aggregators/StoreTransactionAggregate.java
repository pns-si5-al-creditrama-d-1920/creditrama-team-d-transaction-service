package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.aggregators;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands.StoreTransactionCommand;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events.TransactionApprovedEvent;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events.TransactionStorageCancelledEvent;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events.VerificationCodeNeeded;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.exception.DatabaseWriteException;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.Transaction;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.repository.TransactionRepository;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.EventHandler;
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
        this.errorsOn = true;
        this.errorRate = 5;
    }

    //MEMO : when interfaces are passed onto the constructor annotated with @CommandHandler they are automatically autowired
    @CommandHandler
    public StoreTransactionAggregate(StoreTransactionCommand storeTransactionCommand, TransactionRepository transactionRepository) throws DatabaseWriteException {
        System.out.println("Dans @CommandHandler StoreTransactionCommand " + storeTransactionCommand.toString());
        Transaction transaction = storeTransactionCommand.getTransaction();
        //transaction.setTransactionState(TransactionState.ACCEPTED);
        this.errorsOn = false;

        //save transaction
        transactionRepository.save(transaction);

        //FIXME 2 else needed ?
        //there was an error
        if (this.errorsOn) {
            Random random = new Random();
            // int randomNumber = random.nextInt(100) + 1;
           /* if (randomNumber <= this.errorRate) {
                // throw new DatabaseWriteException("Error due to our fixed rate");
                apply(new TransactionStorageCancelledEvent(storeTransactionCommand.getUuid(), transaction));
            } else {*/
            apply(new TransactionStorageCancelledEvent(storeTransactionCommand.getUuid(), transaction));
            //           }
        } else {
            if (storeTransactionCommand.getTransaction().getAmount() >= 10.0) {
                apply(new VerificationCodeNeeded(storeTransactionCommand.getUuid(), transaction));
            } else {
                apply(new TransactionApprovedEvent(storeTransactionCommand.getUuid(), transaction));
            }
        }
    }

    @EventHandler
    protected void on(TransactionApprovedEvent transactionApprovedEvent) {
        System.out.println("Dans @EventSourcingHandler on " + transactionApprovedEvent.toString());
        this.transaction = transactionApprovedEvent.getTransaction();
        this.uuid = transactionApprovedEvent.getUuid();
    }

    @EventHandler
    protected void on(VerificationCodeNeeded verificationCodeNeeded) {
        System.out.println("Dans @EventSourcingHandler on " + verificationCodeNeeded.toString());
        this.transaction = verificationCodeNeeded.getTransaction();
        this.uuid = verificationCodeNeeded.getUuid();
    }

    @EventHandler
    protected void on(TransactionStorageCancelledEvent transactionStorageCancelledEvent) {
        System.out.println("Dans @EventSourcingHandler on " + transactionStorageCancelledEvent.toString());
        this.transaction = transactionStorageCancelledEvent.getTransaction();
        this.uuid = transactionStorageCancelledEvent.getUuid();
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}