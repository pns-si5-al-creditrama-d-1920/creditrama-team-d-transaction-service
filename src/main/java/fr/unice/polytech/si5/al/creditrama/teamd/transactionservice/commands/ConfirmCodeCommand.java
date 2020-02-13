package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public class ConfirmCodeCommand {

    @TargetAggregateIdentifier
    private String uuid;
    private short code;

    public ConfirmCodeCommand(String uuid, short code) {
        this.uuid = uuid;
        this.code = code;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public short getCode() {
        return code;
    }

    public void setCode(short code) {
        this.code = code;
    }
}
