package cs3410.project.familytree;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Comparator;

public class Relationship extends TreeElement implements Comparator<Relationship> {
    public Person a;
    public Person b;
    public Type type = Type.UNKNOWN;
    public Modifier modifier = Modifier.NONE;

    public void write() throws IOException {
        if(writeLock) return;
        lock();
        File parent = new File(Main.loadedTree.directory, "relationship");
        if(!parent.exists()) parent.mkdir();
        File file = new File(parent, id.toString() + ".txt");
        String out = String.format("description=%s\na=%s\nb=%s\ntype=%s\nmodifier=%s\n", description, a.id, b.id, type,
                modifier);
        Files.write(file.toPath(), out.getBytes());
    }

    public enum Type {
        UNKNOWN, PARENT, CHILD, SPOUSE, SIBLING;
    }

    public enum Modifier {
        NONE, ADOPTED, MARRIED, DIVORCED;
    }

    @Override
    public int compare(Relationship o1, Relationship o2) {
        // TODO
        return 0;
    }
}
