package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.aggregators;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands.ConfirmCodeCommand;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events.CodeConfirmedEvent;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events.TransactionRejectedEvent;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.Transaction;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.repository.TransactionRepository;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.Optional;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class ConfirmCodeAggregate {

    @AggregateIdentifier
    private String uuid;

    private Transaction transaction;

    private short code;

    @CommandHandler
    public ConfirmCodeAggregate(ConfirmCodeCommand confirmCodeCommand, TransactionRepository transactionRepository) {
        System.out.println("Dans @CommandHandler ConfirmCodeAggregate " + confirmCodeCommand.toString());

        Optional<Transaction> transactionOpt = transactionRepository.findById(confirmCodeCommand.getUuid());
        if (!transactionOpt.isPresent() || transactionOpt.get().getCode() != code) {
            apply(new TransactionRejectedEvent(confirmCodeCommand.getUuid(), transactionOpt.get()));
        }
        Transaction transaction = transactionOpt.get();
        transaction.setCode((short) 0);
        transactionRepository.save(transaction);

        apply(new CodeConfirmedEvent(confirmCodeCommand.getUuid(), transaction, confirmCodeCommand.getCode()));
    }

    @EventHandler
    protected void on(CodeConfirmedEvent codeConfirmedEvent) {
        System.out.println("Dans @EventSourcingHandler on " + codeConfirmedEvent.toString());
        this.uuid = codeConfirmedEvent.getUuid();
        this.transaction = codeConfirmedEvent.getTransaction();
        this.code = codeConfirmedEvent.getCode();
    }
}
