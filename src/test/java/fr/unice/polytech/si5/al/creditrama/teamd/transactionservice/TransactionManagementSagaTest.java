package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.commands.*;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.events.*;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.BankAccount;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.model.TransactionState;
import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.sagas.TransactionManagementSaga;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.axonframework.test.saga.SagaTestFixture;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = PropertyOverrideContextInitializer.class)
@Profile("!disable-kafka")
public class TransactionManagementSagaTest {

    private static SagaTestFixture<TransactionManagementSaga> fixture = new SagaTestFixture<>(TransactionManagementSaga.class);
    private static String transactionUuid;
    private static BankAccount sourceAccount;
    private static BankAccount destAccount;
    private static String ibanSource = UUID.randomUUID().toString();
    private static String ibanDest = UUID.randomUUID().toString();

    @BeforeAll
    static void setUp() {
        fixture.registerCommandGateway(CommandGateway.class);
        transactionUuid = UUID.randomUUID().toString();

        sourceAccount = BankAccount.builder().iban(ibanSource).accountNumber("24").balance(200.0).build();
        destAccount = BankAccount.builder().iban(ibanDest).accountNumber("25").balance(100.0).build();
    }

    @Test
    public void handleTransactionCreatedTest() {
        fixture.givenAggregate(transactionUuid).published().whenAggregate(transactionUuid).publishes(new TransactionCreatedEvent(
                transactionUuid,
                sourceAccount,
                destAccount,
                5.0,
                LocalDateTime.now(Clock.systemDefaultZone()),
                TransactionState.PENDING.getValue(),
                (short) (new Random().nextInt(9000) + 1000))
        )
                .expectActiveSagas(1)
                .expectDispatchedCommands(new CheckTransactionCommand(transactionUuid));
    }

    @Test
    public void handleTransactionCheckedTest() {
        TransactionCheckedEvent transactionCheckedEvent = new TransactionCheckedEvent(
                transactionUuid,
                ibanSource,
                ibanDest,
                5.0
        );

        fixture.givenAggregate(transactionUuid).published(new TransactionCreatedEvent(
                transactionUuid,
                sourceAccount,
                destAccount,
                5.0,
                LocalDateTime.now(Clock.systemDefaultZone()),
                TransactionState.PENDING.getValue(),
                (short) (new Random().nextInt(9000) + 1000)))
                .whenAggregate(transactionUuid).publishes(transactionCheckedEvent)
                .expectActiveSagas(1)
                .expectDispatchedCommands(new MakeTransferCommand(transactionCheckedEvent.getUuid(),
                        transactionCheckedEvent.getSourceIban(), transactionCheckedEvent.getDestIban(), transactionCheckedEvent.getAmount()));
    }

    @Test
    public void handleTransactionStoredTest() {
        TransactionStoredEvent transactionStoredEvent = new TransactionStoredEvent(transactionUuid);

        fixture.givenAggregate(transactionUuid).published(
                new TransactionCreatedEvent(
                        transactionUuid,
                        sourceAccount,
                        destAccount,
                        5.0,
                        LocalDateTime.now(Clock.systemDefaultZone()),
                        TransactionState.PENDING.getValue(),
                        (short) (new Random().nextInt(9000) + 1000))
        )
                .andThenAPublished(new TransactionCheckedEvent(
                        transactionUuid,
                        ibanSource,
                        ibanDest,
                        5.0
                ))
                .whenAggregate(transactionUuid).publishes(transactionStoredEvent)
                .expectActiveSagas(1)
                .expectDispatchedCommands(new ApproveTransactionCommand(transactionUuid));
    }

    @Test
    public void handleTransferCancelledTest() {
        TransferCancelledEvent transferCancelledEvent = new TransferCancelledEvent(transactionUuid, ibanSource, ibanDest, 5.0);

        fixture.givenAggregate(transactionUuid).published(new TransactionCreatedEvent(
                transactionUuid,
                sourceAccount,
                destAccount,
                5.0,
                LocalDateTime.now(Clock.systemDefaultZone()),
                TransactionState.PENDING.getValue(),
                (short) (new Random().nextInt(9000) + 1000)))
                .andThenAPublished(new TransactionCheckedEvent(
                        transactionUuid,
                        ibanSource,
                        ibanDest,
                        5.0
                ))
                .andThenAPublished(new TransactionStoredEvent(transactionUuid))
                .whenAggregate(transactionUuid).publishes(transferCancelledEvent)
                .expectActiveSagas(1)
                .expectDispatchedCommands(new ReverseTransferCommand(transferCancelledEvent.getUuid(),
                        transferCancelledEvent.getSourceIban(), transferCancelledEvent.getDestIban(), transferCancelledEvent.getAmount()));
    }

    @Test
    public void handleAwaitingTransferTest() {
        fixture.givenAggregate(transactionUuid).published().whenAggregate(transactionUuid).publishes(new AwaitingTransferEvent(transactionUuid))
                .expectActiveSagas(1);
    }

    @Test
    public void handleTransactionRejectedTest() {
        TransactionRejectedEvent transactionRejectedEvent = new TransactionRejectedEvent(transactionUuid);

        fixture.givenAggregate(transactionUuid).published().whenAggregate(transactionUuid).publishes(
                transactionRejectedEvent
        )
                .expectActiveSagas(1)
                .expectDispatchedCommands(new RejectTransactionCommand(transactionUuid));
    }

    @Test
    public void handleVerificationCodeNeededTest() {
        fixture.givenAggregate(transactionUuid).published().whenAggregate(transactionUuid).publishes(new VerificationCodeNeeded(transactionUuid))
                .expectActiveSagas(1);
    }

    @Test
    public void handleTransactionApprovedTest() {
        fixture.givenAggregate(transactionUuid).published().whenAggregate(transactionUuid).publishes(new TransactionApprovedEvent(transactionUuid))
                .expectActiveSagas(0);
    }

    @Test
    public void handleTransactionClosedTest() {
        fixture.givenAggregate(transactionUuid).published().whenAggregate(transactionUuid).publishes(new TransactionClosedEvent(transactionUuid))
                .expectActiveSagas(0);
    }
}
