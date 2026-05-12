package com.example.k2tree;


public class K2Node {

    private final boolean hasValue;

    private final K2Node[] children;

    public K2Node(boolean hasValue) {

        this.hasValue = hasValue;

        this.children = new K2Node[4];
    }

    public boolean hasValue() {
        return hasValue;
    }

    public K2Node[] getChildren() {
        return children;
    }

    public void setChild(int index, K2Node child) {
        children[index] = child;
    }
}