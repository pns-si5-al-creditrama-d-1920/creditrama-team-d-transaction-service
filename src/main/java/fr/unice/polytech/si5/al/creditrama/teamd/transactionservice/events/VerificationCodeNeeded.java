package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events;

public class VerificationCodeNeeded {
    private String uuid;

    public VerificationCodeNeeded(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
