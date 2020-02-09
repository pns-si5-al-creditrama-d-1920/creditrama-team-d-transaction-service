package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.service;

import org.springframework.stereotype.Service;

@Service
public class ErrorService {
    private static boolean error = false;
    private static int errorRate = 0;

    public boolean isErrorsOn() {
        return error;
    }

    public void setErrorsOn(Boolean state) {
        error = state;
    }

    public int getErrorRate() {
        return errorRate;
    }

    public void setErrorRate(Integer newRate) {
        errorRate = newRate;
    }
}
