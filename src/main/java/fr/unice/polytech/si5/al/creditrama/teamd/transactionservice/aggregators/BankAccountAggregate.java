package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.aggregators;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.client.BankAccountClient;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands.UpdateBankAccountCommand;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events.TransactionRejectedEvent;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events.UpdatedBankAccountEvent;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.Transaction;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class BankAccountAggregate {

    @AggregateIdentifier
    private String uuid;

    private Transaction transaction;

    public BankAccountAggregate() {
    }

}
