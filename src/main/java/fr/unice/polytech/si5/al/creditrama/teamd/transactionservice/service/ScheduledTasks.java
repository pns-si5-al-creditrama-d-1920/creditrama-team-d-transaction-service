package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.service;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands.ReverseTransferCommand;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.Transaction;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.TransactionState;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.repository.TransactionRepository;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ScheduledTasks {
    private TransactionRepository transactionRepository;
    private CommandGateway commandGateway;

    @Autowired
    public ScheduledTasks(TransactionRepository transactionRepository, CommandGateway commandGateway) {
        this.transactionRepository = transactionRepository;
        this.commandGateway = commandGateway;
    }

    @Scheduled(fixedDelay = 120000)
    public void checkCodeForPendingTransactions() {
        List<Transaction> transactions = transactionRepository.findByCodeNotNullAndTransactionState(TransactionState.PENDING);
        transactions.stream()
                .filter(transaction -> transaction.getCreatedTransaction().plusMinutes(15).isAfter(LocalDateTime.now()))
                .forEach(transaction -> {
//                    transaction.setCode((short) 0);
//                    transaction.setTransactionState(TransactionState.CANCEL);
                    System.out.println(String.format("Canceling transaction %s", transaction.getUuid()));
                    commandGateway.send(new ReverseTransferCommand(transaction.getUuid(), transaction.getSource().getIban(), transaction.getDest().getIban(), transaction.getAmount()));
//                    transactionRepository.save(transaction);
                });
    }
}