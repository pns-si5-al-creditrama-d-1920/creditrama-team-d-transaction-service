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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@CrossOrigin(origins = "*", allowedHeaders = "content-type")
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
    public ResponseEntity<List<Transaction>> getTransactionByIban(@RequestParam(value = "type", required = false) String type, @PathVariable(value = "id") long id) {
        if (type == null) {
            return ResponseEntity.ok(transactionService.getAllTransactionByIban(id, null));
        }
        if (type.equals("ACCEPTED")) {
            return ResponseEntity.ok(transactionService.getAllTransactionByIban(id, TransactionState.ACCEPTED));
        }
        if (type.equals("PENDING")) {
            return ResponseEntity.ok(transactionService.getAllTransactionByIban(id, TransactionState.PENDING));
        }
        return ResponseEntity.notFound().build();
    }


}
