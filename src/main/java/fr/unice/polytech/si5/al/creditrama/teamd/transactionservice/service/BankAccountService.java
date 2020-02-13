package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.service;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands.RejectTransactionCommand;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands.StoreTransactionCommand;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.kafka.TransactionStreams;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.TransferDTO;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import static fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.kafka.TransactionStreams.TRANSFER_DONE_TOPIC;
import static fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.kafka.TransactionStreams.TRANSFER_ERROR_TOPIC;

@Service
@EnableBinding(TransactionStreams.class)
@Profile("!disable-kafka")
public class BankAccountService {
    private final TransactionStreams transactionStreams;
    private CommandGateway commandGateway;

    @Autowired
    public BankAccountService(TransactionStreams transactionStreams, CommandGateway commandGateway) {
        this.transactionStreams = transactionStreams;
        this.commandGateway = commandGateway;
    }

    public void makeTransfer(TransferDTO transferDTO) {
        MessageChannel messageChannel = transactionStreams.makeTransfer();
        messageChannel.send(MessageBuilder
                .withPayload(transferDTO)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build());
    }

    public void reverseTransfer(TransferDTO transferDTO) {
        MessageChannel messageChannel = transactionStreams.reverseTransfer();
        messageChannel.send(MessageBuilder
                .withPayload(transferDTO)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build());
    }

    @StreamListener(TRANSFER_DONE_TOPIC)
    public void transferDone(TransferDTO transferDTO) {
        commandGateway.send(new StoreTransactionCommand(transferDTO.getUuid()));
    }

    @StreamListener(TRANSFER_ERROR_TOPIC)
    public void transferError(TransferDTO transferDTO) {
        commandGateway.send(new RejectTransactionCommand(transferDTO.getUuid()));
    }
}
