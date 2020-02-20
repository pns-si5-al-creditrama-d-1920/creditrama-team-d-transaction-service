package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.aggregators;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.client.BankAccountClient;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events.*;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands.*;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.*;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.repository.TransactionRepository;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.service.BankAccountService;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.service.ErrorService;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.service.NotificationService;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Random;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
@NoArgsConstructor
public class TransactionAggregate {
    private static final Logger log = LoggerFactory.getLogger(TransactionAggregate.class);

    @AggregateIdentifier
    private String uuid;
    private BankAccount sourceAccount;
    private BankAccount destAccount;
    private double amount;
    private LocalDateTime createdTransaction;
    private TransactionState transactionState;
    private short code;

    /***************** SAGA COMMAND HANDLERS *****************/

    @CommandHandler
    public TransactionAggregate(CreateTransactionCommand createTransactionCommand, BankAccountClient bankAccountClient, TransactionRepository transactionRepository) {
        BankAccount bankAccountSrc = bankAccountClient.getBankAccount(createTransactionCommand.getSourceAccount());
        BankAccount bankAccountDst = bankAccountClient.getBankAccount(createTransactionCommand.getDestAccount());

        Transaction transaction = new Transaction(createTransactionCommand.getUuid(), bankAccountSrc, bankAccountDst, createTransactionCommand.getAmount(),
                createTransactionCommand.getCreatedTransaction(), TransactionState.valueOf(createTransactionCommand.getTransactionState()), createTransactionCommand.getCode());

        if (bankAccountSrc != null && bankAccountDst != null) {
            transactionRepository.save(transaction);
            log.info("[ Transaction data ] " + createTransactionCommand.toString());
            apply(new TransactionCreatedEvent(transaction.getUuid(), bankAccountSrc, bankAccountDst, transaction.getAmount(),
                    transaction.getCreatedTransaction(), transaction.getTransactionState().getValue(), transaction.getCode()));
        } else {
            apply(new TransactionRejectedEvent(transaction.getUuid()));
        }
    }

    @CommandHandler
    public void checkTransaction(CheckTransactionCommand checkTransactionCommand) {
        Transaction transaction = buildTransaction();

        if (this.sourceAccount == null || this.destAccount == null || this.sourceAccount.getBalance() < this.amount) {
            apply(new TransactionRejectedEvent(transaction.getUuid()));
        } else {
            apply(new TransactionCheckedEvent(transaction.getUuid(), transaction.getSource().getIban(),
                    transaction.getDest().getIban(), transaction.getAmount()));
        }
    }

    @CommandHandler
    public void checkAmount(CheckTransactionAmountCommand checkTransactionAmountCommand, NotificationService notificationService) {
        Transaction transaction = buildTransaction();

        if (transaction.getAmount() >= 10) {
            notificationService.sendMail(transaction);
            apply(new VerificationCodeNeeded(transaction.getUuid()));
        } else {
            apply(new TransactionAmountCheckedEvent(transaction.getUuid(), transaction.getSource().getIban(),
                    transaction.getDest().getIban(), transaction.getAmount()));
        }
    }

    @CommandHandler
    public void makeTransfer(MakeTransferCommand makeTransferCommand, BankAccountService bankAccountService) {
        TransferDTO transferDTO = new TransferDTO(makeTransferCommand.getUuid(), makeTransferCommand.getSourceIban(), makeTransferCommand.getDestIban(),
                makeTransferCommand.getAmount());

        bankAccountService.makeTransfer(transferDTO);
        apply(new AwaitingTransferEvent(makeTransferCommand.getUuid()));
    }

    @CommandHandler
    public void storeTransaction(StoreTransactionCommand storeTransactionCommand, TransactionRepository transactionRepository) {
        Transaction transaction = buildTransaction();

        //Save transaction
        transactionRepository.save(transaction);

        //Error service used to toggle database writing errors
        ErrorService errorService = new ErrorService();

        if (errorService.isErrorsOn()) {
            //Errors are enabled
            Random random = new Random();
            int randomNumber = random.nextInt(100) + 1;
            if (randomNumber <= errorService.getErrorRate()) {
                //There was an error
                apply(new TransferCancelledEvent(transaction.getUuid(), transaction.getSource().getIban(),
                        transaction.getDest().getIban(), transaction.getAmount()));
            } else {
                apply(new TransactionStoredEvent(transaction.getUuid()));
            }
        } else {
            apply(new TransactionStoredEvent(transaction.getUuid()));
        }
    }

    @CommandHandler
    public void reverseTransfer(ReverseTransferCommand reverseTransferCommand, BankAccountService bankAccountService) {
        TransferDTO transferDTO = new TransferDTO(reverseTransferCommand.getUuid(), reverseTransferCommand.getDestIban(),
                reverseTransferCommand.getSourceIban(), reverseTransferCommand.getAmount());

        bankAccountService.reverseTransfer(transferDTO);
        apply(new AwaitingTransferEvent(reverseTransferCommand.getUuid()));
    }

    @CommandHandler
    public void confirmCode(ConfirmCodeCommand confirmCodeCommand) {
        Transaction transaction = buildTransaction();

        if (this.code != confirmCodeCommand.getCode()) {
            apply(new VerificationCodeNeeded(transaction.getUuid()));
        } else {
            apply(new CodeConfirmedEvent(transaction.getUuid(), transaction));
        }
    }

    @CommandHandler
    public void rejectTransaction(RejectTransactionCommand rejectTransactionCommand, TransactionRepository transactionRepository) {
        Transaction transaction = buildTransaction();
        transaction.setTransactionState(TransactionState.CANCEL);

        transactionRepository.save(transaction);
        apply(new TransactionClosedEvent(transaction.getUuid()));
    }

    @CommandHandler
    public void approveTransaction(ApproveTransactionCommand approveTransactionCommand, TransactionRepository transactionRepository) {
        Transaction transaction = buildTransaction();
        transaction.setTransactionState(TransactionState.ACCEPTED);

        transactionRepository.save(transaction);

        apply(new TransactionClosedEvent(transaction.getUuid()));
    }

    /***************** SAGA EVENT HANDLERS *****************/

    @SagaEventHandler(associationProperty = "uuid")
    protected void on(TransactionCreatedEvent transactionCreatedEvent) {
        this.uuid = transactionCreatedEvent.getUuid();
        this.sourceAccount = transactionCreatedEvent.getSourceAccount();
        this.destAccount = transactionCreatedEvent.getDestAccount();
        this.amount = transactionCreatedEvent.getAmount();
        this.createdTransaction = transactionCreatedEvent.getCreatedTransaction();
        this.transactionState = TransactionState.valueOf(transactionCreatedEvent.getTransactionState());
        this.code = transactionCreatedEvent.getCode();
    }

    @SagaEventHandler(associationProperty = "uuid")
    protected void on(TransactionRejectedEvent transactionRejectedEvent) {
        this.uuid = transactionRejectedEvent.getUuid();
        this.transactionState = TransactionState.CANCEL;
    }

    @SagaEventHandler(associationProperty = "uuid")
    protected void on(TransactionApprovedEvent transactionApprovedEvent) {
        this.transactionState = TransactionState.ACCEPTED;
    }

    private Transaction buildTransaction() {
        return new Transaction(this.uuid, this.sourceAccount, this.destAccount, this.amount, this.createdTransaction, this.transactionState, this.code);
    }
}
