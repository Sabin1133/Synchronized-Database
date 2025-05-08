package dev.syncdb.compute;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * A class similar to {@code ExecutorService}, used for parallel
 * processing of tasks
 */
public class Threadpool {
	private WorkerThread[] threads;
	private LinkedBlockingQueue<Runnable> tasks;
	private AtomicInteger workingThreads;

	/**
	 * Sets the number of threads and also starts them.
	 * 
	 * @param numThreads the number of worker threads in the Pool
	 */
	public Threadpool(int numThreads) {
		this.threads = new WorkerThread[numThreads];
		this.tasks = new LinkedBlockingQueue<>();
		this.workingThreads = new AtomicInteger(0);

		for (int i = 0; i < numThreads; ++i) {
			this.threads[i] = new WorkerThread(this.tasks, this.workingThreads);
			this.threads[i].start();
		}	
	}

	/**
	 * Add a task to the processing queue.
	 *
	 * @param task to be processed
	 */
	public void submit(Runnable task) {
		this.tasks.add(task);
	}

	/**
	 * Shuts down the worker threads. The threads will no longer
	 * process any tasks and are waited until they finish the current task.
	 */
	public void shutdownService() {
		for (var thread : this.threads)
			try {
				thread.interrupt();
				thread.join();
			} catch (Exception e) {}
	}

	/**
	 * Shuts down the worker threads. The threads are waited
	 * until they finish all the tasks in the queue, even though
	 * additional tasks might be added.
	 */
	public void shutdownServiceWait() {
		while (!this.tasks.isEmpty() || this.workingThreads.get() > 0) {}

		for (var thread : this.threads)
			try {
				thread.interrupt();
				thread.join();
			} catch (Exception e) {}
	}
}


class WorkerThread extends Thread {
	private LinkedBlockingQueue<Runnable> tasks;
	private AtomicInteger threadpoolWorkingThreads;

	public WorkerThread(LinkedBlockingQueue<Runnable> tpTasks, AtomicInteger threadpoolWorkingThreads) {
		this.tasks = tpTasks;
		this.threadpoolWorkingThreads = threadpoolWorkingThreads;
	}
	
	public void run() {
		Runnable task = null;

		while (true) {
			try {
				task = this.tasks.take();
			} catch (Exception e) {
				break;
			}

			// signal the ThreadPool that the current thread is working
			this.threadpoolWorkingThreads.incrementAndGet();
			task.run();
			this.threadpoolWorkingThreads.decrementAndGet();
		}
	}
}
