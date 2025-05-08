package dev.syncdb.storage;

import java.util.Arrays;

import dev.syncdb.concurrent.BufferLock;
import dev.syncdb.concurrent.ReaderPriorityBufferLock;
import dev.syncdb.concurrent.WriterPriorityBufferLock;


public final class SynchronizedBlock {
    private int sequence;
    private BufferLock lock;
    private byte[] data;

    public SynchronizedBlock(PriorityType priority) {
        this.sequence = 0;

        switch (priority) {
            case ReaderPriority:
                this.lock = new ReaderPriorityBufferLock();
                break;
            case WriterPriority:
                this.lock = new WriterPriorityBufferLock();
                break;
            default:
                this.lock = new ReaderPriorityBufferLock();
                break;
        }
    }

    public void setContent(byte[] data) {
        try {
            this.lock.updateLockAcquire();
        } catch (Exception e) {}

        this.data = Arrays.copyOf(data, data.length);

        ++this.sequence;

        try {
            this.lock.updateLockRelease();
        } catch (Exception e) {}
    }

    public byte[] getContent()
    {
        byte[] data = null;

        try {
            this.lock.readLockAcquire();
        } catch (Exception e) {}

        data = Arrays.copyOf(this.data, this.data.length);

        try {
            this.lock.readLockRelease();
        } catch (Exception e) {}

        return data;
    }

    public int getSequence() {
        return this.sequence;
    }
}
