package com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.dynamicbitvector;

import com.github.jcodevandamme.semantics.rdf.model.Tuple;
import com.github.jcodevandamme.semantics.rdf.structure.bitstring.BitInterface;
import com.github.jcodevandamme.semantics.rdf.structure.bitstring.RoaringBitString;
import com.github.jcodevandamme.semantics.rdf.structure.tree.dk2.DK2Configuration;

import java.util.ArrayList;
import java.util.List;

public class DynamicBitVector {

    private Node root;
    private final DK2Configuration config;
    public DynamicBitVector(Node root, DK2Configuration config) {
        this.root = root;
        this.config = config;
    }
    public Node root() { return root; }
    public int size() {
        int size = 0;
        for (Entry e : ((InternalNode) root).entries()) {
            size += e.b();
        }
        return size;
    }

    public static void set(boolean value, LeafNode leaf, int index) {
        boolean oneSet = false;
        boolean oneUnset = false;

        if (leaf.bits().access(index) == 1) {
            if (!value) {
                System.out.println("Setting 1 to 0");
                oneUnset = true;
            } else {
                System.out.println("Bit was 1, no update necessary");
                return;
            }
        } else if (value) {
            System.out.println("Setting 0 to 1");
            oneSet = true;
        } else {
            System.out.println("Bit was 0, no update necessary");
            return;
        }

        leaf.bits().setBit(value, index);

        updateOCounters(leaf, oneSet, oneUnset);
    }

    private static void updateOCounters(LeafNode leaf, boolean oneSet, boolean oneUnset) {
        Node current = leaf;
        while (current.parent() != null) {
            InternalNode parent = current.parent();

            if (oneSet) {
                parent.entries().get(current.indexInParent()).updateO(+1);

            } else if (oneUnset) {
                parent.entries().get(current.indexInParent()).updateO(-1);
            }

            current = parent;
        }
    }

    public void addK2Bits(LeafNode leaf,int k, int index) {
        // Append k^2 Bits by shifting the indices
        // -> Updates BitString Size internally
        leaf.bits().addBits(index, k*k);

        increaseBCounters(leaf, k);

        // If Leaf Capacity was reached, split in two
        if (leaf.size() > leaf.maxCapacity()) {

            Tuple<LeafNode> leafs = splitLeafNode(leaf, k);

            InternalNode parent;

            if (leaf.parent() == null) {
                parent = new InternalNode(
                        config.internalMinimumCapacity(),
                        config.internalMaximumCapacity()
                );
                parent.add(leafs.t1(), 0);
                parent.add(leafs.t2(), 1);
                root = parent;
                return;
            }

            parent = leaf.parent();
            parent.remove(leaf);

            // Append new ones
            parent.add(leafs.t1(), leaf.indexInParent());
            leafs.t1().setParent(parent, leaf.indexInParent());

            parent.add(leafs.t2(), leaf.indexInParent() + 1);
            leafs.t2().setParent(parent, leaf.indexInParent() + 1);

            expandInternal(parent);
        }
    }

    private void increaseBCounters(LeafNode leaf, int k) {
        assert k >= 0;

        Node current = leaf;
        while (current.parent() != null) {
            InternalNode parent = current.parent();

            parent.entries().get(current.indexInParent()).updateB(k*k);

            current = parent;
        }
    }
    private void decreaseBCounters(LeafNode leaf, int delta) {
        assert delta <= 0;

        Node current = leaf;
        while (current.parent() != null) {
            InternalNode parent = current.parent();

            parent.entries().get(current.indexInParent()).updateB(delta);

            current = parent;
        }
    }

