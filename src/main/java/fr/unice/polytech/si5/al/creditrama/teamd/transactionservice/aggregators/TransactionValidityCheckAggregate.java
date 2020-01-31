package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.aggregators;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands.TransactionValidityCheckCommand;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events.TransactionValidityCheckedEvent;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events.TransactionRejectedEvent;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.BankAccount;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.Transaction;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class TransactionValidityCheckAggregate {

    @AggregateIdentifier
    private String uuid;

    private Transaction transaction;

    public TransactionValidityCheckAggregate() {
    }

    @CommandHandler
    public TransactionValidityCheckAggregate(TransactionValidityCheckCommand transactionValidityCheckCommand) {
        System.out.println("Dans @CommandHandler TransactionValidityCheckAggregate " + transactionValidityCheckCommand.toString());
        Transaction transaction = transactionValidityCheckCommand.getTransaction();

        // check that source account has enough money for the transaction
        BankAccount bankAccountSrc = transaction.getSource();
        BankAccount bankAccountDest = transaction.getDest();

        if (bankAccountSrc == null || bankAccountDest == null) {
            apply(new TransactionRejectedEvent(transactionValidityCheckCommand.getUuid(), transaction));
        } else if (bankAccountSrc.getBalance() < transaction.getAmount()) {
            apply(new TransactionRejectedEvent(transactionValidityCheckCommand.getUuid(), transaction));
        } else {
            apply(new TransactionValidityCheckedEvent(transactionValidityCheckCommand.getUuid(), transaction));
        }
    }

    @EventSourcingHandler
    protected void on(TransactionValidityCheckedEvent transactionValidityCheckedEvent) {
        this.uuid = transactionValidityCheckedEvent.getUuid();
        this.transaction = transactionValidityCheckedEvent.getTransaction();
    }
}
