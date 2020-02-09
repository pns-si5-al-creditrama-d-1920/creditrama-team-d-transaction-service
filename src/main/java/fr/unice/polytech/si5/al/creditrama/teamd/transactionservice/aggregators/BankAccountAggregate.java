package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.aggregators;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.client.BankAccountClient;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands.MakeTransferCommand;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands.ReverseTransferCommand;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events.TransactionRejectedEvent;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events.TransferDoneEvent;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events.TransferReversedEvent;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.Transaction;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.spring.stereotype.Aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class BankAccountAggregate {

    @AggregateIdentifier
    private String bankUuid;
    private Transaction transaction;

    public BankAccountAggregate() {
    }

    @CommandHandler
    public void makeTransfer(MakeTransferCommand makeTransferCommand, BankAccountClient bankAccountClient) {
        System.out.println("Dans @CommandHandler MakeTransferCommand " + makeTransferCommand.toString());
        Transaction transaction = makeTransferCommand.getTransaction();
        try {
            bankAccountClient.updateBankAccount(transaction.getSource().getIban(), transaction.getSource().getBalance() - transaction.getAmount());
            bankAccountClient.updateBankAccount(transaction.getDest().getIban(), transaction.getDest().getBalance() + transaction.getAmount());

            apply(new TransferDoneEvent(transaction.getUuid(), transaction));
        } catch (Exception e) {
            //FIXME reverse transfer ?
            apply(new TransactionRejectedEvent(transaction.getUuid(), transaction));
        }
    }

    @SagaEventHandler(associationProperty = "bankUuid")
    protected void on(TransferDoneEvent transferDoneEvent) {
        System.out.println("Dans @EventHandler on " + transferDoneEvent.toString());
        this.transaction = transferDoneEvent.getTransaction();
        this.bankUuid = transferDoneEvent.getBankUuid();
    }

    @CommandHandler
    public void reverseTransfer(ReverseTransferCommand reverseTransferCommand, BankAccountClient bankAccountClient) {
        System.out.println("Dans @CommandHandler ReverseTransferCommand " + reverseTransferCommand.toString());
        Transaction transaction = reverseTransferCommand.getTransaction();

        try {
            bankAccountClient.updateBankAccount(transaction.getSource().getIban(), transaction.getSource().getBalance() + transaction.getAmount());
            bankAccountClient.updateBankAccount(transaction.getDest().getIban(), transaction.getDest().getBalance() - transaction.getAmount());

            apply(new TransferReversedEvent(transaction.getUuid(), transaction));
        } catch (Exception e) {
            apply(new TransactionRejectedEvent(transaction.getUuid(), transaction));
        }
    }

    //TODO vraiment besoin de ça ?
    @SagaEventHandler(associationProperty = "bankUuid")
    protected void on(TransferReversedEvent transferReversedEvent) {
        System.out.println("Dans @EventHandler on " + transferReversedEvent.toString());
        this.transaction = transferReversedEvent.getTransaction();
        this.bankUuid = transferReversedEvent.getBankUuid();
    }

    @SagaEventHandler(associationProperty = "bankUuid")
    protected void on(TransactionRejectedEvent transactionRejectedEvent) {
        System.out.println("Dans @EventHandler on " + transactionRejectedEvent.toString());
        this.bankUuid = transactionRejectedEvent.getUuid();
        this.transaction = transactionRejectedEvent.getTransaction();
    }
}
