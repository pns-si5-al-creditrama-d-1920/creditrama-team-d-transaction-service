package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.client;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.BankAccount;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "bankaccounts", url = "${service.bankaccount}", configuration = FeignConfiguration.class)
public interface BankAccountClient {

    @GetMapping("/accounts/{iban}")
    BankAccount getBankAccount(@PathVariable("iban") String iban);

    @PatchMapping("/accounts/{iban}")
    ResponseEntity<BankAccount> updateBanAccount(@PathVariable String iban, @RequestParam double balance);

}