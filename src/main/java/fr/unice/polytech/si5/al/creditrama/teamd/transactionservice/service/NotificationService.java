package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.service;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.kafka.NotificationStreams;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

@Service
@EnableBinding(NotificationStreams.class)
@Profile("!disable-kafka")
public class NotificationService {
    private final NotificationStreams notificationStreams;

    @Autowired
    public NotificationService(NotificationStreams notificationStreams) {
        this.notificationStreams = notificationStreams;
    }

    public void sendMail(Transaction transaction) {

        MessageChannel messageChannel = notificationStreams.sendTransaction();

         messageChannel.send(MessageBuilder
                .withPayload(transaction)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build());
    }


}
