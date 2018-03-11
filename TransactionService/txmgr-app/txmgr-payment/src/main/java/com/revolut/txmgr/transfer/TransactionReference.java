package com.revolut.txmgr.transfer;

public class TransactionReference {

    private Thread thread;
    private TransactionPhase state;
    private Transaction transaction;
    
    TransactionReference(Thread thread) {
        this.thread = thread;
        this.state = TransactionPhase.INITIALIZED;
    }

    public void commit(Transaction transaction) {
        assert Thread.holdsLock(this);

        assert this.thread == Thread.currentThread();
        assert this.state == TransactionPhase.INITIALIZED;
        assert this.transaction == null;

        this.thread = null;
        this.state = TransactionPhase.COMMITED;
        this.transaction = transaction;
    }

    public void rollback(Transaction transaction) {
        assert Thread.holdsLock(this);

        assert this.thread != null;
        assert this.state == TransactionPhase.INITIALIZED;
        assert this.transaction == null;

        this.thread = null;
        this.state = TransactionPhase.ROLLED_BACK;
        this.transaction = transaction;
    }

    public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}
	
	public TransactionPhase getState() {
		return state;
	}

	public void setState(TransactionPhase state) {
		this.state = state;
	}
	
	public Thread getThread() {
		return thread;
	}

	public void setThread(Thread thread) {
		this.thread = thread;
	}

}
