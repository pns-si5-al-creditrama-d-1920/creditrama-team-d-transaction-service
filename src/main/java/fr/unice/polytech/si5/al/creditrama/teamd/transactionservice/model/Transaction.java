package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("transactions")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Transaction {
    @Id
    private String uuid;

    private BankAccount source;

    private BankAccount dest;

    private double amount;

    @CreatedDate
    private LocalDateTime createdTransaction;

    private TransactionState transactionState;

    private short code;
}
