package com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.dynamicbitvector;

public class DynamicBitVector {

    private final Node root;
    public DynamicBitVector(Node root) {
        this.root = root;
    }
    public Node root() { return root; }
    public int size() {
        int size = 0;
        for (Entry e : ((InternalNode) root).entries()) {
            size += e.b();
        }
        return size;
    }

    @Override
    public String toString() {
        StringBuilder strb = new StringBuilder();
        levelToString(strb, root, "");
        return strb.toString();
    }
    private void levelToString(StringBuilder strb, Node node, String prefix) {

        if (node instanceof LeafNode leaf) {
            strb.append(prefix)
                    .append("Leaf Node ")
                    .append(leaf.bits())
                    .append("\n");
            return;
        }

        InternalNode internal = (InternalNode) node;

        strb.append(prefix)
                .append("Internal Node")
                .append("\n");

        for (Entry e : internal.entries()) {

            strb.append(prefix)
                    .append("|---[")
                    .append(e.b())
                    .append(" / ")
                    .append(e.o())
                    .append("]")
                    .append("\n");

            levelToString(strb, e.p(), prefix + "|   ");
        }
    }
}
