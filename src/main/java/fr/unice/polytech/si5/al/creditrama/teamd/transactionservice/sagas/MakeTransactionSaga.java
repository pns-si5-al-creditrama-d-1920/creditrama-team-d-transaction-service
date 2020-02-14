package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.sagas;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events.*;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands.*;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

@Saga
public class MakeTransactionSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    public MakeTransactionSaga() {
    }

    @StartSaga
    @SagaEventHandler(associationProperty = "uuid")
    public void handle(TransactionCreatedEvent transactionCreatedEvent) {
        System.out.println("Saga invoked CreateTransactionEvent " + transactionCreatedEvent.toString());
        commandGateway.send(new CheckTransactionCommand(transactionCreatedEvent.getUuid()));
    }

    @SagaEventHandler(associationProperty = "uuid")
    public void handle(TransactionCheckedEvent transactionCheckedEvent) {
        System.out.println("Saga invoked TransactionCheckedEvent");

        commandGateway.send(new MakeTransferCommand(transactionCheckedEvent.getUuid(),
                transactionCheckedEvent.getSourceIban(), transactionCheckedEvent.getDestIban(), transactionCheckedEvent.getAmount()));
    }

    @SagaEventHandler(associationProperty = "uuid")
    public void handle(TransferCancelledEvent transferCancelledEvent) {
        System.out.println("Saga invoked TransferCancelledEvent");
        commandGateway.send(new ReverseTransferCommand(transferCancelledEvent.getUuid(),
                transferCancelledEvent.getSourceIban(), transferCancelledEvent.getDestIban(), transferCancelledEvent.getAmount()));
    }

    @SagaEventHandler(associationProperty = "uuid")
    public void handle(AwaitingTransferEvent awaitingTransferEvent) {
        System.out.println("Saga invoked AwaitingTransferEvent" + awaitingTransferEvent.toString());
        System.out.println("Waiting for transfer to be processed...");
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
    public void handle(TransactionRejectedEvent transactionRejectedEvent) {
        commandGateway.send(new RejectTransactionCommand(transactionRejectedEvent.getUuid()));
        System.out.println("Transaction has been successfully rejected.");
    }

    @SagaEventHandler(associationProperty = "uuid")
    public void handle(TransactionApprovedEvent transactionApprovedEvent) {
        System.out.println("Transaction has been successfully approved.");
    }

    @SagaEventHandler(associationProperty = "uuid")
    @EndSaga
    public void handle(TransactionClosedEvent transactionClosedEvent) {
        System.out.println("Transaction is now finished.");
    }
}
