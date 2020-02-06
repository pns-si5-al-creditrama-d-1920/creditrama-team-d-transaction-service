package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.sagas;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands.*;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events.*;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.Transaction;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.TransactionState;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.repository.TransactionRepository;
import org.axonframework.eventhandling.DisallowReplay;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
//TODO investiguer sur les SAGA stores
// NOTE : si 2 fois le même aggregate identifier à la même value : error, explications :
// https://medium.com/@gushakov/discovering-cqrs-and-event-sourcing-with-axon-framework-afb3782e39c7

@Saga
public class TransactionManagementSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    @Autowired
    private TransactionRepository transactionRepository;

    public TransactionManagementSaga() {
    }

    //TODO create custom exceptions
    @StartSaga
    @SagaEventHandler(associationProperty = "uuid")
    public void handle(CreateTransactionEvent createTransactionEvent) {
        System.out.println("Saga invoked TransactionCreatedEvent" + createTransactionEvent.getUuid());
        Transaction transaction = createTransactionEvent.getTransaction();

        commandGateway.send(new TransactionValidityCheckCommand(transaction.getUuid(), transaction));
    }

    @SagaEventHandler(associationProperty = "uuid")
    public void handle(TransactionValidityCheckedEvent transactionValidityCheckedEvent) {
        System.out.println("Saga invoked TransactionValidityCheckedEvent");
        Transaction transaction = transactionValidityCheckedEvent.getTransaction();

        commandGateway.send(new UpdateBankAccountCommand(transaction.getUuid(), transaction));
    }

    @SagaEventHandler(associationProperty = "uuid")
    public void handle(UpdatedBankAccountEvent updatedBankAccountEvent) {
        System.out.println("Saga invoked UpdatedBankAccountEvent");
        Transaction transaction = updatedBankAccountEvent.getTransaction();

        commandGateway.send(new StoreTransactionCommand(transaction.getUuid(), transaction));
    }

    @SagaEventHandler(associationProperty = "uuid")
    public void handle(VerificationCodeNeeded verificationCodeNeeded) {
        System.out.println("Saga invoked VerificationCodeNeeded" + verificationCodeNeeded.toString());
        System.out.println("Waiting for verification code...");
    }

    @SagaEventHandler(associationProperty = "uuid")
    public void handle(CodeConfirmedEvent codeConfirmedEvent) {
        System.out.println("Saga invoked CodeConfirmedEvent");

        commandGateway.send(new ApproveTransactionCommand(codeConfirmedEvent.getUuid(), codeConfirmedEvent.getTransaction()));
    }

    @SagaEventHandler(associationProperty = "uuid")
    public void handle(TransactionStorageCancelledEvent transactionStorageCancelledEvent) {
        System.out.println("Saga invoked TransactionStorageCancelledEvent");

        commandGateway.send(new CancelTransactionStorageCommand(transactionStorageCancelledEvent.getUuid(), transactionStorageCancelledEvent.getTransaction()));
    }

    @SagaEventHandler(associationProperty = "uuid")
    @EndSaga
    public void handle(TransactionApprovedEvent transactionApprovedEvent) {
        System.out.println("Saga invoked TransactionApprovedEvent");

        Transaction transaction = transactionApprovedEvent.getTransaction();
        transaction.setTransactionState(TransactionState.ACCEPTED);

        transactionRepository.save(transaction);

        //commandGateway.send(new ApproveTransactionCommand(uuid, transactionApprovedEvent.getTransaction()));
    }

    @SagaEventHandler(associationProperty = "uuid")
    @EndSaga
    public void handle(TransactionRejectedEvent transactionRejectedEvent) {
        System.out.println("Saga invoked TransactionRejectedEvent");

        Transaction transaction = transactionRejectedEvent.getTransaction();
        transaction.setTransactionState(TransactionState.CANCEL);

        transactionRepository.save(transaction);

        //commandGateway.send(new RejectTransactionCommand(uuid, transactionRejectedEvent.getTransaction()));
    }
}
