package cs3410.project.familytree.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;

import cs3410.project.familytree.FamilyTree;
import cs3410.project.familytree.Main;
import cs3410.project.familytree.Person;

public class TreeGraph extends JPanel {

    public TreeGraph() {
        this.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                revalidate();
                repaint();
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.clearRect(0, 0, getWidth(), getHeight());
        drawRecursive(g, Main.loadedTree.root, getWidth() / 2, getHeight() / 2);
        Main.loadedTree.drawUnlockAll();
    }

    private void drawRecursive(Graphics g, Person p, int x, int y) {
        if(p == null || p.drawLock) return;
        p.drawLock();
        int w = drawPerson(g, p, x, y);
        if(p.mother != null) {
            drawRecursive(g, p.mother, x - w, y - 50);
            g.drawLine(x, y, x - w, y - 10);
        }
        if(p.father != null) {
            drawRecursive(g, p.father, x + w, y - 50);
            g.drawLine(x, y, x + w, y - 10);
        }
    }

    private int drawPerson(Graphics g, Person p, int x, int y) {
        String name = p.getName();
        String dates = String.format("%s-%s", p.birthDate == null ? "????" : FamilyTree.YEAR_FORMAT.format(p.birthDate),
                p.deathDate == null ? "" : FamilyTree.YEAR_FORMAT.format(p.deathDate));
        int w = Math.max(g.getFontMetrics().stringWidth(name), g.getFontMetrics().stringWidth(dates));
        Point cursor = this.getMousePosition();
        boolean hovered = cursor != null && withinBounds(cursor.x, cursor.y, x + 20 - (w / 2), y, w + 10, 40);
        if(hovered) {
            g.fillRect(x + 20 - (w / 2), y, w + 10, 40);
            g.setColor(Color.WHITE);
        } else {
            g.drawRect(x + 20 - (w / 2), y, w + 10, 40);
        }
        g.drawString(name, x + 25 - (w / 2), y + 15);
        g.drawString(dates, x + 25 - (w / 2), y + 35);
        g.setColor(Color.BLACK);
        return w;
    }

    private static boolean withinBounds(int x, int y, int bx, int by, int bw, int bh) {
        return x >= bx && y >= by && x < (bx + bw) && y < (by + bh);
    }
    
    private class Node {
        // TODO store bounds of each node for layout
    }
}
