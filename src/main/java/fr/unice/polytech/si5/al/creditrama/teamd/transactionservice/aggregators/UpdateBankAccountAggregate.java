package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.aggregators;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.client.BankAccountClient;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands.UpdateBankAccountCommand;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events.TransactionRejectedEvent;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events.UpdatedBankAccountEvent;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.Transaction;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
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
        double srcNewBalance = transaction.getSource().getBalance() - transaction.getAmount();
        double destNewBalance = transaction.getDest().getBalance() + transaction.getAmount();

        System.out.println("bank client " + bankAccountClient);

        bankAccountClient.updateBanAccount(transaction.getSource().getIban(), transaction.getSource().getBalance() - transaction.getAmount());
        bankAccountClient.updateBanAccount(transaction.getDest().getIban(), transaction.getDest().getBalance() + transaction.getAmount());

        if (bankAccountClient.getBankAccount(transaction.getSource().getIban()).getBalance() == srcNewBalance &&
                bankAccountClient.getBankAccount(transaction.getDest().getIban()).getBalance() == destNewBalance) {
            apply(new UpdatedBankAccountEvent(updateBankAccountCommand.getUuid(), transaction));
        } else {
            apply(new TransactionRejectedEvent(updateBankAccountCommand.getUuid(), transaction));
        }
    }

    @EventSourcingHandler
    protected void on(UpdatedBankAccountEvent updatedBankAccountEvent) {
        this.uuid = updatedBankAccountEvent.getUuid();
        this.transaction = updatedBankAccountEvent.getTransaction();
    }

    @EventSourcingHandler
    protected void on(TransactionRejectedEvent transactionRejectedEvent) {
        this.uuid = transactionRejectedEvent.getUuid();
        this.transaction = transactionRejectedEvent.getTransaction();
    }
}
