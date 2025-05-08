Copyright (c) 2024 Sabin Padurariu

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
━━━┏━━━┓┏━━━┓┏━━━┓━━━━┏┓━━┏┓┏━━━┓┏┓━┏┓━━━━┏━━━┓┏━━━┓┏━━━┓┏━━━┓┏━━┓┏━┓━┏┓┏━━━┓━━━
━━━┃┏━┓┃┃┏━┓┃┃┏━━┛━━━━┃┗┓┏┛┃┃┏━┓┃┃┃━┃┃━━━━┃┏━┓┃┃┏━━┛┃┏━┓┃┗┓┏┓┃┗┫┣┛┃┃┗┓┃┃┃┏━┓┃━━━
━━━┃┃━┃┃┃┗━┛┃┃┗━━┓━━━━┗┓┗┛┏┛┃┃━┃┃┃┃━┃┃━━━━┃┗━┛┃┃┗━━┓┃┃━┃┃━┃┃┃┃━┃┃━┃┏┓┗┛┃┃┃━┗┛━━━
━━━┃┗━┛┃┃┏┓┏┛┃┏━━┛━━━━━┗┓┏┛━┃┃━┃┃┃┃━┃┃━━━━┃┏┓┏┛┃┏━━┛┃┗━┛┃━┃┃┃┃━┃┃━┃┃┗┓┃┃┃┃┏━┓━━━
━━━┃┏━┓┃┃┃┃┗┓┃┗━━┓━━━━━━┃┃━━┃┗━┛┃┃┗━┛┃━━━━┃┃┃┗┓┃┗━━┓┃┏━┓┃┏┛┗┛┃┏┫┣┓┃┃━┃┃┃┃┗┻━┃━━━
━━━┗┛━┗┛┗┛┗━┛┗━━━┛━━━━━━┗┛━━┗━━━┛┗━━━┛━━━━┗┛┗━┛┗━━━┛┗┛━┗┛┗━━━┛┗━━┛┗┛━┗━┛┗━━━┛━━━
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┏━┓┏━┓┏━━━┓━━━━┏━━━┓━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┃┃┗┛┃┃┃┏━━┛━━━━┃┏━┓┃━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┃┏┓┏┓┃┃┗━━┓━━━━┗┛┏┛┃━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┃┃┃┃┃┃┃┏━━┛━━━━━━┃┏┛━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┃┃┃┃┃┃┃┗━━┓━━━━━━┏┓━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┗┛┗┛┗┛┗━━━┛━━━━━━┗┛━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

# Synchronized Database

## General Overview

Basic implementation of an in-memory database that stores data of UTF-8 format
from concurrent queries.

## Functionality

The sole purpose of the database is handling concurrent queries that would
otherwise make storing data impossible because of race conditions.

The database solves the problem of race condition created by concurrent queries
by leveraging the reader-writer approach.

A synchronized database requires a synchronization mechanism. If the mechanism
is used on the entire data structure, like a global lock, rather than on
individual components, there is no performance upgrade because the writers still
update the database in a synchronized manner. In order for an increase in
performance there needs to be a synchronization mechanism per buffer. In this
way operations on different buffers are executed in parallel.

## Implementation

The package includes classes for processing queries and storing data.

### Buffer Lock

The buffer Lock is used to solve the reader-writer shared memory problem and
has a reader priority implementation and a writer priority implementation.

The Lock is used when two types of threads (reader and writer) try to access
a shared buffer at the same time. The reader tries to read the data in the
buffer while the writer tries to read/write data from/to the shared buffer. So
in other words the reader tries to see the current state while the writer tries
to update the current state.

The implementation uses a synchronization mechanism called condition variable,
implemented using semaphores. The `read` and `update` semaphores act more like
condition variables because once a sempahore is incremented one thread that was
waiting on that semaphore can continue its execution. So in other words the
thread is notified woken up (notified) and continues its execution, behaviour
that is similar to that of a condition variable.

### In-memory Database

The database is just a simple collection of blocks that can be indexed. A block
holds a buffer for strings and a buffer lock for synchronization.
In this way writers can write to different blocks at the same time without
blocking the entire database. The blocks also have a sequence number used for
testing the integrity of the data.

### Thread Pool

The threadpool is a processing class, similar to the `ExecutorService` class,
that processes tasks in parallel.

It uses worker threads to process all submited tasks, which are placed in a
queue waiting to be executed, in parallel. 
