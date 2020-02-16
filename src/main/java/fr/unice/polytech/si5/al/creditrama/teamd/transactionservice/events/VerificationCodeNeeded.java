package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events;

import lombok.Getter;

@Getter
public class VerificationCodeNeeded extends Event {

    public VerificationCodeNeeded(String uuid) {
        this.uuid = uuid;
    }
}
