package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.service;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.client.BankAccountClient;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands.ConfirmCodeCommand;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands.CreateTransactionCommand;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.*;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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
        commandGateway.send(new CreateTransactionCommand(uuid, transactionRequest.getIbanSource(), transactionRequest.getIbanDest(),
                transactionRequest.getAmount(), LocalDateTime.now(), TransactionState.PENDING.getValue(), (short) (new Random().nextInt(9000) + 1000)));
        return new TransactionResponse(uuid, transactionRequest.getAmount() >= 10);
    }

    public List<Transaction> getAcceptedTransactionByIban(String iban) {
        List<Transaction> allByIban = transactionRepository.findAllByDestIbanAndTransactionStateOrderByCreatedTransactionDesc(iban, TransactionState.ACCEPTED);
        allByIban.addAll(transactionRepository.findAllBySourceIbanAndTransactionStateOrderByCreatedTransactionDesc(iban, TransactionState.ACCEPTED));
        return allByIban;
    }

    public List<Transaction> getAllTransactionByIban(long id, TransactionState type) {
        if (type != null) {
            List<Transaction> allById = transactionRepository.findAllByDestClientAndTransactionStateOrderByCreatedTransactionDesc(id, type);
            allById.addAll(transactionRepository.findAllBySourceClientAndTransactionStateOrderByCreatedTransactionDesc(id, type));
            return allById;
        }
        List<Transaction> allById = transactionRepository.findAllByDestClientOrderByCreatedTransactionDesc(id);
        allById.addAll(transactionRepository.findAllBySourceClientOrderByCreatedTransactionDesc(id));
        return allById;
    }

    public HttpStatus confirmCode(String uuid, short code) {
        commandGateway.send(new ConfirmCodeCommand(uuid, code));
        Optional<Transaction> transaction = transactionRepository.findById(uuid);
        if (transaction.isPresent()) {
            return transaction.get().getCode() != code ? HttpStatus.EXPECTATION_FAILED : HttpStatus.OK;
        } else {
            return HttpStatus.NOT_FOUND;
        }
    }
}
