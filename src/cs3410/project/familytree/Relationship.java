package cs3410.project.familytree;

import java.io.IOException;
import java.util.Comparator;

public class Relationship extends TreeElement implements Comparator<Relationship> {
    public Person a;
    public Person b;
    public Type type = Type.UNKNOWN;
    public Modifier modifier = Modifier.NONE;

    public void write() throws IOException {
        if(writeLock) return;
        lock();
        Main.loadedTree.toWrite.put("relationship/" + id.toString() + ".txt", String
                .format("description=%s\na=%s\nb=%s\ntype=%s\nmodifier=%s\n", description, a.id, b.id, type, modifier));
        a.write();
        b.write();
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
