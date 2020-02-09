package fr.unice.polytech.si5.al.creditrama.teamd.transactionservice;

import fr.unice.polytech.si5.al.creditrama.teamd.transactionservice.service.TransactionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("disable-kafka")
@Transactional
public class BankServiceBusinessNOP {

    @Autowired
    private TransactionService business;

    @Before
    public void setUp() {
   /*   Client client = new Client(null, "nathan", "password", "n@gmail.com", true, true, true, true, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
      client = clientService.save(client);
      clientId = client.getUserId();
      bankAccountId = client.getBankAccounts().get(0).getAccountNumber();*/
    }

    @Test
    public void test() {
        System.out.println("coucou");
    }
/**
 @Test public void manageBankAccountsOfClient() throws Exception {
 BankAccount expected = BankAccount.builder().balance(500.0).build();
 BankAccount received = business.createClientBankAccount(clientId, expected);
 String id = received.getAccountNumber();

 expected.setAccountNumber(id);
 Optional<BankAccount> actual = bankAccountRepository.findById(id);
 assertEquals(Optional.of(expected), actual);

 Optional<Client> client = clientRepository.findById(clientId);

 assertTrue(client.get().getBankAccounts().contains(expected));
 assertEquals(business.retrieveClientBankAccounts(clientId), client.get().getBankAccounts());
 }

 @Test public void manageRecipients() throws Exception {
 BankAccount account = BankAccount.builder().balance(999.99).build();
 String expectedBankAccountId = bankAccountRepository.save(account).getAccountNumber();

 Client client = new Client(null, "théo", "password", "n@gmail.com", true, true, true, true, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
 client.getBankAccounts().add(account);
 client = clientRepository.save(client);
 Integer clientId = client.getUserId();

 // Add a recipient
 String recipient = business.createClientRecipient(this.clientId, expectedBankAccountId);
 assertTrue(business.retrieveClientRecipients(this.clientId).contains(recipient));

 recipient = business.createClientRecipient(this.clientId, expectedBankAccountId);
 assertNull(recipient);

 recipient = business.createClientRecipient(clientId, expectedBankAccountId);
 assertNull(recipient);

 // Remove a recipient
 business.removeRecipient(this.clientId, expectedBankAccountId);
 assertFalse(business.retrieveClientRecipients(this.clientId).contains(recipient));
 }

 @Test public void transferToRecipient() throws Exception {
 BankAccount account = BankAccount.builder().balance(1000.0).build();
 Integer bankAccountId = bankAccountRepository.save(account).getAccountNumber();

 Client client = new Client(new Integer(0), "alexis", "password", "al@gmail.com", true, true, true, true, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
 client.getBankAccounts().add(account);
 Integer clientId = clientService.save(client).getUserId();

 assertEquals(100.0, bankAccountRepository.findById(this.bankAccountId).get().getBalance(), 0.01);
 assertEquals(1000.0, bankAccountRepository.findById(bankAccountId).get().getBalance(), 0.01);

 business.createClientRecipient(clientId, this.bankAccountId);

 BankTransaction transaction = BankTransaction.builder()
 .sourceId(bankAccountId)
 .destinationId(this.bankAccountId)
 .amount(500.0)
 .build();

 business.createClientTransaction(clientId, transaction);

 assertEquals(500.0, bankAccountRepository.findById(bankAccountId).get().getBalance(), 0.01);
 assertEquals(600.0, bankAccountRepository.findById(this.bankAccountId).get().getBalance(), 0.01);

 }

 @Test(expected = InvalidBankTransactionException.class)
 public void transferToSameAccount() throws Exception {
 BankTransaction transaction = BankTransaction.builder()
 .sourceId(bankAccountId)
 .destinationId(this.bankAccountId)
 .amount(500.0)
 .build();
 business.createClientTransaction(clientId, transaction);
 }

 @Test(expected = InvalidBankTransactionException.class)
 public void invalidTransaction() throws Exception {
 BankAccount account = BankAccount.builder().balance(1000.0).build();
 Integer bankAccountId = bankAccountRepository.save(account).getAccountNumber();

 Client client = new Client(null, "alexis", "password", "al@gmail.com", true, true, true, true, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
 client.getBankAccounts().add(account);
 Integer clientId = clientRepository.save(client).getUserId();

 BankTransaction transaction = BankTransaction.builder()
 .sourceId(this.bankAccountId)
 .destinationId(bankAccountId)
 .amount(500.0)
 .build();

 business.createClientTransaction(clientId, transaction);
 }

 @Test(expected = InvalidBankTransactionException.class)
 public void transferToNonRecipient() throws Exception {
 BankAccount account = BankAccount.builder().balance(1000.0).build();
 Integer bankAccountId = bankAccountRepository.save(account).getAccountNumber();

 Client client = new Client(null, "alexis", "password", "al@gmail.com", true, true, true, true, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
 client.getBankAccounts().add(account);
 Integer clientId = clientRepository.save(client).getUserId();

 BankTransaction transaction = BankTransaction.builder()
 .sourceId(bankAccountId)
 .destinationId(this.bankAccountId)
 .amount(500.0)
 .build();

 business.createClientTransaction(clientId, transaction);
 }

 @Test(expected = BankAccountNotFoundException.class)
 public void removeRecipientNotPresent() throws Exception {
 BankAccount account = BankAccount.builder().balance(999.99).build();
 Integer bankAccountId = bankAccountRepository.save(account).getAccountNumber();

 business.removeRecipient(this.clientId, bankAccountId);
 }
 **/
}
