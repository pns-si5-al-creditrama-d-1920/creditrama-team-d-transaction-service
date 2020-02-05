package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.aggregators;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.client.BankAccountClient;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands.CreateTransactionCommand;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events.TransactionCreatedEvent;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.BankAccount;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.TransactionState;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.time.LocalDateTime;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class CreateTransactionAggregate {

    @AggregateIdentifier
    private String uuid;

    private String source;

    private String dest;

    private double amount;

    private LocalDateTime createdTransaction;

    private TransactionState transactionState;

    private short code;

    public CreateTransactionAggregate() {
        // Required by Axon to build a default Aggregate prior to Event Sourcing
    }

    @CommandHandler
    public CreateTransactionAggregate(CreateTransactionCommand createTransactionCommand, BankAccountClient bankAccountClient) {
        System.out.println("Dans @CommandHandler TransactionAggregate " + createTransactionCommand.toString());

        BankAccount bankAccountSrc = bankAccountClient.getBankAccount(createTransactionCommand.getSource());
        BankAccount bankAccountDst = bankAccountClient.getBankAccount(createTransactionCommand.getDest());

        apply(new TransactionCreatedEvent(createTransactionCommand.getUuid(), bankAccountSrc,
                bankAccountDst, createTransactionCommand.getAmount(),
                createTransactionCommand.getCreatedTransaction(), createTransactionCommand.getTransactionState(), createTransactionCommand.getCode()));
    }

    @EventHandler
    protected void on(TransactionCreatedEvent transactionCreatedEvent) {
        System.out.println("Dans @EventSourcingHandler on " + transactionCreatedEvent.toString());
        this.uuid = transactionCreatedEvent.getUuid();
        this.source = transactionCreatedEvent.getSource().getIban();
        this.dest = transactionCreatedEvent.getDest().getIban();
        this.amount = transactionCreatedEvent.getAmount();
        this.createdTransaction = transactionCreatedEvent.getCreatedTransaction();
        this.transactionState = transactionCreatedEvent.getTransactionState();
        this.code = code;
    }
}

