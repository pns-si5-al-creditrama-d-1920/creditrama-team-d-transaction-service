package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.controller;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.Transaction;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.TransactionRequest;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.TransactionResponse;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<TransactionResponse> createTransaction(@RequestBody TransactionRequest transactionRequest) {
        Transaction transaction = transactionService.makeTransaction(transactionRequest);
        return ResponseEntity.ok(TransactionResponse.builder()
                .uuid(transaction.getUuid())
                .code(transaction.getCode() != 0).build());
    }

    @PatchMapping("/transactions/{uuid}/code")
    public ResponseEntity<Boolean> confirmCode(@RequestParam(value = "code") String code, @PathVariable(value = "uuid") String uuid) {
        return ResponseEntity.ok(transactionService.confirmCode(uuid, Short.parseShort(code)));
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
