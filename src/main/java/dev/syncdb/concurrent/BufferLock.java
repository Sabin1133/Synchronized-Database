package dev.syncdb.concurrent;


/**
 * Interface that is used by object that solve the
 * reader-writer shared memory synchronization problem.
 * 
 * The two locks are called {@code read} (reader) and {@code update}
 * (writer) because the reader only reads/queries the current state of the
 * shared memory while the writer, besides reading, can also write
 * and therefore change/update the current state of the data in the memory
 */
public interface BufferLock {
	void readSemAcquire() throws Exception;
	void readSemRelease() throws Exception;

	void updateSemAcquire() throws Exception;
	void updateSemRelease() throws Exception;
}
