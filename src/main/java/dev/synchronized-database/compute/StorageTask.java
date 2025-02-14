package dev.concurrentdb.compute;


public record StorageTask(int index, String data) {
    public boolean isWrite() {
        return data != null;
    }
}
