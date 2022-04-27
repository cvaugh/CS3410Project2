package cs3410.project.familytree.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import cs3410.project.familytree.Person;

public class PersonEditorPanel extends JPanel {
    final Person person;
    private final KeyListener keyListener = new KeyAdapter() {
        @Override
        public void keyReleased(KeyEvent e) {
            save();
        }
    };
    private final SpringLayout layout = new SpringLayout();
    private final JLabel id = new JLabel("ID:");
    private final JTextField givenName = new JTextField();
    private final JTextField familyName = new JTextField();
    private final JTextField title = new JTextField();
    private final JTextField suffix = new JTextField();
    private final JButton mother = new JButton("Click to set");
    private final JButton father = new JButton("Click to set");

    public PersonEditorPanel(Person person) {
        this.person = person;
        setLayout(layout);
        id.setPreferredSize(new Dimension(400, 10));
        id.setForeground(Color.GRAY);
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
        givenName.addKeyListener(keyListener);
        layout.putConstraint(SpringLayout.WEST, givenName, 0, SpringLayout.EAST, givenNameLabel);
        layout.putConstraint(SpringLayout.NORTH, givenName, 5, SpringLayout.SOUTH, id);
        add(givenName);

        JLabel familyNameLabel = new JLabel("Family Name:");
        familyNameLabel.setPreferredSize(new Dimension(80, 30));
        layout.putConstraint(SpringLayout.WEST, familyNameLabel, 5, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.NORTH, familyNameLabel, 10, SpringLayout.SOUTH, givenNameLabel);
        add(familyNameLabel);
        familyName.setPreferredSize(new Dimension(200, 30));
        familyName.addKeyListener(keyListener);
        layout.putConstraint(SpringLayout.WEST, familyName, 0, SpringLayout.EAST, familyNameLabel);
        layout.putConstraint(SpringLayout.NORTH, familyName, 10, SpringLayout.SOUTH, givenName);
        add(familyName);

        JLabel titleLabel = new JLabel("Title:");
        titleLabel.setPreferredSize(new Dimension(40, 30));
        layout.putConstraint(SpringLayout.WEST, titleLabel, 20, SpringLayout.EAST, givenName);
        layout.putConstraint(SpringLayout.NORTH, titleLabel, 5, SpringLayout.SOUTH, id);
        add(titleLabel);
        title.setPreferredSize(new Dimension(70, 30));
        title.addKeyListener(keyListener);
        layout.putConstraint(SpringLayout.WEST, title, 0, SpringLayout.EAST, titleLabel);
        layout.putConstraint(SpringLayout.NORTH, title, 5, SpringLayout.SOUTH, id);
        add(title);

        JLabel suffixLabel = new JLabel("Suffix:");
        suffixLabel.setPreferredSize(new Dimension(40, 30));
        layout.putConstraint(SpringLayout.WEST, suffixLabel, 20, SpringLayout.EAST, familyName);
        layout.putConstraint(SpringLayout.NORTH, suffixLabel, 10, SpringLayout.SOUTH, titleLabel);
        add(suffixLabel);
        suffix.setPreferredSize(new Dimension(70, 30));
        suffix.addKeyListener(keyListener);
        layout.putConstraint(SpringLayout.WEST, suffix, 0, SpringLayout.EAST, suffixLabel);
        layout.putConstraint(SpringLayout.NORTH, suffix, 10, SpringLayout.SOUTH, title);
        add(suffix);

        JLabel motherLabel = new JLabel("Mother:");
        motherLabel.setPreferredSize(new Dimension(60, 30));
        layout.putConstraint(SpringLayout.WEST, motherLabel, 20, SpringLayout.EAST, suffix);
        layout.putConstraint(SpringLayout.NORTH, motherLabel, 5, SpringLayout.SOUTH, id);
        add(motherLabel);
        mother.setPreferredSize(new Dimension(300, 30));
        mother.addActionListener(e -> {
            new PersonSelectDialog() {
                @Override
                public void onClose(Person clicked) {
                    if(clicked.equals(PersonEditorPanel.this.person)
                            || clicked.equals(PersonEditorPanel.this.person.father)) {
                        JOptionPane.showMessageDialog(PersonEditorPanel.this, "Invalid selection", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        person.setMother(clicked);
                        save();
                        PersonEditorPanel.this.update();
                    }
                }
            }.setVisible(true);
        });
        layout.putConstraint(SpringLayout.WEST, mother, 0, SpringLayout.EAST, motherLabel);
        layout.putConstraint(SpringLayout.NORTH, mother, 5, SpringLayout.SOUTH, id);
        add(mother);

        JLabel fatherLabel = new JLabel("Father:");
        fatherLabel.setPreferredSize(new Dimension(60, 30));
        layout.putConstraint(SpringLayout.WEST, fatherLabel, 20, SpringLayout.EAST, suffix);
        layout.putConstraint(SpringLayout.NORTH, fatherLabel, 10, SpringLayout.SOUTH, motherLabel);
        add(fatherLabel);
        father.setPreferredSize(new Dimension(300, 30));
        father.addActionListener(e -> {
            new PersonSelectDialog() {
                @Override
                public void onClose(Person clicked) {
                    if(clicked.equals(PersonEditorPanel.this.person)
                            || clicked.equals(PersonEditorPanel.this.person.mother)) {
                        JOptionPane.showMessageDialog(PersonEditorPanel.this, "Invalid selection", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        person.setFather(clicked);
                        save();
                        PersonEditorPanel.this.update();
                    }
                }
            }.setVisible(true);
        });
        layout.putConstraint(SpringLayout.WEST, father, 0, SpringLayout.EAST, fatherLabel);
        layout.putConstraint(SpringLayout.NORTH, father, 10, SpringLayout.SOUTH, mother);
        add(father);

        update();
    }

    private void update() {
        id.setText("ID: " + person.id);
        givenName.setText(person.givenName);
        familyName.setText(person.familyName);
        title.setText(person.title);
        suffix.setText(person.suffix);
        mother.setText(person.mother == null ? "Click to set" : person.mother.toString());
        father.setText(person.father == null ? "Click to set" : person.father.toString());
    }

    public void save() {
        if(person != null) {
            person.givenName = givenName.getText();
            person.familyName = familyName.getText();
            person.title = title.getText();
            person.suffix = suffix.getText();
            // TODO missing fields
        }
    }
}
