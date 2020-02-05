package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.aggregators;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands.ApproveTransactionCommand;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands.RejectTransactionCommand;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.Transaction;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.TransactionState;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.repository.TransactionRepository;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
public class UpdateTransactionAggregate {

    @AggregateIdentifier
    private String uuid;
    private Transaction transaction;

    public UpdateTransactionAggregate() {
    }

    @CommandHandler
    public UpdateTransactionAggregate(ApproveTransactionCommand cancelTransactionStorageCommand, TransactionRepository transactionRepository) {
        System.out.println("Dans @CommandHandler ApproveTransactionCommand " + cancelTransactionStorageCommand.toString());
        Transaction transaction = cancelTransactionStorageCommand.getTransaction();
        transaction.setTransactionState(TransactionState.ACCEPTED);

        transactionRepository.save(transaction);
    }

    @CommandHandler
    public UpdateTransactionAggregate(RejectTransactionCommand cancelTransactionStorageCommand, TransactionRepository transactionRepository) {
        System.out.println("Dans @CommandHandler RejectTransactionCommand " + cancelTransactionStorageCommand.toString());
        Transaction transaction = cancelTransactionStorageCommand.getTransaction();
        transaction.setTransactionState(TransactionState.CANCEL);

        transactionRepository.save(transaction);
    }
}
