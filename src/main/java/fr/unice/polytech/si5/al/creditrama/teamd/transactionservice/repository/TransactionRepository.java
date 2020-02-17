package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.repository;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.Transaction;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.TransactionState;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TransactionRepository extends MongoRepository<Transaction, String> {
    List<Transaction> findAllByDestIbanAndTransactionStateOrderByCreatedTransactionDesc(String iban, TransactionState transactionState);

    List<Transaction> findAllBySourceIbanAndTransactionStateOrderByCreatedTransactionDesc(String iban, TransactionState transactionState);

    List<Transaction> findAllBySourceClientAndTransactionStateOrderByCreatedTransactionDesc(long id, TransactionState transactionState);

    List<Transaction> findAllByDestClientAndTransactionStateOrderByCreatedTransactionDesc(long id, TransactionState transactionState);

    List<Transaction> findByCodeNotNullAndTransactionStateOrderByCreatedTransactionDesc(TransactionState state);

    List<Transaction> findAllBySourceClientOrderByCreatedTransactionDesc(long id);

    List<Transaction> findAllByDestClientOrderByCreatedTransactionDesc(long id);
}
