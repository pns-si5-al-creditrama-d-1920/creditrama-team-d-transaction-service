package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.sagas;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events.*;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands.*;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.service.NotificationService;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Saga
public class TransactionManagementSaga {
    private Logger logger = LoggerFactory.getLogger(TransactionManagementSaga.class);
    @Autowired
    private transient CommandGateway commandGateway;

    public TransactionManagementSaga() {
    }

    @StartSaga
    @SagaEventHandler(associationProperty = "uuid")
    public void handle(TransactionCreatedEvent transactionCreatedEvent) {
        logger.info(transactionCreatedEvent.toString());
        commandGateway.send(new CheckTransactionCommand(transactionCreatedEvent.getUuid()));
    }

    @SagaEventHandler(associationProperty = "uuid")
    public void handle(TransactionCheckedEvent transactionCheckedEvent) {
        logger.info(transactionCheckedEvent.toString());
        commandGateway.send(new MakeTransferCommand(transactionCheckedEvent.getUuid(),
                transactionCheckedEvent.getSourceIban(), transactionCheckedEvent.getDestIban(), transactionCheckedEvent.getAmount()));
    }

    @SagaEventHandler(associationProperty = "uuid")
    public void handle(TransferCancelledEvent transferCancelledEvent) {
        logger.info(transferCancelledEvent.toString());
        commandGateway.send(new ReverseTransferCommand(transferCancelledEvent.getUuid(),
                transferCancelledEvent.getSourceIban(), transferCancelledEvent.getDestIban(), transferCancelledEvent.getAmount()));
    }

    @SagaEventHandler(associationProperty = "uuid")
    public void handle(AwaitingTransferEvent awaitingTransferEvent) {
        logger.info(awaitingTransferEvent.toString());
        System.out.println("Waiting for transfer to be processed...");
    }

    @SagaEventHandler(associationProperty = "uuid")
    public void handle(VerificationCodeNeeded verificationCodeNeeded) {
        logger.info(verificationCodeNeeded.toString());
        System.out.println("Waiting for verification code...");
    }

    @SagaEventHandler(associationProperty = "uuid")
    public void handle(TransactionStoredEvent transactionStoredEvent) {
        logger.info(transactionStoredEvent.toString());
        commandGateway.send(new ApproveTransactionCommand(transactionStoredEvent.getUuid()));
    }

    @SagaEventHandler(associationProperty = "uuid")
    public void handle(TransactionRejectedEvent transactionRejectedEvent) {
        logger.info(transactionRejectedEvent.toString());
        commandGateway.send(new RejectTransactionCommand(transactionRejectedEvent.getUuid()));
    }

    @SagaEventHandler(associationProperty = "uuid")
    @EndSaga
    public void handle(TransactionApprovedEvent transactionApprovedEvent) {
        logger.info(transactionApprovedEvent.toString());
    }

    @SagaEventHandler(associationProperty = "uuid")
    @EndSaga
    public void handle(TransactionClosedEvent transactionClosedEvent) {
        logger.info("[ Transaction : " + transactionClosedEvent.getUuid() + " ] is done.");
    }
}
