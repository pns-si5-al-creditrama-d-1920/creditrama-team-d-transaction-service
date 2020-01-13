package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class BankAccount {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Double balance;

    private String iban;

    private long client;

    private String accountNumber;

    public void addMoney(double amount) {
        this.balance += amount;
    }

    public void removeMoney(double amount) {
        this.balance -= amount;
    }
}
