package dev.storage.compute;

/* DO NOT MODIFY */
public record StorageTask(int index, String data) {
    public boolean isWrite() {
        return data != null;
    }
}
