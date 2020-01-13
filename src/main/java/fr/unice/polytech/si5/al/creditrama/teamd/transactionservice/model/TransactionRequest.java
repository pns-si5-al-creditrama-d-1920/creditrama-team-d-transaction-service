package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model;

import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class TransactionRequest {
    private String ibanSource;

    private String ibanDest;

    private double amount;

}
