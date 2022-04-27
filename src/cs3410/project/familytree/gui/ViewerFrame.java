package cs3410.project.familytree.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import cs3410.project.familytree.Main;
import cs3410.project.familytree.Person;

public class ViewerFrame extends JFrame {
    private final JPanel topContainer = new JPanel();
    private final JScrollPane topScrollPane = new JScrollPane(topContainer);
    private final TreeGraph graph = new TreeGraph();
    private final JScrollPane bottomScrollPane = new JScrollPane(graph);

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
    }

    private void addMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        JMenuItem menuItem = new JMenuItem("New Tree");
        menuItem.setMnemonic(KeyEvent.VK_N);
        menuItem.addActionListener(e -> {
            // TODO
        });
        fileMenu.add(menuItem);
        menuItem = new JMenuItem("Open Tree");
        menuItem.setMnemonic(KeyEvent.VK_O);
        menuItem.addActionListener(e -> {
            // TODO
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
        JMenu treeMenu = new JMenu("Tree");
        menuItem = new JMenuItem("New Person");
        menuItem.setMnemonic(KeyEvent.VK_P);
        menuItem.addActionListener(e -> {
            Person p = new Person();
            if(Main.loadedTree.root == null) {
                Main.loadedTree.root = p;
                Main.loadedTree.orphans.remove(p);
            }
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
        menuItem = new JMenuItem("Clear Orphans");
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
        JMenu toolsMenu = new JMenu("Tools");
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
            int size = Main.loadedTree.getSize();
            JOptionPane.showMessageDialog(ViewerFrame.this,
                    String.format("The loaded family tree contains %d %s, of which %d %s orphaned.", size,
                            size == 1 ? "person" : "people", Main.loadedTree.orphans.size(),
                            Main.loadedTree.orphans.size() == 1 ? "is" : "are"),
                    getTitle(), JOptionPane.INFORMATION_MESSAGE);
        });
        toolsMenu.add(menuItem);
        menuBar.add(toolsMenu);
        setJMenuBar(menuBar);
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

    private void setActivePerson(Person p) {
        for(Component c : topContainer.getComponents()) {
            if(c instanceof PersonEditorPanel) {
                ((PersonEditorPanel) c).save();
            }
        }
        topContainer.removeAll();
        topContainer.add(new PersonEditorPanel(p));
        topContainer.revalidate();
        topContainer.repaint();
        graph.repaint();
    }
}
