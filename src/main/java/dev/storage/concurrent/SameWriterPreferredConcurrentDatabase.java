package dev.storage.concurrent;

import java.util.concurrent.Semaphore;

import dev.storage.storage.SharedDatabase;

import dev.storage.storage.ConcurrentDatabase;

public class SameWriterPreferredConcurrentDatabase extends ConcurrentDatabase {
	public SameWriterPreferredConcurrentDatabase(SharedDatabase sharedDatabase) {
		super(sharedDatabase);

		for (int i = 0; i < this.semaphores.length; ++i)
			this.semaphores[i] = new BufferSemaphore() {
				private int active_readers = 0;
				private int active_writers = 0;
				private int waiting_readers = 0;
				private int waiting_writers = 0;
				private Semaphore enter = new Semaphore(1);
				private Object readCV = new Object();
				private Object updateCV = new Object();

				public void readSemAcquire() throws Exception {
					this.enter.acquire();

					if (this.active_writers > 0 || this.waiting_writers > 0) {
						this.waiting_readers++;
						this.enter.release();

						synchronized (this.readCV) {
							this.readCV.wait();
						}
					}

					this.active_readers++;

					if (this.waiting_readers > 0) {
						this.waiting_readers--;

						synchronized (this.readCV) {
							this.readCV.notify();
						}
					} else if (this.waiting_readers == 0) {
						this.enter.release();
					}
				}
			
				public void readSemRelease() throws Exception {
					this.enter.acquire();

					this.active_readers--;

					if (this.active_readers == 0 && this.waiting_writers > 0) {
						this.waiting_writers--;

						synchronized (this.updateCV) {
							this.updateCV.notify();
						}
					} else if (this.active_readers > 0 || this.waiting_writers == 0) {
						this.enter.release();
					}
				}
			
				public void updateSemAcquire() throws Exception {
					this.enter.acquire();

					if (this.active_readers > 0 || this.active_writers > 0) {
						this.waiting_writers++;
						this.enter.release();

						synchronized (this.updateCV) {
							this.updateCV.wait();
						}
					}

					this.active_writers++;

					this.enter.release();
				}
			
				public void updateSemRelease() throws Exception {
					this.enter.acquire();

					this.active_writers--;

					if (this.waiting_readers > 0 && this.waiting_writers == 0) {
						this.waiting_readers--;

						synchronized (this.readCV) {
	        				this.readCV.notify();
						}
					} else if (this.waiting_writers > 0) {
						this.waiting_writers--;

						synchronized (this.updateCV) {
	        				this.updateCV.notify();
						}
					} else if (this.waiting_readers == 0 && this.waiting_writers == 0) {
						this.enter.release();
					}
				}
			};
	}
}
