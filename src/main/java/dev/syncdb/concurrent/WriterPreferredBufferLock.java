package dev.syncdb.concurrent;

import java.util.concurrent.Semaphore;


public class WriterPreferredBufferLock implements BufferLock {
	private int active_readers = 0;
	private int active_writers = 0;
	private int waiting_readers = 0;
	private int waiting_writers = 0;
	private Semaphore enter = new Semaphore(1);
	private Semaphore readSem = new Semaphore(0);
	private Semaphore updateSem = new Semaphore(0);

	public void readLockAcquire() throws Exception {
		this.enter.acquire();

		// if there are active writers of waiting writers
		// wait until they have left
		if (this.active_writers > 0 || this.waiting_writers > 0) {
			this.waiting_readers++;
			this.enter.release();
			this.readSem.acquire();
		}

		this.active_readers++;

		// for waiting readers that enter
		if (this.waiting_readers > 0) {
			this.waiting_readers--;
			this.readSem.release();
		} else if (this.waiting_readers == 0) {
			this.enter.release();
		}
	}

	public void readLockRelease() throws Exception {
		this.enter.acquire();

		this.active_readers--;

		// if there are no more active readers
		// let writers enter
		if (this.active_readers == 0 && this.waiting_writers > 0) {
			this.waiting_writers--;
			this.updateSem.release();
		} else if (this.active_readers > 0 || this.waiting_writers == 0) {
			this.enter.release();
		}
	}

	public void updateLockAcquire() throws Exception {
		this.enter.acquire();

		// if there are reades or writers wait
		// until they have left
		if (this.active_readers > 0 || this.active_writers > 0) {
			this.waiting_writers++;
			this.enter.release();
			this.updateSem.acquire();
		}

		this.active_writers++;

		this.enter.release();
	}

	public void updateLockRelease() throws Exception {
		this.enter.acquire();

		this.active_writers--;

		// first check if there are waiting readers so every
		// thread gets the chance to entering
		if (this.waiting_readers > 0 && this.waiting_writers == 0) {
			this.waiting_readers--;
			this.readSem.release();
		// then check for waiting writers
		} else if (this.waiting_writers > 0) {
			this.waiting_writers--;
			this.updateSem.release();
		} else if (this.waiting_readers == 0 && this.waiting_writers == 0) {
			this.enter.release();
		}
	}
}
