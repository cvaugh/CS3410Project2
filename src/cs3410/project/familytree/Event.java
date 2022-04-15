package cs3410.project.familytree;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class Event extends TreeElement implements Comparator<Event> {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    public Date date;

    public void write() throws IOException {
        if(writeLock) return;
        lock();
        File parent = new File(Main.loadedTree.directory, "event");
        if(!parent.exists()) parent.mkdir();
        File file = new File(parent, id.toString() + ".txt");
        String out = String.format("description=%s\ndate=%s\n", description, DATE_FORMAT.format(date));
        Files.write(file.toPath(), out.getBytes());
    }

    @Override
    public int compare(Event arg0, Event arg1) {
        return arg0.date.compareTo(arg1.date);
    }

    public enum Type {
        BIRTH, DEATH, MARRIAGE, DIVORCE, OTHER;
    }
}
