package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.exception;

public class DatabaseWriteException extends Exception {
    public DatabaseWriteException(String errorMessage) {
        super(errorMessage);
    }
}
