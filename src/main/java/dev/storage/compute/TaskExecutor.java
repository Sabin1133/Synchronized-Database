package dev.storage.compute;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import dev.storage.storage.ConcurrentDatabase;
import dev.storage.storage.ConcurrentDatabases;
import dev.storage.storage.DatabaseTransaction;
import dev.storage.storage.LockType;
import dev.storage.storage.SharedDatabase;

/* DO NOT MODIFY THE METHODS SIGNATURES */
public class TaskExecutor {
    private final SharedDatabase sharedDatabase;

    public TaskExecutor(int storageSize, int blockSize, long readDuration, long writeDuration) {
        sharedDatabase = new SharedDatabase(storageSize, blockSize, readDuration, writeDuration);
    }

    public List<EntryResult> ExecuteWork(int numberOfThreads, List<StorageTask> tasks, LockType lockType) {
        /* IMPLEMENT HERE THE THREAD POOL, ASSIGN THE TASKS AND RETURN THE RESULTS */
        ConcurrentDatabase concurrentDatabase = ConcurrentDatabases.newDatabase(sharedDatabase, lockType);
		List<DatabaseTransaction> transactions = new ArrayList<>();
		Threadpool tp = new Threadpool(numberOfThreads);

		for (var task : tasks)
			transactions.add(new DatabaseTransaction(concurrentDatabase, task));

		transactions.stream().forEach(tp::submit);
		
		tp.shutdownServiceWait();

		return transactions.stream().map(DatabaseTransaction::getResult).collect(Collectors.toList());
	}

    public List<EntryResult> ExecuteWorkSerial(List<StorageTask> tasks) {
        var results = tasks.stream().map(task -> {
            try {
                if (task.isWrite()) {
                    return sharedDatabase.addData(task.index(), task.data());
                } else {
                    return sharedDatabase.getData(task.index());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).toList();

        return results.stream().toList();
    }
}
