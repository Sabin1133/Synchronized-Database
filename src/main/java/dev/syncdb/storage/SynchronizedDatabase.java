package dev.syncdb.storage;


public final class SynchronizedDatabase {
    private final SynchronizedBlock[] blocks;

    public SynchronizedDatabase(PriorityType priority, int size) {
        this.blocks =  new SynchronizedBlock[size];

        for (int i = 0; i < size; ++i)
            this.blocks[i] = new SynchronizedBlock(priority);
    }

    public void setData(int index, byte[] data) {
        this.blocks[index].setContent(data);
    }

    public byte[] getData(int index) {
        return this.blocks[index].getContent();
    }

    public int getSize() {
        return this.blocks.length;
    }
}
