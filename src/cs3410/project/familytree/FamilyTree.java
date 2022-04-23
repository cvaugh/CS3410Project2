package cs3410.project.familytree;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FamilyTree {
    // XXX hardcoded file for testing
    public File file = new File("test.tree");
    public Person root;
    public Stack<TreeElement> writeLocked = new Stack<>();
    public Map<String, String> toWrite = new HashMap<>();

    public void write() throws IOException {
        if(root != null) root.write();
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file));
        for(String path : toWrite.keySet()) {
            zos.putNextEntry(new ZipEntry(path));
            zos.write(toWrite.get(path).getBytes());
            zos.closeEntry();
        }
        zos.close();
        unlockAll();
    }

    public void read() throws IOException {
        // TODO
    }

    public void unlockAll() {
        while(!writeLocked.isEmpty()) {
            writeLocked.pop().writeLock = false;
        }
    }
}
