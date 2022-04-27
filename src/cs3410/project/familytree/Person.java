package cs3410.project.familytree;

import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Person implements Comparator<Person> {
    public UUID id;
    public boolean writeLock = false;
    public String description = "";
    public String givenName = "";
    public String familyName = "";
    public String title = "";
    public String suffix = "";
    public Date birthDate;
    public Date deathDate;
    public Person mother;
    public Person father;
    public Set<Person> spouses = new HashSet<>();
    public Set<Person> children = new HashSet<>();

    public Person() {
        id = UUID.randomUUID();
    }

    public void write() throws IOException {
        if(writeLock) return;
        lock();
        // TODO write full metadata
        Main.loadedTree.toWrite.put(id.toString() + ".person",
                String.format("description=%s\ngivenName=%s\nfamilyName=%s\ntitle=%s\nsuffix=%s\n", description,
                        givenName, familyName, title, suffix));
        mother.write();
        father.write();
        for(Person p : spouses) {
            p.write();
        }
        for(Person p : children) {
            p.write();
        }
    }

    private void lock() {
        writeLock = true;
        Main.loadedTree.writeLocked.push(this);
    }

    @Override
    public int compare(Person arg0, Person arg1) {
        if(arg0.familyName.equalsIgnoreCase(arg1.familyName)) {
            return arg0.givenName.compareTo(arg1.givenName);
        } else {
            return arg0.familyName.compareTo(arg1.familyName);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(givenName.isEmpty() && familyName.isEmpty()) {
            sb.append("Unnamed");
        } else {
            if(!givenName.isEmpty()) {
                sb.append(givenName);
            }
            if(!familyName.isEmpty()) {
                if(!givenName.isEmpty()) sb.append(" ");
                sb.append(familyName);
            }
        }
        sb.append(" (");
        if(birthDate != null) {
            sb.append(FamilyTree.YEAR_FORMAT.format(birthDate));
        }
        sb.append("-");
        if(deathDate != null) {
            sb.append(FamilyTree.YEAR_FORMAT.format(deathDate));
        }
        sb.append(")");
        return sb.toString();
    }
}
