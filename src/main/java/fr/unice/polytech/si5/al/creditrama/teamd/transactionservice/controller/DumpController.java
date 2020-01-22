package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.controller;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.Transaction;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.repository.TransactionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
public class DumpController {
    private TransactionRepository repository;

    public DumpController(TransactionRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/dump")
    public ResponseEntity<List<Transaction>> instantPrettyDump() {
        return new ResponseEntity<>(this.repository.findAll(), HttpStatus.OK);
    }
}
