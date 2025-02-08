package dev.storage.storage;

import dev.storage.compute.EntryResult;
import dev.storage.storage.SharedDatabase;

/**
 * Database class used for conccurent acces. Wrapper over SharedDatabase.
 */
public abstract class ConcurrentDatabase {
	protected SharedDatabase sharedDB;
	protected BufferSemaphore[] semaphores;
	
	public ConcurrentDatabase(SharedDatabase sharedDatabase) {
		this.sharedDB = sharedDatabase;
		// one semaphore for every index/block in the database
		this.semaphores = new BufferSemaphore[this.sharedDB.getSize()];
	}

	public EntryResult addData(int index, String data) {
		EntryResult result = null;

		try {
			this.semaphores[index].updateSemAcquire();
		} catch (Exception e) {}

		result = this.sharedDB.addData(index, data);

		try {
			this.semaphores[index].updateSemRelease();
		} catch (Exception e) {}

		return result;
	}

	public EntryResult getData(int index) {
		EntryResult result = null;

		try {
			this.semaphores[index].readSemAcquire();
		} catch (Exception e) {}

		result = this.sharedDB.getData(index);

		try {
			this.semaphores[index].readSemRelease();
		} catch (Exception e) {}

		return result;
	}

	public int getSize() {
		return this.sharedDB.getSize();
	}
}
