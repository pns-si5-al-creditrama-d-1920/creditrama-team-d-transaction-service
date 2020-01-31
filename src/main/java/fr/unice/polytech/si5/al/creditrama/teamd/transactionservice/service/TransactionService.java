package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.service;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.client.BankAccountClient;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands.ReceiveTransactionCommand;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.exception.DatabaseWriteException;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.BankAccount;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.Transaction;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.TransactionRequest;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.TransactionState;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.repository.TransactionRepository;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class TransactionService {
    private TransactionRepository transactionRepository;
    private BankAccountClient bankAccountClient;
    private NotificationService notificationService;
    private CommandGateway commandGateway;
    private boolean errorsOn;
    private int errorRate;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, BankAccountClient bankAccountClient, NotificationService notificationService, CommandGateway commandGateway) {
        this.transactionRepository = transactionRepository;
        this.bankAccountClient = bankAccountClient;
        this.notificationService = notificationService;
        this.errorsOn = false;
        this.errorRate = 5;
        this.commandGateway = commandGateway;
    }

    /* SAGA ADDED */
    public CompletableFuture<String> createTransaction(TransactionRequest transactionRequest) {
        String uuid = UUID.randomUUID().toString();

        System.out.println("transaction request " + transactionRequest.toString());
        System.out.println("command gateway : " + commandGateway.toString());
        return commandGateway.send(new ReceiveTransactionCommand(uuid, transactionRequest.getIbanSource(), transactionRequest.getIbanDest(), transactionRequest.getAmount()));
    }

    /* SAGA ADDED */

    public ResponseEntity<HttpStatus> makeTransaction(final TransactionRequest transactionRequest) {
        Transaction transaction = new Transaction();
        transaction.setUuid(UUID.randomUUID().toString().replace("-", ""));
        transaction.setTransactionState(TransactionState.PENDING);
        transactionRepository.save(transaction);
        try {
            BankAccount bankAccountSrc = bankAccountClient.getBankAccount(transactionRequest.getIbanSource());
            BankAccount bankAccountDst = bankAccountClient.getBankAccount(transactionRequest.getIbanDest());
            transaction.setDest(bankAccountDst);
            transaction.setSource(bankAccountSrc);
            transaction.setAmount(transactionRequest.getAmount());
            transaction.setCreatedTransaction(LocalDateTime.now());
            if (this.errorsOn) {
                Random random = new Random();
                int randomNumber = random.nextInt(100) + 1;
                if (randomNumber <= this.errorRate) {
                    throw new DatabaseWriteException("Error due to our fixed rate");
                }
            }
            //TODO ADD PARAMETER FOR OVERDRAFT AND WE SHOULD USE PROCEDURE OVER REST
            //IF TWO TRANSACTION IN SAME TIME, IN REST STYLE WE SET BALANCE
            //WE SHOULD SEND A PROCEDURE TO BANK SERVICE WHICH REDUCE CURRENT BALANCE
            if (bankAccountSrc.getBalance() - transaction.getAmount() >= 0) {
                bankAccountClient.updateBanAccount(bankAccountSrc.getIban(), bankAccountSrc.getBalance() - transaction.getAmount());
                bankAccountClient.updateBanAccount(bankAccountDst.getIban(), bankAccountSrc.getBalance() + transaction.getAmount());
            }
        } catch (Exception e) {
            e.printStackTrace();
            //By Default if we catch an exception we make the transaction in cancel state
            transaction.setTransactionState(TransactionState.CANCEL);
            transactionRepository.save(transaction);
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        transaction.setTransactionState(TransactionState.ACCEPTED);
        transactionRepository.save(transaction);
        notificationService.sendMail(transaction);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    public List<Transaction> getAcceptedTransactionByIban(String iban) {
        List<Transaction> allByIban = transactionRepository.findAllByDestIbanAndTransactionState(iban, TransactionState.ACCEPTED);
        allByIban.addAll(transactionRepository.findAllBySourceIbanAndTransactionState(iban, TransactionState.ACCEPTED));
        return allByIban;
    }

    public List<Transaction> getAcceptedTransactionByIban(long id) {
        List<Transaction> allById = transactionRepository.findAllByDestClientAndTransactionState(id, TransactionState.ACCEPTED);
        allById.addAll(transactionRepository.findAllBySourceClientAndTransactionState(id, TransactionState.ACCEPTED));
        return allById;
    }

    public boolean isErrorsOn() {
        return errorsOn;
    }

    public void setErrorsOn(Boolean errorsOn) {
        this.errorsOn = errorsOn;
    }

    public int getErrorRate() {
        return errorRate;
    }

    public void setErrorRate(int errorRate) {
        this.errorRate = errorRate;
    }
}
