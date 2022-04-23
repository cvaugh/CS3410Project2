package cs3410.project.familytree.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.BevelBorder;

import cs3410.project.familytree.Person;

public class PersonEditorPanel extends JPanel {
    private final SpringLayout layout = new SpringLayout();
    private final JLabel id = new JLabel("ID:");
    private final JTextField givenName = new JTextField();
    private final JTextField familyName = new JTextField();
    private final JTextField title = new JTextField();
    private final JTextField suffix = new JTextField();
    private final JButton addRelationship = new JButton("Add Relationship");
    private final JButton addEvent = new JButton("Add Event");
    private final JButton delete = new JButton("Delete");

    public PersonEditorPanel() {
        super();
        setLayout(layout);
        setPreferredSize(new Dimension(450, 200));
        setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        id.setPreferredSize(new Dimension(400, 10));
        id.setForeground(Color.GRAY);
        // id.setFont(id.getFont().deriveFont(9.0f));
        id.setFont(new Font("Courier New", Font.PLAIN, 9));
        layout.putConstraint(SpringLayout.WEST, id, 5, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.NORTH, id, 5, SpringLayout.NORTH, this);
        add(id);
        JLabel givenNameLabel = new JLabel("Given Name:");
        givenNameLabel.setPreferredSize(new Dimension(80, 30));
        layout.putConstraint(SpringLayout.WEST, givenNameLabel, 5, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.NORTH, givenNameLabel, 5, SpringLayout.SOUTH, id);
        add(givenNameLabel);
        givenName.setPreferredSize(new Dimension(200, 30));
        layout.putConstraint(SpringLayout.WEST, givenName, 0, SpringLayout.EAST, givenNameLabel);
        layout.putConstraint(SpringLayout.NORTH, givenName, 5, SpringLayout.SOUTH, id);
        add(givenName);

        JLabel familyNameLabel = new JLabel("Family Name:");
        familyNameLabel.setPreferredSize(new Dimension(80, 30));
        layout.putConstraint(SpringLayout.WEST, familyNameLabel, 5, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.NORTH, familyNameLabel, 10, SpringLayout.SOUTH, givenNameLabel);
        add(familyNameLabel);
        familyName.setPreferredSize(new Dimension(200, 30));
        layout.putConstraint(SpringLayout.WEST, familyName, 0, SpringLayout.EAST, familyNameLabel);
        layout.putConstraint(SpringLayout.NORTH, familyName, 10, SpringLayout.SOUTH, givenName);
        add(familyName);

        JLabel titleLabel = new JLabel("Title:");
        titleLabel.setPreferredSize(new Dimension(40, 30));
        layout.putConstraint(SpringLayout.WEST, titleLabel, 20, SpringLayout.EAST, givenName);
        layout.putConstraint(SpringLayout.NORTH, titleLabel, 5, SpringLayout.SOUTH, id);
        add(titleLabel);
        title.setPreferredSize(new Dimension(70, 30));
        layout.putConstraint(SpringLayout.WEST, title, 0, SpringLayout.EAST, titleLabel);
        layout.putConstraint(SpringLayout.NORTH, title, 5, SpringLayout.SOUTH, id);
        add(title);

        JLabel suffixLabel = new JLabel("Suffix:");
        suffixLabel.setPreferredSize(new Dimension(40, 30));
        layout.putConstraint(SpringLayout.WEST, suffixLabel, 20, SpringLayout.EAST, familyName);
        layout.putConstraint(SpringLayout.NORTH, suffixLabel, 10, SpringLayout.SOUTH, titleLabel);
        add(suffixLabel);
        suffix.setPreferredSize(new Dimension(70, 30));
        layout.putConstraint(SpringLayout.WEST, suffix, 0, SpringLayout.EAST, suffixLabel);
        layout.putConstraint(SpringLayout.NORTH, suffix, 10, SpringLayout.SOUTH, title);
        add(suffix);

        addRelationship.setPreferredSize(new Dimension(130, 30));
        addRelationship.addActionListener(e -> {
            // TODO
        });
        layout.putConstraint(SpringLayout.WEST, addRelationship, 5, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.SOUTH, addRelationship, -5, SpringLayout.SOUTH, this);
        add(addRelationship);

        addEvent.setPreferredSize(new Dimension(130, 30));
        addEvent.addActionListener(e -> {
            // TODO
        });
        layout.putConstraint(SpringLayout.WEST, addEvent, 5, SpringLayout.EAST, addRelationship);
        layout.putConstraint(SpringLayout.SOUTH, addEvent, -5, SpringLayout.SOUTH, this);
        add(addEvent);

        delete.setPreferredSize(new Dimension(130, 30));
        delete.addActionListener(e -> {
            // TODO
        });
        layout.putConstraint(SpringLayout.WEST, delete, 5, SpringLayout.EAST, addEvent);
        layout.putConstraint(SpringLayout.SOUTH, delete, -5, SpringLayout.SOUTH, this);
        add(delete);
    }

    private void update(Person person) {
        id.setText("ID: " + person.id);
        givenName.setText(person.givenName);
        familyName.setText(person.familyName);
        title.setText(person.title);
        suffix.setText(person.suffix);
    }
}
