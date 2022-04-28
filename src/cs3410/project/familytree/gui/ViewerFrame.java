package cs3410.project.familytree.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import cs3410.project.familytree.FamilyTree;
import cs3410.project.familytree.Main;
import cs3410.project.familytree.Person;

public class ViewerFrame extends JFrame {
    private static final JFileChooser FILE_CHOOSER;
    static {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) {
            e.printStackTrace();
        }
        FILE_CHOOSER = new JFileChooser();
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch(Exception e) {
            e.printStackTrace();
        }
        FILE_CHOOSER.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().endsWith(".tree");
            }

            @Override
            public String getDescription() {
                return "Family Tree (.tree)";
            }
        });
        FILE_CHOOSER.setFileSelectionMode(JFileChooser.FILES_ONLY);
        FILE_CHOOSER.setCurrentDirectory(new File("."));
    }
    private final JPanel topContainer = new JPanel();
    private final JScrollPane topScrollPane = new JScrollPane(topContainer);
    protected final TreeGraph graph = new TreeGraph(this);
    private final JScrollPane bottomScrollPane = new JScrollPane(graph);
    private final JMenu treeMenu = new JMenu("Tree");
    private final JMenu toolsMenu = new JMenu("Tools");

    public ViewerFrame() {
        setTitle("Family Tree");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(Main.loadedTree != null) {
                    try {
                        Main.loadedTree.write();
                    } catch(IOException ex) {
                        ex.printStackTrace();
                    }
                }
                ViewerFrame.this.setVisible(false);
                ViewerFrame.this.dispose();
                System.exit(0);
            }
        });
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                topContainer.revalidate();
                graph.revalidate();
                graph.repaint();
            }
        });
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().setPreferredSize(new Dimension(1000, 800));
        addMenu();
        addComponents();
        pack();
        setLocationRelativeTo(null);
        updateMenuBar();
        if(Main.loadedTree != null && Main.loadedTree.root != null) {
            setActivePerson(Main.loadedTree.root);
        }
    }

    private void addMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        JMenuItem menuItem = new JMenuItem("New Tree");
        menuItem.setMnemonic(KeyEvent.VK_N);
        menuItem.addActionListener(e -> {
            int r = FILE_CHOOSER.showSaveDialog(ViewerFrame.this);
            if(r == JFileChooser.APPROVE_OPTION) {
                if(FILE_CHOOSER.getSelectedFile().exists()) {
                    JOptionPane.showMessageDialog(ViewerFrame.this, "The selected file already exists.",
                            "Error Creating Tree", JOptionPane.ERROR_MESSAGE);
                } else {
                    Main.loadedTree = new FamilyTree(FILE_CHOOSER.getSelectedFile());
                    clearActivePerson();
                    updateMenuBar();
                }
            }
        });
        fileMenu.add(menuItem);
        menuItem = new JMenuItem("Open Tree");
        menuItem.setMnemonic(KeyEvent.VK_O);
        menuItem.addActionListener(e -> {
            int r = FILE_CHOOSER.showSaveDialog(ViewerFrame.this);
            if(r == JFileChooser.APPROVE_OPTION) {
                try {
                    FamilyTree.open(FILE_CHOOSER.getSelectedFile());
                    if(Main.loadedTree.root != null) {
                        setActivePerson(Main.loadedTree.root);
                    } else {
                        clearActivePerson();
                    }
                    updateMenuBar();
                } catch(IOException e1) {
                    JOptionPane.showMessageDialog(ViewerFrame.this, "An exception occurred while loading the file.",
                            "Error Loading Tree", JOptionPane.ERROR_MESSAGE);
                    e1.printStackTrace();
                }
            }
        });
        fileMenu.add(menuItem);
        fileMenu.addSeparator();
        menuItem = new JMenuItem("Exit");
        menuItem.setMnemonic(KeyEvent.VK_X);
        menuItem.addActionListener(e -> {
            ViewerFrame.this.dispatchEvent(new WindowEvent(ViewerFrame.this, WindowEvent.WINDOW_CLOSING));
        });
        fileMenu.add(menuItem);
        menuBar.add(fileMenu);
        treeMenu.setEnabled(false);
        menuItem = new JMenuItem("New Person");
        menuItem.setMnemonic(KeyEvent.VK_P);
        menuItem.addActionListener(e -> {
            Person p = new Person();
            setActivePerson(p);
        });
        treeMenu.add(menuItem);
        menuItem = new JMenuItem("Select Person");
        menuItem.setMnemonic(KeyEvent.VK_S);
        menuItem.addActionListener(e -> {
            new PersonSelectDialog() {
                @Override
                public void onClose(Person clicked) {
                    setActivePerson(clicked);
                }
            }.setVisible(true);
        });
        treeMenu.add(menuItem);
        menuItem = new JMenuItem("Clear Disconnected");
        menuItem.setMnemonic(KeyEvent.VK_C);
        menuItem.addActionListener(e -> {
            boolean clear = false;
            for(Component c : topContainer.getComponents()) {
                if(c instanceof PersonEditorPanel) {
                    clear = Main.loadedTree.orphans.contains(((PersonEditorPanel) c).person);
                    break;
                }
            }
            if(clear) {
                topContainer.removeAll();
                topContainer.revalidate();
                topContainer.repaint();
            }
            Main.loadedTree.orphans.clear();
        });
        treeMenu.add(menuItem);
        menuBar.add(treeMenu);
        toolsMenu.setEnabled(false);
        menuItem = new JMenuItem("Tree Root");
        menuItem.setMnemonic(KeyEvent.VK_R);
        menuItem.addActionListener(e -> {
            JOptionPane
                    .showMessageDialog(ViewerFrame.this,
                            Main.loadedTree.root == null ? "The currently loaded family tree does not have a root."
                                    : String.format("The root of the currently loaded family tree is \"%s\".",
                                            Main.loadedTree.root.toString()),
                            getTitle(), JOptionPane.INFORMATION_MESSAGE);
        });
        toolsMenu.add(menuItem);
        menuItem = new JMenuItem("Tree Size");
        menuItem.setMnemonic(KeyEvent.VK_S);
        menuItem.addActionListener(e -> {
            int size = Main.loadedTree.getSize(true);
            JOptionPane.showMessageDialog(ViewerFrame.this,
                    String.format(
                            "The loaded family tree contains %d %s, of which %d %s are disconnected from the root.",
                            size, size == 1 ? "person" : "people", Main.loadedTree.orphans.size(),
                            Main.loadedTree.orphans.size() == 1 ? "is" : "are"),
                    getTitle(), JOptionPane.INFORMATION_MESSAGE);
        });
        toolsMenu.add(menuItem);
        menuItem = new JMenuItem("List Ancestors");
        menuItem.setMnemonic(KeyEvent.VK_A);
        menuItem.addActionListener(e -> {
            if(graph.active == null) {
                JOptionPane.showMessageDialog(ViewerFrame.this, "No person is selected.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            List<Person> results = new ArrayList<>();
            Main.loadedTree.traverse(person -> {
                if(person != graph.active) {
                    results.add(person);
                }
            }, graph.active, true, false);
            displayList(results, String.format("%s has %d ancestors:", graph.active, results.size()), "Ancestors");
        });
        toolsMenu.add(menuItem);
        menuItem = new JMenuItem("List Descendants");
        menuItem.setMnemonic(KeyEvent.VK_A);
        menuItem.addActionListener(e -> {
            if(graph.active == null) {
                JOptionPane.showMessageDialog(ViewerFrame.this, "No person is selected.", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            List<Person> results = new ArrayList<>();
            Main.loadedTree.traverse(person -> {
                if(person != graph.active) {
                    results.add(person);
                }
            }, graph.active, false, true);
            displayList(results, String.format("%s has %d descendants:", graph.active, results.size()), "Descendants");
        });
        toolsMenu.add(menuItem);
        menuItem = new JMenuItem("List Leaf Nodes");
        menuItem.setMnemonic(KeyEvent.VK_A);
        menuItem.addActionListener(e -> {
            List<Person> results = new ArrayList<>();
            Main.loadedTree.traverse(person -> {
                if(person.mother == null && person.father == null) {
                    results.add(person);
                }
            });
            displayList(results, String.format("The tree contains %d leaf nodes:", results.size()), "Leaf Nodes");
        });
        toolsMenu.add(menuItem);
        menuItem = new JMenuItem("List Internal Nodes");
        menuItem.setMnemonic(KeyEvent.VK_A);
        menuItem.addActionListener(e -> {
            List<Person> results = new ArrayList<>();
            Main.loadedTree.traverse(person -> {
                if(person.mother != null || person.father != null) {
                    results.add(person);
                }
            });
            displayList(results, String.format("The tree contains %d internal nodes:", results.size()),
                    "Internal Nodes");
        });
        toolsMenu.add(menuItem);
        menuItem = new JMenuItem("List Disconnected Nodes");
        menuItem.setMnemonic(KeyEvent.VK_A);
        menuItem.addActionListener(e -> {
            List<Person> results = new ArrayList<>();
            results.addAll(Main.loadedTree.orphans);
            displayList(results, String.format("The tree contains %d disconnected nodes:", results.size()),
                    "Disconnected Nodes");
        });
        toolsMenu.add(menuItem);
        menuBar.add(toolsMenu);
        setJMenuBar(menuBar);
    }

    private void displayList(List<Person> list, String message, String title) {
        StringBuilder sb = new StringBuilder();
        for(Person p : list) {
            sb.append(p.toString());
            sb.append("\n");
        }
        JOptionPane.showMessageDialog(this, String.format("%s\n\n%s", message, sb.toString()), title,
                JOptionPane.PLAIN_MESSAGE);
    }

    private void updateMenuBar() {
        treeMenu.setEnabled(Main.loadedTree != null);
        toolsMenu.setEnabled(Main.loadedTree != null);
    }

    private void addComponents() {
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topScrollPane.setViewportView(topContainer);
        topScrollPane.setPreferredSize(new Dimension(1000, 300));
        topScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        topScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(topScrollPane);

        bottomScrollPane.add(graph);
        bottomScrollPane.setViewportView(graph);
        bottomScrollPane.setPreferredSize(new Dimension(1000, 500));
        bottomScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        bottomScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(bottomScrollPane);
    }

    protected void clearActivePerson() {
        topContainer.removeAll();
        topContainer.revalidate();
        topContainer.repaint();
        graph.setActivePerson(null);
        graph.repaint();
    }

    protected void setActivePerson(Person p) {
        if(Main.loadedTree.root == null) {
            Main.loadedTree.root = p;
            Main.loadedTree.orphans.remove(p);
        }
        for(Component c : topContainer.getComponents()) {
            if(c instanceof PersonEditorPanel) {
                ((PersonEditorPanel) c).save();
            }
        }
        topContainer.removeAll();
        topContainer.add(new PersonEditorPanel(p, this));
        topContainer.revalidate();
        topContainer.repaint();
        graph.setActivePerson(p);
        graph.repaint();
    }
}
