package com.example.k2tree;

public class K2TreePrinter {

    public void print(K2Tree tree) {

        printNode(tree.getRoot(), 0);
    }

    private void printNode(K2Node node, int level) {

        if (node == null) {
            return;
        }

        for (int i = 0; i < level; i++) {
            System.out.print("  ");
        }

        System.out.println(node.hasValue());

        for (K2Node child : node.getChildren()) {

            printNode(child, level + 1);
        }
    }
}