    private Tuple<LeafNode> splitLeafNode(LeafNode node, int k) {
        Tuple<BitInterface> splitBits = splitBits(node.bits(), k);

        LeafNode leftLeaf = new LeafNode(
                config.leafMinimumCapacity(),
                config.chunkSize(),
                splitBits.t1()
        );
        LeafNode rightLeaf = new LeafNode(
                config.leafMinimumCapacity(),
                config.chunkSize(),
                splitBits.t2()
        );
        return new Tuple<LeafNode>(
                leftLeaf,
                rightLeaf
        );
    }
    public Tuple<BitInterface> splitBits(BitInterface bitString, int k) {
        int half = bitString.size() / 2;
        int overshoot = half % (k*k);
        int splitIdx = (k*k) - overshoot + half;

        List<Boolean> leftBits = new ArrayList<>();
        List<Boolean> rightBits = new ArrayList<>();

        for (int i = 0; i < bitString.size(); i++) {
            if (i < splitIdx) {
                leftBits.add(bitString.access(i) == 1);
            } else {
                rightBits.add(bitString.access(i) == 1);
            }
        }
        return new Tuple<BitInterface>(
                new RoaringBitString(leftBits),
                new RoaringBitString(rightBits)
        );
    }
    private void expandInternal(InternalNode node) {
        if (node.size() > node.maxCapacity()) {
            Tuple<InternalNode> nodes = splitInternalNode(node);

            InternalNode parent;
            if (node.parent() == null) {
                parent = new InternalNode(
                        config.internalMinimumCapacity(),
                        config.internalMaximumCapacity()
                );
                parent.add(nodes.t1(), 0);
                nodes.t1().setParent(parent, 0);

                parent.add(nodes.t2(), 1);
                nodes.t2().setParent(parent, 1);

                root = parent;

            } else {
                parent = node.parent();
                parent.entries().remove(node.indexInParent());

                parent.add(nodes.t1(), node.indexInParent());
                nodes.t1().setParent(parent, node.indexInParent());

                parent.add(nodes.t2(), node.indexInParent() + 1);
                nodes.t2().setParent(parent, node.indexInParent() + 1);

                expandInternal(node.parent());
            }
        }
    }
    private Tuple<InternalNode> splitInternalNode(InternalNode node) {
        Tuple<List<Entry>> splitEntries = splitEntries(node.entries());

        InternalNode leftNode = new InternalNode(
                config.internalMinimumCapacity(),
                config.internalMaximumCapacity()
        );
        leftNode.entries().addAll(splitEntries.t1());

        InternalNode rightNode = new InternalNode(
                config.internalMinimumCapacity(),
                config.internalMaximumCapacity()
        );
        rightNode.entries().addAll(splitEntries.t2());

        return new Tuple<InternalNode>(
                leftNode,
                rightNode
        );
    }
    public Tuple<List<Entry>> splitEntries(List<Entry> entries) {

        int splitIdx = entries.size() / 2;
        splitIdx -= entries.size() % 2;

        List<Entry> leftEntries = new ArrayList<>();
        List<Entry> rightEntries = new ArrayList<>();

        for (int i = 0; i < entries.size(); i++) {
            if (i < splitIdx) {
                leftEntries.add(entries.get(i));
            } else {
                rightEntries.add(entries.get(i));
            }
        }
        return new Tuple<List<Entry>>(
                leftEntries,
                rightEntries
        );
    }
    public void removeK2Bits(LeafNode leaf,int k, int index) {
        // Append k^2 Bits by shifting the indices
        // -> Updates BitString Size internally
        int removedBits = leaf.bits().removeBits(index, k*k);

        decreaseBCounters(leaf, k);

        // If Leaf Capacity was reached, split in two
        if (leaf.size() > leaf.maxCapacity()) {

            Tuple<LeafNode> leafs = splitLeafNode(leaf, k);

            InternalNode parent;

            if (leaf.parent() == null) {
                parent = new InternalNode(
                        config.internalMinimumCapacity(),
                        config.internalMaximumCapacity()
                );
                parent.add(leafs.t1(), 0);
                parent.add(leafs.t2(), 1);
                root = parent;
                return;
            }

            parent = leaf.parent();
            parent.remove(leaf);

            // Append new ones
            parent.add(leafs.t1(), leaf.indexInParent());
            leafs.t1().setParent(parent, leaf.indexInParent());

            parent.add(leafs.t2(), leaf.indexInParent() + 1);
            leafs.t2().setParent(parent, leaf.indexInParent() + 1);

            expandInternal(parent);
        }
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
