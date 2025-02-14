package dev.concurrentdb.compute;

import dev.concurrentdb.storage.SynchronizedDatabase;


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

		// if (this.task.isWrite())
		// 	this.result = this.synchronizedDB.addData(this.task.index(), this.task.data());
		// else
		// 	this.result = this.synchronizedDB.getData(this.task.index());
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
