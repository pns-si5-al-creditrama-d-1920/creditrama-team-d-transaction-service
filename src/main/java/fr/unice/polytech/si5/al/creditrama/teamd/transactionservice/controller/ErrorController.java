package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.controller;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.service.ErrorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
public class ErrorController {

    private ErrorService errorService;

    @Autowired
    public ErrorController(ErrorService errorService) {
        this.errorService = errorService;
    }

    @GetMapping("/writeerror")
    public ResponseEntity<Boolean> getTransactionErrors() {
        return ResponseEntity.ok(errorService.isErrorsOn());
    }

    @PostMapping("/writeerror")
    public ResponseEntity<Boolean> setTransactionErrors(@RequestBody Boolean state) {
        errorService.setErrorsOn(state);
        return ResponseEntity.ok(errorService.isErrorsOn());
    }

    @GetMapping("/errorrate")
    public ResponseEntity<Integer> getErrorRate() {
        return ResponseEntity.ok(errorService.getErrorRate());
    }

    @PostMapping("/errorrate")
    public ResponseEntity<Integer> setErrorRate(@RequestBody Integer newRate) {
        errorService.setErrorRate(newRate);
        return ResponseEntity.ok(errorService.getErrorRate());
    }

}
