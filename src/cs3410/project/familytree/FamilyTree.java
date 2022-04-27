package cs3410.project.familytree;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FamilyTree {
    public static final DateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy");
    // XXX hardcoded file for testing
    public File file = new File("test.tree");
    public Person root;
    public Stack<Person> writeLocked = new Stack<>();
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

    public Set<Person> getPeople() {
        Set<Person> set = new HashSet<>();
        addRecursive(root, set);
        return set;
    }

    public int getSize() {
        return getPeople().size();
    }

    private static void addRecursive(Person person, Set<Person> set) {
        if(person == null || set.contains(person)) return;
        set.add(person);
        addRecursive(person.mother, set);
        addRecursive(person.father, set);
        for(Person p : person.spouses) {
            addRecursive(p, set);
        }
        for(Person p : person.children) {
            addRecursive(p, set);
        }
    }

    public void unlockAll() {
        while(!writeLocked.isEmpty()) {
            writeLocked.pop().writeLock = false;
        }
    }
}
