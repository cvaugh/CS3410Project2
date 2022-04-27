package cs3410.project.familytree.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;

import cs3410.project.familytree.Main;
import cs3410.project.familytree.Person;

public abstract class PersonSelectDialog extends JDialog {

    public PersonSelectDialog() {
        setTitle("Select Person");
        setResizable(false);
        setModal(true);
        setSize(300, 600);
        setLocationRelativeTo(null);
        setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

        final Person[] people = new Person[Main.loadedTree.getSize()];
        int i = 0;
        for(Person p : Main.loadedTree.getPeople()) {
            people[i] = p;
            i++;
        }
        JList<Person> list = new JList<>(people);
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    int index = list.locationToIndex(e.getPoint());
                    if(index == -1) return;
                    onClose(people[index]);
                    PersonSelectDialog.this
                            .dispatchEvent(new WindowEvent(PersonSelectDialog.this, WindowEvent.WINDOW_CLOSING));
                }
            }

        });
        JScrollPane scroll = new JScrollPane(list);
        scroll.setViewportView(list);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scroll);
    }

    public abstract void onClose(Person clicked);
}