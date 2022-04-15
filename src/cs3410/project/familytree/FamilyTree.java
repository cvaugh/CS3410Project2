package cs3410.project.familytree;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class FamilyTree {
    // XXX hardcoded directory for testing
    public File directory = new File("tree");
    public List<Person> people = new ArrayList<>();
    public Stack<TreeElement> writeLocked = new Stack<>();

    public void write() throws IOException {
        if(!directory.exists()) directory.mkdir();
        for(Person p : people) {
            p.write();
        }
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
