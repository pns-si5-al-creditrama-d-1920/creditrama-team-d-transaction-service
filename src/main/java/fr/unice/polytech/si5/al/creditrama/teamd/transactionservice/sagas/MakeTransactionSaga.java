package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.sagas;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands.*;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events.*;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.Transaction;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.TransactionState;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.repository.TransactionRepository;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Saga
public class MakeTransactionSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private TransactionRepository transactionRepository;

    public MakeTransactionSaga() {
    }

    //TODO create custom exceptions
    @StartSaga
    @SagaEventHandler(associationProperty = "uuid")
    public void handle(CreateTransactionEvent createTransactionEvent) {
        System.out.println("Saga invoked CreateTransactionEvent" + createTransactionEvent.toString());
        commandGateway.send(new CheckTransactionCommand(createTransactionEvent.getTransaction().getUuid()));
    }

    @SagaEventHandler(associationProperty = "uuid")
    public void handle(TransactionCheckedEvent transactionCheckedEvent) {
        System.out.println("Saga invoked TransactionCheckedEvent");
        String bankUuid = UUID.randomUUID().toString();
        SagaLifecycle.associateWith("bankUuid", bankUuid);
        commandGateway.send(new MakeTransferCommand(bankUuid, transactionCheckedEvent.getUuid(), transactionCheckedEvent.getTransaction()));
    }

    @SagaEventHandler(associationProperty = "uuid")
    public void handle(VerificationCodeNeeded verificationCodeNeeded) {
        System.out.println("Saga invoked VerificationCodeNeeded" + verificationCodeNeeded.toString());
        System.out.println("Waiting for verification code...");
    }

    @SagaEventHandler(associationProperty = "bankUuid")
    public void handle(TransferDoneEvent transferDoneEvent) {
        System.out.println("Saga invoked TransferDoneEvent");
        commandGateway.send(new StoreTransactionCommand(transferDoneEvent.getTransaction().getUuid(), transferDoneEvent.getBankUuid()));
    }

    @SagaEventHandler(associationProperty = "bankUuid")
    public void handle(TransferReversedEvent transferReversedEvent) {
        System.out.println("Saga invoked TransferReversedEvent");
        commandGateway.send(new RejectTransactionCommand(transferReversedEvent.getTransaction().getUuid(), transferReversedEvent.getTransaction()));

    }

    @SagaEventHandler(associationProperty = "bankUuid")
    public void handle(TransferCancelledEvent transferCancelledEvent) {
        System.out.println("Saga invoked TransferCancelledEvent");
        commandGateway.send(new ReverseTransferCommand(transferCancelledEvent.getBankUuid(), transferCancelledEvent.getTransaction().getUuid(), transferCancelledEvent.getTransaction()));
    }

    @SagaEventHandler(associationProperty = "uuid")
    @EndSaga
    public void handle(TransactionRejectedEvent transactionRejectedEvent) {
        System.out.println("Saga invoked TransactionRejectedEvent");

        Transaction transaction = transactionRejectedEvent.getTransaction();
        transaction.setTransactionState(TransactionState.CANCEL);

        transactionRepository.save(transaction);
    }

    @SagaEventHandler(associationProperty = "uuid")
    @EndSaga
    public void handle(TransactionApprovedEvent transactionApprovedEvent) {
        System.out.println("Saga invoked TransactionApprovedEvent");

        Transaction transaction = transactionApprovedEvent.getTransaction();
        transaction.setTransactionState(TransactionState.ACCEPTED);

        transactionRepository.save(transaction);
    }
}
