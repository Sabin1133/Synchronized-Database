package dev.syncdb.concurrent;

import java.util.concurrent.Semaphore;


public class ReaderPriorityBufferLock implements BufferLock {
	private int active_readers = 0;
	private Semaphore readSem = new Semaphore(1);
	private Semaphore updateSem = new Semaphore(1);

	public void readLockAcquire() throws Exception {
		this.readSem.acquire();

		this.active_readers++;

		// if there is even one reader block the writer
		// until all readers have left
		if (this.active_readers == 1)
			this.updateSem.acquire();

		this.readSem.release();
	}

	public void readLockRelease() throws Exception {
		this.readSem.acquire();

		this.active_readers--;

		// let the writer enter if all readers have left
		if (this.active_readers == 0)
			this.updateSem.release();

		this.readSem.release();
	}

	public void updateLockAcquire() throws Exception {
		// wait for all readers to leave
		this.updateSem.acquire();
	}

	public void updateLockRelease() throws Exception {
		// let anyone enter
		this.updateSem.release();
	}
}
