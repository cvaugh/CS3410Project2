package cs3410.project.familytree.gui;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import cs3410.project.familytree.Main;

public class ViewerFrame extends JFrame {
    private final JPanel leftContainer = new JPanel();
    private final JScrollPane leftScrollPane = new JScrollPane(leftContainer);
    private final JPanel rightContainer = new JPanel();
    private final JScrollPane rightScrollPane = new JScrollPane(rightContainer);

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
                // TODO
            }
        });
        // TODO custom layout manager
        setLayout(new BoxLayout(getContentPane(), BoxLayout.X_AXIS));
        getContentPane().setPreferredSize(new Dimension(1000, 800));
        addComponents();
        pack();
        setLocationRelativeTo(null);
    }

    private void addComponents() {
        // FIXME swing hangs due to the following line
        leftContainer.setLayout(new BoxLayout(leftContainer, BoxLayout.Y_AXIS));
        PersonEditorPanel pep = new PersonEditorPanel();
        leftContainer.add(pep);
        leftContainer.add(new PersonEditorPanel());
        leftScrollPane.setViewportView(leftContainer);
        leftScrollPane.setPreferredSize(new Dimension(500, 800));
        leftScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        leftScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(leftScrollPane);

        rightScrollPane.add(rightContainer);
        rightScrollPane.setViewportView(rightContainer);
        rightScrollPane.setPreferredSize(new Dimension(500, 800));
        rightScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        rightScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(rightScrollPane);
    }
}
