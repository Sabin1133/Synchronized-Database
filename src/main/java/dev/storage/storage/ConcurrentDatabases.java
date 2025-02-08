package dev.storage.storage;

import dev.storage.storage.LockType;
import dev.storage.storage.SharedDatabase;

/**
 * Class with static methods for creating {@code ConcurrentDatabase} objects
 */
public class ConcurrentDatabases {
	public static ConcurrentDatabase newDatabase(SharedDatabase sharedDatabase, LockType type) {
		ConcurrentDatabase concurrentDB = null;

		switch (type) {
		case LockType.ReaderPreferred:
			concurrentDB = new ReaderPreferredConcurrentDatabase(sharedDatabase);
			break;
		case LockType.WriterPreferred1:
			concurrentDB = new WriterPreferredConcurrentDatabase(sharedDatabase);
			break;
		case LockType.WriterPreferred2:
			concurrentDB = new SameWriterPreferredConcurrentDatabase(sharedDatabase);
			break;
		default:
			break;
		}
		
		return concurrentDB;
	}
}
