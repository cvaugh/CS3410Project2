package cs3410.project.familytree.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.JPanel;

import cs3410.project.familytree.FamilyTree;
import cs3410.project.familytree.Main;
import cs3410.project.familytree.Person;

public class TreeGraph extends JPanel {
    private static final Color ACTIVE_COLOR = new Color(0xC8E6C9);
    private static final Color FAKE_COLOR = new Color(0x9E9E9E);
    private List<Node> nodes = new ArrayList<>();
    private int maxDepth = -1;
    Person active;

    public TreeGraph(ViewerFrame parent) {
        this.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                revalidate();
                repaint();
            }
        });
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for(Node node : nodes) {
                    if(node.contains(e.getPoint().x, e.getPoint().y) && node.person != null) {
                        parent.setActivePerson(node.person);
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
        if(Main.loadedTree == null) return;
        maxDepth = -1;
        createNodesRecursive(g, active, getHeight() - getHeight() / 5, 0, 0);
        Main.loadedTree.drawUnlockAll();
        drawNodes(g);
    }

    private void drawNodes(Graphics g) {
        if(nodes.size() == 0) return;
        for(Node n : nodes) {
            if(n.person != null) {
                if(n.person.mother != null) {
                    Node mother = getNode(n.person.mother);
                    n.parents[0] = mother;
                }
                if(n.person.father != null) {
                    Node father = getNode(n.person.father);
                    n.parents[1] = father;
                }
            }
        }
        boolean filled = false;
        while(!filled) {
            List<Node> toAdd = new ArrayList<Node>();
            for(Node n : nodes) {
                if(n.depth == maxDepth + 1) continue;
                if(n.parents[0] == null) {
                    toAdd.add(getNullParent(g, n, false, n.y));
                }
                if(n.parents[1] == null) {
                    toAdd.add(getNullParent(g, n, true, n.y));
                }
            }
            nodes.addAll(toAdd);
            filled = true;
            for(Node n : nodes) {
                if(n.depth == maxDepth + 1) continue;
                if(n.parents[0] == null || n.parents[1] == null) {
                    filled = false;
                    break;
                }
            }
        }
        @SuppressWarnings("unchecked")
        List<Node>[] generations = new List[maxDepth + 2];
        for(int i = 0; i < generations.length; i++) {
            generations[i] = new ArrayList<Node>();
        }
        int[] generationWidths = new int[generations.length];
        for(Node n : nodes) {
            generations[n.depth].add(n);
            generationWidths[n.depth] += n.width;
        }
        for(int i = 0; i < generations.length; i++) {
            generations[i].sort(new Comparator<Node>() {
                @Override
                public int compare(Node arg0, Node arg1) {
                    return Integer.compare(Math.min(arg0.lefts, arg0.rights), Math.min(arg1.lefts, arg1.rights));
                }
            });
            boolean allFake = true;
            int x = getWidth() / 2 - generationWidths[i] / 2;
            for(Node n : generations[i]) {
                if(!n.fake) allFake = false;
                n.x = x;
                n.y -= 20 * i * (i - 1);
                x += n.width + 10;
            }
            if(allFake) nodes.removeAll(generations[i]);
        }
        for(Node n : nodes) {
            n.drawLines(g);
        }
        for(Node n : nodes) {
            Point cursor = this.getMousePosition();
            n.hovered = cursor != null && n.contains(cursor.x, cursor.y);
            n.draw(g);
        }
    }

    private void createNodesRecursive(Graphics g, Person p, int y, int depth, int direction) {
        if(p == null || p.drawLock) return;
        if(depth > maxDepth) maxDepth = depth;
        p.drawLock();
        createNode(g, p, y, depth, direction);
        if(p.mother != null) {
            createNodesRecursive(g, p.mother, y - 50, depth + 1, direction - 1);
        }
        if(p.father != null) {
            createNodesRecursive(g, p.father, y - 50, depth + 1, direction + 1);
        }
    }

    private Node getNullParent(Graphics g, Node child, boolean right, int y) {
        Node parent = new Node(null, "Unknown", "", false, false, child.depth + 1, y - 50,
                g.getFontMetrics().stringWidth("Unknown") + 10, 20, child.depth + (right ? 1 : -1), true);
        parent.lefts = child.lefts + (right ? 0 : 1);
        parent.rights = child.rights + (right ? 1 : 0);
        child.parents[right ? 1 : 0] = parent;
        return parent;
    }

    private void createNode(Graphics g, Person p, int y, int depth, int direction) {
        String name = p.getName();
        String dates = String.format("%s-%s", p.birthDate == null ? "????" : FamilyTree.YEAR_FORMAT.format(p.birthDate),
                p.deathDate == null ? "" : FamilyTree.YEAR_FORMAT.format(p.deathDate));
        int w = Math.max(g.getFontMetrics().stringWidth(name), g.getFontMetrics().stringWidth(dates));
        nodes.add(new Node(p, name, dates, p == active, Main.loadedTree.orphans.contains(p), depth, y, w + 10, 40,
                direction, false));
    }

    private Node getNode(Person p) {
        for(Node node : nodes) {
            if(node.person == p) {
                return node;
            }
        }
        return null;
    }

    public void setActivePerson(Person person) {
        this.active = person;
    }

    private class Node {
        Person person;
        String name;
        String dates;
        boolean active;
        boolean hovered;
        boolean orphaned;
        int depth;
        int x;
        int y;
        int width;
        int height;
        Node[] parents = new Node[2];
        int lefts = 0;
        int rights = 0;
        boolean fake;

        Node(Person person, String name, String dates, boolean active, boolean orphaned, int depth, int y, int width,
                int height, int direction, boolean fake) {
            this.person = person;
            this.name = name;
            this.dates = dates;
            this.active = active;
            this.orphaned = orphaned;
            this.depth = depth;
            this.y = y;
            this.width = width;
            this.height = height;
            if(direction > 1) {
                rights += direction;
            } else if(direction < 1) {
                lefts -= direction;
            }
            this.fake = fake;
        }

        void draw(Graphics g) {
            if(active) {
                g.setColor(ACTIVE_COLOR);
                g.fillRect(x, y, width, height);
            }
            if(orphaned) {
                g.setColor(Color.RED);
            } else if(fake) {
                g.setColor(FAKE_COLOR);
            } else {
                g.setColor(Color.BLACK);
            }
            if(hovered) {
                g.fillRect(x, y, width, height);
                g.setColor(Color.WHITE);
            } else {
                g.drawRect(x, y, width, height);
            }
            g.drawString(name, x + 5, y + 15);
            g.drawString(dates, x + 5, y + 35);
            g.setColor(Color.BLACK);
        }

        void drawLines(Graphics g) {
            if(parents[0] != null && nodes.contains(parents[0])) {
                if(parents[0].fake) g.setColor(FAKE_COLOR);
                g.drawLine(x + width / 2, y, parents[0].x + parents[0].width / 2, parents[0].y + parents[0].height);
            }
            g.setColor(Color.BLACK);
            if(parents[1] != null && nodes.contains(parents[1])) {
                if(parents[1].fake) g.setColor(FAKE_COLOR);
                g.drawLine(x + width / 2, y, parents[1].x + parents[1].width / 2, parents[1].y + parents[1].height);
            }
            g.setColor(Color.BLACK);
        }

        boolean contains(int x, int y) {
            return x >= this.x && y >= this.y && x < (this.x + width) && y < (this.y + height);
        }
    }
}
