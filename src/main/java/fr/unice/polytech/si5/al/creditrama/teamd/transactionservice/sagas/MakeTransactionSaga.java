package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.sagas;

import fr.unice.polytech.si5.al.creditrama.teamd.coreapi.commands.*;
import fr.unice.polytech.si5.al.creditrama.teamd.coreapi.events.*;
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

    public MakeTransactionSaga() {
    }

    //TODO create custom exceptions
    @StartSaga
    @SagaEventHandler(associationProperty = "uuid")
    public void handle(TransactionCreatedEvent transactionCreatedEvent) {
        System.out.println("Saga invoked CreateTransactionEvent" + transactionCreatedEvent.toString());
        commandGateway.send(new CheckTransactionCommand(transactionCreatedEvent.getUuid()));
    }

    @SagaEventHandler(associationProperty = "uuid")
    public void handle(TransactionCheckedEvent transactionCheckedEvent) {
        System.out.println("Saga invoked TransactionCheckedEvent");

        String bankUuid = UUID.randomUUID().toString();
        SagaLifecycle.associateWith("bankUuid", bankUuid);
        System.out.println("bankUUid : " + bankUuid);

        commandGateway.send(new MakeTransferCommand(bankUuid, transactionCheckedEvent.getUuid(),
                transactionCheckedEvent.getSourceIban(), transactionCheckedEvent.getDestIban(), transactionCheckedEvent.getAmount()));
    }

    @SagaEventHandler(associationProperty = "bankUuid")
    public void handle(TransferDoneEvent transferDoneEvent) {
        System.out.println("Saga invoked TransferDoneEvent");
        commandGateway.send(new StoreTransactionCommand(transferDoneEvent.getTransactionUuid(), transferDoneEvent.getBankUuid()));
    }

    @SagaEventHandler(associationProperty = "bankUuid")
    public void handle(TransferReversedEvent transferReversedEvent) {
        System.out.println("Saga invoked TransferReversedEvent");
        commandGateway.send(new RejectTransactionCommand(transferReversedEvent.getTransactionUuid()));
    }

    @SagaEventHandler(associationProperty = "bankUuid")
    public void handle(TransferCancelledEvent transferCancelledEvent) {
        System.out.println("Saga invoked TransferCancelledEvent");
        commandGateway.send(new ReverseTransferCommand(transferCancelledEvent.getBankUuid(), transferCancelledEvent.getUuid(),
                transferCancelledEvent.getSourceIban(), transferCancelledEvent.getDestIban(), transferCancelledEvent.getAmount()));
    }

    @SagaEventHandler(associationProperty = "uuid")
    public void handle(VerificationCodeNeeded verificationCodeNeeded) {
        System.out.println("Saga invoked VerificationCodeNeeded" + verificationCodeNeeded.toString());
        System.out.println("Waiting for verification code...");
    }

    @SagaEventHandler(associationProperty = "uuid")
    public void handle(TransactionStoredEvent transactionStoredEvent) {
        System.out.println("Saga invoked TransactionStoredEvent");
        commandGateway.send(new ApproveTransactionCommand(transactionStoredEvent.getUuid()));
    }

    @SagaEventHandler(associationProperty = "uuid")
    @EndSaga
    public void handle(TransactionRejectedEvent transactionRejectedEvent) {
        System.out.println("Saga invoked TransactionRejectedEvent");
    }

    @SagaEventHandler(associationProperty = "uuid")
    @EndSaga
    public void handle(TransactionApprovedEvent transactionApprovedEvent) {
        System.out.println("Saga invoked TransactionApprovedEvent");
    }
}
