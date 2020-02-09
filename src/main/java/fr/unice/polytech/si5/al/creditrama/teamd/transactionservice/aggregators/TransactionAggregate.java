package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.aggregators;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.client.BankAccountClient;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands.*;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events.*;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.BankAccount;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.Transaction;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.TransactionState;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.repository.TransactionRepository;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.service.NotificationService;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Random;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class TransactionAggregate {


    @Autowired
    NotificationService notificationService;
    @AggregateIdentifier
    private String uuid;

    private BankAccount sourceAccount;

    private BankAccount destAccount;

    private double amount;

    private LocalDateTime createdTransaction;

    private TransactionState transactionState;

    private short code;

    private boolean errorOn;
    private int errorRate;

    public TransactionAggregate() {
        this.errorOn = true;
        this.errorRate = 5;
    }

    @CommandHandler
    public TransactionAggregate(CreateTransactionCommand createTransactionCommand, BankAccountClient bankAccountClient, TransactionRepository transactionRepository) {
        System.out.println("Dans @CommandHandler CreateTransactionCommand " + createTransactionCommand.toString());

        BankAccount bankAccountSrc = bankAccountClient.getBankAccount(createTransactionCommand.getSource());
        BankAccount bankAccountDst = bankAccountClient.getBankAccount(createTransactionCommand.getDest());

        Transaction transaction = new Transaction(createTransactionCommand.getUuid(), bankAccountSrc, bankAccountDst, createTransactionCommand.getAmount(),
                createTransactionCommand.getCreatedTransaction(), createTransactionCommand.getTransactionState(), createTransactionCommand.getCode());

        if (bankAccountSrc != null && bankAccountDst != null) {
            transactionRepository.save(transaction);
            apply(new CreateTransactionEvent(transaction.getUuid(), transaction));
        } else {
            apply(new TransactionRejectedEvent(transaction.getUuid(), transaction));
        }
    }

    @SagaEventHandler(associationProperty = "uuid")
    protected void on(CreateTransactionEvent createTransactionEvent) {
        System.out.println("Dans @EventHandler on " + createTransactionEvent.toString());
        Transaction transaction = createTransactionEvent.getTransaction();
        this.uuid = transaction.getUuid();
        this.sourceAccount = transaction.getSource();
        this.destAccount = transaction.getDest();
        this.amount = transaction.getAmount();
        this.createdTransaction = transaction.getCreatedTransaction();
        this.transactionState = transaction.getTransactionState();
        this.code = transaction.getCode();

    }

    @CommandHandler
    public void checkTransaction(CheckTransactionCommand checkTransactionCommand) {
        System.out.println("Dans @CommandHandler CheckTransactionCommand " + checkTransactionCommand.toString());
        Transaction transaction = buildTransaction();

        if (this.sourceAccount == null || this.destAccount == null || this.sourceAccount.getBalance() < this.amount) {
            apply(new TransactionRejectedEvent(transaction.getUuid(), transaction));
        } else {
            apply(new TransactionCheckedEvent(transaction.getUuid(), transaction));
        }
    }

    @SagaEventHandler(associationProperty = "uuid")
    protected void on(TransactionRejectedEvent transactionRejectedEvent) {
        System.out.println("Dans @EventHandler on " + transactionRejectedEvent.toString());
        this.uuid = transactionRejectedEvent.getTransaction().getUuid();
        this.transactionState = TransactionState.CANCEL;
    }

    @CommandHandler
    public void storeTransaction(StoreTransactionCommand storeTransactionCommand, TransactionRepository transactionRepository) {
        System.out.println("Dans @CommandHandler StoreTransactionCommand " + storeTransactionCommand.toString());
        Transaction transaction = buildTransaction();

        //save transaction
        transactionRepository.save(transaction);

        if (this.errorOn) {
            //there was an error
            Random random = new Random();
            int randomNumber = random.nextInt(100) + 1;
            if (randomNumber <= this.errorRate) {
                apply(new ReverseTransferCommand(storeTransactionCommand.getUuid(), transaction));
                //throw new DatabaseWriteException("Error due to our fixed rate");
            }
        }

        if (transaction.getAmount() >= 10.0) {
            apply(new VerificationCodeNeeded(transaction.getUuid()));
        } else {
            apply(new TransactionApprovedEvent(transaction.getUuid(), transaction));
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
            apply(new TransactionRejectedEvent(transaction.getUuid(), transaction));
        } else {
            //FIXME à quoi ça sert ça ?
            // transaction.setCode((short) 0);
            // transactionRepository.save(transaction);
            apply(new TransactionApprovedEvent(transaction.getUuid(), transaction));
        }
    }

    private Transaction buildTransaction() {
        return new Transaction(this.uuid, this.sourceAccount, this.destAccount, this.amount, this.createdTransaction, this.transactionState, this.code);
    }
}
