package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.aggregators;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands.CreateTransactionCommand;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events.TransactionCreatedEvent;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.BankAccount;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.TransactionState;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.time.LocalDateTime;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class TransactionAggregate {

    @AggregateIdentifier
    private String uuid;

    private BankAccount source;

    private BankAccount dest;

    private double amount;

    private LocalDateTime createdTransaction;

    private TransactionState transactionState;

    public TransactionAggregate() {
        // Required by Axon to build a default Aggregate prior to Event Sourcing
    }

    @CommandHandler
    public TransactionAggregate(CreateTransactionCommand createTransactionCommand) {
        System.out.println("Dans @CommandHandler TransactionAggregate " + createTransactionCommand.toString());
        apply(new TransactionCreatedEvent(createTransactionCommand.getUuid(), createTransactionCommand.getSource(),
                createTransactionCommand.getDest(), createTransactionCommand.getAmount(),
                createTransactionCommand.getCreatedTransaction(), createTransactionCommand.getTransactionState()));
    }

    @EventSourcingHandler
    protected void on(TransactionCreatedEvent transactionCreatedEvent) {
        System.out.println("Dans @EventSourcingHandler on " + transactionCreatedEvent.toString());
        this.uuid = transactionCreatedEvent.getUuid();
        this.source = transactionCreatedEvent.getSource();
        this.dest = transactionCreatedEvent.getDest();
        this.amount = transactionCreatedEvent.getAmount();
        this.createdTransaction = transactionCreatedEvent.getCreatedTransaction();
        this.transactionState = transactionCreatedEvent.getTransactionState();
    }
}

