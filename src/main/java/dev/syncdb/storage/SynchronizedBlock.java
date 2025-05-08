package dev.syncdb.storage;

import java.util.Arrays;

import dev.syncdb.concurrent.BufferLock;
import dev.syncdb.concurrent.ReaderPreferredBufferLock;
import dev.syncdb.concurrent.WriterPreferredBufferLock;


public final class SynchronizedBlock {
    private int sequence;
    private BufferLock lock;
    private byte[] data;

    public SynchronizedBlock(PriorityType priority) {
        this.sequence = 0;

        switch (priority) {
            case ReaderPriority:
                this.lock = new ReaderPreferredBufferLock();
                break;
            case WriterPriority:
                this.lock = new WriterPreferredBufferLock();
                break;
            default:
                this.lock = new ReaderPreferredBufferLock();
                break;
        }
    }

    public void setContent(byte[] data) {
        try {
            this.lock.updateSemAcquire();
        } catch (Exception e) {}

        this.data = Arrays.copyOf(data, data.length);

        ++this.sequence;

        try {
            this.lock.updateSemRelease();
        } catch (Exception e) {}
    }

    public byte[] getContent()
    {
        byte[] data = null;

        try {
            this.lock.readSemAcquire();
        } catch (Exception e) {}

        data = Arrays.copyOf(this.data, this.data.length);

        try {
            this.lock.readSemRelease();
        } catch (Exception e) {}

        return data;
    }

    public int getSequence() {
        return this.sequence;
    }
}
