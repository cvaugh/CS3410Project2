package cs3410.project.familytree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class FamilyTree {
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final DateFormat YEAR_FORMAT = new SimpleDateFormat("yyyy");
    public File file;
    public Person root;
    public Set<Person> orphans = new HashSet<>();
    public Stack<Person> writeLocked = new Stack<>();
    public Stack<Person> drawLocked = new Stack<>();
    public Stack<Person> traversalLocked = new Stack<>();
    public Map<String, String> toWrite = new HashMap<>();

    public FamilyTree(File file) {
        this.file = file;
    }

    public void write() throws IOException {
        if(root != null) root.write();
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file));
        for(String path : toWrite.keySet()) {
            zos.putNextEntry(new ZipEntry(path));
            zos.write(toWrite.get(path).getBytes());
            zos.closeEntry();
        }
        zos.close();
        writeUnlockAll();
    }

    public void read() throws IOException {
        ZipFile zip = new ZipFile(file);
        Enumeration<? extends ZipEntry> entries = zip.entries();
        Map<String, Person> toAdd = new HashMap<>();
        Map<String, String> motherIds = new HashMap<>();
        Map<String, String> fatherIds = new HashMap<>();
        Map<String, List<String>> childIds = new HashMap<>();
        while(entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            InputStream in = zip.getInputStream(entry);
            String content = new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
            in.close();
            boolean isRoot = false;
            String id = entry.getName().split("\\.")[0];
            if(id.startsWith("r")) {
                id = id.substring(1);
                isRoot = true;
            }
            if(content.isEmpty()) continue;
            Person person = new Person(UUID.fromString(id));
            String[] lines = content.split("\n");
            person.givenName = lines[0].split("=", 2)[1];
            person.familyName = lines[1].split("=", 2)[1];
            person.title = lines[2].split("=", 2)[1];
            person.suffix = lines[3].split("=", 2)[1];
            motherIds.put(id, lines[4].split("=", 2)[1]);
            fatherIds.put(id, lines[5].split("=", 2)[1]);
            String children = lines[6].split("=", 2)[1];
            childIds.put(id, new ArrayList<>());
            if(!children.equals("null")) {
                for(String child : children.split(",")) {
                    childIds.get(id).add(child);
                }
            }
            String birthDate = lines[7].split("=", 2)[1];
            if(!birthDate.equals("null")) {
                try {
                    person.birthDate = FamilyTree.DATE_FORMAT.parse(birthDate);
                } catch(ParseException e) {
                    e.printStackTrace();
                }
            }
            String deathDate = lines[8].split("=", 2)[1];
            if(!deathDate.equals("null")) {
                try {
                    person.deathDate = FamilyTree.DATE_FORMAT.parse(deathDate);
                } catch(ParseException e) {
                    e.printStackTrace();
                }
            }
            toAdd.put(id, person);
            if(isRoot) root = person;
        }
        zip.close();
        for(String id : toAdd.keySet()) {
            toAdd.get(id).setMother(toAdd.get(motherIds.get(id)));
            toAdd.get(id).setFather(toAdd.get(fatherIds.get(id)));
            for(String child : childIds.get(id)) {
                toAdd.get(id).children.add(toAdd.get(child));
            }
        }
    }

    public static void open(File file) throws IOException {
        FamilyTree tree = new FamilyTree(file);
        Main.loadedTree = tree;
        tree.read();
    }

    public Set<Person> getPeople(boolean includeOrphaned) {
        Set<Person> set = new HashSet<>();
        traverse(p -> {
            set.add(p);
        });
        if(includeOrphaned) set.addAll(orphans);
        return set;
    }

    public int getSize(boolean includeOrphaned) {
        return getPeople(includeOrphaned).size();
    }

    public void traverse(TraversalAction action) {
        traverse(action, true, true);
    }

    public void traverse(TraversalAction action, boolean upward, boolean downward) {
        traverse(action, root, upward, downward);
    }

    public void traverse(TraversalAction action, Person start, boolean upward, boolean downward) {
        traverseRecursive(action, start, upward, downward);
        traversalUnlockAll();
    }

    private void traverseRecursive(TraversalAction action, Person person, boolean upward, boolean downward) {
        if(person == null || person.traversalLock) return;
        person.traversalLock();
        action.execute(person);
        if(upward) {
            traverseRecursive(action, person.mother, upward, downward);
            traverseRecursive(action, person.father, upward, downward);
        }
        if(downward) {
            for(Person child : person.children) {
                traverseRecursive(action, child, upward, downward);
            }
        }
    }

    public void writeUnlockAll() {
        while(!writeLocked.isEmpty()) {
            writeLocked.pop().writeLock = false;
        }
    }

    public void drawUnlockAll() {
        while(!drawLocked.isEmpty()) {
            drawLocked.pop().drawLock = false;
        }
    }

    private void traversalUnlockAll() {
        while(!traversalLocked.isEmpty()) {
            traversalLocked.pop().traversalLock = false;
        }
    }
}
