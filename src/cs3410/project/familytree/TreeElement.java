package cs3410.project.familytree;

import java.io.IOException;
import java.util.UUID;

public abstract class TreeElement {
    public UUID id;
    public String description;
    public boolean writeLock;

    public abstract void write() throws IOException;
    
    public void lock() {
        writeLock = true;
        Main.loadedTree.writeLocked.push(this);
    }
}
