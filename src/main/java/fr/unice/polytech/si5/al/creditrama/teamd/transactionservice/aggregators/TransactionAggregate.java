package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.aggregators;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.client.BankAccountClient;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands.*;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events.*;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.BankAccount;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.Transaction;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.TransactionState;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.repository.TransactionRepository;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.spring.stereotype.Aggregate;

import java.time.LocalDateTime;
import java.util.Optional;
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

    private boolean errorOn;
    private int errorRate;

    public TransactionAggregate() {
        // Required by Axon to build a default Aggregate prior to Event Sourcing
        System.out.println("Dans TransactionRequestAggregate le constructor vide");
        this.errorOn = true;
        this.errorRate = 5;
    }

    /* The @CommandHandler annotated functions are the place where you would put your decision-making/business logic.  */
    @CommandHandler
    public TransactionAggregate(CreateTransactionCommand createTransactionCommand, BankAccountClient bankAccountClient) {
        System.out.println("Dans @CommandHandler CreateTransactionCommand " + createTransactionCommand.toString());

        BankAccount bankAccountSrc = bankAccountClient.getBankAccount(createTransactionCommand.getSource());
        BankAccount bankAccountDst = bankAccountClient.getBankAccount(createTransactionCommand.getDest());

        apply(new CreateTransactionEvent(createTransactionCommand.getUuid(), bankAccountSrc,
                bankAccountDst, createTransactionCommand.getAmount(), createTransactionCommand.getCreatedTransaction(),
                createTransactionCommand.getTransactionState(), createTransactionCommand.getCode()));
    }

    /* As all the Event Sourcing Handlers combined will form the Aggregate, this is where all the state changes happen. */
    @SagaEventHandler(associationProperty = "uuid")
    protected void on(CreateTransactionEvent createTransactionEvent) {
        System.out.println("Dans @EventHandler on " + createTransactionEvent.toString());
        this.uuid = createTransactionEvent.getUuid();
        this.sourceAccount = createTransactionEvent.getSource();
        this.destAccount = createTransactionEvent.getDest();
        this.amount = createTransactionEvent.getAmount();
        this.createdTransaction = createTransactionEvent.getCreatedTransaction();
        this.transactionState = createTransactionEvent.getTransactionState();
        this.code = createTransactionEvent.getCode();
    }

    @CommandHandler
    public void on(TransactionValidityCheckCommand transactionValidityCheckCommand) {
        System.out.println("Dans @CommandHandler TransactionValidityCheckCommand " + transactionValidityCheckCommand.toString());
        Transaction transaction = transactionValidityCheckCommand.getTransaction();

        // check that source account has enough money for the transaction
        BankAccount bankAccountSrc = transaction.getSource();
        BankAccount bankAccountDst = transaction.getDest();

        if (bankAccountSrc == null || bankAccountDst == null) {
            apply(new TransactionRejectedEvent(transactionValidityCheckCommand.getUuid(), transactionValidityCheckCommand.getTransaction()));
        } else if (bankAccountSrc.getBalance() < transaction.getAmount()) {
            apply(new TransactionRejectedEvent(transactionValidityCheckCommand.getUuid(), transactionValidityCheckCommand.getTransaction()));
        } else {
            apply(new TransactionValidityCheckedEvent(transactionValidityCheckCommand.getUuid(), transaction));
        }
    }

    @SagaEventHandler(associationProperty = "uuid")
    protected void on(TransactionRejectedEvent transactionRejectedEvent) {
        System.out.println("Dans @EventHandler on " + transactionRejectedEvent.toString());
        this.uuid = transactionRejectedEvent.getUuid();
        this.transactionState = TransactionState.CANCEL;
    }

    @SagaEventHandler(associationProperty = "uuid")
    protected void on(TransactionValidityCheckedEvent transactionValidityCheckedEvent) {
        System.out.println("Dans @EventHandler on " + transactionValidityCheckedEvent.toString());
        this.uuid = transactionValidityCheckedEvent.getUuid();
        //FIXME really need that ? what to de here ?
        this.transactionState = TransactionState.ACCEPTED;
    }

    @CommandHandler
    public void updateBankAccount(UpdateBankAccountCommand updateBankAccountCommand, BankAccountClient bankAccountClient) {
        System.out.println("Dans @CommandHandler UpdateBankAccountAggregate " + updateBankAccountCommand.toString());
        Transaction transaction = updateBankAccountCommand.getTransaction();

        try {
            bankAccountClient.updateBankAccount(transaction.getSource().getIban(), transaction.getSource().getBalance() - transaction.getAmount());
            bankAccountClient.updateBankAccount(transaction.getDest().getIban(), transaction.getDest().getBalance() + transaction.getAmount());

            apply(new UpdatedBankAccountEvent(updateBankAccountCommand.getUuid(), transaction));
        } catch (Exception e) {
            apply(new TransactionRejectedEvent(updateBankAccountCommand.getUuid(), transaction));
        }
    }

    @EventHandler
    protected void on(UpdatedBankAccountEvent updatedBankAccountEvent) {
        this.uuid = updatedBankAccountEvent.getUuid();
        this.destAccount = updatedBankAccountEvent.getTransaction().getDest();
        this.sourceAccount = updatedBankAccountEvent.getTransaction().getSource();
    }

    @CommandHandler
    public void on(StoreTransactionCommand storeTransactionCommand, TransactionRepository transactionRepository) {
        System.out.println("Dans @CommandHandler StoreTransactionCommand " + storeTransactionCommand.toString());
        Transaction transaction = storeTransactionCommand.getTransaction();
        this.errorOn = false;

        //save transaction
        transactionRepository.save(transaction);

        //FIXME 2 else needed ?
        //there was an error
        if (this.errorOn) {
            Random random = new Random();
            // int randomNumber = random.nextInt(100) + 1;
           /* if (randomNumber <= this.errorRate) {
                // throw new DatabaseWriteException("Error due to our fixed rate");
                apply(new TransactionStorageCancelledEvent(storeTransactionCommand.getUuid(), transaction));
            } else {*/
            apply(new TransactionStorageCancelledEvent(storeTransactionCommand.getUuid(), transaction));
            //           }
        } else {
            if (storeTransactionCommand.getTransaction().getAmount() >= 10.0) {
                apply(new VerificationCodeNeeded(storeTransactionCommand.getUuid(), transaction));
            } else {
                apply(new TransactionApprovedEvent(storeTransactionCommand.getUuid(), transaction));
            }
        }
    }

    @SagaEventHandler(associationProperty = "uuid")
    protected void on(VerificationCodeNeeded verificationCodeNeeded) {
        System.out.println("Dans @EventHandler on " + verificationCodeNeeded.toString());
        this.uuid = verificationCodeNeeded.getUuid();
        this.transactionState = TransactionState.PENDING;
    }

    @SagaEventHandler(associationProperty = "uuid")
    protected void on(TransactionStorageCancelledEvent transactionStorageCancelledEvent) {
        System.out.println("Dans @EventHandler on " + transactionStorageCancelledEvent.toString());
        this.uuid = transactionStorageCancelledEvent.getUuid();
        this.transactionState = TransactionState.CANCEL;
    }

    @SagaEventHandler(associationProperty = "uuid")
    protected void on(TransactionApprovedEvent transactionApprovedEvent) {
        System.out.println("Dans @EventHandler on " + transactionApprovedEvent.toString());
        this.uuid = transactionApprovedEvent.getUuid();
        this.transactionState = TransactionState.ACCEPTED;
    }

    @CommandHandler
    public void on(CancelTransactionStorageCommand cancelTransactionStorageCommand, BankAccountClient bankAccountClient) {
        System.out.println("Dans @CommandHandler CancelTransactionStorageCommand " + cancelTransactionStorageCommand.toString());
        Transaction transaction = cancelTransactionStorageCommand.getTransaction();

        //compensiate accounts
        bankAccountClient.updateBankAccount(transaction.getSource().getIban(), transaction.getSource().getBalance() + transaction.getAmount());
        bankAccountClient.updateBankAccount(transaction.getDest().getIban(), transaction.getDest().getBalance() - transaction.getAmount());

        apply(new TransactionRejectedEvent(cancelTransactionStorageCommand.getUuid(), transaction));
    }

    @CommandHandler
    public void on(ConfirmCodeCommand confirmCodeCommand, TransactionRepository transactionRepository) {
        System.out.println("Dans @CommandHandler ConfirmCodeCommand " + confirmCodeCommand.toString());

        Optional<Transaction> transactionOpt = transactionRepository.findById(confirmCodeCommand.getUuid());
        if (!transactionOpt.isPresent() || transactionOpt.get().getCode() != code) {
            apply(new TransactionRejectedEvent(confirmCodeCommand.getUuid(), transactionOpt.get()));
        }
        Transaction transaction = transactionOpt.get();
        transaction.setCode((short) 0);
        transactionRepository.save(transaction);

        apply(new CodeConfirmedEvent(confirmCodeCommand.getUuid(), transactionOpt.get(), confirmCodeCommand.getCode()));
    }

    @SagaEventHandler(associationProperty = "uuid")
    protected void on(CodeConfirmedEvent codeConfirmedEvent) {
        System.out.println("Dans @EventSourcingHandler on " + codeConfirmedEvent.toString());
        this.uuid = codeConfirmedEvent.getUuid();
        this.code = codeConfirmedEvent.getCode();
    }

    public String getUuid() {
        return uuid;
    }

    public BankAccount getSourceAccount() {
        return sourceAccount;
    }

    public BankAccount getDestAccount() {
        return destAccount;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getCreatedTransaction() {
        return createdTransaction;
    }

    public TransactionState getTransactionState() {
        return transactionState;
    }

    public short getCode() {
        return code;
    }
}
