package cs3410.project.familytree;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class Person extends TreeElement implements Comparator<Person> {
    public String givenName = "";
    public String familyName = "";
    public String title = "";
    public String suffix = "";
    public Set<Relationship> relationships = new HashSet<>();
    public Set<Event> events = new HashSet<>();

    public void write() throws IOException {
        if(writeLock) return;
        lock();
        Main.loadedTree.toWrite.put("person/" + id.toString() + ".txt",
                String.format("description=%s\ngivenName=%s\nfamilyName=%s\ntitle=%s\nsuffix=%s\n", description,
                        givenName, familyName, title, suffix));
        for(Relationship r : relationships) {
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
