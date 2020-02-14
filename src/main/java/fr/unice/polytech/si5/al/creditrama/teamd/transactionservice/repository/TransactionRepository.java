package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.repository;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.Transaction;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.TransactionState;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
    List<Transaction> findAllByDestIbanAndTransactionState(String iban, TransactionState transactionState);

    List<Transaction> findAllBySourceIbanAndTransactionState(String iban, TransactionState transactionState);

    List<Transaction> findAllBySourceClientAndTransactionState(long id, TransactionState transactionState);

    List<Transaction> findAllByDestClientAndTransactionState(long id, TransactionState transactionState);

    List<Transaction> findByCodeNotNullAndTransactionState(TransactionState state);

    List<Transaction> findAllBySourceClient(long id);

    List<Transaction> findAllByDestClient(long id);
}
