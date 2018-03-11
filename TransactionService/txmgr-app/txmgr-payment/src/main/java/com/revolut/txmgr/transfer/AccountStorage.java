package com.revolut.txmgr.transfer;

import com.revolut.txmgr.api.ResponseCode;
import com.revolut.txmgr.api.json.TransferRequestDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Account storage implementation
 */
public class AccountStorage {
    private static final Logger logger = LoggerFactory.getLogger(AccountStorage.class);

    private final Map<UUID, TransactionReference> transactions = new ConcurrentHashMap<>();
    private final Map<Long, Account> accounts;

    /**
     * @param accounts
     */
    public AccountStorage(Map<Long, Account> accounts) {
        this.accounts = Objects.requireNonNull(accounts, "accounts");
    }

    public ResponseCode processTransfer(TransferRequestDTO transferReqDto) {
        UUID transactionId = UUID.fromString(transferReqDto.getTransactionId());
        
        long sourceAccountId = transferReqDto.getSourceAccountId();
        Account sourceAccount=accounts.get(sourceAccountId);
        Currency sourceCurrency = sourceAccount.getCurrency();
        
        long targetAccountId = transferReqDto.getTargetAccountId();
        Account targetAccount=accounts.get(targetAccountId);
        Currency targetCurrency = targetAccount.getCurrency();
        
        long sourceAmount = transferReqDto.getAmount();
        
        long targetAmount = sourceAmount; // TODO: Using currency converter calculate target amount

        // TODO: Add few validations
        
        Thread currentThread = Thread.currentThread();
        do {
            TransactionReference transactionRef = transactions.computeIfAbsent(transactionId, k -> new TransactionReference(currentThread));
                        
            try {
                synchronized (transactionRef) {
                    // operation is created and going to be processed by another thread, let it get in to complete (we wait)
                    while (transactionRef.getThread() != null && transactionRef.getThread() != currentThread) {
                        assert transactionRef.getState() == TransactionPhase.INITIALIZED;
                        assert transactionRef.getTransaction() == null;
                        try {
                            transactionRef.wait(900);
                        } catch (InterruptedException e) {
                            throw new RuntimeException("interrupted");
                        }
                     }

                    // operation is complete by another thread
                    if (transactionRef.getThread() == null) {
                        if (transactionRef.getState() == TransactionPhase.ROLLED_BACK) {
                            // rolled back transaction
                        	ResponseCode responseCode = transactionRef.getTransaction().getResponseCode();
                            return responseCode;
                        }
                        assert transactionRef.getState() == TransactionPhase.COMMITED;
                        ResponseCode responseCode = transactionRef.getTransaction().getResponseCode();
                        return responseCode;
                       
                    }

                    assert transactionRef.getThread() == currentThread;

                    // todo check for locked etc.

                    synchronized (sourceAccount) {
                        synchronized (targetAccount) {
                        	
                        	Transaction transaction = new Transaction(transactionId, 
                                    sourceAccountId, sourceAmount, sourceCurrency,
                                    targetAccountId, targetAmount, targetCurrency,
                                    transferReqDto.getRemarks());
                        	
                        	//Failure Conditions
                            if (!sourceAccount.ensureAvailableToWithdraw(sourceAmount)) {
                                if(logger.isInfoEnabled()) logger.info("Not enough balance on account {}: {}, txn {}",
                                        sourceAccountId, sourceAccount.getBalance(), transactionId);
                                transaction.setResponseCode(ResponseCode.INSUFFICIENT_BALANCE);
                                transactionRef.rollback(transaction);
                                return ResponseCode.INSUFFICIENT_BALANCE;
                            }
                            //TODO add more failure conditions

                            // all checks made, now the balance changing logic
                            sourceAccount.transact(-sourceAmount);
                            targetAccount.transact(targetAmount);
                            transaction.setResponseCode(ResponseCode.SUCCESS);
                            transactionRef.commit(transaction);
                        }
                    }

                    assert transactionRef.getState() == TransactionPhase.COMMITED;
                    assert transactionRef.getTransaction() != null;
                    // wake up awaiting threads
                    transactionRef.notifyAll();
                    return ResponseCode.SUCCESS;
                }
            } finally {
                // TODO: Logic to handle in any unexpected error scenario
            }
        } while (true);
    }

    
    public ResponseCode getTransferStatus(UUID transactionId) {
        TransactionReference ref = transactions.get(transactionId);
        if (ref == null) {
            return ResponseCode.NOT_FOUND;
        }
        synchronized (ref) {
            if (ref.getState() == TransactionPhase.COMMITED) {
                // only success
                return ref.getTransaction().getResponseCode();
            }
        }
        return ResponseCode.NOT_FOUND;
    }

    
    public static class Account {
        private final long accountId;
        private final Currency currency;
        private final long minimumBalance;
        private long currentBalance;

        public Account(long accountId, Currency currency, long minBalance, long balance) {
            if (balance < minBalance) {
                throw new IllegalArgumentException("Insufficient Balance " + balance + " " + minBalance);
            }
            this.accountId = accountId;
            this.currency = currency;
            this.minimumBalance = minBalance;
            this.currentBalance = balance;
        }

        public long accountId() {
            return accountId;
        }
        
        public Currency getCurrency() {
			return currency;
		}

        private boolean ensureAvailableToWithdraw(long amount) {
            assert Thread.holdsLock(this);
            return currentBalance - amount >= minimumBalance;
        }

        /**
         * @param amount positive: transfer, negative: withdraw
         */
        private void transact(long amount) {
            assert Thread.holdsLock(this);
            long newBalance = this.currentBalance + amount;
            assert newBalance >= minimumBalance;
            this.currentBalance = newBalance;
        }

        public synchronized long getBalance() {
            return currentBalance;
        }

        @Override
        public String toString() {
            return "Account{" +
                    "accountId=" + accountId +
                    ", minimumBalance=" + minimumBalance +
                    ", currentBalance=" + currentBalance +
                    '}';
        }
    }
}
