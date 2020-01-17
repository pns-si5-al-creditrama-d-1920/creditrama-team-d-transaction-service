package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.kafka;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface NotificationStreams {
    @Output("CreditRama.SendNotif.Email.Transaction")
    MessageChannel sendTransaction();


}
