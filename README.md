Copyright Sabin Padurariu 2024

┏━━━┓┏━━━┓┏━━━┓━━━━┏┓━━┏┓┏━━━┓┏┓━┏┓━━━━┏━━━┓┏━━━┓┏━━━┓┏━━━┓┏━━┓┏━┓━┏┓┏━━━┓━━━━┏━┓┏━┓┏━━━┓┏━━━┓
┃┏━┓┃┃┏━┓┃┃┏━━┛━━━━┃┗┓┏┛┃┃┏━┓┃┃┃━┃┃━━━━┃┏━┓┃┃┏━━┛┃┏━┓┃┗┓┏┓┃┗┫┣┛┃┃┗┓┃┃┃┏━┓┃━━━━┃┃┗┛┃┃┃┏━━┛┃┏━┓┃
┃┃━┃┃┃┗━┛┃┃┗━━┓━━━━┗┓┗┛┏┛┃┃━┃┃┃┃━┃┃━━━━┃┗━┛┃┃┗━━┓┃┃━┃┃━┃┃┃┃━┃┃━┃┏┓┗┛┃┃┃━┗┛━━━━┃┏┓┏┓┃┃┗━━┓┗┛┏┛┃
┃┗━┛┃┃┏┓┏┛┃┏━━┛━━━━━┗┓┏┛━┃┃━┃┃┃┃━┃┃━━━━┃┏┓┏┛┃┏━━┛┃┗━┛┃━┃┃┃┃━┃┃━┃┃┗┓┃┃┃┃┏━┓━━━━┃┃┃┃┃┃┃┏━━┛━━┃┏┛
┃┏━┓┃┃┃┃┗┓┃┗━━┓━━━━━━┃┃━━┃┗━┛┃┃┗━┛┃━━━━┃┃┃┗┓┃┗━━┓┃┏━┓┃┏┛┗┛┃┏┫┣┓┃┃━┃┃┃┃┗┻━┃━━━━┃┃┃┃┃┃┃┗━━┓━━┏┓━
┗┛━┗┛┗┛┗━┛┗━━━┛━━━━━━┗┛━━┗━━━┛┗━━━┛━━━━┗┛┗━┛┗━━━┛┗┛━┗┛┗━━━┛┗━━┛┗┛━┗━┛┗━━━┛━━━━┗┛┗┛┗┛┗━━━┛━━┗┛━
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

<br>

# **Concurrent Database**

### General Overview

The package includes an `ExecutorService` processig type class used for parallel execution and a Database class that solves the problem of concurrency using the reader-writer approach.

### Buffer Semaphore

The buffer semaphore is used to solve the reader-writer shared memory synchronization problem. The name has nothing to do with the fact that in this example it is used for a database "buffer" that can be "read" or "updated".

The semaphore is used if two types of threads (reader and writer) try to access a shared memory at the same time.

The read will try to read the data in memory while the writer will try to read/write data from/to the shared memory. So in other words the reader will try to inspect the current state while the writer will try to update the current state.

The second implementation of the writers' priority version uses condition variables, implemented using the java monitor of any object. The `read` and `update` semaphores, in the writers' priority version, act more like condition variables because once a sempahore is incremented one thread that was waiting on that semaphore can continue its execution. So in other words the thread is woken up and the behaviour is similar to that of a condition variable.

### The Database

The database is just a simple array of buffers (`char` arrays) that can
store strings. All buffers can be read or written to and have a sequence number used for testing the integrity of the data.

Every buffer has its own Semaphore in order for the operations
to be true parallel.

### ThreadPool

The threadpool is a processing class, similar to the `ExecutorService` class, that processes tasks in parallel.

It uses worker threads to process all submited tasks, which are placed in a queue waiting to be processed, in a true parallel manner. 

### Remarks and Conclusions

In order to implement a Concurrent Database a synchronization mechanism needs to be used. If the mechanism is used for the entire data structure,
global lock, there will be no upgrade because the writers will still use the database in a synchronized manner. In order for an increase in performance to exist there needs to be a synchronization mechanism per memory region that allows operations on different buffers
to be executed in parallel.

Also the writer threads, in general, bring a decrease in performance because they need more synchronization in order to maintain data integrity because they update the current state so even in parallel they take more time to execuite. Whereas the reader threads only read the state, operation which can be achieved in parallel with no synchronization mechanisms, meaning they are faster overall. So the time of proccessing increases with the number of writer threads.
