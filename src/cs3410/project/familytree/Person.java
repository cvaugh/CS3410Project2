package cs3410.project.familytree;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class Person extends TreeElement implements Comparator<Person> {
    public String givenName = "";
    public String familyName = "";
    public String title = "";
    public String suffix = "";
    public Relationship mother;
    public Relationship father;
    public Set<Relationship> spouses = new HashSet<>();
    public Set<Relationship> children = new HashSet<>();
    public Set<Relationship> otherRelations = new HashSet<>();
    public Set<Event> events = new HashSet<>();

    public void write() throws IOException {
        if(writeLock) return;
        lock();
        File parent = new File(Main.loadedTree.directory, "person");
        if(!parent.exists()) parent.mkdir();
        File file = new File(parent, id.toString() + ".txt");
        String out = String.format("description=%s\ngivenName=%s\nfamilyName=%s\ntitle=%s\nsuffix=%s\n", description,
                givenName, familyName, title, suffix);
        Files.write(file.toPath(), out.getBytes());
        mother.write();
        father.write();
        for(Relationship r : spouses) {
            r.write();
        }
        for(Relationship r : children) {
            r.write();
        }
        for(Relationship r : otherRelations) {
            r.write();
        }
        for(Event e : events) {
            e.write();
        }
    }

    @Override
    public int compare(Person arg0, Person arg1) {
        if(arg0.familyName.equalsIgnoreCase(arg1.familyName)) {
            return arg0.givenName.compareTo(arg1.givenName);
        } else {
            return arg0.familyName.compareTo(arg1.familyName);
        }
    }
}
