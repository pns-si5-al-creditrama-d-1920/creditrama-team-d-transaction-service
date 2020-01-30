package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.service;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.Transaction;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.TransactionState;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScheduledTasks {
    private TransactionRepository transactionRepository;

    @Autowired
    public ScheduledTasks(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Scheduled(fixedDelay = 120000)
    public void checkCodeForPendingTransactions() {
        List<Transaction> transactions = transactionRepository.findByCodeNotNullAndTransactionState(TransactionState.PENDING);
        transactions.forEach(transaction -> {
            transaction.setCode((short) 0);
            transaction.setTransactionState(TransactionState.CANCEL);
            transactionRepository.save(transaction);
            System.out.println(String.format("Cancel transaction %s", transaction.getUuid()));
        });
    }
}