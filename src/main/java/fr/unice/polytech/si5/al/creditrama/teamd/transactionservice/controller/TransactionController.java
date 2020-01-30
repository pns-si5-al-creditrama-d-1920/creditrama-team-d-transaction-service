package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.controller;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.Transaction;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.TransactionRequest;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "content-type")
@RestController
public class TransactionController {
    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transactions")
    public ResponseEntity<HttpStatus> createTransaction(@RequestBody TransactionRequest transaction) {
        return transactionService.makeTransaction(transaction);
    }

    @GetMapping("/transactions/{iban}")
    public ResponseEntity<List<Transaction>> getTransactionByIban(@PathVariable(value = "iban") String iban) {
        return ResponseEntity.ok(transactionService.getAcceptedTransactionByIban(iban));
    }

    @GetMapping("clients/{id}/transactions")
    public ResponseEntity<List<Transaction>> getTransactionByIban(@PathVariable(value = "id") long id) {
        return ResponseEntity.ok(transactionService.getAcceptedTransactionByIban(id));
    }

    @GetMapping("/writeerror")
    public ResponseEntity<Boolean> getTransactionErrors() {
        return ResponseEntity.ok(transactionService.isErrorsOn());
    }

    @PostMapping("/writeerror")
    public ResponseEntity<Boolean> setTransactionErrors(@RequestBody Boolean state) {
        transactionService.setErrorsOn(state);
        return ResponseEntity.ok(transactionService.isErrorsOn());
    }

    @GetMapping("/errorrate")
    public ResponseEntity<Integer> getErrorRate() {
        return ResponseEntity.ok(transactionService.getErrorRate());
    }

    @PostMapping("/errorrate")
    public ResponseEntity<Integer> setErrorRate(@RequestBody Integer newRate) {
        transactionService.setErrorRate(newRate);
        return ResponseEntity.ok(transactionService.getErrorRate());
    }
}
