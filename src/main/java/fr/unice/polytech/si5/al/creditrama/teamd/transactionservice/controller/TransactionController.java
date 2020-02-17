package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.controller;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.Transaction;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.TransactionRequest;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.TransactionResponse;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.TransactionState;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

@CrossOrigin(origins = "*")
@RestController
public class TransactionController {
    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transactions")
    public TransactionResponse createTransaction(@RequestBody TransactionRequest transactionRequest) {
        return transactionService.createTransaction(transactionRequest);
    }

    @PatchMapping("/transactions/{uuid}/code")
    public ResponseEntity<HttpStatus> confirmCode(@RequestParam(value = "code") String code, @PathVariable(value = "uuid") String uuid) {
        return ResponseEntity.ok(transactionService.confirmCode(uuid, Short.parseShort(code)));
    }

    @GetMapping("/transactions/{iban}")
    public ResponseEntity<List<Transaction>> getTransactionByIban(@PathVariable(value = "iban") String iban) {
        return ResponseEntity.ok(transactionService.getAcceptedTransactionByIban(iban));
    }

    @GetMapping("clients/{id}/transactions")
    public ResponseEntity<List<Transaction>> getTransactionByIban(@RequestParam(value = "type", required = false) TransactionState type, @PathVariable(value = "id") long id) {
        List<Transaction> allTransactionByIban = transactionService.getAllTransactionByIban(id, type).stream().map(v -> {
            v.setCode((short) 0);
            return v;
        }).collect(toList());
        allTransactionByIban.sort(Comparator.comparing(Transaction::getCreatedTransaction));
        return ResponseEntity.ok(allTransactionByIban);
    }


}
