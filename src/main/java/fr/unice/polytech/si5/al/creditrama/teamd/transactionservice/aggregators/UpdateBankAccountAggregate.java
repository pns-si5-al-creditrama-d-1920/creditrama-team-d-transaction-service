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
public class UpdateBankAccountAggregate {

    @AggregateIdentifier
    private String uuid;

    private Transaction transaction;

    public UpdateBankAccountAggregate() {
    }

    @CommandHandler
    public UpdateBankAccountAggregate(UpdateBankAccountCommand updateBankAccountCommand, BankAccountClient bankAccountClient) {
        System.out.println("Dans @CommandHandler UpdateBankAccountAggregate " + updateBankAccountCommand.toString());
        Transaction transaction = updateBankAccountCommand.getTransaction();

        try {
            bankAccountClient.updateBankAccount(transaction.getSource().getIban(), transaction.getSource().getBalance() - transaction.getAmount());
            bankAccountClient.updateBankAccount(transaction.getDest().getIban(), transaction.getDest().getBalance() + transaction.getAmount());

            apply(new UpdatedBankAccountEvent(updateBankAccountCommand.getUuid(), transaction));
        } catch (Exception e) {
            apply(new TransactionRejectedEvent(updateBankAccountCommand.getUuid(), transaction));
        }
    }

    @EventHandler
    protected void on(UpdatedBankAccountEvent updatedBankAccountEvent) {
        this.uuid = updatedBankAccountEvent.getUuid();
        this.transaction = updatedBankAccountEvent.getTransaction();
    }

    @EventHandler
    protected void on(TransactionRejectedEvent transactionRejectedEvent) {
        this.uuid = transactionRejectedEvent.getUuid();
        this.transaction = transactionRejectedEvent.getTransaction();
    }
}
