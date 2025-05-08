package dev.syncdb.compute;

import dev.syncdb.storage.SynchronizedDatabase;


/**
 * A class that implements runnable and performs a transaction
 * on th provided database. 
 */
public class DatabaseTransaction implements Runnable {
	private SynchronizedDatabase synchronizedDB;
	private StorageTask task;
	private EntryResult result;
	
	public DatabaseTransaction(SynchronizedDatabase synchronizedDatabase) {
		this.synchronizedDB = synchronizedDatabase;
		this.task = null;
		this.result = null;
	}

	public DatabaseTransaction(SynchronizedDatabase synchronizedDatabase, StorageTask task) {
		this.synchronizedDB = synchronizedDatabase;
		this.task = task;
		this.result = null;
	}

	/**
	 * Sets the task of the transaction if it was not already set
	 * in the constructor.
	 * @param task task to be processed
	 */
	public void setTask(StorageTask task) {
		this.task = task;
	}

	/**
	 * Perform transaction
	 */
	public void run() {
		if (this.task == null) {
			this.result = null;
			return;
		}

		if (this.task.isWrite())
			this.synchronizedDB.setData(this.task.index(), this.task.data().getBytes());

		byte[] data = this.synchronizedDB.getData(this.task.index());
		int sequence = this.synchronizedDB.getSequence(this.task.index());

		this.result = new EntryResult(this.task.index(), new String(data), sequence);
	}

	/**
	 * If the task was not set prior to this call the return value
	 * will be {@code null}.
	 * @return result of transaction
	 */
	public EntryResult getResult() {
		return this.result;
	}
}
