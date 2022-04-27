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
    public boolean drawLock = false;
    public String description = "";
    public String givenName = "";
    public String familyName = "";
    public String title = "";
    public String suffix = "";
    public Date birthDate;
    public Date deathDate;
    public Person mother;
    public Person father;
    public Set<Person> children = new HashSet<>();

    public Person() {
        id = UUID.randomUUID();
        Main.loadedTree.orphans.add(this);
    }

    public void write() throws IOException {
        if(writeLock) return;
        writeLock();
        // TODO write full metadata
        Main.loadedTree.toWrite.put(id.toString() + ".person",
                String.format("description=%s\ngivenName=%s\nfamilyName=%s\ntitle=%s\nsuffix=%s\n", description,
                        givenName, familyName, title, suffix));
        if(mother != null) mother.write();
        if(father != null) father.write();
        for(Person p : children) {
            p.write();
        }
    }

    public void setMother(Person p) {
        if(p == null) {
            if(this.mother != null) {
                this.mother.children.remove(this);
            }
        } else {
            p.children.add(this);
        }
        this.mother = p;
        if(this.mother == null && this.father == null && this.children.isEmpty()) {
            Main.loadedTree.orphans.add(this);
        } else {
            Main.loadedTree.orphans.remove(this);
        }
    }

    public void setFather(Person p) {
        if(p == null) {
            if(this.father != null) {
                this.father.children.remove(this);
            }
        } else {
            p.children.add(this);
        }
        this.father = p;
        if(this.mother == null && this.father == null && this.children.isEmpty()) {
            Main.loadedTree.orphans.add(this);
        } else {
            Main.loadedTree.orphans.remove(this);
        }
    }

    private void writeLock() {
        writeLock = true;
        Main.loadedTree.writeLocked.push(this);
    }

    public void drawLock() {
        drawLock = true;
        Main.loadedTree.drawLocked.push(this);
    }

    @Override
    public int compare(Person arg0, Person arg1) {
        if(arg0.familyName.equalsIgnoreCase(arg1.familyName)) {
            return arg0.givenName.compareTo(arg1.givenName);
        } else {
            return arg0.familyName.compareTo(arg1.familyName);
        }
    }

    public String getName() {
        StringBuilder sb = new StringBuilder();
        if(!givenName.isEmpty()) {
            sb.append(givenName);
        }
        if(!(givenName.isEmpty() || familyName.isEmpty())) {
            sb.append(" ");
        }
        if(!familyName.isEmpty()) {
            sb.append(familyName);
        }
        if(!suffix.isEmpty() && sb.length() > 0) {
            sb.append(" ");
        }
        if(!suffix.isEmpty()) {
            sb.append(suffix);
        }
        return sb.toString();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(!title.isEmpty()) {
            sb.append(title);
            sb.append(" ");
        }
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
        if(!suffix.isEmpty()) {
            sb.append(" ");
            sb.append(suffix);
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
