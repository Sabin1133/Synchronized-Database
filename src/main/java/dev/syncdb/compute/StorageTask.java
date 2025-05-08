package dev.syncdb.compute;


public record StorageTask(int index, String data) {
    public boolean isWrite() {
        return data != null;
    }
}
