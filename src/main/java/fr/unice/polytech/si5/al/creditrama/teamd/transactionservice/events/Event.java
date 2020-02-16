package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events;

import lombok.Getter;

@Getter
public abstract class Event {
    protected String uuid;

    @Override
    public String toString() {
        return "[ Transaction : " + uuid + " ] " + this.getClass().getSimpleName() + " was thrown.";
    }
}
