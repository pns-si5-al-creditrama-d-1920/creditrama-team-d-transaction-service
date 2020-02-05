package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.aggregators;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands.ReceiveTransactionCommand;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events.TransactionReceivedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class TransactionRequestAggregate {

    @AggregateIdentifier
    private String uuid;

    private String sourceIban;

    private String destIban;

    private Double amount;

    public TransactionRequestAggregate() {
        // Required by Axon to build a default Aggregate prior to Event Sourcing
        System.out.println("Dans TransactionRequestAggregate le constructor vide");
    }

    /* The @CommandHandler annotated functions are the place where you would put your decision-making/business logic.  */
    @CommandHandler
    public TransactionRequestAggregate(ReceiveTransactionCommand receiveTransactionCommand) {
        System.out.println("Dans @CommandHandler TransactionRequestAggregate " + receiveTransactionCommand.toString());
        apply(new TransactionReceivedEvent(receiveTransactionCommand.getUuid(), receiveTransactionCommand.getSourceIban(),
                receiveTransactionCommand.getDestIban(), receiveTransactionCommand.getAmount()));
    }

    /* As all the Event Sourcing Handlers combined will form the Aggregate, this is where all the state changes happen. */
    @EventHandler
    protected void on(TransactionReceivedEvent transactionReceivedEvent) {
        System.out.println("Dans @EventSourcingHandler on " + transactionReceivedEvent.toString());
        this.uuid = transactionReceivedEvent.getTransactionUuid();
        this.sourceIban = transactionReceivedEvent.getSourceIban();
        this.destIban = transactionReceivedEvent.getDestIban();
        this.amount = transactionReceivedEvent.getAmount();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSourceIban() {
        return sourceIban;
    }

    public void setSourceIban(String sourceIban) {
        this.sourceIban = sourceIban;
    }

    public String getDestIban() {
        return destIban;
    }

    public void setDestIban(String destIban) {
        this.destIban = destIban;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
