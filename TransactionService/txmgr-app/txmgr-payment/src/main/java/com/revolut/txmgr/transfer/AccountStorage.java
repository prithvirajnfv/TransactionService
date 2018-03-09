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

        checkAccounts(sourceAccountId, targetAccountId);
        checkAmount(sourceAmount, sourceCurrency, targetAmount, targetCurrency);

        Thread currentThread = Thread.currentThread();
        do {
            TransactionReference transactionRef = transactions.computeIfAbsent(transactionId, k -> new TransactionReference(currentThread));
            boolean rolledback = false;
            try {
                synchronized (transactionRef) {
                    // operation is created and going to be processed by another thread, let it get in to complete (we wait)
                    while (transactionRef.getThread() != null && transactionRef.getThread() != currentThread) {
                        assert transactionRef.getState() == TransactionPhase.INITIALIZED;
                        assert transactionRef.getTransaction() == null;
                        try {
                            transactionRef.wait(1000L);
                        } catch (InterruptedException e) {
                            throw new RuntimeException("interrupted");
                        }
                     }

                    // operation is complete by another thread
                    if (transactionRef.getThread() == null) {
                        if (transactionRef.getState() == TransactionPhase.ROLLED_BACK) {
                            // try one more time
                            // Let other thread to remove rolled back TransactionReference
                            rolledback = true;
                            continue;
                        }
                        assert transactionRef.getState() == TransactionPhase.COMMITED;
                        ResponseCode responseCode = transactionRef.getTransaction().getResponseCode();
                        return responseCode;
                       
                    }

                    assert transactionRef.getThread() == currentThread;

                    // todo check for locked etc.

                    synchronized (sourceAccount) {
                        synchronized (targetAccount) {
                            if (!sourceAccount.ensureAvailableToWithdraw(sourceAmount)) {
                                logger.warn("Not enough balance on account {}: {}, txn {}",
                                        sourceAccountId, sourceAccount.getBalance(), transactionId);
                                transactionRef.rollback();
                                return ResponseCode.INSUFFICIENT_BALANCE;
                            }

                            Transaction transaction = new Transaction(transactionId, ResponseCode.SUCCESS,
                                    sourceAccountId, sourceAmount, sourceCurrency,
                                    targetAccountId, targetAmount, targetCurrency,
                                    transferReqDto.getRemarks());

                            // all checks made, now the balance changing logic
                            sourceAccount.transact(-sourceAmount);
                            targetAccount.transact(targetAmount);

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
                if (rolledback) {
                    transactions.remove(transactionId, transactionRef);
                }
            }
        } while (true);
    }

    private static void checkAccounts(long sourceAccountId, long targetAccountId) {
        if (sourceAccountId <= 0L) {
            throw new IllegalArgumentException("Illegal sourceAccountId " + sourceAccountId);
        }
        if (targetAccountId <= 0L) {
            throw new IllegalArgumentException("Illegal targetAccountId " + targetAccountId);
        }
        if (sourceAccountId == targetAccountId) {
            throw new IllegalArgumentException("Transactions on the same accounts are forbidden " + sourceAccountId);
        }
    }

    private static void checkAmount(long sourceAmount, Currency sourceCurrency, long targetAmount, Currency targetCurrency) {
        if (sourceCurrency == null || targetCurrency == null) {
            throw new IllegalArgumentException("Currency not set");
        }
        if (sourceAmount <= 0 || targetAmount <= 0) {
            throw new IllegalArgumentException("Illegal amounts " + sourceAmount + " " + targetAmount);
        }
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
        private long balance;

        public Account(long accountId, Currency currency, long minBalance, long balance) {
            if (balance < minBalance) {
                throw new IllegalArgumentException("Insufficient Balance " + balance + " " + minBalance);
            }
            this.accountId = accountId;
            this.currency = currency;
            this.minimumBalance = minBalance;
            this.balance = balance;
        }

        public long accountId() {
            return accountId;
        }
        
        public Currency getCurrency() {
			return currency;
		}

        private boolean ensureAvailableToWithdraw(long amount) {
            assert Thread.holdsLock(this);
            return balance - amount >= minimumBalance;
        }

        /**
         * @param amount positive: enroll, negative - withdraw
         */
        private void transact(long amount) {
            assert Thread.holdsLock(this);
            long newBalance = this.balance + amount;
            assert newBalance >= minimumBalance;
            this.balance = newBalance;
        }

        public synchronized long getBalance() {
            return balance;
        }

        @Override
        public String toString() {
            return "Account{" +
                    "accountId=" + accountId +
                    ", lowerLimit=" + minimumBalance +
                    ", balance=" + balance +
                    '}';
        }
    }
}
