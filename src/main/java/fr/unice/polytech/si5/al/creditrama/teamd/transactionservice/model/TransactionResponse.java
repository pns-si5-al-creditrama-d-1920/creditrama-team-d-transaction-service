package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model;

import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class TransactionResponse {
    private String uuid;
    private boolean code;
}
