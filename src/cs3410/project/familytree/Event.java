package cs3410.project.familytree;

import java.io.IOException;
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
        Main.loadedTree.toWrite.put("event/" + id.toString() + ".txt",
                String.format("description=%s\ndate=%s\n", description, DATE_FORMAT.format(date)));
    }

    @Override
    public int compare(Event arg0, Event arg1) {
        return arg0.date.compareTo(arg1.date);
    }

    public enum Type {
        BIRTH, DEATH, MARRIAGE, DIVORCE, OTHER;
    }
}
