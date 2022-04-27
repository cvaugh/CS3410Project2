package cs3410.project.familytree.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

import cs3410.project.familytree.FamilyTree;
import cs3410.project.familytree.Main;
import cs3410.project.familytree.Person;

public class TreeGraph extends JPanel {
    private static final Color ACTIVE_COLOR = new Color(0xC8E6C9);
    private final ViewerFrame parent;
    private Map<Person, Node> nodes = new HashMap<>();
    private Person active;

    public TreeGraph(ViewerFrame parent) {
        this.parent = parent;
        this.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                revalidate();
                repaint();
            }
        });
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for(Person person : nodes.keySet()) {
                    if(nodes.get(person).contains(e.getPoint().x, e.getPoint().y)) {
                        parent.setActivePerson(person);
                    }
                }
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        nodes.clear();
        g.clearRect(0, 0, getWidth(), getHeight());
        createNodesRecursive(g, Main.loadedTree.root, getWidth() / 2, getHeight() / 2);
        Main.loadedTree.drawUnlockAll();
        drawNodes(g);
    }

    private void drawNodes(Graphics g) {
        for(Person p : nodes.keySet()) {
            nodes.get(p).draw(g);
        }
    }

    private void createNodesRecursive(Graphics g, Person p, int x, int y) {
        if(p == null || p.drawLock) return;
        p.drawLock();
        int w = createNode(g, p, x, y);
        if(p.mother != null) {
            createNodesRecursive(g, p.mother, x - w, y - 50);
            g.drawLine(x, y, x - w, y - 10);
        }
        if(p.father != null) {
            createNodesRecursive(g, p.father, x + w, y - 50);
            g.drawLine(x, y, x + w, y - 10);
        }
    }

    private int createNode(Graphics g, Person p, int x, int y) {
        String name = p.getName();
        String dates = String.format("%s-%s", p.birthDate == null ? "????" : FamilyTree.YEAR_FORMAT.format(p.birthDate),
                p.deathDate == null ? "" : FamilyTree.YEAR_FORMAT.format(p.deathDate));
        int w = Math.max(g.getFontMetrics().stringWidth(name), g.getFontMetrics().stringWidth(dates));
        Point cursor = this.getMousePosition();
        boolean hovered = cursor != null && withinBounds(cursor.x, cursor.y, x + 20 - (w / 2), y, w + 10, 40);
        Node n = new Node(name, dates, p == active, hovered, x, y, w, 40);
        Node mother = nodes.get(p.mother);
        if(mother != null) {
            n.connected.add(mother);
            mother.connected.add(n);
        }
        Node father = nodes.get(p.father);
        if(father != null) {
            n.connected.add(father);
            father.connected.add(n);
        }
        for(Person c : p.children) {
            Node child = nodes.get(c);
            if(child != null) {
                n.connected.add(child);
                child.connected.add(n);
            }
        }
        nodes.put(p, n);
        return w;
    }

    public void setActivePerson(Person person) {
        this.active = person;
    }

    private static boolean withinBounds(int x, int y, int bx, int by, int bw, int bh) {
        return x >= bx && y >= by && x < (bx + bw) && y < (by + bh);
    }

    // TODO store bounds of each node for layout
    private class Node {
        String name;
        String dates;
        boolean active;
        boolean hovered;
        int x;
        int y;
        int width;
        int height;
        Set<Node> connected = new HashSet<>();

        Node(String name, String dates, boolean active, boolean hovered, int x, int y, int width, int height) {
            this.name = name;
            this.dates = dates;
            this.active = active;
            this.hovered = hovered;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        void draw(Graphics g) {
            if(active) {
                g.setColor(ACTIVE_COLOR);
                g.fillRect(x + 20 - (width / 2), y, width + 10, height);
                g.setColor(Color.BLACK);
            }
            if(hovered) {
                g.fillRect(x + 20 - (width / 2), y, width + 10, height);
                g.setColor(Color.WHITE);
            } else {
                g.drawRect(x + 20 - (width / 2), y, width + 10, height);
            }
            g.drawString(name, x + 25 - (width / 2), y + 15);
            g.drawString(dates, x + 25 - (width / 2), y + 35);
            g.setColor(Color.BLACK);
        }

        boolean contains(int x, int y) {
            return x >= this.x && y >= this.y && x < (this.x + width) && y < (this.y + height);
        }
    }
}
