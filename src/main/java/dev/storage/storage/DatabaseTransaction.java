package dev.storage.storage;

import dev.storage.storage.ConcurrentDatabase;
import dev.storage.compute.StorageTask;

import dev.storage.compute.EntryResult;

/**
 * A class that implements runnable and performs a transaction
 * on th provided database. 
 */
public class DatabaseTransaction implements Runnable {
	private ConcurrentDatabase concurrentDB;
	private StorageTask task;
	private EntryResult result;
	
	public DatabaseTransaction(ConcurrentDatabase concurrentDatabase) {
		this.concurrentDB = concurrentDatabase;
		this.task = null;
		this.result = null;
	}

	public DatabaseTransaction(ConcurrentDatabase concurrentDatabase, StorageTask task) {
		this.concurrentDB = concurrentDatabase;
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
			this.result = this.concurrentDB.addData(this.task.index(), this.task.data());
		else
			this.result = this.concurrentDB.getData(this.task.index());
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
