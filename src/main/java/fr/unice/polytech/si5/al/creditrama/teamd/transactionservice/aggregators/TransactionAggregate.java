package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.aggregators;

import fr.unice.polytech.si5.al.creditrama.teamd.coreapi.commands.*;
import fr.unice.polytech.si5.al.creditrama.teamd.coreapi.events.*;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.client.BankAccountClient;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.BankAccount;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.Transaction;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.TransactionState;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.repository.TransactionRepository;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.service.ErrorService;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.spring.stereotype.Aggregate;

import java.time.LocalDateTime;
import java.util.Random;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class TransactionAggregate {

    @AggregateIdentifier
    private String uuid;

    private BankAccount sourceAccount;

    private BankAccount destAccount;

    private double amount;

    private LocalDateTime createdTransaction;

    private TransactionState transactionState;

    private short code;

    private String bankUuid;

    public TransactionAggregate() {
    }

    @CommandHandler
    public TransactionAggregate(CreateTransactionCommand createTransactionCommand, BankAccountClient bankAccountClient, TransactionRepository transactionRepository) {
        System.out.println("Dans @CommandHandler CreateTransactionCommand " + createTransactionCommand.toString());

        BankAccount bankAccountSrc = bankAccountClient.getBankAccount(createTransactionCommand.getSourceAccount());
        BankAccount bankAccountDst = bankAccountClient.getBankAccount(createTransactionCommand.getDest());

        Transaction transaction = new Transaction(createTransactionCommand.getUuid(), bankAccountSrc, bankAccountDst, createTransactionCommand.getAmount(),
                createTransactionCommand.getCreatedTransaction(), TransactionState.valueOf(createTransactionCommand.getTransactionState()), createTransactionCommand.getCode());

        if (bankAccountSrc != null && bankAccountDst != null) {
            transactionRepository.save(transaction);
            apply(new TransactionCreatedEvent(transaction.getUuid(), transaction.getSource().getIban(), transaction.getDest().getIban(), transaction.getAmount(),
                    transaction.getCreatedTransaction(), transaction.getTransactionState().getValue(), transaction.getCode()));
        } else {
            apply(new TransactionRejectedEvent(transaction.getUuid()));
        }
    }

    @SagaEventHandler(associationProperty = "uuid")
    protected void on(TransactionCreatedEvent transactionCreatedEvent, BankAccountClient bankAccountClient) {
        System.out.println("Dans @EventHandler on " + transactionCreatedEvent.toString());
        BankAccount bankAccountSrc = bankAccountClient.getBankAccount(transactionCreatedEvent.getSourceAccount());
        BankAccount bankAccountDst = bankAccountClient.getBankAccount(transactionCreatedEvent.getDestAccount());

        this.uuid = transactionCreatedEvent.getUuid();
        this.sourceAccount = bankAccountSrc;
        this.destAccount = bankAccountDst;
        this.amount = transactionCreatedEvent.getAmount();
        this.createdTransaction = transactionCreatedEvent.getCreatedTransaction();
        this.transactionState = TransactionState.valueOf(transactionCreatedEvent.getTransactionState());
        this.code = transactionCreatedEvent.getCode();
    }

    @CommandHandler
    public void checkTransaction(CheckTransactionCommand checkTransactionCommand) {
        System.out.println("Dans @CommandHandler CheckTransactionCommand " + checkTransactionCommand.toString());

        Transaction transaction = buildTransaction();

        if (this.sourceAccount == null || this.destAccount == null || this.sourceAccount.getBalance() < this.amount) {
            apply(new TransactionRejectedEvent(transaction.getUuid()));
        } else {
            apply(new TransactionCheckedEvent(transaction.getUuid(), transaction.getSource().getIban(),
                    transaction.getDest().getIban(), transaction.getAmount()));
        }
    }

    @SagaEventHandler(associationProperty = "uuid")
    protected void on(TransactionRejectedEvent transactionRejectedEvent) {
        System.out.println("Dans @EventHandler on " + transactionRejectedEvent.toString());
        this.transactionState = TransactionState.CANCEL;
    }

    @CommandHandler
    public void storeTransaction(StoreTransactionCommand storeTransactionCommand, TransactionRepository transactionRepository) {
        System.out.println("Dans @CommandHandler StoreTransactionCommand " + storeTransactionCommand.toString());
        Transaction transaction = buildTransaction();
        this.bankUuid = storeTransactionCommand.getBankUuid();

        //save transaction
        transactionRepository.save(transaction);

        //error service used to toggle database writing errors
        ErrorService errorService = new ErrorService();

        if (errorService.isErrorsOn()) {
            //there was an error
            Random random = new Random();
            int randomNumber = random.nextInt(100) + 1;
            if (randomNumber <= errorService.getErrorRate()) {
                apply(new TransferCancelledEvent(storeTransactionCommand.getBankUuid(), transaction.getUuid(), transaction.getSource().getIban(),
                        transaction.getDest().getIban(), transaction.getAmount()));
                //throw new DatabaseWriteException("Error due to our fixed rate");
            } else {
                if (transaction.getAmount() >= 10.0) {
                    apply(new VerificationCodeNeeded(transaction.getUuid()));
                } else {
                    apply(new TransactionStoredEvent(transaction.getUuid()));
                }
            }
        } else {
            if (transaction.getAmount() >= 10.0) {
                apply(new VerificationCodeNeeded(transaction.getUuid()));
            } else {
                apply(new TransactionApprovedEvent(transaction.getUuid()));
            }
        }
    }

    @SagaEventHandler(associationProperty = "uuid")
    protected void on(TransactionApprovedEvent transactionApprovedEvent) {
        System.out.println("Dans @EventHandler on " + transactionApprovedEvent.toString());
        this.transactionState = TransactionState.ACCEPTED;
    }

    @CommandHandler
    public void confirmCode(ConfirmCodeCommand confirmCodeCommand) {
        System.out.println("Dans @CommandHandler ConfirmCodeCommand " + confirmCodeCommand.toString());
        Transaction transaction = buildTransaction();

        if (this.code != confirmCodeCommand.getCode()) {
            //FIXME improve this
            apply(new TransferCancelledEvent(this.bankUuid, transaction.getUuid(), transaction.getSource().getIban(),
                    transaction.getDest().getIban(), transaction.getAmount()));
        } else {
            //FIXME à quoi ça sert ça ?
            // transaction.setCode((short) 0);
            // transactionRepository.save(transaction);
            apply(new TransactionStoredEvent(transaction.getUuid()));
        }
    }

    @CommandHandler
    public void rejectTransaction(RejectTransactionCommand rejectTransactionCommand, TransactionRepository transactionRepository) {
        System.out.println("Dans @CommandHandler RejectTransactionCommand " + rejectTransactionCommand.toString());

        Transaction transaction = buildTransaction();
        transaction.setTransactionState(TransactionState.CANCEL);

        transactionRepository.save(transaction);
    }

    @CommandHandler
    public void approveTransaction(ApproveTransactionCommand approveTransactionCommand, TransactionRepository transactionRepository) {
        System.out.println("Dans @CommandHandler ApproveTransactionCommand " + approveTransactionCommand.toString());

        Transaction transaction = buildTransaction();
        transaction.setTransactionState(TransactionState.ACCEPTED);

        transactionRepository.save(transaction);
    }

    private Transaction buildTransaction() {
        return new Transaction(this.uuid, this.sourceAccount, this.destAccount, this.amount, this.createdTransaction, this.transactionState, this.code);
    }
}
