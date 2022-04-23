package cs3410.project.familytree;

import cs3410.project.familytree.gui.ViewerFrame;

public class Main {
    public static FamilyTree loadedTree;

    public static void main(String[] args) {
        loadedTree = new FamilyTree();
        new ViewerFrame().setVisible(true);
    }
}
