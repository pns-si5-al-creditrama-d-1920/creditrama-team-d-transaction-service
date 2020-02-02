package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.sagas;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.client.BankAccountClient;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands.*;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events.*;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.exception.DatabaseWriteException;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.Transaction;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.TransactionState;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.repository.TransactionRepository;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

//TODO investiguer sur les SAGA stores
// NOTE : si 2 fois le même aggregate identifier à la même value : error, explications :
// https://medium.com/@gushakov/discovering-cqrs-and-event-sourcing-with-axon-framework-afb3782e39c7
@Saga
public class TransactionManagementSaga {
    //TODO add confirmation code to SAGA

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private BankAccountClient bankAccountClient;

    @Autowired
    private TransactionRepository transactionRepository;

    public TransactionManagementSaga() {
    }

    //TODO create custom exceptions
    @StartSaga
    @SagaEventHandler(associationProperty = "transactionUuid")
    public void handle(TransactionReceivedEvent transactionReceivedEvent) {
        System.out.println("Saga invoked TransactionReceivedEvent " + transactionReceivedEvent);

        //associate Saga
        String uuid = UUID.randomUUID().toString();
        SagaLifecycle.associateWith("uuid", uuid);

        //send next event
        commandGateway.send(new CreateTransactionCommand(uuid, transactionReceivedEvent.getSourceIban(), transactionReceivedEvent.getDestIban(),
                transactionReceivedEvent.getAmount(), LocalDateTime.now(), TransactionState.PENDING, (short) (new Random().nextInt(9000) + 1000)));
    }

    @SagaEventHandler(associationProperty = "uuid")
    public void handle(TransactionCreatedEvent transactionCreatedEvent) {
        System.out.println("Saga invoked TransactionCreatedEvent");
        System.out.println("transaction id" + transactionCreatedEvent.getUuid());

        Transaction transaction = transactionCreatedEvent.getTransaction();

        //associate Saga
        String uuid = UUID.randomUUID().toString();
        transaction.setUuid(uuid);
        SagaLifecycle.associateWith("uuid", uuid);

        //send next event
        commandGateway.send(new TransactionValidityCheckCommand(uuid, transaction));
    }

    @SagaEventHandler(associationProperty = "uuid")
    public void handle(TransactionValidityCheckedEvent transactionValidityCheckedEvent) {
        System.out.println("Saga invoked TransactionValidityCheckedEvent");

        Transaction transaction = transactionValidityCheckedEvent.getTransaction();

        //associate Saga
        String uuid = UUID.randomUUID().toString();
        transaction.setUuid(uuid);
        SagaLifecycle.associateWith("uuid", uuid);

        //send next event
        commandGateway.send(new UpdateBankAccountCommand(uuid, transaction));
    }

    @SagaEventHandler(associationProperty = "uuid")
    public void handle(UpdatedBankAccountEvent updatedBankAccountEvent) throws DatabaseWriteException {
        System.out.println("Saga invoked UpdatedBankAccountEvent");

        Transaction transaction = updatedBankAccountEvent.getTransaction();

        //associate Saga
        String uuid = UUID.randomUUID().toString();
        transaction.setUuid(uuid);
        SagaLifecycle.associateWith("uuid", uuid);

        //send next event
        commandGateway.send(new StoreTransactionCommand(uuid, transaction));
    }

    @SagaEventHandler(associationProperty = "uuid")
    @EndSaga
    public void handle(TransactionStorageCancelledEvent transactionStorageCancelledEvent) {
        System.out.println("Saga invoked TransactionStorageCancelledEvent");

        Transaction transaction = transactionStorageCancelledEvent.getTransaction();
        transaction.setTransactionState(TransactionState.CANCEL);

        bankAccountClient.updateBanAccount(transaction.getSource().getIban(), transaction.getSource().getBalance() + transaction.getAmount());
        bankAccountClient.updateBanAccount(transaction.getDest().getIban(), transaction.getDest().getBalance() - transaction.getAmount());

        transactionRepository.save(transaction);

        System.out.println("Transaction not stored " + transaction.getUuid());
        //send next event
    }

    @SagaEventHandler(associationProperty = "uuid")
    @EndSaga
    public void handle(TransactionApprovedEvent transactionApprovedEvent) {
        System.out.println("Saga invoked TransactionApprovedEvent");

        Transaction transaction = transactionApprovedEvent.getTransaction();
        transaction.setTransactionState(TransactionState.ACCEPTED);

        transactionRepository.save(transaction);

        System.out.println("Transaction approved ! " + transactionApprovedEvent.getUuid());
    }

    @SagaEventHandler(associationProperty = "uuid")
    @EndSaga
    public void handle(TransactionRejectedEvent transactionRejectedEvent) {
        System.out.println("Saga invoked TransactionRejectedEvent");

        Transaction transaction = transactionRejectedEvent.getTransaction();
        transaction.setTransactionState(TransactionState.CANCEL);

        transactionRepository.save(transaction);

        System.out.println("Transaction rejected ! " + transaction.getUuid());
    }
}