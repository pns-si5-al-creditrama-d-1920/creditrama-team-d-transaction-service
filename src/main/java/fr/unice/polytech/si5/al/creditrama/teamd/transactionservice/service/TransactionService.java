package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.service;

import fr.unice.polytech.si5.al.creditrama.teamd.coreapi.commands.ConfirmCodeCommand;
import fr.unice.polytech.si5.al.creditrama.teamd.coreapi.commands.CreateTransactionCommand;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.client.BankAccountClient;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.*;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class TransactionService {
    private TransactionRepository transactionRepository;
    private BankAccountClient bankAccountClient;
    private NotificationService notificationService;
    private CommandGateway commandGateway;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, BankAccountClient bankAccountClient, NotificationService notificationService, CommandGateway commandGateway) {
        this.transactionRepository = transactionRepository;
        this.bankAccountClient = bankAccountClient;
        this.notificationService = notificationService;
        this.commandGateway = commandGateway;
    }

    /* SAGA ADDED */
    public TransactionResponse createTransaction(TransactionRequest transactionRequest) {
        String uuid = UUID.randomUUID().toString();
        System.out.println("transaction request " + transactionRequest.toString());
        System.out.println("command gateway : " + commandGateway.toString());
        commandGateway.send(new CreateTransactionCommand(uuid, transactionRequest.getIbanSource(), transactionRequest.getIbanDest(),
                transactionRequest.getAmount(), LocalDateTime.now(Clock.systemDefaultZone()), TransactionState.PENDING.getValue(), (short) (new Random().nextInt(9000) + 1000)));
        return new TransactionResponse(uuid, transactionRequest.getAmount() >= 10);
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

    public boolean oldConfirmCode(String uuid, short code) {
        Optional<Transaction> transactionOpt = transactionRepository.findById(uuid);
        if (!transactionOpt.isPresent() || transactionOpt.get().getCode() != code) {
            return false;
        }
        Transaction transaction = transactionOpt.get();
        transaction.setCode((short) 0);
        transaction.setTransactionState(TransactionState.ACCEPTED);
        transactionRepository.save(transaction);
        return true;
    }

    public void confirmCode(String uuid, short code) {
        commandGateway.send(new ConfirmCodeCommand(uuid, code));
    }
}
