package cs3410.project.familytree;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Person implements Comparable<Person> {
    public UUID id;
    public boolean writeLock = false;
    public boolean drawLock = false;
    public boolean traversalLock = false;
    public String givenName = "";
    public String familyName = "";
    public String title = "";
    public String suffix = "";
    public Date birthDate;
    public Date deathDate;
    public Person mother;
    public Person father;
    public Set<Person> children = new HashSet<>();

    public Person(UUID id) {
        this.id = id;
        Main.loadedTree.orphans.add(this);
    }

    public Person() {
        this(UUID.randomUUID());
    }

    public void write() throws IOException {
        if(writeLock) return;
        writeLock();
        StringBuilder c = new StringBuilder();
        for(Person p : children) {
            c.append(",");
            c.append(p.id);
        }
        Main.loadedTree.toWrite.put((this == Main.loadedTree.root ? "r" : "") + id.toString() + ".person",
                String.format(
                        "givenName=%s\nfamilyName=%s\ntitle=%s\nsuffix=%s\nmother=%s\nfather=%s\nchildren=%s\nbirthDate=%s\ndeathDate=%s\n",
                        givenName, familyName, title, suffix, mother == null ? "null" : mother.id.toString(),
                        father == null ? "null" : father.id.toString(),
                        children.isEmpty() ? "null" : c.toString().substring(1),
                        birthDate == null ? "null" : FamilyTree.DATE_FORMAT.format(birthDate),
                        deathDate == null ? "null" : FamilyTree.DATE_FORMAT.format(deathDate)));
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
            Main.loadedTree.orphans.remove(p);
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
            Main.loadedTree.orphans.remove(p);
        }
        this.father = p;
        if(this.mother == null && this.father == null && this.children.isEmpty()
                && !this.equals(Main.loadedTree.root)) {
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

    public void traversalLock() {
        traversalLock = true;
        Main.loadedTree.traversalLocked.push(this);
    }

    @Override
    public int compareTo(Person p) {
        if(this.familyName.equalsIgnoreCase(p.familyName)) {
            return this.givenName.compareTo(p.givenName);
        } else {
            return this.familyName.compareTo(p.familyName);
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
