package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.kafka;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface TransactionStreams {
    String TRANSFER_DONE_TOPIC = "CreditRama.Transaction.BankAccount.TransferDone";
    String TRANSFER_ERROR_TOPIC = "CreditRama.Transaction.BankAccount.TransferRejected";

    @Output("CreditRama.SendEmail.Transaction")
    MessageChannel sendTransaction();

    @Output("CreditRama.Transaction.BankAccount.MakeTransfer")
    MessageChannel makeTransfer();

    @Output("CreditRama.Transaction.BankAccount.ReverseTransfer")
    MessageChannel reverseTransfer();

    @Input(TRANSFER_DONE_TOPIC)
    MessageChannel transferDone();

    @Input(TRANSFER_ERROR_TOPIC)
    MessageChannel transferError();
}
