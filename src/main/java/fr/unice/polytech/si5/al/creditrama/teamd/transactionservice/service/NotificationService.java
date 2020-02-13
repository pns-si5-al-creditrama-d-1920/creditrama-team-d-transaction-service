package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.service;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.kafka.TransactionStreams;
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
@EnableBinding(TransactionStreams.class)
@Profile("!disable-kafka")
public class NotificationService {
    private final TransactionStreams transactionStreams;

    @Autowired
    public NotificationService(TransactionStreams transactionStreams) {
        this.transactionStreams = transactionStreams;
    }

    public void sendMail(Transaction transaction) {
        MessageChannel messageChannel = transactionStreams.sendTransaction();
        messageChannel.send(MessageBuilder
                .withPayload(transaction)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build());
    }
}
